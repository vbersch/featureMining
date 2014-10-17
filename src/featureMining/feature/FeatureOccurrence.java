package featureMining.feature;

import java.io.Serializable;

public class FeatureOccurrence implements Serializable{
	
	private static final long serialVersionUID = 5672536098702575276L;
	private String documentName;
	private long startOffset;
	private long endOffset;
	private String containingSentence;
	
	public FeatureOccurrence(String docName, long start, long end, String wholeSentence){
		this.documentName = docName;
		startOffset = start;
		endOffset = end;
		containingSentence = wholeSentence;
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
	
	
	
}
