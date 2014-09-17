package featureMining;

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

public class DocWorker extends Thread {

	private final CloseableHttpClient httpClient;
    private final HttpContext context;
    private final int id;
    
    public DocWorker(CloseableHttpClient httpClient, int id) {
        this.httpClient = httpClient;
        this.context = HttpClientContext.create();
        this.id = id;
    }
    
    @Override
    public void run() {
    	
    	String url = FeatureMining.getSingleton().getNextLink();
    	while(url != null){
    		HttpGet httpget = new HttpGet(url);
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
	                
					//add document to the corpus 
					FeatureMining.getSingleton().addDocument(doc);
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
	        url = FeatureMining.getSingleton().getNextLink();
    	}
    }
	
}
