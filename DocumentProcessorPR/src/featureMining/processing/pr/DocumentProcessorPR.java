/*
 *  DocumentProcessorPR.java
 *
 * Copyright (c) 2000-2012, The University of Sheffield.
 *
 * This file is part of GATE (see http://gate.ac.uk/), and is free
 * software, licenced under the GNU Library General Public License,
 * Version 3, 29 June 2007.
 *
 * A copy of this licence is included in the distribution in the file
 * licence.html, and is also available at http://gate.ac.uk/gate/licence.html.
 *
 *  test, 3/10/2014
 *
 * For details on the configuration options, see the user guide:
 * http://gate.ac.uk/cgi-bin/userguide/sec:creole-model:config
 */

package featureMining.processing.pr;

import featureMining.feature.Feature;
import featureMining.feature.FeatureContainer;
import featureMining.feature.FeatureOccurrence;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.LanguageAnalyser;
import gate.Resource;
import gate.creole.AbstractProcessingResource;
import gate.creole.ExecutionException;
import gate.creole.ResourceInstantiationException;
import gate.creole.metadata.CreoleResource;
import gate.util.OffsetComparator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class is the implementation of the resource DOCUMENTPROCESSORPR.
 */
@CreoleResource(name = "DocumentProcessorPR", 
				comment = "A processing Ressource to mine the Features of a Gate Corpus")
public class DocumentProcessorPR extends AbstractProcessingResource implements LanguageAnalyser{

	private static final long serialVersionUID = 4408363290028424392L;
	private Corpus corpus;
	private Document document;
	private static FeatureContainer featureContainer;
	private static int docNum;
	private static int currentDoc;
	private static FileWriter fileWriter;

	public Resource init() throws ResourceInstantiationException {
		String path = System.getProperty("user.dir") + "/DocumentProcessorPR/processingLog.log";
		try {
			fileWriter = new FileWriter(new File(path));
			docNum = featureContainer.getLinkNum();
			currentDoc = 0;
		
			fileWriter.write("init processing resource...");
			fileWriter.write("\n\n\nMining Features in heading Annotations\n--------------------------------------------\n\n");
		} catch (IOException e) {
			e.printStackTrace();
		}
		return this;
	}

