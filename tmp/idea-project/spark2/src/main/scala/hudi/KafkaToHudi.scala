package hudi


import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet, SQLException}
import java.text.SimpleDateFormat
import java.time.format.DateTimeFormatter
import java.time.{LocalDateTime, ZoneId}
import java.util
import java.util.{Date, HashMap, Map, TimeZone, UUID}

import org.apache.hudi.DataSourceWriteOptions.{PARTITIONPATH_FIELD_OPT_KEY, PRECOMBINE_FIELD_OPT_KEY, RECORDKEY_FIELD_OPT_KEY, STORAGE_TYPE_OPT_KEY, TABLE_TYPE_OPT_KEY}
import org.apache.hudi.QuickstartUtils.getQuickstartWriteConfigs
import org.apache.hudi.config.HoodieWriteConfig.TABLE_NAME
import org.apache.hudi.table.HoodieTable
import org.apache.spark.sql.SaveMode.Append
import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.functions._
import org.apache.spark.sql.streaming.{StreamingQuery, StreamingQueryListener}
import org.apache.spark.sql.streaming.Trigger.ProcessingTime
import org.apache.spark.sql.types.{DataType, DataTypes, StructType}
import org.apache.spark.sql.{DataFrame, Row, RowFactory, SparkSession}

import scala.collection.mutable.ArrayBuffer

//import org.apache.hudi.OverwriteWithLatestAvroPayload

/**
 * spark 2.4.7
 * hudi 0.8.0
 */
