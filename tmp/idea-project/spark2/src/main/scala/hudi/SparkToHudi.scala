package hudi

import org.apache.hudi.DataSourceWriteOptions
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.hudi.QuickstartUtils._
import scala.collection.JavaConversions._
import org.apache.spark.sql.SaveMode._
import org.apache.hudi.DataSourceReadOptions._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig._


object SparkToHudi {
  def main(args: Array[String]): Unit = {
    println(System.getenv("HADOOP_HOME"))
    val ss = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
      .master("local[*]")
      .getOrCreate()
    import ss.implicits._

    val source = ss.createDataset(List((1,"qq",1,"a"),(2,"ww",5,"b"))).toDF("id","name","ts","path")
    source.write.format("hudi")
      .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
      .options(getQuickstartWriteConfigs)
      .option(RECORDKEY_FIELD_OPT_KEY, "id")
      .option(TABLE_NAME, "test")
      .option(PARTITIONPATH_FIELD_OPT_KEY, "path")
      .mode(Overwrite)
      .save("D:\\bak\\bigdata_notes\\tmp\\idea-project\\spark2\\src\\main\\hudi_data")

//    val tableName = "hudi_trips_cow"
//    val basePath = "D:\\bak\\bigdata_notes\\tmp\\idea-project\\spark2\\src\\main\\hudi_data"
//    val dataGen = new DataGenerator
//    val inserts = convertToStringList(dataGen.generateInserts(10))
//    val df = ss.read.json(ss.sparkContext.parallelize(inserts, 2))
//    df.show()
//    df.write.format("hudi").
//      options(getQuickstartWriteConfigs).
//      option(PRECOMBINE_FIELD_OPT_KEY, "ts").
//      option(RECORDKEY_FIELD_OPT_KEY, "uuid").
//      option(PARTITIONPATH_FIELD_OPT_KEY, "partitionpath").
//      option(TABLE_NAME, tableName).
//      mode(Overwrite).
//      save(basePath)
  }
}

