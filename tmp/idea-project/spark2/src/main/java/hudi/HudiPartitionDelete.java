package hudi;

import org.apache.hudi.client.HoodieWriteResult;
import org.apache.hudi.client.SparkRDDWriteClient;
import org.apache.hudi.client.common.HoodieSparkEngineContext;
import org.apache.hudi.common.table.timeline.HoodieActiveTimeline;
import org.apache.hudi.common.table.timeline.HoodieTimeline;
import org.apache.hudi.common.util.Option;
import org.apache.hudi.config.HoodieIndexConfig;
import org.apache.hudi.config.HoodieWriteConfig;
import org.apache.hudi.exception.HoodieException;
import org.apache.hudi.index.HoodieIndex;
import org.apache.spark.api.java.JavaSparkContext;
import org.apache.spark.sql.SparkSession;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Properties;

public class HudiPartitionDelete {
    public static void main(String[] args) {

        SparkSession spark = SparkSession.builder()
                .appName("test")
                .master("local[*]")
                .getOrCreate();
        Properties pro = new Properties();
        pro.put("hoodie.datasource.write.storage.type", "MERGE_ON_READ");
        pro.put("hoodie.compact.inline", "true");
        pro.put("hoodie.index.type", "BLOOM");
        JavaSparkContext jssc = JavaSparkContext.fromSparkContext(spark.sparkContext());
        HoodieWriteConfig hoodieCfg = HoodieWriteConfig
                .newBuilder().withPath("/tmp/hudi_mor_table")
                // MERGE_ON_READ 必须指定
                .withSchema("{\"type\":\"record\",\"name\":\"KafkaSource_record\",\"namespace\":\"hoodie.KafkaSource\",\"fields\":[{\"name\":\"offset\",\"type\":[\"string\",\"null\"]},{\"name\":\"partition\",\"type\":[\"string\",\"null\"]},{\"name\":\"ftime\",\"type\":[\"string\",\"null\"]},{\"name\":\"logname\",\"type\":[\"string\",\"null\"]},{\"name\":\"server\",\"type\":[\"string\",\"null\"]},{\"name\":\"title\",\"type\":[\"int\",\"null\"]},{\"name\":\"info_version\",\"type\":[\"int\",\"null\"]},{\"name\":\"uid\",\"type\":[\"long\",\"null\"]},{\"name\":\"role_id\",\"type\":[\"long\",\"null\"]},{\"name\":\"stats_obj\",\"type\":[\"int\",\"null\"]},{\"name\":\"login_platform\",\"type\":[\"string\",\"null\"]},{\"name\":\"ip\",\"type\":[\"string\",\"null\"]},{\"name\":\"stats_location\",\"type\":[\"string\",\"null\"]},{\"name\":\"stats_rel\",\"type\":[\"string\",\"null\"]},{\"name\":\"opt_result\",\"type\":[\"string\",\"null\"]},{\"name\":\"stats_col\",\"type\":[\"string\",\"null\"]},{\"name\":\"pdate\",\"type\":\"int\"},{\"name\":\"gate\",\"type\":[\"string\",\"null\"]},{\"name\":\"path\",\"type\":\"string\"},{\"name\":\"ts\",\"type\":[\"long\",\"null\"]},{\"name\":\"uuid\",\"type\":[\"string\",\"null\"]}]}")
                .withProperties(pro)
                .withAutoCommit(false)
                .build();

        SparkRDDWriteClient client = new SparkRDDWriteClient<>(new HoodieSparkEngineContext(jssc), hoodieCfg, true);
        String cleanInstant = HoodieActiveTimeline.createNewInstantTime();
        client.startCommitWithTime(cleanInstant, HoodieTimeline.REPLACE_COMMIT_ACTION);
        HoodieWriteResult writeResult = client.deletePartitions(Collections.singletonList("20210608/mx2"), cleanInstant);
        boolean commitSuccess = client.commit(cleanInstant, writeResult.getWriteStatuses(), Option.empty(),
                HoodieTimeline.REPLACE_COMMIT_ACTION,
                writeResult.getPartitionToReplaceFileIds());
        if (commitSuccess) {
            System.out.println("Commit " + cleanInstant + " successful!");
        } else {
            System.out.println("Commit " + cleanInstant + " failed!");
            throw new HoodieException("Commit " + cleanInstant + " failed!");
        }

//        client.clean();


        client.close();
    }
}
