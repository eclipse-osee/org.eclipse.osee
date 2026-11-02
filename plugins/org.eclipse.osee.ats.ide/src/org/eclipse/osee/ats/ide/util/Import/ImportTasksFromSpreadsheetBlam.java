/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.ide.util.Import;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.NewTaskSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.blam.AbstractAtsBlam;
import org.eclipse.osee.ats.ide.editor.WorkflowEditor;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.navigate.AtsNavigateViewItems;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.CoreImage;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XWidgetData;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.plugin.core.util.Jobs;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.demo.DemoUtilCore;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.plugin.xnavigate.XNavItemCat;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.util.XWidgetSwtRenderer;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonPushWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXListArtifactDropWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.swt.program.Program;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.osgi.service.component.annotations.Component;

/**
 * @author Donald G. Dunne
 */
@Component(service = AbstractBlam.class, immediate = true)
public class ImportTasksFromSpreadsheetBlam extends AbstractAtsBlam {

   private static final String BLAM_NAME = "Import Tasks From Spreadsheet";
   static final String OPEN_EXCEL_IMPORT_EXAMPLE_SPREADSHEET = "Open Excel Import Example Spreadsheet";
   public final static String TASK_IMPORT_SPREADSHEET = "Task Import Spreadsheet";
   public final static String TEAM_WORKFLOW = "Taskable Workflow (drop here)";
   public final static String EMAIL_POCS = "Email POCs";
   public final static String FIX_TITLES = "Fix Titles (Removes Non-Printable Chars and Truncate)";
   public final static String INVALID_BLAM_CAUSE = "Invalid BLAM Spreadsheet";

   private TeamWorkFlowArtifact taskableStateMachineArtifact;
   private NewTaskSet newTaskSet;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public ImportTasksFromSpreadsheetBlam() {
      super(BLAM_NAME, CoreImage.IMPORT);
   }

   //////////////////////////////////////////
   // Widgets
   //////////////////////////////////////////

   @Override
   public List<XWidgetData> getXWidgetItems() {
      createWidgetBuilder();
      wb.andWidget(OPEN_EXCEL_IMPORT_EXAMPLE_SPREADSHEET, WidgetId.XXButtonPushWidget);
      wb.andListDropWidget(TEAM_WORKFLOW);
      wb.andFileSelWidget(TASK_IMPORT_SPREADSHEET);
      wb.andCheckBoxWidget(FIX_TITLES);
      wb.andCheckBoxWidget(EMAIL_POCS);
      return wb.getXWidgetDatas();
   }

   @Override
   public void widgetCreated(XWidget xWidget, FormToolkit toolkit, Artifact art, XWidgetSwtRenderer xWidgetSwtRenderer,
      XModifiedListener modListener, boolean isEditable) {
      super.widgetCreated(xWidget, toolkit, art, xWidgetSwtRenderer, modListener, isEditable);
      if (xWidget.getLabel().equals(TEAM_WORKFLOW) && taskableStateMachineArtifact != null) {
         XXListArtifactDropWidget viewer = (XXListArtifactDropWidget) xWidget;
         viewer.setSelected(Arrays.asList(taskableStateMachineArtifact));
      }
      if (xWidget.getLabel().equals(OPEN_EXCEL_IMPORT_EXAMPLE_SPREADSHEET)) {
         XXButtonPushWidget button = (XXButtonPushWidget) xWidget;
         button.addXModifiedListener(new XModifiedListener() {

            @Override
            public void widgetModified(XWidget widget) {
               try {
                  File file = getSampleSpreadsheetFile();
                  Program.launch(file.getCanonicalPath());
               } catch (Exception ex) {
                  log(ex);
               }
            }
         });
      }
   }

   //////////////////////////////////////////
   // Operation
   //////////////////////////////////////////

