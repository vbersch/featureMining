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
	private String name;
	
	private String oldName;
	
	/** The occurence. */
	private int occurrence;
	
	/** The synonyms. */
	private ArrayList<String> synonyms;
	
	//private String sourceName;
	
	private ArrayList<FeatureOccurrence> featureOccurrences;
	
	private ArrayList<String> distinctDescription;
	
	private ArrayList<String> singleWords;
	
	/**
	 * Instantiates a new feature.
	 *
	 * @param name the name
	 */
	public Feature(String name, ArrayList<String> singleWords, String wholeSentence, String docName, long startIndex, long endIndex){
		this.name = name;
		occurrence = 1;
		synonyms = null;
		//this.sourceName = sourceName;
		this.singleWords = singleWords;
		featureOccurrences = new ArrayList<FeatureOccurrence>();
		distinctDescription = new ArrayList<String>();
		distinctDescription.add(wholeSentence);
		this.addFeatureOccurrence(wholeSentence, docName, startIndex, endIndex);
		oldName = null;
	}
	
	public ArrayList<String> getSingleWords() {
		return singleWords;
	}

	public String getOldName() {
		return oldName;
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
	public String getName() {
		return name;
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
		this.oldName = this.name;
		this.name = newName;
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
			long startIndex, long endIndex) {
		if(!distinctDescription.contains(wholeSentence)){
			distinctDescription.add(wholeSentence);
		}
		FeatureOccurrence featureOccurrence = new FeatureOccurrence(docName, startIndex, endIndex, wholeSentence);
		this.featureOccurrences.add(featureOccurrence);
		this.occurrence++;
		
	}
	
}
