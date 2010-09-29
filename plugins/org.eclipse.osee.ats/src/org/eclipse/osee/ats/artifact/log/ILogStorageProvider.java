/*
 * Created on Sep 29, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.artifact.log;

import org.eclipse.osee.framework.ui.plugin.util.Result;

public interface ILogStorageProvider {

   String getLogXml();

   Result saveLogXml(String xml);

   String getLogTitle();

   String getLogId();
}
