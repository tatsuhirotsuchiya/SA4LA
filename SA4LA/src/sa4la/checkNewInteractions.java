package sa4la;

import java.util.Collections;

//変異の結果、新しく網羅されたinteractionを探し、データを更新するクラス
public class checkNewInteractions extends checkInteractions {
	checkNewInteractions(State state) {
		super(state);
	}

	// テストケース生成
	@Override
	protected int[] getTestCase() {
		return state.mtestcase;
	}

	// ttestcases更新
	@Override
	protected void updateTestcases(Interaction i) {
		// System.out.print(key + ": ");
		// i.print();
		assert !i.testcases.contains(state.mrow);
		i.testcases.add(state.mrow);
		Collections.sort(i.testcases);
		i.num += 1;
		// missinteractionの更新
		if (i.num == 1) {// interactionが新しく網羅された場合
			dif -= 1;
			assert state.nocoveredmap.containsKey(i.ikey);
			state.nocoveredmap.remove(i.ikey);
		}
	}
}
