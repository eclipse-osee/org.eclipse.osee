/*
 * Created on Mar 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.logging;

import java.util.logging.Level;


/**
 * @author afinkbei
 *
 */
public interface IHealthStatus {
	public Throwable getException();
	public String getMessage();
	public String getPlugin();
	public Level getLevel();
}
