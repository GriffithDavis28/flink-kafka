import flinkKafkaExample.ValidJson;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class ValidJsonTest {

    //Invalid email test
    @Test
    void testCase001() {
        //String event = "{ \"updatedDate\": \"2024-06-06\", \"email\": \"invalid-email\" }";

        String updatedDate = "2024-05-11T10:08:23.000Z";
        String email = "invalid-email";

        JSONObject json = new JSONObject();
        json.put("updatedDate", updatedDate);
        json.put("email", email);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            JSONObject filteredJson = new JSONObject(json);
            if (json.has("updatedDate")) {
                filteredJson.put("updatedDate", json.getString("updatedDate"));
                parseDate(filteredJson.getString("updatedDate"));

                if (json.has("email")) {
                    String validEmail = json.getString("email");
                    if (isValidEmail(validEmail)) {
                        filteredJson.put("email", json.getString("email"));
                    } else {
                        throw new IllegalArgumentException("invalid domain");
                    }
                } else {
                    throw new IllegalArgumentException("invalid email address");
                }
            }
        });

        assertEquals("invalid domain", exception.getMessage());
    }

    //UpdatedDate invalid or empty
    @Test
    void testCase002() {

        String updatedDate = "invalid";
        String email = "griffith@gmail.com";

        JSONObject json = new JSONObject();
        json.put("updatedDate", updatedDate);
        json.put("email", email);

        Exception exception = assertThrows(ParseException.class, () -> {
            JSONObject filteredJson = new JSONObject(json);
            if (json.has("updatedDate")) {
                filteredJson.put("updatedDate", json.getString("updatedDate"));
                parseDate(filteredJson.getString("updatedDate"));

                if (json.has("email")) {
                    String validEmail = json.getString("email");
                    if (isValidEmail(validEmail)) {
                        filteredJson.put("email", json.getString("email"));
                    } else {
                        throw new IllegalArgumentException("invalid domain");
                    }
                } else {
                    throw new IllegalArgumentException("invalid email address");
                }
            }
        });

        assertTrue(exception.getMessage().contains(""));

    }

    //Job Category in json missing or invalid
    @Test
    void testCase003() {
        String data = "{\"updatedDate\":\"2024-05-11T10:08:23.000Z\",\"email\":\"griffith@gmail.com\",\"jobCat\":\" \"}\n";
//        String data = "{\n" +
//                "\"cdc_pk\": \"663f426f783f8562f9ea7a01.1714417954718.1714845584015\", \"cdc_oid\": \"663f426f783f8562f9ea7a01\", \"_id\": {\"oid\": \"663f426f783f8562f9ea7a01\"}, \"applicationId\": \"JOB_APPLICATION-3-7726404\", \"applied\": [{\"applicationId\": \"JOB_APPLICATION-3-7726404\", \"atsId\": \"CANDIDATE-3-4462699\", \"dateCreated\": \"2024-05-11T10:08:23.000Z\", \"email\": \"ladleenasanjay@gmail.com\", \"fieldID\": \"f913b11a-50ac-45c7-903f-2901a190f846\", \"isCareerSite\": null, \"isRejected\": null, \"jobCategory\": \"Sales\", \"jobLocation\": \"India - Mumbai\", \"jobSeqNo\": \"ABLAUS31087044ENUSINTERNAL\", \"jobTitle\": \"Territory Business Manager- Multitherapy- Satara\", \"locale\": null, \"siteType\": \"external\", \"source\": \"LinkedIn\", \"trafficSourceList\": [{\"createdDate\": \"2024-05-11T10:08:23.000Z\", \"id\": null, \"rawUrl\": \"LinkedIn\", \"referralType\": \"LinkedIn\", \"sourceFullname\": \"LinkedIn\", \"sourceShortName\": \"LinkedIn\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": null}], \"uid\": null, \"updatedDate\": \"2024-05-11T10:08:23.000Z\"}, {\"applicationId\": \"JOB_APPLICATION-3-7726404\", \"atsId\": \"CANDIDATE-3-4462699\", \"dateCreated\": \"2024-05-11T10:08:18.022Z\", \"email\": \"ladleenasanjay@gmail.com\", \"fieldID\": \"3349adf4-ce69-4aa4-9471-9c4c3e2fecd8\", \"isCareerSite\": true, \"isRejected\": null, \"jobCategory\": \"Sales\", \"jobLocation\": \"India - Mumbai\", \"jobSeqNo\": \"ABLAUS31087044ENUSEXTERNAL\", \"jobTitle\": \"Territory Business Manager- Multitherapy- Satara\", \"locale\": \"en_us\", \"siteType\": \"external\", \"source\": null, \"trafficSourceList\": [{\"createdDate\": \"2024-05-11T10:08:18.022Z\", \"id\": null, \"rawUrl\": \"https://click.appcast.io/\", \"referralType\": \"Job Board\", \"sourceFullname\": \"Appcast_IndeedOrganic_phenom-feeds\", \"sourceShortName\": \"Appcast_indeedorganic\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": \"phenom-feeds\"}, {\"createdDate\": \"2024-05-11T10:04:00.014Z\", \"id\": null, \"rawUrl\": \"LinkedIn\", \"referralType\": null, \"sourceFullname\": \"LinkedIn\", \"sourceShortName\": \"LinkedIn\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": null}], \"uid\": \"18f671c07ee315-144000-2d4f-18f671c07ef56e\", \"updatedDate\": \"2024-05-11T10:08:18.022Z\"}], \"applyTransactionId\": null, \"aryaScore\": {\"candidateScore\": \"1.4269663202897627\", \"createdDate\": \"2024-05-11T10:14:00.256Z\", \"experienceScore\": 0.9142833948135376, \"fit\": null, \"fitValue\": null, \"goodToHaveSkillScore\": null, \"jobSkillsCount\": null, \"locationDistance\": 20, \"locationScore\": 1, \"md5Check\": \"aa6adef8b0ef775eb4afe453510fb7e6b8d17d60\", \"mustToHaveSkillScore\": null, \"score\": 1.4269663095474243, \"scoredBy\": null, \"skillsScore\": 0.20409341156482697, \"source\": \"Phenom\", \"titleScore\": 0.37053799629211426, \"uid\": null, \"updatedDate\": \"2024-05-13T10:23:24.220Z\"}, \"atsId\": \"CANDIDATE-3-4462699\", \"bulkUpdateLock\": null, \"consent\": \"not available\", \"consentDate\": null, \"createdDate\": \"2024-05-11T10:03:00.026Z\", \"crmRecommended\": null, \"dismissReasons\": null, \"documentUpdatedDate\": \"2024-05-13T09:39:59.196Z\", \"firstSourceChannel\": \"Appcast_indeedorganic / phenom-feeds\", \"hiringStatus\": {\"atsValue\": null, \"disposition\": null, \"hiringStatusCode\": null, \"hiringStepCode\": \"JOB_APPLICATION_DEFAULT_DEFINITION_STEP_B__ACTION_2\", \"hsPriority\": 0, \"isRejected\": null, \"previousHiringStatusCode\": \"\", \"previousHiringStepCode\": \"\", \"previousRejectionReason\": \"\", \"previousRejectionReasonId\": \"\", \"previousStep\": null, \"previousValue\": \"Lead\", \"previousWorkFlowStepId\": null, \"recruiterUserId\": null, \"rejectCode\": null, \"rejectionReason\": \"\", \"rejectionReasonId\": \"\", \"step\": \"New Candidate\", \"updatedDate\": \"2024-05-11T10:08:23.000Z\", \"value\": \"REVIEW\", \"workFlowStepId\": \"JOB_APPLICATION_DEFAULT_DEFINITION_STEP_B__ACTION_2\"}, \"incompleteApplied\": [], \"interviewSchedule\": null, \"isConfidential\": null, \"isVerified\": null, \"jobCategory\": \"Sales\", \"jobId\": \"31087044\", \"jobSeekerSource\": [\"Viewed | CAREER SITE\", \"Incomplete Applied | CAREER SITE\", \"Applied | CAREER SITE\", \"Applied | ats\"], \"jobSeqNo\": null, \"jobTitle\": \"Territory Business Manager- Multitherapy- Satara\", \"lastSourceChannel\": \"LinkedIn / not_set\", \"leadStatus\": \"Unread\", \"mlLanguage\": null, \"parent\": \"663f426f783f8562f9ea79fd\", \"recommendationSeenBy\": null, \"recommendationWidgetSoucre\": null, \"refNum\": \"ABLAUS\", \"seen\": null, \"source\": null, \"sourceTopic\": null, \"sourced\": [], \"status\": \"Applied\", \"statusPriority\": 1000, \"subscribed\": [], \"trafficSourceList\": [{\"activityTypes\": [\"applied\", \"viewed\"], \"createdDate\": \"2024-05-11T10:03:00.026Z\", \"rawUrl\": \"https://click.appcast.io/\", \"referralType\": \"Job Board\", \"sourceFullname\": \"Appcast_IndeedOrganic_phenom-feeds\", \"sourceShortName\": \"Appcast_indeedorganic\", \"utmCampaign\": null, \"utmMedium\": \"phenom-feeds\"}, {\"activityTypes\": [\"applied\", \"viewed\"], \"createdDate\": \"2024-05-11T10:04:00.014Z\", \"rawUrl\": \"LinkedIn\", \"referralType\": null, \"sourceFullname\": \"LinkedIn\", \"sourceShortName\": \"LinkedIn\", \"utmCampaign\": null, \"utmMedium\": null}], \"updatedDate\": \"2024-05-11T10:08:23.000Z\", \"userId\": \"fjmbi0hbhgidolb1gvujv0cofg\", \"userType\": null, \"viewed\": [{\"dateCreated\": \"2024-05-11T10:03:00.026Z\", \"fieldID\": \"e4098864-7335-482e-86ce-01c519210244\", \"jobCategory\": \"Sales\", \"jobLocation\": \"India - Mumbai\", \"jobSeqNo\": \"ABLAUS31087044ENUSEXTERNAL\", \"jobTitle\": \"Territory Business Manager- Multitherapy- Satara\", \"locale\": \"en_us\", \"priority\": null, \"siteType\": \"external\", \"source\": null, \"trafficSourceList\": [{\"createdDate\": \"2024-05-11T10:03:00.026Z\", \"id\": null, \"rawUrl\": \"https://click.appcast.io/\", \"referralType\": \"Job Board\", \"sourceFullname\": \"Appcast_IndeedOrganic_phenom-feeds\", \"sourceShortName\": \"Appcast_indeedorganic\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": \"phenom-feeds\"}, {\"createdDate\": \"2024-05-11T10:04:00.014Z\", \"id\": null, \"rawUrl\": \"LinkedIn\", \"referralType\": null, \"sourceFullname\": \"LinkedIn\", \"sourceShortName\": \"LinkedIn\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": null}], \"updatedDate\": \"2024-05-11T10:08:00.026Z\"}], \"cdc_modifiedon\": \"1715687946199\", \"cdc_operation\": \"update\", \"updatedOn\": null}";


        JSONObject json = new JSONObject(data);

        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
                    JSONObject filteredJson = new JSONObject(json);
                    if (json.has("updatedDate")) {
                        filteredJson.put("updatedDate", json.getString("updatedDate"));
                        parseDate(filteredJson.getString("updatedDate"));

                        if (json.has("email")) {
                            String validEmail = json.getString("email");
                            if (isValidEmail(validEmail)) {
                                filteredJson.put("email", json.getString("email"));
                            } else {
                                throw new IllegalArgumentException("invalid domain");
                            }
                        } else {
                            throw new IllegalArgumentException("invalid email address");
                        }

                        if (json.has("jobCategory")) {
                            String jobCategory = json.getString("jobCategory").trim();
                            if (jobCategory.isEmpty()) {
                                throw new IllegalArgumentException("jobCategory is empty");
                            }
                            filteredJson.put("JobCategory", json.getString("jobCategory"));
                        }else {
                            throw new IllegalArgumentException("job Category is missing");
                        }

                    }
                });
        assertEquals("job Category is missing", exception.getMessage());
    }

    //Json structure test
    @Test
    void testCase004(){
        String data = "{\n" +
                "\"cdc_pk\": \"663f426f783f8562f9ea7a01.1714417954718.1714845584015\", \"cdc_oid\": \"663f426f783f8562f9ea7a01\", \"_id\": {\"oid\": \"663f426f783f8562f9ea7a01\"}, \"applicationId\": \"JOB_APPLICATION-3-7726404\", \"applied\": [{\"applicationId\": \"JOB_APPLICATION-3-7726404\", \"atsId\": \"CANDIDATE-3-4462699\", \"dateCreated\": \"2024-05-11T10:08:23.000Z\", \"email\": \"ladleenasanjay@gmail.com\", \"fieldID\": \"f913b11a-50ac-45c7-903f-2901a190f846\", \"isCareerSite\": null, \"isRejected\": null, \"jobCategory\": \"Sales\", \"jobLocation\": \"India - Mumbai\", \"jobSeqNo\": \"ABLAUS31087044ENUSINTERNAL\", \"jobTitle\": \"Territory Business Manager- Multitherapy- Satara\", \"locale\": null, \"siteType\": \"external\", \"source\": \"LinkedIn\", \"trafficSourceList\": [{\"createdDate\": \"2024-05-11T10:08:23.000Z\", \"id\": null, \"rawUrl\": \"LinkedIn\", \"referralType\": \"LinkedIn\", \"sourceFullname\": \"LinkedIn\", \"sourceShortName\": \"LinkedIn\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": null}], \"uid\": null, \"updatedDate\": \"2024-05-11T10:08:23.000Z\"}, {\"applicationId\": \"JOB_APPLICATION-3-7726404\", \"atsId\": \"CANDIDATE-3-4462699\", \"dateCreated\": \"2024-05-11T10:08:18.022Z\", \"email\": \"ladleenasanjay@gmail.com\", \"fieldID\": \"3349adf4-ce69-4aa4-9471-9c4c3e2fecd8\", \"isCareerSite\": true, \"isRejected\": null, \"jobCategory\": \"Sales\", \"jobLocation\": \"India - Mumbai\", \"jobSeqNo\": \"ABLAUS31087044ENUSEXTERNAL\", \"jobTitle\": \"Territory Business Manager- Multitherapy- Satara\", \"locale\": \"en_us\", \"siteType\": \"external\", \"source\": null, \"trafficSourceList\": [{\"createdDate\": \"2024-05-11T10:08:18.022Z\", \"id\": null, \"rawUrl\": \"https://click.appcast.io/\", \"referralType\": \"Job Board\", \"sourceFullname\": \"Appcast_IndeedOrganic_phenom-feeds\", \"sourceShortName\": \"Appcast_indeedorganic\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": \"phenom-feeds\"}, {\"createdDate\": \"2024-05-11T10:04:00.014Z\", \"id\": null, \"rawUrl\": \"LinkedIn\", \"referralType\": null, \"sourceFullname\": \"LinkedIn\", \"sourceShortName\": \"LinkedIn\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": null}], \"uid\": \"18f671c07ee315-144000-2d4f-18f671c07ef56e\", \"updatedDate\": \"2024-05-11T10:08:18.022Z\"}], \"applyTransactionId\": null, \"aryaScore\": {\"candidateScore\": \"1.4269663202897627\", \"createdDate\": \"2024-05-11T10:14:00.256Z\", \"experienceScore\": 0.9142833948135376, \"fit\": null, \"fitValue\": null, \"goodToHaveSkillScore\": null, \"jobSkillsCount\": null, \"locationDistance\": 20, \"locationScore\": 1, \"md5Check\": \"aa6adef8b0ef775eb4afe453510fb7e6b8d17d60\", \"mustToHaveSkillScore\": null, \"score\": 1.4269663095474243, \"scoredBy\": null, \"skillsScore\": 0.20409341156482697, \"source\": \"Phenom\", \"titleScore\": 0.37053799629211426, \"uid\": null, \"updatedDate\": \"2024-05-13T10:23:24.220Z\"}, \"atsId\": \"CANDIDATE-3-4462699\", \"bulkUpdateLock\": null, \"consent\": \"not available\", \"consentDate\": null, \"createdDate\": \"2024-05-11T10:03:00.026Z\", \"crmRecommended\": null, \"dismissReasons\": null, \"documentUpdatedDate\": \"2024-05-13T09:39:59.196Z\", \"firstSourceChannel\": \"Appcast_indeedorganic / phenom-feeds\", \"hiringStatus\": {\"atsValue\": null, \"disposition\": null, \"hiringStatusCode\": null, \"hiringStepCode\": \"JOB_APPLICATION_DEFAULT_DEFINITION_STEP_B__ACTION_2\", \"hsPriority\": 0, \"isRejected\": null, \"previousHiringStatusCode\": \"\", \"previousHiringStepCode\": \"\", \"previousRejectionReason\": \"\", \"previousRejectionReasonId\": \"\", \"previousStep\": null, \"previousValue\": \"Lead\", \"previousWorkFlowStepId\": null, \"recruiterUserId\": null, \"rejectCode\": null, \"rejectionReason\": \"\", \"rejectionReasonId\": \"\", \"step\": \"New Candidate\", \"updatedDate\": \"2024-05-11T10:08:23.000Z\", \"value\": \"REVIEW\", \"workFlowStepId\": \"JOB_APPLICATION_DEFAULT_DEFINITION_STEP_B__ACTION_2\"}, \"incompleteApplied\": [], \"interviewSchedule\": null, \"isConfidential\": null, \"isVerified\": null, \"jobCategory\": \"Sales\", \"jobId\": \"31087044\", \"jobSeekerSource\": [\"Viewed | CAREER SITE\", \"Incomplete Applied | CAREER SITE\", \"Applied | CAREER SITE\", \"Applied | ats\"], \"jobSeqNo\": null, \"jobTitle\": \"Territory Business Manager- Multitherapy- Satara\", \"lastSourceChannel\": \"LinkedIn / not_set\", \"leadStatus\": \"Unread\", \"mlLanguage\": null, \"parent\": \"663f426f783f8562f9ea79fd\", \"recommendationSeenBy\": null, \"recommendationWidgetSoucre\": null, \"refNum\": \"ABLAUS\", \"seen\": null, \"source\": null, \"sourceTopic\": null, \"sourced\": [], \"status\": \"Applied\", \"statusPriority\": 1000, \"subscribed\": [], \"trafficSourceList\": [{\"activityTypes\": [\"applied\", \"viewed\"], \"createdDate\": \"2024-05-11T10:03:00.026Z\", \"rawUrl\": \"https://click.appcast.io/\", \"referralType\": \"Job Board\", \"sourceFullname\": \"Appcast_IndeedOrganic_phenom-feeds\", \"sourceShortName\": \"Appcast_indeedorganic\", \"utmCampaign\": null, \"utmMedium\": \"phenom-feeds\"}, {\"activityTypes\": [\"applied\", \"viewed\"], \"createdDate\": \"2024-05-11T10:04:00.014Z\", \"rawUrl\": \"LinkedIn\", \"referralType\": null, \"sourceFullname\": \"LinkedIn\", \"sourceShortName\": \"LinkedIn\", \"utmCampaign\": null, \"utmMedium\": null}], \"updatedDate\": \"2024-05-11T10:08:23.000Z\", \"userId\": \"fjmbi0hbhgidolb1gvujv0cofg\", \"userType\": null, \"viewed\": [{\"dateCreated\": \"2024-05-11T10:03:00.026Z\", \"fieldID\": \"e4098864-7335-482e-86ce-01c519210244\", \"jobCategory\": \"Sales\", \"jobLocation\": \"India - Mumbai\", \"jobSeqNo\": \"ABLAUS31087044ENUSEXTERNAL\", \"jobTitle\": \"Territory Business Manager- Multitherapy- Satara\", \"locale\": \"en_us\", \"priority\": null, \"siteType\": \"external\", \"source\": null, \"trafficSourceList\": [{\"createdDate\": \"2024-05-11T10:03:00.026Z\", \"id\": null, \"rawUrl\": \"https://click.appcast.io/\", \"referralType\": \"Job Board\", \"sourceFullname\": \"Appcast_IndeedOrganic_phenom-feeds\", \"sourceShortName\": \"Appcast_indeedorganic\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": \"phenom-feeds\"}, {\"createdDate\": \"2024-05-11T10:04:00.014Z\", \"id\": null, \"rawUrl\": \"LinkedIn\", \"referralType\": null, \"sourceFullname\": \"LinkedIn\", \"sourceShortName\": \"LinkedIn\", \"subSource\": null, \"utmCampaign\": null, \"utmMedium\": null}], \"updatedDate\": \"2024-05-11T10:08:00.026Z\"}], \"cdc_modifiedon\": \"1715687946199\", \"cdc_operation\": \"update\", \"updatedOn\": null}";
        JSONObject json = new JSONObject(data);
        JSONArray applied = json.getJSONArray("applied");

        for (int i = 0; i < applied.length() ; i++) {

            JSONObject applicationProps = applied.getJSONObject(i);

            assertNotNull(applicationProps.getString("applicationId"));
            assertNotNull(applicationProps.getString("atsId"));
            assertNotNull(applicationProps.getString("dateCreated"));
            assertNotNull(applicationProps.getString("email"));
            assertNotNull(applicationProps.getString("fieldID"));
            assertNotNull(applicationProps.getString("updatedDate"));
            assertNotNull(applicationProps.getString("jobSeqNo"));
        }
    }

    //Valid date format
    @Test
    void testCase005(){
            String date = "05-11-2024T10:08:23.000Z";

            JSONObject json = new JSONObject();
            json.put("date", date);

            Exception exception = assertThrows(ParseException.class, () -> {
                JSONObject filteredJson = new JSONObject();
                if (json.has("date")){
                    filteredJson.put("date", json.getString("date"));
                    parseDate(filteredJson.getString("date"));
                }
            });
            assertEquals(true, true);
    }

    //Invalid date Format
    @Test
    void testCase006(){
        String date = "05-2022-11T10:08:23.000Z";

        JSONObject json = new JSONObject();
        json.put("date", date);

        Exception exception = assertThrows(ParseException.class, () -> {
            JSONObject filteredJson = new JSONObject();
            if (json.has("date")){
                filteredJson.put("date", json.getString("date"));
                parseDate(filteredJson.getString("date"));
            }
        });
        assertEquals("Invalid date Format", exception.getMessage());
    }


        private static final String[] DATE_FORMAT = {"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'","yyyy-dd-MM'T'HH:mm:ss.SSS'Z'", "dd-MM-yyyy'T'HH:mm:ss.SSS'Z'", "MM-dd-yyyy'T'HH:mm:ss.SSS'Z'"};

        public static Date parseDate (String s) throws ParseException {
            if (s == null) {
                throw new ParseException("Date is not given", 0);
            }


            for (String dateFormat : DATE_FORMAT){
                SimpleDateFormat format = new SimpleDateFormat(dateFormat);
                format.setLenient(false);
                try{
                    format.parse(s);
                }catch (ParseException e){
                    e.printStackTrace();
                    //System.out.println("Invalid dateFormat"+dateFormat+" with given date: "+s);
                }
            }
            throw new ParseException("Unparsable"+s, 0);
        }

        private static boolean isValidEmail (String email){
            if (email == null || !email.contains("@")) {
                return false;
            }
            String domain = email.substring(email.lastIndexOf('@') + 1);
            return domain.endsWith(".com");
        }
}


