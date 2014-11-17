package featureMining.persistence.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import featureMining.controller.FeatureMining;
import featureMining.feature.Feature;
import featureMining.feature.FeatureContainer;
import featureMining.feature.FeatureOccurrence;
import featureMining.persistence.IPersistenceHandler;
import featureMining.ui.RootFeatureWindow;

// TODO: Auto-generated Javadoc
/**
 * The Class XmlHandler.
 * This Implementation of the IPersistenceHandler provides
 * Saving and loading to and from XML Files.
 */
public class XmlHandler implements IPersistenceHandler{
	
	/**  the logger. */
	private static Logger logger = LoggerFactory.getLogger(XmlHandler.class);
	
	/** The instance. */
	private static XmlHandler instance = null;
	
	/** The root window. */
	private RootFeatureWindow rootWindow;
	
	/** The doc. */
	private Document doc;
	
	/**  Was stemming enabled during creation of xml File?. */
	private boolean isStemmingEnabled;
	
	/**
	 * Constructor.
	 *
	 * @param rootWindow the root window
	 */
	private XmlHandler(RootFeatureWindow rootWindow){
		this.rootWindow = rootWindow;
	};
	
	/* (non-Javadoc)
	 * @see featureMining.persistence.IPersistenceHandler#load(java.lang.String)
	 * load from xml File
	 */
	@Override
	public void load(String path) {
		try{
			File xmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			FeatureContainer featureContainer = new FeatureContainer();
			
			String isEnabled = doc.getElementsByTagName("StemmingEnabled").item(0).getTextContent();
			if(isEnabled.equals("True")){
				isStemmingEnabled = true;
			}else{
				isStemmingEnabled = false;
			}
			
			Node featureNode = doc.getElementsByTagName("Features").item(0);
			if(featureNode.getNodeType() == Node.ELEMENT_NODE) {
				Element featureElement = (Element)featureNode;
				Node linkNumNode = featureElement.getElementsByTagName("LinkNum").item(0);
				featureContainer.setLinkNum(Integer.parseInt(linkNumNode.getTextContent()));
				
				this.fillFeatureContainer(featureElement.getElementsByTagName("Feature"), featureContainer);
					
			}
			Node evalNode = doc.getElementsByTagName("Evaluation").item(0);
			Element evalElement = (Element)evalNode;
			rootWindow.setFeatureFound(Integer.parseInt(evalElement.getElementsByTagName("FeaturesFound").item(0).getTextContent()));
			rootWindow.setContent(featureContainer);
			
		}catch(Exception e){
			e.printStackTrace();
		}
	}

