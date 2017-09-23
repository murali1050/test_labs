package yPub.java;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


import oracle.jdbc.OracleTypes;
import oracle.sql.ARRAY;

public class JobsDAO extends BaseDAO{

	public List<SwcPegaNotifyBO> getPegaNotificationList() {
		BSDLogger.infoLog(this.getClass(), "Entering JobsDAO:getPegaNotificationList");
		
		int lastSeqID = 0;
		String message = null;
		String source = null;
		String yPubStatus = null;
		String cloudStatus = null;
		
		Content content = null;
		ResultSet pegaNotifyRS = null;
		CallableStatement stmt = null;
		Connection springConnection = null;
		SwcPegaNotifyBO swcPegaNotify = null;
		List<SwcPegaNotifyBO> swcPegaNotifyList =null;
		try{
			BSDLogger.infoLog(this.getClass(), "Checking before the Connection getPegaNotificationList:---->");
			springConnection = objJdbcTemplate;
			BSDLogger.infoLog(this.getClass(), "Checking After the Connection getPegaNotificationList:---->" + springConnection);
			BSDLogger.infoLog(this.getClass(),"Connection is made: "+springConnection);
		
			stmt = springConnection.prepareCall("begin Swc_jobs_Pkg.swc_process_pega_notify_dummy(?,?,?,?); end;");
			stmt.registerOutParameter(1, OracleTypes.CURSOR);
			stmt.registerOutParameter(2, OracleTypes.INTEGER);
			stmt.registerOutParameter(3, OracleTypes.INTEGER);
			stmt.registerOutParameter(4, OracleTypes.VARCHAR);
			stmt.execute();   
			
			lastSeqID = stmt.getInt(2);
			int statusFlag = stmt.getInt(3);
			String statusMessage = stmt.getString(4);
			BSDLogger.infoLog(this.getClass(), "Last Processed Sequence ID : " + lastSeqID);
			BSDLogger.infoLog(this.getClass(), "Flag : " + statusFlag);
			BSDLogger.infoLog(this.getClass(), "Message : " + statusMessage);
			if(statusFlag == 3) {
				pegaNotifyRS = (ResultSet) stmt.getObject(1);
				
				if (pegaNotifyRS != null) {
					
					swcPegaNotifyList = new ArrayList<SwcPegaNotifyBO>();
					while (pegaNotifyRS.next()) {
						source = null;
						content = new Content();
						swcPegaNotify = new SwcPegaNotifyBO();
						
						swcPegaNotify.setSwcPubId(pegaNotifyRS.getInt("SWC_YPUB_ID"));
						swcPegaNotify.setCaseTypeID(pegaNotifyRS.getString("CASE_TYPE_ID"));
						swcPegaNotify.setPegaStepID(pegaNotifyRS.getString("PEGA_STEP_ID"));
						swcPegaNotify.setFlowName(pegaNotifyRS.getString("FLOW_NAME"));
						swcPegaNotify.setPegaStepName(pegaNotifyRS.getString("PEGA_STEP_NAME"));
						source = pegaNotifyRS.getString("SOURCE");
						swcPegaNotify.setSource(source);
						
						content.setTransactionId(String.valueOf(pegaNotifyRS.getInt("TRANSACTION_ID")));
						content.setXmlId(String.valueOf(pegaNotifyRS.getInt("XML_ID")));
						content.setSoftwareType(pegaNotifyRS.getString("SW_TYPE_NAME"));
						content.setReleaseVersion(pegaNotifyRS.getString("RELEASE_VERSION"));
						message = pegaNotifyRS.getString("MESSAGE");
						message = message.replaceAll("\"", "");
						content.setMessage(message);
						content.setSLA(String.valueOf(pegaNotifyRS.getLong("SLA_TIME")));
						content.setTransactionClose("FALSE");
						
						yPubStatus = pegaNotifyRS.getString("YPUBLISH_STATUS");
						if(null != yPubStatus && !("").equals(yPubStatus)) {
							if(("YCLEAN-C".equalsIgnoreCase(yPubStatus) 
										|| "YCLEAN-E".equalsIgnoreCase(yPubStatus))) {
								content.setPublishStatus("ROLLEDBACK");
							} else if(yPubStatus.startsWith("Y")) {
								content.setPublishStatus("-");
							} else {
								content.setPublishStatus(yPubStatus);
							}
						}
						
						content.setFileId(String.valueOf(pegaNotifyRS.getInt("FILE_ID")));
						content.setFileName(pegaNotifyRS.getString("FILE_NAME"));
						content.setImageId(pegaNotifyRS.getString("IMAGE_ID"));
						content.setDtrTag(pegaNotifyRS.getString("CLOUD_DTR_TAG"));
						cloudStatus = pegaNotifyRS.getString("CLOUD_STATUS");
						if(null != cloudStatus && !("").equals(cloudStatus) 
								&& "C".equalsIgnoreCase(source)) {
							if("YCLOUD-NW".equalsIgnoreCase(cloudStatus)) {
								content.setCloudStatus("QUEUED");
							} else if("YCLOUD-C".equalsIgnoreCase(cloudStatus)
									|| "YCLOUD-E".equalsIgnoreCase(cloudStatus)) {
								content.setCloudStatus("NOTIFIED");
							} else {
								content.setCloudStatus(cloudStatus);
							}
						} else {
							content.setCloudStatus(cloudStatus);
						}
						
						if(source != null && source.equalsIgnoreCase("T")) {
							content.setTransactionStatus(pegaNotifyRS.getString("TRANSACTION_STATUS"));
						}else {
							content.setFileStatus(pegaNotifyRS.getString("TRANSACTION_STATUS"));
						}
						swcPegaNotify.setContent(content);
						swcPegaNotifyList.add(swcPegaNotify);
					}
				} else {
					BSDLogger.infoLog(this.getClass(), "Cursor is NULL or Empty");
				}
			} else if(statusFlag == 2) {
				BSDLogger.infoLog(this.getClass(), "There are no jobs to Process");
			} else {
				BSDLogger.infoLog(this.getClass(), "Error in SQL Procedure (Swc_jobs_Pkg.swc_process_pega_notify) - SQL Error MSG: " 
									+ statusMessage);
				BSDLogger.errorLog(this.getClass(), "SQL Exception Occured: "+statusMessage);
			}
		} catch (SQLException e) {
			BSDLogger.errorLog(this.getClass(), "Fatal Error Occured"+e);
			e.printStackTrace();
		}catch (Exception e){
			BSDLogger.errorLog(this.getClass(), "Fatal Error Occured"+e);
			e.printStackTrace();
		}finally{
			BSDLogger.infoLog(this.getClass(), "Last Processed Sequence ID : " + lastSeqID);
			closeDBConnection(null, stmt, null);
			
		}
		BSDLogger.infoLog(this.getClass(), "Exiting JobsDAO:getPegaNotificationList");
		return swcPegaNotifyList;
	}

