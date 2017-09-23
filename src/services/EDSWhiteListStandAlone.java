package services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import sun.misc.BASE64Encoder;

public class EDSWhiteListStandAlone {
	
	EDSWhiteListStandAlone() {
		
		String request = null;
		String whiteListURL = null;
		String authentication = null;
		
		BASE64Encoder enc = null;
		HttpHeaders entityHeaders = null;
		RestTemplate restTemplate = null;
		ResponseEntity<String> result = null;
		HttpEntity<String> requestEntity = null;
		
		try {
			enc = new BASE64Encoder();
			restTemplate = new RestTemplate();
			restTemplate.setErrorHandler( new ResponseErrorHandler() {
				public boolean hasError(ClientHttpResponse arg0) throws IOException {
					if( arg0.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR 
							|| arg0.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR ){
						System.out.println("EhiteList API throwing Error");
						return true;
					}
					else{
						System.out.println("No Error Response from WhiteList API");
						return false ;
					}
				}
				public void handleError(ClientHttpResponse arg0) throws IOException {
					/** Explicitly set this method to empty  */
				}
			});
			StringHttpMessageConverter converter = new StringHttpMessageConverter();
			converter.setSupportedMediaTypes(Arrays.asList(new MediaType("text", 
					"plain", Charset.forName("UTF-8"))));
			restTemplate.getMessageConverters().add( 0 , converter ) ;
			trustSelfSignedSSL();
//			authentication = enc.encode((new StringBuffer("swc_user.gen").append(":").append("cisco123").toString()).getBytes());
//			authentication = enc.encode((new StringBuffer("swc_user.gen").append(":").append("$wcfy!4").toString()).getBytes());
			authentication = enc.encode((new StringBuffer("eb-mdf-int.gen").append(":").append("ebfmdfwhitelist").toString()).getBytes());
//			authentication = enc.encode((new StringBuffer("muralive").append(":").append("MAug_2016").toString()).getBytes());
			whiteListURL = "http://wwwin-tools-stage.cisco.com/software/service/check/eds/whitelist/manage/fetch";
			request = "{\"mdfIDs\" : [268438938,268438939,268438944,268438945,268438954,268438957]}";
			entityHeaders = new HttpHeaders();
			entityHeaders.set("Authorization", "Basic " + authentication);
			requestEntity = new HttpEntity<String>(entityHeaders);
			result = restTemplate.exchange(whiteListURL, HttpMethod.GET, requestEntity, String.class);
			System.out.println("Response from WhiteList API:"+result.getBody());
		} catch (Exception e) {
			System.out.println("Error:" + e.getMessage());
		}
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
		new EDSWhiteListStandAlone();
	}

}