	public void execute() throws ExecutionException {
		AnnotationSet headings = document.getAnnotations("Original markups")
				.get("heading");

		Iterator<Annotation> it = headings.iterator();
		
		try {
			fileWriter.write("\nprocessing for Document: " + document.getName());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		while (it.hasNext()) {
			Annotation next = it.next();
			this.parseHeading(next, document, featureContainer);
		}
		
		
		if(currentDoc == docNum - 1){
			//last processing resource instance parses all documents and matches the Features
			try {
				fileWriter.write("\n\n\nMatching the Features in content Annotations\n--------------------------------------------\n\n\n");
				for(Document doc : this.corpus){
					fileWriter.write("Adding Feature Occurrences for Document " + doc.getName() + "\n");
					this.addFeatureOccurrences(doc, featureContainer);
				}
				this.corpus.getFeatures().put("result", featureContainer);
			
				fileWriter.close();
			} catch (IOException e) {
				
				e.printStackTrace();
			}
		}else{
			currentDoc++;
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void parseHeading(Annotation next, Document doc, FeatureContainer featureContainer) {
		AnnotationSet tokens = doc.getAnnotations().get("Token");
		long start = next.getStartNode().getOffset();
		long end = next.getEndNode().getOffset();
		String wholeSentence = gate.Utils.stringFor(doc, start, end);
		String hierarchy = next.getFeatures().get("hierarchy").toString();
		hierarchy = "bla";
		if(containsBlacklistWord(wholeSentence , featureContainer.getOptions().getSentenceBlacklist())){
			return;
		}
		List sortedTokens = new ArrayList(tokens.getContained(next.getStartNode().getOffset(), next.getEndNode().getOffset()));
		Collections.sort(sortedTokens, new OffsetComparator());
		int i = 0;
		while(i < sortedTokens.size()) {
			Annotation first = null;
			Annotation last = null;
			ArrayList<Annotation> featureAnnots;
			Annotation token = (Annotation) sortedTokens.get(i);
			String category = token.getFeatures().get("category").toString();

			if (isNoun(category,featureContainer.getOptions().isDomainSpecific())){
				int j = 0;
				boolean foundFirst = false;
				boolean foundLast = false;
				do{
					if(i + j < sortedTokens.size()){
						Annotation nextToken = (Annotation)sortedTokens.get(i+j);
						String nextCategory = nextToken.getFeatures().get("category").toString();
						if(isNoun(nextCategory, false) && !foundLast){
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
						if(isNoun(prevCategory, false) && !foundFirst){
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
				if(!containsBlacklistWord(featureString, featureContainer.getOptions().getFeatureBlacklist())){
					featureAnnots = new ArrayList(tokens.get(first.getStartNode().getOffset(), last.getEndNode().getOffset()));
					Collections.sort(featureAnnots , new OffsetComparator());
					featureContainer.addFeature(featureAnnots, wholeSentence, doc, hierarchy);
				}
			}else{
				i++;
			}
		}
	}
	
	private boolean containsBlacklistWord(String candidate, ArrayList<String> sentenceBlacklist) {
		for(String word : sentenceBlacklist){
			if(candidate.contains(word)){
				return true;
			}
		}
		return false;
	}

	public boolean isNoun(String category, boolean dsp) {
		if(dsp){
			return (category.equals("NNP") || category.equals("NNPS"));
		}
		return (category.equals("NNP") || category.equals("NNPS") || category.equals("NN") || category.equals("NNS"));
	}
	
	private void addFeatureOccurrences(Document doc , FeatureContainer featureContainer){
		AnnotationSet contents = doc.getAnnotations("Original markups").get("content");
		AnnotationSet tokens = doc.getAnnotations().get("Token");
		List<Annotation> sortedContents = new ArrayList<Annotation>(contents);
		
		Collections.sort(sortedContents, new OffsetComparator());
		
		if(featureContainer.getOptions().isEnableStemming()){
		
			for(int i = 0; i < sortedContents.size(); i++ ){
				Annotation contentAnnot = (Annotation) sortedContents.get(i);
				AnnotationSet contentTokens = tokens.get(contentAnnot.getStartNode().getOffset() , contentAnnot.getEndNode().getOffset());
				
				ArrayList<Annotation> sortedTokens = new ArrayList<Annotation>(contentTokens);
				Collections.sort(sortedTokens , new OffsetComparator());
				for(int j = 0; j < sortedTokens.size(); j++){
					Annotation token = sortedTokens.get(j);
					String tokenStem = token.getFeatures().get("stem").toString();
					for(Map.Entry<String,Feature> e : featureContainer.getFeatureStorage().entrySet()){
						Feature feature = e.getValue();
						if(feature.getFeatureStem().startsWith(tokenStem)){
							checkForOccurrence(doc, feature, sortedTokens, j);
						}
					}
				}
			}
		}else{
			for(int i = 0; i < sortedContents.size(); i++ ){
				Annotation contentAnnot = (Annotation) sortedContents.get(i);
				for(String feature : featureContainer.getFeatureStorage().keySet()){
					String content = gate.Utils.stringFor(doc, contentAnnot.getStartNode().getOffset(), contentAnnot.getEndNode().getOffset());
					Pattern featurePattern = Pattern.compile(feature);
					Matcher featureMatcher = featurePattern.matcher(content);
					while(featureMatcher.find()){
						long startIndex = contentAnnot.getStartNode().getOffset() + (long)featureMatcher.start();
						long endIndex = contentAnnot.getStartNode().getOffset() + (long)featureMatcher.end();
						Iterator<Annotation> sentenceIt = doc.getAnnotations().get(startIndex, endIndex).get("Sentence").iterator();
						while(sentenceIt.hasNext()){
							Annotation sentence = (Annotation) sentenceIt.next();
							String wholeSentence = gate.Utils.stringFor(doc, sentence.getStartNode().getOffset(), sentence.getEndNode().getOffset());
							featureContainer.addOccurence(feature, wholeSentence, doc.getName(), startIndex, endIndex, "content");
						}
					}
				}
			}
		}
	}
	

	private int checkForOccurrence(Document doc, Feature feature, ArrayList<Annotation> sortedTokens, int index) {
		String featureStem = feature.getFeatureStem();
		String[] splitStem = featureStem.split(" ");
		int size = splitStem.length;
		String candidateStem = "";
		String candidateFeatureString = "";
		if(index + size < sortedTokens.size()){
			for(int i = index; i < index + size; i++){
				candidateStem += sortedTokens.get(i).getFeatures().get("stem") + " ";
				candidateFeatureString += sortedTokens.get(i).getFeatures().get("string") + " ";
			}
			
			candidateStem = candidateStem.trim();
			candidateFeatureString = candidateFeatureString.trim();
			if(featureStem.equals(candidateStem)){
				long startIndex = sortedTokens.get(index).getStartNode().getOffset();
				long endIndex = sortedTokens.get(index + size - 1).getEndNode().getOffset();
				String wholeSentence = "";
				Iterator<Annotation> sentenceIt = doc.getAnnotations().get(startIndex, endIndex).get("Sentence").iterator();
				while(sentenceIt.hasNext()){
					Annotation sentence = (Annotation) sentenceIt.next();
					wholeSentence = gate.Utils.stringFor(doc, sentence.getStartNode().getOffset(), sentence.getEndNode().getOffset());
				}
				
				FeatureOccurrence newOccurrence = new FeatureOccurrence();
				newOccurrence.setContainingSentence(wholeSentence);
				newOccurrence.setOccurrenceName(candidateFeatureString);
				newOccurrence.setDocumentName(doc.getName());
				newOccurrence.setStartOffset(startIndex);
				newOccurrence.setEndOffset(endIndex);
				newOccurrence.setHierarchy("content");
				
				feature.addFeatureOccurrence(newOccurrence);
				return index + size;
			}
		}
		return 0;
	}

	@Override
	public Corpus getCorpus() {
		return this.corpus;
	}

	@Override
	public Document getDocument() {
		return this.document;
	}

	@Override
	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	@Override
	public void setDocument(Document doc) {
		this.document = doc;
	}

	public FeatureContainer getFeatureContainer() {
		return featureContainer;
	}

	public void setFeatureContainer(FeatureContainer featureContainer2) {
		featureContainer = featureContainer2;
	}

} // class DocumentProcessorPR
