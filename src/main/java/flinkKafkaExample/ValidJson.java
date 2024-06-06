package flinkKafkaExample;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ValidJson {

    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    public static JSONObject validJson(String event) throws ParseException {

        JSONObject json = new JSONObject(event);
        if (json.has("updatedDate")) {

            JSONObject filteredJson = new JSONObject();
            filteredJson.put("updatedDate", json.getString("updatedDate"));
            parseDate(filteredJson.getString("updatedDate"));

            if(json.has("email")) {
                String email = json.getString("email");
                if (isValidEmail(email)) {
                    filteredJson.put("email", json.getString(email));
                } else {
                    throw new IllegalArgumentException("Invalid domain");
                }
            }
            else{
                    throw new IllegalArgumentException("Invalid email address");
            }
            return filteredJson;
        }else
        return null;
    }

    public static Date parseDate(String s) throws ParseException{
        if(s==null){
            throw new ParseException("Date is not given", 0);
        }

        SimpleDateFormat format = new SimpleDateFormat(DATE_FORMAT);
        format.setLenient(false);
        return format.parse(s);
    }

    private static boolean isValidEmail(String email){
        if(email==null || !email.contains("@")){
            return false;
        }
        String domain = email.substring(email.lastIndexOf('@')+1);
        return domain.endsWith(".com");
    }
}
