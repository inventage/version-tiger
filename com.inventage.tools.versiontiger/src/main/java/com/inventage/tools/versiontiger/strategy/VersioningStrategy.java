package com.inventage.tools.versiontiger.strategy;

import com.inventage.tools.versiontiger.MavenVersion;

/**
 * Strategy interface for version changes on a version item.
 * 
 * @author nw
 */
public interface VersioningStrategy {

	/**
	 * Apply strategy represented by current class to version. Should return a
	 * new instance of a version.
	 * 
	 * @param version
	 *            the currently present version.
	 * @return a new instance of a version with the changes represented by this
	 *         class applied.
	 */
	MavenVersion apply(MavenVersion version);

	/**
	 * Indicates if the strategy supports the entry of additional information.
	 * 
	 * @return true if the strategy desires some information, or false
	 *         otherwise.
	 */
	boolean requiresDataInput();

	/**
	 * Method used for injecting data into the strategy.
	 * 
	 * @param data
	 *            some data.
	 * @return the same strategy, but enriched with the provided data.
	 */
	VersioningStrategy setData(Object data);
}
