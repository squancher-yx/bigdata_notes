package hudi

import org.apache.hudi.DataSourceWriteOptions.{PARTITIONPATH_FIELD_OPT_KEY, PRECOMBINE_FIELD_OPT_KEY, RECORDKEY_FIELD_OPT_KEY}
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TABLE_NAME
import org.apache.spark.sql.SaveMode.Append
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger.ProcessingTime
import org.apache.spark.sql.types.{DataTypes, StructType}
import org.apache.spark.sql.{DataFrame, Row, RowFactory, SparkSession}

import scala.collection.mutable.ArrayBuffer

object KafkaToHudi {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "127.0.0.1:9092")
      .option("subscribe", "quickstart-events")
      //groupIdPrefix、kafka.group.id spark3 可用
      //      .option("groupIdPrefix", "quickstart-events-group")
      .load()

    val sourceColumn = Array("value", "offset", "partition")
    val valueColumns = Array("gate", "pdate", "ftime", "server", "logname", "title", "stats_col")
    val test = new ArrayBuffer[ColumnInfo]()



    df.selectExpr("CAST(value AS STRING)", "CAST(offset AS STRING)", "CAST(partition AS STRING)")
      .as[(String, String, String)]
      .mapPartitions(f => new MapColumns(f, test))(RowEncoder.apply(createSchema(test)))
      .toDF(sourceColumn: _*)
      .writeStream
      .outputMode("append")
      .option("checkpointLocation", "D:\\bak\\bigdata_notes\\tmp\\idea-project\\spark2\\src\\main\\checkpoint")
      .foreachBatch((batchDF: DataFrame, batchId: Long) => {
        //        batchDF
        //          .write
        //          .format("hudi")
        //          .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
        //          .options(getQuickstartWriteConfigs)
        //          // key 为分区唯一RECORDKEY_FIELD_OPT_KEY
        //          .option(RECORDKEY_FIELD_OPT_KEY, "id")
        //          .option(TABLE_NAME, "test")
        //          .option(PARTITIONPATH_FIELD_OPT_KEY, "path")
        //          .option("hoodie.index.type", "INMEMORY")
        //          //        .option("hoodie.keep.max.commits", "20")
        //          //        .option("hoodie.keep.min.commits", "11")
        //          //        .option("hoodie.cleaner.commits.retained", "2")
        //          .mode(Append)
        //          .save("D:\\bak\\bigdata_notes\\tmp\\idea-project\\spark2\\src\\main\\hudi_data")
        batchDF.show()
      })
      .trigger(ProcessingTime("10 seconds"))
      .start().awaitTermination()

  }

  def createSchema(columnInfo: ArrayBuffer[ColumnInfo]): StructType = {
    var customStructType: StructType = new StructType
    for (i <- columnInfo) {
      customStructType = customStructType.add(i.columnName, matchType(i.columnType), true)
    }
    customStructType
  }

  def matchType(columntype: String) = columntype match {
    case "Long" => DataTypes.LongType
    case "BooleanType" => DataTypes.BooleanType
    case "DoubleType" => DataTypes.DoubleType
    case "StringType" => DataTypes.StringType
    case "IntegerType" => DataTypes.IntegerType
    case "TimestampType" => DataTypes.TimestampType
    case "FloatType" => DataTypes.FloatType
  }
}

case class ColumnInfo(columnName: String, columnType: String, columnIndex: Int)

class MapColumns(iter: Iterator[(String, String, String)], columnInfo: ArrayBuffer[ColumnInfo]) extends Iterator[Row] {

  override def hasNext: Boolean = {
    iter.hasNext
  }

  override def next(): Row = {
    val row = iter.next
    val value = row._1.split("\t", -1)
    val offset = row._2
    val partition = row._3
    Row()
  }
}