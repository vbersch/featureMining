package featureMining.feature;

import org.junit.Assert;
import org.junit.Test;

public class FeatureTest {

	@Test
	public void testUpdateName() {
		String newLabel = "newLabel";
		Feature feature = new Feature();
		feature.setLabel("oldLabel");
		feature.updateName(newLabel);
		Assert.assertEquals(newLabel, feature.getLabel());
	}

	@Test
	public void testCompareTo() {
		Feature feature1 = new Feature();
		Feature feature2 = new Feature();
		
		feature1.setOccurrence(3);
		feature2.setOccurrence(2);
		Assert.assertTrue(feature1.compareTo(feature2) < 0);
	}

	@Test
	public void testAddFeatureOccurrence() {
		Feature feature = new Feature();
		int expected = feature.getOccurrence() + 2;
		FeatureOccurrence fOccurrence1 = new FeatureOccurrence();
		FeatureOccurrence fOccurrence2 = new FeatureOccurrence();
		fOccurrence1.setContainingSentence("containingSentence");
		fOccurrence2.setContainingSentence("containingSentence");
		feature.addFeatureOccurrence(fOccurrence1);
		feature.addFeatureOccurrence(fOccurrence2);
		Assert.assertEquals(expected, feature.getOccurrence());
	}

}
