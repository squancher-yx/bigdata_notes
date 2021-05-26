package hudi

import java.util
import java.util.{HashMap, Map}

import org.apache.hudi.DataSourceWriteOptions
import org.apache.hudi.config.HoodieWriteConfig
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.hudi.QuickstartUtils._

import scala.collection.JavaConversions._
import org.apache.spark.sql.SaveMode._
import org.apache.hudi.DataSourceReadOptions._
import org.apache.hudi.DataSourceWriteOptions._
import org.apache.hudi.config.HoodieWriteConfig._
import org.apache.spark.sql.execution.datasources.DataSourceUtils
//PARTITIONING_COLUMNS_KEY

object SparkToHudi {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
      .master("local[*]")
      .getOrCreate()
    import ss.implicits._

    val demoConfigs: util.Map[String, String] = new util.HashMap[String, String]
    demoConfigs.put("hoodie.insert.shuffle.parallelism", "10")
    demoConfigs.put("hoodie.upsert.shuffle.parallelism", "10")
    for(i <- 0 until 100){
      val source = ss.createDataset(List((i,"qq",7,"a","qq"),(i+1,"ww",7,"a","ee"))).toDF("id","name","ts","path","new_column")
      source.write.format("hudi")
        .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
        .options(demoConfigs)
        // key 为分区唯一RECORDKEY_FIELD_OPT_KEY
        .option(RECORDKEY_FIELD_OPT_KEY, "id")
        .option(TABLE_NAME, "test")
        .option(PARTITIONPATH_FIELD_OPT_KEY, "path")
        .option("hoodie.index.type", "INMEMORY")
        // .hoodie目录中的小文件数量，不一定准确
        .option("hoodie.keep.max.commits", "20")
        .option("hoodie.keep.min.commits", "10")
        // 保留的提交次数，分区下的 parquet 文件数量
        .option("hoodie.cleaner.commits.retained", "4")
        .mode(Append)
        .save("D:\\bak\\bigdata_notes\\tmp\\idea-project\\spark2\\src\\main\\hudi_data")
      println("done:" + i)
    }
import org.apache.hudi.metrics

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

