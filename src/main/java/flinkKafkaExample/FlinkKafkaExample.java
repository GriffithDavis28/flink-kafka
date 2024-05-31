package flinkKafkaExample;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.configuration.CheckpointingOptions;
import org.apache.flink.configuration.Configuration;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer011;

import javax.security.auth.login.AppConfigurationEntry;
import java.util.Properties;

public class FlinkKafkaExample {



    public static void main(String[] args) throws Exception {
        String inputTopic = "flink_input";
        String outputTopic = "flink_output";
        String consumerGroup = "SPARTANS";
        String address = "localhost:9092";

        final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
        environment.enableCheckpointing(5000);

        environment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

        environment.setStateBackend(new FsStateBackend("file:///home/davisgriffith/Analytics/project/checkpoints"));

        FlinkKafkaConsumer<String> consumer = createStringConsumer(inputTopic, address, consumerGroup);

        DataStream<String> data = environment.addSource(consumer);

        StreamingFileSink<String> sink = StreamingFileSink
                .forRowFormat(new Path("file:///home/davisgriffith/Analytics/project/my-flink-project"), new SimpleStringEncoder())
                .build();

        FlinkKafkaProducer<String> producer = createStringProducer(outputTopic, address);

        data.map(new Capitalizer())
                .addSink(producer);

        data.addSink(sink);

        data.writeAsText("///home/davisgriffith/Analytics/project/consumedData.txt", FileSystem.WriteMode.OVERWRITE).setParallelism(1);

        environment.execute();
    }

    public static FlinkKafkaConsumer<String> createStringConsumer(String topic, String kafkaAddress, String kafkaGroup){
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafkaAddress);
        props.setProperty("group.id", kafkaGroup);
        FlinkKafkaConsumer<String> consumer = new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), props);

        return consumer;
    }

    public static FlinkKafkaProducer<String> createStringProducer(String topic, String kafkaAddress){
        return  new FlinkKafkaProducer<>(kafkaAddress, topic, new SimpleStringSchema());
    }


//    public static void capialize() throws Exception {
//        String inputTopic = "flink_input";
//        String outputTopic = "flink_output";
//        String consumerGroup = "SPARTANS";
//        String address = "localhost:9092";
//
//        StreamExecutionEnvironment env = StreamExecutionEnvironment.getExecutionEnvironment();
//
//        FlinkKafkaConsumer<String> consumer = createStringConsumer(inputTopic, address, consumerGroup);
//
//        DataStream<String> data = env.addSource(consumer);
//
//        FlinkKafkaProducer<String> producer = createStringProducer(outputTopic, address);
//
//        data.map(new Capitalizer())
//                .addSink(producer);
//
//        env.execute();
//    }
}
