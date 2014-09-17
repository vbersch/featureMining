package featureMining;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

import java.util.ArrayList;
import java.util.Iterator;

public class DocAnnotator {
	
	private Corpus corpus;
	private Document testDoc;
	private ArrayList<String> featureStrings;
	
	public DocAnnotator(Document doc){
		this.testDoc = doc;
		try {
			corpus = gate.Factory.newCorpus("testCorpus");
		} catch (ResourceInstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.corpus.add(this.testDoc);
		featureStrings = new ArrayList<String>();
	}
	
	public void processDocument(){
		
		String[] processingResources = {
				"gate.creole.tokeniser.DefaultTokeniser",
		        "gate.creole.splitter.SentenceSplitter",
		        "gate.creole.POSTagger"
		};
		
		try {
			runProcessingResources(processingResources);
		} catch (GateException e) {
			e.printStackTrace();
		}
		
		System.out.println("\ntest: " + this.testDoc.getAnnotations().getAllTypes().toString());
		AnnotationSet defaultAnnots = this.testDoc.getAnnotations();
		AnnotationSet tokens = defaultAnnots.get("Token");
		AnnotationSet origAnnots = this.testDoc.getAnnotations("Original markups");
		AnnotationSet listEl = origAnnots.get("li");
		AnnotationSet featureAnnots = this.testDoc.getAnnotations("Feature Annotations");
		
		Iterator<Annotation> it = listEl.iterator();
		while(it.hasNext()){
			Annotation next = it.next();
			
			if(!listEl.getContained(next.getStartNode().getOffset(), next.getEndNode().getOffset()).contains("li")){
				Iterator<Annotation> it2 = tokens.getContained(next.getStartNode().getOffset(), next.getEndNode().getOffset()).iterator();
				String heading = "";
				while(it2.hasNext()){
					Annotation token = it2.next();
					String category = token.getFeatures().get("category").toString();
					if(category == "NNP" || category == "NNS"){
						featureStrings.add(gate.Utils.stringFor(this.testDoc, token));
						featureAnnots.add(token);
						heading += gate.Utils.stringFor(this.testDoc, token) + " ";
					}
				}
				System.out.println(heading);
			}
			
		}
		FeatureMining.getSingleton().getRootWindow().fillFeatureList(featureStrings);
//		it = featureAnnots.iterator();
//		while(it.hasNext()){
//			Annotation next = it.next();
//			System.out.println("Type: " + next.getType() + " ------ Content: " + gate.Utils.stringFor(this.testDoc, next) + " ----- Features: " + next.getFeatures().get("category"));
//		}
	}
	
	private void runProcessingResources(String[] processingResource)
	          throws GateException {
	    SerialAnalyserController pipeline = (SerialAnalyserController)Factory
	            .createResource("gate.creole.SerialAnalyserController");

	    for(int pr = 0; pr < processingResource.length; pr++) {
	      System.out.print("\t* Loading " + processingResource[pr] + " ... ");
	      pipeline.add((gate.LanguageAnalyser)Factory
	              .createResource(processingResource[pr]));
	      System.out.println("done");
	    }
	    pipeline.setCorpus(this.corpus);
	    System.out.print("Running processing resources over corpus...");
	    pipeline.execute();
	    System.out.println("done");
	  }
}
