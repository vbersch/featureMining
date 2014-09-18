package featureMining;

import featureMining.processing.HtmlProcessor;
import featureMining.ui.RootFeatureWindow;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.util.GateException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

//main class
public class FeatureMining {//Singleton
	
	public static int maxThreads = 4;
	private SimpleProcessor documentProcessor;
	private RootFeatureWindow rootWindow;
	private static FeatureMining instance = null;
	
	private FeatureMining(){}
	
	public static FeatureMining getSingleton(){
		if(instance == null){
			instance = new FeatureMining();
		}
		return instance;
	}
	
	private void go(String[] args) {		
		rootWindow = new RootFeatureWindow();
		documentProcessor = new HtmlProcessor();
	}
	
	public RootFeatureWindow getRootWindow() {
		return rootWindow;
	}
	
	public static void main(String[] args) {
		
		try {
			Gate.init();
		} catch (GateException e1) {
			e1.printStackTrace();
		}
		
		FeatureMining mainApp = getSingleton();
		mainApp.go(args);
	}

	public SimpleProcessor getDocumentProcessor() {
		return this.documentProcessor;
	}
	
}
