package featureMining.persistence.xml;

import java.util.ArrayList;

import org.junit.Test;
import org.mockito.Mockito;

import featureMining.feature.FeatureContainer;
import featureMining.feature.OptionTransferObject;
import featureMining.ui.RootFeatureWindow;
import gate.Annotation;
import gate.Document;
import gate.FeatureMap;
import gate.Node;

public class XmlHandlerText {

	@Test
	public void testPersistAndLoad() {
		String path = System.getProperty("user.dir") + "/featureMining/test/test.xml";
		FeatureContainer featureContainer = createFeatureContainer();
		
		RootFeatureWindow rootWindow = Mockito.mock(RootFeatureWindow.class);
		
		Mockito.when(rootWindow.getFeatureFound()).thenReturn(1);
		Mockito.when(rootWindow.getCurrentFeatureNum()).thenReturn(1);
		
		XmlHandler xmlHandler = XmlHandler.getSingleton();
		xmlHandler.setRootWindow(rootWindow);
		
		xmlHandler.persist(path, featureContainer);
		xmlHandler.load(path);
		
	}

	private FeatureContainer createFeatureContainer() {
		OptionTransferObject options = Mockito.mock(OptionTransferObject.class);
		FeatureContainer featureContainer =  new FeatureContainer();
		featureContainer.setOptions(options);
		Annotation mockedAnnot1 = Mockito.mock(Annotation.class);
		Annotation mockedAnnot2 = Mockito.mock(Annotation.class);
		FeatureMap mockedMap1 = Mockito.mock(FeatureMap.class);
		FeatureMap mockedMap2 = Mockito.mock(FeatureMap.class);
		Node startNode = Mockito.mock(Node.class);
		Node endNode = Mockito.mock(Node.class);
		
		String wholeSentence = "First Second Third Fourth.";
		
		Mockito.when(startNode.getOffset()).thenReturn((long) 0);
		Mockito.when(endNode.getOffset()).thenReturn((long) 11);
		
		Mockito.when(mockedAnnot1.getFeatures()).thenReturn(mockedMap1);
		Mockito.when(mockedMap1.get("string")).thenReturn("First");
		Mockito.when(mockedMap1.get("stem")).thenReturn("stem1");
		Mockito.when(mockedAnnot1.getStartNode()).thenReturn(startNode);
		
		Mockito.when(mockedAnnot2.getFeatures()).thenReturn(mockedMap2);
		Mockito.when(mockedMap2.get("string")).thenReturn("Second");
		Mockito.when(mockedMap2.get("stem")).thenReturn("stem2");
		Mockito.when(mockedAnnot2.getEndNode()).thenReturn(endNode);
		
		Document gateDocument = Mockito.mock(Document.class);
		Mockito.when(gateDocument.getName()).thenReturn("doc1");
		
		ArrayList<Annotation> featureAnnots = new ArrayList<Annotation>();
		featureAnnots.add(mockedAnnot1);
		featureAnnots.add(mockedAnnot2);
		
		Mockito.when(options.isEnableStemming()).thenReturn(true);
		featureContainer.addFeature(featureAnnots, wholeSentence, gateDocument, "content");
		
		return featureContainer;
		
	}
}
