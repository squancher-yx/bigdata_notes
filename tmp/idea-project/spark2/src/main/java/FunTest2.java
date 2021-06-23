import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.*;
import java.util.stream.Collector;

public class FunTest2 {
    public static void main(String[] args) throws Exception {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = new Date(1623218880000L);
        System.out.println(df.format(date));

        df.setTimeZone(TimeZone.getTimeZone("UTC"));
        Date date2 = df.parse("2021-06-16T07:33:00.000Z");

        LocalDateTime localDateTime4 = LocalDateTime.ofInstant(date2.toInstant(), ZoneId.of("+08:00"));

        System.out.println(localDateTime4.toString()+"Z");
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        System.out.println(localDateTime4.format(pattern));


        SimpleDateFormat df2 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date3 = new Date(Long.parseLong("1623998400000"));
        System.out.println(df2.format(date3));



    }

}

