package com.yx.flink;

import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.operators.DataSource;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.hadoopcompatibility.HadoopInputs;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapred.JobConf;
import org.apache.hadoop.mapred.TextInputFormat;

public class FlinkHDFSSource {
    public static void main(String[] args) throws Exception {
        ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
        JobConf job = new JobConf();
        job.set("io.compression.codec.lzo.class","com.hadoop.compression.lzo.lzoCodec");
        job.set("io.compression.codecs","org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.DefaultCodec,org.apache.hadoop.io.compress.BZip2Codec,com.hadoop.compression.lzo.LzoCodec,com.hadoop.compression.lzo.LzopCodec");
        DataSource<Tuple2<LongWritable, Text>> ds = env.createInput(HadoopInputs.readHadoopFile(new TextInputFormat(),LongWritable.class,Text.class,"path",job));
        ds.print();
    }
}
