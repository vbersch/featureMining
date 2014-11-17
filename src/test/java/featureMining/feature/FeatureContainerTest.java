package featureMining.feature;

import gate.Annotation;
import gate.Document;
import gate.FeatureMap;
import gate.Node;

import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class FeatureContainerTest {
	private static Feature mockedFeature1 = Mockito.mock(Feature.class);
	private static Feature mockedFeature2 = Mockito.mock(Feature.class);
	private FeatureContainer featureContainer;
	private static OptionTransferObject options;	
	
	@Before 
	public void setUp(){
		
		ArrayList<String> desc = new ArrayList<String>();
		desc.add("testDesc1");
		Mockito.when(mockedFeature1.getDistinctDescription()).thenReturn(desc);
		Mockito.when(mockedFeature1.getLabel()).thenReturn("Feature1");
		Mockito.when(mockedFeature1.getFeatureStem()).thenReturn("FeatureStem1");
		mockedFeature2.setLabel("Feature2");
		
		options = Mockito.mock(OptionTransferObject.class);
		
		featureContainer = new FeatureContainer();
		featureContainer.setOptions(options);
	}
	
	@Test(expected = NullPointerException.class)
	public void testGetDescriptionTextEmpty() {
		FeatureContainer featureContainer = new FeatureContainer();
		featureContainer.getDescriptionText("");	
	}
	
	@Test
	public void testGetDescriptionTextStemmingFalse() {
		FeatureOccurrence occurrence = new FeatureOccurrence();
		occurrence.setContainingSentence("descText");
		String expected = "testDesc1\n-------------------\n";
		featureContainer.addFeatureFromXML(mockedFeature1, occurrence, false);
		String actual = featureContainer.getDescriptionText(mockedFeature1.getLabel());
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testGetDescriptionTextStemmingTrue() {
		FeatureOccurrence occurrence = new FeatureOccurrence();
		featureContainer.addFeatureFromXML(mockedFeature1, occurrence, true);
		String expected = "testDesc1\n-------------------\n";
		String actual = featureContainer.getDescriptionText(mockedFeature1.getLabel());
		Assert.assertEquals(expected, actual);
	}
	

	@Test
	public void testAddFeatureOccurrenceFromXML() {
		String occurrenceName = "Occurrence1";
		FeatureOccurrence occurrence = Mockito.mock(FeatureOccurrence.class);
		FeatureOccurrence occurrence2 = Mockito.mock(FeatureOccurrence.class);
		Mockito.when(occurrence.getOccurrenceName()).thenReturn(occurrenceName);
		featureContainer.addFeatureFromXML(mockedFeature1, occurrence, true);
		featureContainer.addFeatureOccurrenceFromXML(mockedFeature1, occurrence2, true);
		Assert.assertEquals(mockedFeature1 , featureContainer.getFeatureDictionary().get(occurrence2.getOccurrenceName()));
	}
	
	@Test
	public void testAddFeatureStemmingEnabled() {
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
		
		String featureString = "First Second";
		String featureStem = "stem1 stem2";
		featureContainer.addFeature(featureAnnots, wholeSentence, gateDocument, "content");
		
		Assert.assertTrue(featureContainer.getFeatureDictionary().get(featureString) != null);
		Assert.assertTrue(featureContainer.getFeatureStorage().get(featureStem) != null);
	}
	
	@Test
	public void testAddFeatureStemmingDisabled() {
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
		
		Mockito.when(options.isEnableStemming()).thenReturn(false);
		
		String featureString = "First Second";
		featureContainer.addFeature(featureAnnots, wholeSentence, gateDocument, "content");
		
		Assert.assertTrue(featureContainer.getFeatureDictionary().get(featureString) != null);
		Assert.assertTrue(featureContainer.getFeatureStorage().get(featureString) != null);
	}
	

	@Test
	public void testGetDistinctLabels() {
		
		FeatureOccurrence mockedOccurrence1 = new FeatureOccurrence();
		FeatureOccurrence mockedOccurrence2 = new FeatureOccurrence();
		FeatureOccurrence mockedOccurrence3 = new FeatureOccurrence();
		
		mockedOccurrence1.setOccurrenceName("occurrence1");
		mockedOccurrence2.setOccurrenceName("occurrence2");
		mockedOccurrence3.setOccurrenceName("firstOccurrence");

		Feature feature = new Feature();
		feature.setLabel("firstOccurrence");
		
		feature.addFeatureOccurrence(mockedOccurrence1);
		feature.addFeatureOccurrence(mockedOccurrence2);
		feature.addFeatureOccurrence(mockedOccurrence3);
		
		featureContainer.getFeatureDictionary().put("firstOccurrence", feature);
		
		ArrayList<String> expected = new ArrayList<String>();
		expected.add("occurrence1");
		expected.add("occurrence2");
		
		Assert.assertEquals(expected, featureContainer.getDistinctLabels("firstOccurrence"));
		
	}

	@Test
	public void testAddOccurrence() { 
		Feature feature = new Feature();
		FeatureOccurrence newOccurrence = new FeatureOccurrence();
		newOccurrence.setContainingSentence("test");
		featureContainer.getFeatureDictionary().put("feature1", feature);
		int expected = feature.getOccurrence() + 1;
		featureContainer.addOccurrence("feature1", newOccurrence);
		int actual = feature.getOccurrence();
		Assert.assertEquals(expected, actual);
	}
	
	@Test
	public void testDeleteFeature() {  
		featureContainer.getFeatureDictionary().put("feature1", mockedFeature1);
		featureContainer.getFeatureStorage().put(mockedFeature1.getFeatureStem(), mockedFeature1);
		featureContainer.deleteFeature("feature1");
		Assert.assertNull(featureContainer.getFeatureDictionary().get("feature1"));
	}
	
	@Test
	public void testChangeFeature() {
		String oldName = "oldName";
		String newName = "newName";
		featureContainer.getFeatureDictionary().put(oldName, mockedFeature1);
		featureContainer.changeFeature(oldName, newName);
		Assert.assertNotNull(featureContainer.getFeatureDictionary().get(newName));
	}
}
