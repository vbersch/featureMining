package featureMining.preprocessing.html;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import featureMining.controller.FeatureMining;
import featureMining.feature.OptionTransferObject;
import featureMining.preprocessing.IDocumentPreprocessor;
import gate.Corpus;
import gate.Document;
import gate.creole.ResourceInstantiationException;

public class HtmlPreprocessor implements IDocumentPreprocessor {
	
	private Corpus corpus;
	private static Pattern htmltagPattern = Pattern
			.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");
	private static Pattern linkPattern = Pattern.compile("href=\"[^>]*\">");
	private String hostName;
	private String baseUrl;
	private String linkBase;
	private Queue<String> links;
	private HtmlHeadingAnnotator headingAnnotator;
	
	public HtmlPreprocessor(){
		links = new LinkedList<String>();
	}
	
	@Override
	public Corpus createAnnotatedCorpus(OptionTransferObject optionsTO) {
		
		this.baseUrl = optionsTO.getBaseUrl();
		if(optionsTO.getDocumentationType() == "Github"){
			this.linkBase = "https://github.com";
			this.headingAnnotator = new GithubHeadingAnnotator();
		}else{
			this.linkBase = optionsTO.getBaseUrl();
		}
		this.hostName = optionsTO.getHostName(); 
		
		try {
			this.corpus = gate.Factory.newCorpus(this.baseUrl);
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
		}
		
		String html = getHTML(baseUrl);
		this.getContentFromBaseUrl(html);
		
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nDocuments created...");
		System.out.println("\nDocuments created...");
		
		if(optionsTO.getDocumentationType() == "General"){
			this.headingAnnotator = new HtmlHeadingAnnotator();
		}else if(optionsTO.getDocumentationType() == "Mixxx"){
			this.headingAnnotator = new MixxxHeadingAnnotator();
		}
		this.headingAnnotator.annotateCorpus(this.corpus, this.baseUrl);
		
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nStarting Gate Processing Units...\n");
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\n--------------------------------------\n");
		
		return this.corpus;
	}
	
	private void getContentFromBaseUrl(String baseContent) {
		System.out.println("hostName: " + this.hostName);

		if (baseContent == "") {
			System.out.println("Cannot read base Url");
			System.exit(0);
		}

		links.offer(this.baseUrl);

		Matcher tagmatch = htmltagPattern.matcher(baseContent);
		while (tagmatch.find()) {
			Matcher matcher = linkPattern.matcher(tagmatch.group());
			matcher.find();
			String link = matcher.group().replaceFirst("href=\"", "")
					.replaceFirst("\">", "")
					.replaceFirst("\"[\\s]?target=\"[a-zA-Z_0-9]*", "")
					.replaceFirst("\".*", "");
			if (valid(link)) {
				if (!link.startsWith("http")) {
					link = linkBase + link;
					if (link.contains("#")) {
						String splitString[] = link.split("#");
						link = splitString[0];
					}
				} 
				if (link != "" && !links.contains(link)) {
					if(link.contains(hostName)){
						links.offer(link);
					}
				}
			}
		}

		System.out.println("\nFound " + links.size() + " Links on " + baseUrl);

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(cm).build();
		
		ArrayList<Thread> threads = new ArrayList<Thread>();

		// init threads
		Runnable run = new DocWorker(httpClient);
		for (int i = 0; i < FeatureMining.maxThreads; i++) {
			threads.add(new Thread(run));
		}

		// start the threads
		for (int j = 0; j < threads.size(); j++) {
			threads.get(j).start();
		}

		// join the threads
		for (int j = 0; j < threads.size(); j++) {
			try {
				threads.get(j).join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	private static String getHTML(String urlString) {
		URL url;
		HttpURLConnection conn;
		InputStream is;
		String html = "";
		try {
			FeatureMining.getSingleton().getRootWindow()
					.setInfoText("Connecting to... " + urlString);
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
	
	public synchronized void addDocument(Document doc) {
		corpus.add(doc);
	}
	
	public synchronized String getNextLink() {
		if (!links.isEmpty()) {
			return links.poll();
		}
		return null;
	}
	
	private static boolean valid(String s) {
		if (s.matches("javascript:.*|mailto:.*")) {
			return false;
		}

		if (s.matches("(?i).*installing.*|.*installation.*|.*setup.*")) {
			return false;
		}
		return true;
	}
	
}
