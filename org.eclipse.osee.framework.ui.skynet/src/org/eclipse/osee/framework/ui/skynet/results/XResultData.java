/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.skynet.results;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage.Manipulations;
import org.eclipse.osee.framework.ui.skynet.widgets.XDate;

/**
 * Used to log Info, Warning and Errors to multiple locations (logger, stderr/out and XResultView). Upon completion, a
 * call to report(title) will open results in the ResultsView
 * 
 * @author Donald G. Dunne
 */
public class XResultData {

   StringBuffer sb = new StringBuffer();
   private static enum Type {
      Severe, Warning, Info;
   }

   public static void runExample() {
      XResultData rd = new XResultData();
      rd.log("This is just a normal log message");
      rd.logWarning("This is a warning");
      rd.logError("This is an error");

      rd.log("Here is a nice table");
      rd.addRaw(AHTML.beginMultiColumnTable(95, 1));
      rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Title", "Status"}));
      for (int x = 0; x < 3; x++)
         rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {"Type " + x, "Title " + x, x + ""}));
      rd.addRaw(AHTML.endMultiColumnTable());
      try {
         rd.report("This is my report title");
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void addRaw(String str) {
      sb.append(str);
   }

   public void reportSevereLoggingMonitor(SevereLoggingMonitor monitorLog) {
      List<IHealthStatus> stats = monitorLog.getSevereLogs();
      for (IHealthStatus stat : new CopyOnWriteArrayList<IHealthStatus>(stats)) {
         if (stat.getException() != null) {
            logError("Exception: " + Lib.exceptionToString(stat.getException()));
         }
      }
   }

   public void log(IProgressMonitor monitor, String str) {
      log(str);
      if (monitor != null) monitor.setTaskName(str);
   }

   public void log(String str) {
      logStr(Type.Info, str + "\n", null);
   }

   public void log(String str, IProgressMonitor monitor) {
      logStr(Type.Info, str + "\n", monitor);
   }

   public void logError(String str) {
      logStr(Type.Severe, str + "\n", null);
   }

   public void logWarning(String str) {
      logStr(Type.Warning, str + "\n", null);
   }

   public boolean isEmpty() {
      return toString().equals("");
   }

   public void logStr(Type type, final String str, final IProgressMonitor monitor) {
      String resultStr = "";
      if (type == Type.Warning)
         resultStr = "Warning: " + str;
      else if (type == Type.Severe)
         resultStr = "Error: " + str;
      else
         resultStr = str;
      sb.append(resultStr);
      OseeLog.log(SkynetGuiPlugin.class, Level.parse(type.name().toUpperCase()), resultStr);
      if (monitor != null) {
         Displays.ensureInDisplayThread(new Runnable() {
            public void run() {
               monitor.subTask(str);
            }
         });
      }
   }

   @Override
   public String toString() {
      return sb.toString();
   }

   public void report(final String title) throws OseeCoreException {
      report(title, Manipulations.ALL);
   }

   public void report(final String title, final Manipulations... manipulations) throws OseeCoreException {
      final String html = getReport(title, manipulations).getManipulatedHtml();
      try {
         ResultsEditor.open("Results", title, html);
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public XResultPage getReport(final String title) {
      return getReport(title, Manipulations.ALL);
   }

   public XResultPage getReport(final String title, Manipulations... manipulations) {
      return new XResultPage(title + " - " + XDate.getDateNow(XDate.MMDDYYHHMM),
            (sb.toString().equals("") ? "Nothing Logged" : sb.toString()), manipulations);
   }

}
