/*
 * Created on Jun 30, 2017
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet.results.html;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.osee.framework.core.util.result.HyperType;
import org.eclipse.osee.framework.core.util.result.Manipulations;
import org.eclipse.osee.framework.core.util.result.XResultPageBase;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.swt.program.Program;

public class XResultPage extends XResultPageBase {

   private final Pattern ATS_NAME_AND_GUID_REGEX = Pattern.compile("([A-Z]{3,4})=(.*?):(.*)");
   private final Pattern ATS_GUID_REGEX = Pattern.compile("([A-Z]{3,4})=(.*)");

   public XResultPage(String title, String text) {
      super(title, text);
   }

   public XResultPage(String title, String html, Manipulations... manipulations) {
      super(title, html, manipulations);
   }

   @Override
   public String handleGuidCmdHyper(String str) {
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
      return str;
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
            OseeLog.log(org.eclipse.osee.framework.ui.skynet.internal.Activator.class, OseeLevel.SEVERE_POPUP, ex);
         }
         Program.launch(filename);
      }
   }

}
