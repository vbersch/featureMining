package featureMining.ui.listener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.filechooser.FileFilter;

import featureMining.controller.FeatureMining;
import featureMining.feature.OptionTransferObject;
import featureMining.persistence.SettingsManager;
import featureMining.persistence.datastore.LuceneDSHandler;
import featureMining.persistence.datastore.SerialDSHandler;
import featureMining.persistence.xml.XmlHandler;
import featureMining.ui.OptionWindow;
import featureMining.ui.RootFeatureWindow;
import featureMining.ui.UiWorker;

// TODO: Auto-generated Javadoc
/**
 * The listener interface for receiving gui events.
 * The class that is interested in processing a gui
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addGuiListener<code> method. When
 * the gui event occurs, that object's appropriate
 * method is invoked.
 *
 * @see GuiEvent
 */
public class GuiActionListener implements ActionListener{
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void actionPerformed(ActionEvent e) {
		RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow(); 
		if(e.getSource() instanceof JMenuItem){
			handleMenuEntry(e , rootWindow);
		}else if(e.getSource() instanceof JButton){
			handleButtonClick(e, rootWindow);
		}
	}

	private void handleButtonClick(ActionEvent e, RootFeatureWindow rootWindow) {
		JButton button = (JButton)e.getSource();
		JList<String> list = rootWindow.getFeatureList();
		if(button.getName() == "deleteButton"){ // delete Feature
			if(list.getSelectedIndex() != -1){
				rootWindow.getInfoTextArea().setText("Feature " + (String) list.getSelectedValue() + " successfully deleted");
				rootWindow.getDescTextArea().setText("");
				rootWindow.getFeatureContainer().deleteFeature((String) list.getSelectedValue());
				rootWindow.updateFeatureList();
				rootWindow.getEditBox().setVisible(false);
				rootWindow.updateEvalTextArea();
			}
		}else if(button.getName() == "changeButton"){
			if(list.getSelectedIndex() != -1){
				String oldName = (String) list.getSelectedValue();
				String newName = rootWindow.getNewNameField().getText();
				Object[] options = {"Accept", "Cancel"};
				int n = JOptionPane.showOptionDialog(rootWindow,
					    "Change " + oldName + " to " + newName + "?",
					    "Rename Feature",
					    JOptionPane.YES_NO_OPTION,
					    JOptionPane.QUESTION_MESSAGE,
					    null,
					    options,
					    options[1]);
				if(n == 0){
					rootWindow.getNewNameField().setText("Enter new Name");
					rootWindow.getInfoTextArea().setText("Feature " + oldName + " successfully changed to " + newName);
					rootWindow.getFeatureContainer().changeFeature(oldName , newName);
					rootWindow.updateFeatureList();
					rootWindow.updateEditBoxContent(newName);
				}
			}
		}else if(button.getName().equals("cancelOptions")){
			rootWindow.getOptionFrame().dispose();
		}else if(button.getName().equals("featureMining")){
			OptionWindow optionWindow = rootWindow.getOptionFrame();
			OptionTransferObject optionsTO = SettingsManager.getOptions();
			optionsTO.setBaseUrl(optionWindow.getBaseUrl());
			optionsTO.setDocumentationType(optionWindow.getDocumentationType());
			optionsTO.setEnableStemming(optionWindow.getEnableStemming());
			optionsTO.setDomainSpecific(optionWindow.getDomainOptions());
			optionsTO.setHostName(optionWindow.getHostName());
			optionsTO.setPreprocessingName(optionWindow.getPreprocessor());
			optionsTO.setThreadNum(optionWindow.getThreadNum());
			optionsTO.setFeatureBlacklistPath(optionWindow.getFeatureBlacklistPath());
			optionsTO.setSentenceBlacklistPath(optionWindow.getSentenceBlacklistPath());
			SettingsManager.loadBlacklists();
			SettingsManager.saveOptions();
			
			UiWorker uiWorker = new UiWorker(rootWindow.getInfoTextArea(), optionsTO);
			uiWorker.execute();
			optionWindow.dispose();
		}
		
	}

	private void handleMenuEntry(ActionEvent e, RootFeatureWindow rootWindow) {
		JMenuItem item = (JMenuItem)e.getSource();
		if(item.getName().equals("exit")){
			System.exit(0);
		}else if(item.getName().equals("exportXml")){
			rootWindow.setPersistenceHandler(XmlHandler.getSingleton());
			if(rootWindow.getFeatureContainer() != null){
				String path = this.showFileDialog("xml" , "export");
				if(path != null){
					rootWindow.getPersistenceHandler().persist(path, rootWindow.getFeatureContainer());
				}
			}
		}else if(item.getName().equals("exportLucene")){
			rootWindow.setPersistenceHandler(LuceneDSHandler.getSingleton());
			if(rootWindow.getFeatureContainer() != null){
				String path = this.showFileDialog("luceneDataStores", "export");
				if(path != null){
					rootWindow.getPersistenceHandler().persist(path, rootWindow.getFeatureContainer());
				}
			}
		}else if(item.getName().equals("exportSerial")){
			rootWindow.setPersistenceHandler(SerialDSHandler.getSingleton());
			if(rootWindow.getFeatureContainer() != null){
				String path = this.showFileDialog("serialDataStores", "export");
				if(path != null){
					rootWindow.getPersistenceHandler().persist(path, rootWindow.getFeatureContainer());
				}
			}
		}else if(item.getName().equals("importXml") ){
			rootWindow.setPersistenceHandler(XmlHandler.getSingleton());
			String path = this.showFileDialog("xml" , "import");
			if(path != null){
				if(path.endsWith(".xml")){
					rootWindow.getPersistenceHandler().load(path);
				}else{
					rootWindow.addInfoTextLine("\nThats not an xml File!");
				}
			}
		}else if(item.getName().equals("importSerial")){
			rootWindow.setPersistenceHandler(SerialDSHandler.getSingleton());
			String path = this.showFileDialog("serialDataStores", "import");
			if(path != null){
				rootWindow.getPersistenceHandler().load(path);
			}
		}else if(item.getName().equals("importLucene")){
			rootWindow.setPersistenceHandler(SerialDSHandler.getSingleton());
			String path = this.showFileDialog("luceneDataStores", "import");
			if(path != null){
				rootWindow.getPersistenceHandler().load(path);
			}
		}
		
	}

	private String showFileDialog(String dir, String mode) {
		RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow(); 
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/" + dir));
		fc.setApproveButtonText("Choose"); 
		if(dir.equals("luceneDataStores") || dir.equals("serialDataStores")){
			fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		}else if(dir.equals("xml")){
			fc.setFileFilter(new FileFilter(){
				@Override
				public boolean accept(File f) {
					if (f.isDirectory()) {
				        return true;
				    }
					String ext = null;
			        String s = f.getName();
			        int i = s.lastIndexOf('.');
	
			        if (i > 0 &&  i < s.length() - 1) {
			            ext = s.substring(i+1).toLowerCase();
			        }
			        if(ext != null){
				        if(ext.equals("xml")){
				        	return true;
				        }
			        }
					
					return false;
				}
				@Override
				public String getDescription() {
					return "*.xml";
				}
			});
		}
		int ret = fc.showOpenDialog(null);
		if(ret == JFileChooser.APPROVE_OPTION){
			return fc.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

}
