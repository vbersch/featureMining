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
	
	/**
	 * Instantiates a new feature.
	 *
	 * @param name the name
	 */
	public Feature(String name, String sourceName){
		this.name = name;
		occurence = 1;
		synonyms = null;
		this.sourceName = sourceName;
	}
	
	/**
	 * Adds the.
	 */
	public void add(){
		occurence++;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName() {
		return name;
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
	
}
