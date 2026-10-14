/*********************************************************************
 * Copyright (c) 2026 Boeing
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

package org.eclipse.osee.framework.ui.skynet.blam.test;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;
import java.util.Map.Entry;
import org.eclipse.jface.window.Window;
import org.eclipse.osee.framework.core.data.IUserGroupArtifactToken;
import org.eclipse.osee.framework.core.data.OseeData;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.jdk.core.result.Manipulations;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.BlamService;
import org.eclipse.osee.framework.ui.skynet.internal.Activator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.dialog.CheckBoxDialog;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractTestingOpenAndTestAllRegisteredBlams extends XNavigateItemAction {

   private final String packageLabel;

   public AbstractTestingOpenAndTestAllRegisteredBlams(String packageLabel) {
      super("Testing - Open and Test All Registered BLAMs - " + packageLabel, CoreImage.BLAM,
         XNavigateItem.TOP_ADMIN_TESTING);
      this.packageLabel = packageLabel;
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {

      CheckBoxDialog diag = new CheckBoxDialog(getName(), "Open Each BLAM and run tests?\n\n" //
         + "Run on Production will open and run non-invasive tests.\n" //
         + "Run on Demo will attempt to run BLAM operation, if available.\n\n" //
         + "Report will show with errors, if any.\n" //
         + "Toggle to keep editors open to check widgets and layout.\n", //
         "Close Blam Editors");
      diag.setChecked(true);
      if (diag.open() != Window.OK) {
         return;
      }
      boolean closeEditor = diag.isChecked();

      try {
         XResultData rd = new XResultData();
         rd.log(getName());
         Map<AbstractBlam, AbstractBlamTest> blamsToTestMap = BlamService.getBlamsToTestMap();
         for (Entry<AbstractBlam, AbstractBlamTest> entry : blamsToTestMap.entrySet()) {
            // Used to log error for just this test
            XResultData thisRd = new XResultData();
            AbstractBlam blam = entry.getKey();
            AbstractBlamTest blamTest = entry.getValue();
            String blamPpackageLabel = AbstractBlam.getPackageLabel(blam);
            String blamLabel = String.format("%s - %s", blam.getClass().getSimpleName(), blamPpackageLabel);
            if (!blamPpackageLabel.equals(packageLabel)) {
               System.err.println("Skipping - " + blamLabel);
               continue;
            }
            try {
               rd.logf("BLAM: %s\n", blamLabel);
               if (blamTest == null) {
                  rd.errorf("[%s] - No Test Found; Should be %sTest in same package\n\n", blamLabel,
                     blam.getClass().getSimpleName());
               } else {

                  blamTest.setCloseEditor(closeEditor);

                  blamTest.test(thisRd);
               }

               // Merge this results with master results
               rd.merge(thisRd);
               rd.log("\n");

               // Only fail this test if thisRd has error; Allows other tests to succeed
               if (thisRd.isErrors()) {
                  XResultDataUI.report(thisRd, "Test Failed - " + blam.getName());
                  break;
               }

            } catch (Exception ex) {
               rd.errorf("Error Testing Blam %s - %s", blam.getName(), Lib.exceptionToString(ex));
            } catch (AssertionError ex) {
               rd.errorf("Assertion Error Testing Blam %s - %s", blam.getName(), Lib.exceptionToString(ex));
            }
         }
         XResultDataUI.report(rd, getName(), Manipulations.HTML_MANIPULATIONS, Manipulations.CONVERT_NEWLINES,
            Manipulations.ERROR_WARNING_HEADER);
         writeReportToCodeReviewOutput(rd);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   /**
    * Write the run's HTML report to the osee.data workspace folder so it persists outside the IDE results view.
    * Failure to write is logged but does not fail the run.
    */
   private void writeReportToCodeReviewOutput(XResultData rd) {
      try {
         String html = XResultDataUI.getReport(rd, getName(), Manipulations.HTML_MANIPULATIONS,
            Manipulations.CONVERT_NEWLINES, Manipulations.ERROR_WARNING_HEADER).getManipulatedHtml();
         String fileName = getName().replaceAll("[^a-zA-Z0-9.-]", "_") + ".html";
         File reportFile = OseeData.writeToFile(fileName, html);
         OseeLog.logf(Activator.class, OseeLevel.INFO, "BLAM test report written to: %s", reportFile.getAbsolutePath());
      } catch (Exception ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      }
   }

   @Override
   public Collection<IUserGroupArtifactToken> getUserGroups() {
      return Arrays.asList(CoreUserGroups.OseeAdmin);
   }

}
