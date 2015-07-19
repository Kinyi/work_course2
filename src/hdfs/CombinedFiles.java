package hdfs;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

public class CombinedFiles {

	private static final String URI = "hdfs://chaoren1:9012/";
	private static final String PATH = "/combinedfile";

	public static void main(String[] args) throws Exception {
		final FileSystem fileSystem = FileSystem.get(new URI(URI), new Configuration());
		final FSDataOutputStream create = fileSystem.create(new Path(PATH));
		String pathname = "/mnt/home/cr12/test";
		final File dir = new File(pathname);
		for (File fileName : dir.listFiles()) {
			System.out.println(fileName.getAbsolutePath());
			final FileInputStream fileInputStream = new FileInputStream(fileName.getAbsolutePath());
			final List<String> readLines = IOUtils.readLines(fileInputStream);
			for (String line : readLines) {
				create.write(line.getBytes());
			}
			fileInputStream.close();
		}
		create.close();
	}
}
