package featureMining.processing;

import featureMining.controller.FeatureMining;
import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;
import featureMining.persistence.SettingsManager;
import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.LanguageAnalyser;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

public class DocumentProcessor implements ISimpleProcessor{
		
	public DocumentProcessor(OptionTransferObject optionsTO){
		
	}
	
	@Override
	public FeatureContainer processCorpus(FeatureContainer featureContainer, Corpus corpus) {
		
		featureContainer.setLinkNum(corpus.getDocumentNames().size());
		featureContainer.setOptions(SettingsManager.getOptions());
		
		String[] processingResources = {
				"gate.creole.tokeniser.DefaultTokeniser",
				"gate.creole.splitter.SentenceSplitter",
				"gate.creole.POSTagger",
				"stemmer.SnowballStemmer",
				"featureMining.processing.pr.DocumentProcessorPR"
		};

		try {
			featureContainer = runProcessingResources(processingResources, corpus, featureContainer);
		} catch (GateException e) {
			e.printStackTrace();
		}
		
		return featureContainer;
	}
	
	private FeatureContainer runProcessingResources(String[] processingResource, Corpus corpus, FeatureContainer featureContainer)
			throws GateException {
		SerialAnalyserController pipeline = (SerialAnalyserController)Factory.createResource("gate.creole.SerialAnalyserController");
		LanguageAnalyser res = null;
		for (int pr = 0; pr < processingResource.length; pr++) {
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\n\t* Loading " + processingResource[pr] + " ... ");
			System.out.print("\n\t* Loading " + processingResource[pr]
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
			System.out.println("done");
		}
		
		pipeline.setCorpus(corpus);
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nRunning processing resources over corpus...");
		System.out.print("Running processing resources over corpus...");
		pipeline.execute();
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("done");
		System.out.println("done");
		return (FeatureContainer) res.getFeatures().get("featureContainer");
	}
}
