package services;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import sun.misc.BASE64Encoder;

/**
 * 
 * @author muralive
 *
 */
public class AsperaTokenGenerator {
	
	static {
	    //for localhost testing only
	    javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier(
	    new javax.net.ssl.HostnameVerifier(){

	        public boolean verify(String hostname,
	                javax.net.ssl.SSLSession sslSession) {
	            if (hostname.equals("transfer-dev3-1.cisco.com")) {
	                return true;
	            }
	            return false;
	        }
	    });
	}
	
	/**
	 * 
	 * @return
	 */
	public static String getUploadSpec() {
		System.out.println("Enter - getUploadSpec in AsperaTokenGenerator ");
		String request = null;
		String asperaUploadURL = null;
		String authentication = null;
		
		BASE64Encoder enc = null;
		HttpHeaders entityHeaders = null;
		RestTemplate restTemplate = null;
		ResponseEntity<String> result = null;
		HttpEntity<String> requestEntity = null;

		try {
			enc = new BASE64Encoder();
			restTemplate = new RestTemplate();
			trustSelfSignedSSL();
			authentication = enc.encode((new StringBuffer("node_ftsswcfx").append(":").append("ftsswcfx").toString()).getBytes());
			asperaUploadURL = "https://transfer-dev3-1.cisco.com:9092/files/upload_setup";
			request = "{\"transfer_requests\":[{\"transfer_request\":{\"paths\":[{\"destination\":\"/\"}]}}]}";
			entityHeaders = new HttpHeaders();
			entityHeaders.set("Authorization", "Basic " + authentication);
			requestEntity = new HttpEntity<String>(request, entityHeaders);
			result = restTemplate.exchange(asperaUploadURL, HttpMethod.POST, requestEntity, String.class);
			System.out.println("Response from Aspera API:"+result.getBody());
		} catch (Exception e) {
			System.out.println("Error in getUploadSpec in AsperaTokenGenerator: "+ e);
		}
		System.out.println("Exit - getUploadSpec in AsperaTokenGenerator ");
		return result.getBody();
	}
	

	/**
	 * 
	 * @return
	 */
	public static String getDownloadSpec() {
		System.out.println("Enter - getDownloadSpec in AsperaTokenGenerator ");
		String request = null;
		String asperaDownloadURL = null;
		String authentication = null;
		
		BASE64Encoder enc = null;
		HttpHeaders entityHeaders = null;
		RestTemplate restTemplate = null;
		ResponseEntity<String> result = null;
		HttpEntity<String> requestEntity = null;

		try {
			enc = new BASE64Encoder();
			restTemplate = new RestTemplate();
			trustSelfSignedSSL();
			authentication = enc.encode((new StringBuffer("node_ftsswcfx").append(":").append("ftsswcfx").toString()).getBytes());
			asperaDownloadURL = "https://transfer-dev3-1.cisco.com:9092/files/download_setup";
			request = "{\"transfer_requests\":[{\"transfer_request\":{\"paths\":[{\"source\":\"100MB\"}]}}]}";
			entityHeaders = new HttpHeaders();
			entityHeaders.set("Authorization", "Basic " + authentication);
			requestEntity = new HttpEntity<String>(request, entityHeaders);
			result = restTemplate.exchange(asperaDownloadURL, HttpMethod.POST, requestEntity, String.class);
			System.out.println("Response from Aspera API:"+result.getBody());
		} catch (Exception e) {
			System.out.println("Error in getDownloadSpec in AsperaTokenGenerator: "+ e);
		}
		System.out.println("Exit - getDownloadSpec in AsperaTokenGenerator ");
		return result.getBody();
	}
	
	/**
	 * This pre-defined method is used to override the 
	 * SSL Exception when communicating to the web services
	 */
	public static void trustSelfSignedSSL() {
	    try {
	        SSLContext ctx = SSLContext.getInstance("TLS");
	        X509TrustManager tm = new X509TrustManager() {

	            public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
	            }

	            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
	                return null;
	            }

				public void checkClientTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}

				public void checkServerTrusted(
						java.security.cert.X509Certificate[] arg0, String arg1)
						throws java.security.cert.CertificateException {
				}
	        };
	        ctx.init(null, new TrustManager[]{tm}, null);
	        SSLContext.setDefault(ctx);
	    } catch (Exception ex) {
	        ex.printStackTrace();
	    }
	}
	
	public static void main(String[] args) {
		new AsperaTokenGenerator().getUploadSpec();
		new AsperaTokenGenerator().getDownloadSpec();
	}
}
