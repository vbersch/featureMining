package featureMining;

import gate.Corpus;
import gate.Document;

import java.util.ArrayList;

public abstract class SimpleProcessor {
	
	protected Corpus corpus;
	protected Document testDoc;
	protected ArrayList<String> featureStrings;
	
	public abstract void processCorpus();
	public abstract void createCorpus(String address);
	
}
