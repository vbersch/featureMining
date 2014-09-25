package featureMining.ui;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import featureMining.FeatureMining;
import featureMining.processing.feature.FeatureContainer;

public class UiWorker extends SwingWorker<FeatureContainer, String> {

	private String url;
	private JTextArea infoTextArea;
	private FeatureContainer container;
	
	@Override
	protected FeatureContainer doInBackground() throws Exception {
		FeatureMining.getSingleton().getDocumentProcessor()
		.createCorpus(url);
		this.container = FeatureMining.getSingleton().getDocumentProcessor().processCorpus();
		return container;
	}

	public UiWorker(String url, JTextArea infoTextArea) {
		this.url = url;
		this.infoTextArea = infoTextArea;
		this.container = null;
	}

	@Override
	protected void process(final List<String> chunks) {
		// Updates the messages text area
		for (final String string : chunks) {
			infoTextArea.append(string);
			infoTextArea.append("\n");
		}
	}
	
	@Override
	protected void done(){
		if(this.container != null){
			FeatureMining.getSingleton().getRootWindow().setContent(container);
		}
	}

}
