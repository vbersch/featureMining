package featureMining.persistence.datastore;

import featureMining.feature.Feature;
import featureMining.feature.FeatureContainer;
import featureMining.feature.FeatureOccurrence;
import gate.AnnotationSet;
import gate.Corpus;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.persist.PersistenceException;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;


/**
 * A static Helper class needed for Saving to Gate Datastore.
 */
public final class DataStoreHelper {

	
	/**
	 * Checks if a Folder exists and deletes all its contents, because a Datastore
	 * can only be saved in an empty Directory.
	 *
	 * @param folder the dir
	 */
	public static void checkForExistingDir(File folder) {
		if(folder.exists()){
			try {
				FileUtils.cleanDirectory(folder);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try{
				if(!folder.mkdirs()){
					throw new Exception("Couldn´t create Folder...");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Saves a Corpus to a Datastore
	 *
	 * @param dataStore the data store
	 * @param corpus the corpus
	 */
	public static void persistToDataStore(DataStore dataStore, Corpus corpus){
		try {
			dataStore.open();
			Corpus persistCorpus = (Corpus) dataStore.adopt(corpus);
			dataStore.sync(persistCorpus);
			dataStore.close();
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Annotates a Corpus with all found Features.
	 * Has to be called after the User finished editing the Feature Set.
	 *
	 * @param corpus the corpus
	 * @param featureContainer the feature container
	 */
	public static void prepareCorpusForDataStore(Corpus corpus, FeatureContainer featureContainer) {
		for(Document doc : corpus){
			AnnotationSet resultSet = doc.getAnnotations("FeatureMiningResult");
			AnnotationSet tokenSet = doc.getAnnotations().get("Token");
			AnnotationSet sentenceSet = doc.getAnnotations().get("Sentence");
			AnnotationSet headingSet = doc.getAnnotations("Original markups").get("heading");
			AnnotationSet contentSet = doc.getAnnotations("Original markups").get("content");
			resultSet.addAll(tokenSet);
			resultSet.addAll(sentenceSet);
			resultSet.addAll(headingSet);
			resultSet.addAll(contentSet);
			
			List<Feature> features = new ArrayList<Feature>(featureContainer.getFeatureStorage().values());
			
			for(Feature feature : features){
				for(FeatureOccurrence featOcc : feature.getFeatureOccurrences()){
					if(featOcc.getDocumentName().equals(doc.getName())){
						long start = featOcc.getStartOffset();
						long end = featOcc.getEndOffset();
						FeatureMap featureMap = Factory.newFeatureMap();
						featureMap.put("Name", feature.getLabel());
						featureMap.put("hierarchy", featOcc.getHierarchy());
						featureMap.put("Description", featureContainer.getDescriptionText(feature.getLabel()));
						featureMap.put("FeatureStem", feature.getFeatureStem());
						try {
							resultSet.add(start, end, "Feature", featureMap);
						} catch (InvalidOffsetException e) {
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
}
