package sql

import org.apache.spark.sql.{DataFrame, Dataset, SparkSession}
import org.apache.spark.sql.functions._

object UDFAndWithColumn {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .config("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
      .appName("udf test")
      .master("local[*]")
      .getOrCreate()
    import spark.implicits._
    var df = spark.sparkContext.parallelize(Seq(("a", "b"))).toDF("a", "b")
    fun3(df, spark)

  }

  //方式三
  def fun3(df: DataFrame, spark: SparkSession): Unit ={
    //相当于 UDF1
    val plusOne = udf((x: String) => x + 1)

    df.withColumn("d", plusOne(col("a"))).show()
    //显示调用 apply
    df.withColumn("d", plusOne.apply(col("a"))).show()
    // 注册后调用
    spark.udf.register("plusOne", plusOne)
    df.selectExpr("*","plusOne(a) as d").show()
  }

  //方式二
  def fun2(df: DataFrame, spark: SparkSession): Unit = {
    //使用匿名函数,相当于 UDF2
    val tmp = udf((a: String, b: String) => {
      a+"_end"
    })
    df.withColumn("d", tmp(col("a"), lit(2))).show()
    //显示调用 apply
    df.withColumn("d", tmp.apply(col("a"), lit(2))).show()
  }

  //方式一
  def fun1(df: DataFrame, spark: SparkSession): Unit = {
    import spark.implicits._

    /**
     * 相当于 UDF2
     *
     * @param input 输入字段
     * @param index 任意的额外参数，可随意增减
     * @return
     */
    def udfTest(input: String, index: String): String = {
      // 转换 input -> String
      val len = input.split("0").length
      input + "_end"
    }

    // 方法转换为函数
    val tmp = udf(udfTest _)
    // 使用时 tmp 参数个数需要与 udfTest 对应
    df.withColumn("d", tmp(col("a"), lit(2))).show()
    //显示调用 apply
    df.withColumn("d", tmp.apply(col("a"), lit(2))).show()
  }
}
