package spark;

import org.apache.hadoop.mapred.lib.MultipleTextOutputFormat;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.PairFunction;
import org.apache.spark.streaming.api.java.JavaStreamingContext;
import scala.Tuple2;

import java.util.ArrayList;
import java.util.List;

public class SparkToHDFS {
    public static void main(String[] args) {
        SparkConf conf = new SparkConf();
        conf.setMaster("local[2]");
        conf.setAppName("test");
        JavaSparkContext sc = new JavaSparkContext(conf);
        List<String> list = new ArrayList<>();
        list.add("qq ee");
        list.add("qq rr");
        JavaRDD<String> rdd = sc.parallelize(list);
        // 显示启用压缩，本地测试使用
        // sc.hadoopConfiguration().set("io.compress","io.compression.codecs","org.apache.hadoop.io.compress.GzipCodec,org.apache.hadoop.io.compress.DefaultCodec,org.apache.hadoop.io.compress.BZip2Codec,com.hadoop.compression.lzo.LzoCodec,com.hadoop.compression.lzo.LzopCodec");
        // 禁用生成_SUCCESS文件
//        sc.hadoopConfiguration().set("mapreduce.fileoutputcommitter.marksuccessfuljobs", "false");
        rdd.distinct().mapToPair(new PairFunction<String, Object, Object>() {
            @Override
            public Tuple2<Object, Object> call(String s) throws Exception {
                String[] line = s.split(" ", -1);
                String key = line[0];
                String value = line[1];
                return new Tuple2<>(key, value);
            }
            //需要lzo压缩类 saveAsHadoopFile("path",String.class,String.class,MyOutPutFormat.class,com.hadoop.compression.lzo.LzopCodec);
        }).saveAsHadoopFile("path", String.class, String.class, MyOutPutFormat.class);
    }
}

class MyOutPutFormat extends MultipleTextOutputFormat<String, String> {
    @Override
    protected String generateFileNameForKeyValue(String key, String value, String name) {

        return key + "/" + value + "/" + name;
    }
}
