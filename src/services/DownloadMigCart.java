package services;

import java.io.IOException;
import java.nio.charset.Charset;
import java.text.MessageFormat;
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

public class DownloadMigCart {
	
	DownloadMigCart() {
		
		String imageIds = null;
		String serviceURL = null;
		String cartServiceURL = null;
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
//						System.out.println("No Error Response from Cart Service API");
						return false ;
					}
				}
				public void handleError(ClientHttpResponse arg0) throws IOException {
					/** Explicitly set this method to empty  */
				}
			});
			StringHttpMessageConverter converter = new StringHttpMessageConverter();
			converter.setSupportedMediaTypes(Arrays.asList(new MediaType("text", "plain", Charset.forName("UTF-8"))));
			restTemplate.getMessageConverters().add( 0 , converter ) ;
			trustSelfSignedSSL();
			authentication = enc.encode((new StringBuffer("dcn.gen").append(":").append("c1$co23!#").toString()).getBytes());
			cartServiceURL = "https://tools-dev.cisco.com/support/swcws/DownloadCartServlet?imageGuid={0}&flag=0&oper=imagedetails";
			imageIds = "F513BA289D3DB6128FC0AFEB1D64EAAA2E12C09B,F08D6DF0A07AD2CFC8A27592E12632ECB10BA0F0,5C1AA9C8B336786B136F2B4CB47F8A2B55F4563C,AFCCF9CDD071169E4BA6E90E693281A2D75AA1F3,8B6ED55A9D262550E40C27CBE1DED4316E50C3D7,BCCFBA72AB6CE63BF320EF5FEDBBFD3A70426D17,DC104F51D245104E10C63B4E8E3615C7E8E85E42,6546ACEC78052E0EA6354367D4E1CE4D0DBC229D,F04228B30FC8BF63E45518FE981EFBA2D405BCDE,BCD2A5F306C88376176FE7ACF22A9046FABDDF68,816F0A8599375FA2F58DA356531A26A22D27C839,DEE03808CCCED0C8801CD2FE506DBA4CC6A179FD,146BA6367683F5CB7B2B5950857971B055CD9BD2,076C9F0A9B50FF55DE692A38988BDFA3796D4905,8A9605525B188EFE93C20815E00C1AABAA8E10ED,E275F59A7F6AEA53ACAD6EC65355279478D24449,875AC9BF0A49E96AFC496E5E215A2EE9399041F8,F35EC05CB481ACEBCB8F60FFFFAAFEA053EA7ECA,153A0A8DAE1CEFC89D16D5FCAF318CE516CF412D,E03F35799D2ED49231AF7A2A1733E75EEF1A63AF,34E88941D53F3771EB0403D3ED65570D50574F06,2385DB5BEDCC031B192EFEB09E823FD5EB2231A4,A1FA23ED084BC0B6F5A23EEF865B1B287714A2CF,B715DD95F300E8F2D30681DF2BC1C745568EAB1B,B69A75E08408B9AFA76904EBE08AA5A822593C8A,1DD9C4EC2E5B76E5D5CD4B36E800316D84625DFD,C8BA3E153C24976D2FC68BEF7D5CE3E7F2C65DAA,B2746D57C2862F2B91176AECC30F3A0977614C3A,DDB04A86A8AC92ABD5999FD930996E4AE6AF6726,30276FB1F33700B33C7BEBF4C61DB61FA7C6A3DF,97CAE65F4F51E14009DD0BAF2751B5BE8016E88A,A42CF12896AE589D193C2168F024B2E24287D1F1,5104166B5B1D760B24477D0E1205893A495970B0,FDC8B2DEC296259D4E6E317D1D71BA819B28EE95,301B1EAE38C3F8ECC521540BC9017F686884DA8E,3800D26F7A7F3E8132B7594D679AA8A49F117BB6,19AE7674DCA5ACCB89581109C0815E0DEBA95D31,115A3176EE862E7AB5E164CBCD6CC378CB175E15,23E8299F74D40615427AC4A58745A7A32A0795BA,3630AC8D96D6E77DA4B834C292F86CB203411537,5DB4C7AAF18CA1975885C3987B69DDA0F106D586,80297C894CA7B388F08250FD46B55AC28DEDEFFD,523E692D82FBB43F1F3CEBB45E02C545B27869CB,051D28AFB3DDE0AB4CCC8FBD21E149F25B3D56A2,3CFD6938A19C48CCB71E6E0601ED1F3ED54C7B04,E12988033AD6BB46137E5EA6882E560A6A3A0724,3062496EB15227A0166B077F71E6E6259A8C7097,F95762F8E92062DD7351A4575EF820EA22C328B1,94DB9380749BE2C51D27D66D5337243F9AF3A17C,6D9EAA7B21F843EA0D2138E9524ABDE3FEAFB50E,3E189090EAAC444E77656B4719B5F2BADA17ADDC,ACE59E0AA08864D8F7BEC05AEAC840931921F0FC,5486B1E0537F1D94439BE53F2DD60653C08B6E18,7FF02A9EC2E7951E97D24BA41AE8333E0CA986B6,DEC3DF20266D765881AD2B42BF5B5F47D32FD210,46EDB07C074C4DFA1B0B1E13F0EC0901732C82EE,D4BE5EBB7532E093C0CDB5279131D6C643764233,B21CCF60BD2E43D587F594D2ED59D40623DD8A82,1A8E604E95D456076BCAC633DCCAEBDF5D4D3308,69396857651B7001F224E868F98498DBF3A05A12,5935528F9C8536A616B16B2F7A78113CD7D4ECB9,CF91BBF61983BC310BAA9AC74CE32565F4535837,D659B20834F272D2F3446B376843EC41643E2709,1603128086794088E6CC54DE171E8D57EF3F45DE,AF6A441F4691A91B7306143091A544FF28C83EE9,DE6D7C5F4A566B925045832189BA88027EEA90E2,4E0ED73FDCFFA6C343DC1ACD69DA3183DCA24EA6,13393F14B2F269B734558DD8B34D7585173ACF1A,590F353150EFC6E731C081189206DE48FC699D89,13E517FE9AE1A86DF453CEF8C13F7BBCC8E51577,56B413F17E32EBFFE2DD293792830E65D71E7D59,EF7BC7A42067AC268F05C46244AB8BA10A68EEB2,835683F5B2D2B1949818FDA531C0F84779CFBB00,74E7D69285C711AC9D2A56ECE6283616DA784812,A06BDF86A9A953A5CA0B3AC5F350465FC105B51C,CD8C6FEE430F41E8CF470A92652883C1C12BAAF7,F8796940CF519D5B519ADA0E8D68888EC59ACC28,F6AA71DFE6E747E1571924831D3359CAFFF0B109,98AFDC3E3418DDB69F8F1F31F06E49FC2AC799EF,429AD8CC0E9A4704055B0CB674655594FDDCD9E4,BAAA955492B86391EC2799B0DCE132183BA71C89,7E616F8C89330D2FE1CEDB53C900B98628E9A699,CC2447BCF3EB85A74DE0D73852AA5D476CBA49B1,37DC89ED4120DB92545A0B5E8D75CA3D6E70579D,ADC01A7A4A3A255A684D3E22191EFAE306C895D0,A6A29394DCE6AE56EC9D67BC4FA3960A82F133C6,D2DCCE406ABD82AB59AA7B2586EC870CA6A494A9,8C65B8EF52F16ED6294B64C3D5D86FCD621CF259,4587A59597DCA35CA6A98CFBEDFE0E11D4CB5674,1035F2FFB675D639EC0F804E3062948F7478CA1B,FABA8A59EE7E20D660F51DB234114AFE383AA22D,905AD12FA356F26DEFC135BA75B026AB9B4126A3,A2E410E4665560CA74B0D6F338FDCAAC853E56B2,BFE6157D23A19899D30127DF043E3DFAD2FFBE55,7C9BDF8BEDAB4C9EA31CCC204A482F81243D55F7,16F23892A50E2351A3D1C6AEECD58E95F08A0F1E,5F2CC341E2556DD08B6864B920CDE8C527F0551A,2CFB14C7E1D3B49262BF1E1B5036DC338E92A748,4A640585947313F6091E5274FB06B5415DE114E7,36DBA618DE4C257D63633A455535BF2641272B85,0776C059EE86B7C019C03ADA964E9A39D25963F7,69589AA19881F2B74BF41373BADA7AFFE993865A,A08C41A6CAF1113F75738AAC942D07BEFAA14ADE,33BCF4878DBD396734C3F038CB898B172E9245E7,DECF851D4C6C9F573473FC782AA0F655899E8A2A,7CD2E2728ECE8C34AB69D2F7495ADF34A4F23718,EC0FD3A0C6387E31DE53DF3CAB8139D79BB5FB0E,A7DD5EA364512C5D4FA29AAEECC93785C86D5294,84AD79700874A42647BAC57F387A63578760831C";
			entityHeaders = new HttpHeaders();
			entityHeaders.set("Authorization", "Basic " + authentication);
			requestEntity = new HttpEntity<String>(entityHeaders);
			for(String str : imageIds.split(",")) {
				serviceURL = MessageFormat.format(cartServiceURL, str);
				result = restTemplate.exchange(serviceURL, HttpMethod.GET, requestEntity, String.class);
				String response = result.getBody();
				if(!response.contains("isK9")) {
					System.out.println(str);
					System.out.println("Response from Cart Service API:"+str+result.getBody());
				}
			}
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
		new DownloadMigCart();
	}

}
