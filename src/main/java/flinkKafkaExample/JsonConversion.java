package flinkKafkaExample;

import org.apache.flink.streaming.api.functions.ProcessFunction;
import org.apache.flink.util.Collector;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;


public class JsonConversion extends ProcessFunction<String, String> {

    private static final Logger log = LoggerFactory.getLogger(JsonConversion.class);

    @Override
    public void processElement(String event, Context context, Collector<String> collector) throws Exception {
        try{
            log.info("Incoming data: {}", event);

//            JSONObject json = new JSONObject(event);
//            if (json.has("name") && json.has("email")){
//
//                    JSONObject filteredJson = new JSONObject();
//                    filteredJson.put("name: ", json.getString("name"));
//                    filteredJson.put("email: ", json.getString("email"));
//                    filteredJson.put("address_city: ", json.getJSONObject("address").getString("city"));
//                    filteredJson.put("address_state: ", json.getJSONObject("address").getString("state"));
//                    filteredJson.put("address_zip: ", json.getJSONObject("address").getString("zip"));

                    JSONObject filteredJson = ValidJson.validJson(event);
                    log.info("Filtered data: {}", filteredJson.toString());

                    collector.collect(filteredJson.toString());

        }catch (Exception e){
            log.error("Error while processing: {}", event, e);
            JSONObject error = new JSONObject();
            StringWriter ErrorStr = new StringWriter();
            PrintWriter ErrorWrt = new PrintWriter(ErrorStr);
            e.setStackTrace(Arrays.stream(e.getStackTrace())
                    .filter(se -> !se.getClassName().startsWith("org.apache.flink"))
//                            .filter(se -> !se.getClassName().startsWith(".get("))
                            .collect(Collectors.toList())
                                    .toArray(new StackTraceElement[0]));

            e.printStackTrace(ErrorWrt);
            error.put("Data", event.toString());
            error.put("Error", ErrorStr);

            //
            context.output(FlinkKafkaExample.faultOutputTag, error.toString());
        }
    }
}
