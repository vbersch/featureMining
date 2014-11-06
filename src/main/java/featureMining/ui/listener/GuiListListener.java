package featureMining.ui.listener;

import javax.swing.JList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import featureMining.controller.FeatureMining;
import featureMining.ui.RootFeatureWindow;

/**
 * The listener interface for receiving guiList events.
 * The class that is interested in processing a guiList
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addGuiListListener<code> method. When
 * the guiList event occurs, that object's appropriate
 * method is invoked.
 *
 * @see GuiListEvent
 */
public class GuiListListener implements ListSelectionListener{
	/* (non-Javadoc)
	 * @see javax.swing.event.ListSelectionListener#valueChanged(javax.swing.event.ListSelectionEvent)
	 * Is called if a List in the UI changes its Value.
	 */
	@Override
	public void valueChanged(ListSelectionEvent e) {
		RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow(); 
		JList<?> list = (JList<?>) e.getSource();
		if(list.getName().equals("featureList")){
			if(list.getSelectedIndex() != -1){
				rootWindow.updateEditBoxContent((String)list.getSelectedValue());
				rootWindow.getInfoTextArea().setText(rootWindow.getFeatureContainer().getInfoText((String) list.getSelectedValue()));
				rootWindow.getDescTextArea().setText(rootWindow.getFeatureContainer().getDescriptionText((String) list.getSelectedValue()));
			}
		}else if(list.getName().equals("labelChoosingList")){
			rootWindow.getNewNameField().setText((String)list.getSelectedValue());
		}
	}
}
