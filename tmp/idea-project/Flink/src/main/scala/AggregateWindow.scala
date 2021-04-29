package flink

import java.util.concurrent.Executors

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.{TumblingEventTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time

object AggregateWindow {
  def main(args: Array[String]): Unit = {
//    Executors.newFixedThreadPool()
    val env = StreamExecutionEnvironment.getExecutionEnvironment;
//    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    val source: DataStream[String] = env.socketTextStream("127.0.0.1", 8888)
    source.filter(f=>{
      f.split(" ").length == 2
    }).map(f => {
      val line = f.split(" ")
      (line(0), line(1))
    }).setParallelism(3).rebalance.keyBy(f => {
      f._1
    }).window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
      .aggregate(new MyAggregateFunction).print()
    env.execute("t")

  }

}

class MyAggregateFunction extends AggregateFunction[(String, String), String,String] {
  override def createAccumulator(): String = {
    println("init")
    "init"
  }

  override def add(value: (String, String), accumulator: String): String = {
    println("add")
    println(Thread.currentThread().getName)
    "add"
  }

  override def getResult(accumulator: String): String = {
    println("result")
    "result"
  }

  //合并两个ACC
  override def merge(a: String, b: String): String = {
    println("merge")
    "merge"
  }
}