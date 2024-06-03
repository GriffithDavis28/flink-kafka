package flinkKafkaExample;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.OutputTag;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class FlinkKafkaExample {

    private static final Logger logger = LoggerFactory.getLogger(FlinkKafkaExample.class);
    public static OutputTag<String> faultOutputTag =  new OutputTag<String>("fault-output"){};

    public static void main(String[] args) {

        try {
            if (args.length < 1) {
                logger.error("Send at least one argument");
                return;
            }

            Properties config = new Properties();
            try (InputStream input = new FileInputStream(args[0])) {
                logger.info("Loading configuration file: {}", args[0]);
                config.load(input);
            }

            // Assign properties from the configuration file
            String inputTopic = config.getProperty("inputTopic");
            String outputTopic = config.getProperty("outputTopic");
            String consumerGroup = config.getProperty("consumerGroup");
            String kafkaAddress = config.getProperty("kafkaAddress");
            String checkpoints = config.getProperty("checkpointDirectory");
            String fileInput = config.getProperty("inputFilePath");
            String fileDataPath = config.getProperty("fileDataPath");
            String errorPath = config.getProperty("errorPath");
            String filteredDataOutput = config.getProperty("filteredData");

            // Set the ERROR_PATH system property for Logback
            System.setProperty("ERROR_LOG", errorPath);

            // Setting execution environment
            final StreamExecutionEnvironment environment = StreamExecutionEnvironment.getExecutionEnvironment();

            // Enable checkpoints every 5 seconds
            environment.enableCheckpointing(5000);

            // Ensure checkpoints complete at least once
            environment.getCheckpointConfig().setCheckpointingMode(CheckpointingMode.EXACTLY_ONCE);

            // Set the state backend to save checkpoints
            environment.setStateBackend(new FsStateBackend(checkpoints));

            // Create a Kafka consumer
            FlinkKafkaConsumer<String> consumer = createStringConsumer(inputTopic, kafkaAddress, consumerGroup);

            // Store data after Kafka consumes data
            DataStream<String> data = environment.addSource(consumer);

            // Read data from a file
            DataStream<String> dataFromFile = environment.readTextFile(fileInput);

            // Filtering incoming data to get specific data
            DataStream<String> filteredData = dataFromFile.filter(value -> {
                try {
                    logger.info("Incoming data: {}", value);

                    JSONObject json = new JSONObject(value);
                    if (json.has("name") && json.has("")) {
                        logger.info("Filtered data: {}", json);
                        System.out.println(json);
                        return true;
                    }
                } catch (Exception e) {
                    logger.error("Error while filtering data: {}", value, e);
                }
                return false;
            });

            // Set up StreamingFileSink to write filtered data to a file
            StreamingFileSink<String> sink = StreamingFileSink
                    .forRowFormat(new Path(filteredDataOutput), new SimpleStringEncoder<String>("UTF-8"))
                    .withBucketAssigner(new DateTimeBucketAssigner<>())
                    .withRollingPolicy(
                            DefaultRollingPolicy.builder()
                                    .withInactivityInterval(Duration.ofMinutes(10))
                                    .withRolloverInterval(Duration.ofMinutes(2))
                                    .build()
                    )
                    .build();

            // Add sink to the filtered data
            filteredData.addSink(sink);

            // Create a Kafka producer
            FlinkKafkaProducer<String> producer = createStringProducer(outputTopic, kafkaAddress);

            // Add Kafka producer sink
            filteredData.addSink(producer);

            // Execute the Flink job
            environment.execute("Flink Kafka Example");

        } catch (Exception e) {
            logger.error("Error when executing job: {}", e.getMessage(), e);
        }
    }

    // Setting properties for Kafka consumer
    public static FlinkKafkaConsumer<String> createStringConsumer(String topic, String kafkaAddress, String kafkaGroup) {
        Properties props = new Properties();
        props.setProperty("bootstrap.servers", kafkaAddress);
        props.setProperty("group.id", kafkaGroup);
        return new FlinkKafkaConsumer<>(topic, new SimpleStringSchema(), props);
    }

    // Setting properties for Kafka producer
    public static FlinkKafkaProducer<String> createStringProducer(String topic, String kafkaAddress) {
        return new FlinkKafkaProducer<>(kafkaAddress, topic, new SimpleStringSchema());
    }
}
