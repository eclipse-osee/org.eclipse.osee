/*
 * Created on Nov 21, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.util;

import java.util.logging.Level;
import org.eclipse.nebula.widgets.xviewer.XViewerCells;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;

public class LogUtil {

   /**
    * Create exception string and return. Log if in test so SevereLoggingMonitor picks up.
    */
   public static String getCellExceptionString(Exception ex) {
      String errStr = XViewerCells.getCellExceptionString(ex);
      if (OseeProperties.isInTest()) {
         OseeLog.log(Activator.class, Level.SEVERE, errStr);
      }
      return errStr;
   }

}
