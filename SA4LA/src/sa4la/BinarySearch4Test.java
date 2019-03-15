package sa4la;

public class BinarySearch4Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		BinarySearch4Test t = new BinarySearch4Test();
		int result = t.search(100, 51);
		System.out.println("final result: " + result);
		
		System.out.println("------------------");
		result = t.tmpsearch(100, 150);
		System.out.println("final result: " + result);
		
	}

	// �T�����郁�\�b�h
	public int search(int offset, int size) {
		double partition = 0.7;// �T�C�Y�̌�����
		int result = offset + size;

		System.out.println("Start Binary Search");

		while (size > 0) {
			int division = offset + (int) ((double) size * partition);
			System.out.println("["+offset+","+division+","+Integer.toString(offset+size-1)+"]");
			
			System.out.println("trying " + division + " rows");
//			System.out.println("Searching");

			// LA�����������̂��ŏꍇ����
//			if (aneal.search(division)) {
			if (true) {
//				System.out.println("Get Locating Array with " + division + " rows.");
				size = division - offset;
				result = division;
			} else {
//				System.out.println("Failed to get Locating Array with " + division + " rows.");
				division++;
				size += offset - division;
				offset = division;
			}
		}
		return result;
	}

	// �T�����郁�\�b�h
	public int tmpsearch(int low, int high) {
		double partition = 0.7;// �T�C�Y�̌�����
		int result = high + 1;
		boolean isFound = false;
		
		System.out.println("Start Binary Search");

		while (low <= high) {
			int size = low + (int) ((double) (high - low) * partition);
			System.out.println("["+ low +","+ size +","+ high +"]");
			
			System.out.println("trying " + size + " rows");
//			System.out.println("Searching");

			// LA�����������̂��ŏꍇ����
//			if (aneal.search(division)) {
			if (true) {
	//			System.out.println("Get Locating Array with " + size + " rows.");
				high = size - 1;
				result = size;
			} else {
	//			System.out.println("Failed to get Locating Array with " + size + " rows.");
				low = size + 1;
			}
		}
		return result;
	}
}
