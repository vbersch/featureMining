package featureMining.preprocessing.html;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.util.OffsetComparator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;


public class HtmlHeadingAnnotator {
	
	private ArrayList<String> headings;
	private HashMap<String, Integer> validTags; 
	private Document mainDoc;
	
	
	public HtmlHeadingAnnotator(){
		mainDoc = null;
		headings = new ArrayList<String>();
		validTags = new HashMap<String, Integer>();
		validTags.put("h1", 5);
		validTags.put("h2", 4);
		validTags.put("h3", 3);
		validTags.put("h4", 2);
		validTags.put("h5", 1);
		//validTags.put("li", 0);
	}
	
	
	public void annotateCorpus(Corpus corpus, String baseDoc){
		
		for(Document doc : corpus){
			if(doc.getName() == baseDoc){
				this.mainDoc = doc;
				//this.parseOverviewPage(mainDoc);
			}else{
				this.buildHeadingHierarchy(doc);
			}
		}
		
//		for(Document doc : corpus){
//			if(doc != mainDoc){
//				String highestTag = this.getHighestTag(doc);
//			}
//		}
		
		System.out.println("Building Document Hierarchy done");
		
	}
	
	private void parseOverviewPage(Document mainDoc) {
		AnnotationSet origAnnots = mainDoc.getAnnotations("Original markups");
		List sortedAnnots = new ArrayList(origAnnots);
		//System.out.println("Hello");
		
		
		//System.out.println(mainDoc.getContent());
		
		for(int i = 0; i < sortedAnnots.size(); i++ ){
			Annotation annot = (Annotation) sortedAnnots.get(i);
			if(this.isValidTag(annot)){
				if(gate.Utils.stringFor(mainDoc, annot.getStartNode().getOffset(), annot.getEndNode().getOffset()).contains("Table of Contents")){
					System.out.println(gate.Utils.stringFor(mainDoc, annot.getStartNode().getOffset(), annot.getEndNode().getOffset()));
				}
			}
		}
		
	}

	private boolean isValidTag(Annotation annot){
		return this.validTags.containsKey(annot.getType());
	}
	
	
	private void buildHeadingHierarchy(Document doc) {
		AnnotationSet origAnnots = doc.getAnnotations("Original markups");
		List sortedAnnots = new ArrayList(origAnnots);
		
		Collections.sort(sortedAnnots , new OffsetComparator());
				
		for(int i = 0; i < sortedAnnots.size(); i++ ){
			Annotation annot = (Annotation) sortedAnnots.get(i);
			if(this.isValidTag(annot)){
				//String text = gate.Utils.stringFor(doc, annot.getStartNode().getOffset(), annot.getEndNode().getOffset());
				//System.out.println("Type: " + annot.getType() + " text: " + text);
				//if(mainDoc.getContent().toString().contains(text)){
					FeatureMap map = Factory.newFeatureMap();
					gate.Utils.addAnn(origAnnots, annot.getStartNode().getOffset(), annot.getEndNode().getOffset(), "heading", map);
				//}else{
					//System.out.println("Kein Treffer");
				//}
			}
		}
		AnnotationSet headings = origAnnots.get("heading");
		List sortedHeadings = new ArrayList(headings);
		
		Collections.sort(sortedHeadings, new OffsetComparator());
		
		for(int i = 0; i < sortedHeadings.size(); i++ ){
			Annotation heading = (Annotation)sortedHeadings.get(i);
			long start = heading.getEndNode().getOffset();
			long end = 0;
			if(i+1<sortedHeadings.size()){
				Annotation nextHeading = (Annotation) sortedHeadings.get(i+1);
				end = nextHeading.getStartNode().getOffset();
			}else{
				end = doc.getContent().size();
			}
			if(end - start > 0){
				FeatureMap map = Factory.newFeatureMap();
				gate.Utils.addAnn(origAnnots, start, end, "content", map);
			}
		}
	}


	private String compareTags(String newTag, String type) {
		if(validTags.get(newTag) > validTags.get(type)){
			return newTag;
		}else{
			return type;
		}
	}


	private String getHighestTag(Document doc) {
		
		AnnotationSet origAnnots = doc.getAnnotations("Original markups");
		
		AnnotationSet h1 = origAnnots.get("h1");
		AnnotationSet h2 = origAnnots.get("h2");
		AnnotationSet h3 = origAnnots.get("h3");
		AnnotationSet h4 = origAnnots.get("h4");
		AnnotationSet h5 = origAnnots.get("h5");
		AnnotationSet li = origAnnots.get("li");
		
		List sortedAnnots = new ArrayList(origAnnots);
		Collections.sort(sortedAnnots, new OffsetComparator());
		for(int i = 0; i < sortedAnnots.size(); i++){
			Annotation annot = (Annotation) sortedAnnots.get(i);
			System.out.println("type: " + annot.getType());
		}
		
		
		return null;
	}


	private void printHeadings(Corpus corpus){
		for(Document doc : corpus){
			
		}
	}
	
}
