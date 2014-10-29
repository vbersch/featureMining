package featureMining.ui.listener;

import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JComboBox;

import featureMining.controller.FeatureMining;
import featureMining.ui.RootFeatureWindow;

public class GuiItemListener implements ItemListener{

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
