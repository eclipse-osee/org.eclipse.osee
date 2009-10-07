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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.CountingMap;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.ui.plugin.util.Displays;
import org.eclipse.osee.framework.ui.skynet.SkynetGuiPlugin;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultBrowserHyperCmd;
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
   CountingMap<Type> count = new CountingMap<Type>();

   public static void runExample() {
      runExample("This is my report title");
   }

   public static void runExample(String title) {
      try {
         XResultData rd = new XResultData();
         rd.log("This is just a normal log message");
         rd.logWarning("This is a warning");
         rd.logError("This is an error");

         rd.log("\n\nExample of hyperlinked hrid: " + XResultData.getHyperlink(UserManager.getUser()));

         rd.log("Example of hyperlinked artifact different hyperlink string: " + XResultData.getHyperlink(
               "Different string", UserManager.getUser()));

         rd.log("Example of hyperlinked hrid on another branch: " + getHyperlink(
               UserManager.getUser().getHumanReadableId(), UserManager.getUser().getHumanReadableId(),
               BranchManager.getCommonBranch().getBranchId()));
         rd.addRaw(AHTML.newline());
         rd.addRaw("Example of hyperlink that opens external browser " + getHyperlinkUrlExternal("Google",
               "http://www.google.com") + AHTML.newline());
         rd.addRaw("Example of hyperlink that opens internal browser " + getHyperlinkUrlInternal("Google",
               "http://www.google.com") + AHTML.newline());

         rd.log("\n\nHere is a nice table");
         rd.addRaw(AHTML.beginMultiColumnTable(95, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Title", "Status"}));
         for (int x = 0; x < 3; x++)
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {"Type " + x, "Title " + x, x + ""}));
         rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {"Error / Warning in table ", "Error: this is error",
               "Warning: this is warning"}));
         rd.addRaw(AHTML.endMultiColumnTable());
         rd.report("This is my report title");
      } catch (OseeCoreException ex) {
         OseeLog.log(SkynetGuiPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public void addRaw(String str) {
      sb.append(str);
   }

   public void reportSevereLoggingMonitor(SevereLoggingMonitor monitorLog) {
      List<IHealthStatus> stats = monitorLog.getAllLogs();
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
      count.put(type);
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

   /* 
    * Creates hyperlink using hrid as name.  Default editor will open.
    */
   public static String getHyperlink(Artifact art) {
      return getHyperlink(art.getHumanReadableId(), art.getHumanReadableId(), art.getBranch().getBranchId());
   }

   /* 
    * Creates hyperlink using name.  Default editor will open.
    */
   public static String getHyperlink(String name, Artifact art) {
      return getHyperlink(name, art.getHumanReadableId(), art.getBranch().getBranchId());
   }

   /* 
    * Creates hyperlink using name.  Default editor will open hrid for branchId given
    */
   public static String getHyperlink(String name, String hrid, int branchId) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openArtifctBranch,
            hrid + "(" + branchId + ")"), name);
   }

   public static String getHyperlinkUrlExternal(String name, String url) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.browserExternal, url),
            name);
   }

   public static String getHyperlinkUrlInternal(String name, String url) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.browserInternal, url),
            name);
   }

   public static String getHyperlinkForArtifactEditor(String name, String hrid) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openArtifactEditor, hrid),
            name);
   }

   public static String getHyperlinkForAction(String name, String hrid) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openAction, hrid), name);
   }

   public static String getHyperlinkForAction(Artifact artifact) {
      return getHyperlinkForAction(artifact.getHumanReadableId(), artifact);
   }

   public static String getHyperlinkForAction(String name, Artifact art) {
      return AHTML.getHyperlink(
            XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openAction, art.getGuid()), name);
   }

   private int getCount(Type type) {
      return count.get(type);
   }

   public int getNumErrors() {
      return getCount(Type.Severe);
   }

   public int getNumWarnings() {
      return getCount(Type.Warning);
   }
}
