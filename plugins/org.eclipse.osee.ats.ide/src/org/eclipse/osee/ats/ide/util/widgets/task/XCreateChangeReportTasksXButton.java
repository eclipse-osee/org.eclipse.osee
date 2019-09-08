/*
 * Created on Apr 14, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.ide.util.widgets.task;

import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.AtsImage;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.eclipse.osee.framework.ui.skynet.widgets.IArtifactWidget;
import org.eclipse.osee.framework.ui.skynet.widgets.XButton;
import org.eclipse.osee.framework.ui.skynet.widgets.XModifiedListener;
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
   private final AtsTaskDefToken taskDefToken;
   private final XResultData rd;

   public XCreateChangeReportTasksXButton(String name, AtsTaskDefToken taskDefToken) {
      super(name);
      Conditions.assertNotNull(taskDefToken, "taskDefToken must not be null");
      this.taskDefToken = taskDefToken;
      setImage(ImageManager.getImage(AtsImage.PLAY_GREEN));
      setToolTip(String.format("Click to Create/Update Change Report Tasks from [%s]", taskDefToken.getName()));
      addXModifiedListener(listener);
      rd = new XResultData();
   }

   private final XModifiedListener listener = new XModifiedListener() {
      String fName = getLabel();

      @Override
      public void widgetModified(org.eclipse.osee.framework.ui.skynet.widgets.XWidget widget) {
         if (!isEditable()) {
            IAtsTeamWorkflow sourceTeamWf = getSourceTeamWf();
            if (sourceTeamWf == null) {
               AWorkbench.popup("No single from Team Workflow can be found.");
            } else if (AtsClientService.get().getBranchService().isWorkingBranchInWork(
               sourceTeamWf) || AtsClientService.get().getBranchService().isCommittedBranchExists(hostTeamWf)) {
               AWorkbench.popupf("No working branch or commit found for workflow %s", sourceTeamWf.toStringWithId());
            }
            return;
         }
         createUpdateTasks(fName);
      }

   };

   protected void createUpdateTasks(String name) {

      Job job = new Job("Preparing to " + name) {

         @Override
         protected IStatus run(IProgressMonitor monitor) {

            CreateChangeReportTasksOperation op = new CreateChangeReportTasksOperation(getSourceTeamWf(), taskDefToken);
            XResultData rd = op.run();
            XResultDataUI.report(rd, getName());

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

   // Team Workflow owning branch/commit.  May or may not be host Team Workflow
   private IAtsTeamWorkflow getSourceTeamWf() {
      CreateTasksDefinitionBuilder setDef =
         AtsClientService.get().getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken.getId());
      if (setDef == null) {
         AWorkbench.popup("Task Set Definition %s can not be found", taskDefToken.toStringWithId());
         return null;
      }
      IAtsTeamDefinitionArtifactToken fromTeamDef = setDef.getChgRptOptions().getFromSiblingTeam();
      Collection<IAtsTeamWorkflow> siblings =
         AtsClientService.get().getWorkItemService().getSiblings(hostTeamWf, fromTeamDef);
      if (siblings.size() == 1) {
         return siblings.iterator().next();
      }
      return null;
   }

   @Override
   public void setArtifact(Artifact artifact) {
      if (artifact.isOfType(AtsArtifactTypes.TeamWorkflow)) {
         this.hostTeamWf = (TeamWorkFlowArtifact) artifact;
      }
      IAtsTeamWorkflow sourceTeamWf = getSourceTeamWf();
      boolean workingBranchInWork = AtsClientService.get().getBranchService().isWorkingBranchInWork(sourceTeamWf);
      boolean committedBranchExists = AtsClientService.get().getBranchService().isCommittedBranchExists(sourceTeamWf);
      boolean editable = sourceTeamWf != null && (workingBranchInWork || committedBranchExists);
      super.setEditable(editable);
   }

}
