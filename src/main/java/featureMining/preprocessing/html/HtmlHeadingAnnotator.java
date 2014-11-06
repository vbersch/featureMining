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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HtmlHeadingAnnotator.
 * The HtmlHeadingAnnotator is an Approach to find Html headings 
 * without specializing. It shall provide a generic Heading Annotator 
 * and simultaneously serve as Superclass for specialized Heading 
 * Annotators.
 */
public class HtmlHeadingAnnotator {

	/** the logger */
	private static Logger logger = LoggerFactory.getLogger(HtmlHeadingAnnotator.class);
	
	/** valid heading tags. */
	private HashMap<String, Integer> validTags; 
	
	/**
	 * Constructor.
	 */
	public HtmlHeadingAnnotator(){
		validTags = new HashMap<String, Integer>();
		validTags.put("h1", 5);
		validTags.put("h2", 4);
		validTags.put("h3", 3);
		validTags.put("h4", 2);
		validTags.put("h5", 1);
	}

	/**
	 * Annotates the Gate Corpus.
	 *
	 * @param corpus the corpus
	 * @param baseDoc the base doc
	 */
	public void annotateCorpus(Corpus corpus, String baseDoc){
		
		logger.info("General Html Heading Annotator started...");
		for(Document doc : corpus){
			if(doc.getName() != baseDoc){
				this.buildHeadingHierarchy(doc);
				this.addContentAnnotations(doc);
			}
		}
		logger.info("Building Document Hierarchy done");
	}
	
	/**
	 * Checks if the Annotation is a valid html h-Tag.
	 *
	 * @param annot the annot
	 * @return true, if is valid tag
	 */
	public boolean isValidTag(Annotation annot){
		return this.validTags.containsKey(annot.getType());
	}
	
	/**
	 * Builds the heading hierarchy.
	 *
	 * @param doc the doc
	 */
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
		addContentAnnotations(doc);
	}

	
	/**
	 * Adds the content annotations.
	 *
	 * @param doc the doc
	 * @param origAnnots the orig annots
	 */
	public void addContentAnnotations(Document doc){
		AnnotationSet origAnnots = doc.getAnnotations("Original markups");
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
}
