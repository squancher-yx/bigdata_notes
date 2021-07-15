package other

import org.apache.spark.TaskContext
import org.apache.spark.scheduler.{SparkListener, SparkListenerJobStart}
import org.apache.spark.sql.SparkSession

object CancelJob {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder()
      .appName("test")
      .master("local[*]")
      .getOrCreate()

    val sc = spark.sparkContext
    sc.addSparkListener(new TestListener)
//    sc.setLocalProperty("thread", "client")
    sc.setLocalProperty("spark.job.interruptOnCancel", "true")
    println("paartition:"+TaskContext.getPartitionId())

    val source = sc.parallelize(Seq("aaa","bbb","ccc"))
    new Thread(new Runnable {
      override def run(): Unit =
        source.map(f=>{
          println("kaishi")
          Thread.sleep(1000000)
          println("jiesu")
        }).collect()
    }).start()
    Thread.sleep(1000)
    println(source.count())
    Thread.sleep(1000)
    sc.cancelJob(0)
    println("cancel")
//    sc.stop()
  }

}

class TestListener extends SparkListener{
  override def onJobStart(jobStart: SparkListenerJobStart): Unit = {
    println("jobid:"+jobStart)
    //jobStart.properties 可获取 sc.setLocalProperty 中的参数
    println("thread:"+jobStart.properties.stringPropertyNames())
  }
}