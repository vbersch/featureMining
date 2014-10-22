package featureMining.preprocessing.html;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.util.OffsetComparator;

public class GithubHeadingAnnotator extends HtmlHeadingAnnotator{
	private AnnotationSet contentBody;
	
	
	public GithubHeadingAnnotator(){
		super();
		contentBody = null;
	}
	
	@Override
	public void annotateCorpus(Corpus corpus, String baseDoc){
		System.out.println("Github Heading Annotator started...");
		for(Document doc : corpus){
			if(doc.getName() != baseDoc){
				this.buildHeadingHierarchy(doc);
			}
		}
		System.out.println("Building Document Hierarchy done");
	}
	
	@Override
	public void buildHeadingHierarchy(Document doc){
		AnnotationSet origAnnots = doc.getAnnotations("Original markups");
		AnnotationSet divAnnots = origAnnots.get("div");
		List sortedDivAnnots = new ArrayList(divAnnots);
		Collections.sort(sortedDivAnnots, new OffsetComparator());
		
		for(int i = 0; i < sortedDivAnnots.size(); i++ ){
			Annotation divAnnot = (Annotation)sortedDivAnnots.get(i);
			String divId = (String) divAnnot.getFeatures().get("id");
			String divClass = (String) divAnnot.getFeatures().get("class");
			if(divId != null){
				if(divId.equals("wiki-body")){
					contentBody = gate.Utils.getContainedAnnotations(origAnnots, divAnnot);
					String body = gate.Utils.stringFor(doc, gate.Utils.start(contentBody), gate.Utils.end(contentBody));
					addHeadings(doc, origAnnots, contentBody);
				}
			}
			if(divClass != null){
				if(divClass.equals("gh-header")){
					AnnotationSet mainHeadingSet = gate.Utils.getContainedAnnotations(origAnnots, divAnnot);
					addHeadings(doc, origAnnots, mainHeadingSet);
				}
			}
		}

		System.out.println("adding content annotations");
		this.addContentAnnotations(doc, contentBody);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void addHeadings(Document doc, AnnotationSet origAnnots, AnnotationSet annotSet){
		List sortedAnnots = new ArrayList(annotSet);
		Collections.sort(sortedAnnots , new OffsetComparator());
		for(int i = 0; i < sortedAnnots.size(); i++ ){
			Annotation annot = (Annotation) sortedAnnots.get(i);
			if(this.isValidTag(annot)){
				long start = annot.getStartNode().getOffset();
				long end = annot.getEndNode().getOffset();
				if(!gate.Utils.stringFor(doc, start , end).contains("Navigation")){
					FeatureMap map = Factory.newFeatureMap();
					if(annot.getType().equals("h1")){
						map.put("hierarchy", "topLevel");
					}else if(annot.getType().equals("h2")){
						map.put("hierarchy", "secondLevel");
					}else if(annot.getType().equals("h3")){
						map.put("hierarchy", "thirdLevel");
					}
					gate.Utils.addAnn(origAnnots, start, end, "heading", map);
				}
			}
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void addContentAnnotations(Document doc , AnnotationSet origAnnots){
		
		if(contentBody != null){

			long start = gate.Utils.start(contentBody);
			
			AnnotationSet headings = contentBody.get("heading");
			if(headings.size() > 0){
				List sortedHeadings = new ArrayList(headings);
				Collections.sort(sortedHeadings, new OffsetComparator());
				
				for(int i = -1; i < sortedHeadings.size(); i++ ){
					Annotation heading = null;
					Annotation nextHeading = null;
					long end = 0;
					if(i+1<sortedHeadings.size()){
						nextHeading = (Annotation) sortedHeadings.get(i+1);
						end = nextHeading.getStartNode().getOffset();
					}else{
						end = gate.Utils.end(contentBody);
					}
					if(end - start > 0){
						FeatureMap map = Factory.newFeatureMap();
						gate.Utils.addAnn(origAnnots, start, end, "content", map);
						heading = nextHeading;
						start = heading.getEndNode().getOffset();
					}
				}
			}else{
				FeatureMap map = Factory.newFeatureMap();
				gate.Utils.addAnn(origAnnots, start, gate.Utils.end(contentBody), "content", map);
			}
		}
	}
}
