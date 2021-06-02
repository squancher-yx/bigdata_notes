package Others;

import java.util.EnumSet;

/**
 * 一个枚举类型是一种特殊类型的数据使得能够为一个变量是一组预定义的常量。该变量必须等于为其预定义的值之一。常见示例包括罗盘方向（北、南、东和西的值）和一周中的几天。
 * <p>
 * 因为它们是常量，所以枚举类型的字段的名称是大写字母。
 * <p>
 * 在 Java 编程语言中，您可以使用enum关键字定义枚举类型。
 * <p>
 * EnumSet是所有Set实现类中性能最好的，但它只能保存同一个枚举类的枚举值做为集合元素。
 */
public class EnumAndEnumSet {
    Day day;

    public EnumAndEnumSet(Day day) {
        this.day = day;
    }

    public void tellItLikeItIs() {
        switch (day) {
            case MONDAY:
                System.out.println("Mondays are bad.");
                break;

            case FRIDAY:
                System.out.println("Fridays are better.");
                break;

            case SATURDAY:
            case SUNDAY:
                System.out.println("Weekends are best.");
                break;

            default:
                System.out.println("Midweek days are so-so.");
                break;
        }
    }

    public static void main(String[] args) {
        EnumAndEnumSet firstDay = new EnumAndEnumSet(Day.MONDAY);
        firstDay.tellItLikeItIs();
        EnumAndEnumSet thirdDay = new EnumAndEnumSet(Day.WEDNESDAY);
        thirdDay.tellItLikeItIs();
        EnumAndEnumSet fifthDay = new EnumAndEnumSet(Day.FRIDAY);
        fifthDay.tellItLikeItIs();
        EnumAndEnumSet sixthDay = new EnumAndEnumSet(Day.SATURDAY);
        sixthDay.tellItLikeItIs();
        EnumAndEnumSet seventhDay = new EnumAndEnumSet(Day.SUNDAY);
        seventhDay.tellItLikeItIs();

        EnumSet<Day> es = EnumSet.allOf(Day.class);
        EnumSet<Day> es3 = EnumSet.of(Day.TUESDAY, Day.THURSDAY);
    }
}

enum Day {
    SUNDAY, MONDAY, TUESDAY, WEDNESDAY,
    THURSDAY, FRIDAY, SATURDAY
}