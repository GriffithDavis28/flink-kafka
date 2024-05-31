package flinkKafkaExample;

import org.apache.flink.api.common.functions.MapFunction;

public class Capitalizer implements MapFunction<String, String> {

    @Override
    public String map(String s) throws Exception {
        return "Processed data: "+s.toUpperCase();
    }
}
