package flinkKafkaExample;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class JsonConversion extends ProcessFunction<String, String> {

    private static final Logger log = LoggerFactory.getLogger(JsonConversion.class);

    @Override
    public void processElement(String event, Context context, Collector<String> collector) throws Exception {
        try{
            JSONObject input = new JSONObject(event);

            String name = input.has("name")?input.getString("name"):"";
            String email = input.has("email")?input.getString("email"):"";

            JSONObject newResult = new JSONObject();
            newResult.put("name", name);
            newResult.put("email",email);

            collector.collect(newResult.toString());
        }catch (Exception e){
            log.error("Error while processing: {}", event, e);
            context.output(FlinkKafkaExample.faultOutputTag, event);
        }
    }
}
