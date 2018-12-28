/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.ui.skynet.results;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultBrowserHyperCmd;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.DateUtil;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.html.XResultPage;

/**
 * @author Donald G. Dunne
 */
public class XResultDataUI {

   /*
    * Creates hyperlink using name. Default editor will open id for branchUuid given
    */
   public static String getHyperlink(String name, String id, BranchId branch) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openArtifactBranch,
         id + "(" + branch.getId() + ")"), name);
   }

   public static String getHyperlink(String name, ArtifactId id, BranchId branch) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openArtifactBranch,
         id.getIdString() + "(" + branch.getId() + ")"), name);
   }

   public static String getHyperlinkUrlExternal(String name, String url) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.browserExternal, url),
         name);
   }

   public static String getHyperlinkUrlInternal(String name, String url) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.browserInternal, url),
         name);
   }

   public static String getHyperlinkForArtifactEditor(String name, String id) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openArtifactEditor, id),
         name);
   }

   public static String getHyperlinkForAction(String name, String id) {
      return AHTML.getHyperlink(XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openAction, id), name);
   }

   public static String getHyperlinkForAction(String name, Artifact art) {
      return AHTML.getHyperlink(
         XResultBrowserHyperCmd.getHyperCmdStr(XResultBrowserHyperCmd.openAction, art.getIdString()), name);
   }

   /*
    * Creates hyperlink using id as name. Default editor will open.
    */
   public static String getHyperlink(Artifact art) {
      return getHyperlink(art.getIdString(), art, art.getBranch());
   }

   public static String getHyperlinkWithName(Artifact art) {
      return getHyperlink(art.toStringWithId(), art, art.getBranch());
   }

   /*
    * Creates hyperlink using name. Default editor will open.
    */
   public static String getHyperlink(String name, Artifact art) {
      return getHyperlink(name, art, art.getBranch());
   }

   public static String report(XResultData resultData, final String title) {
      return report(resultData, title, Manipulations.ALL);
   }

   public static String report(XResultData resultData, final String title, final Manipulations... manipulations) {
      final String html = getReport(resultData, title, manipulations).getManipulatedHtml();
      ResultsEditor.open("Results", title, html);
      return html;
   }

   public static XResultPage getReport(XResultData resultData, final String title) {
      return getReport(resultData, title, Manipulations.ALL);
   }

   public static XResultPage getReport(XResultData resultData, final String title, Manipulations... manipulations) {
      XResultPage page = new XResultPage(title + " - " + DateUtil.getMMDDYYHHMM(),
         resultData.toString().equals("") ? "Nothing Logged" : resultData.toString(), manipulations);
      if (isErrorWarningCountFromSearch(manipulations)) {
         page.setNumErrors(resultData.getNumErrorsViaSearch());
         page.setNumWarnings(resultData.getNumWarningsViaSearch());
      } else {
         page.setNumErrors(resultData.getNumErrors());
         page.setNumWarnings(resultData.getNumWarnings());
      }
      return page;
   }

   private static boolean isErrorWarningCountFromSearch(Manipulations[] manipulations) {
      if (manipulations != null) {
         for (Manipulations manip : manipulations) {
            if (manip == Manipulations.ERROR_WARNING_FROM_SEARCH) {
               return true;
            }
         }
      }
      return false;
   }

   public static void runExample() {
      runExample("This is my report title");
   }

   public static void runExample(String title) {
      try {
         XResultData rd = new XResultData();
         rd.log("This is just a normal log message");
         rd.warning("This is a warning");
         rd.error("This is an error");

         rd.log("\n\nExample of hyperlinked id: " + getHyperlink(UserManager.getUser()));

         rd.log("Example of hyperlinked artifact different hyperlink string: " + getHyperlink("Different string",
            UserManager.getUser()));

         rd.log("Example of hyperlinked id on another branch: " + getHyperlink(UserManager.getUser().getIdString(),
            UserManager.getUser(), COMMON));
         rd.addRaw(AHTML.newline());
         rd.addRaw("Example of hyperlink that opens external browser " + getHyperlinkUrlExternal("Google",
            "http://www.google.com") + AHTML.newline());
         rd.addRaw("Example of hyperlink that opens internal browser " + getHyperlinkUrlInternal("Google",
            "http://www.google.com") + AHTML.newline());

         rd.log("\n\nHere is a nice table");
         rd.addRaw(AHTML.beginMultiColumnTable(95, 1));
         rd.addRaw(AHTML.addHeaderRowMultiColumnTable(new String[] {"Type", "Title", "Status"}));
         for (int x = 0; x < 3; x++) {
            rd.addRaw(AHTML.addRowMultiColumnTable(new String[] {"Type " + x, "Title " + x, x + ""}));
         }
         rd.addRaw(AHTML.addRowMultiColumnTable(
            new String[] {"Error / Warning in table ", "Error: this is error", "Warning: this is warning"}));
         rd.addRaw(AHTML.endMultiColumnTable());
         report(rd, "This is my report title");
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   public static void reportSevereLoggingMonitor(SevereLoggingMonitor monitorLog, XResultData rd) {
      List<IHealthStatus> stats = monitorLog.getAllLogs();
      for (IHealthStatus stat : new ArrayList<IHealthStatus>(stats)) {
         if (stat.getException() != null) {
            rd.error("Exception: " + Lib.exceptionToString(stat.getException()));
         }
      }
   }

}
