/*
 * Created on Sep 15, 2019
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.ide.util.widgets.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.CreateTasksOption;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTasks;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.task.create.ChangeReportData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.ChangeReportTasksUtil;
import org.eclipse.osee.ats.core.task.CreateTasksWorkflow;
import org.eclipse.osee.ats.ide.branch.AtsBranchManager;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.KindType;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.revision.ChangeData;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

public class CreateChangeReportTasksOperation {

   private final IAtsTeamWorkflow sourceTeamWf;
   private final XResultData rd;
   private final AtsTaskDefToken taskDefToken;
   private final AtsApi atsApi;

   public CreateChangeReportTasksOperation(IAtsTeamWorkflow sourceTeamWf, AtsTaskDefToken taskDefToken) {
      this.sourceTeamWf = sourceTeamWf;
      this.taskDefToken = taskDefToken;
      this.rd = new XResultData();
      this.atsApi = AtsClientService.get();
   }

   public XResultData run() {
      // Get from TeamWf
      if (sourceTeamWf == null) {
         AWorkbench.popup("No single from Team Workflow can be found.");
      }

      // If working branch, get change data from version
      IAtsVersion targetedVersion = atsApi.getVersionService().getTargetedVersion(sourceTeamWf);

      // Else get change data from earliest transaction

      ChangeData changeData = AtsBranchManager.getChangeData(sourceTeamWf, targetedVersion);
      Collection<Artifact> addedModifiedArts =
         changeData.getArtifacts(KindType.Artifact, getAttributeTypesToIgnore(), ModificationType.INTRODUCED,
            ModificationType.NEW, ModificationType.MODIFIED, ModificationType.MERGED, ModificationType.APPLICABILITY);
      Collection<Artifact> deletedArts = changeData.getArtifacts(KindType.Artifact, ModificationType.DELETED);
      Collection<Artifact> relOnlyChangedArts = changeData.getArtifacts(KindType.RelationOnly,
         ModificationType.INTRODUCED, ModificationType.NEW, ModificationType.MODIFIED, ModificationType.MERGED,
         ModificationType.DELETED, ModificationType.APPLICABILITY);

      @SuppressWarnings("unchecked")
      Set<Artifact> allArtifacts = org.eclipse.osee.framework.jdk.core.util.Collections.setUnion(addedModifiedArts,
         deletedArts, relOnlyChangedArts);

      if (allArtifacts.isEmpty()) {
         rd.log("No matching artifacts to create tasks");
         return rd;
      }

      CreateTasksDefinition setDef =
         atsApi.getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken).getCreateTasksDef();
      if (setDef == null) {
         rd.errorf("No CreateTasksDefintiion found for id %s", taskDefToken.toStringWithId());
         return rd;
      }

      Collection<IAtsTeamDefinitionArtifactToken> toSiblingTeams = setDef.getChgRptOptions().getToSiblingTeams();
      if (toSiblingTeams.isEmpty()) {
         rd.errorf("No sibling team defs found for id %s", taskDefToken.toStringWithId());
         return rd;
      }

      // If branch is committed, bulk load the final versions of the change report artifacts
      if (atsApi.getBranchService().isBranchesAllCommitted(sourceTeamWf)) {
         ArtifactQuery.reloadArtifacts(allArtifacts);
      }

      // Verify that name is set for all loaded artifacts
      for (Artifact art : allArtifacts) {
         boolean fail = false;
         try {
            art.getName();
         } catch (Exception ex) {
            rd.addRaw(AHTML.addRowMultiColumnTable(taskDefToken.getName(),
               "Exception accessing name of " + atsApi.getAtsId(art) + " - " + ex.getLocalizedMessage(), ".", "."));
            fail = true;
         }
         if (fail) {
            XResultDataUI.report(rd, taskDefToken.getName());
            return rd;
         }
      }
      boolean reportOnly =
         getTaskDefinition().getChgRptOptions().getCreateOptions().contains(CreateTasksOption.ReportOnly);

      IAtsChangeSet changes = null;
      if (!reportOnly) {
         changes = atsApi.createChangeSet(setDef.getName());
      }
      for (IAtsTeamDefinitionArtifactToken teamDefTok : toSiblingTeams) {

         ChangeReportData crd = new ChangeReportData();
         crd.setReportOnly(reportOnly);
         crd.setRd(rd);
         crd.setSourceTeamWf(sourceTeamWf);
         WorkType workType = WorkType.None;
         if (teamDefTok.getName().contains("Code")) {
            workType = WorkType.Code;
         } else if (teamDefTok.getName().contains("Test")) {
            workType = WorkType.Test;
         }
         crd.setWorkType(workType);

         CreateTasksWorkflow workflowCreator = new CreateTasksWorkflow("", setDef.getChgRptOptions().getCreateOptions(),
            crd.getTaskedArtToName().values(), reportOnly, crd.getRd(), changes, new Date(), AtsCoreUsers.SYSTEM_USER,
            crd.getSourceTeamWf(), targetedVersion, crd.getWorkType(), null, null);

         IAtsTeamWorkflow destTeamWf = (TeamWorkFlowArtifact) workflowCreator.createMissingWorkflow().getStoreObject();

         // Compute task names; add to crd
         ChangeReportTasksUtil.getTaskNamesToBeCreated(crd, setDef, workType);

         // Compute missing tasks; add to crd
         ChangeReportTasksUtil.getReferencedArtsToTasks(crd, setDef, workType, destTeamWf);

         Set<ArtifactId> currentlyReferencedArts = crd.getReferencedArtsToTasks().keySet();
         Collection<ArtifactId> artsReferencedByTasks = crd.getTaskNamesToReqId().values();
         // if currently referenced and not should be, mark as no match
         for (ArtifactId currRef : currentlyReferencedArts) {
            if (!artsReferencedByTasks.contains(currRef)) {
               dereferenceTask(crd, crd.getReferencedArtsToTasks().get(currRef));
            }
         }
         for (ArtifactId artRef : artsReferencedByTasks) {
            if (!currentlyReferencedArts.contains(artRef)) {
               createTask(crd, artRef);
            }
         }
         // Call server to generate
         if (reportOnly) {
            XResultDataUI.report(crd.getRd(), "Create Change Report Tasks");
         } else {
            crd.getNewTaskData().setAsUserId(AtsClientService.get().getUserService().getCurrentUser().getUserId());
            crd.getNewTaskData().setCommitComment("Create Change Report Tasks");
            crd.getNewTaskData().setTeamWfId(destTeamWf.getId());

            NewTaskDatas newTasks = new NewTaskDatas();
            newTasks.getTaskDatas().add(crd.getNewTaskData());
            JaxAtsTasks jaxAtsTasks = AtsClientService.getTaskEp().create(newTasks);
            if (jaxAtsTasks.getResults().isErrors()) {
               crd.getRd().addRaw(jaxAtsTasks.getResults().toString());
            } else {
               // Reload wf and tasks
            }
         }
      }

      return rd;
   }

   private void createTask(ChangeReportData crd, ArtifactId artRef) {
      ArtifactToken artRefTok = atsApi.getQueryService().getArtifactToken(artRef);
      crd.getRd().logf("No matching task for artifact %s; Create new task.", artRefTok.toStringWithId());
      if (crd.isPersist()) {
         for (Entry<String, ArtifactId> entry : crd.getTaskNamesToReqId().entrySet()) {
            if (artRef.equals(entry.getValue())) {
               JaxAtsTask task = new JaxAtsTask();
               task.setName(entry.getKey());
               task.setAssigneeUserIds(Arrays.asList(AtsCoreUsers.UNASSIGNED_USER.getUserId()));
               task.addAttribute(AtsAttributeTypes.TaskToChangedArtifactReference, entry.getValue());
            }
         }
         // Add to task creation definition
         // Add reference attribute
      }
   }

   private void dereferenceTask(ChangeReportData crd, IAtsTask task) {
      crd.getRd().logf("No matching artifact for Task %s; De-referenced task can be deleted.", task.toStringWithId());
      if (crd.isPersist()) {
         // TBD
         // remove task reference
         // add no-match to task title?
         // remove AutoGen static id
      }
   }

   protected CreateTasksDefinition getTaskDefinition() {
      return AtsClientService.get().getTaskSetDefinitionProviderService().getTaskSetDefinition(
         taskDefToken).getCreateTasksDef();
   }

   protected Collection<AttributeTypeId> getAttributeTypesToIgnore() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
         getTaskDefinition().getChgRptOptions().getNotAttributeTypes());
   }

}
