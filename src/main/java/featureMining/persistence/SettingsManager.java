package featureMining.persistence;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import featureMining.controller.FeatureMining;
import featureMining.feature.OptionTransferObject;

/**
 * The Class SettingsManager.
 * The SettingsManager loads and saves the Settings of 
 * the last Feature Mining Run.
 */
public class SettingsManager {

	/** the logger */
	private static Logger logger = LoggerFactory.getLogger(FeatureMining.class);
	
	/** The File Path, where the Settings are saved in. */
	private static final String fileName = System.getProperty("user.dir")+ "/settings.xml";
	
	/** The options of the OptionWindow. */
	private static OptionTransferObject options;
	
	/**
	 * Loads the Options from an xml File and restores a OptionTransferObject out 
	 * of it.
	 *
	 * @return the option transfer object
	 */
	public static OptionTransferObject restoreOptions(){
		File settingsFile = new File(fileName);
		if(settingsFile.exists()){
			try {
	            JAXBContext context = JAXBContext.newInstance(OptionTransferObject.class);
	            Unmarshaller un = context.createUnmarshaller();
	            options = (OptionTransferObject) un.unmarshal(settingsFile);
	            return options;
	        } catch (JAXBException e) {
	            logger.warn("Couldn´t load settings...\n Using default settings...");
	            return null;
	        }
		}
        return null;// if no settings File exists, default Settings will be shown
	}
	
	/**
	 * Serialize the OptionTransferObject to a xml File.
	 */
	public static void saveOptions(){
		File settingsFile = new File(fileName);
		if(settingsFile.exists()){
			settingsFile.delete();
		}
		try {
            JAXBContext context = JAXBContext.newInstance(OptionTransferObject.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(options, settingsFile);
        } catch (JAXBException e) {
            e.printStackTrace();
            logger.warn("Couldn´t save Settings...");
        }
	}
	
	/**
	 * Gets the options.
	 *
	 * @return the options
	 */
	public static OptionTransferObject getOptions(){
		if(options == null){
			options = new OptionTransferObject();
		}
		return options;
	}

	/**
	 * If a path to a Blacklist is specified, the List will be loaded here.
	 */
	public static void loadBlacklists() {
		if(!options.getFeatureBlacklistPath().equals("")){
			options.setFeatureBlacklist(loadFile(options.getFeatureBlacklistPath()));
		}
		
		if(!options.getSentenceBlacklistPath().equals("")){
			options.setSentenceBlacklist(loadFile(options.getSentenceBlacklistPath()));
		}
	}

	/**
	 * Helper Function.
	 * Loads a Text File, puts every Line in an ArrayList and returns it.
	 *
	 * @param path the path
	 * @return the array list
	 */
	private static ArrayList<String> loadFile(String path) {
		ArrayList<String> blacklist = new ArrayList<String>();
		try {
			BufferedReader in = new BufferedReader(new FileReader(path));
			String line = null;
			while ((line = in.readLine()) != null) {
				blacklist.add(line.trim());
			}
			in.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return blacklist;
	}
	
}
