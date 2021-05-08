package hudi

import org.apache.spark.sql.SparkSession

object SparkRead {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
      .master("local[*]")
      .getOrCreate()
    ss.read.format("org.apache.hudi")
      .load("file:///D:/bak/bigdata_notes/tmp/idea-project/spark2/src/main/hudi_data/*").rdd.foreach(f=>{println(f)})
  }

}
