package featureMining.feature;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

// TODO: Auto-generated Javadoc
/**
 * The Class FeatureContainer.
 */
public class FeatureContainer implements Serializable{
	

	private static final long serialVersionUID = -4133816966091176361L;

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
		info += "\n#occurrences: \t" + feature.getOccurrence();
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
	
	public static String packContainer(FeatureContainer featureContainer){
		try {
			System.out.print("Packing FeatureContainer Bytestream to String...");
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			ObjectOutputStream os = new ObjectOutputStream(bos);
			os.writeObject(featureContainer);
			os.close();
			System.out.print("done");
			return bos.toString();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static FeatureContainer restoreContainer(String byteString){
		
		try {
			System.out.print("\nRestoring FeatureContainer from byteStreamString...");
			ByteArrayInputStream bis = new ByteArrayInputStream(byteString.getBytes());
			ObjectInputStream inputStream = new ObjectInputStream(bis);
			FeatureContainer restoredContainer = (FeatureContainer) inputStream.readObject();
			inputStream.close();
			System.out.print("done\n");
			return restoredContainer;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
