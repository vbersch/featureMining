package featureMining.preprocessing;

import gate.Corpus;
import gate.Document;

public interface IDocumentPreprocessor {

	
	public Corpus createAnnotatedCorpus(String address, String hostName);

}
