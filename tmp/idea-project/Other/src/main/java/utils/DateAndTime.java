package utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Set;

public class DateAndTime {
    public static void TimeTest() throws ParseException {
        //Instant用于表示一个时间戳，它与System.currentTimeMillis()有些类似，不过Instant可以精确到纳秒（Nano-Second），System.currentTimeMillis()方法只精确到毫秒
        Instant instant = Instant.now();//当前时间戳

        //获取所有合法的“区域/城市”字符串 :
        Set<String> zoneIds = ZoneId.getAvailableZoneIds();
        //获取系统默认时区 :
        ZoneId systemZoneId = ZoneId.systemDefault();
        //创建时区 ：
        ZoneId shanghaiZoneId = ZoneId.of("Africa/Bangui");

        //LocalDateTime：包含日期和时间
        LocalDateTime aaa = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());//从Instant获取
        //获取当前时间 ：
        LocalDateTime localDateTime = LocalDateTime.now();//2019-01-21T16:15:52.863
        //创建特定日期 (多种自定义)：
        LocalDateTime localDateTime1 = LocalDateTime.of(2019, 01, 21, 16, 22, 34);
        LocalDateTime localDateTime2 = LocalDateTime.of(2021, Month.APRIL, 25, 15, 56, 0);
        //获取获取年、月、日信息
        LocalDateTime.now().getYear();//2019
        LocalDateTime.now().getMonth();//JANUARY
        LocalDateTime.now().getDayOfYear();//21
        LocalDateTime.now().getDayOfMonth();//21
        LocalDateTime.now().getDayOfWeek();//MONDAY
        LocalDateTime.now().getHour();//16

        //Duration : 计算两个“时间”的间隔
        Duration duration = Duration.between(localDateTime1, aaa);
        long days = duration.toDays();              // 这段时间的总天数
        long hours = duration.toHours();            // 这段时间的小时数
        long minutes = duration.toMinutes();        // 这段时间的分钟数
        long seconds = duration.getSeconds();       // 这段时间的秒数
        long milliSeconds = duration.toMillis();    // 这段时间的毫秒数
        long nanoSeconds = duration.toNanos();      // 这段时间的纳秒数

        //Period : 用于计算两个“日期”的间隔
        //由于Period是以年月日衡量时间段，所以between()方法只能接收LocalDate类型的参数
        Period period = Period.between(
                LocalDate.of(2019, 1, 21),
                LocalDate.of(2019, 2, 21));


        //使用plus方法增加年份
        //改变时间后会返回一个新的实例nextYearTime
        LocalDateTime time1 = LocalDateTime.of(2017, 1, 1, 1, 1,1);
        LocalDateTime nextYearTime = time1.plusYears(1);
        System.out.println(nextYearTime); //2018-01-01T01:01:01

        //使用minus方法减年份
        LocalDateTime time2 = LocalDateTime.of(2017, 1, 1, 1, 1, 1);
        LocalDateTime lastYearTime = time2.minusYears(1);
        System.out.println(lastYearTime); //2016-01-01T01:01:01

        //使用with方法设置月份
        LocalDateTime time3 = LocalDateTime.of(2017, 1, 1, 1, 1, 1);
        LocalDateTime changeTime = time3.withMonth(12);
        System.out.println(changeTime); //2017-12-01T01:01:01

        //判断当前日期属于星期几
        LocalDateTime time = LocalDateTime.now();
        DayOfWeek dayOfWeek = time.getDayOfWeek();
        System.out.println(dayOfWeek); //WEDNESDAY
        //LocalDate和LocalTime与LocalDateTime类似

