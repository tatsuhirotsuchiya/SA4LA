package sa4la;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.stream.Stream;

public class InputFile {
	private final Option option = new Option();// グローバル変数用クラス

	// テキストファイルを読み込み、値をセットする
	public void setvariables(String name) {
		try {
			// ファイルを読み込む
			File file = new File(name);
			BufferedReader br = new BufferedReader(new FileReader(file));

			// テキストから値を取り出し、セットする
			option.setstrength(Integer.parseInt(br.readLine()));// 強度を取り出す
			option.setcolumn(Integer.parseInt(br.readLine()));// パラメータ数と取り出す

			// 値数と取り出す
//			String[] str = br.readLine().split(" ", 0);
			String[] str = br.readLine().split("\\s+");
			int[] values = Stream.of(str).mapToInt(Integer::parseInt).toArray();// 型変換
			option.setvalues(values);

			// ファイルを閉じる
			br.close();

		} catch (FileNotFoundException e) {
			System.out.println(e);
		} catch (IOException e) {
			System.out.println(e);
		}

	}
}
