package spark;

import org.apache.spark.SparkConf;
import org.apache.spark.api.java.JavaFutureAction;
import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.VoidFunction;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.function.Consumer;

//java API不适用，scala API collectAsync可直接map
public class AsyncActionTest {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
        SparkConf conf = new SparkConf();
        conf.setMaster("local[2]");
        conf.setAppName("sdf");
        conf.set("spark.scheduler.mode","FAIR");

        JavaSparkContext jsc = new JavaSparkContext(conf);
        jsc.setLogLevel("ERROR");
        ArrayList<String> list = new ArrayList<>();
        list.add("qqq");
        list.add("www");
        list.add("eee");

        ArrayList<String> list2 = new ArrayList<>();
        list2.add("rrr");
        list2.add("ttt");
        list2.add("yyy");

        JavaRDD<String> rdd= jsc.parallelize(list);
        JavaRDD<String> rdd2= jsc.parallelize(list2);

        JavaFutureAction<List<String>> test = rdd.collectAsync();

        test.get().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });
        JavaFutureAction<List<String>> test2 = rdd2.collectAsync();
        test2.get().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                System.out.println(s);
            }
        });
                Thread.sleep(100000);
    }
}
