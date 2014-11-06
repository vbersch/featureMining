package featureMining.ui;

import javax.swing.SwingWorker;
import featureMining.controller.FeatureMining;
import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;

/**
 * The Class UiWorker.
 * Is needed to perform the Feature Mining without
 * freezing the GUI Window.
 */
public class UiWorker extends SwingWorker<FeatureContainer, String> {

	/** The options to. */
	private OptionTransferObject optionsTO;
	
	/** The container. */
	private FeatureContainer container;
	
	/**
	 * Constructor.
	 *
	 * @param infoTextArea the info text area
	 * @param optionsTO the options to
	 */
	public UiWorker(OptionTransferObject optionsTO) {
		this.optionsTO = optionsTO;
		this.container = null;	
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#doInBackground()
	 * Calls the doProcessing Method where the Feature
	 * Mining takes place.
	 */
	@Override
	protected FeatureContainer doInBackground() throws Exception {
		this.container = FeatureMining.getSingleton().doProcessing(optionsTO);
		return container;
	}
	
	/* (non-Javadoc)
	 * @see javax.swing.SwingWorker#done()
	 * is called when the Feature Mining is finished.
	 */
	@Override
	protected void done(){
		if(this.container != null){
			FeatureMining.getSingleton().getRootWindow().setContent(container);
		}
	}

}
