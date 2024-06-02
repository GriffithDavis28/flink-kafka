package flinkKafkaExample;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.core.fs.FileSystem;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import java.util.Properties;

public class FlinkKafkaExample {

    public static void main(String[] args) throws Exception {
        try{
            String inputTopic = "flink_input";
            String outputTopic = "flink_output";
            String consumerGroup = "SPARTANS";
            String address = "localhost:9092";

            final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();
            environment.enableCheckpointing(5000);

            environment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

            environment.setStateBackend(new FsStateBackend("file:///home/griffith/IdeaProjects/checkpoints"));

            FlinkKafkaConsumer<String> consumer = createStringConsumer(inputTopic, address, consumerGroup);

            DataStream<String> data = environment.addSource(consumer);

            DataStream<String> filteredData = data.filter(value-> true);

            StreamingFileSink<String> sink = StreamingFileSink
                    .forRowFormat(new Path("file:///home/griffith/IdeaProjects/flink"), new SimpleStringEncoder())
                    .build();

            FlinkKafkaProducer<String> producer = createStringProducer(outputTopic, address);

            data.addSink(producer);

            data.addSink(sink);

            data.writeAsText("///home/griffith/IdeaProjects/flink/consumedData.txt", FileSystem.WriteMode.OVERWRITE).setParallelism(1);
            filteredData.writeAsText("///home/griffith/IdeaProjects/flink/filteredData.txt", FileSystem.WriteMode.OVERWRITE).setParallelism(1);
            environment.execute("example");
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
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

    public static String extractfield(String json){
        try{
            ObjectMapper obj = new ObjectMapper();
            JsonNode node = obj.readTree(json);

            if(node.has("name")){
                return  node.get("name").asText();
            }
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
        return null;
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
