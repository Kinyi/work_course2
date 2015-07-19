package hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.util.Bytes;

/**
 * 该文件的作用是使用Java代码筛选出只知道rowkey非前面部分的行，但filter不仅可以作用于行，还可以作用于前缀(PrefixFilter)和列(QualifierFilter)
 * 当使用多个filter时，可以把多个filter添加到FilterList中，最后把FilterList传给scan对象
 */
public class HBaseTest2 {
	public static final String TABLE_NAME = "hmbbs_logs";

	public static void main(String[] args) throws Exception{
		scan();
	}
	
	private static void scan() throws IOException {
		HTable hTable = new HTable(getConf(), TABLE_NAME);
		//使用scan对象可以设定startRow、stopRow，使用filter可以设定指定的行和列
		Scan scan = new Scan();
		//使用正则表达式来匹配想要的行
		RowFilter rowFilter = new RowFilter(CompareOp.EQUAL, new RegexStringComparator("^\\d+\\.\\d+\\.\\d+\\.143[:]\\d*[:]-?\\d*$"));
		scan.setFilter(rowFilter);
		ResultScanner scanner = hTable.getScanner(scan);  //整行
		//指定列簇、列
//		ResultScanner scanner = hTable.getScanner(Bytes.toBytes("f1"), Bytes.toBytes("c1"));  //指定的列
		for (Result result : scanner) {  //迭代出每一条记录
			byte[] row = result.getRow();
			byte[] value = result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("date"));
			System.out.println("rowkey -> "+Bytes.toString(row)+", cf:date -> "+Bytes.toString(value));
		}
		
		hTable.close();  //关闭hbase连接
	}

	private static Configuration getConf() {
		Configuration conf = HBaseConfiguration.create();
		conf.setStrings("hbase.zookeeper.quorum", "hadoop0");
		return conf;
	}

}
