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
package org.eclipse.osee.framework.ui.skynet.results.html;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.swt.program.Program;

/**
 * @author Donald G. Dunne
 */
public class XResultPage {

   public static enum Manipulations {
      NONE, //
      GUID_CMD_HYPER,
      // Replace all GUID strings with hyperlinks; ATS=<guid> opens Action editor
      // ART=<guid> opens Artifact editor, BOTH=<guid> allows either
      ERROR_RED, // Make all "Error" strings red
      WARNING_YELLOW, // Make all "Warning" strings yellow
      CONVERT_NEWLINES, // Convert all \n to <br>
      HTML_MANIPULATIONS, // Do all except converting newlines
      RAW_HTML, // Just display in simple html page
      ERROR_WARNING_HEADER, // Shows Errors: 4 Warnings: 23 count at top of page
      ALL,
      ERROR_WARNING_FROM_SEARCH // Performs search for error and warning count instead of using logError and logWarning counts
   };
   public enum HyperType {
      ATS,
      ART,
      BOTH
   };

   private String title;
   private String html;
   private final String id; // Used to add and remove to menu item
   private String manipulatedHtml;
   private Set<Manipulations> manipulations = new HashSet<>();
   private int numWarnings = Integer.MAX_VALUE;
   private int numErrors = Integer.MAX_VALUE;

   private final Pattern ATS_NAME_AND_GUID_REGEX = Pattern.compile("([A-Z]{3,4})=(.*?):(.*)");
   private final Pattern ATS_GUID_REGEX = Pattern.compile("([A-Z]{3,4})=(.*)");

   /**
    * Create and display result page with all Manipulations available
    */
   public XResultPage(String title, String text) {
      this(title, text, Manipulations.ALL);
   }

   /**
    * Create and display result page with given Manipulations
    *
    * @param title title of the page (include date/time due or something unique due to multi-page view of results)
    * @param html html to display (minus manipulations). this html MUST already handle new lines (eg
    * text.replaceAll("\n",AHTML.newLine())) or use the CONVERT_NEWLINES manipultion
    * @param manipulations manipulations desired for the input HTML
    */
   public XResultPage(String title, String html, Manipulations... manipulations) {
      this.title = title;
      this.html = html;
      id = GUID.create();
      for (Manipulations man : manipulations) {
         switch (man) {
            case ALL:
               this.manipulations.add(Manipulations.GUID_CMD_HYPER);
               this.manipulations.add(Manipulations.ERROR_RED);
               this.manipulations.add(Manipulations.CONVERT_NEWLINES);
               this.manipulations.add(Manipulations.WARNING_YELLOW);
               break;
            case HTML_MANIPULATIONS:
               this.manipulations.add(Manipulations.GUID_CMD_HYPER);
               this.manipulations.add(Manipulations.ERROR_RED);
               this.manipulations.add(Manipulations.WARNING_YELLOW);
               break;
            default:
               this.manipulations.add(man);
               break;
         }
      }
   }

   public int getNumWarnings() {
      if (numWarnings == Integer.MAX_VALUE) {
         if (manipulations.contains(Manipulations.WARNING_YELLOW)) {
            numWarnings = Lib.numOccurances(html, "Warning:");
         }
         return 0;
      } else {
         return numWarnings;
      }
   }

   public int getNumErrors() {
      if (numErrors == Integer.MAX_VALUE) {
         if (manipulations.contains(Manipulations.WARNING_YELLOW)) {
            numErrors = Lib.numOccurances(html, "Error:");
         }
         return 0;
      } else {
         return numErrors;
      }
   }

   public String getId() {
      return id;
   }

   public String getErrorWarningHtml() {
      int numErrors = getNumErrors();
      int numWarnings = getNumWarnings();
      return String.format("%s <b>Errors</b>: %d  <b>Warnings</b>: %d%s<br/><br/>",
         getErrorWarningColorPre(numErrors, numWarnings), numErrors, numWarnings,
         getErrorWarningColorPost(numErrors, numWarnings));
   }

   private String getErrorWarningColorPre(int numErrors, int numWarnings) {
      if (numErrors > 0) {
         return "<font color=\"red\">";
      } else if (numWarnings > 0) {
         return "<font color=\"yellow\">";
      }
      return "";
   }

   private String getErrorWarningColorPost(int numErrors, int numWarnings) {
      if (numErrors > 0 || numWarnings > 0) {
         return "</font>";
      }
      return "";
   }

   public String getManipulatedHtml() {
      return getManipulatedHtml(manipulations);
   }

