package hudi;

import org.apache.hudi.com.codahale.metrics.Gauge;
import org.apache.hudi.com.codahale.metrics.MetricRegistry;
import org.apache.hudi.metrics.userdefined.AbstractUserDefinedMetricsReporter;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.spark.sql.execution.columnar.STRING;

import java.io.Closeable;
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
        exec.scheduleWithFixedDelay(this::report, 30, 60, TimeUnit.SECONDS);
    }

    @Override
    public void report() {
        Map<String, org.apache.hudi.com.codahale.metrics.Gauge> map = this.getRegistry().getGauges();
        String clean_duration = map.getOrDefault("KafkaSource.clean.duration", () -> null).getValue().toString();
        String clean_numFilesDeleted = map.getOrDefault("KafkaSource.clean.numFilesDeleted", () -> null).getValue().toString();
        String deltacommit_commitTime = map.getOrDefault("KafkaSource.deltacommit.commitTime", () -> null).getValue().toString();
        String deltacommit_duration = map.getOrDefault("KafkaSource.deltacommit.duration", () -> null).getValue().toString();
        String deltacommit_totalBytesWritten = map.getOrDefault("KafkaSource.deltacommit.totalBytesWritten", () -> null).getValue().toString();
        String deltacommit_totalCompactedRecordsUpdated = map.getOrDefault("KafkaSource.deltacommit.totalCompactedRecordsUpdated", () -> null).getValue().toString();
        String deltacommit_totalCreateTime = map.getOrDefault("KafkaSource.deltacommit.totalCreateTime", () -> null).getValue().toString();
        String deltacommit_totalFilesInsert = map.getOrDefault("KafkaSource.deltacommit.totalFilesInsert", () -> null).getValue().toString();
        String deltacommit_totalFilesUpdate = map.getOrDefault("KafkaSource.deltacommit.totalFilesUpdate", () -> null).getValue().toString();
        String deltacommit_totalInsertRecordsWritten = map.getOrDefault("KafkaSource.deltacommit.totalInsertRecordsWritten", () -> null).getValue().toString();
        String deltacommit_totalLogFilesCompacted = map.getOrDefault("KafkaSource.deltacommit.totalLogFilesCompacted", () -> null).getValue().toString();
        String deltacommit_totalLogFilesSize = map.getOrDefault("KafkaSource.deltacommit.totalLogFilesSize", () -> null).getValue().toString();
        String deltacommit_totalRecordsWritten = map.getOrDefault("KafkaSource.deltacommit.totalRecordsWritten", () -> null).getValue().toString();
        String deltacommit_totalUpdateRecordsWritten = map.getOrDefault("KafkaSource.deltacommit.totalUpdateRecordsWritten", () -> null).getValue().toString();
        String deltacommit_totalUpsertTime = map.getOrDefault("KafkaSource.deltacommit.totalUpsertTime", () -> null).getValue().toString();
        String finalize_duration = map.getOrDefault("KafkaSource.finalize.duration", () -> null).getValue().toString();
        String finalize_numFilesFinalized = map.getOrDefault("KafkaSource.finalize.numFilesFinalized", () -> null).getValue().toString();
        String index_INSERT_duration = map.getOrDefault("KafkaSource.index.INSERT.duration", () -> null).getValue().toString();
        System.err.println("clean_duration:"+clean_duration);
        System.err.println("clean_numFilesDeleted:"+clean_numFilesDeleted);
        System.err.println("deltacommit_commitTime:"+deltacommit_commitTime);
        System.err.println("deltacommit_duration:"+deltacommit_duration);
        System.err.println("deltacommit_totalBytesWritten:"+deltacommit_totalBytesWritten);
        System.err.println("deltacommit_totalCompactedRecordsUpdated:"+deltacommit_totalCompactedRecordsUpdated);
        System.err.println("deltacommit_totalCreateTime:"+deltacommit_totalCreateTime);
        System.err.println("deltacommit_totalFilesInsert:"+deltacommit_totalFilesInsert);
        System.err.println("deltacommit_totalFilesUpdate:"+deltacommit_totalFilesUpdate);
        System.err.println("deltacommit_totalInsertRecordsWritten:"+deltacommit_totalInsertRecordsWritten);
        System.err.println("deltacommit_totalLogFilesCompacted:"+deltacommit_totalLogFilesCompacted);
        System.err.println("deltacommit_totalLogFilesSize:"+deltacommit_totalLogFilesSize);
        System.err.println("deltacommit_totalRecordsWritten:"+deltacommit_totalRecordsWritten);
        System.err.println("deltacommit_totalUpdateRecordsWritten:"+deltacommit_totalUpdateRecordsWritten);
        System.err.println("deltacommit_totalUpsertTime:"+deltacommit_totalUpsertTime);
        System.err.println("finalize_duration:"+finalize_duration);
        System.err.println("finalize_numFilesFinalized:"+finalize_numFilesFinalized);
        System.err.println("index_INSERT_duration:"+index_INSERT_duration);

//        this.getRegistry().getGauges().forEach((key, value) ->
//                log.warn("key: " + key + " value: " + value.getValue().toString()));
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
