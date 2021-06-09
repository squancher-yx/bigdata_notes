package Others;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * 数组，Collection（I/O channel， 产生器 generator ?）
 */
public class StreamDemo {
    public static void main(String[] args) {
        List<String> strings = Arrays.asList("abc", "", "bc", "efg", "abcd", "", "jkl");
//        int[] a = new int[10];
//        IntStream tmp = Arrays.stream(a);
        Stream<String> stream = strings.parallelStream().map(a -> a + "").filter(String::isEmpty);
//        long count = strings.parallelStream().filter(String::isEmpty).count();
        List<String> data = Arrays.asList("hello", "world", "hello");
        // 使用自定义 Collector
        Object[] result = data.stream().collect(new MySetCollector<>());
        Arrays.stream(result).forEach(System.out::println);
        // 使用 Collectors
        List<String> ll = data.stream().collect(Collectors.toCollection(LinkedList::new));
        //默认为ArrayList
        List<String> ll2 = data.stream().collect(Collectors.toList());
        // 默认为HashSet
        Set<String> ss = data.stream().collect(Collectors.toSet());
    }
}

/**
 * 自定义 Collector
 * Collector<T, A, R>
 * T（输入的元素类型）：T
 * A（累积结果的容器类型）：Set<T>
 * R（最终生成的结果类型）：T[]
 *
 * @param <T>
 */
class MySetCollector<T> implements Collector<T, Set<T>, Object[]> {

    /**
     * supplier参数用于生成结果容器，容器类型为A
     *
     * @return
     */
    @Override
    public Supplier<Set<T>> supplier() {
        System.out.println("supplier invoked");
        return HashSet::new;
    }

    /**
     * accumulator用于消费元素，也就是归纳元素，这里的T就是元素，它会将流中的元素一个一个与结果容器A发生操作
     *
     * @return
     */
    @Override
    public BiConsumer<Set<T>, T> accumulator() {
        System.out.println("accumulator invoked");
        return Set::add;

    }

    /**
     * combiner用于两个两个合并并行执行的线程的执行结果，将其合并为一个最终中间结果A
     *
     * @return
     */
    @Override
    public BinaryOperator<Set<T>> combiner() {
        System.out.println("combiner invoked");
        return (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        };
    }

    /**
     * finisher用于将之前整合完的结果A转换成为R
     *
     * @return
     */
    @Override
    public Function<Set<T>, Object[]> finisher() {
        System.out.println("finisher invoked");
//        return Function.identity();   // return t -> t;

        return t -> t.toArray();
    }

    /**
     * Characteristics：这个特征值是一个枚举，拥有三个值：CONCURRENT（多线程并行），UNORDERED（无序），IDENTITY_FINISH（无需转换结果）。其中四参of方法中没有finisher参数，所以必有IDENTITY_FINISH特征值。
     * 如果Collector实现中没有IDENTITY_FINISH特性，才会调用实现类中的finisher()方法，否则直接将中间结果容器强转成最终的结果类型。
     * @return
     */
    @Override
    public Set<Characteristics> characteristics() {
        System.out.println("characteristics invoked");
        // 结果容器类型和最终结果类型一致，设置IDENTITY_FINISH特性
        // Collections.unmodifiableSet:不可变集合
//        return Collections.unmodifiableSet(EnumSet
//                .of(Collector.Characteristics.IDENTITY_FINISH));
        return Collections.emptySet();
    }

}
