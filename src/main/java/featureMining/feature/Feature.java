package featureMining.feature;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * The Class Feature.
 * Represents one mined Feature and can have several Occurrences with different labels.
 */
public class Feature implements Comparable<Feature>, Serializable{
	

	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = -827956067506812104L;

	/** The label which is shown in the UI. */
	private String label;
	
	/** The old label before a change */
	private String oldLabel;
	
	/** The Number of Occurrences in all Documents. */
	private int occurrence;
		
	/** The feature occurrences. */
	private ArrayList<FeatureOccurrence> featureOccurrences;
	
	/** Every distinct Sentence this feature occurred in. */
	private ArrayList<String> distinctDescription;
	
	/** The current Label split in single Words. */
	private ArrayList<String> singleWords;
	
	/** The word stem of the Feature Label. */
	private String featureStem;
	
	/**
	 * Constructor used 
	 *
	 * @param label the name
	 * @param singleWords the single words
	 * @param wholeSentence the whole sentence
	 * @param docName the doc name
	 * @param startIndex the start index
	 * @param endIndex the end index
	 * @param hierarchy the hierarchy
	 */
	
	/**
	 * Constructor
	 */
	public Feature() {
		oldLabel = null;
		singleWords = new ArrayList<String>();
		featureOccurrences = new ArrayList<FeatureOccurrence>();
		distinctDescription = new ArrayList<String>();
		occurrence = 0;
	}

	/**
	 * Gets the single words.
	 *
	 * @return the single words
	 */
	public ArrayList<String> getSingleWords() {
		return singleWords;
	}

	/**
	 * Gets the old label.
	 *
	 * @return the old label
	 */
	public String getOldLabel() {
		return oldLabel;
	}
	
	/**
	 * Gets the feature stem.
	 *
	 * @return the feature stem
	 */
	public String getFeatureStem() {
		return featureStem;
	}

	/**
	 * Sets the feature stem.
	 *
	 * @param featureStem the new feature stem
	 */
	public void setFeatureStem(String featureStem) {
		this.featureStem = featureStem;
	}

	/**
	 * Gets the old name.
	 *
	 * @return the old name
	 */
	public String getOldName() {
		return oldLabel;
	}

	/**
	 * Sets the label.
	 *
	 * @param label the new label
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * Sets the feature occurrences.
	 *
	 * @param featureOccurrences the new feature occurrences
	 */
	public void setFeatureOccurrences(
			ArrayList<FeatureOccurrence> featureOccurrences) {
		this.featureOccurrences = featureOccurrences;
	}

	/**
	 * Sets the single words.
	 *
	 * @param singleWords the new single words
	 */
	public void setSingleWords(ArrayList<String> singleWords) {
		this.singleWords = singleWords;
	}

	/**
	 * Sets the occurrence.
	 *
	 * @param occurence the new occurrence
	 */
	public void setOccurrence(int occurence) {
		this.occurrence = occurence;
	}

	/**
	 * Sets the distinct description.
	 *
	 * @param description the new distinct description
	 */
	public void setDistinctDescription(ArrayList<String> description) {
		this.distinctDescription = description;
	}

	/**
	 * Sets the old name.
	 *
	 * @param oldName the new old name
	 */
	public void setOldName(String oldName) {
		this.oldLabel = oldName;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getLabel() {
		return label;
	}
	
	/**
	 * Gets the distinct description.
	 *
	 * @return the distinct description
	 */
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
	 * Changes a Features Label
	 *
	 * @param newLabel the new Label
	 */
	public void updateName(String newLabel) {
		this.oldLabel = this.label;
		this.label = newLabel;
		this.singleWords.clear();
		String[] words = newLabel.split(" ");
		for(int i = 0; i < words.length; i++){
			singleWords.add(words[i]);
		}
	}

	/* (non-Javadoc)
	 * @see java.lang.Comparable#compareTo(java.lang.Object)
	 * 
	 * needed to sort Features by Occurrence
	 */
	@Override
	public int compareTo(Feature feat) {
		return feat.occurrence - this.occurrence;
	}

	/**
	 * Gets the feature occurrences.
	 *
	 * @return the feature occurrences
	 */
	public ArrayList<FeatureOccurrence> getFeatureOccurrences() {
		return featureOccurrences;
	}

	/**
	 * Adds a feature occurrence.
	 *
	 * @param newOccurrence the new occurrence
	 */
	public void addFeatureOccurrence(FeatureOccurrence newOccurrence) {
		if(!distinctDescription.contains(newOccurrence.getContainingSentence())){
			distinctDescription.add(newOccurrence.getContainingSentence());
		}
		
		this.featureOccurrences.add(newOccurrence);
		this.occurrence++;
	}
}
