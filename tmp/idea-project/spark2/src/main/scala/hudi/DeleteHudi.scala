package hudi

import java.io.IOException
import java.util

import org.apache.hudi.DataSourceReadOptions
import org.apache.hudi.DataSourceWriteOptions.{OPERATION_OPT_KEY, PARTITIONPATH_FIELD_OPT_KEY, PRECOMBINE_FIELD_OPT_KEY, RECORDKEY_FIELD_OPT_KEY, TABLE_TYPE_OPT_KEY}
import org.apache.hudi.config.HoodieWriteConfig.TABLE_NAME
import org.apache.hudi.exception.HoodieCommitException
import org.apache.spark.sql.SaveMode.{Append, Overwrite}
import org.apache.spark.sql.SparkSession

object DeleteHudi {
  def main(args: Array[String]): Unit = {
    val ss = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("hudi test")
//      .master("local[*]")
      .getOrCreate()
    import ss.implicits._
    deleteREcords(ss)

  }

  def deleteREcords(ss: SparkSession): Unit = {
    val demoConfigs: util.Map[String, String] = new util.HashMap[String, String]
    demoConfigs.put("hoodie.insert.shuffle.parallelism", "10")
    demoConfigs.put("hoodie.upsert.shuffle.parallelism", "10")
    val source = ss.read
      .format("hudi")
      //默认快照查询(最新)
      //      .option(DataSourceReadOptions.QUERY_TYPE_OPT_KEY, DataSourceReadOptions.QUERY_TYPE_INCREMENTAL_OPT_VAL)
      //      .option(DataSourceReadOptions.BEGIN_INSTANTTIME_OPT_KEY, "20210603160721")
      //      .option(DataSourceReadOptions.END_INSTANTTIME_OPT_KEY, 20210703160721L)
      .load("hdfs://10.17.64.238:9000/tmp/hudi_mor_table/20210607/mx/*")
    source.show(30)

    source.write.format("hudi")
      .option(PRECOMBINE_FIELD_OPT_KEY, "ts")
      .options(demoConfigs)
      // key 为分区唯一RECORDKEY_FIELD_OPT_KEY
      .option(RECORDKEY_FIELD_OPT_KEY, "uuid")
      .option(TABLE_NAME, "KafkaSource")
      .option(PARTITIONPATH_FIELD_OPT_KEY, "path")
      .option(TABLE_TYPE_OPT_KEY, "MERGE_ON_READ")
      .option("hoodie.index.type", "BLOOM")
      .option(OPERATION_OPT_KEY, "delete")
      // .hoodie目录中的小文件数量，不一定准确（看规律可能是每种后缀文件分别最小数量）
      .option("hoodie.keep.max.commits", "6")
      .option("hoodie.keep.min.commits", "3")
      // 保留的提交次数，分区下的 parquet 文件数量
      .option("hoodie.cleaner.commits.retained", "2")
      .mode(Append)
      .save("hdfs://10.17.64.238:9000/tmp/hudi_mor_table")
  }
}
