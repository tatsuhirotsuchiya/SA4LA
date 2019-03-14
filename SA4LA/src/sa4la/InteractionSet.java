package sa4la;

import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

//interactionの集合を格納するクラス
public class InteractionSet {
	final List<Interaction> set;// 集合
	final int num;// 集合の要素数
	final List<IntBuffer> isetkey;// interactionset特定のためのキー
	IntBuffer lkey = null;// locationmap用のキー

	public InteractionSet(List<Interaction> interactionset) {
		set = interactionset;
		num = set.size();

		// isetkey生成
		List<IntBuffer> keys = new ArrayList<>(set.size());
		for (Interaction i : set)
			keys.add(i.ikey);
		Collections.sort(keys);// ソート
		isetkey = keys;
	}

	// testcasesを表示
	public List<Integer> testcases() {
		Set<Integer> tset = new HashSet<>();
		for (Interaction i : set)
			tset.addAll(i.testcases);

		List<Integer> testcases = new ArrayList<>(tset);
		Collections.sort(testcases);
		return testcases;
	}

	// location_key生成用メソッド
	public void genLocationKey() {
		List<Integer> list = testcases();
		int[] array = new int[list.size()];
		for (int i = 0; i < array.length; i++)
			array[i] = list.get(i);
		lkey = IntBuffer.wrap(array);
	}
}
