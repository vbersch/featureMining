package featureMining.preprocessing;

import featureMining.feature.OptionTransferObject;
import gate.Corpus;

public interface IDocumentPreprocessor {

	public Corpus createAnnotatedCorpus(OptionTransferObject optionsTO);

}