   public String getManipulatedHtml(Collection<Manipulations> manipulations) {
      if (manipulatedHtml == null) {
         String str =
            (manipulations.contains(Manipulations.ERROR_WARNING_HEADER) ? getErrorWarningHtml() : "") + getText();
         if (manipulations.contains(Manipulations.RAW_HTML)) {
            str = AHTML.simplePage(str);
         } else {
            if (manipulations.contains(Manipulations.CONVERT_NEWLINES)) {
               str = str.replaceAll("\n", AHTML.newline());
            }
            if (manipulations.contains(Manipulations.GUID_CMD_HYPER)) {
               // System.err.println("match " + line);
               // Match getText so it doesn't mess up replace
               // Retireve all ATS=WPN_PAGE:HSRID matches
               Matcher m = ATS_NAME_AND_GUID_REGEX.matcher(str);
               Set<String> cmdNameGuids = new HashSet<>();
               while (m.find()) {
                  cmdNameGuids.add(m.group());
               }
               // Retrieve all ATS=Name:HRSID matches and replace with hyperlinking
               for (String cmdNameGuid : cmdNameGuids) {
                  String value = cmdNameGuid;
                  value = value.replaceAll("^.*?=", "");
                  String name = value;
                  name = name.replaceAll(":.*$", "");
                  String guid = value;
                  guid = guid.replaceAll("^.*:", "");
                  if (cmdNameGuid.startsWith(HyperType.BOTH.name())) {
                     String replaceStr = guid + " (" + XResultDataUI.getHyperlinkForAction("ATS-" + name, guid);
                     replaceStr += "  " + XResultDataUI.getHyperlinkForArtifactEditor("AE-" + name, guid);
                     replaceStr += ")";
                     str = str.replaceAll(cmdNameGuid, replaceStr);
                  } else if (cmdNameGuid.startsWith(HyperType.ATS.name())) {
                     str = str.replaceAll(cmdNameGuid, XResultDataUI.getHyperlinkForAction(name, guid));
                  } else if (cmdNameGuid.startsWith(HyperType.ART.name())) {
                     str = str.replaceAll(cmdNameGuid, XResultDataUI.getHyperlinkForArtifactEditor(name, guid));
                  }
               }
               // Retrieve all ATS=GUID matches and replace with hyperlinking
               m = ATS_GUID_REGEX.matcher(str);
               Set<String> cmdGuids = new HashSet<>();
               while (m.find()) {
                  cmdGuids.add(m.group());
               }
               for (String cmdGuid : cmdGuids) {
                  String guid = cmdGuid;
                  guid = guid.replaceAll("^.*?=", "");
                  if (cmdGuid.startsWith(HyperType.BOTH.name())) {
                     String replaceStr = guid + " (" + XResultDataUI.getHyperlinkForAction("ATS", guid);
                     replaceStr += "  " + XResultDataUI.getHyperlinkForArtifactEditor("AE", guid);
                     replaceStr += ")";
                     str = str.replaceAll(cmdGuid, replaceStr);
                  } else if (cmdGuid.startsWith(HyperType.ATS.name())) {
                     str = str.replaceAll(cmdGuid, XResultDataUI.getHyperlinkForAction(guid, guid));
                  } else if (cmdGuid.startsWith(HyperType.ART.name())) {
                     str = str.replaceAll(cmdGuid, XResultDataUI.getHyperlinkForArtifactEditor(guid, guid));
                  }
               }
            }
            if (manipulations.contains(Manipulations.ERROR_RED)) {
               str = str.replaceAll("Error:", AHTML.color("red", "Error:"));
            }
            if (manipulations.contains(Manipulations.WARNING_YELLOW)) {
               str = str.replaceAll("Warning:", AHTML.color("orange", "Warning:"));
            }
         }
         manipulatedHtml = str;
      }
      return manipulatedHtml;
   }

   public String getText() {
      return html;
   }

   public void setHtml(String html) {
      this.html = html;
   }

   public String getTitle() {
      return title;
   }

   public void setTitle(String title) {
      this.title = title;
   }

   public Set<Manipulations> getManipulations() {
      return manipulations;
   }

   public void setManipulations(Set<Manipulations> manipulations) {
      this.manipulations = manipulations;
   }

   public void handleExport() {
      Dialogs.exportHtmlTableDialog(title, html, true);
   }

   public void handleExportExcel() {
      Dialogs.exportHtmlExcelTableDialog(title, html, true);
   }

   public void saveToFile() {
      saveToFile(null);
   }

   public void saveToFile(String filename) {
      if (manipulatedHtml == null) {
         getManipulatedHtml();
      }
      if (filename == null) {
         Dialogs.saveHtmlDialog(manipulatedHtml, true);
      } else {
         try {
            Lib.writeStringToFile(manipulatedHtml, new File(filename));
         } catch (IOException ex) {
            OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
         Program.launch(filename);
      }
   }

   public void setNumErrors(int numErrors) {
      this.numErrors = numErrors;
   }

   public void setNumWarnings(int numWarnings) {
      this.numWarnings = numWarnings;
   }

}
