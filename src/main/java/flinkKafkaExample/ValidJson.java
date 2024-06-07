package flinkKafkaExample;

import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;

public class ValidJson {

    private static final String[] DATE_FORMAT = {"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","yyyy-dd-MM'T'HH:mm:ss.SSS'Z'", "dd-MM-yyyy'T'HH:mm:ss.SSS'Z'", "MM-dd-yyyy'T'HH:mm:ss.SSS'Z'"};

    public static JSONObject validJson(String event) throws ParseException {

        JSONObject json = new JSONObject(event);
        if (json.has("date")){
            JSONObject filteredJson = new JSONObject();
            if(json.getString("date").isEmpty())
            {
                LocalDateTime date = LocalDateTime.now();
                filteredJson.put("date", date);
                try{
                    Long epochSeconds = parseDate(filteredJson.getString("date")).getTime() / 1000;
                    System.out.println(epochSeconds);
                    filteredJson.put("date", epochSeconds);
                }catch (Exception e){
                    e.printStackTrace();
                }
            }else {
                filteredJson.put("date", json.getString("date"));
                Long epochSeconds = parseDate(filteredJson.getString("date")).getTime() / 1000;
                System.out.println(epochSeconds);
                filteredJson.put("date", epochSeconds);
            }
//            filteredJson.put("updatedDate", json.getString("updatedDate"));
//            parseDate(filteredJson.getString("updatedDate"));
//
//            if(json.has("email")) {
//                String email = json.getString("email");
//                if (isValidEmail(email)) {
//                    filteredJson.put("email", json.getString(email));
//                } else {
//                    throw new IllegalArgumentException("Invalid domain");
//                }
//            }
//            else{
//                    throw new IllegalArgumentException("Invalid email address");
//            }
//
//            if(json.has("jobCategory")){
//                filteredJson.put("jobCategory", json.getString("jobCategory"));
//            }
            return filteredJson;
        }else
        return null;
    }

    public static Date parseDate(String s) throws ParseException{
        if(s==null){
            throw new ParseException("Date is not given", 0);
        }

        for (String dateFormat : DATE_FORMAT){
            try{
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                format.setLenient(false);
                return format.parse(s);
            }catch (ParseException e){
                e.printStackTrace();
            }
        }
        throw new ParseException("Unparsable"+s, 0);
    }

    private static boolean isValidEmail(String email){
        if(email==null || !email.contains("@")){
            return false;
        }
        String domain = email.substring(email.lastIndexOf('@')+1);
        return domain.endsWith(".com");
    }
}
