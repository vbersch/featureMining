package featureMining.processing;

import featureMining.feature.FeatureContainer;
import gate.Corpus;

/**
 * The Interface ISimpleProcessor.
 * An Interface to provide a Possibility to 
 * exchange the Document Processor. 
 */
public interface ISimpleProcessor {
	
	/**
	 * A Method to process a Corpus and return the resulting 
	 * FeatureContainer.
	 *
	 * @param featureContainer the feature container
	 * @param corpus the corpus
	 * @return the feature container
	 */
	FeatureContainer processCorpus(FeatureContainer featureContainer, Corpus corpus);
	
}
