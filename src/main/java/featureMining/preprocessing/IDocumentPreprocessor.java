package featureMining.preprocessing;

import featureMining.feature.OptionTransferObject;
import gate.Corpus;

/**
 * The Interface IDocumentPreprocessor.
 * This Interface is needed to provide the generic Structure
 * the DocumentProcessor in the next Step demands. By implementing 
 * this Interface, several Document Formats can be realized. For
 * now, there is only an Implementation for the Html Format.
 */
public interface IDocumentPreprocessor {

	/**
	 * Creates the annotated corpus.
	 *
	 * @param optionsTO the options to
	 * @return the annotated corpus
	 */
	public Corpus createAnnotatedCorpus(OptionTransferObject optionsTO);

}
