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

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
import org.eclipse.osee.framework.ui.skynet.widgets.XWidget;
import org.eclipse.osee.framework.ui.swt.Displays;
import org.eclipse.osee.framework.ui.swt.ImageManager;

/**
 * Generic task creation off change report from sibling or self workflow based on ATS Task Set definition.
 *
 * @author Donald G. Dunne
 */
public class XCreateChangeReportTasksXButton extends XButton implements IArtifactWidget {

   public static final Object WIDGET_ID = XCreateChangeReportTasksXButton.class.getSimpleName();
   // Team Workflow hosting this button (not necessarily one holding branch/commit
   private TeamWorkFlowArtifact hostTeamWf;
   private final List<AtsTaskDefToken> taskDefTokens = new ArrayList<>();
   boolean creating = false;
   boolean debug = false; // true to display more detail regarding task matches
   boolean reportOnly = false; // true to not persist; used for debugging

   public XCreateChangeReportTasksXButton(String name, AtsTaskDefToken... taskDefTokens) {
      super(name);
      for (AtsTaskDefToken taskDefToken : taskDefTokens) {
         this.taskDefTokens.add(taskDefToken);
      }
      Conditions.assertNotNullOrEmpty(this.taskDefTokens, "taskDefToken must not be null");
      setImage(ImageManager.getImage(AtsImage.PLAY_GREEN));
      setToolTip(String.format("Click to Create/Update Change Report Tasks from [%s]", name));
      addXModifiedListener(listener);
   }

   private final XModifiedListener listener = new XModifiedListener() {
      String fName = getLabel();

      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         debug = false;
         reportOnly = false;
         createUpdateTasks(fName);
      }

      @Override
      public void handleRightClick(XWidget widget) {
         debug = true;
         reportOnly = true;
         createUpdateTasks(fName);
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
      if (!MessageDialog.openConfirm(Displays.getActiveShell(), useName, useName + "?")) {
         creating = false;
         return;
      }

      final String fName = name;
      Displays.ensureInDisplayThread(new Runnable() {

         @Override
         public void run() {
            setLabel(fName + " - Creating...");
            bComp.layout(true);
            parent.layout(true);
         }
      });

      final IAtsTeamWorkflow teamWf = this.hostTeamWf;
      Job job = new Job(name) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            try {
               for (AtsTaskDefToken taskDefToken : taskDefTokens) {
                  // Multiple TaskSetDefinitions can be registered for a transition; ensure applicable before running
                  CreateTasksDefinitionBuilder taskSetDefinition =
                     AtsClientService.get().getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken);
                  if (taskSetDefinition != null && taskSetDefinition.getCreateTasksDef().getHelper().isApplicable(
                     teamWf, AtsClientService.get())) {

                     ChangeReportTaskData crtd = new ChangeReportTaskData();
                     crtd.setOperationName(getName());
                     crtd.setTaskDefToken(taskDefToken);
                     crtd.setHostTeamWf(hostTeamWf);
                     crtd.setAsUser(AtsClientService.get().getUserService().getCurrentUser());

                     // Use booleans above to debug task matches
                     crtd.setDebug(debug);
                     crtd.setReportOnly(reportOnly);

                     crtd = AtsClientService.get().getTaskService().createTasks(crtd);
                     XResultDataUI.report(crtd.getResults(), getName());

                     // Reload team wfs if tasks created
                     if (crtd.getTransaction() != null && crtd.getTransaction().isValid()) {
                        final ChangeReportTaskData fData = crtd;
                        Thread reload = new Thread(new Runnable() {
                           @Override
                           public void run() {
                              for (ChangeReportTaskTeamWfData crttwd : fData.getChangeReportDatas()) {
                                 ArtifactQuery.reloadArtifactFromId(crttwd.getDestTeamWf(),
                                    AtsClientService.get().getAtsBranch());
                              }
                           }
                        });
                        reload.start();
                     }
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

   @Override
   public Result isDirty() {
      return Result.FalseResult;
   }

   @Override
   public void revert() {
      // do nothing
   }

   @Override
   public void saveToArtifact() {
      // do nothing
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.hostTeamWf = (TeamWorkFlowArtifact) artifact;
      }
      super.setEditable(true);
   }

}
