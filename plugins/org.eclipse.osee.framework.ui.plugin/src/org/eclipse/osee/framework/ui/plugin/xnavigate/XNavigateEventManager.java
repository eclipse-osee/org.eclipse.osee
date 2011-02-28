/*
 * Created on Feb 25, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.xnavigate;

import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.internal.OseePluginUiActivator;
import org.eclipse.osee.framework.ui.swt.Displays;

public class XNavigateEventManager {

   private static final Set<IXNavigateEventListener> listeners = new HashSet<IXNavigateEventListener>();

   public static void register(IXNavigateEventListener listener) {
      listeners.add(listener);
   }

   public static void itemRefreshed(final XNavigateItem item) {
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            for (IXNavigateEventListener listener : listeners) {
               try {
                  listener.refresh(item);
               } catch (Exception ex) {
                  OseeLog.log(OseePluginUiActivator.class, OseeLevel.SEVERE, ex);
               }
            }
         }
      });
   }
}
