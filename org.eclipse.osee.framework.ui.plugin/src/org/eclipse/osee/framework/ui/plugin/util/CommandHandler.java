/*
 * Created on Nov 8, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.plugin.util;

import java.util.logging.Level;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.OseePluginUiActivator;

/**
 * @author Ryan D. Brooks
 */
public abstract class CommandHandler extends AbstractHandler {

   /* (non-Javadoc)
    * @see org.eclipse.core.commands.AbstractHandler#isEnabled()
    */
   @Override
   public boolean isEnabled() {
      try {
         return isEnabledWithException();
      } catch (OseeCoreException ex) {
         OseeLog.log(OseePluginUiActivator.class, Level.SEVERE, ex);
         return false;
      }
   }

   public abstract boolean isEnabledWithException() throws OseeCoreException;
}
