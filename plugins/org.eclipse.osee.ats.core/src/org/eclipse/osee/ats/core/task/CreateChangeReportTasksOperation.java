/*********************************************************************
 * Copyright (c) 2019 Boeing
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

package org.eclipse.osee.ats.core.task;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.commit.CommitConfigItem;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.data.AtsTaskDefToken;
import org.eclipse.osee.ats.api.program.IAtsProgram;
import org.eclipse.osee.ats.api.task.CreateTasksOption;
import org.eclipse.osee.ats.api.task.IAtsTaskProvider;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportOptionsToTeam;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskData;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatch;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatchResult;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskMatchType;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskNameProviderToken;
import org.eclipse.osee.ats.api.task.create.ChangeReportTaskTeamWfData;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinition;
import org.eclipse.osee.ats.api.task.create.CreateTasksDefinitionBuilder;
import org.eclipse.osee.ats.api.task.create.IAtsChangeReportTaskNameProvider;
import org.eclipse.osee.ats.api.task.create.StaticTaskDefinition;
import org.eclipse.osee.ats.api.task.related.AutoGenVersion;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsCoreUsers;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.task.internal.AtsTaskProviderCollector;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.result.table.XResultTable;
import org.eclipse.osee.framework.jdk.core.result.table.XResultTableColumn;
import org.eclipse.osee.framework.jdk.core.result.table.XResultTableDataType;
import org.eclipse.osee.framework.jdk.core.result.table.XResultTableRow;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * See AutomaticTaskGenDesign.adoc
 *
 * @author Donald G. Dunne
 */
public class CreateChangeReportTasksOperation {

   private final AtsTaskDefToken taskDefToken;
   private final AtsApi atsApi;
   private final ChangeReportTaskData crtd;
   private final static String TASK_GEN_TAG = "taskgen";

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
      rd.log(crtd.getOperationName() + "\n");

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
         // Multiple TaskSetDefinitions can be declared, ensure this one is applicable to be run
         CreateTasksDefinition setDef = taskSetDefinition.getCreateTasksDef();
         if (!setDef.getHelper().isApplicable(hostTeamWf, atsApi)) {
            rd.logf("CreateTaskSetDefinition not applicable for %s", hostTeamWf.toStringWithId());
            return crtd;
         }
         rd.logf("Creating tasks for task definition %s\n", setDef.toStringWithId());

         crtd.setSetDef(setDef);

         // Skip if the program team defs doesn't contain host wf team def
         IAtsProgram program = atsApi.getProgramService().getProgram(hostTeamWf);
         IAtsTeamDefinition fromSiblingTeamDef = getFromSiblingTeamDef(setDef, program, rd);
         if (!atsApi.getProgramService().getTeamDefs(program).contains(fromSiblingTeamDef)) {
            rd.logf("Host Team Wf Team Definition %s does not belong to Program %s Team Definitions; skipping",
               hostTeamWf.getTeamDefinition().toStringWithId(), program.toStringWithId());
            return crtd;
         }

