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

import featureMining.main.FeatureMining;
import featureMining.preprocessing.IDocumentPreprocessor;
import gate.Corpus;
import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;

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
	public Corpus createAnnotatedCorpus(String address, String hostName) {
		
		if(hostName == null){
			Pattern hostPattern = Pattern.compile("//.*?/");
			Matcher hostMatcher = hostPattern.matcher(address);
			hostMatcher.find();
			hostName = hostMatcher.group();
		}

		try {
			this.corpus = gate.Factory.newCorpus("testCorpus");
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
		}
		this.baseUrl = address;
		String html = getHTML(address);
		this.getContentFromBaseUrl(html);
		
		String[] processingResources = {
				"gate.creole.tokeniser.DefaultTokeniser",
				"gate.creole.splitter.SentenceSplitter",
				"gate.creole.POSTagger" };

		try {
			runProcessingResources(processingResources);
		} catch (GateException e) {
			e.printStackTrace();
		}

		this.headingAnnotator = new HtmlHeadingAnnotator();
		this.headingAnnotator.annotateCorpus(this.corpus, this.baseUrl);
		
		
		return this.corpus;
	}
	
	private void runProcessingResources(String[] processingResource)
			throws GateException {
		SerialAnalyserController pipeline = (SerialAnalyserController) Factory
				.createResource("gate.creole.SerialAnalyserController");

		for (int pr = 0; pr < processingResource.length; pr++) {
			FeatureMining
					.getSingleton()
					.getRootWindow()
					.addInfoTextLine(
							"\n\t* Loading " + processingResource[pr] + " ... ");
			System.out.print("\n\t* Loading " + processingResource[pr]
					+ " ... ");
			pipeline.add((gate.LanguageAnalyser) Factory
					.createResource(processingResource[pr]));
			FeatureMining.getSingleton().getRootWindow()
					.addInfoTextLine("done");
			System.out.println("done");
		}
		pipeline.setCorpus(this.corpus);
		FeatureMining
				.getSingleton()
				.getRootWindow()
				.addInfoTextLine(
						"\nRunning processing resources over corpus...");
		System.out.print("Running processing resources over corpus...");
		pipeline.execute();
		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("done");
		System.out.println("done");
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