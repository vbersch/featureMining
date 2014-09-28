package featureMining.main;

import javax.swing.SwingUtilities;

import featureMining.feature.FeatureContainer;
import featureMining.preprocessing.IDocumentPreprocessor;
import featureMining.preprocessing.html.HtmlPreprocessor;
import featureMining.processing.DocumentProcessor;
import featureMining.processing.ISimpleProcessor;
import featureMining.ui.RootFeatureWindow;
import gate.Corpus;
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
	private DocumentProcessor documentProcessor;
	
	private IDocumentPreprocessor documentPreprocessor;
	
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
		    	rootWindow = new RootFeatureWindow(); //create the GUI and wait for input
		    }
		});
		
		documentPreprocessor = new HtmlPreprocessor();
		documentProcessor = new DocumentProcessor();
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
	public DocumentProcessor getDocumentProcessor() {
		return this.documentProcessor;
	}

	public IDocumentPreprocessor getDocumentPreprocessor() {
		return documentPreprocessor;
	}

	public FeatureContainer doProcessing(String url, String hostName) {
		
		FeatureContainer featureContainer = new FeatureContainer();
		this.documentPreprocessor = new HtmlPreprocessor();
		Corpus corpus = this.documentPreprocessor.createAnnotatedCorpus(url, hostName);
		documentProcessor = new DocumentProcessor();
		documentProcessor.processCorpus(featureContainer, corpus);
		
		return featureContainer;
	}
	
}
