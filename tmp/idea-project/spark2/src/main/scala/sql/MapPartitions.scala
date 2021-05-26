package sql

import org.apache.spark.sql.catalyst.encoders.RowEncoder
import org.apache.spark.sql.{Encoders, Row, RowFactory, SparkSession}

object MapPartitions {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("udf test")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._
    var df = spark.sparkContext.parallelize(Seq(("a", "b"))).toDF("a", "b")
    // 不支持自动隐式转换，手动实现
    df.mapPartitions(f => new MapColumns(f))(Encoders.bean(classOf[BeanTest])).show()

  }

}

class MapColumns(iter: Iterator[Row]) extends Iterator[BeanTest] {
  override def hasNext: Boolean = {
    iter.hasNext
  }

  override def next(): BeanTest = {
    val row = iter.next
    println(row.getString(1))
    val value = row.getString(1).split("0", -1)
    val sourceColumn = Array("value", "offset", "partition")
    //    val offset = row.getString(2)
    //    val partition = row.getString(3)
    //    udf()
    val tmp = Row(0, "qq")
    println(tmp + "123")
    BeanTest("1233")
    //    ("1","2q")
  }
}

case class BeanTest(columns: String) extends Serializable