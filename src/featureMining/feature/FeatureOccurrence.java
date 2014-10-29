package featureMining.feature;

import java.io.Serializable;

public class FeatureOccurrence implements Serializable{
	
	private static final long serialVersionUID = 5672536098702575276L;
	private String documentName;
	private long startOffset;
	private long endOffset;
	private String containingSentence;
	private String hierarchy;
	private String occurrenceName;
	
	public FeatureOccurrence(){
		
	}
	
	public FeatureOccurrence(String docName, long start, long end, String wholeSentence, String hierarchy){
		this.documentName = docName;
		startOffset = start;
		endOffset = end;
		containingSentence = wholeSentence;
		this.hierarchy = hierarchy;
	}

	public String getOccurrenceName() {
		return occurrenceName;
	}

	public void setOccurrenceName(String occurrenceName) {
		this.occurrenceName = occurrenceName;
	}

	public void setDocumentName(String documentName) {
		this.documentName = documentName;
	}

	public void setStartOffset(long startOffset) {
		this.startOffset = startOffset;
	}

	public void setEndOffset(long endOffset) {
		this.endOffset = endOffset;
	}

	public void setContainingSentence(String containingSentence) {
		this.containingSentence = containingSentence;
	}

	public void setHierarchy(String hierarchy) {
		this.hierarchy = hierarchy;
	}

	public String getDocumentName() {
		return documentName;
	}

	public long getStartOffset() {
		return startOffset;
	}

	public long getEndOffset() {
		return endOffset;
	}

	public String getContainingSentence() {
		return containingSentence;
	}

	public String getHierarchy() {
		return hierarchy;
	}
	
}
