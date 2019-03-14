package sa4la;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.List;

//interactionの情報を格納するクラス

public class Interaction implements Cloneable {
	public final int[] columns;// 各パラメータの位置を格納した配列
	public final int[] values;// 各値を格納した配列
	public final IntBuffer ikey;// interaction特定用のキー
	public List<Integer> testcases = null;// 各テストケースがinteractionをcoverしているかを格納する配列
	public int num = 0;

	Interaction(int[] columns, int[] values) {
		this.columns = columns;
		this.values = values;

		// ikey生成
		int[] b = new int[columns.length + values.length];
		for (int i = 0; i < columns.length; i++)
			b[i] = columns[i];
		for (int i = 0; i < values.length; i++)
			b[columns.length + i] = values[i];
		ikey = IntBuffer.wrap(b);
	}

	// testcasesの初期化
	public void initialtestcases(int row) {
		testcases = new ArrayList<>(row);
		num = 0;
	}

	// 保持しているinteractionを表示
	public void print() {
		System.out.print("[");
		for (int i = 0; i < columns.length; i++) {
			System.out.print(columns[i]);
			if (i != columns.length - 1)
				System.out.print(", ");
		}
		System.out.print("] = (");
		for (int i = 0; i < values.length; i++) {
			System.out.print(values[i]);
			if (i != values.length - 1)
				System.out.print(", ");
		}
		System.out.println(")");
	}

	@Override
	public Interaction clone() {
		Interaction b = null;
		try {
			b = (Interaction) super.clone();
			if (this.testcases != null)
				b.testcases = new ArrayList<>(this.testcases);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return b;
	}
}
