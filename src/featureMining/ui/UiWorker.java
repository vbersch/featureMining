package featureMining.ui;

import java.util.List;

import javax.swing.JTextArea;
import javax.swing.SwingWorker;

import featureMining.feature.FeatureContainer;
import featureMining.main.FeatureMining;

public class UiWorker extends SwingWorker<FeatureContainer, String> {

	private String url;
	private String hostName;
	private JTextArea infoTextArea;
	private FeatureContainer container;
	
	@Override
	protected FeatureContainer doInBackground() throws Exception {
		this.container = FeatureMining.getSingleton().doProcessing(url, hostName);
//		FeatureMining.getSingleton().getDocumentProcessor()
//		.createCorpus(url);
//		this.container = FeatureMining.getSingleton().getDocumentProcessor().processCorpus();
		return container;
	}

	public UiWorker(String url, JTextArea infoTextArea, String hostName) {
		this.url = url;
		this.infoTextArea = infoTextArea;
		this.container = null;
		this.hostName = hostName;
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
