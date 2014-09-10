package featureMining;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

public class Main {
	
	public static HashMap<String,String> features;
	private static Pattern htmltagPattern;
	private static Pattern linkPattern;
	
	public static void main(String[] args) {
		
		//init
		features = new HashMap<String,String>();
		String baseUrl = "";
		htmltagPattern = Pattern.compile("<a\\b[^>]*href=\"[^>]*>(.*?)</a>");
	    linkPattern = Pattern.compile("href=\"[^>]*\">");
//		try {
//			Gate.init();
//		} catch (GateException e) {
//			e.printStackTrace();
//		}
		
		if(args.length < 1){
			System.out.println("Pass a url as argument!");
			System.exit(0);
		}else{
			baseUrl = args[0];
		}
		System.out.println("Getting the html sites from " + baseUrl);
		ArrayList<String> docs = getContentFromBaseUrl(baseUrl);  
	}
	
	
	private static ArrayList<String> getContentFromBaseUrl(String baseUrl){
		String baseContent = getHTML(baseUrl);
		ArrayList<String> links = new ArrayList<String>();
		//System.out.println(html);
		
		if(baseContent == ""){
			System.out.println("Cannot read base Url");
			System.exit(0);
		}
		
		Matcher tagmatch = htmltagPattern.matcher(baseContent);
		while (tagmatch.find()) {
			Matcher matcher = linkPattern.matcher(tagmatch.group());
			matcher.find();
			String link = matcher.group().replaceFirst("href=\"", "")
					.replaceFirst("\">", "")
					.replaceFirst("\"[\\s]?target=\"[a-zA-Z_0-9]*", "");
			if (valid(link)) {
				links.add(makeAbsolute(baseUrl, link));
			}
		}
		
		return null;
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
	    return true;
	  }

	  private static String makeAbsolute(String url, String link) {
	    if (link.matches("http://.*")) {
	      return link;
	    }
	    if (link.matches("/.*") && url.matches(".*$[^/]")) {
	      return url + "/" + link;
	    }
	    if (link.matches("[^/].*") && url.matches(".*[^/]")) {
	      return url + "/" + link;
	    }
	    if (link.matches("/.*") && url.matches(".*[/]")) {
	      return url + link;
	    }
	    if (link.matches("/.*") && url.matches(".*[^/]")) {
	      return url + link;
	    }
	    throw new RuntimeException("Cannot make the link absolute. Url: " + url
	        + " Link " + link);
	  }
}
