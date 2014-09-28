package featureMining.processing;

import featureMining.feature.FeatureContainer;
import gate.Corpus;

public interface ISimpleProcessor {
	
	FeatureContainer processCorpus(FeatureContainer featureContainer, Corpus corpus);
	
}