         // Team Wf owning change report
         IAtsTeamWorkflow chgRptTeamWf = null;
         if (crtd.getChgRptTeamWf().isValid()) {
            chgRptTeamWf = atsApi.getQueryService().getTeamWf(crtd.getHostTeamWf());
         } else {
            IAtsTeamDefinition fromSiblingTeamD = getFromSiblingTeamDef(setDef, program, rd);
            Collection<IAtsTeamWorkflow> siblings =
               atsApi.getWorkItemService().getSiblings(hostTeamWf, fromSiblingTeamD);
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

         // Generate and store ChangeItems from branch or commit
         ChangeReportTasksUtil.getBranchOrCommitChangeData(crtd, setDef);
         if (crtd.getResults().isErrors()) {
            return crtd;
         }

         List<ChangeReportOptionsToTeam> toSiblingTeamDatas = setDef.getChgRptOptions().getToSiblingTeamDatas();
         if (toSiblingTeamDatas.isEmpty()) {
            rd.errorf("No sibling team defs found for id %s\n", taskDefToken.toStringWithId());
            return crtd;
         }
         rd.logf("For Sibling Team AIs [%s]\n", Collections.toString(", ", toSiblingTeamDatas));

         boolean reportOnly = crtd.isReportOnly() || getTaskDefinition().getChgRptOptions().getCreateOptions().contains(
            CreateTasksOption.ReportOnly);
         crtd.setReportOnly(reportOnly);
         rd.logf("Report Only %s\n", reportOnly);

         IAtsChangeSet changes = null;
         if (!reportOnly) {
            changes = atsApi.createChangeSet(setDef.getName(), crtd.getAsUser());
         }
         for (ChangeReportOptionsToTeam toSiblingTeamDef : toSiblingTeamDatas) {
            IAtsTeamDefinition toTeamDef = getToTeamDef(setDef, program, rd, toSiblingTeamDef);
            if (toTeamDef == null) {
               rd.errorf("\n\nCan't determine toTeamDef from Team Def or Work Type %s\n", toSiblingTeamDef);
               return crtd;
            }
            rd.logf("\n\nHandling Team %s\n", toTeamDef.toStringWithId());

            ChangeReportTaskTeamWfData crttwd = new ChangeReportTaskTeamWfData();
            crtd.addChangeReportData(crttwd);
            crttwd.setReportOnly(reportOnly);
            crttwd.setRd(rd);
            crttwd.setChgRptTeamWf(chgRptTeamWf.getStoreObject());
            crttwd.setDestTeamDef(toTeamDef.getStoreObject());

            WorkType workType = WorkType.None;
            Collection<WorkType> workTypes = toTeamDef.getWorkTypes();
            if (workTypes.contains(WorkType.Code) || toTeamDef.getName().contains("Code")) {
               workType = WorkType.Code;
            } else if (workTypes.contains(WorkType.Test) || toTeamDef.getName().contains("Test")) {
               workType = WorkType.Test;
            }
            if (workType == WorkType.None) {
               rd.errorf("\n\nCan't determine Work Type from Team Def or Name %s\n", toTeamDef.toStringWithId());
               return crtd;
            }
            crttwd.setWorkType(workType);

            IAtsVersion targetedVersion = atsApi.getVersionService().getTargetedVersion(chgRptTeamWf);
            if (targetedVersion == null || targetedVersion.isInvalid()) {
               rd.errorf("Targeted Version is invalid for Host Team Workflow %s\n", chgRptTeamWf.toStringWithAtsId());
               return crtd;
            }
            IAtsActionableItem ai = getToSiblingAi(setDef, program, rd, toSiblingTeamDef);
            if (ai == null || ai.isInvalid()) {
               rd.errorf("Actionable Item  %s is invalid for team %s or work type %s\n", toSiblingTeamDef.getAiId(),
                  toSiblingTeamDef.getTeamId(), toSiblingTeamDef.getWorkType());
               return crtd;
            }

            // Get task name provider if supplied or set to default
            ChangeReportTaskNameProviderToken nameProviderId = toSiblingTeamDef.getNameProviderId();
            if (nameProviderId == null) {
               nameProviderId = ChangeReportTaskNameProviderToken.DefaultChangeReportOptionsNameProvider;
            }
            IAtsChangeReportTaskNameProvider nameProvider =
               atsApi.getTaskService().getChangeReportOptionNameProvider(nameProviderId);
            if (nameProvider == null) {
               crtd.getResults().errorf("Can't find IAtsChangeReportTaskNameProvider for provider id %s",
                  nameProviderId.toStringWithId());
               return crtd;
            }

            // Add task match for each task that is needed (matches to existing tasks will be later)
            Map<ArtifactId, ArtifactToken> idToArtifact = nameProvider.getTasksComputedAsNeeded(crtd, crttwd, atsApi);
            addIfDebug("============== Task Computed As Needed ================================\n");
            for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
               addIfDebug("[%s]\n", taskMatch.toString().replaceFirst("mapped to task none", ""));
            }
            addIfDebug("=======================================================================\n");

            // Return if not tasks to create?
            if (crttwd.getTaskMatches().isEmpty()) {
               rd.success("No Tasks to Generate");
               continue;
            }

            // Get or create destTeamWf
            IAtsTeamWorkflow destTeamWf =
               ChangeReportTasksUtil.getDestTeamWfOrNull(crttwd, workType, atsApi, chgRptTeamWf, toTeamDef);
            NewTaskData newTaskData = NewTaskData.create(crttwd.getNewTaskSet(), destTeamWf);
            if (destTeamWf == null) {
               if (changes != null) {
                  CreateTasksWorkflow workflowCreator =
                     new CreateTasksWorkflow(hostTeamWf.getName(), setDef.getChgRptOptions().getCreateOptions(), true,
                        reportOnly, crttwd.getRd(), changes, new Date(), AtsCoreUsers.SYSTEM_USER, chgRptTeamWf,
                        new CommitConfigItem(targetedVersion, atsApi), crttwd.getWorkType(), null, null);
                  workflowCreator.setActionableItem(ai);
                  destTeamWf = workflowCreator.createMissingWorkflow();
                  rd.success("Created Destination Team Wf %s\n", destTeamWf.toStringWithId());
               } else {
                  rd.logf("Need to Create Destination Team Wf for team %s\n", toTeamDef.toStringWithId());
               }
            } else {
               rd.logf("Using existing Destination Team Wf %s\n", destTeamWf.toStringWithId());
            }
            atsApi.getVersionService().setTargetedVersion(destTeamWf, targetedVersion, changes);
            if (changes != null && atsApi.getRelationResolver().areNotRelated(chgRptTeamWf, AtsRelationTypes.Derive_To,
               destTeamWf)) {
               changes.relate(chgRptTeamWf, AtsRelationTypes.Derive_To, destTeamWf);
            }
            if (destTeamWf != null) {
               crttwd.setDestTeamWf(destTeamWf.getStoreObject());
               crtd.getIdToTeamWf().put(destTeamWf.getId(), destTeamWf);
               crtd.getDestTeamWfs().add(ArtifactToken.valueOf(destTeamWf.getStoreObject().getId(),
                  destTeamWf.getName(), atsApi.getAtsBranch()));
            }

            // Compute missing tasks; add task or null to crttwd.ChangeReportTaskMatch objects
            ChangeReportTasksUtil.determinExistingTaskMatchType(idToArtifact, crtd, crttwd, setDef, workType,
               destTeamWf);

            Set<String> addModTaskNames = new HashSet<>();
            Set<String> deletedTaskNames = new HashSet<>();
            Date createdDate = new Date();
            // Log changes needed; Add to delete if needed or to NewTaskData
            Collection<ChangeReportTaskMatch> taskMatches = crttwd.getTaskMatches();
            for (ChangeReportTaskMatch taskMatch : taskMatches) {
               ChangeReportTaskMatchType matchType = taskMatch.getMatchType();
               // Skip if task already exists
               if (matchType == ChangeReportTaskMatchType.Match) {

                  boolean autoGen = atsApi.getTaskService().isAutoGen(taskMatch.getTaskWf());
                  boolean deReferenced = atsApi.getTaskService().isAutoGenDeReferenced(taskMatch.getTaskWf());
                  if (!autoGen || deReferenced) {
                     crtd.getResults().success(ChangeReportTaskMatchResult.TaskExistsNeedsDereference.getName(),
                        taskMatch.getTaskWf().toStringWithId());
                     taskMatch.setMatchResult(ChangeReportTaskMatchResult.TaskExistsNeedsDereference);
                     if (changes != null) {
                        atsApi.getTaskService().removeDeReferencedNote(taskMatch.getTaskWf(), changes);
                     }
                  } else {
                     crtd.getResults().logf(ChangeReportTaskMatchResult.TaskExistsNoChangeNeeded.getName(),
                        taskMatch.getTaskWf().toStringWithId());
                     taskMatch.setMatchResult(ChangeReportTaskMatchResult.TaskExistsNoChangeNeeded);
                  }
               }

               // Create if no task was found
               else if (matchType == ChangeReportTaskMatchType.ChgRptTskCompAsNeeded) {
                  if (!addModTaskNames.contains(taskMatch.getTaskName())) {
                     crtd.getResults().success(ChangeReportTaskMatchResult.CreateNewChgRptTask.getName(),
                        taskMatch.getTaskName());
                     addToNewTaskData(crtd, crttwd, taskMatch, createdDate);
                     addModTaskNames.add(taskMatch.getTaskName());
                     taskMatch.setMatchResult(ChangeReportTaskMatchResult.CreateNewChgRptTask);
                  }
               }

               // Create if no task was found
               else if (matchType == ChangeReportTaskMatchType.StaticTskCompAsNeeded) {
                  if (!addModTaskNames.contains(taskMatch.getTaskName())) {
                     crtd.getResults().success(ChangeReportTaskMatchResult.CreateNewStaticTask.getName(),
                        taskMatch.getTaskName());
                     addToNewTaskData(crtd, crttwd, taskMatch, createdDate);
                     addModTaskNames.add(taskMatch.getTaskName());
                     taskMatch.setMatchResult(ChangeReportTaskMatchResult.CreateNewStaticTask);
                  }
               }

               // Create if no task was found
               else if (matchType == ChangeReportTaskMatchType.AdditionalTskCompAsNeeded) {
                  crtd.getResults().success(ChangeReportTaskMatchResult.CreateNewAdditionalTask.getName(),
                     taskMatch.getTaskName());
                  addToNewTaskData(crtd, crttwd, taskMatch, createdDate);
                  addModTaskNames.add(taskMatch.getTaskName());
                  taskMatch.setMatchResult(ChangeReportTaskMatchResult.CreateNewAdditionalTask);
               }

               // Mark as de-referenced if no reference or referenced artifact not there anymore
               else if ((matchType == ChangeReportTaskMatchType.TaskRefAttrMissing) || (matchType == ChangeReportTaskMatchType.TaskRefAttrButNoRefChgArt)) {
                  if (!deletedTaskNames.contains(taskMatch.getTaskName())) {
                     crtd.getResults().success(ChangeReportTaskMatchResult.NoMatchArtTaskCanBeDeleted.getName(),
                        taskMatch.getTaskWf().toStringWithId());
                     taskMatch.setMatchResult(ChangeReportTaskMatchResult.NoMatchArtTaskCanBeDeleted);
                     if (!reportOnly) {
                        if (changes == null) {
                           crtd.getResults().errorf("AtsChangeSet can not be null.");
                        } else {
                           atsApi.getTaskService().addDeReferencedNote(taskMatch.getTaskWf(), changes);
                        }
                     }
                     deletedTaskNames.add(taskMatch.getTaskName());
                  }
               } else {
                  crtd.getResults().errorf(ChangeReportTaskMatchResult.UnhandledMatchType.getName(),
                     taskMatch.getMatchType().name());
                  taskMatch.setMatchResult(ChangeReportTaskMatchResult.UnhandledMatchType);
               }
            }

            if (!reportOnly && !crttwd.getNewTaskSet().getNewTaskDatas().iterator().next().getTasks().isEmpty()) {
               crttwd.getNewTaskSet().setAsUserId(crtd.getAsUser().getUserId());
               crttwd.getNewTaskSet().setCommitComment("Create Change Report Tasks");
               if (destTeamWf != null) {
                  newTaskData.setTeamWfId(destTeamWf.getId());
               }

               /**
                * Until all transition is done on server, need to call to directly generate tasks so it can be part of
                * the transition change set. Otherwise calling server to generate tasks will result in teamWf and tasks
                * to be reloaded and thus lose transition data.
                */
               CreateTasksOperation operation = new CreateTasksOperation(crttwd.getNewTaskSet(), atsApi);
               operation.setIdToTeamWf(crtd.getIdToTeamWf());
               operation.validate();
               if (crtd.getResults().isSuccess()) {
                  operation.run(changes);
                  if (crtd.getResults().isErrors()) {
                     return crtd;
                  }
               }
            }

            // Create final results table
            XResultTable table = new XResultTable();
            rd.getTables().add(table);
            if (destTeamWf != null) {
               table.setName(destTeamWf.getTeamDefinition().getName() + " - " + destTeamWf.toStringWithId());
            } else {
               table.setName(toTeamDef.getName() + " - <new team wf>");
            }

            List<XResultTableColumn> columns = table.getColumns();
            columns.add(new XResultTableColumn(Columns.ChgRptArtName.name(), Columns.ChgRptArtName.name(), 200,
               XResultTableDataType.String));
            columns.add(new XResultTableColumn(Columns.ChgRptArtId.name(), Columns.ChgRptArtId.name(), 80,
               XResultTableDataType.Integer));
            columns.add(new XResultTableColumn(Columns.ChgRptArtType.name(), Columns.ChgRptArtType.name(), 200,
               XResultTableDataType.String));
            columns.add(new XResultTableColumn(Columns.ComputedTaskName.name(), Columns.ComputedTaskName.name(), 200,
               XResultTableDataType.String));
            columns.add(new XResultTableColumn(Columns.MatchType.name(), Columns.MatchType.name(), 200,
               XResultTableDataType.String));
            columns.add(
               new XResultTableColumn(Columns.Action.name(), Columns.Action.name(), 200, XResultTableDataType.String));

            for (ChangeReportTaskMatch taskMatch : crttwd.getTaskMatches()) {
               String safeName = "";
               if (taskMatch.getChgRptArt().isValid()) {
                  safeName = atsApi.getStoreService().getSafeName(taskMatch.getChgRptArt());
               }
               table.getRows().add(new XResultTableRow( //
                  taskMatch.getChgRptArtName(), //
                  taskMatch.getChgRptArt().getIdString(), //
                  safeName, //
                  taskMatch.getTaskName(), //
                  taskMatch.getMatchType().name(), //
                  (taskMatch.getMatchResult() != null ? taskMatch.getMatchResult().getDisplayName() : "None")
               //
               ));
            }

         }

