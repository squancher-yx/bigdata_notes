## 简介
spark structured streaming 是 spark2 后提出，目前社区现状是 Dstream 几乎不更新了，structured streaming 较为活跃。


**主要特点**：以结构化的方式操作流式数据，能够像使用Spark SQL处理离线的批处理一样，处理流数据，代码更简洁，写法更简单。

**模型**：将实时数据流视为被连续追加的表（无界的表）。从流数据源读取最新的可用数据，对其进行增量处理以更新结果，然后丢弃该源数据。仅保留更新结果所需的最小中间状态数据。


## 使用方式

**输出支持几种模式：**
	+ Complete Mode：整个更新后的结果表。
	+ Append Mode：自上次触发以来追加在结果表中的新行。
	+ Update Mode：自上次触发以来在结果表中已更新的行（包含 Append）。
	
**watermark：**
如果长时间运行，中间内存会积压越来越多，系统需要知道何时可以从内存中状态删除旧的聚合，仅在特定聚合条件下可以清除聚合状态
	+ 输出模式必须为“追加”或“更新”。
	+ 聚合必须有一列为事件时间（watermark 中指定的列）或者包含在 window 中。
	+ withWatermark 和聚合必须使用相同时间戳列。例如， df.withWatermark("time", "1 min").groupBy("time2").count()在附加输出模式下无效。
	+ withWatermark必须在使用水印详细信息的聚合之前调用。例如，df.groupBy("time").count().withWatermark("time", "1 min")在追加输出模式下无效。
	注意：可确保引擎永远不会丢弃任何少于延迟时间内的数据，但是延迟超过的数据不能保证被删除；它可能会或可能不会聚合。数据延迟的时间越长，引擎处理数据的可能性就越小。
	
Spark 2.3 后支持流之间的 join，为了避免无界状态，必须定义其他联接条件，以使无限期的旧输入不能与将来的输入匹配：
	+ 在两个输入上定义水印延迟
	+ 定义两个输入之间的事件时间约束：
		+ 时间范围加入条件（例如...JOIN ON leftTime BETWEEN rightTime AND rightTime + INTERVAL 1 HOUR），
		加入事件时间窗口（例如...JOIN ON leftTimeWindow = rightTimeWindow）。
	+ 流/普通 Dataset 之间的支持如下：
	
|Left Input|Right Input|Join Type|
|-|-|-|
|Static|Static|All types|Supported, since its not on streaming data even though it can be present in a streaming query|
|Stream|Static|Inner|Supported, not stateful|
|-|-|Left Outer|Supported, not stateful|
|-|-|Right Outer|Not supported|
|-|-|Full Outer|Not supported|
|Static|Stream|Inner|Supported, not stateful|
|-|-|Left Outer|Not supported|
|-|-|Right Outer|Supported, not stateful|
|-|-|Full Outer|Not supported|
|Stream|Stream|Inner|Supported, optionally specify watermark on both sides + time constraints for state cleanup|
|-|-|Left Outer|Conditionally supported, must specify watermark on right + time constraints for correct results, optionally specify watermark on left for all state cleanup|
|-|-|Right Outer|Conditionally supported, must specify watermark on left + time constraints for correct results, optionally specify watermark on right for all state cleanup|
|-|-|Full Outer|Not supported|


一些功能基本上很难有效地在流数据上实现，不支持的 DataFrame/Dataset 操作：
	+ 流数据集尚不支持多个流聚合。
	+ 流数据集不支take前N行。
	+ 不支持 Distinct
	+ 仅在聚合之后且在“完整输出模式”下，流数据集才支持排序操作。
	+ 不能直接使用 count()，可以使用 ds.groupBy().count()
	