   @Override
   public void runOperation(final VariableMap variableMap, IProgressMonitor monitor) throws Exception {

      // Remove when demo operation test is implemented in Blam
      if (DemoUtilCore.isDemoDbBlamTest()) {
         return;
      }

      newTaskSet = NewTaskSet.create(getName(), AtsApiService.get().getUserService().getCurrentUserId());
      newTaskSet.getResults().setTitle(getName());
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            try {
               List<Artifact> artifacts = variableMap.getArtifacts(TEAM_WORKFLOW);
               String filename = variableMap.getString(TASK_IMPORT_SPREADSHEET);
               boolean emailPocs = variableMap.getBoolean(EMAIL_POCS);
               boolean fixTitles = variableMap.getBoolean(FIX_TITLES);

               if (artifacts.isEmpty()) {
                  AWorkbench.popup("ERROR", "Must drag in Team Workflow to add tasks.");
                  return;
               }
               if (artifacts.size() > 1) {
                  AWorkbench.popup("ERROR", "Only drag ONE Team Workflow.");
                  return;
               }
               Artifact artifact = artifacts.iterator().next();
               if (!artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
                  AWorkbench.popup("ERROR", "Artifact MUST be Team Workflow");
                  return;
               }
               if (!Strings.isValid(filename)) {
                  AWorkbench.popup("ERROR", "Must enter valid filename.");
                  return;
               }
               File file = new File(filename);
               try {
                  // close editor
                  WorkflowEditor editor = WorkflowEditor.getWorkflowEditor((TeamWorkFlowArtifact) artifact);
                  if (editor != null) {
                     editor.close(true);
                  }

                  performImport(fixTitles, emailPocs, (TeamWorkFlowArtifact) artifact, file);

                  Thread.sleep(2000);

                  // re-open editor
                  WorkflowEditor.edit((TeamWorkFlowArtifact) artifact);
                  editor = WorkflowEditor.getWorkflowEditor((TeamWorkFlowArtifact) artifact);
                  if (editor != null) {
                     editor.setPage(1);
                  }
               } catch (Exception ex) {
                  log(ex);
               }
               /**
                * If spreadsheet has critical errors, display to user and don't update workflow
                */
               if (newTaskSet.getResults().isErrors()) {
                  XResultDataUI.report(newTaskSet.getResults(), "Error: " + getName());
               }

            } catch (Exception ex) {
               log(ex);
            }
         }

      });
   }

   //////////////////////////////////////////
   // Helpers
   //////////////////////////////////////////

   public File getSampleSpreadsheetFile() throws Exception {
      return OseeInf.getResourceAsFile("atsImport/Task_Import.xml", getClass());
   }

   public void setTaskableStateMachineArtifact(TeamWorkFlowArtifact taskableStateMachineArtifact) {
      this.taskableStateMachineArtifact = taskableStateMachineArtifact;
   }

   public NewTaskSet performImport(boolean fixTitles, boolean emailPocs, IAtsTeamWorkflow teamWf, File file) {
      try {
         AtsUtilClient.setEmailEnabled(emailPocs);

         NewTaskData newTaskData = new NewTaskData();
         List<NewTaskData> newTaskDatas = new ArrayList<NewTaskData>();
         newTaskDatas.add(newTaskData);
         if (newTaskSet == null) {
            newTaskSet = NewTaskSet.create(getName(), AtsApiService.get().getUserService().getCurrentUserId());
         }
         newTaskSet.setTaskDatas(newTaskDatas);
         newTaskSet.getTaskData().setTeamWfId(teamWf.getId());
         newTaskSet.getTaskData().setFixTitles(fixTitles);

         XResultData rd = newTaskSet.getResults();
         rd.log("========================================");
         rd.log("Import Tasks From Spreadsheet");
         rd.log("========================================");
         rd.logf("File: %s\n", file.getName());
         rd.logf("Date: %s\n", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
         rd.logf("Workflow: [%s] - %s\n", teamWf.getAtsId(), teamWf.getName());
         rd.log("----------------------------------------");
         rd.log("");

         Job job = Jobs.startJob(new TaskImportJob(file,
            new ExcelAtsTaskArtifactExtractor((TeamWorkFlowArtifact) teamWf.getStoreObject(), newTaskSet.getTaskData()),
            rd));
         job.join();

         if (rd.isErrors()) {
            rd.log("");
            rd.log("========================================");
            rd.logf("Excel Extractor aborted with %d error(s)\n", rd.getNumErrors());
            rd.log("========================================");
            return newTaskSet;
         }

         rd.log("");
         rd.log("========================================");
         rd.logf("Excel Extractor completed successfully - %d task(s) extracted\n", newTaskData.getTasks().size());
         rd.log("========================================");

         AtsApiService.get().getTaskService().createTasks(newTaskSet);

         if (rd.isErrors()) {
            rd.log("");
            rd.log("========================================");
            rd.logf("Importer aborted with %d error(s)\n", rd.getNumErrors());
            rd.log("========================================");
         } else {
            rd.log("");
            rd.log("========================================");
            rd.logf("Importer completed successfully - %d task(s) created\n", newTaskData.getTasks().size());
            rd.log("========================================");
         }

         return newTaskSet;
      } catch (Exception ex) {
         newTaskSet.getResults().errorf("Exception in %s: %s\n", getName(), Lib.exceptionToString(ex));
      } finally {
         AtsUtilClient.setEmailEnabled(true);
      }
      return newTaskSet;
   }

   //////////////////////////////////////////
   // Categories & Description
   //////////////////////////////////////////

   @Override
   public Collection<XNavItemCat> getCategories() {
      return Arrays.asList(AtsNavigateViewItems.ATS_IMPORT);
   }

   @Override
   public String getDescriptionUsage() {
      return "Import tasks from spreadsheet into given Team Workflow.  " //
         + "After \"Notes\" column, remaining columns will attempt to match " //
         + "column name with valid attribute type name add that to workflow.";
   }

}
