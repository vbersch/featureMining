package featureMining.processing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import featureMining.controller.FeatureMining;
import featureMining.feature.FeatureContainer;
import featureMining.persistence.SettingsManager;
import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.LanguageAnalyser;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

/**
 * The Class DocumentProcessor.
 * The DocumentProcessor calls several Gate Processing Resources 
 * in a Pipeline and fills a FeatureContainer with the results.
 */
public class DocumentProcessor implements ISimpleProcessor{
	
	/** the logger */
	private static Logger logger = LoggerFactory.getLogger(DocumentProcessor.class);
	
	/**
	 * Constructor.
	 *
	 */
	public DocumentProcessor(){
		
	}
	
	/* (non-Javadoc)
	 * @see featureMining.processing.ISimpleProcessor#processCorpus(featureMining.feature.FeatureContainer, gate.Corpus)
	 * Declares some Processing Resources and runs them.
	 */
	@Override
	public FeatureContainer processCorpus(FeatureContainer featureContainer, Corpus corpus) {
		
		featureContainer.setLinkNum(corpus.getDocumentNames().size());
		featureContainer.setOptions(SettingsManager.getOptions());
		
		String[] processingResources = {
				"gate.creole.tokeniser.DefaultTokeniser", //Adds Tokens to every Document
				"gate.creole.splitter.SentenceSplitter", //Splits the Document into Sentence Annotations
				"gate.creole.POSTagger", //Adds Part-of-Speech Features to the Tokens Feature Maps
				"stemmer.SnowballStemmer", //Provides a Word Stem for every Token
				//does the Feature Mining based on the Annotation Structure 
				//after preprocessing
				"featureMining.processing.pr.DocumentProcessorPR" 
		};

		try {
			featureContainer = runProcessingResources(processingResources, corpus, featureContainer);
		} catch (GateException e) {
			e.printStackTrace();
		}
		
		return featureContainer;
	}
	
	/**
	 * Creates a Gate Pipeline and runs the above specified 
	 * Processing Resources.
	 *
	 * @param processingResource the processing resource
	 * @param corpus the corpus
	 * @param featureContainer the feature container
	 * @return the feature container
	 * @throws GateException the gate exception
	 */
	private FeatureContainer runProcessingResources(String[] processingResource, Corpus corpus, FeatureContainer featureContainer)
			throws GateException {
		SerialAnalyserController pipeline = (SerialAnalyserController)Factory.createResource("gate.creole.SerialAnalyserController");
		LanguageAnalyser res = null;
		for (int pr = 0; pr < processingResource.length; pr++) {
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\n\t* Loading " + processingResource[pr] + " ... ");
			logger.info("\n\t* Loading " + processingResource[pr]
					+ " ... ");

			if(processingResource[pr].equals("featureMining.processing.pr.DocumentProcessorPR")){	
				FeatureMap map = Factory.newFeatureMap();
				map.put("featureContainer", featureContainer);
				res = (gate.LanguageAnalyser) Factory.createResource(processingResource[pr], map);
			}else{
				res = (gate.LanguageAnalyser) Factory.createResource(processingResource[pr]);
			}
			pipeline.add(res);
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("done");
			logger.info("done");
		}
		
		pipeline.setCorpus(corpus);
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nRunning processing resources over corpus...");
		logger.info("Running processing resources over corpus...");
		pipeline.execute();
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("done");
		logger.info("done");
		return (FeatureContainer) res.getFeatures().get("featureContainer");
	}
}
