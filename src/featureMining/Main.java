package featureMining;

import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.Gate;
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
public class Main {
	
	private static Pattern htmltagPattern = Pattern.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");
	private static Pattern linkPattern = Pattern.compile("href=\"[^>]*\">");
	private static Corpus corpus;
	private static String hostName;
	private static int maxThreads = 4;
	private static Queue<String> links;
	
	public static void main(String[] args) {
		
		//init
		String baseUrl = "";
		links = new LinkedList<String>();
		
		if(args.length < 1){
			System.out.println("Pass a url as argument!");
			System.exit(0);
		}else{
			baseUrl = args[0];
		}
		
	    Pattern hostPattern = Pattern.compile("//.*?/");
		Matcher hostMatcher = hostPattern.matcher(baseUrl);
		hostMatcher.find();
		hostName = hostMatcher.group().replaceAll("/", "");
		
		try {
			Gate.init();
			corpus = Factory.newCorpus(hostName);
		} catch (GateException e1) {
			e1.printStackTrace();
		}
		getContentFromBaseUrl(baseUrl);  
	}
	
	
	private static void getContentFromBaseUrl(String baseUrl){
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
		
		DocWorker[] threads = new DocWorker[maxThreads];
		
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
	
	private static boolean valid(String s) {
	    if (s.matches("javascript:.*|mailto:.*")) {
	      return false;
	    }
	    	    
	    if(s.matches("(?i).*installing.*|.*installation.*")){
	    	return false;
	    }
	    return true;
	}
	
	public static synchronized String getNextLink(){
		if(!links.isEmpty()){
			return links.poll();
		}
		return null;
	}
	
	public static synchronized void addDocument(Document doc){
		corpus.add(doc);
	}
}
