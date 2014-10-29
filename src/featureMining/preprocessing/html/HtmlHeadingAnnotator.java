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

	private HashMap<String, Integer> validTags; 
	private Document mainDoc;
	
	
	public HtmlHeadingAnnotator(){
		mainDoc = null;
		validTags = new HashMap<String, Integer>();
		validTags.put("h1", 5);
		validTags.put("h2", 4);
		validTags.put("h3", 3);
		validTags.put("h4", 2);
		validTags.put("h5", 1);
	}
	
	
	public void annotateCorpus(Corpus corpus, String baseDoc){
		
		System.out.println("General Html Heading Annotator started...");
		for(Document doc : corpus){
			if(doc.getName() == baseDoc){
				this.mainDoc = doc;
			}else{
				this.buildHeadingHierarchy(doc);
			}
		}
		System.out.println("Building Document Hierarchy done");
	}
	
	public boolean isValidTag(Annotation annot){
		return this.validTags.containsKey(annot.getType());
	}
	
	public void buildHeadingHierarchy(Document doc) {
		AnnotationSet origAnnots = doc.getAnnotations("Original markups");
		List<Annotation> sortedAnnots = new ArrayList<Annotation>(origAnnots);
		
		Collections.sort(sortedAnnots , new OffsetComparator());
				
		for(int i = 0; i < sortedAnnots.size(); i++ ){
			Annotation annot = (Annotation) sortedAnnots.get(i);
			if(this.isValidTag(annot)){
				FeatureMap map = Factory.newFeatureMap();
				gate.Utils.addAnn(origAnnots, annot.getStartNode().getOffset(), annot.getEndNode().getOffset(), "heading", map);
			}
		}
		addContentAnnotations(doc, origAnnots);
	}

	
	public void addContentAnnotations(Document doc , AnnotationSet origAnnots){
		AnnotationSet headings = origAnnots.get("heading");
		List<Annotation> sortedHeadings = new ArrayList<Annotation>(headings);
		
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
}
