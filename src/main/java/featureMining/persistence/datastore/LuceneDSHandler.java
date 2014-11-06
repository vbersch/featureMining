package featureMining.persistence.datastore;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import featureMining.controller.FeatureMining;
import featureMining.feature.FeatureContainer;
import featureMining.persistence.IPersistenceHandler;
import gate.Factory;
import gate.creole.annic.Constants;
import gate.creole.annic.IndexException;
import gate.creole.annic.Indexer;
import gate.creole.annic.SearchException;
import gate.creole.annic.lucene.LuceneIndexer;
import gate.creole.annic.lucene.LuceneSearcher;
import gate.persist.LuceneDataStoreImpl;
import gate.persist.PersistenceException;
 
/**
 * The Class LuceneDSHandler.
 * Handles saving a Corpus to a Lucene enabled Datastore.
 */
public class LuceneDSHandler implements IPersistenceHandler{

	/** singleton instance. */
	private static LuceneDSHandler instance = null;
	
	/**
	 * Instantiates a new lucene ds handler.
	 */
	private LuceneDSHandler(){}
	
	/* (non-Javadoc)
	 * @see featureMining.persistence.IPersistenceHandler#load(java.lang.String)
	 * Uses the same load logic as the SerialDatastoreHandler.
	 */
	@Override
	public void load(String path) {
		SerialDSHandler.getSingleton().load(path);
	}

	/* (non-Javadoc)
	 * @see featureMining.persistence.IPersistenceHandler#persist(java.lang.String, featureMining.feature.FeatureContainer)
	 * Creates the Lucene Indexer and persists a FeatureContainer to Datastore.
	 * For now, the Name is always newStore and the Datastores can´t be created in the same Directory.
	 */
	@Override
	public void persist(String path, FeatureContainer featureContainer) {
		String name = "/newStore";
		File dir = new File(path+name);
		DataStoreHelper.checkForExistingDir(dir);
		
		File indexDir;
		try {
			LuceneDataStoreImpl luceneDataStore = (LuceneDataStoreImpl)Factory.createDataStore
					("gate.persist.LuceneDataStoreImpl", dir.toURI().toURL().toString());
			
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nOpening Lucene DataStore");
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nStarting to index Files...");
			
			DataStoreHelper.prepareCorpusForDataStore(FeatureMining.getSingleton().getCorpus(), featureContainer);
			
			String indexPath = dir.getPath() + "-index";
			indexDir = new File(indexPath);
			DataStoreHelper.checkForExistingDir(indexDir); 
			Indexer indexer = new LuceneIndexer(indexDir.toURI().toURL());
			Map<String, Object> parameters = new HashMap<String, Object>(); 
			parameters.put(Constants.INDEX_LOCATION_URL, indexDir.toURI().toURL()); 
			parameters.put(Constants.BASE_TOKEN_ANNOTATION_TYPE, "Token"); 
			parameters.put(Constants.CREATE_TOKENS_AUTOMATICALLY, new Boolean(true)); 
			parameters.put(Constants.INDEX_UNIT_ANNOTATION_TYPE, "Sentence"); 
			List<String> setsToInclude = new ArrayList<String>(); 
			setsToInclude.add("Key"); 
			setsToInclude.add("<null>"); 
			setsToInclude.add("Original markups");
			setsToInclude.add("FeatureMiningResult");
			
			parameters.put(Constants.ANNOTATION_SETS_NAMES_TO_INCLUDE, setsToInclude); 
			parameters.put(Constants.ANNOTATION_SETS_NAMES_TO_EXCLUDE, new ArrayList<String>()); 
			 
			parameters.put(Constants.FEATURES_TO_INCLUDE, new ArrayList<String>()); 
			parameters.put(Constants.FEATURES_TO_EXCLUDE, new ArrayList<String>()); 
			  
			luceneDataStore.setIndexer(indexer, parameters); 
			luceneDataStore.setSearcher(new LuceneSearcher());
		
			DataStoreHelper.persistToDataStore(luceneDataStore, FeatureMining.getSingleton().getCorpus());
			
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nLucene DataStore created");
			
		}catch (IOException | PersistenceException | UnsupportedOperationException | IndexException | SearchException e1) {
			e1.printStackTrace();
		}
	}
	
	/**
	 * LuceneDSHandler obeys the Singleton Pattern.
	 *
	 * @return the singleton
	 */
	public static LuceneDSHandler getSingleton(){
		if(instance == null){
			instance = new LuceneDSHandler();
		}
		return instance;
	}

}
