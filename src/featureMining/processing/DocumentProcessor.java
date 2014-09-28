package featureMining.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import featureMining.feature.FeatureContainer;
import featureMining.main.FeatureMining;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.OffsetComparator;

public class DocumentProcessor implements ISimpleProcessor{

	@Override
	public FeatureContainer processCorpus(FeatureContainer featureContainer, Corpus corpus) {
		
		String[] processingResources = {
				"gate.creole.tokeniser.DefaultTokeniser",
				"gate.creole.splitter.SentenceSplitter",
				"gate.creole.POSTagger" };

		try {
			runProcessingResources(processingResources, corpus);
		} catch (GateException e) {
			e.printStackTrace();
		}
		
		Iterator<Document> docIterator = corpus.iterator();
		while (docIterator.hasNext()) {
			Document doc = docIterator.next();
			AnnotationSet tokens = doc.getAnnotations().get("Token");
			AnnotationSet headings = doc.getAnnotations("Original markups")
					.get("heading");

			Iterator<Annotation> it = headings.iterator();
			while (it.hasNext()) {
				Annotation next = it.next();
				String wholeSentence = gate.Utils.stringFor(doc, next
						.getStartNode().getOffset(), next.getEndNode()
						.getOffset());

				List sortedTokens = new ArrayList(tokens.getContained(next
						.getStartNode().getOffset(), next.getEndNode()
						.getOffset()));
				Collections.sort(sortedTokens, new OffsetComparator());
				Annotation first = null;
				Annotation last = null;
				for (int i = 0; i < sortedTokens.size(); i++) {
					Annotation token = (Annotation) sortedTokens.get(i);
					String category = token.getFeatures().get("category")
							.toString();
					if (category == "NNP" || category == "NNS"
							|| category == "NN" || category == "NNPS") {
						if (first == null) {
							first = token;
						} else {
							last = token;
						}
					} else {
						if (first != null) {
							if (last == null) {
								last = first;
							}
							String featureString = gate.Utils.stringFor(doc,
									first.getStartNode().getOffset(), last
											.getEndNode().getOffset());
							featureContainer.add(featureString, doc.getName(),
									wholeSentence);
							first = null;
							last = null;
						}
					}
				}
				if(first != null) {
					if (last == null) {
						last = first;
					} // if it2 comes to an end and a noun was the last word
					String featureString = gate.Utils.stringFor(doc, first
							.getStartNode().getOffset(), last.getEndNode()
							.getOffset());
					featureContainer.add(featureString, doc.getName(),
							wholeSentence);
					first = null;
					last = null;
				}
			}
			AnnotationSet contents = doc.getAnnotations("Original markups").get("content");
			List sortedContents = new ArrayList(contents);
			
			Collections.sort(sortedContents, new OffsetComparator());
			
			for(int i = 0; i < sortedContents.size(); i++ ){
				Annotation contentAnnot = (Annotation) sortedContents.get(i);
				for(String feature : featureContainer.getFeatureStorage().keySet()){
					String content = gate.Utils.stringFor(doc, contentAnnot.getStartNode().getOffset(), contentAnnot.getEndNode().getOffset());
					Pattern featurePattern = Pattern.compile(feature);
					Matcher featureMatcher = featurePattern.matcher(content);
					while(featureMatcher.find()){
						long startIndex = contentAnnot.getStartNode().getOffset() + (long)featureMatcher.start();
						long endIndex = contentAnnot.getStartNode().getOffset() + (long)featureMatcher.end();
						Iterator sentenceIt = doc.getAnnotations().get(startIndex, endIndex).get("Sentence").iterator();
						while(sentenceIt.hasNext()){
							Annotation sentence = (Annotation) sentenceIt.next();
							String wholeSentence = gate.Utils.stringFor(doc, sentence.getStartNode().getOffset(), sentence.getEndNode().getOffset());
							featureContainer.addOccurence(feature, wholeSentence);
						}
					}
				}
			}
		}
		return featureContainer;
	}
	
	private void runProcessingResources(String[] processingResource, Corpus corpus)
			throws GateException {
		SerialAnalyserController pipeline = (SerialAnalyserController) Factory
				.createResource("gate.creole.SerialAnalyserController");

		for (int pr = 0; pr < processingResource.length; pr++) {
			FeatureMining
					.getSingleton()
					.getRootWindow()
					.addInfoTextLine(
							"\n\t* Loading " + processingResource[pr] + " ... ");
			System.out.print("\n\t* Loading " + processingResource[pr]
					+ " ... ");
			pipeline.add((gate.LanguageAnalyser) Factory
					.createResource(processingResource[pr]));
			FeatureMining.getSingleton().getRootWindow()
					.addInfoTextLine("done");
			System.out.println("done");
		}
		pipeline.setCorpus(corpus);
		FeatureMining
				.getSingleton()
				.getRootWindow()
				.addInfoTextLine(
						"\nRunning processing resources over corpus...");
		System.out.print("Running processing resources over corpus...");
		pipeline.execute();
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("done");
		System.out.println("done");
	}
	
}
