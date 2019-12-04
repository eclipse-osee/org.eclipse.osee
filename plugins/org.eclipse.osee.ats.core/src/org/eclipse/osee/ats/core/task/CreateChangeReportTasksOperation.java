/*
 * Created on Sep 15, 2019
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
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
import org.eclipse.osee.ats.api.task.create.StaticTaskDefinition;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
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

/**
 * Rules:<br/>
 * <br/>
 * <br/>
 * Default rules that can be overridden:<br/>
 * <br/>
 * If artifact attribute was changed, task will be created as Add/Mod and task static id set to AutoGenTask so can't be
 * deleted by users.<br/>
 * If artifact relation was changed, task will be created for both artifacts as as Relation and task static id set to
 * AutoGenTask so can't be deleted by users.<br/>
 * If artifact was deleted, task created with "<name> (Deleted)" as name.<br/>
 * <br/>
 * <br/>
 * Default rules that can NOT be overridden:<br/>
 * <br/>
 * If static tasks (non change report driven) are defined, they will be created upon first task generation.<br/>
 * If task created and then artifact name changes, old will be marked as de-referenced and new task created with new
 * name.<br/>
 * If task created and then artifact change reverted, task notes attribute will be appended with "No Matching Artifact
 * Change" and the AutoGenTask static id attribute will be removed, this allows anyone to delete task.<br/>
 *
 * @author Donald G. Dunne
 */
public class CreateChangeReportTasksOperation {

   private final AtsTaskDefToken taskDefToken;
   private final AtsApi atsApi;
   private final ChangeReportTaskData crtd;

   public CreateChangeReportTasksOperation(ChangeReportTaskData crtd, AtsApi atsApi, IAtsChangeSet changes) {
      this.crtd = crtd;
      this.taskDefToken = crtd.getTaskDefToken();
      this.atsApi = atsApi;
      if (changes != null) {
         this.crtd.setChanges(changes);
      }

   }

