import java.util

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

object FunTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
      .master("local[*]")
      .getOrCreate()

    val hoodieCfg = HoodieWriteConfig.newBuilder()
      .withPath("D:\\bak\\bigdata_notes\\tmp\\idea-project\\spark2\\src\\main\\hudi_data")
      .withAutoCommit(false)
      .build()
    val client = new SparkRDDWriteClient(new HoodieSparkEngineContext(spark.sparkContext), hoodieCfg, true)
    val cleanInstant = HoodieActiveTimeline.createNewInstantTime(1621997154000L)
    client.startCommitWithTime(cleanInstant, HoodieTimeline.REPLACE_COMMIT_ACTION)
    val writeResult = client.deletePartitions(util.Arrays.asList("a"),cleanInstant)
    client.commit(cleanInstant,writeResult.getWriteStatuses)

  }

}
