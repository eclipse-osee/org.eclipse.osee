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
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.workflow.AtsActionEndpointApi;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * ATS Navigate Item that exports a program's configuration and workflows to SysML V2 textual notation (.sysml files).
 * Currently hard-coded to SAW_PL_Program for demo purposes.
 *
 * @author Donald G. Dunne
 */
public class ExportProgramToSysmlNavigateItem extends XNavigateItemAction {

   private static final String TITLE = "Export Program to SysML V2";
   private static final String OUTPUT_DIR = System.getProperty("user.home") + "/SawPlProgramSysMl";

   public ExportProgramToSysmlNavigateItem() {
      super(TITLE, CoreImage.EXPORT, AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      XResultData rd = new XResultData();

      try {
         // Hard-coded to SAW PL Program for demo
         ArtifactId programId = DemoArtifactToken.SAW_PL_Program;
         AtsActionEndpointApi actionEp = AtsApiService.get().getServerEndpoints().getActionEndpoint();

         // Ensure output directory exists
         File outputDir = new File(OUTPUT_DIR);
         if (!outputDir.exists() && !outputDir.mkdirs()) {
            rd.errorf("Could not create output directory: %s", outputDir.getAbsolutePath());
            XResultDataUI.report(rd, TITLE);
            return;
         }

         // Export config
         rd.log("Exporting config for SAW PL Program...");
         String configSysml = actionEp.getSysmlConfig(programId);
         File configFile = new File(outputDir, "saw-pl-program-config.sysml");
         Lib.writeStringToFile(configSysml, configFile);
         rd.logf("  Config written to: %s (%d bytes)\n", configFile.getAbsolutePath(), configSysml.length());

         // Export workflows
         rd.log("Exporting workflows for SAW PL Program...");
         String workflowsSysml = actionEp.getSysmlWorkflows(programId);
         File workflowsFile = new File(outputDir, "saw-pl-program-workflows.sysml");
         Lib.writeStringToFile(workflowsSysml, workflowsFile);
         rd.logf("  Workflows written to: %s (%d bytes)\n", workflowsFile.getAbsolutePath(),
            workflowsSysml.length());

         rd.log("\nExport complete.");
      } catch (Exception ex) {
         rd.errorf("Error exporting to SysML: %s", ex.getMessage());
      }

      XResultDataUI.report(rd, TITLE);
   }

   @Override
   public String getDescription() {
      return "Exports SAW PL Program configuration and workflows to SysML V2 textual notation.\n" //
         + "Output: " + OUTPUT_DIR;
   }
}
