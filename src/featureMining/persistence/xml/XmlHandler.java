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

public class XmlHandler implements IPersistenceHandler{
	
	private static XmlHandler instance = null;
	private RootFeatureWindow rootWindow;
	private Document doc;
	
	@Override
	public void load(String path) {
		try{
			File xmlFile = new File(path);
			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
			doc = dBuilder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			FeatureContainer featureContainer = new FeatureContainer();
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

	private void fillFeatureContainer(NodeList nodeList, FeatureContainer featureContainer) {
		rootWindow.setCurrentFeatureNum(nodeList.getLength());
		for(int i = 0; i< nodeList.getLength(); i++){
			Node feature = nodeList.item(i);
			Element featureElement = (Element) feature;
			String name;
			String oldName = null;
			
			name = featureElement.getElementsByTagName("Name").item(0).getTextContent();
			
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
				
				if(newFeature == null){
					newFeature = new Feature(name, singleWords, containingSentence, docName, start, end, hierarchy);
				}else{
					newFeature.addFeatureOccurrence(containingSentence, docName, start, end, hierarchy);
				}
			}
			
			newFeature.setOldName(oldName);
			featureContainer.getFeatureStorage().put(name, newFeature);
		}
	}

	@Override
	public void persist(String path, FeatureContainer featureContainer) {
		DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
			doc = docBuilder.newDocument();
			Element rootElement = doc.createElement("FeatureContainer");
			doc.appendChild(rootElement);
			
			Element features = doc.createElement("Features");
			Element evaluation = doc.createElement("Evaluation");
			
			rootElement.appendChild(features);
			rootElement.appendChild(evaluation);
			
			Element linkNum = doc.createElement("LinkNum");
			linkNum.appendChild(doc.createTextNode(String.valueOf(featureContainer.getLinkNum())));
			features.appendChild(linkNum);
			
			List<Feature> sortedFeatures = new ArrayList<Feature>(featureContainer.getFeatureStorage().values());
			Collections.sort(sortedFeatures);
			
			for (Feature feature : sortedFeatures) {
				Element feat = this.createFeature(feature);
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
			System.out.println("File saved!");
		} catch (TransformerException e) {
			e.printStackTrace();
		}
	}

	private Element createFeature(Feature feature) {
		Element feat = doc.createElement("Feature");
		
		Element name = doc.createElement("Name");
		name.appendChild(doc.createTextNode(feature.getLabel()));
		
		Element occurrence = doc.createElement("OccurrencesNum");
		occurrence.appendChild(doc.createTextNode(String.valueOf(feature.getOccurrence())));
		
		Element descriptions = doc.createElement("Description");
		for(String desc : feature.getDistinctDescription()){
			Element sentence = doc.createElement("Sentence");
			sentence.appendChild(doc.createTextNode(desc));
			descriptions.appendChild(sentence);
		}
		
		Element featureOccurrences = doc.createElement("FeatureOccurrences");
		for(FeatureOccurrence fOccurrence : feature.getFeatureOccurrences()){
			Element featureOccurrence = doc.createElement("FeatureOccurrence");
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

	private XmlHandler(RootFeatureWindow rootWindow){
		this.rootWindow = rootWindow;
	};
	
	public static XmlHandler getSingleton(){
		if(instance == null){
			instance = new XmlHandler(FeatureMining.getSingleton().getRootWindow());
		}
		return instance;
	}
	

}
