package hudi

import org.apache.hudi.{DataSourceReadOptions, DataSourceWriteOptions}
import org.apache.hudi.common.table.HoodieTableMetaClient
import org.apache.spark.sql.{DataFrame, ForeachWriter, Row, SparkSession}
import org.apache.hudi.common.model.HoodieTableType.{COPY_ON_WRITE, MERGE_ON_READ}
import org.apache.spark.TaskContext
import org.apache.spark.sql.streaming.Trigger.ProcessingTime

object SparkReadHudi {
  def main(args: Array[String]): Unit = {
    normalRead()
//    structuredStreamingRead()
  }

  /**
   * 需要 spark3 或 spark2.4.3 以上，hudi 0.8 以上
   */
  def structuredStreamingRead(): Unit = {
    val ss = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
      .master("local[*]")
      .getOrCreate()
    val tmp = ss.readStream
      .format("org.apache.hudi")
      .load("file:///D:/bak/bigdata_notes/tmp/idea-project/spark2/src/main/hudi_data/")
      .writeStream.foreachBatch{
      (batchDF: DataFrame, batchId: Long) =>
        println("batchID:" + batchId)
//        batchDF.show(100)
//        batchDF.repartition(3).foreach { f =>
////          println(Thread.currentThread().getName+"    "+f+"    "+ TaskContext.getPartitionId())
//          //            println(f)
//        }
        println(batchDF.count())
    }
      .trigger(ProcessingTime("10 seconds"))
      .start()
    tmp.awaitTermination()
  }

  def normalRead() {
    val ss = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
      .master("local[*]")
      .getOrCreate()
    val tmp = ss.read
      .format("hudi")
      //默认快照查询(最新)
//      .option(DataSourceReadOptions.QUERY_TYPE_OPT_KEY, DataSourceReadOptions.QUERY_TYPE_INCREMENTAL_OPT_VAL)
//      .option(DataSourceReadOptions.BEGIN_INSTANTTIME_OPT_KEY, 20210521113319L)
      .load("file:///D:/bak/bigdata_notes/tmp/idea-project/spark2/src/main/hudi_data/*/*/*")
//      .show(100)
    println(tmp.count())
  }
}
