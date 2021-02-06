package com.yx.flink;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Target;
import static com.yx.flink.testclass.$;
import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaSparkContext;


public class FunTest {
    public static void main(String[] args) throws ParseException {
        SparkConf conf = new SparkConf();
        conf.setMaster("local[*]");
        conf.setAppName("test");
        JavaSparkContext jsc = new JavaSparkContext(conf);

    }
}


class testclass {
    public static int $(String name) {
        return 1;
    }
}