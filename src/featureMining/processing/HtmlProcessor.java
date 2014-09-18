package featureMining.processing;

import featureMining.DocWorker;
import featureMining.FeatureMining;
import featureMining.SimpleProcessor;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.Gate;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

public class HtmlProcessor extends SimpleProcessor{
	
	private static Pattern htmltagPattern = Pattern.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");
	private static Pattern linkPattern = Pattern.compile("href=\"[^>]*\">");
	private String hostName;
	private Queue<String> links;
	
	public HtmlProcessor(){
		links = new LinkedList<String>();
		featureStrings = new ArrayList<String>();
	}
	
	public void processCorpus(){
		
		String[] processingResources = {
				"gate.creole.tokeniser.DefaultTokeniser",
		        "gate.creole.splitter.SentenceSplitter",
		        "gate.creole.POSTagger"
		};
		
		try {
			runProcessingResources(processingResources);
		} catch (GateException e) {
			e.printStackTrace();
		}
		
		System.out.println("\ntest: " + this.testDoc.getAnnotations().getAllTypes().toString());
		AnnotationSet defaultAnnots = this.testDoc.getAnnotations();
		AnnotationSet tokens = defaultAnnots.get("Token");
		AnnotationSet origAnnots = this.testDoc.getAnnotations("Original markups");
		AnnotationSet listEl = origAnnots.get("li");
		AnnotationSet featureAnnots = this.testDoc.getAnnotations("Feature Annotations");
		
		Iterator<Annotation> it = listEl.iterator();
		while(it.hasNext()){
			Annotation next = it.next();
			if(!listEl.getContained(next.getStartNode().getOffset(), next.getEndNode().getOffset()).contains("li")){
				Iterator<Annotation> it2 = tokens.getContained(next.getStartNode().getOffset(), next.getEndNode().getOffset()).iterator();
				String heading = "";
				while(it2.hasNext()){
					Annotation token = it2.next();
					String category = token.getFeatures().get("category").toString();
					if(category == "NNP" || category == "NNS"){
						featureStrings.add(gate.Utils.stringFor(this.testDoc, token));
						featureAnnots.add(token);
						heading += gate.Utils.stringFor(this.testDoc, token) + " ";
					}
				}
				System.out.println(heading);
			}
		}
		FeatureMining.getSingleton().getRootWindow().fillFeatureList(featureStrings);
	}
	
	private void runProcessingResources(String[] processingResource)
	          throws GateException {
	    SerialAnalyserController pipeline = (SerialAnalyserController)Factory
	            .createResource("gate.creole.SerialAnalyserController");

	    for(int pr = 0; pr < processingResource.length; pr++) {
	      System.out.print("\t* Loading " + processingResource[pr] + " ... ");
	      pipeline.add((gate.LanguageAnalyser)Factory
	              .createResource(processingResource[pr]));
	      System.out.println("done");
	    }
	    pipeline.setCorpus(this.corpus);
	    System.out.print("Running processing resources over corpus...");
	    pipeline.execute();
	    System.out.println("done");
	  }

	@Override
	public void createCorpus(String address) {
		try {
			String html = getHTML(address);
			FeatureMap params = Factory.newFeatureMap();	
			params.put("encoding", "UTF-8");
			this.testDoc = Factory.newDocument(html);
			this.testDoc.setPreserveOriginalContent(false);
			this.testDoc.setFeatures(params);
			corpus = gate.Factory.newCorpus("testCorpus");
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
		}
		
		this.corpus.add(this.testDoc);
		Pattern hostPattern = Pattern.compile("//.*?/");
		Matcher hostMatcher = hostPattern.matcher(address);
		hostMatcher.find();
		this.hostName = hostMatcher.group();
	}
	
	private void getContentFromBaseUrl(String baseUrl){
		String baseContent 	= getHTML(baseUrl); //get html for the initial site

		System.out.println("hostName: " + hostName);
		
		if(baseContent == ""){
			System.out.println("Cannot read base Url");
			System.exit(0);
		}
		
		links.offer(baseUrl);
		
		Matcher tagmatch = htmltagPattern.matcher(baseContent);
		while (tagmatch.find()) {
			Matcher matcher = linkPattern.matcher(tagmatch.group());
			matcher.find();
			String link = matcher.group().replaceFirst("href=\"", "")
					.replaceFirst("\">", "")
					.replaceFirst("\"[\\s]?target=\"[a-zA-Z_0-9]*", "")
					.replaceFirst("\".*", "");
			if (valid(link)) {
				if(!link.startsWith("http")){
					link = baseUrl + link;
					if(link.contains("#")){
						String splitString[] = link.split("#");
						link = splitString[0];
					}
				}else if(!link.matches(".*" + hostName + ".*")){
					link = "";
				}
				if(link != "" && !links.contains(link)){
					links.offer(link);
				}
			}
		}
		
		System.out.println("\nFound " + links.size() + " Links on " + baseUrl );
		
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom()
		        .setConnectionManager(cm)
		        .build();
		
		DocWorker[] threads = new DocWorker[FeatureMining.maxThreads];
		
		//init threads
		for (int i = 0; i < threads.length; i++) {
		    threads[i] = new DocWorker(httpClient, i+1);
		}
		
		// start the threads
		for (int j = 0; j < threads.length; j++) {
		    threads[j].start();
		}

		// join the threads
		for (int j = 0; j < threads.length; j++) {
		    try {
				threads[j].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		System.out.println("\nDocuments created...");
	}
	
	private static boolean valid(String s) {
	    if (s.matches("javascript:.*|mailto:.*")) {
	      return false;
	    }
	    	    
	    if(s.matches("(?i).*installing.*|.*installation.*")){
	    	return false;
	    }
	    return true;
	}
	
	public synchronized String getNextLink(){
		if(!links.isEmpty()){
			return links.poll();
		}
		return null;
	}
	
	public synchronized void addDocument(Document doc){
		corpus.add(doc);
	}
	
	private static String getHTML(String urlString){
		URL url;
		HttpURLConnection conn;
		InputStream is;
		String html = "";
		try {
			System.out.println("Connecting to... " + urlString);
			url = new URL(urlString);
			conn = (HttpURLConnection) url.openConnection();
			conn.addRequestProperty("User-Agent", "Mozilla/4.76"); 
			is = conn.getInputStream();
			html = IOUtils.toString(is, "UTF-8");
		} catch (IOException e) {
			html = "";
		}
		return html;
	}
	
}
