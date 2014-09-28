package featureMining.ui;

public class OptionTransferObject {

	private String hostName;
	private String baseUrl;
	private String preprocessingName;
	private String documentationType;
	private int threadNum;
	
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
	
}