   public ChangeReportTaskData run() {
      XResultData rd = crtd.getResults();

      System.err.println("CreateChangeReportTasksOperation.run");

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

         // Calculate all change items that would need a task created
         ChangeReportTasksUtil.processChangeData(crtd);
         if (crtd.getAllArtifacts().isEmpty()) {
            rd.log("No matching artifacts to create tasks\n");
            return crtd;
         }

         // Verify that name is set for all loaded artifacts
         boolean fail = false;
         Map<ArtifactId, ArtifactToken> idToArtifact = new HashMap<ArtifactId, ArtifactToken>();
         for (ArtifactId art : crtd.getAllArtifacts()) {
            try {
               ArtifactToken artifact =
                  atsApi.getQueryService().getArtifact(art, crtd.getWorkOrParentBranch(), DeletionFlag.INCLUDE_DELETED);
               artifact.getName();
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
            // Then, going to compute/match all that are needed
            // Later, all ChangeReportTaskMatch that have no task, create new task
            //        all ChangeReportTaskMatch that have found == false, mark to dereference task

            // Compute what tasks are needed from changes; add task matches to crd
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
               else if (matchType == ChangeReportTaskMatchType.ChangedReportTaskComputedAsNeeded) {
                  if (!addModTaskNames.contains(taskMatch.getTaskName())) {
                     crtd.getResults().warningf("Create new chg rpt task [%s]\n", taskMatch.getTaskName());
                     addToNewTaskData(crtd, crttwd, taskMatch, createdDate);
                     addModTaskNames.add(taskMatch.getTaskName());
                  }
               }

               // Create if no task was found
               else if (matchType == ChangeReportTaskMatchType.StaticTaskComputedAsNeeded) {
                  if (!addModTaskNames.contains(taskMatch.getTaskName())) {
                     crtd.getResults().warningf("Create new static task [%s]\n", taskMatch.getTaskName());
                     addToNewTaskData(crtd, crttwd, taskMatch, createdDate);
                     addModTaskNames.add(taskMatch.getTaskName());
                  }
               }

               // Mark as de-referenced if no reference or referenced artifact not there anymore
               else if ((matchType == ChangeReportTaskMatchType.TaskRefAttrMissing) || (matchType == ChangeReportTaskMatchType.TaskRefAttrValidButRefChgArtMissing)) {
                  if (!deletedTaskNames.contains(taskMatch.getTaskName())) {
                     crtd.getResults().warningf(
                        "No matching artifact for Task %s; De-referenced task can be deleted.\n",
                        taskMatch.getTaskWf().toStringWithId());
                     if (!reportOnly) {
                        if (changes == null) {
                           crtd.getResults().errorf("AtsChangeSet can not be null.");
                        } else {
                           // Add StaticId that will keep task from being deleted
                           changes.deleteAttribute(taskMatch.getTaskWf(), CoreAttributeTypes.StaticId,
                              ChangeReportTasksUtil.AUTO_GENERATED_STATIC_ID);

                           // Add note to user that task is de-referenced
                           String note = atsApi.getAttributeResolver().getSoleAttributeValue(taskMatch.getTaskWf(),
                              AtsAttributeTypes.WorkflowNotes, "");
                           if (!note.contains(ChangeReportTasksUtil.DE_REFERRENCED_NOTE)) {
                              note = note + ChangeReportTasksUtil.DE_REFERRENCED_NOTE;
                              changes.setSoleAttributeValue(taskMatch.getTaskWf(), AtsAttributeTypes.WorkflowNotes,
                                 note);
                           }
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

               /**
                * Until all transition is done on server, need to call to directly generate tasks so it can be part of
                * the transition change set. Otherwise calling server to generate tasks will result in teamWf and tasks
                * to be reloaded and thus lose transition data.
                */
               CreateTasksOperation operation =
                  new CreateTasksOperation(crttwd.getNewTaskData(), atsApi, crtd.getResults());
               operation.setIdToTeamWf(crtd.getIdToTeamWf());
               operation.validate();
               if (crtd.getResults().isSuccess()) {
                  operation.run(changes);
                  if (crtd.getResults().isErrors()) {
                     return crtd;
                  }
               }
            }
         }

         if (!reportOnly && changes != null) {
            TransactionId transId = changes.executeIfNeeded();
            if (transId != null && transId.isValid()) {
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

   private void addToNewTaskData(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, ChangeReportTaskMatch taskMatch, Date createdDate) {
      JaxAtsTask task = new JaxAtsTask();
      task.setName(taskMatch.getTaskName());
      if (taskMatch.getMatchType() == ChangeReportTaskMatchType.StaticTaskComputedAsNeeded) {
         StaticTaskDefinition createTaskDef = taskMatch.getCreateTaskDef();
         List<ArtifactId> assigneeeAccountIds = new LinkedList<>();
         for (Long id : createTaskDef.getAssigneeAccountIds()) {
            assigneeeAccountIds.add(ArtifactId.valueOf(id));
         }
         task.setAssigneeAccountIds(assigneeeAccountIds);
         if (Strings.isValid(createTaskDef.getDescription())) {
            task.setDescription(createTaskDef.getDescription());
         }
         if (Strings.isValid(createTaskDef.getRelatedToState())) {
            task.setRelatedToState(createTaskDef.getRelatedToState());
         }
      } else if (taskMatch.getMatchType() == ChangeReportTaskMatchType.ChangedReportTaskComputedAsNeeded) {
         task.addAttribute(AtsAttributeTypes.TaskToChangedArtifactReference, taskMatch.getChgRptArt());
         task.setAssigneeUserIds(Arrays.asList(AtsCoreUsers.UNASSIGNED_USER.getUserId()));
      } else {
         crtd.getResults().errorf("Un-handled MatchType [%s]", taskMatch.getMatchType().name());
         return;
      }
      task.setCreatedDate(createdDate);
      if (crtd.getAsUser() != null) {
         AtsUser asUser = crtd.getAsUser();
         task.setCreatedByUserId(asUser.getUserId());
      } else {
         task.setCreatedByUserId(AtsCoreUsers.SYSTEM_USER.getUserId());
      }
      task.addAttribute(CoreAttributeTypes.StaticId, ChangeReportTasksUtil.AUTO_GENERATED_STATIC_ID);
      crttwd.getNewTaskData().getNewTasks().add(task);
   }

   protected CreateTasksDefinition getTaskDefinition() {
      return atsApi.getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken).getCreateTasksDef();
   }

   protected Collection<AttributeTypeId> getAttributeTypesToIgnore() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
         getTaskDefinition().getChgRptOptions().getNotAttributeTypes());
   }

}
