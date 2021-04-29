//import flink.MyAggregateFunction
//import org.apache.flink.api.java.tuple.Tuple
//import org.apache.flink.api.scala.createTypeInformation
//import org.apache.flink.streaming.api.scala.function.ProcessWindowFunction
//import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
//import org.apache.flink.streaming.api.windowing.assigners.TumblingProcessingTimeWindows
//import org.apache.flink.streaming.api.windowing.time.Time
//import org.apache.flink.util.Collector
//
//object ProcessWindowFunctionTest {
//  def main(args: Array[String]): Unit = {
//    val env = StreamExecutionEnvironment.getExecutionEnvironment;
//    //    env.setStreamTimeCharacteristic(TimeCharacteristic.ProcessingTime)
//    val source: DataStream[String] = env.socketTextStream("127.0.0.1", 8888)
//    source.filter(f=>{
//      f.split(" ").length == 2
//    }).map(f => {
//      val line = f.split(" ")
//      (line(0), line(1))
//    }).setParallelism(3).rebalance.keyBy(f => {
//      f._1
//    }).window(TumblingProcessingTimeWindows.of(Time.seconds(10)))
//      .process(new ProcessWindowFunction {
//        override def process(key: String, context: Context, elements: Iterable[(String, String)], out: Collector[Nothing]): Unit = {
//        }
//      }).print()
//    env.execute("t")
//
//  }
//
//}
