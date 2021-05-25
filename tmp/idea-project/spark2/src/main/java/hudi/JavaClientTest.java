//package hudi;
//
//import org.apache.hadoop.conf.Configuration;
//import org.apache.hadoop.fs.FileSystem;
//import org.apache.hadoop.fs.Path;
//import org.apache.hudi.client.HoodieJavaWriteClient;
//import org.apache.hudi.client.HoodieReadClient;
//import org.apache.hudi.client.SparkRDDWriteClient;
//import org.apache.hudi.client.common.HoodieJavaEngineContext;
//import org.apache.hudi.client.common.HoodieSparkEngineContext;
//import org.apache.hudi.common.engine.HoodieEngineContext;
//import org.apache.hudi.common.engine.HoodieLocalEngineContext;
//import org.apache.hudi.common.fs.FSUtils;
//import org.apache.hudi.common.model.HoodieAvroPayload;
//import org.apache.hudi.common.model.HoodieKey;
//import org.apache.hudi.common.model.HoodieRecord;
//import org.apache.hudi.common.model.HoodieTableType;
//import org.apache.hudi.common.table.HoodieTableMetaClient;
//import org.apache.hudi.config.HoodieCompactionConfig;
//import org.apache.hudi.config.HoodieIndexConfig;
//import org.apache.hudi.config.HoodieWriteConfig;
//import org.apache.hudi.index.HoodieIndex;
//import org.apache.hudi.index.SparkHoodieIndex;
//import org.apache.hudi.io.HoodieReadHandle;
//import org.apache.hudi.io.storage.HoodieFileReader;
//import org.apache.hudi.io.storage.HoodieFileReaderFactory;
//import org.apache.hudi.io.storage.HoodieHFileReader;
//import org.apache.hudi.table.HoodieJavaCopyOnWriteTable;
//import org.apache.hudi.table.HoodieJavaTable;
//import org.apache.hudi.table.HoodieTable;
//
//import java.io.IOException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
//public class JavaClientTest {
//    public static void main(String[] args) throws IOException {
//        String tablePath = "D:\\bak\\bigdata_notes\\tmp\\idea-project\\spark2\\src\\main\\hudi_data\\";
//        String tableName = "test";
//        String tableType = HoodieTableType.COPY_ON_WRITE.name();
//        Configuration hadoopConf = new Configuration();
//        Path path = new Path(tablePath);
//        FileSystem fs = FSUtils.getFs(tablePath, hadoopConf);
//        HoodieTableMetaClient.initTableType(hadoopConf, tablePath, HoodieTableType.valueOf(tableType),
//                tableName, HoodieAvroPayload.class.getName());
//        HoodieWriteConfig hudiWriteConf = HoodieWriteConfig.newBuilder()
//                // 数据schema
//                .withSchema(HoodieExampleDataGenerator.TRIP_EXAMPLE_SCHEMA)
//                // 数据插入更新并行度
//                .withParallelism(2, 2)
//                // 数据删除并行度
//                .withDeleteParallelism(2)
//                // hudi表索引类型，内存
//                .withIndexConfig(HoodieIndexConfig.newBuilder().withIndexType(HoodieIndex.IndexType.INMEMORY).build())
//                // 合并
//                .withCompactionConfig(HoodieCompactionConfig.newBuilder().archiveCommitsWith(20, 30).build())
//                .withPath(tablePath)
//                .forTable(tableName)
//                .build();
//
//        HoodieJavaWriteClient client = new HoodieJavaWriteClient<>(new HoodieJavaEngineContext(hadoopConf), hudiWriteConf);
//        String newCommitTime = client.startCommit();
//
//        System.out.println("Starting commit " + newCommitTime);
//
//        HoodieExampleDataGenerator<HoodieAvroPayload> dataGen = new HoodieExampleDataGenerator<>();
//        List<HoodieRecord<HoodieAvroPayload>> records = dataGen.generateInserts(newCommitTime, 10);
//        List<HoodieRecord<HoodieAvroPayload>> recordsSoFar = new ArrayList<>(records);
//        List<HoodieRecord<HoodieAvroPayload>> writeRecords =
//                recordsSoFar.stream().map(r -> new HoodieRecord<HoodieAvroPayload>(r)).collect(Collectors.toList());
//        client.insert(writeRecords,newCommitTime);
//        client.close();
//    }
//}
