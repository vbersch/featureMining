package featureMining.persistence.datastore;

import java.io.File;

import org.junit.Test;
import org.mockito.Mockito;

public class DataStoreHelperTest {

	@Test
	public void testCheckForExistingDirNotExists() {
		File mockedFolder = Mockito.mock(File.class);
		Mockito.when(mockedFolder.exists()).thenReturn(false);
		Mockito.when(mockedFolder.mkdirs()).thenReturn(true);
		DataStoreHelper.checkForExistingDir(mockedFolder);
	}
}
