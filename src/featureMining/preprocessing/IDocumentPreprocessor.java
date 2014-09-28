package featureMining.preprocessing;

import featureMining.ui.OptionTransferObject;
import gate.Corpus;

public interface IDocumentPreprocessor {

	public Corpus createAnnotatedCorpus(OptionTransferObject optionsTO);

}
