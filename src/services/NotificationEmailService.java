package services;

import java.util.Properties;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;

/**
 * This Service is used to send the notification Email based 
 * on the Recipient's notification setting
 * 
 * @author Infosys
 * 
 * @version 1.0, October 2012
 *
 */

public class NotificationEmailService {
	
	public static final String CISCO_LIFE = "cisco.life";
	public static final String CISCO_JAVA_MAIL = "javamail.cisco.com";
	public static final String SMTP_HOST = "mail.smtp.host";
	public static final String FROM_MAIL_ID = "do-not-reply-asdc@cisco.com";
	public static final String UTF_CONTENT_TYPE = "text/html;charset=UTF-8";
	public static final String EMAIL_ID = "@cisco.com";
	public static final String AUTO_GENERATED_MAIL = "Auto-Generated Mail From DCP Notification";
	public static final String SUCCESS = "1";
	public static final String FAILURE = "0";
	public static final String LDAP_INTERNAL_ACCESS = "4";
	public static final String LDAP_MAIL = "mail";
	public static final String CISCOLIFE_DEV = "DEV";
	public static final String CISCOLIFE_STAGE = "STAGE";
	public static final String CISCOLIFE_LT = "LT";
	public static final String CISCOLIFE_PROD = "PROD";
	public static final String STAGE_ENV_CONSTANT = "[Stage] : ";
	public static final String DEV_ENV_CONSTANT = "[Dev] : ";
	public static final String COMMA = ",";
	
	/**
	 * This method is used to send the email to the multiple user with the same content and subject
	 * In mail, we can add attachment, TO, CC, BCC as addressees
	 * 
	 * @param subject
	 * 			String
	 * @param content
 	 * 			String
	 * @param toRecipient
	 * 			String (Comma-Separated)
	 * @param ccRecipient
	 * 			String (Comma-Separated)
	 * @param bccRecipient
	 * 			String (Comma-Separated)
	 * @param attachment
	 * 			String (Comma-Separated)
	 * @return String
	 * 			If mail send successfully, success msg will return otherwise it returns null   
	 * @throws MessagingException
	 * 			Exception
	 */
	
	public String sendNotificationEmail(String subject, String content, String toRecipient, 
							String ccRecipient, String bccRecipient, String attachment) throws MessagingException{
		
		String ciscoLife = null;
		
		try {
		
			//Get the system properties 
			Properties properties = System.getProperties();
			
			//Put the CISCO's host ID's to the SMTP host
	        properties.put(SMTP_HOST, CISCO_JAVA_MAIL);
	
	        Session session = Session.getDefaultInstance(properties);
	        
	        MimeBodyPart messageBodyPart = new MimeBodyPart();
	        
	        //Set the Content to the body of the mail
			messageBodyPart.setText(content, UTF_CONTENT_TYPE);
			
			Multipart multipart = new MimeMultipart();
			
			multipart.addBodyPart(messageBodyPart);
			
			MimeMessage message= new MimeMessage(session);
			
			ciscoLife = System.getProperty(CISCO_LIFE);
			
			if(ciscoLife != null && subject != null && CISCOLIFE_STAGE.equalsIgnoreCase(ciscoLife)) {
				
				subject = new StringBuffer(STAGE_ENV_CONSTANT).append(subject).toString();
			}
			
			if(ciscoLife != null && subject != null && CISCOLIFE_DEV.equalsIgnoreCase(ciscoLife)) {
				
				subject = new StringBuffer(DEV_ENV_CONSTANT).append(subject).toString();
			}
			
			//Set the Subject of the mail
			message.setSubject(subject);
//			message.setSubject(subject, UTF_CONTENT_TYPE);
			
			//Set the From Mail-ID 
			message.setFrom(new InternetAddress(FROM_MAIL_ID));
			
			String[] toRecipArr = null;
			
			//Add the TO Recipient
			if(toRecipient != null) {
				
				toRecipArr = toRecipient.split(COMMA);
				
				System.out.println("Mail is sending to : " + toRecipient);
			
				for(String recip : toRecipArr) {
					
					if(recip.contains("@")) {
						
						message.addRecipient(Message.RecipientType.TO, new InternetAddress(recip));
						
					} else {
						
						message.addRecipient(Message.RecipientType.TO, 
								new InternetAddress(recip + EMAIL_ID));
					}
				}
				
				if (!(System.getProperty(CISCO_LIFE).equalsIgnoreCase(CISCOLIFE_PROD))) {
					
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress("muralive@cisco.com"));
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress("kjyothis@cisco.com"));
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress("smudassi@cisco.com"));
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress("ricjose@cisco.com"));
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress("ravganga@cisco.com"));
					message.addRecipient(Message.RecipientType.BCC, new InternetAddress("jakaushi@cisco.com"));
				}
			}
			
			//Add the CC Recipient, if any
			String[] ccRecipArr = null;
			
			if(ccRecipient != null) {
				
				ccRecipArr = ccRecipient.split(COMMA);
			
				for(String ccrecip : ccRecipArr) {
					
					message.addRecipient(Message.RecipientType.CC, 
							new InternetAddress(ccrecip + EMAIL_ID));
					
				}
			}
			
			String[] bccRecipArr = null;
			
			//Add the BCC Recipient, if any
			if(bccRecipient != null) {
				
				bccRecipArr = bccRecipient.split(COMMA);
			
				for(String bccrecip : bccRecipArr) {
					
					message.addRecipient(Message.RecipientType.BCC, 
							new InternetAddress(bccrecip + EMAIL_ID));
					
				}
			}
			
			/**
			 *  Check if the Attachment is null or not, if its not null create the 
			 *  datasource for the file location which is passed from the controller
			 *  Finally add the attachment to the body part
			 */
			
			if(attachment != null) {
				
				BodyPart messageAttachment = new MimeBodyPart();
				
				DataSource source = new FileDataSource(attachment);
				
				messageAttachment.setDataHandler(new DataHandler(source));
				
				int pos = attachment.lastIndexOf("\\");
				
				//Get the filename from the attachment location
				String fileName = attachment.substring(pos + 1, attachment.length());
				
				messageAttachment.setFileName(fileName);
				
				multipart.addBodyPart(messageAttachment);
			}
			
			message.setContent(content, "text/html;charset=UTF-8");
//			message.setContent(multipart, "text/html;charset=UTF-8");
//			message.setContent(multipart);
			
			Transport.send(message);
		}
		catch (Exception e) {
			
			System.out.println("Error in the SendNotificationEmail : " +  e.getMessage());
			
			return FAILURE;
		}
		
		System.out.println("E-Mail Send Successfully");
		
		return SUCCESS;
	}
	
}
