package hbase;

import java.io.IOException;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.HColumnDescriptor;
import org.apache.hadoop.hbase.HTableDescriptor;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.HBaseAdmin;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.Put;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

public class HBaseTest {
	public static final String TABLE_NAME = "hmbbs_logs";
	public static final String ROW_KEY = "rk1";

	public static void main(String[] args) throws Exception{
//		ddl(TABLE_NAME, "f1","f2");
//		dml();
		scan();
	}
	
	private static void scan() throws IOException {
		HTable hTable = new HTable(getConf(), TABLE_NAME);
		//使用scan对象可以设定startRow、stopRow，使用filter可以设定指定的列
		Scan scan = new Scan();
//		scan.setStartRow(startRow);
//		scan.setStopRow(stopRow);
		scan.setStartRow(Bytes.toBytes("27.19.74.143"));
		scan.setStopRow(Bytes.toBytes("27.19.74.144"));
//		scan.setFilter(filter);
		ResultScanner scanner = hTable.getScanner(scan);  //整行
		//指定列簇、列
//		ResultScanner scanner = hTable.getScanner(Bytes.toBytes("f1"), Bytes.toBytes("c1"));  //指定的列
		for (Result result : scanner) {  //迭代出每一条记录
//			System.out.println(result);
			byte[] row = result.getRow();
			byte[] value = result.getValue(Bytes.toBytes("cf"), Bytes.toBytes("date"));
			System.out.println("rowkey -> "+Bytes.toString(row)+", cf:date -> "+Bytes.toString(value));
		}
		
		hTable.close();  //关闭hbase连接
	}

	private static void dml() throws IOException {
		HTable hTable = new HTable(getConf(), TABLE_NAME);
		Put put = new Put(Bytes.toBytes(ROW_KEY));  //创建指定行键的put对象
		put.add(Bytes.toBytes("f2"), Bytes.toBytes("c2"), Bytes.toBytes("钱六"));  //确定记录的数据
		hTable.put(put);  //插入数据
		
		Get get = new Get(Bytes.toBytes(ROW_KEY));  
		Result result = hTable.get(get);  //获取指定行键的记录数据
		Object valueString = Bytes.toString(result.getValue(Bytes.toBytes("f1"), Bytes.toBytes("c1"))); //获取该记录的值
		System.out.println(result);
		System.out.println(valueString);
		
//		Delete delete = new Delete(Bytes.toBytes(ROW_KEY));  
//		hTable.delete(delete);  //删除指定行键的记录
		
		hTable.close();  //关闭hbase连接
	}
	
	//封装创建表的操作
	public static void ddl(String tableName,String... familyNames) throws Exception{
		Configuration conf = getConf();  //获取hbase的配置文件
		HBaseAdmin hBaseAdmin = new HBaseAdmin(conf);
		
		//创建表
		HTableDescriptor htableDesc = new HTableDescriptor(tableName);
		for (String familyName : familyNames) {
			HColumnDescriptor family = new HColumnDescriptor(familyName);
			htableDesc.addFamily(family);			
		}
		if (!hBaseAdmin.tableExists(tableName)) {
			hBaseAdmin.createTable(htableDesc);  //一般为逆推，先要createTable，此时需要HTableDescriptor，再创建，需要添加列簇再创建
		}
		//创建完表后检查是否存在该表
		System.out.println("表是否存在:"+hBaseAdmin.tableExists(tableName));
//		hBaseAdmin.disableTable(tableName); //失效表
//		hBaseAdmin.deleteTable(tableName);  //删除表
		
		hBaseAdmin.close();  //关闭hbase连接
		
	}

	private static Configuration getConf() {
		Configuration conf = HBaseConfiguration.create();
		conf.setStrings("hbase.zookeeper.quorum", "hadoop0");
		return conf;
	}

}
