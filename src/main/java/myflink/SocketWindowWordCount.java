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
        env.enableCheckpointing(8000);
        //Creating a data stream of type string
        DataStream<String> text =  env.socketTextStream("localhost", 9000, "\n");

        //Counting number of words
        DataStream<Tuple2<String, Integer>> windowCountsKeyBy = text.flatMap(new CustomFlatMap());

        DataStream<String> windowCounts = windowCountsKeyBy.keyBy(0)
        .timeWindow(Time.seconds(5))
        .sum(1).map(a -> a.f0 + " " + a.f1);

        StreamingFileSink<String> sinkExample = StreamingFileSink
                .forRowFormat(new Path("file:///home/davisgriffith/Analytics/project/test.txt"), new SimpleStringEncoder())
                        .build();

        //Print results
        windowCounts.print().setParallelism(1);
        windowCounts.writeAsText("testing/test1.txt", FileSystem.WriteMode.OVERWRITE).setParallelism(1);
        windowCounts.print();
        windowCounts.addSink(sinkExample);
        env.execute("Socket Window WordCount");
    }
}
