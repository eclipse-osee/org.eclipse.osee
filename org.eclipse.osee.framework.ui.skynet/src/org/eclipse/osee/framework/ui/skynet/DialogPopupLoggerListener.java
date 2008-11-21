/*
 * Created on Jun 25, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet;

import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.swt.widgets.Display;

/**
 * @author b1528444
 */
public class DialogPopupLoggerListener implements ILoggerListener {

   public static final String SPLIT = "##split##";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.logging.ILoggerListener#log(java.lang.String, java.lang.String, java.util.logging.Level, java.lang.String, java.lang.Throwable)
    */
   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
      if (level == OseeLevel.SEVERE_POPUP) {
         String[] msgs = message.split(SPLIT);
         String title = "OSEE Error";
         String messageText = message;
         String reasonMessage = "";
         if (msgs.length == 2) {
            title = msgs[0];
            messageText = msgs[1];
         } else if (msgs.length == 3) {
            title = msgs[0];
            messageText = msgs[1];
            reasonMessage = msgs[2];
         }
         final IStatus status = new Status(Status.ERROR, loggerName, reasonMessage, th);
         final String realTitle = title;
         final String realMessageText = messageText;
         Display.getDefault().syncExec(new Runnable() {
            @Override
            public void run() {
               ErrorDialog.openError(Display.getDefault().getActiveShell(), realTitle, realMessageText, status);
            }
         });
      }
   }
}