**不同操作输出支持的模式**
|Query Type||Supported Output Modes|Notes|
|-|-|-|-|
|Queries with aggregation|Aggregation on event-time with watermark|Append, Update, Complete|Append mode uses watermark to drop old aggregation state. But the output of a windowed aggregation is delayed the late threshold specified in `withWatermark()` as by the modes semantics, rows can be added to the Result Table only once after they are finalized (i.e. after watermark is crossed). See the Late Data section for more details.<br><br>Update mode uses watermark to drop old aggregation state.<br><br>Complete mode does not drop old aggregation state since by definition this mode preserves all data in the Result Table.|
|-|Other aggregations|Complete, Update|Since no watermark is defined (only defined in other category), old aggregation state is not dropped.<br><br>Append mode is not supported as aggregates can update thus violating the semantics of this mode.|
|Queries with mapGroupsWithState|-|Update|-|
|Queries with flatMapGroupsWithState|Append operation mode|Append|Aggregations are allowed after flatMapGroupsWithState.|
|-|Update operation mode|Update|Aggregations not allowed after flatMapGroupsWithState.|
|Queries with joins|-|Append|Update and Complete mode not supported yet. See the support matrix in the Join Operations section for more details on what types of joins are supported.|
|Other queries|-|Append, Update|Complete mode not supported as it is infeasible to keep all unaggregated data in the Result Table.|

**使用Foreach和ForeachBatch**
foreachBatch 允许流查询的每个微批处理的输出数据上执行的函数。
```
streamingDF.writeStream.foreachBatch { (batchDF: DataFrame, batchId: Long) =>
  batchDF.persist()
  batchDF.write.format(...).save(...)  // location 1
  batchDF.write.format(...).save(...)  // location 2
  batchDF.unpersist()
}
```
注意：
	+ 默认情况下，foreachBatch仅提供至少一次写入保证。但是，您可以使用提供给该函数的batchId作为对输出进行重复数据删除并获得一次保证的方式。
	+ foreachBatch不适用于连续处理模式，因为它基本上依赖于流查询的微批执行。如果以连续模式写入数据，请改用foreach。

foreach
```
streamingDatasetOfString.writeStream.foreach(
  new ForeachWriter[String] {

    def open(partitionId: Long, version: Long): Boolean = {
      // Open connection
    }

    def process(record: String): Unit = {
      // Write string to connection
    }

    def close(errorOrNull: Throwable): Unit = {
      // Close the connection
    }
  }
).start()
```
这些方法的生命周期如下：
+ 对于每个具有partition_id的分区：
	+ 对于每个具有epoch_id的流数据的每个批次：
		+ 调用方法open（partitionId，epochId）。
		+ 如果open（…）返回true，则对于分区和批处理的每一行，将调用process（row）方法。
		+ 调用方法close（error）时，处理行时发生错误（如果有）。
如果存在open（）方法成功返回（与返回值无关），则调用close（）方法（如果存在），除非JVM或Python进程在中间崩溃。

注意： Spark无法保证（partitionId，epochId）的输出相同，因此无法使用（partitionId，epochId）实现重复数据删除。例如，出于某些原因，source提供了不同数量的分区，Spark优化更改了分区数量，等等。如果需要在输出上进行重复数据删除，尝试foreachBatch。


**Triggers**
流查询的触发器设置定义了流数据处理的时间

|Trigger Type|Description|
|-|-|
|unspecified (default)|If no trigger setting is explicitly specified, then by default, the query will be executed in micro-batch mode, where micro-batches will be generated as soon as the previous micro-batch has completed processing.|
|Fixed interval micro-batches|The query will be executed with micro-batches mode, where micro-batches will be kicked off at the user-specified intervals.<br>If the previous micro-batch completes within the interval, then the engine will wait until the interval is over before kicking off the next micro-batch.<br>If the previous micro-batch takes longer than the interval to complete (i.e. if an interval boundary is missed), then the next micro-batch will start as soon as the previous one completes (i.e., it will not wait for the next interval boundary).<br>If no new data is available, then no micro-batch will be kicked off.|
|One-time micro-batch|The query will execute *only one* micro-batch to process all the available data and then stop on its own. This is useful in scenarios you want to periodically spin up a cluster, process everything that is available since the last period, and then shutdown the cluster. In some case, this may lead to significant cost savings.|
|Continuous with fixed checkpoint interval (experimental)|The query will be executed in the new low-latency, continuous processing mode. Read more about this in the Continuous Processing section below.|

**kafka 集成**
使用 spark structured streaming 时完全由 spark 管理消费偏移量。spark 不会提交任何偏移量；spark2 无法指定 group id（spark3 可以强制指定）；spark2只能指定偏移量进行消费，spark可以从指定时间戳消费（仅调用KafkaConsumer.offsetsForTimes实现）