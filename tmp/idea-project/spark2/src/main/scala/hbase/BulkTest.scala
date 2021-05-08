package hbase

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.hbase.TableName
import org.apache.hadoop.hbase.util.Bytes
import org.apache.spark.sql.SparkSession
//import org.apache.hadoop.hbase.spark.HBaseRDDFunctions._
//import org.apache.hadoop.hbase.spark.{HBaseContext, KeyFamilyQualifier}

object BulkTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder()
      .appName("test")
      .master("local[*]")
      .getOrCreate()
    val sc = spark.sparkContext
    val rdd = sc.parallelize(Array(
      (Bytes.toBytes("1"),
        (Bytes.toBytes(""), Bytes.toBytes("a"), Bytes.toBytes("foo1"))),
      (Bytes.toBytes("3"),
        (Bytes.toBytes(""), Bytes.toBytes("b"), Bytes.toBytes("foo2.b")))));
    val config = new Configuration()
//    val hbaseContext = new HBaseContext(sc, config)
//    hbaseContext.bulkLoad()
  }

}
