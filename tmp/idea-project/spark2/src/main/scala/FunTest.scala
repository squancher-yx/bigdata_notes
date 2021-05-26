import java.util

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.api.java.UDF1
import org.apache.spark.{SparkConf, TaskContext}
import org.apache.spark.sql.{Encoders, Row, RowFactory, SparkSession}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.spark.sql.functions._
import org.apache.spark.sql.types.{IntegerType, StringType}

import scala.collection.JavaConverters

object FunTest {
  def main(args: Array[String]): Unit = {
    val f = FileSystem.get(new Configuration())
    val spark = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._
    val list: java.util.ArrayList[(String, String)] = new util.ArrayList[(String, String)]()
    for (i <- 1000000 until 5000000) {
      list.add((i + "qweqwe0wetrwertert0erterter0ertert", i + "qwe"))
    }
    val seq = JavaConverters.asScalaIteratorConverter(list.iterator()).asScala.toSeq
    val time = System.currentTimeMillis()
    var df = spark.sparkContext.parallelize(seq).toDF("a", "b")


    val tmp = udf(udfTest _)
    val tmp2 = udf(udfTest2 _)
//    udf((a:String,b:String)=>{})
    //    val b = tmp
    df = df.withColumn("d", tmp(col("a"), lit(2)))
    df = df.withColumn("d", column("q"))
    df = df.withColumn("d", udf(new UDF1[String,String] {
      override def call(t1: String): String = t1
    },StringType).apply(col("a")))
    df = df.withColumn("b", tmp2(col("a"), lit(1)))
    df.withColumn("c", tmp2(col("a"), lit(1))).show()
//        df.mapPartitions(f=>new MapColumns(f))(Encoders.kryo(classOf[Row])).show()


    val time2 = System.currentTimeMillis()
    println(time2 - time)
    //    df.withColumn("a", udf(udfTest(""),StringType)).show()
  }

  def udfTest(input: String, index: String): String = {
    //    println(1)
    val len = input.split("0").length
    input + "_end"
  }

  def udfTest2(input: String, index: String): String = {
    //    println(2)
    val len = input.split("0").length
    input + "_end2"
  }
}

class MapColumns(iter: Iterator[Row]) extends Iterator[Row] {


  override def hasNext: Boolean = {
    iter.hasNext
  }

  override def next(): Row = {
    val row = iter.next
    val value = row.getString(1).split("0", -1)
    //    val offset = row.getString(2)
    //    val partition = row.getString(3)
    //    udf()
    RowFactory.create(value: _*)
  }
}
