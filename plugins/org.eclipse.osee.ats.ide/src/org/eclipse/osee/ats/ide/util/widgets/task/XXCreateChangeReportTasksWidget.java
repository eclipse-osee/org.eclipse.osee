/*********************************************************************
 * Copyright (c) 2009 Boeing
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
package org.eclipse.osee.ats.ide.util.widgets.task;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.util.AtsImage;
import org.eclipse.osee.ats.api.util.WidgetIdAts;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.ChangeReportTasksUtil;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.widget.WidgetId;
import org.eclipse.osee.framework.core.widget.XOption;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.XXButtonHyperWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.xx.util.listen.XModifiedListener;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.osgi.service.component.annotations.Component;

/**
 * Generic task creation off change report from sibling or self workflow based on ATS Task Set definition.
 *
 * @author Donald G. Dunne
 */
@Component(service = XWidget.class, immediate = true)
public class XXCreateChangeReportTasksWidget extends XXButtonHyperWidget {

   public static final WidgetId ID = WidgetIdAts.XXCreateChangeReportTasksWidget;

   // Team Workflow hosting this button (not necessarily one holding branch/commit
   private TeamWorkFlowArtifact hostTeamWf;
   private AtsTaskDefToken taskDefToken;
   boolean creating = false;
   boolean debug = false; // true to display more detail regarding task matches
   boolean reportOnly = false; // true to not persist; used for debugging
   AtsApi atsApi;

   //////////////////////////////////////////
   // Constructors
   //////////////////////////////////////////

   public XXCreateChangeReportTasksWidget() {
      this(AtsTaskDefToken.SENTINEL);
   }

   public XXCreateChangeReportTasksWidget(AtsTaskDefToken taskDefToken) {
      super(ID, "Create Code/Test Tasks");
      this.taskDefToken = taskDefToken;
      Conditions.assertNotNull(this.taskDefToken, "taskDefToken must not be null");
      setOseeImage(AtsImage.PLAY_GREEN);
      addXModifiedListener(listener);
      atsApi = AtsApiService.get();
      set(XOption.NO_COLON);
   }

   //////////////////////////////////////////
   // Storage
   //////////////////////////////////////////

   @Override
   public boolean handleSelection() {
      // Left-click: create/update tasks and persist
      debug = false;
      reportOnly = false;
      createUpdateTasks(getLabel());
      return false;
   }

   //////////////////////////////////////////
   // Misc
   //////////////////////////////////////////

   private final XModifiedListener listener = new XModifiedListener() {
      @Override
      public void handleRightClick(XWidget widget) {
         // Right-click: debug / report-only (does not persist)
         debug = true;
         reportOnly = true;
         createUpdateTasks(getLabel());
      }
   };

   protected void createUpdateTasks(String name) {
      if (creating) {
         AWorkbench.popup("Creating Tasks, Please Wait");
         return;
      }
      creating = true;
      String useName = name;
      if (reportOnly) {
         useName = useName + " (ReportOnly)";
      }
      if (!reportOnly && hostTeamWf.getTags().contains(ChangeReportTasksUtil.FINAL_TASK_GEN_TAG)) {
         AWorkbench.popup(ChangeReportTasksUtil.FINAL_TASK_GEN_MSG);
         creating = false;
         return;
      }
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), useName, useName + "?")) {
         creating = false;
         return;
      }
      final String fName = name;
      Displays.ensureInDisplayThread(new Runnable() {
         @Override
         public void run() {
            setLabel(fName + " - Creating...");
            comp.layout(true);
            comp.getParent().layout(true);
         }
      });
      final IAtsTeamWorkflow teamWf = this.hostTeamWf;
      Job job = new Job(name) {
         @Override
         protected IStatus run(IProgressMonitor monitor) {
            try {
               // Multiple TaskSetDefinitions can be registered for a transition; ensure applicable before running
               CreateTasksDefinitionBuilder taskSetDefinition =
                  AtsApiService.get().getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken);
               if (taskSetDefinition == null) {
                  final String msg = String.format("No Task Set Definition found for [%s]", taskDefToken.getName());
                  Displays.ensureInDisplayThread(() -> AWorkbench.popup(getName(), msg));
               } else if (!taskSetDefinition.getCreateTasksDef().getHelper().isApplicable(teamWf,
                  AtsApiService.get())) {
                  final String msg = String.format("Task Set Definition [%s] is not applicable to this workflow",
                     taskDefToken.getName());
                  Displays.ensureInDisplayThread(() -> AWorkbench.popup(getName(), msg));
               } else {
                  ChangeReportTaskData crtd = new ChangeReportTaskData();
                  crtd.setOperationName(getName());
                  crtd.setCommitComment(taskSetDefinition.getCreateTasksDef().getCommitComment());
                  crtd.setTaskDefToken(taskDefToken);
                  crtd.setHostTeamWf(hostTeamWf);
                  crtd.setAsUser(AtsApiService.get().getUserService().getCurrentUser());
                  // Use booleans above to debug task matches
                  crtd.setDebug(debug);
                  crtd.setReportOnly(reportOnly);
                  boolean isFinalTaskGen = atsApi.getBranchService().isCommittedBranchExists(teamWf);
                  crtd.setFinalTaskGen(isFinalTaskGen);
                  crtd = AtsApiService.get().getTaskService().createTasks(crtd);
                  XResultDataUI.report(crtd.getResults(), getName());
                  // Reload team wfs if tasks created
                  if (crtd.getTransaction() != null && crtd.getTransaction().isValid()) {
                     final ChangeReportTaskData fData = crtd;
                     Thread reload = new Thread(new Runnable() {
                        @Override
                        public void run() {
                           for (ChangeReportTaskTeamWfData crttwd : fData.getChangeReportDatas()) {
                              ArtifactQuery.reloadArtifactFromId(crttwd.getDestTeamWf(),
                                 AtsApiService.get().getAtsBranch());
                           }
                        }
                     });
                     reload.start();
                  }
               }
            } finally {
               Displays.ensureInDisplayThread(new Runnable() {
                  @Override
                  public void run() {
                     setLabel(fName);
                     creating = false;
                  }
               });
            }
            return Status.OK_STATUS;
         }
      };
      job.schedule();
   };

   @Override
   public TeamWorkFlowArtifact getArtifact() {
      return hostTeamWf;
   }

   //////////////////////////////////////////
   // Initialization
   //////////////////////////////////////////

   @Override
   public void setArtifact(Artifact artifact) {
      // Set fields before super.setArtifact() since super triggers refresh()/initializeSelected()
      // and getArtifact() is overridden to return hostTeamWf
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.hostTeamWf = (TeamWorkFlowArtifact) artifact;
      }
      // Resolve taskDefToken from the widget definition parameter (set by ChangeReportTasksWidgetDef)
      Object param = getParameters().get(AtsTaskDefToken.ID);
      if (param instanceof AtsTaskDefToken) {
         this.taskDefToken = (AtsTaskDefToken) param;
      }
      super.setArtifact(artifact);
      if (taskDefToken != null && taskDefToken.isValid()) {
         setToolTip(String.format("Click to Create/Update Change Report Tasks from [%s]", taskDefToken.getName()));
      }
      super.setEditable(true);
   }

}
