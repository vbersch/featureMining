package featureMining.feature;

import java.io.Serializable;

/**
 * The Class FeatureOccurrence.
 * Represents a single Occurrence of a Feature within the Documentation.
 * 
 */
public class FeatureOccurrence implements Serializable{
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 5672536098702575276L;
	
	/** Needed to annotate the correct Document on saving to Datastore. */
	private String documentName;
	
	/** start offset within the text. */
	private long startOffset;
	
	/** end offset within the text. */
	private long endOffset;
	
	/** The containing sentence. */
	private String containingSentence;
	
	/** The hierarchy level of the containing sentence. */
	private String hierarchy;
	
	/** The occurrence name. */
	private String occurrenceName;
	
	/**
	 * Constructor
	 */
	public FeatureOccurrence(){
		
	}

	/**
	 * Gets the occurrence name.
	 *
	 * @return the occurrence name
	 */
	public String getOccurrenceName() {
		return occurrenceName;
	}

	/**
	 * Sets the occurrence name.
	 *
	 * @param occurrenceName the new occurrence name
	 */
	public void setOccurrenceName(String occurrenceName) {
		this.occurrenceName = occurrenceName;
	}

	/**
	 * Sets the document name.
	 *
	 * @param documentName the new document name
	 */
	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	/**
	 * Sets the start offset.
	 *
	 * @param startOffset the new start offset
	 */
	public void setStartOffset(long startOffset) {
		this.startOffset = startOffset;
	}

	/**
	 * Sets the end offset.
	 *
	 * @param endOffset the new end offset
	 */
	public void setEndOffset(long endOffset) {
		this.endOffset = endOffset;
	}

	/**
	 * Sets the containing sentence.
	 *
	 * @param containingSentence the new containing sentence
	 */
	public void setContainingSentence(String containingSentence) {
		this.containingSentence = containingSentence;
	}

	/**
	 * Sets the hierarchy.
	 *
	 * @param hierarchy the new hierarchy
	 */
	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}

	/**
	 * Gets the document name.
	 *
	 * @return the document name
	 */
	public String getDocumentName() {
		return documentName;
	}

	/**
	 * Gets the start offset.
	 *
	 * @return the start offset
	 */
	public long getStartOffset() {
		return startOffset;
	}

	/**
	 * Gets the end offset.
	 *
	 * @return the end offset
	 */
	public long getEndOffset() {
		return endOffset;
	}

	/**
	 * Gets the containing sentence.
	 *
	 * @return the containing sentence
	 */
	public String getContainingSentence() {
		return containingSentence;
	}

	/**
	 * Gets the hierarchy.
	 *
	 * @return the hierarchy
	 */
	public String getHierarchy() {
		return hierarchy;
	}
	
}
