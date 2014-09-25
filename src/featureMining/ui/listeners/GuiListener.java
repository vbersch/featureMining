package featureMining.ui.listeners;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JList;
import javax.swing.JMenuItem;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import featureMining.FeatureMining;
import featureMining.ui.RootFeatureWindow;

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
public class GuiListener implements ActionListener, ListSelectionListener{
	
	/* (non-Javadoc)
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow(); 
		if(e.getSource() instanceof JMenuItem){
			JMenuItem item = (JMenuItem)e.getSource();
			if(item.getName().equals("exit")){
				System.exit(0);
			}
		}else if(e.getSource() instanceof JButton){ //delete an item
			JList list = rootWindow.getFeatureList();
			if(list.getSelectedIndex() != -1){
				rootWindow.getInfoTextArea().setText("Feature " + (String) list.getSelectedValue() + " successfully deleted");
				rootWindow.getDescTextArea().setText("");
				rootWindow.getFeatureContainer().deleteFeature((String) list.getSelectedValue());
				rootWindow.updateFeatureList();
			}
		}
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

}
