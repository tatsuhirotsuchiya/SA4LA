package sa4la;


public class Usage {
	public void print() {
		String str = "Locating Arrays by Simulated Annealing v20\n" + "\n"
				+ "-d,--detection-number [NUMBER]   set the max number can be detected faults \n"
				+ "-o,--output           [FILE]     write to the given file\n"
				+ "-s,--seed             [SEED]     set the seed value for the random number generator\n"
				+ "-i,--iteration        [COUNT]    set the initial number of iterations allowed at each array size\n"
				+ "-r,--retries          [COUNT]    set the number of retries allowed at the same array size\n"
				+ "-t,--temperature      [TEMP]     set the initial temperature\n"
				+ "-l,--lower-bound      [SIZE]     let the locating array be no smaller than the given size\n"
				+ "-u,--upper-bound      [SIZE]     let the locating array be no larger than the given size\n"
				+ "-w,--weight           [VALUE]    weight for 1st term of objective function\n"
				+ "--cool                [0< <1]    cooling coefficient \n"
				+ "--baseline                       use baseline algoirthm \n"
				+ "-h,--help                        show this help and exit\n\n\n";
		System.out.print(str);
	}
}
