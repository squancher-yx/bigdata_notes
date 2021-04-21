package flink.scala

import org.apache.flink.api.common.functions.AggregateFunction
import org.apache.flink.streaming.api.TimeCharacteristic
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment, createTypeInformation}
import org.apache.flink.streaming.api.windowing.assigners.{TumblingEventTimeWindows, TumblingProcessingTimeWindows}
import org.apache.flink.streaming.api.windowing.time.Time

object AggregateWindow {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment;
//    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
    val source: DataStream[String] = env.socketTextStream("127.0.0.1", 8888)
    source.map(f => {
      val line = f.split(" ")
      (line(0), line(1))
    }).keyBy(f => {
      f._1
    }).window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
      .aggregate(new MyAggregateFunction).print()
    env.execute("t")
  }

}

class MyAggregateFunction extends AggregateFunction[(String, String), String,String] {
  override def createAccumulator(): String = {
    ""
  }

  override def add(value: (String, String), accumulator: String): String = {
    println("add")
    ""
  }

  override def getResult(accumulator: String): String = {
    println("qqq")
    ""
  }

  //合并两个ACC
  override def merge(a: String, b: String): String = {
    ""
  }
}