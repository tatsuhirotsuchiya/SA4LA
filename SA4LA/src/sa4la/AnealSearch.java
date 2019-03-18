package sa4la;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

//SAを実行するクラス
public class AnealSearch {
	private final Option option = new Option();// グローバル変数用クラス
	private final int dnum = option.getdetectednum();// detectできる数
	private final int strength = option.getstrength();// 強度
	private final int[] values = option.getvalues();// 値数
	private final int column = option.getcolumn();// パラメータ数
	private int row;
	private final Random rnd = new Random(option.getseed());// 乱数生成用インスタンス
	private final IntBuffer zerokey = IntBuffer.wrap(new int[0]);// 空のIntBuffer

	// Simulated Annealing実行
	public boolean search(int row) {
		double temperature = option.gettemperature();// 温度
		double decrement = option.getdecrement();// 減衰率
		this.row = row;// 行を保存

		// アレイをランダム生成
		int[][] array = new int[row][column];
		for (int i = 0; i < row; i++) {
			for (int j = 0; j < column; j++) {
				array[i][j] = rnd.nextInt(values[j]);
			}
		}

		// interactionsの設定
		List<Interaction> interactions = new ArrayList<>(option.getinteractions().size());
		for (Interaction i0 : option.getinteractions()) {
			Interaction i = i0.clone();
			interactions.add(i);
		}

		State state = new State(array, interactions, dnum, strength);// 状態の作成
		State neighborhood;
		double p;

		// 繰り返し数を満たす、温度が下がりきる、またはfitnessが0になるまでループ
		int i = 1;// 初期化を1回と考える
		int iterations = option.getiterations();// 繰り返しの最大数
		while (i < iterations && temperature > 0.0001 && state.fitness() > 0) {
			neighborhood = mutatestate(state);
			neighborhood = calculatefitness(neighborhood);

			// fitnessが同値、または小さくなれば更新
			if (state.fitness() >= neighborhood.fitness())
				state = neighborhood;

			// fitnessが大きくなった場合は確率で更新
			else {
				double b = (double) (state.fitness() - neighborhood.fitness()) / temperature;
				p = Math.pow(Math.E, b);
				// System.out.println(p);// 確率表示

				if (rnd.nextDouble() <= p)
					state = neighborhood;
			}

			// 温度を下げる
			temperature = decrement * temperature;
			i++;

			// state.printarray();
			// System.out.println("misslocation: " + state.misslocation + ",
			// missinteraction: " + state.missinteraction);
			// System.out.println();
		}

		// 結果を返す
		System.out.println("Stopped search after " + i + "/" + option.getiterations() + " iteration(s)");
		System.out.println("Uncovered Interaction: " + state.missinteraction);
		System.out.println("Covered but not located interaction: " + state.misslocation);
		if (state.fitness() == 0) {
			option.setstate(state);
			return true;
		} else
			return false;
	}

