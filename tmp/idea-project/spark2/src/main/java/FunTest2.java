import java.util.*;
import java.util.function.*;
import java.util.stream.Collector;

public class FunTest2 {
    public static void main(String[] args) {
        List<String> data = Arrays.asList("hello", "world", "hello");
        Set<String> result = data.stream().collect(new MySetCollector<>());
        System.out.println(result);


    }


    public static void print(String content) {
        System.out.println(content);
    }
}


class MySetCollector<T> implements Collector<T, Set<T>, Set<T>> {

    @Override
    public Supplier<Set<T>> supplier() {
        System.out.println("supplier invoked");
        return HashSet::new;
    }

    @Override
    public BiConsumer<Set<T>, T> accumulator() {
        System.out.println("accumulator invoked");
//		return HashSet<T>::add;   // 报错
        /**
         * 作为accumulator而言，它能明确的仅仅是supplier提供的结果容器类型是Set类型，
         * 而不知道supplier提供的具体结果容器类型（这里是HashSet）。
         * 如果supplier提供的结果容器类型是TreeSet类型，
         * 那么accumulator使用HashSet提供的add方法就会出错。
         * 因此这里应该使用Set提供的add方法。
         */
//        return new BiConsumer<Set<T>, T>() {
//            @Override
//            public void accept(Set<T> ts, T t) {
//                ts.add(t);
//            }
//        };
        BiConsumer<Set<T>, T> tmp = Set<T>::add;
//        BiConsumer<test2> tmp2 = test2::add;
        ComparisonProvider tmp3 = test2::add;
        return Set<T>::add;

    }

    @Override
    public BinaryOperator<Set<T>> combiner() {
        System.out.println("combiner invoked");
        return (set1, set2) -> {
            set1.addAll(set2);
            return set1;
        };
    }

    @Override
    public Function<Set<T>, Set<T>> finisher() {
        System.out.println("finisher invoked");
        return Function.identity();
//        return t -> t;
    }

    @Override
    public Set<Characteristics> characteristics() {
        System.out.println("characteristics invoked");
        // 结果容器类型和最终结果类型一致，设置IDENTITY_FINISH特性
        Set<Characteristics> tmp = new HashSet<>();
        tmp.add(Characteristics.UNORDERED);
        // Collections.unmodifiableSet:不可变集合
        // return tmp;
        return Collections.unmodifiableSet(EnumSet
                .of(Characteristics.UNORDERED));
    }

}

class test2 {
    public int add() {
        return 1;
    }
}

interface ComparisonProvider {
    void accept(test2 t);

}