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

import featureMining.feature.OptionTransferObject;
import featureMining.main.FeatureMining;
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
public class GuiListener implements ActionListener, ListSelectionListener, ItemListener{
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@SuppressWarnings("rawtypes")
	@Override
	public void actionPerformed(ActionEvent e) {
		RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow(); 
		if(e.getSource() instanceof JMenuItem){
			JMenuItem item = (JMenuItem)e.getSource();
			if(item.getName().equals("exit")){
				System.exit(0);
			}else if(item.getName().equals("exportXml")){
				rootWindow.setPersistenceHandler(XmlHandler.getSingleton());
				if(rootWindow.getFeatureContainer() != null){
					String path = this.showFileDialog();
					if(path != null){
						rootWindow.getPersistenceHandler().persist(path, rootWindow.getFeatureContainer());
					}
				}
			}else if(item.getName().equals("importXml")){
				rootWindow.setPersistenceHandler(XmlHandler.getSingleton());
				String path = this.showFileDialog();
				if(path != null){
					if(path.endsWith(".xml")){
						rootWindow.getPersistenceHandler().load(path);
					}else{
						rootWindow.addInfoTextLine("\nThats not an xml File!");
					}
				}
			}
		}else if(e.getSource() instanceof JButton){
			JButton button = (JButton)e.getSource();
			JList list = rootWindow.getFeatureList();
			if(button.getName() == "deleteButton"){ // delete Feature
				if(list.getSelectedIndex() != -1){
					rootWindow.getInfoTextArea().setText("Feature " + (String) list.getSelectedValue() + " successfully deleted");
					rootWindow.getDescTextArea().setText("");
					rootWindow.getFeatureContainer().deleteFeature((String) list.getSelectedValue());
					rootWindow.updateFeatureList();
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
						rootWindow.getInfoTextArea().setText("Feature " + oldName + " successfully change to " + newName);
						rootWindow.getFeatureContainer().changeFeature(oldName , newName);
						rootWindow.updateFeatureList();
					}
				}
			}else if(button.getName() == "cancelOptions"){
				rootWindow.getOptionFrame().dispose();
			}else if(button.getName() == "featureMining"){
				OptionWindow optionWindow = rootWindow.getOptionFrame();
				OptionTransferObject optionsTO = new OptionTransferObject(optionWindow.getBaseUrl(), optionWindow.getHostName(),
						optionWindow.getPreprocessor(), optionWindow.getThreadNum(), optionWindow.getDocumentationType());
				optionsTO.setDomainSpecific(optionWindow.getDomainOptions());
				UiWorker uiWorker = new UiWorker(rootWindow.getInfoTextArea(), optionsTO);
				uiWorker.execute();
				optionWindow.dispose();
			}
		}
	}

	private String showFileDialog() {
		RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow(); 
		final JFileChooser fc = new JFileChooser();
		fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/xml"));
		fc.setApproveButtonText("Choose"); 
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
		int ret = fc.showOpenDialog(null);
		if(ret == JFileChooser.APPROVE_OPTION){
			return fc.getSelectedFile().getAbsolutePath();
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow(); 
		JList list = rootWindow.getFeatureList();
		if(list.getSelectedIndex() != -1){
			rootWindow.getEditBox().setVisible(true);
			rootWindow.getInfoTextArea().setText(rootWindow.getFeatureContainer().getInfoText((String) list.getSelectedValue()));
			rootWindow.getDescTextArea().setText(rootWindow.getFeatureContainer().getDescriptionText((String) list.getSelectedValue()));
		}
	}

	@Override
	public void itemStateChanged(ItemEvent evt) {
		JComboBox comboBox = (JComboBox)evt.getSource();
		if(comboBox.getName().equals("preprocessorOptions")){
			RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow();
			if (evt.getStateChange() == ItemEvent.SELECTED){
				if((String)comboBox.getSelectedItem() == "Html"){
					rootWindow.getOptionFrame().addHtmlOptions();
				}else{
					rootWindow.getOptionFrame().removeHtmlOptions();
				}
			}
		}
	}

}
