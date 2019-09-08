/*
 * Created on Sep 15, 2019
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.rest.internal.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.config.tx.AtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.CreateTasksOption;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskDatas;
import org.eclipse.osee.ats.api.task.create.ChangeReportOptions;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTask;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.ChangeReportTasksUtil;
import org.eclipse.osee.ats.core.task.CreateTasksWorkflow;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Lib;

public class CreateChangeReportTasksOperation {

   private final AtsTaskDefToken taskDefToken;
   private final AtsApi atsApi;
   private final ChangeReportTaskData crtd;

   public CreateChangeReportTasksOperation(ChangeReportTaskData crtd, AtsApi atsApi) {
      this.crtd = crtd;
      this.taskDefToken = crtd.getTaskDefToken();
      this.atsApi = atsApi;
   }

   public ChangeReportTaskData run() {
      XResultData rd = crtd.getResults();

      try {
         if (crtd.getHostTeamWf() == null || crtd.getHostTeamWf().isInvalid()) {
            rd.error("No Host Team Workflow specified.\n");
            return crtd;
         }
         IAtsTeamWorkflow hostTeamWf = atsApi.getQueryService().getTeamWf(crtd.getHostTeamWf());
         if (hostTeamWf == null) {
            rd.error("No Host Team Workflow can be found.\n");
            return crtd;
         }
         rd.logf("Creating from host Team Wf %s\n", hostTeamWf.toStringWithId());

         CreateTasksDefinitionBuilder taskSetDefinition =
            atsApi.getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken);
         if (taskSetDefinition == null) {
            rd.errorf("No CreateTasksDefintiion found for Task Def id %s\n", taskDefToken.toStringWithId());
            return crtd;
         }
         CreateTasksDefinition setDef = taskSetDefinition.getCreateTasksDef();
         rd.logf("Creating tasks for task definition %s\n", setDef.toStringWithId());

         crtd.setSetDef(setDef);

         // TBD Add configuration to allow task to be created on host workflow?
         // Team Wf owning change report
         IAtsTeamWorkflow chgRptTeamWf = null;
         if (crtd.getChgRptTeamWf().isValid()) {
            chgRptTeamWf = atsApi.getQueryService().getTeamWf(crtd.getHostTeamWf());
         } else {
            ChangeReportOptions opts = setDef.getChgRptOptions();
            IAtsTeamDefinitionArtifactToken fromSiblingTeam =
               AtsTeamDefinitionArtifactToken.valueOf(opts.getFromSiblingTeamDef());
            Collection<IAtsTeamWorkflow> siblings =
               atsApi.getWorkItemService().getSiblings(hostTeamWf, fromSiblingTeam);
            if (siblings.size() > 1 || siblings.isEmpty()) {
               rd.errorf("Expeceted one source sibling workflow, found %s\n", siblings);
               return crtd;
            }
            chgRptTeamWf = siblings.iterator().next();
         }
         rd.logf("Using Change Report Team Wf %s\n", chgRptTeamWf.toStringWithId());
         crtd.setChgRptTeamWf(chgRptTeamWf.getStoreObject());

         ChangeReportTasksUtil.getBranchOrCommitChangeData(crtd, setDef);
         if (crtd.getResults().isErrors()) {
            return crtd;
         }

         Map<String, String> toSiblingTeamAis = setDef.getChgRptOptions().getToSiblingTeamAiMap();
         if (toSiblingTeamAis.isEmpty()) {
            rd.errorf("No sibling team defs found for id %s\n", taskDefToken.toStringWithId());
            return crtd;
         }

         ChangeReportTasksUtil.processChangeData(crtd);
         if (crtd.getAllArtifacts().isEmpty()) {
            rd.log("No matching artifacts to create tasks\n");
            return crtd;
         }

         // Verify that name is set for all loaded artifacts
         boolean fail = false;
         for (ArtifactId art : crtd.getAllArtifacts()) {
            try {
               ArtifactToken artifact =
                  atsApi.getQueryService().getArtifact(art, crtd.getWorkOrParentBranch(), DeletionFlag.INCLUDE_DELETED);
               artifact.getName();
            } catch (Exception ex) {
               rd.errorf("Exception accessing name of %s - %s\n", art.getId(), ex.getLocalizedMessage());
               fail = true;
            }
         }
         if (fail) {
            return crtd;
         }

         boolean reportOnly =
            getTaskDefinition().getChgRptOptions().getCreateOptions().contains(CreateTasksOption.ReportOnly);
         crtd.setReportOnly(reportOnly);
         rd.logf("Report Only %s\n", reportOnly);

         IAtsChangeSet changes = null;
         if (!reportOnly) {
            changes = atsApi.createChangeSet(setDef.getName());
         }
         for (Entry<String, String> teamDefAi : toSiblingTeamAis.entrySet()) {
            IAtsTeamDefinition teamDef =
               atsApi.getTeamDefinitionService().getTeamDefinitionById(ArtifactId.valueOf(teamDefAi.getKey()));
            rd.logf("\n\nHandling Team %s\n", teamDef.toStringWithId());

            ChangeReportTaskTeamWfData crttwd = new ChangeReportTaskTeamWfData();
            crtd.addChangeReportData(crttwd);
            crttwd.setReportOnly(reportOnly);
            crttwd.setRd(rd);
            crttwd.setChgRptTeamWf(chgRptTeamWf.getStoreObject());
            WorkType workType = WorkType.None;
            if (teamDef.getName().contains("Code")) {
               workType = WorkType.Code;
            } else if (teamDef.getName().contains("Test")) {
               workType = WorkType.Test;
            }
            crttwd.setWorkType(workType);

            IAtsVersion targetedVersion = atsApi.getVersionService().getTargetedVersion(chgRptTeamWf);
            IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItem(teamDefAi.getValue());
            if (ai == null || ai.isInvalid()) {
               rd.errorf("Actionable Item  %s is invalid for team %s\n", teamDefAi.getValue(), teamDefAi.getKey());
               return crtd;
            }
            CreateTasksWorkflow workflowCreator =
               new CreateTasksWorkflow("", setDef.getChgRptOptions().getCreateOptions(),
                  crttwd.getTaskedArtToName().values(), reportOnly, crttwd.getRd(), changes, new Date(),
                  AtsCoreUsers.SYSTEM_USER, chgRptTeamWf, targetedVersion, crttwd.getWorkType(), null, null);
            workflowCreator.setActionableItem(ai);

            // Compute task names; add to crd
            ChangeReportTasksUtil.getTaskNamesFromChanges(crtd, crttwd, atsApi);

            // Create destination teamWf if tasks are needed and not already exist
            IAtsTeamWorkflow destTeamWf = workflowCreator.createMissingWorkflow();
            if (destTeamWf == null) {
               crttwd.getRd().logf("No Destination Team Created/Needed for %s\n", teamDef.toStringWithId());
               continue;
            }
            crttwd.setDestTeamWf(destTeamWf.getStoreObject());
            rd.logf("Destination Team Wf %s\n", destTeamWf.toStringWithId());

            // Compute missing tasks; add to crd
            ChangeReportTasksUtil.getReferencedArtsToTasks(crttwd, setDef, workType, destTeamWf);

            Set<ArtifactId> currentlyReferencedArts = crttwd.getReferencedArtsToTasks().keySet();
            Collection<ArtifactId> artsReferencedByTasks = crttwd.getTaskNamesToReqId().values();

            // if currently referenced and not should be, mark as no match
            for (ArtifactId currRef : currentlyReferencedArts) {
               if (!artsReferencedByTasks.contains(currRef)) {
                  IAtsTask task = crttwd.getReferencedArtsToTasks().get(currRef);
                  dereferenceTask(crttwd, task);
               }
            }
            for (ArtifactId artRef : artsReferencedByTasks) {
               if (!currentlyReferencedArts.contains(artRef)) {
                  createTask(crttwd, artRef);
               }
            }

            if (reportOnly) {
               return crtd;
            } else {
               crttwd.getNewTaskData().setAsUserId(crtd.getAsUser().getUserId());
               crttwd.getNewTaskData().setCommitComment("Create Change Report Tasks");
               crttwd.getNewTaskData().setTeamWfId(destTeamWf.getId());

               NewTaskDatas newTasks = new NewTaskDatas();
               newTasks.getTaskDatas().add(crttwd.getNewTaskData());
               atsApi.getTaskService().createTasks(newTasks);
            }
         }

      } catch (Exception ex) {
         crtd.results.errorf("Exception creating tasks %s", Lib.exceptionToString(ex));
      }
      return crtd;
   }

   private void createTask(ChangeReportTaskTeamWfData crd, ArtifactId artRef) {
      ArtifactToken artRefTok = atsApi.getQueryService().getArtifactToken(artRef);
      crd.getRd().logf("No matching task for artifact %s; Create new task.", artRefTok.toStringWithId());
      Set<Entry<String, ArtifactId>> entrySet = crd.getTaskNamesToReqId().entrySet();
      if (crd.isPersist()) {
         for (Entry<String, ArtifactId> entry : crd.getTaskNamesToReqId().entrySet()) {
            if (artRef.equals(entry.getValue())) {
               JaxAtsTask task = new JaxAtsTask();
               task.setName(entry.getKey());
               task.setAssigneeUserIds(Arrays.asList(AtsCoreUsers.UNASSIGNED_USER.getUserId()));
               task.addAttribute(AtsAttributeTypes.TaskToChangedArtifactReference, entry.getValue());
               crd.getNewTaskData().getNewTasks().add(task);
               crtd.results.logf("Create task %s", task.toStringWithId());
            }
         }
      }
   }

   private void dereferenceTask(ChangeReportTaskTeamWfData crd, IAtsTask task) {
      crd.getRd().logf("No matching artifact for Task %s; De-referenced task can be deleted.", task.toStringWithId());
      if (crd.isPersist()) {
         // TBD
         // remove task reference
         // add no-match to task title?
         // remove AutoGen static id
      }
   }

   protected CreateTasksDefinition getTaskDefinition() {
      return atsApi.getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken).getCreateTasksDef();
   }

   protected Collection<AttributeTypeId> getAttributeTypesToIgnore() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
         getTaskDefinition().getChgRptOptions().getNotAttributeTypes());
   }

}
