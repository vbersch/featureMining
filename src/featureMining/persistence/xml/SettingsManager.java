package featureMining.persistence.xml;

import java.io.File;

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
	
}