         if (reportOnly) {
            return crtd;
         } else if (changes != null) {
            TransactionId transId = changes.executeIfNeeded();
            crtd.getResults().log("\n");
            if (transId != null && transId.isValid()) {
               crtd.setTransaction(transId);
               crtd.setIds(changes.getIds());
               crtd.getResults().success("Tasks Updated\n");
            } else {
               crtd.getResults().success("No Changes Needed\n");
            }
         }
      } catch (Exception ex) {
         crtd.results.errorf("Exception creating tasks %s", Lib.exceptionToString(ex));
      }
      return crtd;
   }

   public static enum Columns {
      ChgRptArtId,
      ChgRptArtType,
      ChgRptArtName,
      ComputedTaskName,
      MatchType,
      Action;
   };

   private void addIfDebug(String format, Object... data) {
      if (crtd.isDebug()) {
         crtd.getResults().logf(format, data);
      }
   }

   private IAtsActionableItem getToSiblingAi(CreateTasksDefinition setDef, IAtsProgram program, XResultData rd, ChangeReportOptionsToTeam toSiblingTeamDef) {
      IAtsActionableItem toSiblingAi = null;
      if (Strings.isNumeric(toSiblingTeamDef.getAiId())) {
         toSiblingAi = atsApi.getActionableItemService().getActionableItem(toSiblingTeamDef.getAiId());
      } else {
         toSiblingAi = getAiFromWorkType(setDef, program, rd, toSiblingTeamDef.getWorkType());
      }
      return toSiblingAi;
   }

   private IAtsActionableItem getAiFromWorkType(CreateTasksDefinition setDef, IAtsProgram program, XResultData rd, WorkType workType) {
      if (workType == null) {
         rd.errorf("Team Definition and Work Type invalid in CreateTaskDefinition\n");
         return null;
      }
      Collection<IAtsActionableItem> ais = atsApi.getProgramService().getAis(program, workType);
      if (ais.size() == 0) {
         rd.errorf("No Actionable Item of Work Type [%s] found\n",
            setDef.getChgRptOptions().getFromSiblingTeamDefWorkType());
         return null;
      } else if (ais.size() > 1) {
         // Attempt to find configured taskgen team def
         for (IAtsActionableItem ai : ais) {
            if (ai.hasTag(TASK_GEN_TAG)) {
               return ai;
            }
         }
         rd.errorf("Multiple Actionable Item of Work Type [%s] found, only one should be\n",
            setDef.getChgRptOptions().getFromSiblingTeamDefWorkType());
         return null;
      }
      return ais.iterator().next();
   }

   private IAtsTeamDefinition getToTeamDef(CreateTasksDefinition setDef, IAtsProgram program, XResultData rd, ChangeReportOptionsToTeam toSiblingTeamDef) {
      IAtsTeamDefinition toTeamDef = null;
      if (Strings.isNumeric(toSiblingTeamDef.getTeamId())) {
         toTeamDef =
            atsApi.getTeamDefinitionService().getTeamDefinitionById(ArtifactId.valueOf(toSiblingTeamDef.getTeamId()));
      } else {
         WorkType workType = toSiblingTeamDef.getWorkType();
         toTeamDef = getTeamDefFromWorkType(setDef, program, rd, workType);
      }
      return toTeamDef;
   }

   private IAtsTeamDefinition getFromSiblingTeamDef(CreateTasksDefinition setDef, IAtsProgram program, XResultData rd) {
      ArtifactToken fromTeamDefTok = setDef.getChgRptOptions().getFromSiblingTeamDef();
      IAtsTeamDefinition fromTeamDef = null;
      if (fromTeamDefTok == null) {
         WorkType workType = setDef.getChgRptOptions().getFromSiblingTeamDefWorkType();
         fromTeamDef = getTeamDefFromWorkType(setDef, program, rd, workType);
      } else {
         fromTeamDef = atsApi.getTeamDefinitionService().getTeamDefinitionById(fromTeamDefTok);
      }
      return fromTeamDef;
   }

   private IAtsTeamDefinition getTeamDefFromWorkType(CreateTasksDefinition setDef, IAtsProgram program, XResultData rd, WorkType workType) {
      if (workType == null) {
         rd.errorf("Team Definition and Work Type invalid in CreateTaskDefinition\n");
         return null;
      }
      Collection<IAtsTeamDefinition> teamDefs = atsApi.getProgramService().getTeamDefs(program, workType);
      if (teamDefs.size() == 0) {
         rd.errorf("No Team Definitions of Work Type [%s] found\n",
            setDef.getChgRptOptions().getFromSiblingTeamDefWorkType());
         return null;
      } else if (teamDefs.size() > 1) {
         // Attempt to find configured taskgen team def
         for (IAtsTeamDefinition teamDef : teamDefs) {
            if (teamDef.hasTag(TASK_GEN_TAG)) {
               return teamDef;
            }
         }
         rd.errorf("Multiple Team Definitions of Work Type [%s] found, only one should be\n",
            setDef.getChgRptOptions().getFromSiblingTeamDefWorkType());
         return null;
      }
      return teamDefs.iterator().next();
   }

   private void addToNewTaskData(ChangeReportTaskData crtd, ChangeReportTaskTeamWfData crttwd, ChangeReportTaskMatch taskMatch, Date createdDate) {
      JaxAtsTask task = new JaxAtsTask();
      task.setName(taskMatch.getTaskName());
      if (taskMatch.getMatchType() == ChangeReportTaskMatchType.StaticTskCompAsNeeded) {
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
      } else if (taskMatch.getMatchType() == ChangeReportTaskMatchType.AdditionalTskCompAsNeeded) {
         task.setAssigneeAccountIds(Arrays.asList(AtsCoreUsers.UNASSIGNED_USER.getArtifactId()));
         task.setDescription("see title");
      } else if (taskMatch.getMatchType() == ChangeReportTaskMatchType.ChgRptTskCompAsNeeded) {
         if (taskMatch.isChgRptArtValid()) {
            task.addAttribute(AtsAttributeTypes.TaskToChangedArtifactName, taskMatch.getChgRptArtName());
            task.addAttribute(AtsAttributeTypes.TaskToChangedArtifactDeleted, taskMatch.isChgRptArtDeleted());
            task.addAttribute(AtsAttributeTypes.TaskToChangedArtifactReference,
               ArtifactId.create(taskMatch.getChgRptArt()));
         }
         task.setAssigneeUserIds(Arrays.asList(AtsCoreUsers.UNASSIGNED_USER.getUserId()));
      } else {
         crtd.getResults().errorf("Un-handled MatchType [%s]...Aborting\n", taskMatch.getMatchType().name());
         return;
      }

      task.setCreatedDate(createdDate);
      task.setCreatedByUserId(AtsCoreUsers.SYSTEM_USER.getUserId());

      // Allow extensions to set TaskAutoGenVersion, else fail
      AutoGenVersion ver = null;
      List<IAtsTaskProvider> taskProviders = AtsTaskProviderCollector.getTaskProviders();
      for (IAtsTaskProvider provider : taskProviders) {
         ver = provider.getAutoGenTaskVersionToSet(crtd, crttwd, taskMatch);
         if (ver != null) {
            break;
         }
      }
      if (ver == null) {
         crtd.getResults().errorf("No provider configured to set TaskAutoGenVersion, aborting...");
         return;
      }
      if (Strings.isValid(taskMatch.getAutoTaskGenType())) {
         task.addAttribute(AtsAttributeTypes.TaskAutoGenType, taskMatch.getAutoTaskGenType());
      }
      task.addAttribute(AtsAttributeTypes.TaskAutoGenVersion, ver.getName());
      task.addAttribute(AtsAttributeTypes.TaskAutoGen, true);

      // Allow extensions to add to generating task
      for (IAtsTaskProvider provider : AtsTaskProviderCollector.getTaskProviders()) {
         provider.addToAutoGeneratingTask(crtd, crttwd, taskMatch, task);
      }
      crttwd.getNewTaskSet().getNewTaskDatas().iterator().next().getTasks().add(task);
   }

   protected CreateTasksDefinition getTaskDefinition() {
      return atsApi.getTaskSetDefinitionProviderService().getTaskSetDefinition(taskDefToken).getCreateTasksDef();
   }

   protected Collection<AttributeTypeId> getAttributeTypesToIgnore() {
      return org.eclipse.osee.framework.jdk.core.util.Collections.castAll(
         getTaskDefinition().getChgRptOptions().getNotAttributeTypes());
   }

}
