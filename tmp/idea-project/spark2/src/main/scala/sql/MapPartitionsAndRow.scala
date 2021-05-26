package sql

import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.types.{DataTypes, StructType}
import org.apache.spark.sql.{Encoders, Row, RowFactory, SparkSession}

import scala.collection.mutable.ArrayBuffer

object MapPartitionsAndRow {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("udf test")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._
    var df = spark.sparkContext.parallelize(Seq(("a", "b"))).toDF("a", "b")
    // 不支持自动隐式转换，手动实现
    var customStructType: StructType = new StructType
    customStructType = customStructType.add("obj1", DataTypes.StringType, false)
    customStructType = customStructType.add("obj2", DataTypes.StringType, false)
    df.mapPartitions(f => new MapColumns(f))(RowEncoder.apply(customStructType)).show()

  }

}

/**
 * 自定义迭代器，spark 不需要拉取整个分区数据再处理
 *
 * @param iter
 */
class MapColumns(iter: Iterator[Row]) extends Iterator[Row] {
  override def hasNext: Boolean = {
    iter.hasNext
  }

  override def next(): Row = {
    val row = iter.next
    val res = new ArrayBuffer[String]()
    for (i <- 0 until row.length) {
      res += row.getString(i)
    }
    Row(res: _*)
  }
}

case class BeanTest(columns: String) extends Serializable