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

		int lower = offset;
		int upper = offset + size - 1;
		
		System.out.println("Start Binary Search");

        assert (size == upper - lower + 1);
        
		while (size > 0) {
		    assert (size == upper - lower + 1);
		    
			int division = offset + (int) ((double) size * partition);

			System.out.println("trying " + division + " rows");
			System.out.println("Searching");

			// LAが見つかったのかで場合分け
			if (aneal.search(division)) {
				System.out.println("Get Locating Array with " + division + " rows.");
				// new Option().getstate().printarray();
				
				size = division - offset;
				result = division;
				
				upper = division - 1;
	            assert (size == upper - lower + 1);
				
			} else {
				System.out.println("Failed to get Locating Array with " + division + " rows.");
                lower = division + 1;
                
				division++;
				size += offset - division;
				offset = division;
				
                assert (size == upper - lower + 1);				
			}
		}
		return result; // みつからなかったら offset + sizeがかえる
	}
}