	// 変異用メソッド
	private State mutatestate(State state) {
		// stateをコピー
		State mstate = state.clone();

		// 変異
		// カバーできていないinteactionがある場合
		if (mstate.missinteraction > 0) {
			// 変異に使用するinteractionをランダムに決定
			IntBuffer[] keys = mstate.nocoveredmap.keySet().toArray(new IntBuffer[mstate.nocoveredmap.size()]);
			Interaction i = mstate.nocoveredmap.get(keys[rnd.nextInt(keys.length)]);
			assert i.num == 0;
			mstate.mrow = rnd.nextInt(row);
			// 変異前のテストケースを保存
			mstate.pretestcase = mstate.array[mstate.mrow].clone();
			// テストケースを変異
			for (int c = 0; c < i.columns.length; c++)
				mstate.array[mstate.mrow][i.columns[c]] = i.values[c];

			assert !Arrays.equals(mstate.array[mstate.mrow], mstate.pretestcase);
		} else { // すべてcoverできている場合
			// locateできていないinteractionのキーを抽出
			@SuppressWarnings("unchecked")
			List<IntBuffer>[] keys = mstate.nolocatedmap.keySet().toArray(new ArrayList[mstate.nolocatedmap.size()]);
			// interactionsetをランダムに選択
			InteractionSet iset = mstate.nolocatedmap.get(keys[rnd.nextInt(keys.length)]);
			List<Integer> testcases = iset.testcases();
			int num = testcases.size();

			// coverされているテストケースが1つの場合
			if (num == 1) {
				do { // まだcoverされていないテストケースをランダムに選択
					mstate.mrow = rnd.nextInt(row);
				} while (mstate.mrow == testcases.get(0));
				// 変異前のテストケースを保存
				mstate.pretestcase = mstate.array[mstate.mrow].clone();
				// 変異に使うinteractionをisetからランダムに決定
				Interaction i = iset.set.get(rnd.nextInt(iset.num));
				// テストケースを変異
				for (int c = 0; c < i.columns.length; c++)
					mstate.array[mstate.mrow][i.columns[c]] = i.values[c];
				assert !Arrays.equals(mstate.array[mstate.mrow], mstate.pretestcase);
			} else { // coverされているテストケースが2つ以上の場合
				if (rnd.nextBoolean()) {// 増やす場合
					// まだcoverされていないテストケースをランダムに選択
					boolean end = false;
					do {
						end = true;
						mstate.mrow = rnd.nextInt(row);
						for (int r : testcases) {
							if (mstate.mrow == r) {
								end = false;
								break;
							}
						}
					} while (!end);
					// 変異前のテストケースを保存
					mstate.pretestcase = mstate.array[mstate.mrow].clone();
					// 変異に使うinteractionをランダムに決定
					Interaction i = iset.set.get(rnd.nextInt(iset.num));
					// テストケースを変異
					for (int c = 0; c < i.columns.length; c++)
						mstate.array[mstate.mrow][i.columns[c]] = i.values[c];
					assert !Arrays.equals(mstate.array[mstate.mrow], mstate.pretestcase);
				} else {// 減らす場合
					// coverされているテストケースから選択
					mstate.mrow = testcases.get(rnd.nextInt(testcases.size()));
					// mrowに含まれているiset内のinteractionを取り出す
					List<Interaction> minteractions = new ArrayList<>();
					for (Interaction i : iset.set) {
						// interactionが変異させるテストケースに含まれていた場合
						if (i.testcases.contains(mstate.mrow)) {
							minteractions.add(i);
						}
					}
					assert minteractions.size() > 0;
					int[] mtestcase;// 変異させるテストケース
					boolean end;
					do {
						mtestcase = mstate.array[mstate.mrow].clone();
						// minteractions内のinteractionがmrowに含まれないように変異
						for (Interaction i : minteractions) {
							int mcolumn = i.columns[rnd.nextInt(i.columns.length)];
							int mvalue;
							do {
								mvalue = rnd.nextInt(values[mcolumn]);
							} while (mvalue == mtestcase[mcolumn]);
							mtestcase[mcolumn] = mvalue;
						}
						end = true;
						// 変異させたテストケースに各interactionが含まれていないか確認
						int[] values = new int[strength];
						for (Interaction i : iset.set) {
							for (int t = 0; t < strength; t++)
								values[t] = mtestcase[i.columns[t]];
							if (Arrays.equals(i.values, values)) {
								end = false;
								break;
							}
						}
					} while (!end);
					// 変異前のテストケースを保存
					mstate.pretestcase = mstate.array[mstate.mrow].clone();
					mstate.array[mstate.mrow] = mtestcase;// 変異

					assert !Arrays.equals(mstate.array[mstate.mrow], mstate.pretestcase);
				}
			}
		}

		mstate.mtestcase = mstate.array[mstate.mrow].clone();
		return mstate;

	}

