package featureMining.ui.listener;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import featureMining.controller.FeatureMining;
import featureMining.ui.RootFeatureWindow;

public class GuiListListener implements ListSelectionListener{
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow(); 
		JList<String> list = (JList<String>) e.getSource();
		if(list.getName().equals("featureList")){
			if(list.getSelectedIndex() != -1){
				rootWindow.updateEditBoxContent(list.getSelectedValue());
				rootWindow.getInfoTextArea().setText(rootWindow.getFeatureContainer().getInfoText((String) list.getSelectedValue()));
				rootWindow.getDescTextArea().setText(rootWindow.getFeatureContainer().getDescriptionText((String) list.getSelectedValue()));
			}
		}else if(list.getName().equals("labelChoosingList")){
			rootWindow.getNewNameField().setText(list.getSelectedValue());
		}
	}
}
