package services;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class SimpleJava {
	
	public static void main(String[] args) throws ParseException {
		
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
	}
}
