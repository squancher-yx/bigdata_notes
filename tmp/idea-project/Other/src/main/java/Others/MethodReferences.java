package Others;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.function.Supplier;

/**
 * 函数引用（双冒号）：用于已具有名称的方法的紧凑、易于阅读的 lambda 表达式（已实现的方法）。
 * <p>
 * 引用一个静态方法： ContainingClass :: staticMethodName
 * <p>
 * 引用一个特定对象 的 实例方法:  containingObject :: instanceMethodName
 * <p>
 * 引用特定类型的任意对象的实例方法： ContainingType :: methodName
 * <p>
 * 引用构造函数： ClassName :: new
 */
public class MethodReferences {
    public static void main(String[] args) {

        //  1.静态方法引用
        Comparator<Person> comparatorStatic = Person::compareByAge;
        TmpClass comparatorStatic2 = Person::compareByAge;

        //  2.特定对象的实例方法引用
        ComparisonProvider provider = new ComparisonProvider();
        Comparator<Person> comparatorSpecific = provider::compareByName;
        TmpClass comparatorSpecific2 = provider::compareByName;
        Person a = new Person();
        a.setName("qqq");
        Person b = new Person();
        b.setName("www");
        System.out.println(comparatorSpecific2.testMethod(a, b));

        // 3.特定类型的任意对象的实例方法引用，官方文档及其它资料未找到全面解释
        String[] stringArray = {"Barbara", "James", "Mary", "John",
                "Patricia", "Robert", "Michael", "Linda"};
        Arrays.sort(stringArray, String::compareToIgnoreCase);

        Arbitraryobject tmp2 = test3::add;

        // 4.构造函数引用
        Supplier<HashSet<Person>> supplier = HashSet::new;
        Collection<Person> tmp = supplier.get();
    }
}

class test3 {
    public int add(int b) {
        return 1;
    }
}

interface Arbitraryobject {
    // 第一个参数必须是实现类，后面参数与实现方法相对应
    void accept(test3 t3, int b);
}

interface TmpClass {
    int testMethod(Person a, Person b);
}

class Person {

    public LocalDate getBirthday() {
        return birthday;
    }

    public void setBirthday(LocalDate birthday) {
        this.birthday = birthday;
    }

    private LocalDate birthday;
    String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public static int compareByAge(Person a, Person b) {
        return a.birthday.compareTo(b.birthday);
    }
}

class ComparisonProvider {
    public int compareByName(Person a, Person b) {
        return a.getName().compareTo(b.getName());
    }

    public int compareByAge(Person a, Person b) {
        return a.getBirthday().compareTo(b.getBirthday());
    }
}