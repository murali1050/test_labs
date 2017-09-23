package yPub.java;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import javax.security.cert.CertificateException;
import javax.security.cert.X509Certificate;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.ObjectWriter;
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

public class PegaNotifyJob {
	
	private PegaNotifyJob() {
		
		BSDLogger.infoLog(this.getClass(), "Enter SWC-PEGA Notification job");
		
		String url = null;
		String pyId = null;
		String request = null;
		String pegaPwd = null;
		String pegaUser = null;
		String authentication = null;
		String pegaServiceurl = null;
		
		BASE64Encoder enc = null;
		RestTemplate restTemplate = null;
		ObjectWriter objectWrite = null;
		JobsDAO jobsDAO = new JobsDAO();
		List<SwcPegaNotifyBO> dbResp = null;
		List<SwcPegaNotifyBO> wsResp = new ArrayList<SwcPegaNotifyBO>();
		ResponseEntity<String> result = null;
		HttpEntity<String> requestEntity = null;
		
		try {
			/**
			 * Call the DB Procedure for fetching the Pega Notification List
			 */
			dbResp = jobsDAO.getPegaNotificationList();
			if(dbResp != null && !dbResp.isEmpty()) {
				enc = new BASE64Encoder();
				restTemplate = new RestTemplate();
				objectWrite = new ObjectMapper().writer().withDefaultPrettyPrinter();
				restTemplate.setErrorHandler( new ResponseErrorHandler() {
					public boolean hasError(ClientHttpResponse arg0) throws IOException {
						if( arg0.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR || arg0.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR ){
							BSDLogger.infoLog(this.getClass(), "PEGA giving error response in PegaNotifyJob constructor ");
							return true;
						}
						else{
							BSDLogger.infoLog(this.getClass(), "No Error Response from PEGA in PegaNotifyJob constructor ");
							return false ;
						}
					}
					public void handleError(ClientHttpResponse arg0) throws IOException {
						/**
						 * Explicitly set this method to empty
						 */
					}
				});
				StringHttpMessageConverter converter = new StringHttpMessageConverter();
				converter.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "plain", Charset.forName("UTF-8"))));
				restTemplate.getMessageConverters().add( 0 , converter ) ;
				pegaServiceurl = "http://ibpm.cisco.com/csw/swpub/api/v1/assignments/ASSIGN-WORKLIST {0} {1}!{2}?actionID={3}";
//				pegaServiceurl = "https://ibpm-dev.cisco.com/csw/swpub/api/v1/assignments/ASSIGN-WORKLIST {0} {1}!{2}?actionID={3}";
//				pegaServiceurl = "https://ibpm-stage.cisco.com/csw/swpub/PRRestService/api/v1/assignments/ASSIGN-WORKLIST {0} {1}!{2}?actionID={3}";
				pegaUser = "brms_wb.gen";
				pegaPwd = "brmswb-1234";
//				pegaUser = "brm.gen";
//				pegaPwd = "brmGen123";
				authentication = enc.encode((new StringBuffer(pegaUser).append(":").append(pegaPwd).toString()).getBytes());
				BSDLogger.infoLog(this.getClass(), "Pega Service URL: "+ pegaServiceurl + " - Pega User: "+ pegaUser + " - Pega Password: "+pegaPwd);
				BSDLogger.infoLog(this.getClass(), "Authentication: "+ authentication);
				HttpHeaders entityHeaders = new HttpHeaders();
				entityHeaders.set("Authorization", "Basic " + authentication);
				entityHeaders.set("userid","brm.gen");
				entityHeaders.set("password","brmGen123");
				trustSelfSignedSSL();
				for (SwcPegaNotifyBO swcPegaNotify : dbResp) {
					url = null;
					pyId = null;
					request = null;
					if(swcPegaNotify.getSource().equalsIgnoreCase("T")) {
						pyId = swcPegaNotify.getContent().getTransactionId();
					} else {
						pyId = new StringBuffer(swcPegaNotify.getContent().getTransactionId())
						.append("-")
						.append(swcPegaNotify.getContent().getImageId()).toString();
					}
					request = objectWrite.writeValueAsString(swcPegaNotify);
					requestEntity = new HttpEntity<String>(request, entityHeaders);
					if(("4.4").equalsIgnoreCase(swcPegaNotify.getPegaStepID()) && 
								!("T").equalsIgnoreCase(swcPegaNotify.getSource())) {
						notifiyPegaForUpdate(pyId, request, entityHeaders, swcPegaNotify);
					} else if(("4.4").equalsIgnoreCase(swcPegaNotify.getPegaStepID()) 
							&& ("T").equalsIgnoreCase(swcPegaNotify.getSource())
								&& ("ERROR").equalsIgnoreCase(swcPegaNotify.getContent().getTransactionStatus())) {
						swcPegaNotify.getContent().setTransactionClose("TRUE");
						request = objectWrite.writeValueAsString(swcPegaNotify);
						notifiyPegaForUpdate(pyId, request, entityHeaders, swcPegaNotify);
					} else {
						url = MessageFormat.format(pegaServiceurl, swcPegaNotify.getCaseTypeID(), pyId, 
								swcPegaNotify.getFlowName(), swcPegaNotify.getPegaStepName());
						BSDLogger.infoLog(this.getClass(), "Final: Pega Service URL:"+ url);
						BSDLogger.infoLog(this.getClass(), "Request to PEGA:"+ request);
						try {
							result = restTemplate.exchange(url, HttpMethod.POST, requestEntity, String.class);
							BSDLogger.infoLog(this.getClass(), "Pega WS Response Code:"+ result.getStatusCode().value());
							BSDLogger.infoLog(this.getClass(), "Pega WS Response Message:"+ result.getBody());
							swcPegaNotify.setPegaStatusCode(String.valueOf(result.getStatusCode().value()));
							swcPegaNotify.setPegaStatusMSG(result.getBody());
						} catch (Exception e) {
							BSDLogger.infoLog(this.getClass(), "Pega Error Response Message:"+ e.getMessage());
							BSDLogger.errorLog(this.getClass(), "Got expection" + e);
							swcPegaNotify.setPegaStatusCode("500");
						}
					}
					wsResp.add(swcPegaNotify);
				}
			}
		} catch (Exception e) {

			BSDLogger.errorLog(this.getClass(), "Got expection" + e);
		}
		if(!wsResp.isEmpty()) {
			jobsDAO.updatePegaReponse(wsResp);
		}
		BSDLogger.infoLog(this.getClass(), "Exit SWC-PEGA Notification job");
	}
	
	private void notifiyPegaForUpdate(String pyId, String request,
			HttpHeaders entityHeaders, SwcPegaNotifyBO swcPegaNotify) {
		String url = null;
		String eTag = null;
		String pegaServiceurl = null;
		
		RestTemplate restTemplate = null;
		ResponseEntity<String> result = null;
		HttpEntity<String> requestEntity = null;
		try {
			restTemplate = new RestTemplate();
			restTemplate.setErrorHandler( new ResponseErrorHandler() {
				public boolean hasError(ClientHttpResponse arg0) throws IOException {
					if( arg0.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR || arg0.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR ){
						BSDLogger.infoLog(this.getClass(), "PEGA giving error response in PegaNotifyJob constructor ");
						return true;
					}
					else{
						BSDLogger.infoLog(this.getClass(), "No Error Response from PEGA in PegaNotifyJob constructor ");
						return false ;
					}
				}
				public void handleError(ClientHttpResponse arg0) throws IOException {
					/**
					 * Explicitly set this method to empty
					 */
				}
			});
			StringHttpMessageConverter converter = new StringHttpMessageConverter();
			converter.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "plain", Charset.forName("UTF-8"))));
			restTemplate.getMessageConverters().add( 0 , converter ) ;
			pegaServiceurl = "https://ibpm.cisco.com/csw/swpub/api/v1/cases/{0} {1}";
