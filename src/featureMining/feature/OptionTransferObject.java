package featureMining.feature;

public class OptionTransferObject {

	private String hostName;
	private String baseUrl;
	private String preprocessingName;
	private String documentationType;
	private int threadNum;
	private boolean domainSpecific;
	
	public OptionTransferObject(String baseUrl, String hostName,
			String preprocessor, int threadNum, String documentationType) {
		this.baseUrl = baseUrl;
		this.hostName = hostName;
		this.preprocessingName = preprocessor;
		this.documentationType = documentationType;
		this.threadNum = threadNum;
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
	
}
