[官方文档](https://hbase.apache.org/book.html#_bulk_load)


使用`rdd.hbaseBulkLoad`，需要依赖：
```
<!-- https://mvnrepository.com/artifact/org.apache.hbase/hbase-spark -->
<dependency>
    <groupId>org.apache.hbase</groupId>
    <artifactId>hbase-spark</artifactId>
    <version>2.1.0-cdh6.3.4</version>
</dependency>


import org.apache.hadoop.hbase.spark.HBaseRDDFunctions._
```
