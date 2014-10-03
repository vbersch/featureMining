package featureMining.ui;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;
import featureMining.main.FeatureMining;

public class UiWorker extends SwingWorker<FeatureContainer, String> {

	private OptionTransferObject optionsTO;
	private JTextArea infoTextArea;
	private FeatureContainer container;
	
	public UiWorker(JTextArea infoTextArea, OptionTransferObject optionsTO) {
		this.optionsTO = optionsTO;
		this.infoTextArea = infoTextArea;
		this.container = null;	
	}
	
	@Override
	protected FeatureContainer doInBackground() throws Exception {
		this.container = FeatureMining.getSingleton().doProcessing(optionsTO);
		return container;
	}
	
	@Override
	protected void done(){
		if(this.container != null){
			FeatureMining.getSingleton().getRootWindow().setContent(container);
		}
	}

}