//			pegaServiceurl = "https://ibpm-dev.cisco.com/csw/swpub/api/v1/cases/{0} {1}";
//			pegaServiceurl = "https://ibpm-stage.cisco.com/csw/swpub/PRRestService/api/v1/cases/{0} {1}";
			url = MessageFormat.format(pegaServiceurl, swcPegaNotify.getCaseTypeID(), pyId);
			BSDLogger.infoLog(this.getClass(), "Final: Pega Service URL:"+ url);
			BSDLogger.infoLog(this.getClass(), "Request to PEGA:"+ request);
			trustSelfSignedSSL();
			requestEntity = new HttpEntity<String>(entityHeaders);
			result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
			BSDLogger.infoLog(this.getClass(), "Pega WS Response Code (Get Service):"+ result.getStatusCode().value());
			if (result.getHeaders().get("etag") != null && result.getHeaders().get("etag").size() > 0) {
				eTag = result.getHeaders().get("etag").get(0);
				BSDLogger.infoLog(this.getClass(), "eTag Value:"+ eTag);
				if(eTag != null && !eTag.equals("")) {
					entityHeaders.set("If-Match", eTag);
					entityHeaders.set("userid","brms_wb.gen");
					entityHeaders.set("password","brmswb1234");
					requestEntity = new HttpEntity<String>(request, entityHeaders);
					result = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
					BSDLogger.infoLog(this.getClass(), "Pega WS Response Code (Update Service):"+ result.getStatusCode().value());
					BSDLogger.infoLog(this.getClass(), "Pega WS Response Message (Update Service):"+ result.getBody());
					swcPegaNotify.setPegaStatusCode(String.valueOf(result.getStatusCode().value()));
					swcPegaNotify.setPegaStatusMSG(result.getBody());
				} else {
					swcPegaNotify.setPegaStatusCode(String.valueOf(result.getStatusCode().value()));
					swcPegaNotify.setPegaStatusMSG("E-Tag is not available in the Response header of GET service");
				}
			} else {
				swcPegaNotify.setPegaStatusCode(String.valueOf(result.getStatusCode().value()));
				swcPegaNotify.setPegaStatusMSG("E-Tag is not available in the Response header of GET service");
			}
		} catch (Exception e) {
			swcPegaNotify.setPegaStatusCode("500");
			BSDLogger.errorLog(this.getClass(), "Got expection" + e);
		}
	}

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
		System.out.println("I'm Here - in Main Method");
		new PegaNotifyJob();
	}
}
