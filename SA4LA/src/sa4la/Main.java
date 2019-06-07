package sa4la;

import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Main {
    
    static final long start = System.currentTimeMillis();
    
	public static void main(String[] args) {
		Option option = new Option();// グローバル変数用
		long seed = 0;
		boolean seedflag = false;
		int i = 0;

		// 引数がない場合は、ヘルプ表示 & 終了
		if (args.length == 0) {
			Usage usage = new Usage();
			usage.print();
			System.exit(0);
		} else {
			// 引数からオプションを取り出す
			while (i < args.length) {
				// オプション-h(--help)が入っていた場合は、ヘルプ表示 & 終了
				if (args[i].equals("-h") || args[i].equals("--help")) {
					Usage usage = new Usage();
					usage.print();
					System.exit(0);
				}

				// オプション-sが入っていた場合は, seed指定
				else if (args[i].equals("-s") || args[i].equals("--seed")) {
					i++;
					seed = Long.parseLong(args[i]);
					seedflag = true;
				}

				// オプション-iが入っていた場合は, iterations指定
				else if (args[i].equals("-i") || args[i].equals("--iterations")) {
					i++;
					int iterations = Integer.parseInt(args[i]);
					option.setiterations(iterations);
				}

				// オプション-rが入っていた場合は, retries 指定
				else if (args[i].equals("-r") || args[i].equals("--retries")) {
					i++;
					int retries = Integer.parseInt(args[i]);
					option.setretries(retries);
				}

				// オプション-tが入っていた場合は, temperature指定
				else if (args[i].equals("-t") || args[i].equals("--temperature")) {
					i++;
					double temperature = Double.parseDouble(args[i]);
					option.settemperature(temperature);
				}

				// オプション--coolが入っていた場合は, cooling 係数を指定
				else if (args[i].equals("--cool")) {
					i++;
					double coolingCoefficient = Double.parseDouble(args[i]);
					option.setdecrement(coolingCoefficient);
				}
				
				// オプション--baselineが入っていた場合は, baselineアルゴリズムを選択
				else if (args[i].equals("--baseline")) {
					option.setBaselineAlgorithm(true);
				}
				
				// オプション-wが入っていた場合は, temperature指定
				else if (args[i].equals("-w") || args[i].equals("--weight")) {
					i++;
					double weight = Double.parseDouble(args[i]);
					option.setweight(weight);
				}
				
				// オプション-uが入っていた場合は, 初期upper bound指定
				else if (args[i].equals("-u") || args[i].equals("--upper-bound")) {
					i++;
					int upper = Integer.parseInt(args[i]);
					option.setupper(upper);
				}

				// オプション-lが入っていた場合は, 初期lower bound指定
				else if (args[i].equals("-l") || args[i].equals("--lower-bound")) {
					i++;
					int lower = Integer.parseInt(args[i]);
					option.setlower(lower);
				}

				// オプション-dが入っていた場合は, locateできる数を変更
				else if (args[i].equals("-d") || args[i].equals("--detection-number")) {
					i++;
					int detectednum = Integer.parseInt(args[i]);
					option.setdetectednum(detectednum);
				}

				// オプション-oが入っていた場合は, 出力ファイル指定
				else if (args[i].equals("-o") || args[i].equals("--output")) {
					i++;
					try {
						// debug.log に出力する PrintStream を生成
						PrintStream out = new PrintStream(args[i]);

						// 置き換える
						System.setOut(out);
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
				}

				// オプション指定がない場合
				else
					break;
				i++;
			}
		}

		// シード値が未設定ならランダム生成する
		if (!seedflag) {
			seed = System.currentTimeMillis();
			System.out.println("Choosing random seed: " + seed);
		}

		// シード値をセットする
		option.setseed(seed);

		// 入力用ファイルを読み込み、テキストを取り出す
		InputFile ifile = new InputFile();
		ifile.setvariables(args[i]);

		// SA開始
		Aneal aneal = new Aneal();
		aneal.start();
	}
}