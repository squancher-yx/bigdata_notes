package hudi


import java.text.SimpleDateFormat
import java.util.UUID

import org.apache.hudi.DataSourceWriteOptions.{PARTITIONPATH_FIELD_OPT_KEY, PRECOMBINE_FIELD_OPT_KEY, RECORDKEY_FIELD_OPT_KEY}
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TABLE_NAME
import org.apache.spark.sql.SaveMode.Append
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.Trigger.ProcessingTime
import org.apache.spark.sql.types.{DataType, DataTypes, StructType}
import org.apache.spark.sql.{DataFrame, Row, RowFactory, SparkSession}

import scala.collection.mutable.ArrayBuffer

/**
 * spark 2.4.7
 * hudi 0.8.0
 */
//hudi.KafkaToHudi
object KafkaToHudi {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi write test")
      //      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
      .option("kafka.bootstrap.servers", "node239:9092")
      .option("subscribe", "hudi-test")
      //groupIdPrefix、kafka.group.id spark3 可用
      //      .option("groupIdPrefix", "quickstart-events-group")
      .load()

    val sourceColumn = Array("value", "offset", "partition")
    val columns = createTestMetaData()
    val columnsLength = columns.length

    val pdateUdf = udf((x: String) => {
      var pdate = 99999999
      try {
        if (x.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}:\\d{2}"))
          pdate = x.split(" ")(0).replace("-", "").toInt
        else if (x.matches("dddddddd"))
          pdate = x.toInt
      } catch {
        case e: Exception => e.printStackTrace()
      }
      pdate
    })

    val gateUdf = udf((x: String) => {
      var gate = "unknow"
      try {
        if (x.contains("_"))
          gate = x.split("_")(2)
        else if (x.matches("([a-z]|[A-Z]|[0-9])+"))
          gate = x
      } catch {
        case e: Exception => e.printStackTrace()
      }
      gate
    })

    val uuidUdf = udf(() => {
      UUID.randomUUID().toString
    })

    df.selectExpr("CAST(value AS STRING)", "CAST(offset AS STRING)", "CAST(partition AS STRING)")
      .as[(String, String, String)]
      .filter(f => {
        val value = f._1
        //        println(f)
        //        println(value.split("\t", -1).length == 14 && value.contains("d_active"))
        value.split("\t", -1).length == 14 && value.contains("d_active")
      })
      .mapPartitions(f => new MapColumns(f, columns))(RowEncoder.apply(createSchema(columns)))
      .withColumn("pdate", pdateUdf.apply(column("ftime")))
      .withColumn("gate", gateUdf.apply(column("logname")))
      .withColumn("path", concat_ws("/", column("pdate"), column("gate")))
      // 使用当前时间为时间长
      .withColumn("ts", unix_timestamp())
      // 每条都不重复
      .withColumn("uuid", uuidUdf.apply())
      .writeStream
      .outputMode("append")
      .option("checkpointLocation", "hdfs://10.17.64.238:9000/tmp/spark_to_hudi_checkpoint")
      .foreachBatch((batchDF: DataFrame, batchId: Long) => {
        batchDF.persist()
        batchDF
          .write
          .format("hudi")
          .option(TABLE_NAME, "KafkaSource")
          .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
          .options(getQuickstartWriteConfigs)
          // key 为分区唯一RECORDKEY_FIELD_OPT_KEY
          .option(RECORDKEY_FIELD_OPT_KEY, "uuid")
          .option(PARTITIONPATH_FIELD_OPT_KEY, "path")
          // local只支持 INMEMORY
          //          .option("hoodie.index.type", "INMEMORY")
          .option("hoodie.index.type", "BLOOM")
          .option("hoodie.keep.max.commits", "20")
          .option("hoodie.keep.min.commits", "11")
          .option("hoodie.cleaner.commits.retained", "2")
          .mode(Append)
          .save("hdfs://10.17.64.238:9000/tmp/hudi_test_table")
        println("batchID:" + batchId + ",batchCount:" + batchDF.count())
        batchDF.unpersist()
      })
      .trigger(ProcessingTime("60 seconds"))
      .start().awaitTermination()

  }

  def createSchema(columnInfo: ArrayBuffer[ColumnInfo]): StructType = {
    var customStructType: StructType = new StructType
    customStructType = customStructType.add("offset", matchSchemaType("String"), nullable = true)
    customStructType = customStructType.add("partition", matchSchemaType("String"), nullable = true)
    for (i <- columnInfo) {
      customStructType = customStructType.add(i.columnName, matchSchemaType(i.columnType), nullable = true)
    }
    customStructType
  }

  def matchSchemaType(columntype: String): DataType = columntype match {
    case "Long" => DataTypes.LongType
    case "Boolean" => DataTypes.BooleanType
    case "Double" => DataTypes.DoubleType
    case "Integer" => DataTypes.IntegerType
    case "Int" => DataTypes.IntegerType
    case "Timestamp" => DataTypes.TimestampType
    case "Float" => DataTypes.FloatType
    // 暂时使用 String
    case "Map" => DataTypes.createMapType(DataTypes.StringType, DataTypes.StringType)
    case _ => DataTypes.StringType
  }


  def createTestMetaData(): ArrayBuffer[ColumnInfo] = {
    val columns = new ArrayBuffer[ColumnInfo]
    //    columns += ColumnInfo("gate", "String", 2)
    //    columns += ColumnInfo("pdate", "Int", 0)
    columns += ColumnInfo("ftime", "String", 0)
    columns += ColumnInfo("logname", "String", 2)
    columns += ColumnInfo("server", "String", 1)
    columns += ColumnInfo("title", "Int", 3)
    columns += ColumnInfo("info_version", "Int", 4)
    columns += ColumnInfo("uid", "Long", 5)
    columns += ColumnInfo("role_id", "Long", 6)
    columns += ColumnInfo("stats_obj", "Int", 7)
    columns += ColumnInfo("login_platform", "String", 8)
    columns += ColumnInfo("ip", "String", 9)
    columns += ColumnInfo("stats_location", "String", 10)
    columns += ColumnInfo("stats_rel", "String", 11)
    columns += ColumnInfo("opt_result", "String", 12)
    columns += ColumnInfo("stats_col", "String", 13)

  }
}

case class ColumnInfo(columnName: String, columnType: String, columnIndex: Int)

class MapColumns(iter: Iterator[(String, String, String)], columnInfo: ArrayBuffer[ColumnInfo]) extends Iterator[Row] {
  val dateFormat = new SimpleDateFormat("yyyyMMdd")

  override def hasNext: Boolean = {
    iter.hasNext
  }

  override def next(): Row = {
    val row = iter.next
    val value = row._1.split("\t", -1)
    val offset = row._2
    val partition = row._3
    val res = new ArrayBuffer[Any]
    res += offset
    res += partition
    if (value.length == 14) {
      for (i <- columnInfo) {
        val index = i.columnIndex
        i.columnName match {
          case _ => i.columnType match {
            case "Long" => res += value(index).toLong
            case "Int" => res += value(index).toInt
            case "Double" => res += value(index).toDouble
            case "Integer" => res += value(index).toInt
            // 暂不转换此类型
            case "Timestamp" => DataTypes.TimestampType
            case "Float" => res += value(index).toFloat
            // 暂时使用 String
            case "Map" => DataTypes.createMapType(DataTypes.StringType, DataTypes.StringType)
            case _ => res += value(index)
          }
        }
      }
    }
    Row(res: _*)
  }
}

