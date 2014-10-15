package featureMining.processing;

import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;
import featureMining.main.FeatureMining;
import gate.Corpus;
import gate.Factory;
import gate.FeatureMap;
import gate.LanguageAnalyser;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

public class DocumentProcessor implements ISimpleProcessor{
	
	private OptionTransferObject optionsTO;
	
	public DocumentProcessor(OptionTransferObject optionsTO){
		this.optionsTO = optionsTO;
	}
	
	@Override
	public FeatureContainer processCorpus(FeatureContainer featureContainer, Corpus corpus) {
		
		featureContainer.setLinkNum(corpus.getDocumentNames().size());
		//corpus.getFeatures().put("featureContainer", FeatureContainer.packContainer(featureContainer));
		
		String[] processingResources = {
				"gate.creole.tokeniser.DefaultTokeniser",
				"gate.creole.splitter.SentenceSplitter",
				"gate.creole.POSTagger",
				"featureMining.processing.pr.DocumentProcessorPR"
		};

		try {
			runProcessingResources(processingResources, corpus, featureContainer);
		} catch (GateException e) {
			e.printStackTrace();
		}
		
		return (FeatureContainer) corpus.getFeatures().get("result");
	}
	
	private void runProcessingResources(String[] processingResource, Corpus corpus, FeatureContainer featureContainer)
			throws GateException {
		SerialAnalyserController pipeline = (SerialAnalyserController)Factory.createResource("gate.creole.SerialAnalyserController");

		for (int pr = 0; pr < processingResource.length; pr++) {
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\n\t* Loading " + processingResource[pr] + " ... ");
			System.out.print("\n\t* Loading " + processingResource[pr]
					+ " ... ");
			
			LanguageAnalyser res = null;
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
	}
}
