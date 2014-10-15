package featureMining.persistence.datastore;

import java.io.File;
import java.net.MalformedURLException;

import featureMining.feature.FeatureContainer;
import featureMining.main.FeatureMining;
import featureMining.persistence.IPersistenceHandler;
import featureMining.ui.RootFeatureWindow;
import gate.Corpus;
import gate.DataStore;
import gate.Factory;
import gate.FeatureMap;
import gate.LanguageResource;
import gate.creole.ResourceInstantiationException;
import gate.persist.PersistenceException;
import gate.persist.SerialDataStore;

public class SerialDSHandler implements IPersistenceHandler{
	
	private static SerialDSHandler instance = null;
	private RootFeatureWindow rootWindow;
	
	@Override
	public void load(String path) {
		rootWindow = FeatureMining.getSingleton().getRootWindow();
		File dir = new File(path);
		try {
			rootWindow.addInfoTextLine("\nOpening Serial Datastore...");
			SerialDataStore sds  = new SerialDataStore(dir.toURI().toURL().toString()); 
			sds.open();
			rootWindow.addInfoTextLine("done");
			rootWindow.addInfoTextLine("\nLoading Corpus...");
			FeatureMap params = Factory.newFeatureMap();
			params.put(DataStore.DATASTORE_FEATURE_NAME, sds);
			params.put(DataStore.LR_ID_FEATURE_NAME, sds.getLrIds("gate.corpora.SerialCorpusImpl").get(0));
			Corpus lr = (Corpus)Factory.createResource("gate.corpora.SerialCorpusImpl", params);
			sds.close();
			rootWindow.addInfoTextLine("done");
			
			FeatureContainer featureContainer = (FeatureContainer) lr.getFeatures().get("result");
			rootWindow.setContent(featureContainer);
			
		} catch (PersistenceException | UnsupportedOperationException | MalformedURLException | ResourceInstantiationException e) {
			rootWindow.addInfoTextLine("\nOpening Serial Datastore failed...");
			e.printStackTrace();
		}
		
	}

	@Override
	public void persist(String path, FeatureContainer featureContainer) {
		String name = "/newStore";
		File dir = new File(path+name);
		DataStoreHelper.checkForExistingDir(dir);
		
		try {
			SerialDataStore serialDataStore  = (SerialDataStore)Factory.createDataStore("gate.persist.SerialDataStore" , dir.toURI().toURL().toString());
			
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nOpening Serial DataStore");

			DataStoreHelper.persistToDataStore(serialDataStore, FeatureMining.getSingleton().getCorpus());
			FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nCorpus " + FeatureMining.getSingleton().getCorpus().getName() + " saved in DataStore");
		} catch (PersistenceException | UnsupportedOperationException | MalformedURLException e) {
			e.printStackTrace();
		}	
	}
	
	private SerialDSHandler(){
		
	}
	
	public static SerialDSHandler getSingleton(){
		
		if(instance == null){
			instance = new SerialDSHandler();
		}
		return instance;
	}

}
