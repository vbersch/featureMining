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
	
	private String sourceName;
	
	private ArrayList<String> description;
	
	private ArrayList<String> singleWords;
	
	/**
	 * Instantiates a new feature.
	 *
	 * @param name the name
	 */
	public Feature(String name, String sourceName, ArrayList<String> singleWords, String wholeSentence){
		this.name = name;
		occurrence = 1;
		synonyms = null;
		this.sourceName = sourceName;
		this.singleWords = singleWords;
		description = new ArrayList<String>();
		description.add(wholeSentence);
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

	public void setDescription(ArrayList<String> description) {
		this.description = description;
	}

	public void setOldName(String oldName) {
		this.oldName = oldName;
	}
	/**
	 * Adds the.
	 */
	public void add(String wholeSentence){
		occurrence++;
		if(!description.contains(wholeSentence)){
			description.add(wholeSentence);
		}
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
	}
	
	public ArrayList<String> getDescription() {
		return description;
	}

	public String getSourceName() {
		return sourceName;
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

	public void addDescSentence(String wholeSentence) {
		if(!description.contains(wholeSentence)){
			description.add(wholeSentence);
		}
		this.occurrence++;
	}

	public void update(String newName) {
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
	
}
