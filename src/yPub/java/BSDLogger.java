package yPub.java;



import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class BSDLogger {

	//static Log logger = null;
	
	private static Logger logger = null;

	@SuppressWarnings("unchecked")
	public static void infoLog(Class objClass,String logMessage,Throwable objThrowable)
	  {
	    getLogger(objClass);
	    if (logger.isInfoEnabled())
	      logger.info(logMessage,objThrowable);
	  }
	@SuppressWarnings("unchecked")
	public static void infoLog(Class objClass,String logMessage)
	  {
	    getLogger(objClass);

	    if (logger.isInfoEnabled())
	      logger.info(logMessage);
	  }
	@SuppressWarnings("unchecked")
	public static void debugLog(Class objClass,String logMessage,Throwable objThrowable){
		 getLogger(objClass);
		 if(logger.isDebugEnabled()){
			logger.debug(logMessage,objThrowable); 
		 }
	}
	@SuppressWarnings("unchecked")
	public static void debugLog(Class objClass,String logMessage){
		 getLogger(objClass);
		 if(logger.isDebugEnabled()){
			logger.debug(logMessage); 
		 }
	}
	@SuppressWarnings("unchecked")
	public static void errorLog(Class objClass,String logMessage,Throwable objThrowable){
		 getLogger(objClass);
		 if(logger.isEnabledFor(Level.ERROR)){
			logger.error(logMessage,objThrowable); 
		 }
	}
	@SuppressWarnings("unchecked")
	public static void errorLog(Class objClass,String logMessage){
		 getLogger(objClass);
		 if(logger.isEnabledFor(Level.ERROR)){
			logger.error(logMessage); 
		 }
	}
	@SuppressWarnings("unchecked")
	public static void fatalLog(Class objClass,String logMessage,Throwable objThrowable){
		 getLogger(objClass);
		 if(logger.isEnabledFor(Level.FATAL)){
			logger.fatal(logMessage,objThrowable); 
		 }
	}
	@SuppressWarnings("unchecked")
	public static void fatalLog(Class objClass,String logMessage){
		 getLogger(objClass);
		 if(logger.isEnabledFor(Level.FATAL)){
			logger.fatal(logMessage); 
		 }
	}
	@SuppressWarnings("unchecked")
	public static void warnLog(Class objClass,String logMessage,Throwable objThrowable){
		 getLogger(objClass);
		 if(logger.isEnabledFor(Level.WARN)){
			logger.warn(logMessage,objThrowable); 
		 }
	}
	@SuppressWarnings("unchecked")
	public static void warnLog(Class objClass,String logMessage){
		 getLogger(objClass);
		 if(logger.isEnabledFor(Level.WARN)){
			logger.warn(logMessage); 
		 }
	}
/*	@SuppressWarnings("unchecked")
	protected static Log getLogger(Class objClass)
	  {
	   logger = LogFactory.getLog(objClass);
	    return logger;
	  }*/
	

@SuppressWarnings("unchecked")
protected static Logger getLogger(Class objClass)
  {
	
   logger = Logger.getLogger(objClass);
    return logger;
  }
}