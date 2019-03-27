package sa4la;
import java.util.ArrayList;
import java.util.List;

//CASA Anneal.C内のアルゴリズムを使用

//SAの全体の動きを管理するクラス
public class Aneal {
	private final Option option = new Option();// グローバル変数用クラス

	public void start() {
		// グローバル変数から取り出し
		final int strength = option.getstrength();// 強度
		final int dnum = option.getdetectednum();// locateできる数
		final int[] values = option.getvalues();// 値数
		final int column = option.getcolumn();// パラメータ数

		if (option.getBaselineAlgorithm() == true) {
			System.out.println("*** baseline algorithm");
		}
		else {
			System.out.println("proposed algorithm");
		}
		System.out.println("Start to set up for searching (" + dnum + "," + strength + ")-Locating Array (column: "
				+ column + ")");

		final BinarySearch search = new BinarySearch();

		// 初期lower boundの設定
		int lower = option.getlower();
		if (lower <= 0) {
			int min = getminvalue(values);// 最小の値数を取り出す
			// 最小の値数からLower boundの計算
			lower = getlowerbound(strength, min, column);
		}

		// 初期upper boundの設定
		int upper = option.getupper();
		if (upper <= 0) {
			int max = getmaxvalue(values);// 最大の値数を取り出す
			// Upper boundの計算(１つ値の大きなLAのlowerbound)
			upper = getlowerbound(strength, max + 1, column) * dnum;
		}

		// lowerがupperより大きい場合、逆にする
		if (upper < lower) {
			int temp = upper;
			upper = lower;
			lower = temp;
		}

		// Interactionsの生成
		System.out.println("Initialize interactions");
		setinteractions(strength, column, values);

		System.out.println("Suspect that the optimum number of rows is in [" + lower + ".." + upper + "]");
		System.out.println("Start annealing");
		long start = System.currentTimeMillis();// 時間計測開始

		int result = search.search(lower, upper - lower + 1);

		// 結果がUpper boundより大きかった場合(失敗した場合)、Upper boundを更新して，できるまでトライする
//		while (result > upper) {
//			upper += 1;
//			System.out.println("Trying more conservative upper bound " + upper);
//			result = search.search(lower, upper + 1 - lower);
//		}
		// 単にできるまでやる方向に変更
		while (result > upper) {
			System.out.println("Trying with upper bound " + upper);
			result = search.search(lower, upper + 1 - lower);
		}
		
		// リトライ開始
		int lastResult = result;
		int upped = 0;

		if (option.getretries() > 0) {
			do {
				if (lastResult == result) {
					option.setiterations(option.getiterations() * 1);// SAの繰り返し数を1倍に
					System.out.println("Upping iterations to " + option.getiterations());
					upped++;
				} else {
					upped = 0;
				}

				lastResult = result;
				System.out.println("Restarting binary search with best result at " + lastResult + " rows");
				result = search.search(lower, lastResult - lower);
			} while ((result < lastResult || upped < option.getretries()) && result > lower);
		}
		
/*		do {
			// resultがlower boundに達した場合、lower boundを下げる
//			if (lower == result) {
//				lower--;
//				if (lower < 4)// lower boundが4未満になることはない
//					break;
//				System.out.println("Trying less conservative lower bound " + lower);
//			}

			int lastResult = result;
			int upped = 0;

			do {
				if (lastResult == result) {
					option.setiterations(option.getiterations() * 2);// SAの繰り返し数を2倍に
					System.out.println("Upping iterations to " + option.getiterations());
					upped++;
				} else {
					upped = 0;
				}

				lastResult = result;
				System.out.println("Restarting binary search with best result at " + lastResult + " rows");
				result = search.search(lower, lastResult - lower);
			} while ((result < lastResult || upped < option.getretries()) && result > lower);
		} while (lower == result);*/

		// 結果表示
		long end = System.currentTimeMillis();// 時間計測終了
		System.out.println("Giving up with best result at " + result + " rows");
		System.out.println("Time: " + (end - start) + "ms");
		System.out.println();
		option.getstate().printarray();
	}

	// 配列から最小値を取り出す
	private int getminvalue(int[] values) {
		int min = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] < min)
				min = values[i];
		}
		return min;
	}

	// 配列から最大値を取り出す
	private int getmaxvalue(int[] values) {
		int max = values[0];
		for (int i = 1; i < values.length; i++) {
			if (values[i] > max)
				max = values[i];
		}
		return max;
	}

	// Tang氏のOptimality and Constructions of Locating Arrays
	// 内の式を使って、Lower boundの計算
	private int getlowerbound(int strength, int value, int column) {
		int n1 = 0;// optimal size using Theorem2.1
		int n2 = 0;// optimal size using Theorem2.2
		int c = combination(column, strength);// combination kCt

		// get n1
		if (strength >= 2 && column >= strength)
			n1 = (int) (Math.ceil((double) (2 * c * Math.pow(value, strength)) / (double) (1 + c)));
		else
			n1 = 0;

		// get n2
		if (strength >= 2 && value >= strength)
			n2 = (int) (Math.ceil(
					(double) -3 / 2 - c + Math.sqrt(c * c + (3 + 6 * Math.pow(value, strength)) * c + (double) 9 / 4)));
		else
			n2 = 0;

		// n1とn2を比較して大きい方を返す
		if (n1 > n2)
			return n1;
		else
			return n2;
	}

	// get combination kCt
	private int combination(int k, int t) {
		int n = 1;

		for (int i = 0; i < t; i++)
			n *= k - i;
		for (int i = 0; i < t; i++)
			n /= t - i;
		return n;
	}

	// Interactionを生成し、グローバル変数にセット
	private void setinteractions(int strength, int column, int[] values) {
		List<Interaction> interactions = new ArrayList<Interaction>();// interaction用のリストを生成
		int i_columns[] = new int[strength];
		int i_values[] = new int[strength];
		int v = 0;// 各valuesをあわせて10進数にしたもの
		int v_max = 1;// パラメータの組み合わせによるvの最大値
		boolean end_flag = false;// 下記の無限ループの終了フラグ

		// 1つめのinteractionおよびv_maxを設定
		for (int t = 0; t < strength; t++) {
			i_columns[t] = t;
			v_max *= values[i_columns[t]];
		}

		// 2つめ以降のinteractionを設定
		while (!end_flag) {
			// valuesの設定
			int v_quotient = v;
			for (int t = strength - 1; t >= 0; t--) {
				i_values[t] = v_quotient % values[i_columns[t]];
				v_quotient /= values[i_columns[t]];
			}
			interactions.add(new Interaction(i_columns.clone(), i_values.clone()));
			v++;

			// フラグがtrueの場合
			if (v == v_max) {
				// columnsの設定
				for (int i = strength - 1; i >= 0; i--) {
					if (i_columns[i] < (column - 1) + (i - (strength - 1))) {
						i_columns[i] += 1;
						for (int j = i + 1; j < strength; j++) {
							i_columns[j] = i_columns[j - 1] + 1;
						}
						break;
					}
					// すべてのパラメータを調べ終わった場合
					else if (i == 0)
						end_flag = true;
				}
				// v, v_maxをリセット
				v = 0;
				v_max = 1;
				// v_maxの再設定
				for (int t = 0; t < strength; t++)
					v_max *= values[i_columns[t]];
			}
		}

		// デバッグ用
		// for (int i = 0; i < interactions.size(); i++) {
		// System.out.print(i + ": ");
		// interactions.get(i).print();
		// }
		// System.out.println();

		// グローバル変数にセット
		option.setinteractions(interactions);
	}
}