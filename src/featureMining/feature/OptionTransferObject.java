package featureMining.feature;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Settings")
public class OptionTransferObject {  
	
	private String hostName;
	private String baseUrl;
	private String preprocessingName;
	private String documentationType;
	private int threadNum;
	private boolean domainSpecific;
	
	
	public OptionTransferObject() {
		this.hostName = null;
	}

	public String getHostName() {
		return hostName;
	}

	public String getBaseUrl() {
		return baseUrl;
	}

	public String getPreprocessingName() {
		return preprocessingName;
	}

	public String getDocumentationType() {
		return documentationType;
	}

	public int getThreadNum() {
		return threadNum;
	}

	public boolean isDomainSpecific() {
		return domainSpecific;
	}

	public void setDomainSpecific(String domainSpecific) {
		if(domainSpecific.equals("True")){
			this.domainSpecific = true;
		}else{
			this.domainSpecific = false;
		}
	}
	
	public void setDomainSpecific(boolean domainSpecific) {
		this.domainSpecific = domainSpecific;
	}


	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	public void setPreprocessingName(String preprocessingName) {
		this.preprocessingName = preprocessingName;
	}

	public void setDocumentationType(String documentationType) {
		this.documentationType = documentationType;
	}

	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

}
