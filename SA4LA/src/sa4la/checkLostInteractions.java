package sa4la;

//変異の結果、失われたinteractionを探し、データを更新するクラス
public class checkLostInteractions extends checkInteractions {
	checkLostInteractions(State state) {
		super(state);
	}

	// テストケース生成
	@Override
	protected int[] getTestCase() {
		return state.pretestcase;
	}

	// testcases更新
	@Override
	public void updateTestcases(Interaction i) {
		/// System.out.print(key + ": ");
		// i.print();
		assert i.testcases.indexOf(state.mrow) != -1;
		i.testcases.remove(i.testcases.indexOf(state.mrow));
		i.num -= 1;
		// missinteractionの更新
		if (i.num == 0) {
			dif += 1;
			assert !state.nocoveredmap.containsKey(i.ikey);
			state.nocoveredmap.put(i.ikey, i);
		}
	}
}
