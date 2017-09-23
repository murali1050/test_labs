package yPub.java;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Struct;
import java.util.Iterator;
import java.util.List;

import oracle.sql.ARRAY;
import oracle.sql.ArrayDescriptor;
import oracle.sql.STRUCT;
import oracle.sql.StructDescriptor;

public class SwcPegaObject {
	
	private int swcPubId;
	private String pegaResponseCode;
	private String pegaResponseMSG;

	public int getSwcPubId() {
		return swcPubId;
	}
	public void setSwcPubId(int swcPubId) {
		this.swcPubId = swcPubId;
	}
	public String getPegaResponseCode() {
		return pegaResponseCode;
	}
	public void setPegaResponseCode(String pegaResponseCode) {
		this.pegaResponseCode = pegaResponseCode;
	}
	public String getPegaResponseMSG() {
		return pegaResponseMSG;
	}
	public void setPegaResponseMSG(String pegaResponseMSG) {
		this.pegaResponseMSG = pegaResponseMSG;
	}
	/**
	 * Utility Method to build list of SwcPegaObject objects from oracle
	 * array.
	 *
	 * @param conn
	 *            - JDBC connection object
	 * @param list
	 *            - List into which SwcPegaObject out details has to be
	 *            populated
	 * @param startTime
	 *            the start time
	 * @return List of PartyHierarchy out objects
	 * @throws SQLException
	 *             the sQL exception
	 * @throws InstantiationException
	 *             the instantiation exception
	 * @throws IllegalAccessException
	 *             the illegal access exception
	 */
	public static ARRAY buildOracleArrayFromList(Connection conn,
			List<SwcPegaObject> list)
			throws SQLException, InstantiationException, IllegalAccessException {
		SwcPegaObject element = null;
		Object[] jarray = null;
		Struct charat = null;
		ArrayDescriptor desc = null;
		if (conn != null) {
			desc = ArrayDescriptor.createDescriptor("SWC_PEGA_NOTIFY_LIST", conn);
			int i = 0;
			if (list != null && list.size() > 0) {
				jarray = new Object[list.size()];
				Iterator<SwcPegaObject> iterator = list.iterator();
				while (iterator.hasNext()) {
					element = iterator.next();
					// calls asStruct method from ContactsObject
					charat = element.asStruct(conn);
					jarray[i++] = charat;
				}

			}
			return  new ARRAY(desc, conn, jarray);
		}
		return null;
	}
	/**
	 * Utility method for building SwcPegaObject object from oracle
	 * Struct.
	 * 
	 * @param conn
	 *            - jdbc connection
	 * @return SwcPegaObject information
	 * @throws SQLException
	 *             the sQL exception
	 */
	public STRUCT asStruct(Connection conn) throws SQLException {
		STRUCT struct = null;
		StructDescriptor sd = StructDescriptor.createDescriptor("SWC_PEGA_NOTIFY_OBJ", conn);
		Object[] attributes = new Object[] { swcPubId, pegaResponseCode, pegaResponseMSG };
		struct = new STRUCT(sd, conn, attributes);
		return struct;
	}
}
