package yPub.java;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

	/**
	 * @author 
	 * 
	 */
	public class BaseDAO {
		
		/**
		 * Class level variable to reference to the JDBCTemplate Object which is
		 * used for DB connections.
		 */
		protected static Connection objJdbcTemplate = null;

		/**
		 * Private method to initialize class level variables.
		 */
		public  BaseDAO() {
			if (objJdbcTemplate == null) {
				
				try {
					objJdbcTemplate = getDBConnection();
				}catch (Exception e) {
					e.printStackTrace();
				}
			}
		}

		/**
		 * This function return the jdbctemplate
		 * 
		 * @return data Connection.
		 */
		Connection getDBConnection() throws Exception{
			String configFile = "dev-config.xml";
		
			//Object SDPLogger;
			//SDPLogger.debugLog(this.getClass(), "getDatCon", "Config File : "+ configFile);
			Connection con = null;
//			BeanFactory factory = new XmlBeanFactory(new UrlResource(BaseDAO.class.getResource(configFile)));
//			//SDPLogger.debugLog(this.getClass(), "getDatCon","After creating bean factory");
//
//			JdbcTemplate datCon = (JdbcTemplate) factory.getBean("ASDJobjdbcTemplate");
//			//SDPLogger.debugLog(this.getClass(), "getDatCon","JdbcTemplate created successfully");
			DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
			
			System.out.println("***Connecting***");
			
			//DEV - ADMIN Schema
//			con = DriverManager.getConnection("jdbc:oracle:thin:@lnxdb-dev-vm-218.cisco.com:1521:CSSWDEV", "swc_admin", "Hl2P6b4M");
			//DEV - APPL Schema
//			con = DriverManager.getConnection("jdbc:oracle:thin:@lnxdb-dev-vm-218.cisco.com:1521:CSSWDEV", "SED_YPUB_U", "Eb3B7i1F");
			//STAGE - APPL Schema
//			con = DriverManager.getConnection("jdbc:oracle:thin:@173.38.3.84:1833:CSSWSTG", "SED_YPUB_U", "c$sco23");
			con = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(CONNECT_TIMEOUT=5)(TRANSPORT_CONNECT_TIMEOUT=3)(RETRY_COUNT=1)(ADDRESS_LIST=(LOAD_BALANCE=ON)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=173.38.3.84)(PORT=1833))(ADDRESS=(PROTOCOL=TCP)(HOST=173.38.3.85)(PORT=1833)))(CONNECT_DATA=(SERVICE_NAME=CSSWSTG.cisco.com)))", "SED_YPUB_U", "Mj8Z9q4P$");
			
			try {
//				BSDLogger.infoLog(this.getClass(), " making the connection jdbc template is "+datCon);
//				BSDLogger.infoLog(this.getClass(), " datasource is  is "+datCon.getDataSource());	
			//con = datCon.getDataSource().getConnection();
			//con = (OracleConnection) WSCallHelper.getNativeConnection((WSJdbcConnection) con);
			
			BSDLogger.infoLog(this.getClass(), " connection made in base dao "+con);
			}catch(Exception e){
				BSDLogger.errorLog(this.getClass(), "Error creating connection"+e);
				e.printStackTrace();
			}
			
			return con;
		}
		
		 protected void closeDBConnection(ResultSet rs,Statement stmt,Connection conn){
				
				try {
					if(null != rs){
						rs.close();
						rs = null;
					}
					
					if(stmt != null){
						stmt.close();
						stmt=null;
					}
					if(conn != null){
						conn.close();
						conn=null;
					}
				} catch (SQLException e) {
					e.printStackTrace();
					BSDLogger.errorLog(this.getClass(), "Fatal Error Occured"+e);
				}
			}

		/**
		 * This function return the jdbctemplate
		 * 
		 * @return data Connection.
		 */
/*		public JdbcTemplate getDatCon(String jdbcTemplate) {
			String configFile = BSDConstants.DEV
			+ BSDConstants.FILEPATH;

			//BSDLogger.debugLog(this.getClass(), "getDatCon", "Config File : "
					//+ configFile);

			BeanFactory factory = new XmlBeanFactory(new UrlResource(this
					.getClass().getResource(configFile)));
			//BSDLogger.debugLog(this.getClass(), "getDatCon",
					//"After creating bean factory");

			JdbcTemplate datCon = (JdbcTemplate) factory.getBean("jdbcTemplateCPR");
			//BSDLogger.debugLog(this.getClass(), "getDatCon", "JdbcTemplate created successfully");
			return datCon;
		}
		*/
		
}
