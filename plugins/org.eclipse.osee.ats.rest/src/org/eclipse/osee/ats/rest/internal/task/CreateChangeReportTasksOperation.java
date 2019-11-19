/*
 * Created on Sep 15, 2019
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.rest.internal.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.config.tx.AtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.config.tx.IAtsTeamDefinitionArtifactToken;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.task.CreateTasksOption;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.create.ChangeReportOptions;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatch;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatchType;
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
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.orcs.data.ArtifactReadable;

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
         crtd.getIdToTeamWf().put(hostTeamWf.getId(), hostTeamWf);
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
         crtd.getIdToTeamWf().put(chgRptTeamWf.getId(), chgRptTeamWf);
         crtd.setChgRptTeamWf(chgRptTeamWf.getStoreObject());
         crtd.setActionId(chgRptTeamWf.getParentAction().getStoreObject());

         ChangeReportTasksUtil.getBranchOrCommitChangeData(crtd, setDef);
         if (crtd.getResults().isErrors()) {
            return crtd;
         }

         Map<String, String> toSiblingTeamAis = setDef.getChgRptOptions().getToSiblingTeamAiMap();
         if (toSiblingTeamAis.isEmpty()) {
            rd.errorf("No sibling team defs found for id %s\n", taskDefToken.toStringWithId());
            return crtd;
         }
         rd.logf("For Sibling Team AIs [%s]\n", Collections.toString(", ", toSiblingTeamAis.values()));

         ChangeReportTasksUtil.processChangeData(crtd);
         if (crtd.getAllArtifacts().isEmpty()) {
            rd.log("No matching artifacts to create tasks\n");
            return crtd;
         }

         // Verify that name is set for all loaded artifacts
         boolean fail = false;
         Map<ArtifactId, ArtifactReadable> idToArtifact = new HashMap<ArtifactId, ArtifactReadable>();
         for (ArtifactId art : crtd.getAllArtifacts()) {
            try {
               ArtifactReadable artifact = (ArtifactReadable) atsApi.getQueryService().getArtifact(art,
                  crtd.getWorkOrParentBranch(), DeletionFlag.INCLUDE_DELETED);
               artifact.getSafeName();
               idToArtifact.put(artifact, artifact);
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
            crttwd.getAddedModifiedArts().addAll(crtd.getAddedModifiedArts());
            crttwd.getDeletedArts().addAll(crtd.getDeletedArts());
            crttwd.getRelArts().addAll(crtd.getRelArts());
            crttwd.setDestTeamDef(teamDef.getStoreObject());

            WorkType workType = WorkType.None;
            String workTypeStr =
               atsApi.getAttributeResolver().getSoleAttributeValue(teamDef, AtsAttributeTypes.WorkType, "");
            if (Strings.isValid(workTypeStr)) {
               workType = WorkType.valueOfOrNone(workTypeStr);
            }
            if (workType == WorkType.None) {
               if (teamDef.getName().contains("Code")) {
                  workType = WorkType.Code;
               } else if (teamDef.getName().contains("Test")) {
                  workType = WorkType.Test;
               }
            }
            if (workType == WorkType.None) {
               rd.errorf("\n\nCan't determine Work Type from Team Def or Name%s\n", teamDef.toStringWithId());
               return crtd;
            }
            crttwd.setWorkType(workType);

            IAtsVersion targetedVersion = atsApi.getVersionService().getTargetedVersion(chgRptTeamWf);
            IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItem(teamDefAi.getValue());
            if (ai == null || ai.isInvalid()) {
               rd.errorf("Actionable Item  %s is invalid for team %s\n", teamDefAi.getValue(), teamDefAi.getKey());
               return crtd;
            }

            // Going to create a ChangeReportTaskMatch for all the exist
            // Then, going to compute/match all that are needed (using boolean ChangeReportTaskMatch.found == true?)
            // Later, all ChangeReportTaskMatch that have no task, create new task
            //        all ChangeReportTaskMatch that have found == false, mark to remove task

            // Compute task matches needed; add to crd
            ChangeReportTasksUtil.getTasksComputedAsNeeded(crtd, crttwd, atsApi);
            for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
               if (crtd.isDebug()) {
                  crtd.getResults().logf("Task Computed as Needed [%s]\n", taskMatch.toString());
               }
            }

            // Get or create destTeamWf
            IAtsTeamWorkflow destTeamWf =
               ChangeReportTasksUtil.getDestTeamWfOrNull(crttwd, workType, atsApi, chgRptTeamWf, teamDef);
            if (destTeamWf == null) {
               CreateTasksWorkflow workflowCreator = new CreateTasksWorkflow(hostTeamWf.getName(),
                  setDef.getChgRptOptions().getCreateOptions(), true, reportOnly, crttwd.getRd(), changes, new Date(),
                  AtsCoreUsers.SYSTEM_USER, chgRptTeamWf, targetedVersion, crttwd.getWorkType(), null, null);
               workflowCreator.setActionableItem(ai);
               destTeamWf = workflowCreator.createMissingWorkflow();
               if (changes != null) {
                  changes.relate(chgRptTeamWf, AtsRelationTypes.Derive_To, destTeamWf);
               }
               rd.logf("Created Destination Team Wf %s\n", destTeamWf.toStringWithId());
            } else {
               rd.logf("Using existing Destination Team Wf %s\n", destTeamWf.toStringWithId());
            }
            crttwd.setDestTeamWf(destTeamWf.getStoreObject());
            crtd.getIdToTeamWf().put(destTeamWf.getId(), destTeamWf);
            crtd.getDestTeamWfs().add(ArtifactToken.valueOf(destTeamWf.getStoreObject().getId(), destTeamWf.getName(),
               BranchId.valueOf(atsApi.getAtsBranch().getId())));

            // Compute missing tasks; add task or null to crttwd.ChangeReportTaskMatch objects
            ChangeReportTasksUtil.determinExistingTaskMatchType(idToArtifact, crtd, crttwd, setDef, workType,
               destTeamWf);

            Set<String> addModTaskNames = new HashSet<>();
            Set<String> deletedTaskNames = new HashSet<>();
            Date createdDate = new Date();
            // Log changes needed; Add to delete if needed or to NewTaskData
            for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
               ChangeReportTaskMatchType matchType = taskMatch.getMatchType();
               // Skip if task already exists
               if (matchType == ChangeReportTaskMatchType.Match) {
                  crtd.getResults().logf("Task %s Exists - No Change Needed\n", taskMatch.getTaskWf().toStringWithId());
               }

               // Create if no task was found
               else if (matchType == ChangeReportTaskMatchType.TaskComputedAsNeeded) {
                  if (!addModTaskNames.contains(taskMatch.getTaskName())) {
                     crtd.getResults().warningf("Create new task [%s]\n", taskMatch.getTaskName());
                     addToNewTaskData(crttwd, taskMatch, createdDate);
                     addModTaskNames.add(taskMatch.getTaskName());
                  }
               }

               // Delete if no reference or referenced artifact not there anymore
               else if ((matchType == ChangeReportTaskMatchType.TaskRefAttrMissing) || (matchType == ChangeReportTaskMatchType.TaskRefAttrValidButRefChgArtMissing)) {
                  if (!deletedTaskNames.contains(taskMatch.getTaskName())) {
                     crtd.getResults().warningf("Delete un-referenced task [%s]\n", taskMatch.getTaskName());
                     if (!reportOnly) {
                        if (changes == null) {
                           crtd.getResults().errorf("AtsChangeSet can not be null.");
                        } else {
                           changes.deleteArtifact(taskMatch.getTaskWf());
                        }
                     }
                     deletedTaskNames.add(taskMatch.getTaskName());
                  }
               } else {
                  crtd.getResults().errorf("Unhandled Match Type [%s]\n", taskMatch.getMatchType().name());
               }
            }

            if (reportOnly) {
               return crtd;
            } else if (!crttwd.getNewTaskData().getNewTasks().isEmpty()) {
               crttwd.getNewTaskData().setAsUserId(crtd.getAsUser().getUserId());
               crttwd.getNewTaskData().setCommitComment("Create Change Report Tasks");
               crttwd.getNewTaskData().setTeamWfId(destTeamWf.getId());

               atsApi.getTaskService().createTasks(crttwd.getNewTaskData(), changes, rd, crtd.getIdToTeamWf());
            }
         }

         if (!reportOnly && changes != null) {
            TransactionId transId = changes.executeIfNeeded();
            if (transId.isValid()) {
               crtd.setTransaction(transId);
               crtd.getResults().log("\nTasks Updated\n");
            } else {
               crtd.getResults().log("\nNo Changes Needed\n");
            }
         }
      } catch (Exception ex) {
         crtd.results.errorf("Exception creating tasks %s", Lib.exceptionToString(ex));
      }
      return crtd;
   }

   private void addToNewTaskData(ChangeReportTaskTeamWfData crttwd, ChangeReportTaskMatch taskMatch, Date createdDate) {
      JaxAtsTask task = new JaxAtsTask();
      task.setName(taskMatch.getTaskName());
      task.setCreatedDate(createdDate);
      task.setCreatedByUserId(AtsCoreUsers.SYSTEM_USER.getUserId());
      task.setAssigneeUserIds(Arrays.asList(AtsCoreUsers.UNASSIGNED_USER.getUserId()));
      task.addAttribute(AtsAttributeTypes.TaskToChangedArtifactReference, taskMatch.getChgRptArt());
      task.addAttribute(CoreAttributeTypes.StaticId, ChangeReportTasksUtil.AUTO_GENERATED_STATIC_ID);
      crttwd.getNewTaskData().getNewTasks().add(task);
   }

   // TBD - needed anymore?  We're just deleting un-referenced
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
