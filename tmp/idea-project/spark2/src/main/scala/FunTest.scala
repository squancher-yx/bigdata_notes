import java.text.SimpleDateFormat
import java.util
import java.util.stream.Collectors
import java.util.{Date, Random}

import org.apache.hadoop.conf.Configuration
import org.apache.hadoop.fs.FileSystem
import org.apache.hudi.client.SparkRDDWriteClient
import org.apache.hudi.client.common.HoodieSparkEngineContext
import org.apache.hudi.common.table.timeline.{HoodieActiveTimeline, HoodieTimeline}
import org.apache.hudi.config.HoodieWriteConfig
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
import org.apache.hudi.index.HoodieIndex
object FunTest {
  def main(args: Array[String]): Unit = {
//    Collectors
    import java.util
    val list = util.Arrays.asList("aaaa", "bbbb", "cccc")

    //静态方法语法	ClassName::methodName
  }

}
