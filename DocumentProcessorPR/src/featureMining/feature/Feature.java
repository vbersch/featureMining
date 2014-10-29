package featureMining.feature;

import java.io.Serializable;
import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Feature.
 */
public class Feature implements Comparable, Serializable{
	

	private static final long serialVersionUID = -827956067506812104L;

	/** The name. */
	private String label;
	
	private String oldName;
	
	/** The occurence. */
	private int occurrence;
	
	/** The synonyms. */
	private ArrayList<String> synonyms;
	
	//private String sourceName;
	
	private ArrayList<FeatureOccurrence> featureOccurrences;
	
	private ArrayList<String> distinctDescription;
	
	private ArrayList<String> singleWords;
	
	private String featureStem;
	
	/**
	 * Instantiates a new feature.
	 *
	 * @param label the name
	 */
	public Feature(String label, ArrayList<String> singleWords, String wholeSentence, String docName, long startIndex, long endIndex, String hierarchy){
		this.label = label;
		occurrence = 0;
		synonyms = null;
		this.singleWords = singleWords;
		featureOccurrences = new ArrayList<FeatureOccurrence>();
		distinctDescription = new ArrayList<String>();
		distinctDescription.add(wholeSentence);
		this.addFeatureOccurrence(wholeSentence, docName, startIndex, endIndex, hierarchy);
		oldName = null;
	}
	
	public Feature() {
		oldName = null;
		featureOccurrences = new ArrayList<FeatureOccurrence>();
		distinctDescription = new ArrayList<String>();
		occurrence = 0;
		synonyms = null;
	}

	public ArrayList<String> getSingleWords() {
		return singleWords;
	}

	public String getOldLabel() {
		return oldName;
	}
	
	public String getFeatureStem() {
		return featureStem;
	}

	public void setFeatureStem(String featureStem) {
		this.featureStem = featureStem;
	}

	public String getOldName() {
		return oldName;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public void setSynonyms(ArrayList<String> synonyms) {
		this.synonyms = synonyms;
	}

	public void setFeatureOccurrences(
			ArrayList<FeatureOccurrence> featureOccurrences) {
		this.featureOccurrences = featureOccurrences;
	}

	public void setSingleWords(ArrayList<String> singleWords) {
		this.singleWords = singleWords;
	}

	public void setOccurrence(int occurence) {
		this.occurrence = occurence;
	}

	public void setDistinctDescription(ArrayList<String> description) {
		this.distinctDescription = description;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getLabel() {
		return label;
	}
	
	public ArrayList<String> getDistinctDescription() {
		return distinctDescription;
	}

	/**
	 * Gets the occurence.
	 *
	 * @return the occurence
	 */
	public int getOccurrence() {
		return occurrence;
	}

	/**
	 * Gets the synonyms.
	 *
	 * @return the synonyms
	 */
	public ArrayList<String> getSynonyms() {
		return synonyms;
	}

	public void updateName(String newName) {
		this.oldName = this.label;
		this.label = newName;
		this.singleWords.clear();
		String[] words = newName.split(" ");
		for(int i = 0; i < words.length; i++){
			singleWords.add(words[i]);
		}
	}

	@Override
	public int compareTo(Object f) {
		Feature feat = (Feature) f;
		return feat.occurrence - this.occurrence;
	}

	public ArrayList<FeatureOccurrence> getFeatureOccurrences() {
		return featureOccurrences;
	}

	public void addFeatureOccurrence(String wholeSentence, String docName,
			long startIndex, long endIndex, String hierarchy) {
		if(!distinctDescription.contains(wholeSentence)){
			distinctDescription.add(wholeSentence);
		}
		FeatureOccurrence featureOccurrence = new FeatureOccurrence(docName, startIndex, endIndex, wholeSentence, hierarchy);
		this.featureOccurrences.add(featureOccurrence);
		this.occurrence++;
		
	}

	public void addFeatureOccurrence(FeatureOccurrence newOccurrence) {
		if(!distinctDescription.contains(newOccurrence.getContainingSentence())){
			distinctDescription.add(newOccurrence.getContainingSentence());
		}
		
		this.featureOccurrences.add(newOccurrence);
		this.occurrence++;
	}
	
}
