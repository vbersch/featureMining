package featureMining.processing;

import featureMining.processing.feature.FeatureContainer;
import gate.Corpus;
import gate.Document;

// TODO: Auto-generated Javadoc
/**
 * The Class SimpleProcessor.
 */
public abstract class SimpleProcessor {
	
	/** The corpus. */
	protected Corpus corpus;
	
	/** The test doc. */
	//protected Document testDoc;
	
	/** The feature container. */
	protected FeatureContainer featureContainer;
	
	/**
	 * Process corpus.
	 */
	public abstract FeatureContainer processCorpus();
	
	/**
	 * Creates the corpus.
	 *
	 * @param address the address
	 */
	public abstract void createCorpus(String address);

	public synchronized void addDocument(Document doc) {
		this.corpus.add(doc);
	}
	
}
