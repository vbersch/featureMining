package featureMining.persistence.datastore;

import featureMining.controller.FeatureMining;
import featureMining.feature.Feature;
import featureMining.feature.FeatureContainer;
import featureMining.feature.FeatureOccurrence;
import gate.AnnotationSet;
import gate.Corpus;
import gate.DataStore;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.util.InvalidOffsetException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;

public final class DataStoreHelper {
	
	private DataStoreHelper(){
		
	}
	
	public static void checkForExistingDir(File dir) {
		if(dir.exists()){
			try {
				FileUtils.cleanDirectory(dir);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}else{
			try{
				if(!dir.mkdirs()){
					throw new Exception("Couldn´t create Folder...");
				}
			}catch(Exception e){
				e.printStackTrace();
			}
		}
	}
	
	public static void persistToDataStore(DataStore dataStore, Corpus corpus){
		try {
			dataStore.open();
//			if(corpus.getDataStore() != null){
//				corpus.getDataStore().
//			}
//			Corpus newCorpus =  gate.Factory.newCorpus(corpus.getName()+"0");
//			//copy old corpus
//			newCorpus.addAll(corpus);
//			newCorpus.getFeatures().put("result", corpus.getFeatures().get("result"));
//			corpus = null;
//			FeatureMining.getSingleton().setCorpus(newCorpus);
			Corpus persistCorpus = (Corpus) dataStore.adopt(corpus);
			dataStore.sync(persistCorpus);
			dataStore.close();
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
	}

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
						featureMap.put("Description", featureContainer.getDescriptionText(feature.getLabel()));
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
