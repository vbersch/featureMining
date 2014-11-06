package featureMining.ui.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import featureMining.controller.FeatureMining;
import featureMining.ui.RootFeatureWindow;

/**
 * The listener interface for receiving guiItem events.
 * The class that is interested in processing a guiItem
 * event implements this interface, and the object created
 * with that class is registered with a component using the
 * component's <code>addGuiItemListener<code> method. When
 * the guiItem event occurs, that object's appropriate
 * method is invoked.
 *
 * @see GuiItemEvent
 */
public class GuiItemListener implements ItemListener{

	/* (non-Javadoc)
	 * @see java.awt.event.ItemListener#itemStateChanged(java.awt.event.ItemEvent)
	 * Is called if the ComboBox with the Preprocessing Options
	 * in the OptionWindow changes its value and removes html specific Options if
	 * Html Preprocessor is not selected.
	 */
	@Override
	public void itemStateChanged(ItemEvent evt) {
		JComboBox<?> comboBox = (JComboBox<?>)evt.getSource();
		if(comboBox.getName().equals("preprocessorOptions")){
			RootFeatureWindow rootWindow = FeatureMining.getSingleton().getRootWindow();
			if (evt.getStateChange() == ItemEvent.SELECTED){
				if(((String)comboBox.getSelectedItem()).equals("Html")){
					rootWindow.getOptionFrame().addHtmlOptions();
				}else{
					rootWindow.getOptionFrame().removeHtmlOptions();
				}
			}
		}
	}
}
