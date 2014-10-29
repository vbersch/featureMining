package featureMining.feature;

import gate.Annotation;
import gate.Document;

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
	private HashMap<String, Feature> featureDictionary;
	private int linkNum;
	private OptionTransferObject options;
	
	/**
	 * Instantiates a new feature container.
	 */
	public FeatureContainer(){
		this.featureStorage = new HashMap<String, Feature>();
		this.featureDictionary = new HashMap<String, Feature>();
		linkNum = 0;
	}
	
	public int getLinkNum() {
		return linkNum;
	}

	public OptionTransferObject getOptions() {
		return options;
	}

	public void setOptions(OptionTransferObject options) {
		this.options = options;
	}

	public void setLinkNum(int linkNum) {
		this.linkNum = linkNum;
	}

	/**
	 * Adds the.
	 *
	 * @param featureString the feature string
	 */
	public void addFeature(String featureString, String wholeSentence, String docName, long startIndex, long endIndex, String hierarchy){
		String newFeature = "";
		ArrayList<String> singleWords = new ArrayList<String>();
		//filter some words
		String[] words = featureString.split(" ");
		for(String word : words){
			if(word.matches("[a-zA-Z]*") && !word.matches("[a-zA-Z]")){ // only letters and more than one
				newFeature += word + " ";
				singleWords.add(word);
			}
		}
		newFeature = newFeature.trim();
		
		if(newFeature != ""){
			if(this.featureStorage.containsKey(newFeature)){
				this.featureStorage.get(newFeature).addFeatureOccurrence(wholeSentence, docName, startIndex, endIndex, hierarchy);
			}else{
				this.featureStorage.put(newFeature , new Feature(newFeature, singleWords, wholeSentence, docName, startIndex, endIndex, hierarchy));
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
		Feature feature = this.featureDictionary.get(key);
		info += feature.getLabel();
		info += "\n#occurrences: \t" + feature.getOccurrence();
		if(feature.getOldLabel() != null){
			info += "\nold Name: " + feature.getOldLabel();
		}
		return info;
	}
	
	public void addOccurence(String key, String wholeSentence, String docName, long startIndex, long endIndex, String hierarchy){
		this.featureStorage.get(key).addFeatureOccurrence(wholeSentence, docName, startIndex, endIndex, hierarchy);
	}
	
	public String getDescriptionText(String key){
		String desc = "";
		
		Feature feature = this.featureDictionary.get(key);
		
		for(String text : feature.getDistinctDescription()){
			desc += text + "\n-------------------\n";
		}
		return desc;
	}

	public void deleteFeature(String key) {
		this.featureStorage.remove(key);
	}

	public void changeFeature(String oldName, String newName) {
		Feature f = this.featureStorage.remove(oldName);
		f.updateName(newName);
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

	public void addFeature(ArrayList<Annotation> featureAnnots, String wholeSentence, Document doc, String hierarchy) {
		String featureString = "";
		String featureStem = "";
		ArrayList<String> singleWords = new ArrayList<String>();
		//filter some words
		for(Annotation token : featureAnnots){
			String word = token.getFeatures().get("string").toString();
			String stem = token.getFeatures().get("stem").toString();
			if(word.matches("[a-zA-Z]*") && !word.matches("[a-zA-Z]")){
				featureString += word + " ";
				featureStem += stem + " ";
				singleWords.add(stem);
			}
		}
		
		featureString = featureString.trim();
		featureStem = featureStem.trim();
		
		long startIndex = featureAnnots.get(0).getStartNode().getOffset();
		long endIndex = featureAnnots.get(featureAnnots.size()-1).getEndNode().getOffset();
		
		if(!featureStem.equals("")){
			
			FeatureOccurrence newOccurrence = new FeatureOccurrence();
			newOccurrence.setContainingSentence(wholeSentence);
			newOccurrence.setOccurrenceName(featureString);
			newOccurrence.setDocumentName(doc.getName());
			newOccurrence.setStartOffset(startIndex);
			newOccurrence.setEndOffset(endIndex);
			newOccurrence.setHierarchy(hierarchy);
			
			if(this.featureStorage.containsKey(featureStem)){
				this.featureStorage.get(featureStem).addFeatureOccurrence(newOccurrence);
			}else{
				Feature newFeature = new Feature();
				newFeature.setFeatureStem(featureStem);
				newFeature.setLabel(featureString);
				newFeature.setSingleWords(singleWords);
				newFeature.addFeatureOccurrence(newOccurrence);
				this.featureStorage.put(featureStem , newFeature);
			}
			
			this.featureDictionary.put(featureString, this.featureStorage.get(featureStem));
		}
	}
}
