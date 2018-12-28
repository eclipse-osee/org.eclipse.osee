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
import org.eclipse.osee.framework.core.util.result.XResultPageBase;
import org.eclipse.osee.framework.jdk.core.result.HyperType;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.Dialogs;
import org.eclipse.swt.program.Program;

public class XResultPage extends XResultPageBase {

   private final Pattern ATS_NAME_AND_ID_REGEX = Pattern.compile("([A-Z]{3,4})=(.*?):(.*)");
   private final Pattern ATS_ID_REGEX = Pattern.compile("([A-Z]{3,4})=(.*)");

   public XResultPage(String title, String text) {
      super(title, text);
   }

   public XResultPage(String title, String html, Manipulations... manipulations) {
      super(title, html, manipulations);
   }

   @Override
   public String handleIdCmdHyper(String str) {
      // System.err.println("match " + line);
      // Match getText so it doesn't mess up replace
      // Retireve all ATS=WPN_PAGE:HSRID matches
      Matcher m = ATS_NAME_AND_ID_REGEX.matcher(str);
      Set<String> cmdNameids = new HashSet<>();
      while (m.find()) {
         cmdNameids.add(m.group());
      }
      // Retrieve all ATS=Name:HRSID matches and replace with hyperlinking
      for (String cmdNameId : cmdNameids) {
         String value = cmdNameId;
         value = value.replaceAll("^.*?=", "");
         String name = value;
         name = name.replaceAll(":.*$", "");
         String id = value;
         id = id.replaceAll("^.*:", "");
         if (cmdNameId.startsWith(HyperType.BOTH.name())) {
            String replaceStr = id + " (" + XResultDataUI.getHyperlinkForAction("ATS-" + name, id);
            replaceStr += "  " + XResultDataUI.getHyperlinkForArtifactEditor("AE-" + name, id);
            replaceStr += ")";
            str = str.replaceAll(cmdNameId, replaceStr);
         } else if (cmdNameId.startsWith(HyperType.ATS.name())) {
            str = str.replaceAll(cmdNameId, XResultDataUI.getHyperlinkForAction(name, id));
         } else if (cmdNameId.startsWith(HyperType.ART.name())) {
            str = str.replaceAll(cmdNameId, XResultDataUI.getHyperlinkForArtifactEditor(name, id));
         }
      }
      // Retrieve all ATS=ID matches and replace with hyperlinking
      m = ATS_ID_REGEX.matcher(str);
      Set<String> ids = new HashSet<>();
      while (m.find()) {
         ids.add(m.group());
      }
      for (String id : ids) {
         id = id.replaceAll("^.*?=", "");
         if (id.startsWith(HyperType.BOTH.name())) {
            String replaceStr = id + " (" + XResultDataUI.getHyperlinkForAction("ATS", id);
            replaceStr += "  " + XResultDataUI.getHyperlinkForArtifactEditor("AE", id);
            replaceStr += ")";
            str = str.replaceAll(id, replaceStr);
         } else if (id.startsWith(HyperType.ATS.name())) {
            str = str.replaceAll(id, XResultDataUI.getHyperlinkForAction(id, id));
         } else if (id.startsWith(HyperType.ART.name())) {
            str = str.replaceAll(id, XResultDataUI.getHyperlinkForArtifactEditor(id, id));
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
