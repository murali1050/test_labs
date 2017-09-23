package services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DateTimeZoneManip {
	public static void main(String[] args) throws ParseException {

//		Calendar calendar = Calendar.getInstance();
//		calendar.setTime(new Date());
//		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        sdf.setTimeZone(TimeZone.getTimeZone("PST"));
//        
//        System.out.println(sdf.format(calendar.getTime()));
		
//		TimeZone tz = TimeZone.getTimeZone("PST");
//		 
//	      DateFormat df = DateFormat.getDateTimeInstance();
//	      df.setTimeZone(tz);
//	 
//	      System.out.println("Current time on Wake Island --> " +
//	                         df.format(Calendar.getInstance(tz).getTime()));
		
//		String splitStr = " 279042634:9172546:HELLO,281394067:9172547:HAI,282774238:9172548:BYE,279120799:9172549:SEEYOU,279120819:9172550:HOWARE,281187426:9172551:AMFINE,";
//		String mdfID = "282774238";
//		
//		String []strings = splitStr.split(",");
//		for (String str : strings) {
//			System.out.println("1st:" + str);
//			String[] strings1 = str.split(":");
//			if(strings1[0].equals(mdfID)) {
//				System.out.println("****Am in:"+ strings1[2]);
//			}
//			for(String str1 : strings1) {
//				System.out.println("2nd:" + str1);
//			}
//		}
		
//		Date pDate;
//	    String strDate = "2016-12-15T00:00:00.000-00:00";
//	    Calendar pCal = Calendar.getInstance();
//	    
//	    DateFormat before = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
//	    DateFormat after = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//	    
//	    System.out.println("0: Out:"+strDate);
//	    
//	    Date date = before.parse(strDate);
//	    
//	    System.out.println("1: Out:"+date.getTime());
//	    
//	    pCal.setTime(date);
//	    
//	    System.out.println("2: Out:"+pCal.getTime());
//	    
//	    strDate = after.format(pCal);
//	    
//	    System.out.println("3: Out:"+strDate);
		
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSZ");
//		Date migrationDate = sdf1.parse("2017-08-29T09:58:22.00894-0700");
//		System.out.println("Migration Date: " + migrationDate);
//		
//		SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
//		Date currentDate = df.format(new Date());
		
		
//		SimpleDateFormat sdf1 = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSSSZ");
//		Date migrationDate = sdf1.parse("2017-08-30T09:58:22.00894-0700");
		
		Date migrationDate = javax.xml.bind.DatatypeConverter.parseDateTime("2017-11-11T09:58:22.00894-07:00").getTime();
		System.out.println("Migration Date: " + migrationDate);
		
		Date date = new Date();
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		df.setTimeZone(TimeZone.getTimeZone("America/Los_Angeles"));
//		System.out.println("Current Date/Time: " + df.format(date));
		System.out.println("Current Date/Time(Date Obj)" + df.parse(df.format(date)));
//		System.out.println("Without Parsing:" + date);
		System.out.println("Current after Migration: " + migrationDate.after(date));
		
		long diffInMillies = migrationDate.getTime() - date.getTime();
		TimeUnit timeUnit = TimeUnit.DAYS;
		long difference = timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
		System.out.println(difference);
		
		int diffInDays = (int)( (migrationDate.getTime() - date.getTime()) / (1000 * 60 * 60 * 24) );
		System.out.println(diffInDays);
		
//		Date finaldate = javax.xml.bind.DatatypeConverter.parseDateTime("2017-08-30T09:58:22.00894-07:00").getTime();
//		System.out.println("Final Date:" + finaldate);
		
//		DateFormat targetFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//		Date migrationDate = targetFormat.parse(targetFormat.format(Calendar.getInstance().getTime()));
//		Date currentDate = targetFormat.parse(targetFormat.format(new Date()));
//		System.out.println("MigrationDate:" + migrationDate);
//		System.out.println("CurrentDate:" + currentDate);
	}
}
