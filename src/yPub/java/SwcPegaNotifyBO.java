package yPub.java;

public class SwcPegaNotifyBO {

	private int swcPubId;
	private String caseTypeID;
	private String pegaStepID;
	private String flowName;
	private String pegaStepName;
	private String source;
	private String pegaStatusCode;
	private String pegaStatusMSG;
	private Content content;
	public int getSwcPubId() {
		return swcPubId;
	}
	public void setSwcPubId(int swcPubId) {
		this.swcPubId = swcPubId;
	}
	public String getCaseTypeID() {
		return caseTypeID;
	}
	public void setCaseTypeID(String caseTypeID) {
		this.caseTypeID = caseTypeID;
	}
	public String getPegaStepID() {
		return pegaStepID;
	}
	public void setPegaStepID(String pegaStepID) {
		this.pegaStepID = pegaStepID;
	}
	public String getFlowName() {
		return flowName;
	}
	public void setFlowName(String flowName) {
		this.flowName = flowName;
	}
	public String getPegaStepName() {
		return pegaStepName;
	}
	public void setPegaStepName(String pegaStepName) {
		this.pegaStepName = pegaStepName;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getPegaStatusCode() {
		return pegaStatusCode;
	}
	public void setPegaStatusCode(String pegaStatusCode) {
		this.pegaStatusCode = pegaStatusCode;
	}
	public String getPegaStatusMSG() {
		return pegaStatusMSG;
	}
	public void setPegaStatusMSG(String pegaStatusMSG) {
		this.pegaStatusMSG = pegaStatusMSG;
	}
	public Content getContent() {
		return content;
	}
	public void setContent(Content content) {
		this.content = content;
	}
}
