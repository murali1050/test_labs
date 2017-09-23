package services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

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
import yPub.java.BSDLogger;

public class SWCIntegrationPEGA {

	public static void main(String[] args) {
		
		String message = "POST failed - Checksum value mismatch for the transaction sha512 - \"a2c2986c56a9cb9d3400f5a727e35f0ddcf2dbc9f3d97cc2ac321eb3f988ef44e058265afbcb39207e6a342579f1434c0eb494c4f1101dfad112ef48aef3435b\" value";
		System.out.println(message);
		message = message.replaceAll("\"", "");
		System.out.println(message);
		
		System.out.println("I'm Here");
		String request = "{  \"transactionId\" : \"131696\",  \"xmlId\" : \"173825\",  \"softwareType\" : \"Analog Firmware Loader\",  \"releaseVersion\" : \"v28102016\",  \"transactionStatus\" : null,  \"message\" : \"Successfully Published\",  \"imageId\" : \"3757516\",  \"fileId\" : \"710944\",  \"fileName\" : \"testPega7.tar\",  \"fileStatus\" : \"SUCCESS\",  \"publishStatus\" : \"PUBLISHED\",  \"cloudStatus\" : \"PUBLISHED\",  \"dtrTag\" : \"4_SDSP_710704_1477530721083\",  \"sla\" : \"0\"}";
		String url = "https://ibpm-dev.cisco.com/csw/swpub/api/v1/cases/CISCO-FW-SPFW-WORK 131696-3757516";
		BASE64Encoder enc = new BASE64Encoder();
		String authentication = enc.encode((new StringBuffer("brm.gen").append(":").append("brmGen123").toString()).getBytes());
		HttpHeaders entityHeaders = new HttpHeaders();
		entityHeaders.set("Authorization", "Basic " + authentication);
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler( new ResponseErrorHandler() {
			public boolean hasError(ClientHttpResponse arg0) throws IOException {
				if( arg0.getStatusCode().series() == HttpStatus.Series.CLIENT_ERROR || arg0.getStatusCode().series() == HttpStatus.Series.SERVER_ERROR ){
					System.out.println("PEGA giving error response in PegaNotifyJob constructor ");
					return true;
				}
				else{
					System.out.println("No Error Response from PEGA in PegaNotifyJob constructor ");
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
		HttpEntity<String> requestEntity = new HttpEntity<String>(entityHeaders);
		ResponseEntity<String> result = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
		System.out.println("Pega WS Response Code (Get Service):"+ result.getStatusCode().value());
		if (result.getHeaders().get("etag") != null && result.getHeaders().get("etag").size() > 0) {
			String eTag = result.getHeaders().get("etag").get(0);
			System.out.println("eTag Value:"+ eTag);
			System.out.println("****************");
			if(eTag != null && !eTag.equals("")) {
				entityHeaders.set("If-Match", eTag);
				entityHeaders.set("userid","brm.gen");
				entityHeaders.set("password","brmGen123");
				requestEntity = new HttpEntity<String>(request, entityHeaders);
//				url = "https://wsgx-stage.cisco.com/asdc/services/fprdcpcore/content/cancelcheckout/1000000";
				url = "https://ibpm-dev.cisco.com/csw/swpub/api/v1/cases/CISCO-FW-SPFW-WORK 131696-3757516";
				System.out.println("URL: " + url);
				result = restTemplate.exchange(url, HttpMethod.PUT, requestEntity, String.class);
				System.out.println("Pega WS Response Code (Update Service):"+ result.getStatusCode().value());
				System.out.println("Pega WS Response Message (Update Service):"+ result.getBody());
			}
		}
		
	}
}
