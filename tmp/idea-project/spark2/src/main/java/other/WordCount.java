package other;

import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

public class WordCount {
    public static void main(String[] args) {
        SparkSession spark = SparkSession.builder().getOrCreate();
        JavaSparkContext jsc = new JavaSparkContext(spark.sparkContext());
        if(args.length!=2){
            System.out.println("");
        }
    }
}
