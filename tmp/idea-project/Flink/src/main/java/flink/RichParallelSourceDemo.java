package flink;

import org.apache.flink.configuration.Configuration;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.RichSinkFunction;
import org.apache.flink.streaming.api.functions.source.RichParallelSourceFunction;

public class RichParallelSourceDemo {
    public static void main(String[] args) {
        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
    }
}

class MyParallelSource extends RichParallelSourceFunction<Long>{
    private boolean run = true;
    private Long count = 1L;
    int totalTask = 0;
    @Override
    public void open(Configuration parameters) throws Exception {
        super.open(parameters);
        this.totalTask = getRuntimeContext().getMaxNumberOfParallelSubtasks();
        int index = getRuntimeContext().getIndexOfThisSubtask();
        this.count = this.count + index;
    }

    @Override
    public void run(SourceContext<Long> sourceContext) throws Exception {
        while(run){
            sourceContext.collect(count);
            count+=totalTask;
            Thread.sleep(1000);
        }
    }

    @Override
    public void cancel() {
        this.run=false;
    }
}