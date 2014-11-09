package featureMining.controller;

import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;
import featureMining.preprocessing.IDocumentPreprocessor;
import featureMining.preprocessing.html.HtmlPreprocessor;
import featureMining.processing.DocumentProcessor;
import featureMining.processing.ISimpleProcessor;
import featureMining.ui.RootFeatureWindow;
import gate.Corpus;
import gate.Gate;
import gate.util.GateException;

import java.io.File;
import java.net.MalformedURLException;

import javax.swing.SwingUtilities;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class FeatureMining.
 * Instantiates a RootFeatureWindow and serves as Controller of the 
 * Feature Mining logic.
 */
public class FeatureMining {//Singleton
	
	/** the logger */
	private static Logger logger = LoggerFactory.getLogger(FeatureMining.class);
	
	/** A Gate Corpus. Every Document will be stored in here. */
	private Corpus corpus;
	
	/** Interface for the processing*/
	private ISimpleProcessor documentProcessor;
	
	/** Interface for the preprocessing */
	private IDocumentPreprocessor documentPreprocessor;
	
	/** The UI. */
	private RootFeatureWindow rootWindow;
	
	/** singleton instance */
	private static FeatureMining instance = null;
	
	/**
	 * Private Constructor because of Singleton Pattern.
	 */
	private FeatureMining(){}
	
	/**
	 * Returns the singleton.
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
	 * Getter for the main UI Window.
	 *
	 * @return the UI
	 */
	public RootFeatureWindow getRootWindow() {
		return rootWindow;
	}

	/**
	 * Getter for the documentProcessor.
	 *
	 * @return the documentProcessor
	 */
	public ISimpleProcessor getDocumentProcessor() {
		return this.documentProcessor;
	}

	/**
	 * Setter for the Corpus.
	 *
	 * @param corpus corpus
	 */
	public void setCorpus(Corpus corpus) {
		this.corpus = corpus;
	}

	/**
	 * Getter for the documentPreprocessor.
	 *
	 * @return the documentPreprocessor
	 */
	public IDocumentPreprocessor getDocumentPreprocessor() {
		return documentPreprocessor;
	}

	/**
	 * Creates a FeatureContainer, starts a DocumentPreprocessor and
	 * a Documentprocessor depending on the OptionsTransferObject
	 *
	 * @param optionsTO the optionsTransferObject
	 * @return a FeatureContainer containing the mined Features
	 */
	public FeatureContainer doProcessing(OptionTransferObject optionsTO) {
		
		FeatureContainer featureContainer = new FeatureContainer(); 
		if(optionsTO.getPreprocessingName().equals("Html")){// here is the place to add additional preprocessors for additional formats
			this.documentPreprocessor = new HtmlPreprocessor();
		}else{
			logger.error("No other Preprocessors but HTML implemented yet...");
			System.exit(0);
		}
		
		this.corpus = this.documentPreprocessor.createAnnotatedCorpus(optionsTO);
		documentProcessor = new DocumentProcessor();
		documentProcessor.processCorpus(featureContainer, this.corpus);//actual mining
		
		return featureContainer;
	}

	/**
	 * Getter for the corpus.
	 *
	 * @return the corpus
	 */
	public Corpus getCorpus() {
		return corpus;
	}	
	
	/**
	 * Creates the UI in a new Thread.
	 *
	 * @param args the args
	 */
	private void start(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
		    public void run() {
		    	rootWindow = new RootFeatureWindow(); //create the GUI and wait for input
		    }
		});
	}
	
	/**
	 * The main method.
	 *
	 * @param command line args
	 */
	public static void main(String[] args) {
		
		try {
			if(args.length < 1){
				System.out.println("Install directory of Gate is required as first Parameter");
				System.out.println("Shutting down...");
				System.exit(0);
			}
			Gate.setGateHome(new File(args[0]));
			Gate.init(); //init Gate Embedded
			//register Creole Directories
			Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome().getPath() + "/ANNIE").toURI().toURL());
			Gate.getCreoleRegister().registerDirectories(new File(Gate.getPluginsHome().getPath() + "/Stemmer_Snowball").toURI().toURL());
			Gate.getCreoleRegister().registerDirectories(new File(System.getProperty("user.dir") + "/DocumentProcessorPR").toURI().toURL());
		} catch (GateException e1) {
			e1.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		
		FeatureMining mainApp = getSingleton();
		mainApp.start(args);
	}
	
}
