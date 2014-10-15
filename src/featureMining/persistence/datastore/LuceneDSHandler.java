package featureMining.persistence.datastore;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;

import featureMining.feature.FeatureContainer;
import featureMining.main.FeatureMining;
import featureMining.persistence.IPersistenceHandler;
import gate.Corpus;
import gate.Factory;
import gate.creole.annic.Constants;
import gate.creole.annic.IndexException;
import gate.creole.annic.Indexer;
import gate.creole.annic.SearchException;
import gate.creole.annic.lucene.LuceneIndexer;
import gate.creole.annic.lucene.LuceneSearcher;
import gate.persist.LuceneDataStoreImpl;
import gate.persist.PersistenceException;

public class LuceneDSHandler implements IPersistenceHandler{

	private static LuceneDSHandler instance = null;
	
	private LuceneDSHandler(){}
	
	@Override
	public void load(String path) {
	
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void persist(String path, FeatureContainer featureContainer) {
		String name = "/newStore";
		File dir = new File(path+name);
		DataStoreHelper.checkForExistingDir(dir);
		
		File indexDir;
		try {
			LuceneDataStoreImpl luceneDataStore = (LuceneDataStoreImpl)Factory.createDataStore
					("gate.persist.LuceneDataStoreImpl", dir.toURI().toURL().toString());
			
			String indexPath = dir.getPath() + "-index";
			indexDir = new File(indexPath);
			DataStoreHelper.checkForExistingDir(indexDir); 
			Indexer indexer = new LuceneIndexer(indexDir.toURI().toURL());
			Map parameters = new HashMap(); 
			parameters.put(Constants.INDEX_LOCATION_URL, indexDir.toURI().toURL()); 
			parameters.put(Constants.BASE_TOKEN_ANNOTATION_TYPE, "Token"); 
			parameters.put(Constants.CREATE_TOKENS_AUTOMATICALLY, new Boolean(true)); 
			parameters.put(Constants.INDEX_UNIT_ANNOTATION_TYPE, "Sentence"); 
			List<String> setsToInclude = new ArrayList<String>(); 
			setsToInclude.add("Key"); 
			setsToInclude.add("<null>"); 
			setsToInclude.add("Original Markups");  
			
			parameters.put(Constants.ANNOTATION_SETS_NAMES_TO_INCLUDE, setsToInclude); 
			parameters.put(Constants.ANNOTATION_SETS_NAMES_TO_EXCLUDE, new ArrayList<String>()); 
			 
			parameters.put(Constants.FEATURES_TO_INCLUDE, new ArrayList<String>()); 
			parameters.put(Constants.FEATURES_TO_EXCLUDE, new ArrayList<String>()); 
			  
			luceneDataStore.setIndexer(indexer, parameters); 
			luceneDataStore.setSearcher(new LuceneSearcher());
			
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nOpening Lucene DataStore");
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nStarting to index Files...");

			DataStoreHelper.persistToDataStore(luceneDataStore, FeatureMining.getSingleton().getCorpus());
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nLucene DataStore created");
			
		}catch (IOException | PersistenceException | UnsupportedOperationException | IndexException | SearchException e1) {
			e1.printStackTrace();
		}
	}
	
	public static LuceneDSHandler getSingleton(){
		if(instance == null){
			instance = new LuceneDSHandler();
		}
		return instance;
	}

}
