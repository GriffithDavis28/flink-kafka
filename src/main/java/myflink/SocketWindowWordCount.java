package myflink;

import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.windowing.time.Time;
import org.apache.flink.util.Collector;

public class SocketWindowWordCount {

    public static void main(String[] args) throws Exception{

        final StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
        env.enableCheckpointing(3000);
        //Creating a data stream of type string
        DataStream<String> text =  env.socketTextStream("localhost", 9000, "\n");

        //Counting number of words
        DataStream<Tuple2<String, Integer>> windowCountsKeyBy = text.flatMap(new CustomFlatMap());

        DataStream<String> windowCounts = windowCountsKeyBy.keyBy(0)
        .timeWindow(Time.seconds(5))
        .sum(1).map(a -> a.f0 + " " + a.f1);

        StreamingFileSink<String> sinkExample = StreamingFileSink
                .forRowFormat(new Path("file:///home/davisgriffith/Documents/test1.txt"), new SimpleStringEncoder())
                        .build();

        //Print results
//        windowCounts.print().setParallelism(1);
        //windowCounts.writeAsText("~/home/Documents/test1.txt", FileSystem.WriteMode.NO_OVERWRITE).setParallelism(1);
        windowCounts.addSink(sinkExample);
        env.execute("Socket Window WordCount");
    }
}
