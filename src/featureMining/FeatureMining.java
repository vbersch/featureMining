package featureMining;

import javax.swing.SwingUtilities;

import featureMining.processing.HtmlProcessor;
import featureMining.processing.SimpleProcessor;
import featureMining.ui.RootFeatureWindow;
import gate.Gate;
import gate.util.GateException;

// TODO: Auto-generated Javadoc
//main class
/**
 * The Class FeatureMining.
 */
public class FeatureMining {//Singleton
	
	/** The max threads. */
public static int maxThreads = 4;
	
	/** The document processor. */
	private SimpleProcessor documentProcessor;
	
	/** The root window. */
	private RootFeatureWindow rootWindow;
	
	/** The instance. */
	private static FeatureMining instance = null;
	
	/**
	 * Instantiates a new feature mining.
	 */
	private FeatureMining(){}
	
	/**
	 * Gets the singleton.
	 *
	 * @return the singleton
	 */
	public static FeatureMining getSingleton(){
		if(instance == null){
			instance = new FeatureMining();
		}
		return instance;
	}
	
	/**
	 * Go.
	 *
	 * @param args the args
	 */
	private void go(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	rootWindow = new RootFeatureWindow();
		    }
		});
		
		documentProcessor = new HtmlProcessor();
	}
	
	/**
	 * Gets the root window.
	 *
	 * @return the root window
	 */
	public RootFeatureWindow getRootWindow() {
		return rootWindow;
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main(String[] args) {
		
		try {
			Gate.init();
		} catch (GateException e1) {
			e1.printStackTrace();
		}
		
		FeatureMining mainApp = getSingleton();
		mainApp.go(args);
	}

	/**
	 * Gets the document processor.
	 *
	 * @return the document processor
	 */
	public SimpleProcessor getDocumentProcessor() {
		return this.documentProcessor;
	}
	
}
