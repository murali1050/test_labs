package services;

import com.cisco.support.sdp.common.sdpsharedutils.util.CPRAPIUtil;


public class TestCPR {

	public static void main(String[] args) 
	{
		// TODO Auto-generated method stub
		CPRAPIUtil testCPR = new CPRAPIUtil();
		try {
			//Calling the IDREAD - service 
			//testCPR.getUserAttrFromIDService("ACCESS_TO_ENCRYPT_SW", "vkumarsu");
			
			//Calling the CPR READ
			
			testCPR.getUserAttr("ACCESS_TO_ENCRYPT_SW", "sheramal");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
