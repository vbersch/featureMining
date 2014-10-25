package featureMining.feature;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "Settings")
public class OptionTransferObject implements Serializable{  
	
	private static final long serialVersionUID = -2006007433101173600L;
	private String hostName;
	private String baseUrl;
	private String preprocessingName;
	private String documentationType;
	private int threadNum;
	private boolean domainSpecific;
	private String featureBlacklistPath;
	private String sentenceBlacklistPath;
	private ArrayList<String> featureBlacklist;
	private ArrayList<String> sentenceBlacklist;
	
	public OptionTransferObject() {
		this.hostName = null;
		featureBlacklist = new ArrayList<String>();
		sentenceBlacklist = new ArrayList<String>();
		featureBlacklistPath = "";
		sentenceBlacklistPath = "";
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

	public String getFeatureBlacklistPath() {
		return featureBlacklistPath;
	}

	public void setFeatureBlacklistPath(String featureBlacklistPath) {
		this.featureBlacklistPath = featureBlacklistPath;
	}

	public String getSentenceBlacklistPath() {
		return sentenceBlacklistPath;
	}

	public void setSentenceBlacklistPath(String sentenceBlacklistPath) {
		this.sentenceBlacklistPath = sentenceBlacklistPath;
	}

	public ArrayList<String> getFeatureBlacklist() {
		return featureBlacklist;
	}

	public void setFeatureBlacklist(ArrayList<String> featureBlacklist) {
		this.featureBlacklist = featureBlacklist;
	}

	public ArrayList<String> getSentenceBlacklist() {
		return sentenceBlacklist;
	}

	public void setSentenceBlacklist(ArrayList<String> sentenceBlacklist) {
		this.sentenceBlacklist = sentenceBlacklist;
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
