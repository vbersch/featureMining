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

public class MixxxHeadingAnnotator extends HtmlHeadingAnnotator{

	@Override
	public void annotateCorpus(Corpus corpus, String baseDoc){
		System.out.println("Mixxx Heading Annotator started...");
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
		List<Annotation> sortedAnnots = new ArrayList<Annotation>(origAnnots);
		
		Collections.sort(sortedAnnots , new OffsetComparator());
				
		for(int i = 0; i < sortedAnnots.size(); i++ ){
			Annotation annot = (Annotation) sortedAnnots.get(i);
			if(this.isValidTag(annot)){
				long start = annot.getStartNode().getOffset();
				long end = annot.getEndNode().getOffset();
				if(!gate.Utils.stringFor(doc, start , end).contains("Navigation")){
					FeatureMap map = Factory.newFeatureMap();
//					if(annot.getType().equals("h1")){
//						map.put("hierarchy", "topLevel");
//					}else if(annot.getType().equals("h2")){
//						map.put("hierarchy", "secondLevel");
//					}else if(annot.getType().equals("h3")){
//						map.put("hierarchy", "thirdLevel");
//					}
					map.put("hierarchy", annot.getType());
					gate.Utils.addAnn(origAnnots, start, end, "heading", map);
				}
			}
		}
		this.addContentAnnotations(doc, origAnnots);
	}
}
