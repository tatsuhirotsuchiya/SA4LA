package sa4la;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class State implements Cloneable {
	int[][] array;// 行列
	private final int row;// 行数
	private final int column;// 列数
	private final int dnum;// detectできる数
	private final IntBuffer zerokey = IntBuffer.wrap(new int[0]);// locationkey用の空の場合のキー
	public int missinteraction;// coverしていないinteractionの数
	public int misslocation;// coverしているが、locateできないinteractionの数
	public int[] mtestcase = null;// 変異後のテストケース
	public int[] pretestcase = null;// 変異前のテストケース
	public int mrow;// 変異したテストケース番号
	// interactionをまとめたリスト
	public List<Interaction> interactions;
	// interaction探索用のマップ
	public Map<IntBuffer, Interaction> interaction_map;
	// interactionset探索用のマップ
	public Map<List<IntBuffer>, InteractionSet> interactionset_map = new HashMap<>();
	// locate探索用のマップ
	public Map<IntBuffer, Map<List<IntBuffer>, InteractionSet>> location_map = new HashMap<>();
	// coverできていないinteractionのマップ
	public Map<IntBuffer, Interaction> nocoveredmap = new HashMap<>();
	// locateできていないinteractionsetのマップ
	public Map<List<IntBuffer>, InteractionSet> nolocatedmap = new HashMap<>();
	// 変異の影響を受けたinteractionのキーを保存するリスト
	public List<IntBuffer> mikeys = new ArrayList<>();
	
	private final Option option = new Option();
	
	State(int[][] array, List<Interaction> interactions, int detectednum, int strength) {
		this.array = array;
		row = array.length;
		column = array[0].length;
		this.dnum = detectednum;
		this.interactions = interactions;

		// interaction_mapの設定
		interaction_map = new HashMap<>(interactions.size());
		for (Interaction i : interactions)
			interaction_map.put(i.ikey, i);

		// fitnessの初期計算
		missinteraction = 0;
		// 各interactionがcoverしているかの確認
		int[] values = new int[strength];
		for (Interaction i : interaction_map.values()) {
			i.initialtestcases(row);// interaction内のtestcasesの初期化
			for (int r = 0; r < row; r++) {
				for (int t = 0; t < strength; t++)
					values[t] = array[r][i.columns[t]];
				if (Arrays.equals(i.values, values))
					i.testcases.add(r);
			}
			i.num = i.testcases.size();

			// missinteractionの更新
			if (i.num == 0) {
				missinteraction += 1;
				nocoveredmap.put(i.ikey, i);
			}
		}

		// interactionset_map, location_mapの設定
		for (int dn = 1; dn <= dnum; dn++) {
			boolean end_flag = false;
			int max = interactions.size();
			int[] index = new int[dn];

			// 1つめの設定
			for (int d = 0; d < dn; d++)
				index[d] = d;

			// 2つめ以降の設定
			do {
				List<Interaction> set = new ArrayList<>();
				for (int d = 0; d < dn; d++)
					set.add(interactions.get(index[d]));

				InteractionSet iset = new InteractionSet(set);
				iset.genLocationKey();
				interactionset_map.put(iset.isetkey, iset);

				if (!location_map.containsKey(iset.lkey)) {
					Map<List<IntBuffer>, InteractionSet> map = new HashMap<>();
					map.put(iset.isetkey, iset);
					location_map.put(iset.lkey, map);
				} else
					location_map.get(iset.lkey).put(iset.isetkey, iset);

				for (int i = dn - 1; i >= 0; i--) {
					if (index[i] < max + i - dn) {
						index[i] += 1;
						for (int j = i + 1; j < dn; j++) {
							index[j] = index[j - 1] + 1;
						}
						break;
					}
					// すべてのパラメータを調べ終わった場合
					else if (i == 0)
						end_flag = true;
				}
			} while (!end_flag);
		}

		// nolocatedmapの確認
		for (Map.Entry<IntBuffer, Map<List<IntBuffer>, InteractionSet>> e : location_map.entrySet()) {
			if (e.getValue().size() > 1 && !e.getKey().equals(zerokey)) {
				for (InteractionSet iset : e.getValue().values())
					nolocatedmap.put(iset.isetkey, iset);
			}
		}

		// misslocationの更新
		misslocation = nolocatedmap.size();
	}

	// fitnessの取得
	public double fitness() {
		return option.getweight() * missinteraction + misslocation;
	}

	// missinteractionの更新(複数スレッドでmissinteractionの更新があるため使用)
	synchronized public void updateMissinteraction(int dif) {
		this.missinteraction += dif;
	}

	// mikeysの更新(複数スレッドで更新があるため使用)
	synchronized public void updatemInteractionkeys(List<IntBuffer> ikeys) {
		this.mikeys.addAll(ikeys);
	}

	// アレイを成形して表示
	public void printarray() {
		// 行をバブルソートで上から小さい順に並び替え
		int[] order = new int[row];
		for (int i = 0; i < row; i++)
			order[i] = i;

		for (int i = 0; i < order.length; i++) {
			for (int j = order.length - 1; j > i; j--) {
				for (int c = 0; c < column; c++) {
					if (array[order[j]][c] > array[order[j - 1]][c])
						break;
					else if (array[order[j]][c] < array[order[j - 1]][c]) {
						int temp = order[j];
						order[j] = order[j - 1];
						order[j - 1] = temp;
						break;
					}
				}
			}
		}

		// アレイを表示
		for (int i = 0; i < order.length; i++) {
			for (int c = 0; c < column; c++) {
				System.out.print(array[order[i]][c]);
				System.out.print(" ");
			}
			System.out.println();
		}
		System.out.println();
	}

	// stateの状態を表示
	public void printstate() {
		// 行列表示
		System.out.println("Array = ");
		for (int r = 0; r < row; r++) {
			for (int c = 0; c < column; c++) {
				System.out.print(array[r][c]);
			}
			System.out.println();
		}
		System.out.println("missinteraction = " + missinteraction);
		System.out.println("misslocation = " + misslocation);
		System.out.println("mrow = " + mrow);
		System.out.println("mtestcase = " + Arrays.toString(mtestcase));
		System.out.println("pretestcase = " + Arrays.toString(pretestcase));
	}

	@Override
	public State clone() {
		State b = null;

		try {
			b = (State) super.clone();
			int[][] ba = new int[row][column];
			for (int i = 0; i < row; i++)
				ba[i] = this.array[i].clone();
			b.array = ba;
			if (this.mtestcase != null)
				b.mtestcase = this.mtestcase.clone();
			if (this.pretestcase != null)
				b.pretestcase = this.pretestcase.clone();

			List<Interaction> is = new ArrayList<>(this.interactions.size());
			Map<IntBuffer, Interaction> im = new HashMap<>(this.interaction_map.size());
			Map<List<IntBuffer>, InteractionSet> ism = new HashMap<>(this.interactionset_map.size());
			Map<IntBuffer, Map<List<IntBuffer>, InteractionSet>> lm = new HashMap<>();
			Map<IntBuffer, Interaction> ncm = new HashMap<>();

			for (Interaction i0 : this.interactions) {
				Interaction i = i0.clone();
				is.add(i);
				im.put(i.ikey, i);
				if (i.num == 0)
					ncm.put(i.ikey, i);
			}

			// interactionsetの生成
			for (int dn = 1; dn <= dnum; dn++) {
				boolean end_flag = false;
				int max = is.size();
				int[] index = new int[dn];
				// 1つめの設定
				for (int d = 0; d < dn; d++)
					index[d] = d;

				// 2つめ以降の設定
				do {
					List<Interaction> set = new ArrayList<>();
					for (int d = 0; d < dn; d++)
						set.add(is.get(index[d]));

					InteractionSet iset = new InteractionSet(set);
					iset.genLocationKey();
					ism.put(iset.isetkey, iset);

					if (!lm.containsKey(iset.lkey)) {
						Map<List<IntBuffer>, InteractionSet> map = new HashMap<>();
						map.put(iset.isetkey, iset);
						lm.put(iset.lkey, map);
					} else
						lm.get(iset.lkey).put(iset.isetkey, iset);

					for (int i = dn - 1; i >= 0; i--) {
						if (index[i] < max + i - dn) {
							index[i] += 1;
							for (int j = i + 1; j < dn; j++) {
								index[j] = index[j - 1] + 1;
							}
							break;
						}
						// すべてのパラメータを調べ終わった場合
						else if (i == 0)
							end_flag = true;
					}
				} while (!end_flag);
			}

			b.interactions = is;
			b.interaction_map = im;
			b.interactionset_map = ism;
			b.location_map = lm;
			b.nocoveredmap = ncm;

			Map<List<IntBuffer>, InteractionSet> nlm = new HashMap<>();
			for (Map.Entry<IntBuffer, Map<List<IntBuffer>, InteractionSet>> e : b.location_map.entrySet()) {
				if (e.getValue().size() > 1 && !e.getKey().equals(zerokey)) {
					for (InteractionSet iset : e.getValue().values())
						nlm.put(iset.isetkey, iset);
				}
			}

			b.nolocatedmap = nlm;
			assert b.misslocation == b.nolocatedmap.size();
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return b;
	}
}
