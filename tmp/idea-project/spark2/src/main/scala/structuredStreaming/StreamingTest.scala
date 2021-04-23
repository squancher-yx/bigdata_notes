package structuredStreaming

import org.apache.spark.sql.{SparkSession, functions}
import org.apache.spark.sql.streaming.Trigger.ProcessingTime

object StreamingTest {
  def main(args: Array[String]): Unit = {
    val spark: SparkSession = SparkSession.builder().master("local[*]").appName("streaming")
    .getOrCreate()
    import spark.implicits._
    val source = spark
      .readStream
      .text("D:\\bak\\bigdata_notes\\tmp\\idea-project\\spark2\\src\\main\\resources\\")
      .as[String].flatMap(_.split(" ")).map(T1(_,1)).toDF()


    val tmp = source.writeStream.outputMode("append")
      .format("console")
      .trigger(ProcessingTime("10 seconds"))
      .start()
    tmp.awaitTermination()
  }
  case class T1(value:String,num:Int)

}
