package featureMining.persistence;

import featureMining.feature.FeatureContainer;

public interface IPersistenceHandler {
	
	public void load(String path);
	public void persist(String path, FeatureContainer featureContainer);

}
