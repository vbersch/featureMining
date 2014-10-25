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

import featureMining.feature.OptionTransferObject;

public class SettingsManager {

	private static final String fileName = System.getProperty("user.dir")+ "/settings.xml";
	private static OptionTransferObject options;
	
	public static OptionTransferObject restoreOptions(){
		File settingsFile = new File(fileName);
		if(settingsFile.exists()){
			try {
	            JAXBContext context = JAXBContext.newInstance(OptionTransferObject.class);
	            Unmarshaller un = context.createUnmarshaller();
	            options = (OptionTransferObject) un.unmarshal(settingsFile);
	            return options;
	        } catch (JAXBException e) {
	            System.out.println("Couldn´t load settings...\n Using default settings...");
	            return null;
	        }
		}
        return null;
	}
	
	public static void saveOptions(){
		try {
            JAXBContext context = JAXBContext.newInstance(OptionTransferObject.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);

            marshaller.marshal(options, new File(fileName));
        } catch (JAXBException e) {
            e.printStackTrace();
            System.out.println("Couldn´t save Settings...");
        }
	}
	
	public static OptionTransferObject getOptions(){
		if(options == null){
			options = new OptionTransferObject();
		}
		return options;
	}

	public static void loadBlacklists() {
		if(!options.getFeatureBlacklistPath().equals("")){
			options.setFeatureBlacklist(loadFile(options.getFeatureBlacklistPath()));
		}
		
		if(!options.getSentenceBlacklistPath().equals("")){
			options.setSentenceBlacklist(loadFile(options.getSentenceBlacklistPath()));
		}
	}

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
