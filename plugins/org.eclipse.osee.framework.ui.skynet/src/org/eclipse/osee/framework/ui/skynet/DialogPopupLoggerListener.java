/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.ui.skynet;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.osee.framework.logging.ILoggerListener;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.ui.swt.Displays;

/**
 * @author Andrew M. Finkbeiner
 */
public class DialogPopupLoggerListener implements ILoggerListener {

   @Override
   public void log(String loggerName, Level level, String message, Throwable th) {
      if (level == OseeLevel.SEVERE_POPUP) {
         final String title = "OSEE Error";
         final IStatus status;
         final String realMessageText;
         if (th != null) {
            List<IStatus> exc = new ArrayList<>();
            exceptionToString(true, loggerName, th, exc);
            status =
               new MultiStatus(loggerName, IStatus.ERROR, exc.toArray(new IStatus[exc.size()]), th.getMessage(), th);
            realMessageText = message;
         } else {
            status = new Status(IStatus.ERROR, loggerName, -20, message, th);
            realMessageText = null;
         }
         Displays.pendInDisplayThread(new Runnable() {
            @Override
            public void run() {
               ErrorDialog.openError(Displays.getActiveShell(), title, realMessageText, status);
            }
         });
      }
   }

   private static void exceptionToString(boolean firstTime, String loggerName, Throwable ex, List<IStatus> exc) {
      if (ex == null) {
         return;
      }
      if (!firstTime) {
         exc.add(new Status(IStatus.ERROR, loggerName, ex.getMessage()));
      }
      StackTraceElement st[] = ex.getStackTrace();
      for (int i = 0; i < st.length; i++) {
         StackTraceElement ste = st[i];
         exc.add(new Status(IStatus.ERROR, loggerName, ste.toString()));
      }
      Throwable cause = ex.getCause();
      if (cause != null) {
         exc.add(new Status(IStatus.ERROR, loggerName, "   caused by "));
         exceptionToString(false, loggerName, cause, exc);
      }
   }

}
