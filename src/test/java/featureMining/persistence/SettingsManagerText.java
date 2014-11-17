package featureMining.persistence;

import java.io.File;
import java.util.ArrayList;

import org.junit.Assert;
import org.junit.Test;

import featureMining.feature.OptionTransferObject;

public class SettingsManagerText {

	
	@Test
	public void testRestoreOptions() {
		SettingsManager.fileName = System.getProperty("user.dir")+ "/featureMining/test/testRestoreOptions.xml";
		OptionTransferObject options = SettingsManager.restoreOptions();
		
		Assert.assertEquals("testUrl", options.getBaseUrl());
		Assert.assertEquals("testDocType", options.getDocumentationType());
		Assert.assertTrue(options.isDomainSpecific());
		Assert.assertTrue(options.isDomainSpecific());
		Assert.assertEquals("testHostName", options.getHostName());
		Assert.assertEquals("testPreprocessingName", options.getPreprocessingName());
		Assert.assertEquals(4, options.getThreadNum());
	}

	@Test
	public void testSaveOptions() {
		SettingsManager.fileName = System.getProperty("user.dir")+ "/featureMining/test/testSaveOptions.xml";
		OptionTransferObject options = SettingsManager.getOptions();
		options.setBaseUrl("testUrl");
		options.setDocumentationType("testDocType");
		options.setDomainSpecific(true);
		options.setEnableStemming(true);
		options.setHostName("testHostName");
		options.setPreprocessingName("testPreprocessingName");
		options.setThreadNum(4);
		SettingsManager.saveOptions();
		File file = new File(SettingsManager.fileName);
		Assert.assertTrue(file.exists());
		
	}

	@Test
	public void testLoadBlacklists() {
		OptionTransferObject options = SettingsManager.getOptions();
		options.setSentenceBlacklistPath(System.getProperty("user.dir")+ "/featureMining/test/sentenceBlacklistTest.txt");
		options.setFeatureBlacklistPath(System.getProperty("user.dir")+ "/featureMining/test/featureBlacklistTest.txt");
		ArrayList<String> expectedFeatures = new ArrayList<String>();
		ArrayList<String> expectedSentences = new ArrayList<String>();
		expectedFeatures.add("hello");
		expectedFeatures.add("Feature");
		expectedSentences.add("hello");
		expectedSentences.add("Sentence");
		
		SettingsManager.loadBlacklists();
		
		Assert.assertEquals(expectedFeatures, options.getFeatureBlacklist());
		Assert.assertEquals(expectedSentences, options.getSentenceBlacklist());
	}

}
