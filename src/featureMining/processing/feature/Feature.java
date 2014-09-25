package featureMining.processing.feature;

import java.util.ArrayList;

// TODO: Auto-generated Javadoc
/**
 * The Class Feature.
 */
public class Feature {
	
	
	/** The name. */
	private String name;
	
	/** The occurence. */
	private int occurence;
	
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
		occurence = 1;
		synonyms = null;
		this.sourceName = sourceName;
		this.singleWords = singleWords;
		description = new ArrayList<String>();
		description.add(wholeSentence);
	}
	
	/**
	 * Adds the.
	 */
	public void add(String wholeSentence){
		occurence++;
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
	public int getOccurence() {
		return occurence;
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
	}
	
}
