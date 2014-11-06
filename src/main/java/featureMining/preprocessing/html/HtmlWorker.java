package featureMining.preprocessing.html;

import featureMining.controller.FeatureMining;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The Class HtmlWorker.
 * A Runnable, which gets the Html Content of a URL and 
 * creates a Gate Document out of it. Is needed to make
 * the HTTP Get Requests in parallel.
 */
public class HtmlWorker implements Runnable {

	/** the logger */
	private static Logger logger = LoggerFactory.getLogger(HtmlWorker.class);
	
	/** The Http client. */
	private final CloseableHttpClient httpClient;
    
    /** The Http context. */
    private final HttpContext context;
        
    /**
     * Constructor.
     *
     * @param httpClient the http client
     */
    public HtmlWorker(CloseableHttpClient httpClient) {
        this.httpClient = httpClient; //the same httpClient for all Workers
        this.context = HttpClientContext.create(); //different context for every Worker
    }
    
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     * The overridden run method. The DocWorker retrieves a URL
     * out of a Queue from the current DocumentPreprocessor and
     * extracts its contents.
     */
    @Override
    public void run() {
    	String url = ((HtmlPreprocessor)FeatureMining.getSingleton().getDocumentPreprocessor()).getNextLink();
    	while(url != null){
    		HttpGet httpget = new HttpGet(url);
    		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nThread " + String.valueOf((Thread.currentThread().getId()%HtmlPreprocessor.maxThreads+1)) + ": getting " + url);
    		logger.info("Thread " + String.valueOf((Thread.currentThread().getId()%HtmlPreprocessor.maxThreads+1)) + ": getting " + url);
	        
    		try {
	            CloseableHttpResponse response = httpClient.execute(httpget, context);
	            try {
	                HttpEntity entity = response.getEntity();
	                InputStream is = entity.getContent();
	                String html = IOUtils.toString(is, "UTF-8");
	                
	                //create gate doc from html page
	                FeatureMap params = Factory.newFeatureMap();
					params.put("mimeType", "text/html");
					params.put("encoding", "UTF-8");
					Document doc = Factory.newDocument(html);
					doc.setPreserveOriginalContent(false);
					doc.setFeatures(params);
					doc.setName(url);
	                
					//add document to the corpus 
					((HtmlPreprocessor)FeatureMining.getSingleton().getDocumentPreprocessor()).addDocument(doc);
	            } catch (ResourceInstantiationException e) {
					e.printStackTrace();
				} finally {
	                response.close();
	            }
	        } catch (IOException e) {
	            
	        }
	        url = ((HtmlPreprocessor)FeatureMining.getSingleton().getDocumentPreprocessor()).getNextLink();
    	}
    }
}
