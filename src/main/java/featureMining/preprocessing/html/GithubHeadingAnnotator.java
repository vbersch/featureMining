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
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class GithubHeadingAnnotator.
 * This Class provides the Annotation of Headings and Contents 
 * for Html Documentation hosted on Github.com.
 */
public class GithubHeadingAnnotator extends HtmlHeadingAnnotator{
	
	/** the logger */
	private static Logger logger = LoggerFactory.getLogger(GithubHeadingAnnotator.class);
	
	/** Div Gate AnnotationSet with specific ID. */
	private AnnotationSet contentBody;
	
	/**
	 * Constructor.
	 */
	public GithubHeadingAnnotator(){
		super();
		contentBody = null;
	}
	
	/* (non-Javadoc)
	 * @see featureMining.preprocessing.html.HtmlHeadingAnnotator#annotateCorpus(gate.Corpus, java.lang.String)
	 * Implementation of the annotateCorpus method.
	 * Contains the logic to annotate the corpus with heading and content 
	 * Annotations.
	 */
	@Override
	public void annotateCorpus(Corpus corpus, String baseDoc){
		logger.info("Github Heading Annotator started...");
		for(Document doc : corpus){
			if(doc.getName() != baseDoc){
				this.buildHeadingHierarchy(doc);
				this.addContentAnnotations(doc);
			}
		}
		logger.info("Building Document Hierarchy done");
	}
	
	/* (non-Javadoc)
	 * @see featureMining.preprocessing.html.HtmlHeadingAnnotator#buildHeadingHierarchy(gate.Document)
	 * Looks for the div id´s "wiki-body" and "gh-header" and 
	 * calls addHeadings for their contents.
	 */
	@Override
	public void buildHeadingHierarchy(Document doc){
		AnnotationSet origAnnots = doc.getAnnotations("Original markups");
		AnnotationSet divAnnots = origAnnots.get("div");
		List<Annotation> sortedDivAnnots = new ArrayList<Annotation>(divAnnots);
		Collections.sort(sortedDivAnnots, new OffsetComparator());
		
		for(int i = 0; i < sortedDivAnnots.size(); i++ ){
			Annotation divAnnot = (Annotation)sortedDivAnnots.get(i);
			String divId = (String) divAnnot.getFeatures().get("id");
			String divClass = (String) divAnnot.getFeatures().get("class");
			if(divId != null){
				if(divId.equals("wiki-body")){
					contentBody = gate.Utils.getContainedAnnotations(origAnnots, divAnnot);
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

		logger.info("adding content annotations");
	}
	
	/**
	 * Adds Heading tags to a specific AnnotationSet and
	 * determines the headings hierarchy.
	 *
	 * @param doc the doc
	 * @param origAnnots the orig annots
	 * @param annotSet the annot set
	 */
	private void addHeadings(Document doc, AnnotationSet origAnnots, AnnotationSet annotSet){
		List<Annotation> sortedAnnots = new ArrayList<Annotation>(annotSet);
		Collections.sort(sortedAnnots , new OffsetComparator());
		for(int i = 0; i < sortedAnnots.size(); i++ ){
			Annotation annot = (Annotation) sortedAnnots.get(i);
			if(this.isValidTag(annot)){
				long start = annot.getStartNode().getOffset();
				long end = annot.getEndNode().getOffset();
				if(!gate.Utils.stringFor(doc, start , end).contains("Navigation")){
					FeatureMap map = Factory.newFeatureMap();
					//TODO fix heading hierarchy
					if(annot.getType().equals("h1")){
						map.put("hierarchy", "topLevel");
					}else if(annot.getType().equals("h2")){
						map.put("hierarchy", "secondLevel");
					}else if(annot.getType().equals("h3")){
						map.put("hierarchy", "thirdLevel");
					}else if(annot.getType().equals("h4")){
						map.put("hierarchy", "fourthLevel");
					}else if(annot.getType().equals("h5")){
						map.put("hierarchy", "fifthLevel");
					}
					gate.Utils.addAnn(origAnnots, start, end, "heading", map);
				}
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see featureMining.preprocessing.html.HtmlHeadingAnnotator#addContentAnnotations(gate.Document, gate.AnnotationSet)
	 * Adds the content Annotations to a Gate Document for a specific AnnotationSet.
	 */
	@Override
	public void addContentAnnotations(Document doc){
		if(contentBody != null){
			AnnotationSet origAnnots = doc.getAnnotations("Original markups");
			long start = gate.Utils.start(contentBody);
			
			AnnotationSet headings = contentBody.get("heading");
			if(headings.size() > 0){
				List<Annotation> sortedHeadings = new ArrayList<Annotation>(headings);
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
				long end = gate.Utils.end(contentBody);
				gate.Utils.addAnn(origAnnots, start, end, "content", map);
			}
		}
	}
}
