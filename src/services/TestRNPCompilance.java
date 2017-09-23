package services;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ResourceBundle;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import sun.misc.BASE64Encoder;

import com.cisco.support.sdp.common.sdpsharedutils.constants.SDPSharedUtilsConstants;
import com.cisco.support.sdp.common.sdpsharedutils.logging.SDPLogger;
import com.cisco.support.sdp.common.sdpsharedutils.util.SDPCommonServiceUtil;
import com.cisco.support.sdp.common.sdpsharedutils.util.StringValidator;
import com.cisco.support.sdp.common.sdpsharedutils.util.URLGenerator;
import com.cisco.support.sdp.common.sdpsharedutils.vos.RNPDetailsVO;
import com.cisco.support.sdp.common.sdpsharedutils.vos.ServiceRequest;
import com.cisco.support.sdp.common.sdpsharedutils.vos.ServiceResponse;

public class TestRNPCompilance {
	/**
	 * ATTRIBUTE_NAMES[]
	 */
	private static final String ATTRIBUTE_NAMES[] = { "status", "transId",
			"cprStatus", "status_desc", "ERROR" };
	/**
	 * ELEMENT_NAME
	 */
	private static final String ELEMENT_NAME = "ENTITLEMENT";

	/**
	 * ERROR_NAME
	 */
	private static final String ERROR_ELEMENT_NAME = "ERROR";
	/**
	 * /**
	 *
	 * resourceBundle
	 */
	private static ResourceBundle resourceBundle = ResourceBundle
			.getBundle(SDPSharedUtilsConstants.PROPFILE);

	public static void main(String[] args) {
		// TODO Auto-generated method stub

		ServiceRequest serviceRequest = new ServiceRequest();

		RNPDetailsVO rnpdetails = new RNPDetailsVO();
		rnpdetails.setUserId("test2011CWRN");
		rnpdetails.setMdfId("283704448");

		serviceRequest.setRnpDetailsVO(rnpdetails);

		try {
			ServiceResponse serviceResponse = SDPCommonServiceUtil
					.isRNPCompliance(serviceRequest);

			RNPDetailsVO rnpdetail = serviceResponse.getRnpDetailsVO();
			if (rnpdetail != null) {
				System.out.println(rnpdetail.getEntitlementUser());
				System.out.println(rnpdetail.getTransId());
				System.out.println(rnpdetail.getCprStatus());
//				System.out.println(rnpdetail.getStatus_desc());
//				System.out.println(rnpdetail.getErrorCode());
				System.out.println(rnpdetail.getErrorMsg());
			}

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
