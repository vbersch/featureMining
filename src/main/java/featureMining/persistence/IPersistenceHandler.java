package featureMining.persistence;

import featureMining.feature.FeatureContainer;

/**
 * The Interface IPersistenceHandler.
 * Is a generic Interface, which provides loading and saving 
 * the Feature Mining Results to the File System.  
 */
public interface IPersistenceHandler {
	
	/**
	 * Load the Feature Mining Results from absolute Path.
	 *
	 * @param path the path
	 */
	public void load(String path);
	
	/**
	 * Persist a Feature Container to an absolute Path.
	 *
	 * @param path the path
	 * @param featureContainer the feature container
	 */
	public void persist(String path, FeatureContainer featureContainer);

}
