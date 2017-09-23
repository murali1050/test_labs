package services;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class SWCIntegrationEDS {

	public static void main(String[] args) {
		Date date;
		JSONObject json = null;
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.SSS'Z'");
		String jsonString = "{\"success\": {  \"status\": \"success\",  \"code\": \"EB-SWC-SVC-0200\",  \"message\": \"passed\",  \"timeStamp\": \"2016-12-12T20:43:55.336Z\",  \"data\": {    \"ccoId\": \"I:jprattip\",    \"inputMDFLeafNodeId\": 2791207991,    \"reasonCode\": 171,    \"reasonDesc\": \"Failed: Provided MDF leaf node id does not exist.\",    \"resultCode\": 3,    \"resultDesc\": \"Failed Entitlement\"  }},\"Bad Request\": {  \"message\": \"Bad Request:There is not enough information provided to provide an entitlement decision. CCOID must be provided.\",  \"code\": \"EB-EDS-SVC-0400\",  \"timeStamp\": \"2016-10-31T20:11:02.864Z\",  \"status\": \"fail\"},\"Error\": {  \"message\": \"Api Error\",  \"code\": \"EB-EDS-SVC-0500\",  \"timeStamp\": \"2016-10-31T20:11:02.864Z\",  \"status\": \"fail\",  \"supportEmail\": \"support@ebapi.cisco.com\"}}";
		String withoutSuccess = "{\"Bad Request\": {  \"message\": \"Bad Request:There is not enough information provided to provide an entitlement decision. CCOID must be provided.\",  \"code\": \"EB-EDS-SVC-0400\",  \"timeStamp\": \"2016-10-31T20:11:02.864Z\",  \"status\": \"fail\"},\"Error\": {  \"message\": \"Api Error\",  \"code\": \"EB-EDS-SVC-0500\",  \"timeStamp\": \"2016-10-31T20:11:02.864Z\",  \"status\": \"fail\",  \"supportEmail\": \"support@ebapi.cisco.com\"}}";
		String newString = "{\"status\" : \"success\",\"code\" : \"EB-EDS-SVC-0200\",\"message\" : \"passed\",\"timeStamp\" : \"2017-01-11T21:24:01.586Z\",\"data\" : {	\"ccoUserId\" : \"jki\",	\"mdfLeafNodeId\" : 282861328,	\"reasonCode\" : 45,	\"reasonDescription\" : \"Failed: Product not covered by any contract or subscription in users profile\",	\"resultCode\" : 3,	\"resultDescription\" : \"Failed Entitlement\",	\"entitlementAuditId\" : 12000187061}}";
		try {
			json = new JSONObject(newString);
			if(json.getString("status") != null) {
				String status = json.getString("status");
				System.out.println("Status Value: " + status);
				date = sdf.parse(json.getString("timeStamp"));
				sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
				String newDateString = sdf.format(date);
				System.out.println("New Date Time Format: " + newDateString);
				String ccoID = json.getJSONObject("data").getString("ccoUserId");
				System.out.println("ccoId Value: " + ccoID);
			}
		} catch (JSONException e) {
			System.out.println("*** Am in the JSONException *** " + e.getMessage());
			try {
				if(json.getJSONObject("Error") != null) {
					String status = json.getJSONObject("Error").getString("status");
					System.out.println("ERROR: Status Value: " + status);
					date = sdf.parse(json.getJSONObject("Error").getString("timeStamp"));
					sdf.applyPattern("yyyy-MM-dd HH:mm:ss");
					String newDateString = sdf.format(date);
					System.out.println("Error New Date Time Format: " + newDateString);
				}
			} catch (JSONException je) {
				System.out.println("*** Am in the JSONException *** ");
			}catch (Exception ee) {
				System.out.println("*** Am in the Exception *** " + e.getMessage());
			}
		}catch (Exception e) {
			System.out.println("*** Am in the Exception *** " + e.getMessage());
		}
	}

}
