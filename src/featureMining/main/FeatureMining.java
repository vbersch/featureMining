package featureMining.main;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.SwingUtilities;

import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;
import featureMining.preprocessing.IDocumentPreprocessor;
import featureMining.preprocessing.html.HtmlPreprocessor;
import featureMining.processing.DocumentProcessor;
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

	private String preprocessorName;
	
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
	@SuppressWarnings("deprecation")
	public static void main(String[] args) {
		
		try {
			Gate.init();
			Gate.getCreoleRegister().registerDirectories(new File(System.getProperty("user.dir") + "/DocumentProcessorPR").toURL());

		} catch (GateException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
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

	public FeatureContainer doProcessing(OptionTransferObject optionsTO) {
		
		FeatureContainer featureContainer = new FeatureContainer(); 
		if(optionsTO.getPreprocessingName().equals("Html")){
			this.documentPreprocessor = new HtmlPreprocessor();
		}else{
			System.out.println("No other Preprocessors but HTML implemented yet...");
			System.exit(0);
		}
		
		maxThreads = optionsTO.getThreadNum();
		
		Corpus corpus = this.documentPreprocessor.createAnnotatedCorpus(optionsTO);
		documentProcessor = new DocumentProcessor(optionsTO);
		documentProcessor.processCorpus(featureContainer, corpus);
		
		return featureContainer;
	}

	public void setThreadNum(int threadNum) {
		maxThreads = threadNum; 
	}

	public void setPreprocessor(String preprocessor) {
		this.preprocessorName = preprocessor;
	}
	
}
