package com.xinyan.trust.stream;

import org.junit.Ignore;
import org.junit.Test;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasSize;

/**
 * @ClassName TestStream
 * @Description
 * @Author jingyun_liu
 * @Date 2019/8/13 15:25
 * @Version V1.0
 **/
public class TestStream {

    @Test
    public void testStream() {
        //1.Collection的默认方法stream()和parllelStream()
        List<String> list = Arrays.asList("1", "2a", "3c");
        Stream<String> stream = list.stream();
        Stream<String> parallelStream = list.parallelStream();
        stream.forEach(System.out::println);
        //2. Arrays.stream
        IntStream intStream = Arrays.stream(new int[]{45, 2, 3});
        Stream<Integer> integerStream = Arrays.stream(new Integer[]{45, 2, 3});

        //3.
        Stream<int[]> stream1 = Stream.of(new int[]{2, 4, 12});
        IntStream intStream1 = IntStream.of(new int[]{2, 4, 12});
        //4.迭代无限流 limit限制
        Stream.iterate(1, n -> n + 1).limit(100).forEach(System.out::println);

        //5.生成无限流
        Stream.generate(Math::random).limit(2).forEach(System.out::println);


    }

    //筛选和切片
    @Test
    public void testFilter() {
        //filter(Predicate<T> p)：过滤(根据传入的Lambda返回的ture/false 从流中过滤掉某些数据(筛选出某些数据))
        Arrays.asList(1, 3, 4, 5, 6, 4, 7, 9, 10).stream().filter(i -> i % 2 == 0).forEach(System.out::println);
        System.out.println("-----------------");
        //distinct()：去重(根据流中数据的 hashCode和 equals去除重复元素)
        Arrays.asList(1, 3, 4, 5, 6, 4, 7, 7, 9, 10).stream().filter(i -> i % 2 != 0).distinct().forEach(System.out::println);
        System.out.println("-----------------");
        //limit(long n)：限定保留n个数据 保留前n个
        Arrays.asList(1, 3, 4, 5, 6, 4, 7, 7, 9, 10).stream().filter(i -> i % 2 != 0).limit(3).forEach(System.out::println);
        System.out.println("-----------------");
        //skip(long n)：跳过n个数据 跳过从第一个开始 到第n个 保留后面的数据
        Arrays.asList(1, 3, 4, 5, 6, 4, 7, 7, 9, 10).stream().filter(i -> i % 2 != 0).skip(3).forEach(System.out::println);
    }

    //映射map(Function<T, R> f)：接收一个函数作为参数，该函数会被应用到流中的每个元素上，并将其映射成一个新的元素。
    //flatMap(Function<T, Stream<R>> mapper)：接收一个函数作为参数，将流中的每个值都换成另一个流，然后把所有流连接成一个流
    @Test
    public void testMap() {
        //映射map(Function<T, R> f)：接收一个函数作为参数，该函数会被应用到流中的每个元素上，并将其映射成一个新的元素。
        Arrays.asList("a", "+", "B").stream().map(i -> i.toUpperCase()).forEach(System.out::println);
        System.out.println("-----------------");
        //flatMap(Function<T, Stream<R>> mapper)：接收一个函数作为参数，将流中的每个值都换成另一个流，然后把所有流连接成一个流
        Stream<List<String>> stream = Stream.of(Arrays.asList("A", "liujingyun", "wqe"), Arrays.asList("a", "+", "B"));
        stream.map(i -> i.stream()).forEach(System.out::println);
        System.out.println("-----------------");

    }

    //排序
    @Test
    public void testSorded() {
        //自然排序
        List<String> a = new ArrayList<>();
        Arrays.asList("3", "2", "1").stream().sorted().forEach(x->{
            a.add(x);
        });

        System.out.println("-----------------");
        //定制排序 将0默认放在最后 其他升序排序
        Arrays.asList(3, 0, 1, 2).stream().sorted((x, y) -> {
            if (x == 0 ) {
                return 1;
            }
            if(y == 0){
                return -1;
            }
            return x.compareTo(y);
        }).forEach(System.out::println);
    }

    //收集流
    @Test
    public void testOption() {
        System.out.println("------检查是否匹配所有-----------");
        boolean b = Arrays.asList(1, 3, 5, 1, 11, 4).stream().allMatch(x -> x > 1);
        System.out.println(b);
        System.out.println("--------检查是否至少匹配一个元素---------");
        boolean b1 = Arrays.asList(1, 3, 5, 1, 11, 4).stream().anyMatch(x -> x > 10);
        System.out.println(b1);
        System.out.println("--------返回流中的第一个元素---------");
        Optional<Integer> integer = Arrays.asList(1, 3, 5, 1, 11, 4).stream().findFirst();
        System.out.println(integer.get());
        System.out.println("--------返回流中的任意 元素---------");
        Optional<Integer> any = Arrays.asList(1, 3, 5, 1, 11, 4).stream().findAny();
        System.out.println(any.get());
        //统计
        long l = Arrays.asList(1, 3, 5, 1, 11, 4).stream().distinct().count();
        System.out.println(l);
        Optional<Integer> min = Arrays.asList(1, 3, 5, 1, 11, 4).stream().min((x, y) -> x.compareTo(y));
        System.out.println(min.get());
        Optional<Integer> max = Arrays.asList(1, 3, 5, 1, 11, 4).stream().max((x, y) -> x.compareTo(y));
        System.out.println(max.get());
    }

    //归约
    @Test
    public void testReduce() {
        Integer reduce = Arrays.asList(1, 3, 5, 1, 11, 4).stream().reduce(0, (x, y) -> x + y);
        System.out.println(reduce);
    }

    //汇总 规约
    @Test
    public void testCollect() {
        List<String> collect = Arrays.asList("a", "+", "B").stream().map(i -> i.toUpperCase()).collect(Collectors.toList());
        System.out.println(collect);
    }

    @Test
    public void list() {

        Integer MAXAGE = 5;
        Integer MINAGE = 3;
        List<Integer> integers = Arrays.asList(1, 3, 5, 1, 11, 4);

        Collections.sort(integers, new Comparator<Integer>() {
            @Override
            public int compare(Integer u1, Integer u2) {
                if (MAXAGE.equals(u1)) {
                    return 1;
                } else if (MINAGE.equals(u1)) {
                    return -1;
                } else {
                    return u1.compareTo(u2);
                }
            }
        });
        System.out.println(integers);
    }

    @Test
    public void test1(){
//        List<Integer> list =  new ArrayList<>(Arrays.asList(null, 1, 2, null, 3, null));
//
//        list.removeIf(Objects::isNull);
//
//        assertThat(list, hasSize(3));
    }
}
