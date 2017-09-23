package services;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;
import org.apache.log4j.MDC;
import org.apache.log4j.xml.DOMConfigurator;

public class TestAppender extends Thread{
	
	private static Logger performanceLogger = LogManager.getLogger("local_test.perf");
	private static Logger LOGGER = Logger.getLogger(TestAppender.class);

//	public TestAppender (String s) { 
//	    super(s); 
//	  }
	@Override
	public void run() {
		for (int j = 10001; j<=10050; j++) {
			testMethod(j);
		}
	}
	private static void testMethod (int i) {
		try{
        		MDC.put("transId",i);
        		MDC.put("mdfId",i+i);
        		MDC.put("decisionType","decision");
        		MDC.put("userId","muralive@cisoco.comasdssfdsgfdg");
        		MDC.put("mtdName","Test-Main");
        		MDC.put("executionTime",i);
        		SimpleDateFormat dtFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss");
        		MDC.put("createDate",dtFormat.format(new Date()));
        		SimpleDateFormat tsFormat = new SimpleDateFormat("MM-dd-yyyy HH:mm:ss:SSSSSS");
        		MDC.put("startTime",tsFormat.format(new Date()));
        		MDC.put("endTime",tsFormat.format(new Date()));        		
        		MDC.put("edsToken","token"+i);
        		System.out.println("Value of i:" + i + " - Thread Name:" + Thread.currentThread().getName());
        		performanceLogger.info(i);
    	}catch(Exception e){
    		String errorMsg = "ERROR:"+e.getLocalizedMessage();
    		LOGGER.error("Exception Exception "+errorMsg, e);
    	}finally{
			MDC.clear();
		}
	}
	public static void main(String[] args) {
		
		DOMConfigurator.configure("C:\\Users\\muralive\\Workspaces\\SWC\\Test_Labs\\resource\\log4j-local.xml");
		
//		List<Thread> threadList = new ArrayList<Thread>();
		
//			Thread t1 = new TestAppender();
//			Thread t2 = new TestAppender();
//			Thread t3 = new TestAppender();
//			Thread t4 = new TestAppender();
//			Thread t5 = new TestAppender();
//			Thread t6 = new TestAppender();
//			Thread t7 = new TestAppender();
//			Thread t8 = new TestAppender();
//			Thread t9 = new TestAppender();
//			Thread t10 = new TestAppender();
//			Thread t11 = new TestAppender();
//			Thread t12 = new TestAppender();
		
		for(int i = 1; i <= 60; i++) {
			Thread temp= new TestAppender();
		    temp.start();
		}
		
//			t1.setName("Thread-1");
//			t2.setName("Thread-2");
//			t3.setName("Thread-3");
//			t4.setName("Thread-4");
//			t5.setName("Thread-5");
//			t6.setName("Thread-6");
//			t7.setName("Thread-7");
//			t8.setName("Thread-8");
//			t9.setName("Thread-9");
//			t10.setName("Thread-10");
//			t11.setName("Thread-11");
//			t12.setName("Thread-12");
		
		long startTime = System.currentTimeMillis();
		
//			t1.start();
//			threadList.add(t1);
//			t2.start();
//			threadList.add(t2);
//			t3.start();
//			threadList.add(t3);
//			t4.start();
//			threadList.add(t4);
//			t5.start();
//			threadList.add(t5);
//			t6.start();
//			threadList.add(t6);
//			t7.start();
//			threadList.add(t7);
//			t8.start();
//			threadList.add(t8);
//			t9.start();
//			threadList.add(t9);
//			t10.start();
//			threadList.add(t10);
//			t11.start();
//			threadList.add(t11);
//			t12.start();
//			threadList.add(t12);
//			for(Thread t : threadList) {
//		        // waits for this thread to die
//				t.join();
//			}
		
		long endTime = System.currentTimeMillis();
		long totalTime = endTime - startTime;
		
		LogManager.shutdown();
		System.out.println("Time Taken (MilliSec):" + totalTime);
		System.out.println("Time Taken (Seconds):" + TimeUnit.MILLISECONDS.toSeconds(totalTime));
		System.out.println("Time Taken (Minuetes):" + TimeUnit.MILLISECONDS.toMinutes(totalTime));
		System.out.println("***Execution Completed***");
	}
}