        //Calendar 类
        //获取时间
        // 使用默认时区和语言环境获得一个日历
        Calendar cal = Calendar.getInstance();
        // 赋值时年月日时分秒常用的6个值，注意月份下标从0开始，所以取月份要+1
        System.out.println("年:" + cal.get(Calendar.YEAR));
        System.out.println("月:" + (cal.get(Calendar.MONTH) + 1));
        System.out.println("日:" + cal.get(Calendar.DAY_OF_MONTH));
        System.out.println("时:" + cal.get(Calendar.HOUR_OF_DAY));
        System.out.println("分:" + cal.get(Calendar.MINUTE));
        System.out.println("秒:" + cal.get(Calendar.SECOND));
        //设置时间
        Calendar cal2 = Calendar.getInstance();
        // 如果想设置为某个日期，可以一次设置年月日时分秒，由于月份下标从0开始赋值月份要-1
        // cal.set(year, month, date, hourOfDay, minute, second);
        cal2.set(2018, 1, 15, 23, 59, 59);
        // 或者6个字段分别进行设置，由于月份下标从0开始赋值月份要-1
        cal2.set(Calendar.YEAR, 2018);
        cal2.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal2.set(Calendar.DAY_OF_MONTH, 15);
        cal2.set(Calendar.HOUR_OF_DAY, 23);
        cal2.set(Calendar.MINUTE, 59);
        cal2.set(Calendar.SECOND, 59);
        System.out.println(cal2.getTime());
        //者用 Date 来初始化 Calendar 对象
        cal2.setTime(new Date());

        //时间计算
        Calendar cal3 = Calendar.getInstance();
        cal3.set(2018, 1, 15, 23, 59, 59);
        System.out.println(cal.getTime());
        cal3.add(Calendar.SECOND, 1);
        cal.add(Calendar.YEAR, -1); // 年份减1
        cal.add(Calendar.YEAR, +1); // 年份加1
        cal.add(Calendar.MONTH, -1);// 月份减1
        cal.add(Calendar.DATE, -1);// 日期减1
        cal.set(Calendar.HOUR, 15);//设置时为  15点
        cal.set(Calendar.MINUTE, 45);//设置分为 45
        cal.set(Calendar.SECOND, 30);//设置 秒为30
        System.out.println(cal3.getTime());



        //时间类与Date类的相互转化
        //Date与Instant的相互转化
        Instant instant3  = Instant.now();
        Date date = Date.from(instant3);
        Instant instant2 = date.toInstant();

        //Date转为LocalDateTime
        Date date2 = new Date();
        LocalDateTime localDateTime4 = LocalDateTime.ofInstant(date2.toInstant(), ZoneId.systemDefault());

        //LocalDateTime转Date
        LocalDateTime localDateTime3 = LocalDateTime.now();
        Instant instant4 = localDateTime3.atZone(ZoneId.systemDefault()).toInstant();
        Date date3 = Date.from(instant4);

        //LocalDate转Date
        //因为LocalDate不包含时间，所以转Date时，会默认转为当天的起始时间，00:00:00
        LocalDate localDate4 = LocalDate.now();
        Instant instant5 = localDate4.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant();
        Date date4 = Date.from(instant5);

        //Date转String
        //使用Date和SimpleDateFormat
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("G yyyy年MM月dd号 E a hh时mm分ss秒");
        String format = simpleDateFormat.format(new Date());
        System.out.println(format);
        //打印: 公元 2017年03月21号 星期二 下午 06时38分20秒

        //使用jdk1.8 LocalDateTime和DateTimeFormatter
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter pattern = DateTimeFormatter.ofPattern("G yyyy年MM月dd号 E a hh时mm分ss秒");
        String format2 = now.format(pattern);
        System.out.println(format2);
        //打印: 公元 2017年03月21号 星期二 下午 06时38分20秒

        //String转Date
        //使用Date和SimpleDateFormat
        SimpleDateFormat simpleDateFormat2 = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        Date date5 = simpleDateFormat2.parse("2017-12-03 10:15:30");
        System.out.println(simpleDateFormat.format(date5));
        //打印 2017-12-03 10:15:30

        //使用jdk1.8 LocalDateTime和DateTimeFormatter
        DateTimeFormatter pattern3 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        //严格按照ISO yyyy-MM-dd验证，03写成3都不行
        LocalDateTime dt = LocalDateTime.parse("2017-12-03 10:15:30",pattern3);
        System.out.println(dt.format(pattern3));

    }
}
