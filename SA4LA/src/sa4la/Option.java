package sa4la;

import java.util.List;

//グローバル変数用クラス
public class Option {
	private static int strength;// 強度
	private static int detectednum = 1;// detectできる数
	private static int[] values;// 値数
	private static int column;// パラメータ数
	private static long seed;// 乱数用のシード値
	private static double temperature = 0.5;// 温度
	
//	private static double decrement = 0.99999;// 減衰率
	private static double decrement = 0.999;// 減衰率
//	private static int iterations = 1024;// 繰り返し数
	private static int iterations = 4096;// 繰り返し数
	private static int retries = 2;// リトライ数
	private static int upper = 0;// 初期upper bound
	private static int lower = 0;// 初期lower bound
	private static List<Interaction> interactions;// interactionのセット
	private static State state;// 最適な結果の状態を保存
	private static boolean isBaselineAlgorithm = false; // baseline algorithmをつかう

	// ゲッターとセッター
	public int getstrength() {
		return strength;
	}

	public void setstrength(int strength) {
		Option.strength = strength;
	}

	public int getdetectednum() {
		return detectednum;
	}

	public void setdetectednum(int detectednum) {
		Option.detectednum = detectednum;
	}

	public int[] getvalues() {
		return values;
	}

	public void setvalues(int[] values) {
		Option.values = values;
	}

	public int getcolumn() {
		return column;
	}

	public void setcolumn(int column) {
		Option.column = column;
	}

	public long getseed() {
		return seed;
	}

	public void setseed(long seed) {
		Option.seed = seed;
	}

	public double gettemperature() {
		return temperature;
	}

	public void settemperature(double temperature) {
		Option.temperature = temperature;
	}
	
	public double getdecrement() {
		return decrement;
	}

	public void setdecrement(double decrement) {
		Option.decrement = decrement;
	}

	public int getiterations() {
		return iterations;
	}

	public void setiterations(int iteration) {
		Option.iterations = iteration;
	}

	public int getretries() {
		return retries;
	}

	public void setretries(int retries) {
		Option.retries = retries;
	}

	public int getupper() {
		return upper;
	}

	public void setupper(int upper) {
		Option.upper = upper;
	}

	public int getlower() {
		return lower;
	}

	public void setlower(int lower) {
		Option.lower = lower;
	}

	public List<Interaction> getinteractions() {
		return interactions;
	}

	public void setinteractions(List<Interaction> interactions) {
		Option.interactions = interactions;
	}

	public State getstate() {
		return state;
	}

	public void setstate(State state) {
		Option.state = state;
	}

	public void setBaselineAlgorithm(boolean b) {
		Option.isBaselineAlgorithm  = b;
	}
	public boolean getBaselineAlgorithm() {
		return isBaselineAlgorithm;
	}
}
