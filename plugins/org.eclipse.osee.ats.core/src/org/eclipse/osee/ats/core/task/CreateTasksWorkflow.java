/*
 * Created on Feb 19, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.task;

import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.commit.ICommitConfigItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.task.CreateTasksOption;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsAction;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.INewActionListener;
import org.eclipse.osee.ats.core.internal.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G Dunne
 */
public class CreateTasksWorkflow {

   protected final Collection<CreateTasksOption> createTasksOptions;
   protected final boolean createWorkflow;
   protected final boolean reportOnly;
   protected final XResultData resultData;
   protected final IAtsChangeSet changes;
   protected final Date createdDate;
   protected final AtsUser createdBy;
   protected final IAtsTeamWorkflow sourceTeamWf;
   protected final ICommitConfigItem commitConfigItem;
   protected final WorkType workType;
   protected IAtsTeamWorkflow destTeam;
   protected final String pcrNumber;
   protected final AtsApi atsApi;
   protected IAtsActionableItem actionableItem;

   public CreateTasksWorkflow(String pcrNumber, Collection<CreateTasksOption> createTasksOptions, Collection<String> taskNamesMissingTaskArtifact, //
      boolean reportOnly, XResultData resultData, IAtsChangeSet changes, Date createdDate, AtsUser createdBy, IAtsTeamWorkflow sourceTeamWf, //
      ICommitConfigItem commitConfigArt, WorkType workType, IAtsTeamWorkflow destTeam, IAtsProgram program) {
      this(pcrNumber, createTasksOptions,
         ((!taskNamesMissingTaskArtifact.isEmpty() || createTasksOptions.contains(
            CreateTasksOption.WorkflowsOnly)) && destTeam == null),
         reportOnly, resultData, changes, createdDate, createdBy, sourceTeamWf, commitConfigArt, workType, destTeam,
         program);
   }

   public CreateTasksWorkflow(String pcrNumber, Collection<CreateTasksOption> createTasksOptions, boolean createWorkflow, //
      boolean reportOnly, XResultData resultData, IAtsChangeSet changes, Date createdDate, AtsUser createdBy, IAtsTeamWorkflow sourceTeamWf, //
      ICommitConfigItem commitConfigArt, WorkType workType, IAtsTeamWorkflow destTeam, IAtsProgram program) {
      this.pcrNumber = pcrNumber;
      this.createTasksOptions = createTasksOptions;
      this.createWorkflow = createWorkflow;
      this.reportOnly = reportOnly;
      this.resultData = resultData;
      this.changes = changes;
      this.createdDate = createdDate;
      this.createdBy = createdBy;
      this.sourceTeamWf = sourceTeamWf;
      this.commitConfigItem = commitConfigArt;
      this.workType = workType;
      this.destTeam = destTeam;
      this.atsApi = AtsApiService.get();
   }

   protected void logWorkflowCreation() {
      resultData.logf("Create [%s] Workflow\n", workType.name());
   }

   public boolean isSourceTeamWfEqualDestination() {
      return false;
   }

   public IAtsActionableItem getActionableItem() {
      return actionableItem;
   }

   public Collection<INewActionListener> getNewActionListeners() {
      return Collections.emptyList();
   }

   public IAtsTeamWorkflow createMissingWorkflow() {
      // Now, report all tasks of unknown origin
      if (createWorkflow) {
         logWorkflowCreation();

         /**
          * Don't need to create workflow if source equals destination.
          */
         if (isSourceTeamWfEqualDestination()) {
            destTeam = sourceTeamWf;
         } else {
            // Create missing workflow
            IAtsAction action = sourceTeamWf.getParentAction();
            Set<IAtsActionableItem> actionableItems = new HashSet<>();

            List<AtsUser> assignees = getAssignees();

            IAtsActionableItem aia = getActionableItem();
            if (aia != null) {
               actionableItems.add(aia);
               IAtsTeamWorkflow newTeamWf = atsApi.getActionFactory().createTeamWorkflow(action,
                  AtsApiService.get().getTeamDefinitionService().getImpactedTeamDefs(actionableItems).iterator().next(),
                  actionableItems, assignees, changes, createdDate, createdBy, getNewActionListeners(),
                  CreateTeamOption.Duplicate_If_Exists);

               if (createTasksOptions.contains(CreateTasksOption.CreateWorkflowsAsOseeSystem)) {
                  newTeamWf.getStateMgr().internalSetCreatedBy(AtsCoreUsers.SYSTEM_USER, changes);
               }

               destTeam = newTeamWf;
            }
         }
      }
      return destTeam;
   }

   protected List<AtsUser> getAssignees() {
      List<AtsUser> assignees = new LinkedList<>();
      assignees.add(AtsCoreUsers.UNASSIGNED_USER);
      return assignees;
   }

   public void setTargetedVersion() {
      if (commitConfigItem instanceof IAtsVersion) {
         IAtsVersion targetedVersion = atsApi.getVersionService().getTargetedVersion(destTeam);
         IAtsVersion commitConfigVer = (IAtsVersion) commitConfigItem;
         if (targetedVersion == null || !targetedVersion.equals(commitConfigVer)) {
            if (targetedVersion != null) {
               changes.unrelateAll(destTeam, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version);
            }
            changes.relate(destTeam, AtsRelationTypes.TeamWorkflowTargetedForVersion_Version, commitConfigVer);
         }
      }

   }

   public void setActionableItem(IAtsActionableItem ai) {
      this.actionableItem = ai;
   }

}
