/*
 * Created on Jan 11, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.widgets.xviewer;

import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;

/**
 * @author Donald G. Dunne
 */
public class XViewerCells {

   public static String getCellExceptionString(String message) {
      return CELL_ERROR_PREFIX + " - " + message;
   }

   public static String getCellExceptionString(Exception ex) {
      OSEELog.logException(SkynetGuiPlugin.class, ex, false);
      return CELL_ERROR_PREFIX + " - " + ex.getLocalizedMessage();
   }

   public static final String CELL_ERROR_PREFIX = "!Error";

}
