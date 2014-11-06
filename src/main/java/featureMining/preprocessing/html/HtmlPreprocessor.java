package featureMining.preprocessing.html;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import featureMining.controller.FeatureMining;
import featureMining.feature.OptionTransferObject;
import featureMining.preprocessing.IDocumentPreprocessor;
import gate.Corpus;
import gate.Document;
import gate.creole.ResourceInstantiationException;

/**
 * The Class HtmlPreprocessor.
 * The HtmlPreprocessor extracts Url´s under the
 * Base Url in the optionTransferObject. It coordinates
 * the DocWorkers and starts the right HeadingAnnotator 
 * according to the optionTransferObject.
 */
public class HtmlPreprocessor implements IDocumentPreprocessor {
	
	/** the logger */
	private static Logger logger = LoggerFactory.getLogger(HtmlPreprocessor.class);
	
	/** The corpus. */
	private Corpus corpus;
	
	/** The number of Threads that run to get ´the html pages of the documentation */
	public static int maxThreads = 4;
	
	/** regex to find html <a> tags. */
	private static Pattern htmltagPattern = Pattern
			.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");
	
	/** regex to extract the link from a <a> tag.. */
	private static Pattern linkPattern = Pattern.compile("href=\"[^>]*\">");
	
	/** The host name. */
	private String hostName;
	
	/** The base url. */
	private String baseUrl;
	
	/** fix part of every url. */
	private String linkBase;
	
	/** Queue containing all found URL´s. The
	 * DocWorkers will take these URL´s until the
	 * Queue is empty. */
	private Queue<String> links;
	
	/** The heading annotator. */
	private HtmlHeadingAnnotator headingAnnotator;
	
	/**
	 * Constructor.
	 */
	public HtmlPreprocessor(){
		links = new LinkedList<String>();
	}
	
	/* (non-Javadoc)
	 * @see featureMining.preprocessing.IDocumentPreprocessor#createAnnotatedCorpus(featureMining.feature.OptionTransferObject)
	 * Takes a url and creates a Gate Corpus out of the underlying html documents. 
	 * Then it starts a HeadingAnnotator to get the generic Structure the 
	 * DocumentProcessor demands.
	 */
	@Override
	public Corpus createAnnotatedCorpus(OptionTransferObject optionsTO) {
		
		maxThreads = optionsTO.getThreadNum();
		
		this.baseUrl = optionsTO.getBaseUrl();
		if(optionsTO.getDocumentationType() == "Github"){
			this.linkBase = "https://github.com";// every link has to contain this
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
		logger.info("\nDocuments created...");
		
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
	
	/**
	 * Parses the baseContent for links and calls startWorkers() 
	 * to process the links.
	 *
	 * @param baseContent the base content
	 * @return the content from base url
	 */
	private void getContentFromBaseUrl(String baseContent) {
		logger.info("hostName: " + this.hostName);

		if (baseContent == "") {
			logger.error("Cannot read base Url");
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

		logger.info("\nFound " + links.size() + " Links on " + baseUrl);
		this.startWorkers();
	}
	
	
	/**
	 * Starts the Docworkers to process the found url´s.
	 */
	private void startWorkers(){
		PoolingHttpClientConnectionManager cm = new PoolingHttpClientConnectionManager();
		CloseableHttpClient httpClient = HttpClients.custom()
				.setConnectionManager(cm).build();
		
		ArrayList<Thread> threads = new ArrayList<Thread>();

		// init threads
		Runnable run = new HtmlWorker(httpClient);
		for (int i = 0; i < maxThreads; i++) {
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
	
	/**
	 * Returns the html content under 
	 * a specified url as a String .
	 *
	 * @param urlString the url string
	 * @return the html as String
	 */
	private static String getHTML(String urlString) {
		URL url;
		HttpURLConnection conn;
		InputStream is;
		String html = "";
		try {
			FeatureMining.getSingleton().getRootWindow()
					.setInfoText("Connecting to... " + urlString);
			logger.info("Connecting to... " + urlString);
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
	
	/**
	 * synchronized Method so that the Docworkers
	 * can add Documents to the Corpus from different
	 * Threads.
	 *
	 * @param doc the doc
	 */
	public synchronized void addDocument(Document doc) {
		corpus.add(doc);
	}
	
	/**
	 * Gets the next link.
	 *
	 * @return the next link
	 */
	public synchronized String getNextLink() {
		if (!links.isEmpty()) {
			return links.poll();
		}
		return null;
	}
	
	/**
	 * little Helper method to filter some links.
	 *
	 * @param s the s
	 * @return true, if successful
	 */
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
