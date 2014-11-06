package featureMining.feature;

import java.io.Serializable;
import java.util.ArrayList;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * The Class OptionTransferObject.
 * An Object to save the Programs settings as:
 * - threadNum
 * - hostName
 * - baseUrl
 * - enableStemming
 * - preprocessingName
 * - documentationType
 * - domainspecific
 * - featureBlacklistPath
 * - sentenceBlacklistPath
 * 
 * Has to be Serializable for two reasons:
 * 1. is serialized to an xml File on every change 
 * and loaded on every program start
 * 2. is saved in the FeatureContainer and transferred to 
 * a Gate Processing Unit
 */
@XmlRootElement(name = "Settings")//xml Root Element
public class OptionTransferObject implements Serializable{  
	
	/** For Serialization. */
	private static final long serialVersionUID = -2006007433101173600L;
	
	/** The host name. Found links have to contain the hostName */
	private String hostName;
	
	/** The base url. */
	private String baseUrl;
	
	/** Enable Stemming */
	private boolean enableStemming;
	
	/** The preprocessing name. e.g HTML , PDF, WORD */
	private String preprocessingName;
	
	/** Specifies a specific documentationTyp. e.g General , Github, Mixxx */
	private String documentationType;
	
	/** Number of threads for preprocessing */
	private int threadNum;
	
	/** True: Every Feature has to contain at least one domain specific noun. */
	private boolean domainSpecific;
	
	/** path to a blacklist with words, no Feature is allowed to contain. */
	private String featureBlacklistPath;
	
	/** path to a blacklist containing the sentence Blacklist  */
	private String sentenceBlacklistPath;
	
	/** Features containing one of these words will be discarded */
	private ArrayList<String> featureBlacklist;
	
	/** Sentences containing one of these words will not be regarded for Feature Mining*/
	private ArrayList<String> sentenceBlacklist;
	
	/**
	 * Instantiates a new option transfer object.
	 */
	public OptionTransferObject() {
		this.hostName = null;
		featureBlacklist = new ArrayList<String>();
		sentenceBlacklist = new ArrayList<String>();
		featureBlacklistPath = "";
		sentenceBlacklistPath = "";
	}

	/**
	 * Getter for the hostName.
	 *
	 * @return the hosName
	 */
	public String getHostName() {
		return hostName;
	}

	/**
	 * Getter for the BaseUrl.
	 *
	 * @return the baseUrl
	 */
	public String getBaseUrl() {
		return baseUrl;
	}

	/**
	 * Checks if stemming is enabled.
	 *
	 * @return true, if stemming is enabled
	 */
	public boolean isEnableStemming() {
		return enableStemming;
	}

	/**
	 * Checks if stemming is enabled.
	 *
	 * @return true, if stemming is enabled
	 */
	public boolean isStemmingEnabled() {
		return enableStemming;
	}

	/**
	 * Setter for enableStemming
	 *
	 * @param enableStemming 
	 */
	public void setEnableStemming(boolean enableStemming) {
		this.enableStemming = enableStemming;
	}

	/**
	 * Getter for preprocessing name.
	 *
	 * @return the preprocessing name
	 */
	public String getPreprocessingName() {
		return preprocessingName;
	}

	/**
	 * Gets the documentation type.
	 *
	 * @return the documentation type
	 */
	public String getDocumentationType() {
		return documentationType;
	}

	/**
	 * Gets the thread num.
	 *
	 * @return the thread num
	 */
	public int getThreadNum() {
		return threadNum;
	}

	/**
	 * Checks if is domain specific.
	 *
	 * @return true, if is domain specific
	 */
	public boolean isDomainSpecific() {
		return domainSpecific;
	}

	/**
	 * Gets the feature blacklist path.
	 *
	 * @return the feature blacklist path
	 */
	public String getFeatureBlacklistPath() {
		return featureBlacklistPath;
	}

	/**
	 * Sets the feature blacklist path.
	 *
	 * @param featureBlacklistPath the new feature blacklist path
	 */
	public void setFeatureBlacklistPath(String featureBlacklistPath) {
		this.featureBlacklistPath = featureBlacklistPath;
	}

	/**
	 * Gets the sentence blacklist path.
	 *
	 * @return the sentence blacklist path
	 */
	public String getSentenceBlacklistPath() {
		return sentenceBlacklistPath;
	}

	/**
	 * Sets the sentence blacklist path.
	 *
	 * @param sentenceBlacklistPath the new sentence blacklist path
	 */
	public void setSentenceBlacklistPath(String sentenceBlacklistPath) {
		this.sentenceBlacklistPath = sentenceBlacklistPath;
	}

	/**
	 * Gets the feature blacklist.
	 *
	 * @return the feature blacklist
	 */
	public ArrayList<String> getFeatureBlacklist() {
		return featureBlacklist;
	}

	/**
	 * Sets the feature blacklist.
	 *
	 * @param featureBlacklist the new feature blacklist
	 */
	public void setFeatureBlacklist(ArrayList<String> featureBlacklist) {
		this.featureBlacklist = featureBlacklist;
	}

	/**
	 * Gets the sentence blacklist.
	 *
	 * @return the sentence blacklist
	 */
	public ArrayList<String> getSentenceBlacklist() {
		return sentenceBlacklist;
	}

	/**
	 * Sets the sentence blacklist.
	 *
	 * @param sentenceBlacklist the new sentence blacklist
	 */
	public void setSentenceBlacklist(ArrayList<String> sentenceBlacklist) {
		this.sentenceBlacklist = sentenceBlacklist;
	}

	/**
	 * Sets the domain specific.
	 *
	 * @param domainSpecific the new domain specific
	 */
	public void setDomainSpecific(String domainSpecific) {
		if(domainSpecific.equals("True")){
			this.domainSpecific = true;
		}else{
			this.domainSpecific = false;
		}
	}
	
	/**
	 * Sets the domain specific.
	 *
	 * @param domainSpecific the new domain specific
	 */
	public void setDomainSpecific(boolean domainSpecific) {
		this.domainSpecific = domainSpecific;
	}


	/**
	 * Sets the host name.
	 *
	 * @param hostName the new host name
	 */
	public void setHostName(String hostName) {
		this.hostName = hostName;
	}

	/**
	 * Sets the base url.
	 *
	 * @param baseUrl the new base url
	 */
	public void setBaseUrl(String baseUrl) {
		this.baseUrl = baseUrl;
	}

	/**
	 * Sets the preprocessing name.
	 *
	 * @param preprocessingName the new preprocessing name
	 */
	public void setPreprocessingName(String preprocessingName) {
		this.preprocessingName = preprocessingName;
	}

	/**
	 * Sets the documentation type.
	 *
	 * @param documentationType the new documentation type
	 */
	public void setDocumentationType(String documentationType) {
		this.documentationType = documentationType;
	}

	/**
	 * Sets the thread num.
	 *
	 * @param threadNum the new thread num
	 */
	public void setThreadNum(int threadNum) {
		this.threadNum = threadNum;
	}

	/**
	 * Sets enableStemming from a String
	 *
	 * @param enableStemming the new enable stemming
	 */
	public void setEnableStemming(String enableStemming) {
		if(enableStemming.equals("True")){
			this.enableStemming = true;
		}else{
			this.enableStemming = false;
		}
		
	}

}