//hudi.KafkaToHudi
object KafkaToHudi {
  def main(args: Array[String]): Unit = {
    System.setProperty("HADOOP_USER_NAME", "hadoop")
    val spark = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi write test")
      .master("local[*]")
      .getOrCreate()

    import spark.implicits._

    val df = spark
      .readStream
      .format("kafka")
//            .option("kafka.bootstrap.servers", "node239:9092")
      .option("kafka.bootstrap.servers", "127.0.0.1:9092")
      .option("subscribe", "hudi-test")
//            .option("startingOffsets", """{"hudi-test":{"0":0,"1":0}}""")
      // groupIdPrefix、kafka.group.id spark3 可用
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

    spark.streams.addListener(new StreamingQueryListener() {
      val df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      val pattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
      df.setTimeZone(TimeZone.getTimeZone("UTC"))

      override def onQueryStarted(event: StreamingQueryListener.QueryStartedEvent): Unit = {
        println("Query started: " + event.id)
      }

      override def onQueryProgress(event: StreamingQueryListener.QueryProgressEvent): Unit = {
        val info = event.progress
        val batchID = info.batchId.toString
        val durationMs = info.durationMs
        val timestamp = info.timestamp
        val sources = info.sources
        var inputRowsPerSecond = info.inputRowsPerSecond.toString
        if (inputRowsPerSecond.equals("NaN")) {
          inputRowsPerSecond = "0"
        }
        val processedRowsPerSecond = info.processedRowsPerSecond.toString
        val numInputRows = info.numInputRows.toString
        val addBatch = durationMs.getOrDefault("addBatch", -1).toString
        val getBatch = durationMs.getOrDefault("getBatch", -1).toString
        val getEndOffset = durationMs.getOrDefault("getEndOffset", -1).toString
        val queryPlanning = durationMs.getOrDefault("queryPlanning", -1).toString
        val setOffsetRange = durationMs.getOrDefault("setOffsetRange", -1).toString
        val triggerExecution = durationMs.getOrDefault("triggerExecution", -1).toString
        val walCommit = durationMs.getOrDefault("walCommit", -1).toString
        println("batchID:" + batchID)

        val date2 = df.parse(timestamp)
        val localDateTime4 = LocalDateTime.ofInstant(date2.toInstant, ZoneId.systemDefault)
        val timestampFix = localDateTime4.format(pattern)

        println("inputRowsPerSecond:" + inputRowsPerSecond)
        println("processedRowsPerSecond:" + processedRowsPerSecond)
        println("numInputRows:" + numInputRows)
        println("addBatch:" + addBatch)
        println("getBatch:" + getBatch)
        println("getEndOffset:" + getEndOffset)
        println("queryPlanning:" + queryPlanning)
        println("setOffsetRange:" + setOffsetRange)
        println("triggerExecution:" + triggerExecution)
        println("walCommit:" + walCommit)
        println("startOffset"+sources(0).startOffset)
        println("endOffset"+sources(0).endOffset)

        val mysql = new InsertMetrics()
        val values = Array(batchID, inputRowsPerSecond, processedRowsPerSecond, numInputRows, addBatch, getBatch, getEndOffset, queryPlanning, setOffsetRange, triggerExecution, walCommit)
        println(timestampFix)
        mysql.spark(values, timestampFix)
        //        println("Query made progress: " + event.progress)
      }

      override def onQueryTerminated(event: StreamingQueryListener.QueryTerminatedEvent): Unit = {
        println("Query terminated: " + event.id)
      }
    })
    val query: StreamingQuery = df.selectExpr("CAST(value AS STRING)", "CAST(offset AS STRING)", "CAST(partition AS STRING)")
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
//            .option("checkpointLocation", "hdfs://masters/tmp/spark_to_hudi_checkpoint")
      .option("checkpointLocation", "D:\\tmp\\spark_checkpoint")
      .foreachBatch((batchDF: DataFrame, batchId: Long) => {
//        batchDF.persist()
        batchDF
          .write
          .format("hudi")
          .option(TABLE_NAME, "KafkaSource")
          //          .option(TABLE_TYPE_OPT_KEY, "COPY_ON_WRITE")
          .option(TABLE_TYPE_OPT_KEY, "MERGE_ON_READ")
          .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
          .option("hoodie.insert.shuffle.parallelism", "200")
          .option("hoodie.upsert.shuffle.parallelism", "200")
          // key 为分区唯一RECORDKEY_FIELD_OPT_KEY
          .option(RECORDKEY_FIELD_OPT_KEY, "uuid")
          .option(PARTITIONPATH_FIELD_OPT_KEY, "path")


          .option("hoodie.metrics.on", "true")
          .option("hoodie.metrics.reporter.class", "hudi.Metrics")

          /**
           * 120M -> 80700000B
           * 60M -> 17450000B？  52600000B
           */
          .option("hoodie.parquet.max.file.size", 120 * 1024 * 1024)
          .option("hoodie.parquet.block.size", 120 * 1024 * 1024)
          //           .option("hoodie.parquet.small.file.limit", 1*1024*1024+"")
          .option("hoodie.datasource.write.operation", "insert")
          .option("hoodie.clean.async", "true")
          .option("hoodie.clean.automatic", "true")
          //           .option("hoodie.compaction.target.io", 1*1024*1024)

          // 0.7.0 local只支持 INMEMORY
//                     .option("hoodie.index.type", "INMEMORY")
          .option("hoodie.index.type", "BLOOM")

          // 控制 .hoodie 目录文件
          .option("hoodie.keep.max.commits", "35")
          .option("hoodie.keep.min.commits", "20")

          /**
           * 控制分区下的文件（未达到指定大小时滚动中的文件）
           * 使用 BLOOM 索引时
           * 本地测试为控制分区目录下的滚动文件，无需开启 hoodie.compact.inline。
           * 集群模式下需要打开 hoodie.compact.inline
           * hoodie.compact.inline.max.delta.commits 控制 log 文件的数量（需要看索引类型，如 BLOOM 无 log文件）
           *
           */

          .option("hoodie.cleaner.commits.retained", "1") // n+2 ?
          .option("hoodie.parquet.compression.codec", "gzip")
          .option("hoodie.compact.inline", "true")
          .option("hoodie.datasource.compaction.async.enable", "true")
//          .option("hoodie.logfile.to.parquet.compression.ratio", "0.3")
//          .option("hoodie.compact.inline.max.delta.commits", "2")
//          .option("hoodie.compaction.daybased.target", "10")
          .option("hoodie.parquet.small.file.limit", 100 * 1024 * 1024)
//          .option("hoodie.parquet.small.file.limit", "100857600")
          // 通过每条数据大小计算小文件需要插入多少数据，如果不指定会以最近提交的动态计算（如果不配置且单个批次数据量过大，可能会有很多小文件）
          .option("hoodie.copyonwrite.record.size.estimate", "80")
          //          .option("hoodie.logfile.max.size", "1073741824")
          .mode(Append)
          .save("D:\\tmp\\hudi_mor_table")
//                  .save("hdfs://masters/tmp/hudi_mor_table")
//        println("batchID:" + batchId + ",batchCount:" + batchDF.count())
//        batchDF.unpersist()
      })
      .trigger(ProcessingTime("300 seconds"))
      .start()
    query.awaitTermination()
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

  def Test(): Unit = {
    val url = "jdbc:mysql://192.168.121.128:3306/test"
    //    private String url = "jdbc:mysql://192.168.121.128:3306/test?rewriteBatchedStatements=true&useSSL=false";
    val user = "root"
    val password = "123321"
    var conn: Connection = null
    var pstm: PreparedStatement = null

    try {
      Class.forName("com.mysql.jdbc.Driver")
      conn = DriverManager.getConnection(url, user, password)
      val sql = "INSERT INTO test5 values(?,?,?)"
      pstm = conn.prepareStatement(sql)
      val startTime = System.currentTimeMillis
      for (i <- 1 to 100000) {
        pstm.setInt(1, i)
        pstm.setInt(2, i)
        pstm.setInt(3, i)
        pstm.executeUpdate
      }
      val endTime = System.currentTimeMillis
      System.out.println("用时：" + (endTime - startTime))
    } catch {
      case e: Exception =>
        e.printStackTrace()
        throw new RuntimeException(e)
    } finally {
      if (pstm != null) try pstm.close()
      catch {
        case e: SQLException =>
          e.printStackTrace()
          throw new RuntimeException(e)
      }
      if (conn != null) try conn.close()
      catch {
        case e: SQLException =>
          e.printStackTrace()
          throw new RuntimeException(e)
      }
    }
  }
}
