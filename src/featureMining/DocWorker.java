package featureMining;

import featureMining.processing.HtmlProcessor;
import gate.Document;
import gate.Factory;
import gate.FeatureMap;
import gate.creole.ResourceInstantiationException;

import java.io.IOException;
import java.io.InputStream;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.protocol.HttpContext;

// TODO: Auto-generated Javadoc
/**
 * The Class DocWorker.
 */
public class DocWorker extends Thread {

	/** The http client. */
	private final CloseableHttpClient httpClient;
    
    /** The context. */
    private final HttpContext context;
    
    /** The id. */
    private final int id;
    
    /**
     * Instantiates a new doc worker.
     *
     * @param httpClient the http client
     * @param id the id
     */
    public DocWorker(CloseableHttpClient httpClient, int id) {
        this.httpClient = httpClient;
        this.context = HttpClientContext.create();
        this.id = id;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Thread#run()
     */
    @Override
    public void run() {
    	
    	String url = ((HtmlProcessor)FeatureMining.getSingleton().getDocumentProcessor()).getNextLink();
    	while(url != null){
    		HttpGet httpget = new HttpGet(url);
    		FeatureMining.getSingleton().getRootWindow().addInfoTextLine("\nThread " + this.id + ": getting " + url);
    		System.out.println("Thread " + this.id + ": getting " + url);
	        try {
	            CloseableHttpResponse response = httpClient.execute(httpget, context);
	            try {
	                HttpEntity entity = response.getEntity();
	                InputStream is = entity.getContent();
	                String html = IOUtils.toString(is, "UTF-8");
	                
	                //create gate doc from html site
	                FeatureMap params = Factory.newFeatureMap();
					params.put("mimeType", "text/html");
					params.put("encoding", "UTF-8");
					Document doc = Factory.newDocument(html);
					doc.setPreserveOriginalContent(false);
					doc.setFeatures(params);
					doc.setName(url);
	                
					//add document to the corpus 
					FeatureMining.getSingleton().getDocumentProcessor().addDocument(doc);
	            } catch (ResourceInstantiationException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally {
	                response.close();
	            }
	        } catch (ClientProtocolException ex) {
	            // Handle protocol errors
	        } catch (IOException ex) {
	            // Handle I/O errors
	        }
	        url = ((HtmlProcessor)FeatureMining.getSingleton().getDocumentProcessor()).getNextLink();
    	}
    }
	
}
