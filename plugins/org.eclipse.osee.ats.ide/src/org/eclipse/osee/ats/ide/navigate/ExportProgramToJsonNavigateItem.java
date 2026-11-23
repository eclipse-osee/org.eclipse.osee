/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 **********************************************************************/

package org.eclipse.osee.ats.ide.navigate;

import com.fasterxml.jackson.databind.JsonNode;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.sysml.WorkflowExportData;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateComposite.TableLoadOption;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavigateItemAction;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/** ATS Navigate Item that exports a program's configuration and workflows as JSON files. */
public class ExportProgramToJsonNavigateItem extends XNavigateItemAction {

   private static final String TITLE = "Export Program to JSON";
   private static final String OUTPUT_DIR = System.getProperty("user.home") + "/OseeJsonExport";

   public ExportProgramToJsonNavigateItem() {
      super(TITLE, CoreImage.EXPORT, AtsNavigateViewItems.ATS_UTIL);
   }

   @Override
   public void run(TableLoadOption... tableLoadOptions) {
      AtsApi atsApi = AtsApiService.get();
      WorkflowExportData exportData = new WorkflowExportDialog(atsApi).openAndGet();
      if (exportData == null) {
         return;
      }
      if (exportData.getProgramId() == null || exportData.getProgramId().isInvalid()) {
         XResultData rd = new XResultData();
         rd.error("A Program is required to export.");
         XResultDataUI.report(rd, TITLE);
         return;
      }

      Job job = new Job(TITLE) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            return export(atsApi, exportData);
         }
      };
      job.setUser(true);
      job.setPriority(Job.LONG);
      job.schedule();
   }

   private IStatus export(AtsApi atsApi, WorkflowExportData exportData) {
      WorkflowExportData result = atsApi.getServerEndpoints().getActionEndpoint().getConfigJson(exportData);
      result = atsApi.getServerEndpoints().getActionEndpoint().getWorkflowJson(result);
      XResultData rd = result.getResults();
      try {
         IAtsProgram program = atsApi.getProgramService().getProgramById(exportData.getProgramId());
         IAtsTeamDefinition teamDef = exportData.isTeamDefIdValid()
            ? atsApi.getTeamDefinitionService().getTeamDefinitionById(exportData.getTeamDefId()) : null;
         IAtsVersion version = exportData.isVersionIdValid()
            ? atsApi.getVersionService().getVersionById(exportData.getVersionId()) : null;
         String programName = program == null ? exportData.getProgramId().getIdString() : program.getName();
         String versionName = version == null ? "all_versions" : version.getName();
         File outputDir = new File(OUTPUT_DIR, toFileName(programName) + "_" + toFileName(versionName));
         if (!outputDir.exists() && !outputDir.mkdirs()) {
            rd.errorf("Could not create output directory: %s", outputDir.getAbsolutePath());
         } else {
            writeConfig(result.getConfigJson(), outputDir, rd);
            writeWorkflows(result, outputDir, rd);
            writeReadme(outputDir, program, teamDef, version, exportData, result);
            rd.log("\nExport complete.");
            org.eclipse.swt.program.Program.launch(outputDir.getAbsolutePath());
         }
      } catch (Exception ex) {
         rd.errorf("Error writing JSON export files: %s", ex.getMessage());
      }
      XResultDataUI.report(rd, TITLE);
      return rd.isSuccess() ? Status.OK_STATUS : new Status(IStatus.ERROR, Activator.PLUGIN_ID, -1,
         "JSON export failed; see export results.", null);
   }

   private void writeConfig(String configJson, File outputDir, XResultData rd) throws Exception {
      if (Strings.isValid(configJson)) {
         File configFile = new File(outputDir, "config.json");
         Lib.writeStringToFile(configJson, configFile);
         rd.logf("  Config written to: %s (%d bytes)\n", configFile.getAbsolutePath(), configJson.length());
      }
   }

   private void writeWorkflows(WorkflowExportData exportData, File outputDir, XResultData rd) throws Exception {
      for (String workflowJson : exportData.getWorkflowJson()) {
         JsonNode workflow = JsonUtil.readTree(workflowJson);
         String atsId = workflow.path("AtsId").asText("unknown");
         String artId = workflow.path("id").asText("unknown");
         File workflowFile = new File(outputDir, toWorkflowFileName(atsId) + "_" + artId + ".json");
         Lib.writeStringToFile(workflowJson, workflowFile);
         rd.logf("  Workflow written to: %s (%d bytes)\n", workflowFile.getAbsolutePath(), workflowJson.length());
      }
   }

   private void writeReadme(File outputDir, IAtsProgram program, IAtsTeamDefinition teamDef, IAtsVersion version,
      WorkflowExportData exportData, WorkflowExportData result) throws Exception {
      String date = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
      String readme = String.format("Operation: %s%nRun Date: %s%nProgram: %s%nTeam Definition: %s%nArtifact Type: %s%nVersion: %s%nWorkflows Returned: %d%n",
         TITLE, date, display(program, exportData.getProgramId().getIdString()), display(teamDef, "All Teams"),
         exportData.isArtifactTypeValid() ? exportData.getArtifactType().toString() : "All Team Workflow Types",
         display(version, "All Versions"), result.getWorkflowJson().size());
      Lib.writeStringToFile(readme, new File(outputDir, "README.txt"));
   }

   private String display(Object object, String defaultValue) {
      return object == null ? defaultValue : object.toString();
   }

   private String toFileName(String value) {
      return value.trim().replaceAll("[^a-zA-Z0-9]+", "_").toLowerCase();
   }

   private String toWorkflowFileName(String value) {
      return value.trim().replaceAll("[^a-zA-Z0-9._-]+", "_");
   }

   @Override
   public String getDescription() {
      return "Exports a program's configuration and workflows to JSON files.\nOutput: " + OUTPUT_DIR;
   }
}
