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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import featureMining.feature.FeatureContainer;
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

	public Resource init() throws ResourceInstantiationException {
		
		docNum = featureContainer.getLinkNum();
		currentDoc = 0;
		return this;
	}

	public void execute() throws ExecutionException {
		AnnotationSet headings = document.getAnnotations("Original markups")
				.get("heading");

		Iterator<Annotation> it = headings.iterator();
		while (it.hasNext()) {
			Annotation next = it.next();
			this.parseHeading(next, document, featureContainer);
		}
		this.addFeatureOccurrences(document, featureContainer);
		
		if(currentDoc == docNum - 1){
			this.corpus.getFeatures().put("result", featureContainer);
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
		if(containsBlacklistWord(wholeSentence , featureContainer.getOptions().getSentenceBlacklist())){
			return;
		}
		List sortedTokens = new ArrayList(tokens.getContained(next.getStartNode().getOffset(), next.getEndNode().getOffset()));
		Collections.sort(sortedTokens, new OffsetComparator());
		int i = 0;
		while(i < sortedTokens.size()) {
			Annotation first = null;
			Annotation last = null;
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
					featureContainer.add(featureString, wholeSentence, doc.getName(), first.getStartNode().getOffset(), last.getEndNode().getOffset());
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
						featureContainer.addOccurence(feature, wholeSentence, doc.getName(), startIndex, endIndex);
					}
				}
			}
		}
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