	// fitnessの差分計算用メソッド
	private State calculatefitness(State state) {
		assert state.missinteraction == state.nocoveredmap.size();
		state.mikeys.clear();// mikeysをリセット
		// 変異の結果、新しく現れたinteractionを管理するスレッド
		Thread t1 = new Thread(new checkNewInteractions(state));
		t1.start();
		// 変異の結果、失われたinteractionを管理するスレッド
		Thread t2 = new Thread(new checkLostInteractions(state));
		t2.start();

		// t1, t2が終了するまで、待機
		// while (t1.isAlive() || t2.isAlive())
		//	;
		try {
		 	t1.join(); t2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		assert state.missinteraction == state.nocoveredmap.size();

		// 以下、変異の影響を受けたinteractionsetを取り出す
		Set<IntBuffer> keyset = new HashSet<>(state.interaction_map.keySet());// interactionのキーのセット
		List<IntBuffer> mkeys = state.mikeys;// 変異の影響を受けたinteractionのキーのリスト
		// 変異の影響を受けていないinteractionのキーを取り出す
		for (IntBuffer key : mkeys)
			keyset.remove(key);
		List<IntBuffer> keys = new ArrayList<>(keyset);// セットをリストに変換

		// キーの組み合わせを列挙
		for (int dn = 1; dn <= dnum; dn++) {
			List<IntBuffer> ikeys = new ArrayList<>(dn);
			List<IntBuffer> mikeys = new ArrayList<>(dn);
			for (int i1 = 1, i1_max = mkeys.size(); i1 <= i1_max && i1 <= dn; i1++) {
				boolean end_flag1 = false;
				int[] index1 = new int[i1];
				// 1つめの設定
				for (int d = 0; d < i1; d++)
					index1[d] = d;

				// 2つめ以降の設定
				while (!end_flag1) {
					mikeys.clear();
					for (int d = 0; d < i1; d++)
						mikeys.add(mkeys.get(index1[d]));

					// 処理
					boolean end_flag2 = false;
					int i2 = dn - i1;
					int i2_max = keys.size();
					int[] index2 = new int[i2];
					// 1つめの設定
					if (i2 > 0) {
						for (int d = 0; d < i2; d++)
							index2[d] = d;
					} else
						end_flag2 = true;

					// 2つめ以降の設定
					do {
						ikeys.clear();
						for (int d = 0; d < i2; d++)
							ikeys.add(keys.get(index2[d]));

						// 処理
						ikeys.addAll(mikeys);
						Collections.sort(ikeys);

						// location_map操作
						operateLocationMap(state, ikeys);

						for (int i = i2 - 1; i >= 0; i--) {
							if (index2[i] < i2_max + i - i2) {
								index2[i] += 1;
								for (int j = i + 1; j < i2; j++) {
									index2[j] = index2[j - 1] + 1;
								}
								break;
							}
							// すべてのパラメータを調べ終わった場合
							else if (i == 0)
								end_flag2 = true;
						}
					} while (!end_flag2);

					for (int i = i1 - 1; i >= 0; i--) {
						if (index1[i] < i1_max + i - i1) {
							index1[i] += 1;
							for (int j = i + 1; j < i1; j++) {
								index1[j] = index1[j - 1] + 1;
							}
							break;
						}
						// すべてのパラメータを調べ終わった場合
						else if (i == 0)
							end_flag1 = true;
					}
				}
			}
		}

		return state;
	}

	// location_map操作メソッド
	private void operateLocationMap(State state, List<IntBuffer> key) {
		assert state.misslocation == state.nolocatedmap.size();
		assert state.interactionset_map.containsKey(key);
		InteractionSet iset = state.interactionset_map.get(key);

		// interactionsetをリストから削除
		assert state.location_map.containsKey(iset.lkey) : "1: Map oparation failed.";
		assert state.location_map.get(iset.lkey).containsKey(iset.isetkey) : "2: Map oparation failed.";
		state.location_map.get(iset.lkey).remove(iset.isetkey);
		if (state.location_map.get(iset.lkey).size() == 0) {// 空集合になった場合
			state.location_map.remove(iset.lkey);// マップから削除
		}
		// 残ったinteractionsetがlocateできるようになった場合
		else if (!iset.lkey.equals(zerokey) && state.location_map.get(iset.lkey).size() == 1) {
			state.misslocation -= 2;
			assert state.nolocatedmap.containsKey(iset.isetkey);
			state.nolocatedmap.remove(iset.isetkey);

			for (InteractionSet is : state.location_map.get(iset.lkey).values()) {
				assert state.nolocatedmap.containsKey(is.isetkey);
				state.nolocatedmap.remove(is.isetkey);
			}
		}
		// locate情報に変化がない場合
		else if (!iset.lkey.equals(zerokey)) {
			state.misslocation -= 1;
			assert state.nolocatedmap.containsKey(iset.isetkey);
			state.nolocatedmap.remove(iset.isetkey);
		}

		// interactionを再び追加
		iset.genLocationKey();// キー生成
		if (!state.location_map.containsKey(iset.lkey)) {
			Map<List<IntBuffer>, InteractionSet> map = new HashMap<>();
			map.put(iset.isetkey, iset);
			state.location_map.put(iset.lkey, map);
		} else {
			state.location_map.get(iset.lkey).put(iset.isetkey, iset);
			// もともとあったinteractionがlocateできなくなったため+2
			if (!iset.lkey.equals(zerokey) && state.location_map.get(iset.lkey).size() == 2) {
				state.misslocation += 2;
				for (InteractionSet is : state.location_map.get(iset.lkey).values()) {
					assert !state.nolocatedmap.containsKey(is.isetkey) : "1: interaction exist in state.nolocatedmap.";
					state.nolocatedmap.put(is.isetkey, is);
				}
			} else if (!iset.lkey.equals(zerokey)) {// それ以外は+1
				state.misslocation += 1;
				assert !state.nolocatedmap.containsKey(iset.isetkey) : "2: interaction exist in state.nolocatedmap.";
				state.nolocatedmap.put(iset.isetkey, iset);
			}
		}
		assert state.misslocation == state.nolocatedmap.size();
	}
}
