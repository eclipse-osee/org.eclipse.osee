/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.logging;


/**
 * @author afinkbei
 *
 */
public interface IStatusListener {
	void onStatus(IHealthStatus status);
}
