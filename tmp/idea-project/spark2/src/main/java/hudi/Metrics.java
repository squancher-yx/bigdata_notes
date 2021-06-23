package hudi;

import org.apache.hudi.com.codahale.metrics.Gauge;
import org.apache.hudi.com.codahale.metrics.MetricRegistry;
import org.apache.hudi.metrics.userdefined.AbstractUserDefinedMetricsReporter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.sql.execution.columnar.STRING;

import java.io.Closeable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class Metrics
        extends AbstractUserDefinedMetricsReporter {
    private static final Logger log = LogManager.getLogger(KafkaToHudi.class);

    private ScheduledExecutorService exec = Executors.newScheduledThreadPool(1, r -> {
        Thread t = Executors.defaultThreadFactory().newThread(r);
        t.setDaemon(true);
        return t;
    });

    public Metrics(Properties props, MetricRegistry registry) {
        super(props, registry);
    }

    @Override
    public void start() {
        exec.scheduleAtFixedRate(this::report, 30, 300, TimeUnit.SECONDS);
    }

    @Override
    public void report() {
        Map<String, org.apache.hudi.com.codahale.metrics.Gauge> map = this.getRegistry().getGauges();
        String clean_duration = "-1";
        Gauge clean_duration_tmp = map.getOrDefault("KafkaSource.clean.duration", null);
        if (clean_duration_tmp != null) {
            clean_duration = clean_duration_tmp.getValue().toString();
        }
        String clean_numFilesDeleted = "-1";
        Gauge clean_numFilesDeleted_tmp = map.getOrDefault("KafkaSource.clean.numFilesDeleted", null);
        if (clean_numFilesDeleted_tmp != null) {
            clean_numFilesDeleted = clean_numFilesDeleted_tmp.getValue().toString();
        }
        Gauge deltacommit_commitTime_tmp = map.getOrDefault("KafkaSource.deltacommit.commitTime", null);
        String deltacommit_commitTime = deltacommit_commitTime_tmp != null ? deltacommit_commitTime_tmp.getValue().toString() : "-1";
        Gauge deltacommit_duration_tmp = map.getOrDefault("KafkaSource.deltacommit.duration", null);
        String deltacommit_duration = deltacommit_duration_tmp != null ? deltacommit_duration_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalBytesWritten_tmp = map.getOrDefault("KafkaSource.deltacommit.totalBytesWritten", null);
        String deltacommit_totalBytesWritten = deltacommit_totalBytesWritten_tmp != null ? deltacommit_totalBytesWritten_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalCompactedRecordsUpdated_tmp = map.getOrDefault("KafkaSource.deltacommit.totalCompactedRecordsUpdated", null);
        String deltacommit_totalCompactedRecordsUpdated = deltacommit_totalCompactedRecordsUpdated_tmp != null ? deltacommit_totalCompactedRecordsUpdated_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalCreateTime_tmp = map.getOrDefault("KafkaSource.deltacommit.totalCreateTime", null);
        String deltacommit_totalCreateTime = deltacommit_totalCreateTime_tmp != null ? deltacommit_totalCreateTime_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalFilesInsert_tmp = map.getOrDefault("KafkaSource.deltacommit.totalFilesInsert", null);
        String deltacommit_totalFilesInsert = deltacommit_totalFilesInsert_tmp != null ? deltacommit_totalFilesInsert_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalFilesUpdate_tmp = map.getOrDefault("KafkaSource.deltacommit.totalFilesUpdate", null);
        String deltacommit_totalFilesUpdate = deltacommit_totalFilesUpdate_tmp != null ? deltacommit_totalFilesUpdate_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalInsertRecordsWritten_tmp = map.getOrDefault("KafkaSource.deltacommit.totalInsertRecordsWritten", null);
        String deltacommit_totalInsertRecordsWritten = deltacommit_totalInsertRecordsWritten_tmp != null ? deltacommit_totalInsertRecordsWritten_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalLogFilesCompacted_tmp = map.getOrDefault("KafkaSource.deltacommit.totalLogFilesCompacted", null);
        String deltacommit_totalLogFilesCompacted = deltacommit_totalLogFilesCompacted_tmp != null ? deltacommit_totalLogFilesCompacted_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalLogFilesSized_tmp = map.getOrDefault("KafkaSource.deltacommit.totalLogFilesSize", null);
        String deltacommit_totalLogFilesSize = deltacommit_totalLogFilesSized_tmp != null ? deltacommit_totalLogFilesSized_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalRecordsWritten_tmp = map.getOrDefault("KafkaSource.deltacommit.totalRecordsWritten", null);
        String deltacommit_totalRecordsWritten = deltacommit_totalRecordsWritten_tmp != null ? deltacommit_totalRecordsWritten_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalUpdateRecordsWritten_tmp = map.getOrDefault("KafkaSource.deltacommit.totalUpdateRecordsWritten", null);
        String deltacommit_totalUpdateRecordsWritten = deltacommit_totalUpdateRecordsWritten_tmp != null ? deltacommit_totalUpdateRecordsWritten_tmp.getValue().toString() : "-1";
        Gauge deltacommit_totalUpsertTime_tmp = map.getOrDefault("KafkaSource.deltacommit.totalUpsertTime", null);
        String deltacommit_totalUpsertTime = deltacommit_totalUpsertTime_tmp != null ? deltacommit_totalUpsertTime_tmp.getValue().toString() : "-1";
        Gauge finalize_duration_tmp = map.getOrDefault("KafkaSource.finalize.duration", null);
        String finalize_duration = finalize_duration_tmp != null ? finalize_duration_tmp.getValue().toString() : "-1";
        Gauge finalize_numFilesFinalized_tmp = map.getOrDefault("KafkaSource.finalize.numFilesFinalized", null);
        String finalize_numFilesFinalized = finalize_numFilesFinalized_tmp != null ? finalize_numFilesFinalized_tmp.getValue().toString() : "-1";
        Gauge index_INSERT_duration_tmp = map.getOrDefault("KafkaSource.index.INSERT.duratio", null);
        String index_INSERT_duration = index_INSERT_duration_tmp != null ? index_INSERT_duration_tmp.getValue().toString() : "-1";
        System.out.println("clean_duration:" + clean_duration);
        System.out.println("clean_numFilesDeleted:" + clean_numFilesDeleted);
        System.out.println("deltacommit_commitTime:" + deltacommit_commitTime);
        System.out.println("deltacommit_duration:" + deltacommit_duration);
        System.out.println("deltacommit_totalBytesWritten:" + deltacommit_totalBytesWritten);
        System.out.println("deltacommit_totalCompactedRecordsUpdated:" + deltacommit_totalCompactedRecordsUpdated);
        System.out.println("deltacommit_totalCreateTime:" + deltacommit_totalCreateTime);
        System.out.println("deltacommit_totalFilesInsert:" + deltacommit_totalFilesInsert);
        System.out.println("deltacommit_totalFilesUpdate:" + deltacommit_totalFilesUpdate);
        System.out.println("deltacommit_totalInsertRecordsWritten:" + deltacommit_totalInsertRecordsWritten);
        System.out.println("deltacommit_totalLogFilesCompacted:" + deltacommit_totalLogFilesCompacted);
        System.out.println("deltacommit_totalLogFilesSize:" + deltacommit_totalLogFilesSize);
        System.out.println("deltacommit_totalRecordsWritten:" + deltacommit_totalRecordsWritten);
        System.out.println("deltacommit_totalUpdateRecordsWritten:" + deltacommit_totalUpdateRecordsWritten);
        System.out.println("deltacommit_totalUpsertTime:" + deltacommit_totalUpsertTime);
        System.out.println("finalize_duration:" + finalize_duration);
        System.out.println("finalize_numFilesFinalized:" + finalize_numFilesFinalized);
        System.out.println("index_INSERT_duration:" + index_INSERT_duration);

//        this.getRegistry().getGauges().forEach((key, value) ->
//                log.warn("key: " + key + " value: " + value.getValue().toString()));
        String[] values = new String[18];
        values[0] = clean_duration;
        values[1] = clean_numFilesDeleted;
        values[2] = deltacommit_commitTime;
        values[3] = deltacommit_duration;
        values[4] = deltacommit_totalBytesWritten;
        values[5] = deltacommit_totalCompactedRecordsUpdated;
        values[6] = deltacommit_totalCreateTime;
        values[7] = deltacommit_totalFilesInsert;
        values[8] = deltacommit_totalFilesUpdate;
        values[9] = deltacommit_totalInsertRecordsWritten;
        values[10] = deltacommit_totalLogFilesCompacted;
        values[11] = deltacommit_totalLogFilesSize;
        values[12] = deltacommit_totalRecordsWritten;
        values[13] = deltacommit_totalUpdateRecordsWritten;
        values[14] = deltacommit_totalUpsertTime;
        values[15] = finalize_duration;
        values[16] = finalize_numFilesFinalized;
        values[17] = index_INSERT_duration;
        InsertMetrics mysql = new InsertMetrics();

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = new Date(Long.parseLong(deltacommit_commitTime));

        mysql.hudi(values, df.format(date));

    }

    @Override
    public Closeable getReporter() {
        return null;
    }

    @Override
    public void stop() {
        System.out.println("end");
        exec.shutdown();
    }
}
