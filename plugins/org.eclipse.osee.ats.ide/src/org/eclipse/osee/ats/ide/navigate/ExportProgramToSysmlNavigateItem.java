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

package org.eclipse.osee.ats.ide.navigate;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.sysml.WorkflowExportData;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * ATS Navigate Item that exports a program's configuration and workflows to SysML V2 textual notation (.sysml files).
 *
 * @author Donald G. Dunne
 */
public class ExportProgramToSysmlNavigateItem extends XNavigateItemAction {

   private static final String TITLE = "Export Program to SysML V2";
   private static final String OUTPUT_DIR = System.getProperty("user.home") + "/OseeSysmlExport";

   public ExportProgramToSysmlNavigateItem() {
      super(TITLE, CoreImage.EXPORT, Arrays.asList(CoreUserGroups.OseeAdmin), AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      AtsApi atsApi = AtsApiService.get();

      WorkflowExportData exportData = new WorkflowExportDialog(atsApi).openAndGet();
      if (exportData == null) {
         return; // user cancelled
      }
      if (exportData.getProgramId() == null || exportData.getProgramId().isInvalid()) {
         XResultData cancelRd = new XResultData();
         cancelRd.error("A Program is required to export.");
         XResultDataUI.report(cancelRd, TITLE);
         return;
      }

      // Dialog has closed with valid selections; run the export on the server.
      WorkflowExportData result = atsApi.getServerEndpoints().getActionEndpoint().exportSysml(exportData);
      XResultData rd = result.getResults();

      try {
         IAtsProgram program = atsApi.getProgramService().getProgramById(result.getProgramId());
         String programName = program == null ? result.getProgramId().getIdString() : program.getName();
         String fileBase = toFileName(programName);
         String timestamp = new SimpleDateFormat("yyyyMMdd-HHmmss").format(new Date());

         File outputDir = new File(OUTPUT_DIR);
         if (!outputDir.exists() && !outputDir.mkdirs()) {
            rd.errorf("Could not create output directory: %s", outputDir.getAbsolutePath());
            XResultDataUI.report(rd, TITLE);
            return;
         }

         // Export config
         String configSysml = result.getConfigSysml();
         if (Strings.isValid(configSysml)) {
            File configFile = new File(outputDir, fileBase + "-" + timestamp + "-config.sysml");
            Lib.writeStringToFile(configSysml, configFile);
            rd.logf("  Config written to: %s (%d bytes)\n", configFile.getAbsolutePath(), configSysml.length());
         }

         // Export workflows
         rd.log("Exporting workflows for ATS Program...");
         String workflowsSysml = result.getWorkflowSysml();
         if (Strings.isValid(workflowsSysml)) {
            File workflowsFile = new File(outputDir, fileBase + "-" + timestamp + "-workflows.sysml");
            Lib.writeStringToFile(workflowsSysml, workflowsFile);
            rd.logf("  Workflows written to: %s (%d bytes)\n", workflowsFile.getAbsolutePath(),
               workflowsSysml.length());
         }

         rd.log("\nExport complete.");

         org.eclipse.swt.program.Program.launch(outputDir.getAbsolutePath());
      } catch (Exception ex) {
         rd.errorf("Error writing SysML export files: %s", ex.getMessage());
      }

      XResultDataUI.report(rd, TITLE);
   }

   private String toFileName(String programName) {
      return programName.trim().replaceAll("[^a-zA-Z0-9]+", "_").toLowerCase();
   }

   @Override
   public String getDescription() {
      return "Exports a program's configuration and workflows to SysML V2 textual notation.\n" //
         + "Output: " + OUTPUT_DIR;
   }
}
