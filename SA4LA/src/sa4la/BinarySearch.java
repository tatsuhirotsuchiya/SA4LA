package sa4la;

//CASA BinarySeach.C内のアルゴリズムを使用

//Binary searchを行うクラス
public class BinarySearch {
	private AnealSearch aneal = new AnealSearch();

	// 探索するメソッド
	// offset = low, offset + size -1 = high
	public int search(int offset, int size) {
//		double partition = 0.7;// サイズの減少率
		double partition = 0.5;// サイズの減少率
		int result = offset + size;

		System.out.println("Start Binary Search");

		while (size > 0) {
			int division = offset + (int) ((double) size * partition);

			System.out.println("trying " + division + " rows");
			System.out.println("Searching");

			// LAが見つかったのかで場合分け
			if (aneal.search(division)) {
				System.out.println("Get Locating Array with " + division + " rows.");
				size = division - offset;
				result = division;
			} else {
				System.out.println("Failed to get Locating Array with " + division + " rows.");
				division++;
				size += offset - division;
				offset = division;
			}
		}
		return result;
	}
}
