package featureMining.persistence.datastore;

import java.io.File;
import java.net.MalformedURLException;

import featureMining.controller.FeatureMining;
import featureMining.feature.FeatureContainer;
import featureMining.persistence.IPersistenceHandler;
import featureMining.ui.RootFeatureWindow;
import gate.Corpus;
import gate.DataStore;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;

/**
 * The Class SerialDSHandler.
 * Can load and save a FeatureContainer to a Gate Serial Datastore.
 */
public class SerialDSHandler implements IPersistenceHandler{
	
	/** singleton instance. */
	private static SerialDSHandler instance = null;
	
	/** The root window. */
	private RootFeatureWindow rootWindow;
	
	/* (non-Javadoc)
	 * @see featureMining.persistence.IPersistenceHandler#load(java.lang.String)
	 * Opens the Serial Datastore under the specified path and restores its contained FeatureContainer.
	 */
	@Override
	public void load(String path) {
		rootWindow = FeatureMining.getSingleton().getRootWindow();
		File dir = new File(path);
		try {
			rootWindow.addInfoTextLine("\nOpening Datastore...");
			SerialDataStore serialDataStore  = new SerialDataStore(dir.toURI().toURL().toString()); 
			serialDataStore.open();
			rootWindow.addInfoTextLine("done");
			rootWindow.addInfoTextLine("\nLoading Corpus...");
			FeatureMap params = Factory.newFeatureMap();
			params.put(DataStore.DATASTORE_FEATURE_NAME, serialDataStore);
			params.put(DataStore.LR_ID_FEATURE_NAME, serialDataStore.getLrIds("gate.corpora.SerialCorpusImpl").get(0));
			Corpus corpus = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", params);
			serialDataStore.close();
			rootWindow.addInfoTextLine("done");
			
			FeatureMining.getSingleton().setCorpus(corpus);
			
			FeatureContainer featureContainer = (FeatureContainer) corpus.getFeatures().get("result");
			rootWindow.setContent(featureContainer);
			
		} catch (PersistenceException | UnsupportedOperationException | MalformedURLException | ResourceInstantiationException e) {
			rootWindow.addInfoTextLine("\nOpening Datastore failed...");
			e.printStackTrace();
		}
	}

	/* (non-Javadoc)
	 * Saves a FeatureContainer to a Serial Datastore.
	 * @see featureMining.persistence.IPersistenceHandler#persist(java.lang.String, featureMining.feature.FeatureContainer)
	 */
	@Override
	public void persist(String path, FeatureContainer featureContainer) {
		String name = "/newStore";
		File dir = new File(path+name);
		DataStoreHelper.checkForExistingDir(dir);
		
		try {
			SerialDataStore serialDataStore  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore" , dir.toURI().toURL().toString());
			
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nOpening Serial DataStore");
			
			DataStoreHelper.prepareCorpusForDataStore(FeatureMining.getSingleton().getCorpus(), featureContainer);
			DataStoreHelper.persistToDataStore(serialDataStore, FeatureMining.getSingleton().getCorpus());
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nCorpus " + FeatureMining.getSingleton().getCorpus().getName() + " saved in DataStore");
		} catch (PersistenceException | UnsupportedOperationException | MalformedURLException e) {
			e.printStackTrace();
		}	
	}
	
	/**
	 * Instantiates a new serial ds handler.
	 */
	private SerialDSHandler(){
		
	}
	
	/**
	 * SerialDSHandler obeys the Singleton Pattern.
	 *
	 * @return the singleton
	 */
	public static SerialDSHandler getSingleton(){
		
		if(instance == null){
			instance = new SerialDSHandler();
		}
		return instance;
	}

}
