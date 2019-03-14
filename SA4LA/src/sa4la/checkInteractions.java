package sa4la;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

//変異の結果、影響を受けたinteractionを探し、データを更新するクラス
public class checkInteractions implements Runnable {
	protected final Option option = new Option();// グローバル変数用クラス
	protected final int column = option.getcolumn();
	protected final int strength = option.getstrength();// 強度
	protected final State state;
	protected int dif = 0;// missinteractionの更新のため使用

	checkInteractions(State state) {
		this.state = state;
	}

	public void run() {
		int i_columns[] = new int[strength];
		int i_values[] = new int[strength];
		int[] testcase = getTestCase();// 探索対象とするテストケース
		List<IntBuffer> mlist = new ArrayList<>();// 変異の影響を受けたinteractionのキーを保存するリスト

		// 変異を受けたパラメータを取り出す
		List<Integer> mcolumns = new ArrayList<>(strength);
		for (int c = 0; c < column; c++) {
			if (state.mtestcase[c] != state.pretestcase[c])
				mcolumns.add(c);
		}
		// 変異を受けていないパラメータを取り出す
		List<Integer> columns = new ArrayList<>(column);
		for (int c = 0; c < column; c++)
			columns.add(c);
		for (int c = mcolumns.size() - 1; c >= 0; c--)// インデックスを保つため最後から削除
			columns.remove(mcolumns.get(c));

		// パラメータの組み合わせを列挙
		List<Integer> clist = new ArrayList<>(strength);
		List<Integer> cmlist = new ArrayList<>(strength);
		int i1 = 1;
		if (columns.size() == 0)// すべてのパラメータが影響を受けていた場合
			i1 = strength;
		for (int i1_max = mcolumns.size(); i1 <= i1_max; i1++) {
			boolean end_flag1 = false;
			int[] index1 = new int[i1];
			// 1つめの設定
			for (int t = 0; t < i1; t++)
				index1[t] = t;

			// 2つめ以降の設定
			while (!end_flag1) {
				cmlist.clear();
				for (int t = 0; t < index1.length; t++)
					cmlist.add(mcolumns.get(index1[t]));

				// 処理
				boolean end_flag2 = false;
				int i2 = strength - i1;
				int i2_max = columns.size();
				int[] index2 = new int[i2];
				// 1つめの設定
				if (i2 > 0) {
					for (int t = 0; t < index2.length; t++)
						index2[t] = t;
				} else
					end_flag2 = true;

				// 2つめ以降の設定
				do {
					clist.clear();
					for (int t = 0; t < index2.length; t++)
						clist.add(columns.get(index2[t]));

					// 処理
					clist.addAll(cmlist);
					Collections.sort(clist);
					for (int i = 0; i < i_columns.length; i++)
						i_columns[i] = clist.get(i);
					for (int i = 0; i < i_columns.length; i++)
						i_values[i] = testcase[i_columns[i]];
					final IntBuffer key = genInteractionKey(i_columns, i_values);// キーをセット
					updateTestcases(state.interaction_map.get(key));// interactionの情報をupdate
					mlist.add(key);

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

		// difを基にmissinteractionを更新
		state.updateMissinteraction(dif);
		// mlistを基にmiset_listを更新
		state.updatemInteractionkeys(mlist);
	}

	// interactionキーの生成メソッド
	protected IntBuffer genInteractionKey(int[] columns, int[] values) {
		int[] b = new int[columns.length + values.length];
		for (int i = 0; i < columns.length; i++)
			b[i] = columns[i];
		for (int i = 0; i < values.length; i++)
			b[columns.length + i] = values[i];
		return IntBuffer.wrap(b);
	}

	// 以下Override用メソッド
	// テストケース生成
	protected int[] getTestCase() {
		return new int[0];
	}

	// boolrows更新
	protected void updateTestcases(Interaction i) {
	}
}
