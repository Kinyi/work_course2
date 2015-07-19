package hdfs;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FSDataInputStream;
import org.apache.hadoop.fs.FSDataOutputStream;
import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IOUtils;

public class App2 {
	
	private static final String URI = "hdfs://chaoren1:9012/";

	public static void main(String[] args) throws Exception {
		
		final FileSystem fileSystem = FileSystem.get(new URI(URI), new Configuration());
		

		//mkdir(fileSystem);

		//put(fileSystem);

		//get(fileSystem);

		list(new Path("/user/cr12"),fileSystem);

		//delete(fileSystem);
		
	}


	public static void list(Path path,final FileSystem fileSystem) throws Exception {
		final FileStatus[] listStatus = fileSystem.listStatus(path);
		for (FileStatus fileStatus : listStatus) {
			String isDir = fileStatus.isDir()?"d":"-";
			String copy="3";
			final String permission = fileStatus.getPermission().toString();
			final short replication = fileStatus.getReplication();
			if(replication==0){
				copy="-";
			}
			final String owner = fileStatus.getOwner();
			final String group = fileStatus.getGroup();
			final long len = fileStatus.getLen();
			final Long time = fileStatus.getModificationTime();
			final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
			final String parseTime = simpleDateFormat.format(new Date(time));
			final String way = fileStatus.getPath().toString().substring(20);
			System.out.println(isDir+permission+"\t"+copy+" "+owner+" "+group+" \t"+len+" "+parseTime+" "+way);
			if(fileStatus.isDir()){
				list(fileStatus.getPath(), fileSystem);
			}
		}
	}
	
	public static void delete(final FileSystem fileSystem) throws IOException {
		final boolean delete = fileSystem.delete(new Path("/hello"), true);
		if(delete){
			System.out.println("the file has been deleted");
		}
	}

	public static void get(final FileSystem fileSystem) throws IOException {
		final FSDataInputStream in = fileSystem.open(new Path("/test/hello"));
		IOUtils.copyBytes(in, System.out, 1024, false);
		in.close();
	}

	public static void put(final FileSystem fileSystem) throws IOException,
			FileNotFoundException {
		final FSDataOutputStream out = fileSystem.create(new Path("/test/hello"));
		final FileInputStream in = new FileInputStream("/mnt/home/cr12/hello");
		IOUtils.copyBytes(in, out, 1024, true);
	}

	public static void mkdir(final FileSystem fileSystem) throws IOException {
		final boolean mkdir = fileSystem.mkdirs(new Path("/test"));
		if(mkdir){
			System.out.println("make directory successfully");
		}
	}
}


/*public class App2 {   //weigezhidian
	public static void main(String[] args) throws Exception {
		final Configuration conf = new Configuration();
		final FileSystem fileSystem = FileSystem.get(new URI("hdfs://hadoop1:9000/"), conf);
		System.out.println("hi!");
		listDir(new Path("/"), fileSystem);
	}

	public static void listDir(Path path, FileSystem fileSystem) throws IOException {
		FileStatus[] listStatus = fileSystem.listStatus(path);
		for (FileStatus fileStatus : listStatus) {
			System.out.println(fileStatus.getPath());
			if (fileStatus.isDir()) {
				listDir(fileStatus.getPath(), fileSystem);
			} 
		}
	}
}*/

