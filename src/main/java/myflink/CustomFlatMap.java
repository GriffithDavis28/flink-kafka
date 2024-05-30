package myflink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.util.Collector;
import org.apache.flink.api.java.tuple.Tuple2;
import org.jetbrains.annotations.NotNull;

public class CustomFlatMap implements FlatMapFunction<String, Tuple2<String, Integer>> {
    @Override
    public void flatMap(@NotNull String s, Collector<Tuple2<String, Integer>> collector) throws Exception {
        for (String word : s.split("\\s")){
            collector.collect(Tuple2.of(word, 1));
        }
    }
}
