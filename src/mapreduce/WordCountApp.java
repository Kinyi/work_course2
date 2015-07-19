package mapreduce;

import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;

public class WordCountApp {

	private static final String INPUT_PATH = "hdfs://hadoop0:9000/hello";
	private static final String URI = "hdfs://hadoop0:9000/";
	private static final String OUTPUT_PATH = "hdfs://hadoop0:9000/out";

	public static void main(String[] args) throws Exception {
		Configuration conf = new Configuration();
		FileSystem fileSystem = FileSystem.get(new URI(URI), conf);
		if (fileSystem.exists(new Path(OUTPUT_PATH))) {
			fileSystem.delete(new Path(OUTPUT_PATH), true);
		}
		Job job = new Job(conf, WordCountApp.class.getSimpleName());
		job.setJarByClass(WordCountApp.class);
		FileInputFormat.setInputPaths(job, new Path(INPUT_PATH));
		job.setMapperClass(MyMapper.class);
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(LongWritable.class);
		job.setReducerClass(MyReducer.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(LongWritable.class);
		FileOutputFormat.setOutputPath(job, new Path(OUTPUT_PATH));
		job.waitForCompletion(true);
	}
	
	//引入hashMap，如果每行中有重复的单词，那么map输出的键值对就会有所减少
	static class MyMapper extends
			Mapper<LongWritable, Text, Text, LongWritable> {
		@SuppressWarnings("unchecked")
		@Override
		protected void map(
				LongWritable key,
				Text value,
				@SuppressWarnings("rawtypes") org.apache.hadoop.mapreduce.Mapper.Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] split = line.split("\t");
			HashMap<String, Long> map = new HashMap<String, Long>();
			for (String word : split) {
				Long times = 1L;
				if (!map.keySet().contains(word)) {
					map.put(word, times);
				} else {
					long newValue = map.get(word) + 1;
					map.put(word, newValue);
				}
			}
			for (String keys : map.keySet()) {
				context.write(new Text(keys), new LongWritable(map.get(keys)));
			}
		}
	}

/*	static class MyMapper extends
			Mapper<LongWritable, Text, Text, LongWritable> {
		@SuppressWarnings("unchecked")
		@Override
		protected void map(
				LongWritable key,
				Text value,
				@SuppressWarnings("rawtypes") org.apache.hadoop.mapreduce.Mapper.Context context)
				throws IOException, InterruptedException {
			String line = value.toString();
			String[] split = line.split("\t");
			for (String word : split) {
				context.write(new Text(word), new LongWritable(1));
			}
		}
	}*/

	static class MyReducer extends
			Reducer<Text, LongWritable, Text, LongWritable> {
		@Override
		protected void reduce(Text key, Iterable<LongWritable> values,
				Context context) throws IOException, InterruptedException {
			long total = 0L;
			for (LongWritable times : values) {
				total += times.get();
			}
			context.write(key, new LongWritable(total));
		}
	}
}
