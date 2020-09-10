一、生成LZO文件：

```lzop -v PATH```

二、生成 LZO 索引

+ 如果已经在HDFS中：
```
hadoop jar hadoop/share/hadoop/common/hadoop-lzo-0.4.20.jar com.hadoop.compression.lzo.DistributedLzoIndexer PATH
```
+ 在本地直接生成而不转为 MR 作业
```
hadoop jar hadoop/share/hadoop/common/hadoop-lzo-0.4.20.jar com.hadoop.compression.lzo.LzoIndexer file://PATH(FILE)
```