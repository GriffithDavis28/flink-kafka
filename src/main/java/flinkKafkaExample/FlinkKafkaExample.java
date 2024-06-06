package flinkKafkaExample;

import org.apache.flink.api.common.serialization.SimpleStringEncoder;
import org.apache.flink.api.common.serialization.SimpleStringSchema;
import org.apache.flink.core.fs.Path;
import org.apache.flink.runtime.state.filesystem.FsStateBackend;
import org.apache.flink.streaming.api.CheckpointingMode;
import org.apache.flink.streaming.api.datastream.DataStream;
import org.apache.flink.streaming.api.datastream.SideOutputDataStream;
import org.apache.flink.streaming.api.datastream.SingleOutputStreamOperator;
import org.apache.flink.streaming.api.environment.StreamExecutionEnvironment;
import org.apache.flink.streaming.api.functions.sink.filesystem.StreamingFileSink;
import org.apache.flink.streaming.api.functions.sink.filesystem.bucketassigners.DateTimeBucketAssigner;
import org.apache.flink.streaming.api.functions.sink.filesystem.rollingpolicies.DefaultRollingPolicy;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer;
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaProducer;
import org.apache.flink.util.OutputTag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileInputStream;
import java.io.InputStream;
import java.time.Duration;
import java.util.Properties;

public class FlinkKafkaExample {

    private static final Logger logger = LoggerFactory.getLogger(FlinkKafkaExample.class);
    public static OutputTag<String> faultOutputTag =  new OutputTag<String>("side-output"){};

    public static void main(String[] args) {

        try {
            if (args.length < 1) {
                logger.error("Send at least one argument");
                return;
            }

            Properties config = new Properties();
            // Load configuration from file
            try (InputStream input = new FileInputStream(args[0])) {
                logger.info("Loading configuration file: {}", args[0]);
                config.load(input);
            }

            // Assign properties from the configuration file
            String inputTopic = config.getProperty("inputTopic");
            String successTopic = config.getProperty("successTopic");
            String failureTopic = config.getProperty("failureTopic");
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
            environment.enableCheckpointing(10000);

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

            // Process data with a custom function to filter and tag
            SingleOutputStreamOperator<String> filteredStream = data.process(new JsonConversion());

            // Extract side output for error data
            SideOutputDataStream<String> errorStream = filteredStream.getSideOutput(faultOutputTag);

            // Set up StreamingFileSink to write filtered data to a file
            StreamingFileSink<String> validDataSink = StreamingFileSink
                    .forRowFormat(new Path(filteredDataOutput), new SimpleStringEncoder<String>("UTF-8"))
                    .withBucketAssigner(new DateTimeBucketAssigner<>())
                    .withRollingPolicy(
                            DefaultRollingPolicy.builder()
                                    .withInactivityInterval(Duration.ofMinutes(10))
                                    .withRolloverInterval(Duration.ofMinutes(2))
                                    .build()
                    )
                    .build();

            // Set up StreamingFileSink to write invalid data to a file
            StreamingFileSink<String> invalidDataSink = StreamingFileSink
                    .forRowFormat(new Path(errorPath), new SimpleStringEncoder<String>("UTF-8"))
                    .withBucketAssigner(new DateTimeBucketAssigner<>())
                    .withRollingPolicy(
                            DefaultRollingPolicy.builder()
                                    .withInactivityInterval(Duration.ofMinutes(5))
                                    .withRolloverInterval(Duration.ofMinutes(2))
                                    .build()
                    )
                    .build();

            // Add sink to the filtered data
            filteredStream.addSink(validDataSink);

            // Add sink to the error data
            errorStream.addSink(invalidDataSink);

            // Create a Kafka producer for valid data
            FlinkKafkaProducer<String> successProducer = createStringProducer(successTopic, kafkaAddress);

            // Create a Kafka producer for invalid data
            FlinkKafkaProducer<String> failureProducer = createStringProducer(failureTopic, kafkaAddress);

            // Add Kafka producer sink for valid data
            filteredStream.addSink(successProducer);

            // Add Kafka producer sink for invalid data
            errorStream.addSink(failureProducer);

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
