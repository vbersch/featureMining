package featureMining.persistence.datastore;

import gate.Corpus;
import gate.DataStore;
import gate.persist.PersistenceException;

import java.io.File;
import java.io.IOException;

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
			Corpus persistCorpus = (Corpus) dataStore.adopt(corpus);
			dataStore.sync(persistCorpus);
			dataStore.close();
		} catch (PersistenceException e) {
			e.printStackTrace();
		}
		
	}
	
}
