import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

public class FunTest2 {
    public static void main(String[] args) throws ParseException {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        Date date = new Date(1623218880000L);
        System.out.println(df.format(date));
        df.setTimeZone(TimeZone.getTimeZone("UTC"));

        Date date2 = df.parse("2021-06-09T06:08:00.001Z");
        LocalDateTime localDateTime4 = LocalDateTime.ofInstant(date2.toInstant(), ZoneId.systemDefault());
        System.out.println(localDateTime4.toString()+"Z");
    }

}

