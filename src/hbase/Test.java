package hbase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		Pattern pattern = Pattern.compile("\\d+\\.\\d+\\.\\d+\\.143[:]\\d*[:]-?\\d*");
//		Pattern pattern = Pattern.compile("\\d*\\.");
		Matcher matcher = pattern.matcher("martin192.168.80.143:13266491017:-1868116341kinyi");
		if (matcher.find()) {
			String result = matcher.group();
			System.out.println(result);
		}else {
			System.out.println("match nothing");
		}
	}

}
