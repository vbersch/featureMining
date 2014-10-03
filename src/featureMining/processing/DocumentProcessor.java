package featureMining.processing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;
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
	
	private OptionTransferObject optionsTO;
	
	public DocumentProcessor(OptionTransferObject optionsTO){
		this.optionsTO = optionsTO;
	}
	
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
			AnnotationSet headings = doc.getAnnotations("Original markups")
					.get("heading");

			Iterator<Annotation> it = headings.iterator();
			while (it.hasNext()) {
				Annotation next = it.next();
				this.parseHeading(next, doc, featureContainer);
			}
			this.addFeatureOccurrences(doc, featureContainer);
		}
		return featureContainer;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parseHeading(Annotation next, Document doc, FeatureContainer featureContainer) {
		AnnotationSet tokens = doc.getAnnotations().get("Token");
		long start = next.getStartNode().getOffset();
		long end = next.getEndNode().getOffset();
		String wholeSentence = gate.Utils.stringFor(doc, start, end);
		List sortedTokens = new ArrayList(tokens.getContained(next.getStartNode().getOffset(), next.getEndNode().getOffset()));
		Collections.sort(sortedTokens, new OffsetComparator());
		int i = 0;
		while(i < sortedTokens.size()) {
			Annotation first = null;
			Annotation last = null;
			Annotation token = (Annotation) sortedTokens.get(i);
			String category = token.getFeatures().get("category").toString();
			if (isNoun(category,"dsp")){
				int j = 0;
				boolean foundFirst = false;
				boolean foundLast = false;
				do{
					if(i + j < sortedTokens.size()){
						Annotation nextToken = (Annotation)sortedTokens.get(i+j);
						String nextCategory = nextToken.getFeatures().get("category").toString();
						if(isNoun(nextCategory, "") && !foundLast){
							last = nextToken;
						}else{
							foundLast = true;
						}
					}else{
						foundLast = true;
					}
					if(i - j >= 0){
						Annotation prevToken = (Annotation)sortedTokens.get(i-j);
						String prevCategory = prevToken.getFeatures().get("category").toString();
						if(isNoun(prevCategory, "") && !foundFirst){
							first = prevToken;
						}else{
							foundFirst = true;
						}
					}else{
						foundFirst = true;
					}
					j++;
				}while(!foundFirst || !foundLast);
				i = i + j;
				String featureString = gate.Utils.stringFor(doc,
				first.getStartNode().getOffset(), last.getEndNode().getOffset());
				featureContainer.add(featureString, doc.getName(),wholeSentence);
			}else{
				i++;
			}
		}
	}

	public boolean isNoun(String category, String dsp) {
		if(dsp.equals("dsp") && this.optionsTO.isDomainSpecific()){
			return (category.equals("NNP") || category.equals("NNPS"));
		}
		return (category.equals("NNP") || category.equals("NNPS") || category.equals("NN") || category.equals("NNS"));
	}

	private void addFeatureOccurrences(Document doc , FeatureContainer featureContainer){
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
