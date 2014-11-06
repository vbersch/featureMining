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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class MixxxHeadingAnnotator.
 * Specific HeadingAnnotator for the Mixxx Project.
 */
public class MixxxHeadingAnnotator extends HtmlHeadingAnnotator{

	/** the logger */
	private static Logger logger = LoggerFactory.getLogger(MixxxHeadingAnnotator.class);
	
	/* (non-Javadoc)
	 * @see featureMining.preprocessing.html.HtmlHeadingAnnotator#annotateCorpus(gate.Corpus, java.lang.String)
	 * Loops over the Corpus and calls buildHeadingHierarchy
	 * and addContentAnnotations on every Document.
	 */
	@Override
	public void annotateCorpus(Corpus corpus, String baseDoc){
		logger.info("Mixxx Heading Annotator started...");
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
	 * Annotates every found heading within a Document.
	 */
	@Override
	public void buildHeadingHierarchy(Document doc){
		AnnotationSet origAnnots = doc.getAnnotations("Original markups");
		List<Annotation> sortedAnnots = new ArrayList<Annotation>(origAnnots);
		Pattern headingPattern = Pattern.compile("^[0-9\\.]+");
		Collections.sort(sortedAnnots , new OffsetComparator());
				
		for(int i = 0; i < sortedAnnots.size(); i++ ){
			Annotation annot = (Annotation) sortedAnnots.get(i);
			if(this.isValidTag(annot)){
				long start = annot.getStartNode().getOffset();
				long end = annot.getEndNode().getOffset();
				String sentence = gate.Utils.stringFor(doc, start , end); 
				if(!sentence.contains("Navigation")){
					FeatureMap map = Factory.newFeatureMap();
					Matcher matcher = headingPattern.matcher(sentence);
					
					if(matcher.find()){
						map.put("hierarchy" , matcher.group());
					}else {
						if(annot.getType().equals("h1")){
							map.put("hierarchy", "topLevel");
						}else if(annot.getType().equals("h2")){
							map.put("hierarchy", "secondLevel");
						}else if(annot.getType().equals("h3")){
							map.put("hierarchy", "thirdLevel");
						}else if(annot.getType().equals("h4")){
							map.put("hierarchy", "thirdLevel");
						}else if(annot.getType().equals("h5")){
							map.put("hierarchy", "thirdLevel");
						}
					}
					gate.Utils.addAnn(origAnnots, start, end, "heading", map);
				}
			}
		}	
	}
}