	public void updatePegaReponse(List<SwcPegaNotifyBO> wsResp) {
		BSDLogger.infoLog(this.getClass(), "Entering JobsDAO:updatePegaReponse");
		
		int statusFlag = 0;
		String statusMessage = null;
				
		ARRAY pegaNotifyArray = null;
		CallableStatement stmt = null;
		Connection springConnection = null;
		
		List<SwcPegaObject> swcPegaObjectsList = null;
		
		try{
			BSDLogger.infoLog(this.getClass(), "Checking before the Connection updatePegaReponse:---->");
			springConnection = objJdbcTemplate;
			BSDLogger.infoLog(this.getClass(), "Checking After the Connection updatePegaReponse:---->" + springConnection);
			BSDLogger.infoLog(this.getClass(),"Connection is made: "+springConnection);
			
			swcPegaObjectsList = new ArrayList<SwcPegaObject>();
			swcPegaObjectsList = buildSwcPegaObject(wsResp);
			pegaNotifyArray = SwcPegaObject.buildOracleArrayFromList(springConnection, swcPegaObjectsList);
			stmt = springConnection.prepareCall("begin Swc_jobs_Pkg.update_pega_ypub_response(?,?,?); end;");
			stmt.setArray(1, pegaNotifyArray);
			stmt.registerOutParameter(2, OracleTypes.INTEGER);
			stmt.registerOutParameter(3, OracleTypes.VARCHAR);
			stmt.execute();   
			statusFlag = stmt.getInt(2);
			statusMessage = stmt.getString(3);
			BSDLogger.infoLog(this.getClass(), "DB Status Code: "+ statusFlag + " - DB Status Message: "+ statusMessage);
			
		} catch (SQLException e) {
			BSDLogger.errorLog(this.getClass(), "Fatal Error Occured"+e);
			BSDLogger.infoLog(this.getClass(), "Error in SQL Procedure (Swc_jobs_Pkg.update_pega_ypub_response) - SQL Error MSG: " 
					+ statusMessage);
			e.printStackTrace();
		}catch (Exception e){
			BSDLogger.errorLog(this.getClass(), "Fatal Error Occured"+e);
			BSDLogger.infoLog(this.getClass(), "Error in SQL Procedure (Swc_jobs_Pkg.update_pega_ypub_response) - SQL Error MSG: " 
					+ statusMessage);
			e.printStackTrace();
		}finally{
			closeDBConnection(null, stmt, null);
		}
		BSDLogger.infoLog(this.getClass(), "Exiting JobsDAO:updatePegaReponse");
	}

	private List<SwcPegaObject> buildSwcPegaObject(List<SwcPegaNotifyBO> wsResp) {
		List<SwcPegaObject> swcPegaObjectsList = new ArrayList<SwcPegaObject>();
		SwcPegaObject swcPegaObject = null;
		for (SwcPegaNotifyBO swcPegaNotifyBO  : wsResp) {
			swcPegaObject = new SwcPegaObject();
			swcPegaObject.setSwcPubId(swcPegaNotifyBO.getSwcPubId());
			swcPegaObject.setPegaResponseCode(swcPegaNotifyBO.getPegaStatusCode());
			swcPegaObject.setPegaResponseMSG(swcPegaNotifyBO.getPegaStatusMSG());
			swcPegaObjectsList.add(swcPegaObject);
		}
		return swcPegaObjectsList;
	}
	
	public void closeDBConnec() {
		closeDBConnection(null, null, null);
	}
}