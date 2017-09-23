package services;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.apache.commons.lang.StringEscapeUtils;

import com.cisco.support.sdp.common.sdpsharedutils.logging.SDPLogger;


public class BavaFix {

	public static void main(String[] args) throws UnsupportedEncodingException {

		String bava1 = "<script>alert(18375)</script>";
		String bava2 = "Content-Type:%20multipart/related;%20boundary=_AppScan%0d%0a--_AppScan%0d%0aContent-Location:foo%0d%0aContent-Transfer-Encoding:base64%0d%0a%0d%0aPGh0bWw%2bPHNjcmlwdD5hbGVydCgiWFNTIik8L3NjcmlwdD48L2h0bWw%2b%0d%0a";
		String bava3 = "featureListServiceResponse%27%22%3E%3Ciframe+id%3D18378+src%3Dhttp%3A%2F%2Fdemo.testfire.net%2Fphishing.html%3E";
		String bava4 = "%22%27%3E%3CIMG+SRC%3D%22%2FWF_XSRF18376.html%22%3E";
		
//		System.out.println("Result1:" + URLDecoder.decode(bava1,"UTF-8"));
//		System.out.println("Result2:" + URLDecoder.decode(bava2,"UTF-8"));
//		System.out.println("Result3:" + URLDecoder.decode(bava3,"UTF-8"));
//		System.out.println("Result4:" + URLDecoder.decode(bava4,"UTF-8"));
//		
//		System.out.println("Result1:" + StringEscapeUtils.escapeHtml(URLDecoder.decode(bava1,"UTF-8")));
//		System.out.println("Result2:" + StringEscapeUtils.escapeHtml(URLDecoder.decode(bava2,"UTF-8")));
//		System.out.println("Result3:" + StringEscapeUtils.escapeHtml(URLDecoder.decode(bava3,"UTF-8")));
//		System.out.println("Result4:" + StringEscapeUtils.escapeHtml(URLDecoder.decode(bava4,"UTF-8"))); 
		escapeSpecialCharacter(bava1);
	}
	
	private static String escapeSpecialCharacter(String parameter) {
		System.out.println("Entering: Before escaping the callback:" + parameter);
		if(parameter != null && !parameter.equals("")) {
			try {
				parameter = URLDecoder.decode(parameter,"UTF-8");
				parameter = StringEscapeUtils.escapeHtml(parameter);
				parameter = parameter.replace("\\n", "\\\\\\n");
				parameter = parameter.replace("\\r", "\\\\\\r");
				parameter = parameter.replace("/", "\\/");
				parameter = parameter.replace("?", "\\?");
				parameter = parameter.replace("'", "\\'");
				parameter = parameter.replace("(", "\\(");
				parameter = parameter.replace(")", "\\)");
				parameter = parameter.replace("[", "\\[");
				parameter = parameter.replace("]", "\\]");
			} catch (UnsupportedEncodingException e) {
				System.out.println("in Servlet (escapeSpecialCharacter)" +e);
			}
		}
		System.out.println("Exiting: After escaping the callback:" + parameter);
		return parameter;
	}

}
