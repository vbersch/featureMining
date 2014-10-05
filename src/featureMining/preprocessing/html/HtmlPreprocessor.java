package featureMining.preprocessing.html;

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

import featureMining.feature.OptionTransferObject;
import featureMining.main.FeatureMining;
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
	private Queue<String> links;
	private HtmlHeadingAnnotator headingAnnotator;
	
	public HtmlPreprocessor(){
		links = new LinkedList<String>();
	}
	
	@Override
	public Corpus createAnnotatedCorpus(OptionTransferObject optionsTO) {
		
		try {
			this.corpus = gate.Factory.newCorpus("testCorpus");
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
		}
		
		this.baseUrl = optionsTO.getBaseUrl();
		this.hostName = optionsTO.getHostName(); 
		
		String html = getHTML(baseUrl);
		this.getContentFromBaseUrl(html);
		
		if(optionsTO.getDocumentationType() == "General"){
			this.headingAnnotator = new HtmlHeadingAnnotator();
		}else if(optionsTO.getDocumentationType() == "Mixxx"){
			this.headingAnnotator = new MixxxHeadingAnnotator();
		}else if(optionsTO.getDocumentationType() == "Github"){
			this.headingAnnotator = new GithubHeadingAnnotator();
		}
		this.headingAnnotator.annotateCorpus(this.corpus, this.baseUrl);
		
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
					link = baseUrl + link;
					if (link.contains("#")) {
						String splitString[] = link.split("#");
						link = splitString[0];
					}
				} else if (!link.matches(".*" + hostName + ".*")) {
					link = "";
				}
				if (link != "" && !links.contains(link)) {
					links.offer(link);
				}
			}
		}

		System.out.println("\nFound " + links.size() + " Links on " + baseUrl);

		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(cm).build();

		DocWorker[] threads = new DocWorker[FeatureMining.maxThreads];

		// init threads
		for (int i = 0; i < threads.length; i++) {
			threads[i] = new DocWorker(httpClient, i + 1);
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

		FeatureMining.getSingleton().getRootWindow()
				.addInfoTextLine("\nDocuments created...");
		FeatureMining.getSingleton().getRootWindow()
				.addInfoTextLine("\nStarting Gate Processing Units...\n");
		FeatureMining.getSingleton().getRootWindow()
				.addInfoTextLine("\n--------------------------------------\n");
		System.out.println("\nDocuments created...");
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
