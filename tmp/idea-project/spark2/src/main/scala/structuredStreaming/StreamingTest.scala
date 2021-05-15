package structuredStreaming

import java.sql.Timestamp
import java.text.SimpleDateFormat

import org.apache.spark.sql.functions.window
import org.apache.spark.sql.{ForeachWriter, Row, SparkSession, functions}
import org.apache.spark.sql.streaming.Trigger.ProcessingTime
//如果想要使用更多的内置函数，请引入：
import org.apache.spark.sql.functions._

object StreamingTest {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[*]").appName("streaming")
      .getOrCreate()
    import spark.implicits._

    var sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    //println(sdf.parse("2018-12-01 12:00:02").getTime)

    val source = spark.readStream
      .format("socket")
      .option("host", "127.0.0.1")
      .option("port", 8888)
      .load()
      .as[String]
      .map(row => {
        val line = row.split(",")
        (new Timestamp(sdf.parse(line(0)).getTime), line(1))
      }).toDF("ts", "key")
      .withWatermark("ts", "100 seconds")
      .groupBy(
        window($"ts", "30 seconds", "30 seconds")
        , $"key"
      ).count()

    source.

    val tmp = source.repartition(1).writeStream.outputMode("update")
      .foreach(new ForeachWriter[Row] {
        override def open(partitionId: Long, epochId: Long): Boolean = {
          println("open")
          //返回 true 将调用 process
          true
        }

        override def process(value: Row): Unit = {
          println("value:" + value)
        }

        override def close(errorOrNull: Throwable): Unit = {
          //只要 open 成功将调用 close，除非 JVM 或 Python 进程在中间崩溃。
        }
      })
      //      .format("console")
      .trigger(ProcessingTime("10 seconds"))
      .start()


    tmp.awaitTermination()
  }

  case class T1(value: String, num: Int)

}
