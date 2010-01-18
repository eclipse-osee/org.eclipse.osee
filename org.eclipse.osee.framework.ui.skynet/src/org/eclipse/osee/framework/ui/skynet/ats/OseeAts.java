/*
 * Created on Jan 15, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.ats;

import java.util.logging.Level;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;

public final class OseeAts {

   private static IOseeAtsService oseeAtsInstance;

   private OseeAts() {
   }

   public static IOseeAtsService getInstance() {
      try {
         if (Platform.getExtensionRegistry() == null) {
            return null;
         }
         oseeAtsInstance = SkynetGuiPlugin.getInstance().getOseeAtsService();
      } catch (Exception ex) {
         OseeLog.log(SkynetGuiPlugin.class, Level.SEVERE, ex);
         AWorkbench.popup("ERROR", ex.getLocalizedMessage());
      }
      return oseeAtsInstance;
   }
}
