package featureMining.feature;

import java.util.ArrayList;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class FeatureContainer.
 */
public class FeatureContainer {
	
	/** The feature storage. */
	private HashMap<String, Feature> featureStorage;
	
	private int linkNum;
	
	/**
	 * Instantiates a new feature container.
	 */
	public FeatureContainer(){
		this.featureStorage = new HashMap<String, Feature>();
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
	public void add(String featureString, String sourceUrl, String wholeSentence){
		String newFeature = "";
		ArrayList<String> singleWords = new ArrayList<String>();
		//filter some words
		String[] words = featureString.split(" ");
		for(String word : words){
			if(word.matches("[a-zA-Z]*") && !word.matches("[a-zA-Z]")){ // only letters 
				newFeature += word + " ";
				singleWords.add(word);
			}
		}
		newFeature = newFeature.trim();
		
		if(newFeature != ""){
			if(this.featureStorage.containsKey(newFeature)){
				this.featureStorage.get(newFeature).add(wholeSentence);
			}else{
				this.featureStorage.put(newFeature , new Feature(newFeature, sourceUrl, singleWords, wholeSentence));
			}
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
		if(feature.getOldName() != null){
			info += "\nold Name: " + feature.getOldName();
		}
		return info;
	}
	
	public void addOccurence(String key, String wholeSentence){
		this.featureStorage.get(key).addDescSentence(wholeSentence);
	}
	
	public String getDescriptionText(String key){
		String desc = "";
		
		Feature feature = this.featureStorage.get(key);
		
		for(String text : feature.getDescription()){
			desc += text + "\n-------------------\n";
		}
		return desc;
	}

	public void deleteFeature(String key) {
		this.featureStorage.remove(key);
	}

	public void changeFeature(String oldName, String newName) {
		Feature f = this.featureStorage.remove(oldName);
		f.update(newName);
		this.featureStorage.put(newName, f);
	}
	
}