	/**
	 * create the Features and add them to the FeatureContainer.
	 *
	 * @param nodeList the node list
	 * @param featureContainer the feature container
	 */
	private void fillFeatureContainer(NodeList nodeList, FeatureContainer featureContainer) {
		rootWindow.setCurrentFeatureNum(nodeList.getLength());
		for(int i = 0; i< nodeList.getLength(); i++){
			Node feature = nodeList.item(i);
			Element featureElement = (Element) feature;
			String name;
			String oldName = null;
			String featureStem;
			
			name = featureElement.getElementsByTagName("Label").item(0).getTextContent();
			featureStem = featureElement.getElementsByTagName("FeatureStem").item(0).getTextContent();
			
			ArrayList<String> singleWords = new ArrayList<String>();
			Node wordNode = featureElement.getElementsByTagName("SingleWords").item(0);
			Element wordElement = (Element)wordNode;
			NodeList wordList = wordElement.getElementsByTagName("Word");
			for(int k = 0; k< wordList.getLength(); k++){
				singleWords.add(wordList.item(k).getTextContent());
			}
			NodeList oldNames = featureElement.getElementsByTagName("OldName");
			if(oldNames.getLength() > 0){
				oldName = oldNames.item(0).getTextContent();
			}
			
			Feature newFeature = null;
			Node occurrencesNode = featureElement.getElementsByTagName("FeatureOccurrences").item(0);
			Element occurrencesElement = (Element)occurrencesNode;
			NodeList featureOccurrencesList = occurrencesElement.getElementsByTagName("FeatureOccurrence");
			for(int j = 0; j< featureOccurrencesList.getLength(); j++){
				Element occurenceElement = (Element) featureOccurrencesList.item(j);
				long start = Long.parseLong(occurenceElement.getElementsByTagName("StartOffset").item(0).getTextContent());
				long end = Long.parseLong(occurenceElement.getElementsByTagName("EndOffset").item(0).getTextContent());
				String containingSentence = occurenceElement.getElementsByTagName("ContainingSentence").item(0).getTextContent();
				String docName = occurenceElement.getElementsByTagName("DocumentName").item(0).getTextContent();
				String hierarchy = occurenceElement.getElementsByTagName("Hierarchy").item(0).getTextContent();
				String occurrenceName = occurenceElement.getElementsByTagName("OccurrenceName").item(0).getTextContent();
				
				FeatureOccurrence occurrenceObject = new FeatureOccurrence();
				occurrenceObject.setContainingSentence(containingSentence);
				occurrenceObject.setDocumentName(docName);
				occurrenceObject.setEndOffset(end);
				occurrenceObject.setStartOffset(start);
				occurrenceObject.setHierarchy(hierarchy);
				occurrenceObject.setOccurrenceName(occurrenceName);
				
				if(newFeature == null){
					newFeature = new Feature();
					newFeature.setLabel(name);
					newFeature.setSingleWords(singleWords);
					newFeature.setFeatureStem(featureStem);
					newFeature.setOldName(oldName);
					featureContainer.addFeatureFromXML(newFeature, occurrenceObject, isStemmingEnabled);
				}else{
					featureContainer.addFeatureOccurrenceFromXML(newFeature, occurrenceObject, isStemmingEnabled);
				}
			}
			newFeature.setOccurrence(newFeature.getFeatureOccurrences().size());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see featureMining.persistence.IPersistenceHandler#persist(java.lang.String, featureMining.feature.FeatureContainer)
	 * save to xml.
	 */
	@Override
	public void persist(String path, FeatureContainer featureContainer) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("FeatureContainer");
			doc.appendChild(rootElement);
			
			Element stemmingEnabled = doc.createElement("StemmingEnabled");
			String isEnabled = "";
			if(featureContainer.getOptions().isEnableStemming()){
				isEnabled = "True";
			}else{
				isEnabled = "False";
			}
			stemmingEnabled.appendChild(doc.createTextNode(isEnabled));
			
			Element features = doc.createElement("Features");
			Element evaluation = doc.createElement("Evaluation");
			
			rootElement.appendChild(stemmingEnabled);
			rootElement.appendChild(features);
			rootElement.appendChild(evaluation);
			
			Element linkNum = doc.createElement("LinkNum");
			linkNum.appendChild(doc.createTextNode(String.valueOf(featureContainer.getLinkNum())));
			features.appendChild(linkNum);
			
			List<Feature> sortedFeatures = new ArrayList<Feature>(featureContainer.getFeatureStorage().values());
			Collections.sort(sortedFeatures);
			
			for (Feature feature : sortedFeatures) {
				Element feat = this.createFeatureElement(feature);
				features.appendChild(feat);
			}
			
			Element featureFound = doc.createElement("FeaturesFound");
			featureFound.appendChild(doc.createTextNode(String.valueOf(rootWindow.getFeatureFound())));
			
			Element currentFeatureNum = doc.createElement("FeaturesKept");
			currentFeatureNum.appendChild(doc.createTextNode(String.valueOf(rootWindow.getCurrentFeatureNum())));
			
			Element precision = doc.createElement("Precision");
			float prec =  100.0f*((float)rootWindow.getCurrentFeatureNum() / (float) rootWindow.getFeatureFound());
			precision.appendChild(doc.createTextNode(String.valueOf((int)prec) +"%"));
			
			evaluation.appendChild(featureFound);
			evaluation.appendChild(currentFeatureNum);
			evaluation.appendChild(precision);
			
			writeToFile(path);
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Write to file.
	 *
	 * @param path the path
	 */
	private void writeToFile(String path) {
		// write the content into xml file
		if(path.equals("")){
			path = System.getProperty("user.dir") + "/xml/test.xml";
		}else if(!path.endsWith(".xml")){
			path += ".xml";
		}
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(path));
			transformer.setOutputProperty(OutputKeys.INDENT, "yes");
			transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
			transformer.transform(source, result);
			rootWindow.addInfoTextLine("\nxml saved to " + path);
			logger.info("File saved!");
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Creates a xml Representation of a single Feature.
	 *
	 * @param feature the feature
	 * @return the element
	 */
	private Element createFeatureElement(Feature feature) {
		Element feat = doc.createElement("Feature");
		
		Element name = doc.createElement("Label");
		name.appendChild(doc.createTextNode(feature.getLabel()));
		
		Element occurrence = doc.createElement("OccurrencesNum");
		occurrence.appendChild(doc.createTextNode(String.valueOf(feature.getOccurrence())));
		
		Element featureStem = doc.createElement("FeatureStem");
		featureStem.appendChild(doc.createTextNode(feature.getFeatureStem()));
		
		Element descriptions = doc.createElement("Description");
		for(String desc : feature.getDistinctDescription()){
			Element sentence = doc.createElement("Sentence");
			sentence.appendChild(doc.createTextNode(desc));
			descriptions.appendChild(sentence);
		}
		
		Element featureOccurrences = doc.createElement("FeatureOccurrences");
		for(FeatureOccurrence fOccurrence : feature.getFeatureOccurrences()){
			Element featureOccurrence = doc.createElement("FeatureOccurrence");
			Element occurrenceName = doc.createElement("OccurrenceName");
			occurrenceName.appendChild(doc.createTextNode(fOccurrence.getOccurrenceName()));
			Element docName = doc.createElement("DocumentName");
			docName.appendChild(doc.createTextNode(fOccurrence.getDocumentName()));
			Element start = doc.createElement("StartOffset");
			start.appendChild(doc.createTextNode(String.valueOf(fOccurrence.getStartOffset())));
			Element end = doc.createElement("EndOffset");
			end.appendChild(doc.createTextNode(String.valueOf(fOccurrence.getEndOffset())));
			Element containingSentence = doc.createElement("ContainingSentence");
			containingSentence.appendChild(doc.createTextNode(fOccurrence.getContainingSentence()));
			Element hierarchyElement = doc.createElement("Hierarchy");
			hierarchyElement.appendChild(doc.createTextNode(fOccurrence.getHierarchy()));
			
			featureOccurrence.appendChild(docName);
			featureOccurrence.appendChild(occurrenceName);
			featureOccurrence.appendChild(hierarchyElement);
			featureOccurrence.appendChild(start);
			featureOccurrence.appendChild(end);
			featureOccurrence.appendChild(containingSentence);
			featureOccurrences.appendChild(featureOccurrence);
		}
		
		Element singleWords = doc.createElement("SingleWords");
		for(String word : feature.getSingleWords()){
			Element w = doc.createElement("Word");
			w.appendChild(doc.createTextNode(word));
			singleWords.appendChild(w);
		}
		
		feat.appendChild(name);
		feat.appendChild(occurrence);
		feat.appendChild(featureStem);
		feat.appendChild(descriptions);
		feat.appendChild(featureOccurrences);
		feat.appendChild(singleWords);
		
		if(feature.getOldName() != null){
			Element oldName = doc.createElement("OldName");
			oldName.appendChild(doc.createTextNode(feature.getOldName()));
			feat.appendChild(oldName);
		}
		return feat;
	}
	
	/**
	 * Gets the singleton.
	 *
	 * @return the singleton
	 */
	public static XmlHandler getSingleton(){
		if(instance == null){
			instance = new XmlHandler(FeatureMining.getSingleton().getRootWindow());
		}
		return instance;
	}
	
	/**
	 * Gets the root window.
	 *
	 * @return the root window
	 */
	public RootFeatureWindow getRootWindow() {
		return rootWindow;
	}

	/**
	 * Sets the root window.
	 *
	 * @param rootWindow the new root window
	 */
	public void setRootWindow(RootFeatureWindow rootWindow) {
		this.rootWindow = rootWindow;
	}
}
