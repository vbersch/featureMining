package featureMining.feature;

import gate.Annotation;
import gate.Document;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;


/**
 * The Class FeatureContainer.
 * A Container for every Feature mined.
 */
public class FeatureContainer implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -4133816966091176361L;

	/** HashMap containing the mined Features/Feature Candidates with the 
	 * 	stem as key.
	 *  */
	private HashMap<String, Feature> featureStorage;
	
	/** The feature dictionary.
	 *  A Dictionary containing a mapping for every occurring Feature represantation
	 *  to the corresponding Feature Object
	 *  */
	private HashMap<String, Feature> featureDictionary;
	
	/** The number of links the HtmlPreprocessor parsed. */
	private int linkNum;
	
	/** The options. */
	private OptionTransferObject options;
	
	/**
	 * Instantiates a new feature container.
	 */
	public FeatureContainer(){
		this.featureStorage = new HashMap<String, Feature>();
		this.featureDictionary = new HashMap<String, Feature>();
		linkNum = 0;
	}
	
	/**
	 * Gets the link num.
	 *
	 * @return the link num
	 */
	public int getLinkNum() {
		return linkNum;
	}

	/**
	 * Gets the options.
	 *
	 * @return the options
	 */
	public OptionTransferObject getOptions() {
		return options;
	}

	/**
	 * Sets the options.
	 *
	 * @param options the new options
	 */
	public void setOptions(OptionTransferObject options) {
		this.options = options;
	}

	/**
	 * Sets the link num.
	 *
	 * @param linkNum the new link num
	 */
	public void setLinkNum(int linkNum) {
		this.linkNum = linkNum;
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
	 * Gets a String containing Information about a specific Feature
	 *
	 * @param key label of the List in the UI containing the Feature Labels
	 * @return the info text
	 */
	public String getInfoText(String key) {
		String info = "";
		Feature feature = this.featureDictionary.get(key);
		info += feature.getLabel();
		info += "\n#occurrences: \t" + feature.getOccurrence();
		info += "\nStem: " + feature.getFeatureStem();
		if(feature.getOldLabel() != null){
			info += "\nold Name: " + feature.getOldLabel();
		}
		return info;
	}
	
	/**
	 * Returns a String containing the distinct Sentences, that a specific Feature occurred in.
	 *
	 * @param key the key
	 * @return the description text
	 */
	public String getDescriptionText(String key){
		String desc = "";
		
		Feature feature = this.featureDictionary.get(key);
		
		for(String text : feature.getDistinctDescription()){
			desc += text + "\n-------------------\n";
		}
		return desc;
	}

	/**
	 * Deletes a Feature.
	 *
	 * @param key the key
	 */
	public void deleteFeature(String key) {
		Feature f = this.featureDictionary.get(key);
		this.featureStorage.remove(f.getFeatureStem());
		while(featureDictionary.values().remove(f));
	}

	/**
	 * Change a Features label.
	 *
	 * @param oldName the old name
	 * @param newName the new name
	 */
	public void changeFeature(String oldName, String newName) {
		Feature f = this.featureDictionary.get(oldName);
		f.updateName(newName);
		this.featureDictionary.put(newName, f);
	}
	
	public void addFeatureFromXML(Feature feature, FeatureOccurrence occurrence, boolean isStemmingEnabled){
		String identifier = "";
		if(!isStemmingEnabled){
			identifier = feature.getLabel();
		}else{
			identifier = feature.getFeatureStem();
			this.featureDictionary.put(feature.getLabel() , feature);	
		}
		this.featureDictionary.put(identifier , feature);
		this.featureStorage.put(identifier, feature);
		feature.addFeatureOccurrence(occurrence);
	}
	
	public void addFeatureOccurrenceFromXML(Feature feature, FeatureOccurrence occurrence, boolean isStemmingEnabled){
		if(isStemmingEnabled){
			this.featureDictionary.put(occurrence.getOccurrenceName(), feature);
		}
		feature.addFeatureOccurrence(occurrence);
	}
	
	
	public HashMap<String, Feature> getFeatureDictionary() {
		return featureDictionary;
	}

	/**
	 * Adds a Feature. Features can only be added when found in a heading Annotation.
	 *
	 * @param featureAnnots contains one Token Annotation per Word
	 * @param wholeSentence the whole Sentence containing the Feature
	 * @param the Gate Document containing the Feature
	 * @param The hierarchy within the document
	 */
	public void addFeature(ArrayList<Annotation> featureAnnots, String wholeSentence, Document doc, String hierarchy) {
		String featureString = "";
		String featureStem = "";
		ArrayList<String> singleWords = new ArrayList<String>();
		//filter some words
		for(Annotation token : featureAnnots){
			String word = token.getFeatures().get("string").toString();
			String stem = token.getFeatures().get("stem").toString();
			if(word.matches("[a-zA-Z]*") && !word.matches("[a-zA-Z]")){ //only letters and more than one
				featureString += word + " ";
				featureStem += stem + " ";
				singleWords.add(word);
			}
		}
		
		featureString = featureString.trim();
		featureStem = featureStem.trim();
		
		long startIndex = featureAnnots.get(0).getStartNode().getOffset();
		long endIndex = featureAnnots.get(featureAnnots.size()-1).getEndNode().getOffset();
		
		String identifier = "";
		if(this.getOptions().isEnableStemming()){
			identifier = featureStem;
		}else{
			//if stemming is not enabled there is only one label per Feature and no 
			//Dictionary is needed
			identifier = featureString;
		}
		
		if(!identifier.equals("")){
			//create a new FeatureOccurrence
			FeatureOccurrence newOccurrence = new FeatureOccurrence();
			newOccurrence.setContainingSentence(wholeSentence);
			newOccurrence.setOccurrenceName(featureString);
			newOccurrence.setDocumentName(doc.getName());
			newOccurrence.setStartOffset(startIndex);
			newOccurrence.setEndOffset(endIndex);
			newOccurrence.setHierarchy(hierarchy);
			
			if(this.featureStorage.containsKey(identifier)){//a Feature with this label already exists 
				//just add a new FeatureOccurrence
				this.featureStorage.get(identifier).addFeatureOccurrence(newOccurrence);
			}else{
				//add a new Feature because it does not yet exist
				Feature newFeature = new Feature();
				newFeature.setFeatureStem(featureStem);
				newFeature.setLabel(featureString);
				newFeature.setSingleWords(singleWords);
				newFeature.addFeatureOccurrence(newOccurrence);
				this.featureStorage.put(identifier , newFeature);
				this.featureDictionary.put(featureStem, this.featureStorage.get(identifier));
			}
			
			//add a Dictionary Entry in both cases
			this.featureDictionary.put(featureString, this.featureStorage.get(identifier));
		}
	}

	/**
	 * Gets the distinct labels for the labelChoosingList in the UI
	 *
	 * @param key the key
	 * @return the distinct labels
	 */
	public ArrayList<String> getDistinctLabels(String key) {
		Feature feature = this.featureDictionary.get(key);
		ArrayList<String> distinctLabels = new ArrayList<String>();
		for(FeatureOccurrence fOccurrence : feature.getFeatureOccurrences()){
			if(!fOccurrence.getOccurrenceName().equals(key) && !distinctLabels.contains(fOccurrence.getOccurrenceName())){//current Label not needed
				distinctLabels.add(fOccurrence.getOccurrenceName());
			}
		}
		return distinctLabels;
	}

	/**
	 * Adds the occurrence.
	 *
	 * @param feature the feature
	 * @param featureOccurrence the feature occurrence
	 */
	public void addOccurrence(String feature,
			FeatureOccurrence featureOccurrence) {
		this.featureDictionary.get(feature).addFeatureOccurrence(featureOccurrence);
		
	}

	//could be useful someday but has to be reworked...
//	/**
//	 * Serializes the Bytestream of the FeatureContainer into a String.
//	 * Can be used to store a FeatureContainer as String in the FeatureMap of a Corpus
//	 *
//	 * @param featureContainer the feature container
//	 * @return the string
//	 */
//	public static String packContainer(FeatureContainer featureContainer){
//		try {
//			ByteArrayOutputStream bos = new ByteArrayOutputStream();
//			ObjectOutputStream os = new ObjectOutputStream(bos);
//			os.writeObject(featureContainer);
//			os.close();
//			return bos.toString();
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
//	
//	/**
//	 * Restores a FeatureContainer out of a serialized Bytestream string.
//	 *
//	 * @param byteString the byte string
//	 * @return the feature container
//	 */
//	public static FeatureContainer restoreContainer(String byteString){
//		
//		try {
//			ByteArrayInputStream bis = new ByteArrayInputStream(byteString.getBytes());
//			ObjectInputStream inputStream = new ObjectInputStream(bis);
//			FeatureContainer restoredContainer = (FeatureContainer) inputStream.readObject();
//			inputStream.close();
//			return restoredContainer;
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		return null;
//	}
}
