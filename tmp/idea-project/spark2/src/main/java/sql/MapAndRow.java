package sql;

import org.apache.spark.api.java.JavaRDD;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.api.java.function.Function;
import org.apache.spark.api.java.function.MapFunction;
import org.apache.spark.sql.Encoders;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.catalyst.encoders.RowEncoder;
import org.apache.spark.sql.types.DataTypes;
import org.apache.spark.sql.types.StructField;
import org.apache.spark.sql.types.StructType;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * map 时将 Row 转换为新的 Row 或自定义 Bean 类型
 */
public class MapAndRow {
    // 必须为 public，使用内部类代替
    public static class CustomBean implements Serializable {
        String obj1;
        String obj2;

        public CustomBean(String obj1, String obj2) {
            this.obj1 = obj1;
            this.obj2 = obj2;
        }

        public String getObj1() {
            return obj1;
        }

        public String getObj2() {
            return obj2;
        }

        public void setObj2(String obj2) {
            this.obj2 = obj2;
        }

        public void setObj1(String obj1) {
            this.obj1 = obj1;
        }
    }

    public static void main(String[] args) {
        SparkSession spark = SparkSession
                .builder()
                .master("local[*]")
                .appName("test")
                .getOrCreate();

        JavaSparkContext jsc = JavaSparkContext.fromSparkContext(spark.sparkContext());

        // 动态绑定转换
        JavaRDD<Row> javaRDD = jsc.parallelize(Arrays.asList("q q", "w w")).map(new Function<String, Row>() {
            @Override
            public Row call(String s) throws Exception {
                return RowFactory.create(s.split(" ")[0], s.split(" ")[1]);
            }
        });

        // 创建 Struct
        List<StructField> structFieldList = new ArrayList<>();
        structFieldList.add(DataTypes.createStructField("a", DataTypes.StringType, true));
        structFieldList.add(DataTypes.createStructField("b", DataTypes.StringType, true));
        StructType structType = DataTypes.createStructType(structFieldList);


        // 方式一
        spark.createDataFrame(javaRDD, structType)
                .map((MapFunction<Row, CustomBean>) row -> {
                    return new CustomBean(row.getString(0), row.getString(1));
                }, Encoders.bean(CustomBean.class)).show();

        // 方式二
        // 新的 Row 字段类型需要与 schema 对应，个数不能少于 schema。
        spark.createDataFrame(javaRDD, structType)
                .map((MapFunction<Row, Row>) row -> {
                    return RowFactory.create(row.getString(1) + "1", null);
                }, RowEncoder.apply(structType)).show();
    }
}
