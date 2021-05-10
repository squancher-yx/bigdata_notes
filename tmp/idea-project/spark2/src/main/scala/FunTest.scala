import org.apache.spark.sql.SparkSession

object FunTest {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .master("local[*]")
      .appName("test")
      .getOrCreate()
    val sc = spark.sparkContext
    val conf =sc.hadoopConfiguration.iterator()
    while(conf.hasNext){
      println(conf.next())
    }
  }

}
