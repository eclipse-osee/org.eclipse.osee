/*
 * Created on Oct 28, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.server;

import org.eclipse.osee.framework.core.data.OseeServerInfo;

/**
 * @author Roberto E. Escobar
 */
public interface IApplicationServerLookup {

   OseeServerInfo searchBy(String version);

}
