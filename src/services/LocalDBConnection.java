package services;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.OracleTypes;

import com.cisco.support.sdp.common.sdpsharedutils.util.JobUtils;

public class LocalDBConnection {

	public static void main(String[] args) throws SQLException {
		
		ResultSet ebReportRS = null;
		Connection conn = null;
		CallableStatement stmt = null;
		
		DriverManager.registerDriver(new oracle.jdbc.driver.OracleDriver());
		System.out.println("***Connecting***");
//		conn = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=ON)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=64.100.61.47)(PORT=1541))(ADDRESS=(PROTOCOL=TCP)(HOST=64.100.61.48)(PORT=1541)))(CONNECT_DATA=(SERVICE_NAME=SCASTG.cisco.com)(SERVER=DEDICATED)))", "SED_APPS", "We2P3x7M");
		conn = DriverManager.getConnection("jdbc:oracle:thin:@(DESCRIPTION=(ADDRESS_LIST=(LOAD_BALANCE=ON)(FAILOVER=ON)(ADDRESS=(PROTOCOL=TCP)(HOST=dbc-stg-2064-vip.cisco.com)(PORT=1523))(ADDRESS=(PROTOCOL=TCP)(HOST=dbc-stg-2065-vip.cisco.com)(PORT=1523)))(CONNECT_DATA=(SERVICE_NAME=CSSWSTG.cisco.com)(SERVER=DEDICATED)))", "PUB_PKG", "c$sco23");
		System.out.println("***Connected***");

//		stmt = conn.prepareCall("begin SWC_EB_REPORT_PKG.Pr_get_detailed_data(?,?,?,?,?,?,?,?); end;");
//		String jobHost = JobUtils.getJobOwner("lae-rtp1-si-132.cisco.com", "EB Reverse Mapping Log Job");
		stmt = conn.prepareCall("begin swc_job_owner_pkg.swc_fetch_job_owner(:1, :2); end;");
		stmt.setString(1, "lae-rtp1-si-132.cisco.com");
		stmt.setString(2, "EB Reverse Mapping Log Job");
		stmt.registerOutParameter(1, OracleTypes.VARCHAR);
		stmt.execute();
		String jobHost = stmt.getString(1);
		System.out.println("job Host Value:" + jobHost);
	}

}
