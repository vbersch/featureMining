package featureMining.processing;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;

import featureMining.DocWorker;
import featureMining.FeatureMining;
import featureMining.processing.feature.FeatureContainer;
import gate.Annotation;
import gate.AnnotationSet;
import gate.Document;
import gate.Factory;
import gate.creole.ResourceInstantiationException;
import gate.creole.SerialAnalyserController;
import gate.util.GateException;
import gate.util.OffsetComparator;

// TODO: Auto-generated Javadoc
/**
 * The Class HtmlProcessor.
 */
public class HtmlProcessor extends SimpleProcessor {

	/** The htmltag pattern. */
	private static Pattern htmltagPattern = Pattern
			.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");

	/** The link pattern. */
	private static Pattern linkPattern = Pattern.compile("href=\"[^>]*\">");

	/** The host name. */
	private String hostName;

	private String baseUrl;

	/** The links. */
	private Queue<String> links;

	/** The feature container. */
	private FeatureContainer featureContainer;

	private HtmlHeadingAnnotator headingAnnotator;

	/**
	 * Instantiates a new html processor.
	 */
	public HtmlProcessor() {
		links = new LinkedList<String>();
		featureContainer = new FeatureContainer();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see featureMining.processing.SimpleProcessor#processCorpus()
	 */
	public FeatureContainer processCorpus() {

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

		Iterator<Document> docIterator = this.corpus.iterator();
		while (docIterator.hasNext()) {
			Document doc = docIterator.next();
			AnnotationSet tokens = doc.getAnnotations().get("Token");
			AnnotationSet headings = doc.getAnnotations("Original markups")
					.get("heading");

			Iterator<Annotation> it = headings.iterator();
			while (it.hasNext()) {
				Annotation next = it.next();
				String wholeSentence = gate.Utils.stringFor(doc, next
						.getStartNode().getOffset(), next.getEndNode()
						.getOffset());

				List sortedTokens = new ArrayList(tokens.getContained(next
						.getStartNode().getOffset(), next.getEndNode()
						.getOffset()));
				Collections.sort(sortedTokens, new OffsetComparator());
				Annotation first = null;
				Annotation last = null;
				for (int i = 0; i < sortedTokens.size(); i++) {
					Annotation token = (Annotation) sortedTokens.get(i);
					String category = token.getFeatures().get("category")
							.toString();
					if (category == "NNP" || category == "NNS"
							|| category == "NN" || category == "NNPS") {
						if (first == null) {
							first = token;
						} else {
							last = token;
						}
					} else {
						if (first != null) {
							if (last == null) {
								last = first;
							}
							String featureString = gate.Utils.stringFor(doc,
									first.getStartNode().getOffset(), last
											.getEndNode().getOffset());
							featureContainer.add(featureString, doc.getName(),
									wholeSentence);
							first = null;
							last = null;
						}
					}
				}
				if (first != null) {
					if (last == null) {
						last = first;
					} // if it2 comes to an end and a noun was the last word
					String featureString = gate.Utils.stringFor(doc, first
							.getStartNode().getOffset(), last.getEndNode()
							.getOffset());
					featureContainer.add(featureString, doc.getName(),
							wholeSentence);
					first = null;
					last = null;
				}
			}
			AnnotationSet contents = doc.getAnnotations("Original markups").get("content");
			List sortedContents = new ArrayList(contents);
			
			Collections.sort(sortedContents, new OffsetComparator());
			
			for(int i = 0; i < sortedContents.size(); i++ ){
				Annotation contentAnnot = (Annotation) sortedContents.get(i);
				for(String feature : featureContainer.getFeatureStorage().keySet()){
					String content = gate.Utils.stringFor(doc, contentAnnot.getStartNode().getOffset(), contentAnnot.getEndNode().getOffset());
					Pattern featurePattern = Pattern.compile(feature);
					Matcher featureMatcher = featurePattern.matcher(content);
					while(featureMatcher.find()){
						long startIndex = contentAnnot.getStartNode().getOffset() + (long)featureMatcher.start();
						long endIndex = contentAnnot.getStartNode().getOffset() + (long)featureMatcher.end();
						Iterator sentenceIt = doc.getAnnotations().get(startIndex, endIndex).get("Sentence").iterator();
						while(sentenceIt.hasNext()){
							Annotation sentence = (Annotation) sentenceIt.next();
							String wholeSentence = gate.Utils.stringFor(doc, sentence.getStartNode().getOffset(), sentence.getEndNode().getOffset());
							featureContainer.addOccurence(feature, wholeSentence);
						}
					}
				}
			}
		}
		return featureContainer;
	}

	/**
	 * Run processing resources.
	 *
	 * @param processingResource
	 *            the processing resource
	 * @throws GateException
	 *             the gate exception
	 */
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * featureMining.processing.SimpleProcessor#createCorpus(java.lang.String)
	 */
	@Override
	public void createCorpus(String address) {

		Pattern hostPattern = Pattern.compile("//.*?/");
		Matcher hostMatcher = hostPattern.matcher(address);
		hostMatcher.find();
		this.hostName = hostMatcher.group();

		try {
			corpus = gate.Factory.newCorpus("testCorpus");
		} catch (ResourceInstantiationException e) {
			e.printStackTrace();
		}
		this.baseUrl = address;
		String html = getHTML(address);
		this.getContentFromBaseUrl(html);

	}

	/**
	 * Gets the content from base url.
	 *
	 * @param baseUrl
	 *            the base url
	 * @return the content from base url
	 */
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

		featureContainer.setLinkNum(links.size());
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

	/**
	 * Valid.
	 *
	 * @param s
	 *            the s
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
	 * Adds the document.
	 *
	 * @param doc
	 *            the doc
	 */
	public synchronized void addDocument(Document doc) {
		corpus.add(doc);
	}

	/**
	 * Gets the html.
	 *
	 * @param urlString
	 *            the url string
	 * @return the html
	 */
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

}
