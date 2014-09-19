package featureMining.processing.feature;

import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class FeatureContainer.
 */
public class FeatureContainer {
	
	/** The feature storage. */
	private HashMap<String, Feature> featureStorage;
	
	/** The feature candidates. */
	private HashMap<String, Feature> featureCandidates;
	
	private int linkNum;
	
	/**
	 * Instantiates a new feature container.
	 */
	public FeatureContainer(){
		this.featureStorage = new HashMap<String, Feature>();
		this.featureCandidates = new HashMap<String, Feature>();
		linkNum = 0;
	}
	
	public int getLinkNum() {
		return linkNum;
	}

	public void setLinkNum(int linkNum) {
		this.linkNum = linkNum;
	}

	/**
	 * Adds the.
	 *
	 * @param featureString the feature string
	 */
	public void add(String featureString, String sourceUrl){
		if(this.featureStorage.containsKey(featureString)){
			this.featureStorage.get(featureString).add();
		}else{
			this.featureStorage.put(featureString , new Feature(featureString,sourceUrl));
		}
	}

	/**
	 * Gets the feature storage.
	 *
	 * @return the feature storage
	 */
	public HashMap<String, Feature> getFeatureStorage() {
		return featureStorage;
	}

	/**
	 * Gets the info text.
	 *
	 * @param key the key
	 * @return the info text
	 */
	public String getInfoText(String key) {
		String info = "";
		Feature feature = this.featureStorage.get(key);
		info += feature.getName();
		info += "\n#occurrences: \t" + feature.getOccurence();
		info += "\n#Found in: \t" + feature.getSourceName();
		return info;
	}
}
