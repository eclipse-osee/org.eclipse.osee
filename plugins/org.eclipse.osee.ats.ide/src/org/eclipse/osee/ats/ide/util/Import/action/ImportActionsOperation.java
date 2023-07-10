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

package org.eclipse.osee.ats.ide.util.Import.action;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.ExcelToPojoOperation;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.ECell;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EFile;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.EHeaderCell;
import org.eclipse.osee.framework.jdk.core.util.io.excel.pojo.model.ERow;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;

/**
 * @author Donald G. Dunne
 */
public class ImportActionsOperation {

   private final List<ActionData> actionDatas = new ArrayList<>();
   private final Set<Artifact> actionArts = new HashSet<>();
   private final Map<String, ActionResult> actionNameToAction = new HashMap<>(100);
   private final boolean emailPOCs;
   private final IAtsGoal toGoal;
   private final Map<String, IAgileTeam> teamNameByTeamMap = new HashMap<>();
   private final File file;
   private XResultData rd;
   private final AtsApiIde atsApi;
   private final boolean persist;
   private final String commitComment;

   public ImportActionsOperation(File file, boolean emailPOCs, IAtsGoal toGoal, boolean persist, String commitComment) {
      this.file = file;
      this.emailPOCs = emailPOCs;
      this.toGoal = toGoal;
      this.persist = persist;
      this.commitComment = commitComment;
      atsApi = AtsApiService.get();
   }

   public XResultData run() {

      ExcelToPojoOperation op = new ExcelToPojoOperation(file);
      EFile eFile = op.run();
      if (eFile.getResults().isErrors()) {
         return eFile.getResults();
      }
      rd = eFile.getResults();

      try {
         validateHeaders(eFile);
         if (rd.isErrors()) {
            return rd;
         }

         createActionDatas(eFile);
         if (rd.isErrors()) {
            return rd;
         }

         validateActionDatas(eFile, new XResultData());
         if (rd.isErrors()) {
            return rd;
         }

         if (persist) {
            IAtsChangeSet changes = atsApi.createChangeSet(commitComment);
            createArtifactsAndNotify(changes);
            changes.execute();
         } else {
            ValidationEditorOperation editOp = new ValidationEditorOperation(actionDatas);
            editOp.open();
         }

      } catch (Exception ex) {
         rd.errorf("Exception importing actions %s", Lib.exceptionToString(ex));
      }
      return rd;
   }

   private void validateHeaders(EFile eFile) {
      for (EHeaderCell hCell : eFile.getWorkbook().getSheets().iterator().next().getHeader().getHcells()) {
         String hCellName = hCell.getName();

         // if default column, continue
         if (ActionColumnns.getColNames().contains(hCellName)) {
            continue;
         }

         // else, should be an attribute type name
         String attrTypeName = hCellName;
         if (Strings.isValid(attrTypeName)) {
            if (!AttributeTypeManager.typeExists(attrTypeName)) {
               rd.errorf("Invalid Column or Attribute Type Name => %s\n", attrTypeName);
            } else {
               AttributeTypeToken attributeType = AttributeTypeManager.getType(attrTypeName);
               if (attributeType == null) {
                  rd.errorf("Invalid Column or Attribute Type Name => %s\n", attrTypeName);
               } else {
                  if (!AtsArtifactTypes.TeamWorkflow.isValidAttributeType(attributeType)) {
                     rd.errorf("Invalid Column or Attribute Type Name for Team Workflow => %s\n", attrTypeName);
                  } else {
                     hCell.setStoreType(attributeType);
                     continue;
                  }
               }
            }
         }

         // else, bad column
         rd.errorf("Unhandled column %s => [%s]\n", hCell.getColNum(), hCell.getName());
      }
   }

   private void validateActionDatas(EFile eFile, XResultData rd) {
      int rowNum = 1; // Header is row 1
      for (ActionData aData : actionDatas) {
         rowNum++;
         if (aData.title.equals("")) {
            rd.error("Row " + rowNum + "; Invalid Title");
         }
         if (Strings.isInValid(aData.desc)) {
            rd.error("Row " + rowNum + "; Invalid Description");
         }
         Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
         if (aData.actionableItems.isEmpty()) {
            rd.error("Row " + rowNum + ": Must have at least one ActionableItem defined");
         } else {
            for (String actionableItemName : aData.actionableItems) {
               try {
                  Collection<IAtsActionableItem> aias = new ArrayList<>();
                  for (Artifact aiaArt : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.ActionableItem,
                     actionableItemName, atsApi.getAtsBranch())) {
                     IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(aiaArt);
                     if (ai != null) {
                        aias.add(ai);
                     }
                  }
                  if (aias.isEmpty()) {
                     rd.error("Row " + rowNum + ": Couldn't find actionable item for \"" + actionableItemName + "\"");
                  } else if (aias.size() > 1) {
                     rd.error(
                        "Row " + rowNum + ": Duplicate actionable items found with name \"" + actionableItemName + "\"");
                  } else {
                     IAtsActionableItem aia = aias.iterator().next();
                     teamDefs.addAll(atsApi.getActionableItemService().getImpactedTeamDefs(Arrays.asList(aia)));
                     if (teamDefs.isEmpty()) {
                        rd.error(
                           "Row " + rowNum + ": No related Team Definition for Actionable Item\"" + actionableItemName + "\"");
                     } else if (teamDefs.size() > 1) {
                        rd.error(
                           "Row " + rowNum + ": Duplicate Team Definitions found for Actionable Item\"" + actionableItemName + "\"");
                     }
                  }

               } catch (Exception ex) {
                  rd.error("Row " + rowNum + " - " + ex.getLocalizedMessage());
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
         }
         if (Strings.isValid(aData.version)) {
            try {
               for (IAtsTeamDefinition teamDef : teamDefs) {
                  if (atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef) == null) {
                     rd.errorf("No Team Definitions Holding Versions found for Team Definition [%s]\n", teamDef);
                  }
                  IAtsTeamDefinition teamDefHolVer =
                     atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
                  if (atsApi.getVersionService().getVersion(teamDefHolVer, aData.version) == null) {
                     rd.errorf("No version [%s] configured for Team Definition [%s]\n", aData.version, teamDef);
                  }
               }
            } catch (Exception ex) {
               rd.error("Row " + rowNum + " - " + ex.getLocalizedMessage());
               OseeLog.log(Activator.class, Level.SEVERE, ex);
            }
         }
         // If no assignees, ATS will auto-assign to correct person
         // Else if assignees, confirm that they are valid
         if (aData.assigneeStrs.size() > 0) {
            for (String assignee : aData.assigneeStrs) {
               try {
                  assignee = assignee.replaceFirst("^ *", "");
                  assignee = assignee.replaceFirst(" *$", "");
                  AtsUser user = atsApi.getUserService().getUserByName(assignee);
                  if (user == null) {
                     rd.error("Row " + rowNum + ": Couldn't retrieve user \"" + assignee + "\"");
                  } else {
                     aData.assignees.add(user);
                  }
               } catch (UserNotInDatabase ex) {
                  rd.error("Row " + rowNum + ": " + ex.getLocalizedMessage());
               }
            }
         }

         if (Strings.isValid(aData.agileTeamName)) {
            IAgileTeam aTeam = getAgileTeamByName(aData.agileTeamName);
            if (aTeam == null) {
               rd.errorf("Invalid team name [%s]\n", aData.agileTeamName);
            }
            IAgileBacklog backlog = getAgileBacklog(aData.agileTeamName);
            if (backlog == null) {
               rd.errorf("No backlog for team [%s]\n", aData.agileTeamName);
            }
         }

         if (Strings.isValid(aData.agileSprintName)) {
            IAgileSprint sprint = getAgileSprint(aData.agileTeamName, aData.agileSprintName);
            if (sprint == null) {
               rd.errorf("Invalid sprint name [%s] for team [%s]\n", aData.agileSprintName, aData.agileTeamName);
            }
         }

         if (Strings.isValid(aData.agilePoints)) {
            IAgileTeam aTeam = getAgileTeamByName(aData.agileTeamName);
            if (aTeam == null) {
               rd.errorf("Invalid team name [%s] for points [%s]\n", aData.agileTeamName, aData.agilePoints);
            } else {
               AttributeTypeId pointsAttrType = atsApi.getAgileService().getAgileTeamPointsAttributeType(aTeam);
               if (pointsAttrType == null) {
                  rd.errorf("Points not configured for team [%s]\n", aData.agileTeamName);
               }
            }
         }
      }
   }

   private void createActionDatas(EFile eFile) {

      boolean last = false;
      for (ERow row : eFile.getWorkbook().getSheets().iterator().next().getRows()) {
         ActionData actionData = new ActionData();
         for (ECell cell : row.getCells()) {
            if (cell.getCol().is(ActionColumnns.Title)) {
               actionData.title = validateAndGet(cell);
            } else if (cell.getCol().is(ActionColumnns.Description)) {
               actionData.desc = validateAndGet(cell);
            } else if (cell.getCol().is(ActionColumnns.ActionableItems)) {
               actionData.actionableItems = validateAndGet(cell, ";");
            } else if (cell.getCol().is(ActionColumnns.Assignees)) {
               actionData.assigneeStrs = validateAndGet(cell, ";");
            } else if (cell.getCol().is(ActionColumnns.Originator)) {
               String name = cell.getValue();
               if (Strings.isValid(name)) {
                  AtsUser user = atsApi.getUserService().getUserByName(name);
                  if (user != null) {
                     actionData.originator = user;
                  }
               }
            } else if (cell.getCol().is(ActionColumnns.ChangeType)) {
               actionData.changeType = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumnns.Priority)) {
               actionData.priorityStr = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumnns.Version)) {
               actionData.version = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumnns.AgilePoints)) {
               actionData.agilePoints = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumnns.EstimatedHours)) {
               actionData.estimatedHours = Strings.isNumeric(cell.getValue()) ? Double.valueOf(cell.getValue()) : 0;
            } else if (cell.getCol().is(ActionColumnns.AgileSprintName)) {
               actionData.agileSprintName = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumnns.AgileTeamName)) {
               actionData.agileTeamName = getOrBlank(cell);
            } else {
               String value = cell.getValue();
               Object obj = cell.getCol().getStoreType();
               if (obj instanceof AttributeTypeToken) {
                  AttributeTypeToken attrType = (AttributeTypeToken) obj;
                  JaxAttribute attr = new JaxAttribute();
                  attr.setAttrType(attrType);
                  attr.getValues().add(value);
                  actionData.attributes.add(attr);
               } else {
                  rd.errorf("No Attribute Type for cell with value %s\n", cell.toString());
               }
            }
         }
         if (last) {
            break;
         }
         actionDatas.add(actionData);
      }
   }

   private String getOrBlank(ECell cell) {
      return Strings.isValid(cell.getValue()) ? cell.getValue() : "";
   }

   private String validateAndGet(ECell cell) {
      String value = cell.getValue();
      if (Strings.isInvalid(value)) {
         rd.errorf(cell.getCol().getName() + " must be specified for %s\n", cell.toString());
         return "";
      }
      return value;
   }

   private Collection<String> validateAndGet(ECell cell, String separator) {
      List<String> values = new ArrayList<>();
      String value = cell.getValue();
      if (Strings.isInvalid(value)) {
         rd.errorf(cell.getCol().getName() + " must be specified for %s\n", cell.toString());
      } else {
         for (String val : value.split(separator)) {
            values.add(val);
         }
      }
      return values;
   }

   private void createArtifactsAndNotify(IAtsChangeSet changes) {
      AtsUtilClient.setEmailEnabled(false);
      Set<IAtsTeamWorkflow> teamWfs = new HashSet<>();
      Date createdDate = new Date();
      try {
         AtsUser createdBy = atsApi.getUserService().getCurrentUser();
         for (ActionData aData : actionDatas) {
            ActionResult actionResult = actionNameToAction.get(aData.title);
            if (actionResult == null) {
               ChangeTypes changeType = getChangeType(aData);
               String priorityStr = getPriority(aData);
               ActionResult aResult = atsApi.getActionService().createAction(null, aData.title, aData.desc, changeType,
                  priorityStr, false, null, atsApi.getActionableItemService().getActionableItems(aData.actionableItems),
                  createdDate, createdBy, null, changes);
               actionNameToAction.put(aData.title, aResult);
               for (IAtsTeamWorkflow teamWf : aResult.getTeamWfs()) {
                  processTeamWorkflow(changes, aData, teamWf);
                  teamWfs.add(teamWf);
               }
               actionArts.add(atsApi.getQueryServiceIde().getArtifact(aResult.getActionArt()));
            } else {
               Set<IAtsActionableItem> aias = new HashSet<>();
               for (String actionableItemName : aData.actionableItems) {
                  for (Artifact aiaArt : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.ActionableItem,
                     actionableItemName, atsApi.getAtsBranch())) {
                     IAtsActionableItem ai = atsApi.getActionableItemService().getActionableItemById(aiaArt);
                     if (ai != null) {
                        aias.add(ai);
                     }
                  }
               }
               Map<IAtsTeamDefinition, Collection<IAtsActionableItem>> teamDefToAias = getTeamDefToAias(aias);
               for (Entry<IAtsTeamDefinition, Collection<IAtsActionableItem>> entry : teamDefToAias.entrySet()) {
                  IAtsTeamWorkflow teamWf = atsApi.getActionService().createTeamWorkflow(actionResult.getAction(),
                     entry.getKey(), entry.getValue(), aData.assignees, changes, createdDate, createdBy, null,
                     CreateTeamOption.Duplicate_If_Exists);
                  actionResult.getTeamWfs().add(teamWf);
                  processTeamWorkflow(changes, aData, teamWf);
                  teamWfs.add(teamWf);
               }
            }
         }

         AtsUtilClient.setEmailEnabled(true);

         if (emailPOCs) {
            for (IAtsTeamWorkflow teamWf : teamWfs) {
               try {
                  changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(
                     atsApi.getUserService().getCurrentUser(), teamWf, AtsNotifyType.Assigned));
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Error adding ATS Notification Event", ex);
               }
            }
         }

         WorldEditor.open(new WorldEditorSimpleProvider(getName(), AtsObjects.getArtifacts(teamWfs)));

      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, OseeLevel.SEVERE_POPUP, ex);
      } finally {
         AtsUtilClient.setEmailEnabled(true);
      }
   }

   private String getPriority(ActionData aData) {
      String priorityStr = "3";
      if (Strings.isValid(aData.priorityStr)) {
         priorityStr = aData.priorityStr;
      }
      return priorityStr;
   }

   private ChangeTypes getChangeType(ActionData aData) {
      ChangeTypes changeType = ChangeTypes.None;
      if (Strings.isValid(aData.changeType)) {
         changeType = ChangeTypes.getChangeType(aData.changeType);
      }
      return changeType;
   }

   private void processTeamWorkflow(IAtsChangeSet changes, ActionData aData, IAtsTeamWorkflow teamWf) {
      ChangeTypes changeType = getChangeType(aData);
      String priorityStr = getPriority(aData);

      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.Description, aData.desc);
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.Priority, priorityStr);
      changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.ChangeType, changeType.name());

      addToAgile(changes, aData, teamWf);

      for (JaxAttribute attr : aData.attributes) {
         AttributeTypeToken attrType = attr.getAttrType();
         changes.setAttributeValues(teamWf, attrType, attr.getValues());
      }

      addToGoal(Collections.singleton((TeamWorkFlowArtifact) teamWf.getStoreObject()), changes);

      if (!aData.version.equals("")) {
         IAtsTeamDefinition teamDefHoldVer =
            atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamWf.getTeamDefinition());
         IAtsVersion version = atsApi.getVersionService().getVersion(teamDefHoldVer, aData.version);
         if (version == null) {
            rd.errorf("No version [%s] configured for Team Definition [%s]\n", aData.version,
               teamWf.getTeamDefinition());
         }
         atsApi.getVersionService().setTargetedVersion(teamWf, version, changes);
      }
      if (aData.estimatedHours != null) {
         changes.setSoleAttributeValue((ArtifactId) teamWf, AtsAttributeTypes.EstimatedHours, aData.estimatedHours);
      }
      if (aData.assigneeStrs.size() > 0) {
         teamWf.getStateMgr().setAssignees(aData.assignees);
      }
      if (aData.originator != null) {
         changes.setSoleAttributeValue(teamWf, AtsAttributeTypes.CreatedBy, aData.originator.getUserId());
      }
   }

   private void addToAgile(IAtsChangeSet changes, ActionData aData, IAtsTeamWorkflow teamWf) {
      // If Agile Team, add workflow to backlog
      if (Strings.isValid(aData.agileTeamName)) {
         IAgileBacklog backlog = getAgileBacklog(aData.agileTeamName);
         if (backlog != null) {
            changes.relate(backlog, AtsRelationTypes.Goal_Member, teamWf);
         }
      }

      // If Agile Sprint, add workflow to sprint
      if (Strings.isValid(aData.agileSprintName)) {
         IAgileSprint sprint = getAgileSprint(aData.agileTeamName, aData.agileSprintName);
         if (sprint != null) {
            changes.relate(sprint, AtsRelationTypes.AgileSprintToItem_AtsItem, teamWf);
         }
      }

      // If Agile points, add points to workflow
      if (Strings.isValid(aData.agilePoints)) {
         IAgileTeam aTeam = getAgileTeamByName(aData.agileTeamName);
         AttributeTypeToken attrType = atsApi.getAgileService().getAgileTeamPointsAttributeType(aTeam);
         if (attrType.getId().equals(AtsAttributeTypes.Points.getId())) {
            changes.setSoleAttributeValue(teamWf, attrType, aData.agilePoints);
         } else if (attrType.getId().equals(AtsAttributeTypes.PointsNumeric.getId())) {
            changes.setSoleAttributeValue(teamWf, attrType, Double.valueOf(aData.agilePoints));
         } else {
            throw new OseeArgumentException("Un-configured pointes types for team %s",
               teamWf.getTeamDefinition().toStringWithId());
         }
      }
   }

   private IAgileTeam getAgileTeamByName(String agileTeamName) {
      IAgileTeam team = teamNameByTeamMap.get(agileTeamName);
      if (team == null) {
         IAgileTeam aTeam = atsApi.getAgileService().getAgileTeamByName(agileTeamName);
         if (aTeam != null) {
            teamNameByTeamMap.put(agileTeamName, aTeam);
            team = aTeam;
         }
      }
      return team;
   }

   private IAgileSprint getAgileSprint(String agileTeamName, String agileSprintName) {
      IAgileTeam aTeam = getAgileTeamByName(agileTeamName);
      if (aTeam != null) {
         for (IAgileSprint teamSprint : atsApi.getAgileService().getAgileSprints(aTeam)) {
            if (teamSprint.getName().equals(agileSprintName)) {
               return teamSprint;
            }
         }
      }
      return null;
   }

   private IAgileBacklog getAgileBacklog(String agileTeamName) {
      IAgileTeam aTeam = atsApi.getAgileService().getAgileTeamByName(agileTeamName);
      if (aTeam != null) {
         return atsApi.getAgileService().getAgileBacklog(aTeam);
      }
      return null;
   }

   private void addToGoal(Collection<TeamWorkFlowArtifact> newTeamArts, IAtsChangeSet changes) {
      if (toGoal != null) {
         GoalArtifact goal = (GoalArtifact) atsApi.getQueryService().getArtifact(toGoal);
         if (goal == null) {
            throw new OseeArgumentException("Goal artifact does not exist for goal %s", toGoal.toStringWithId());
         }
         for (Artifact art : newTeamArts) {
            goal.addMember(art);
         }
         changes.add(goal);
      }
   }

   public Map<IAtsTeamDefinition, Collection<IAtsActionableItem>> getTeamDefToAias(
      Collection<IAtsActionableItem> aias) {
      Map<IAtsTeamDefinition, Collection<IAtsActionableItem>> teamDefToAias = new HashMap<>();
      for (IAtsActionableItem aia : aias) {
         IAtsTeamDefinition teamDef =
            atsApi.getTeamDefinitionService().getImpactedTeamDefs(Arrays.asList(aia)).iterator().next();
         if (teamDefToAias.containsKey(teamDef)) {
            teamDefToAias.get(teamDef).add(aia);
         } else {
            teamDefToAias.put(teamDef, Arrays.asList(aia));
         }
      }
      return teamDefToAias;
   }

   public String getDescription() {
      return "Extract each row as an Action";
   }

   public Set<Artifact> getActionArts() {
      return actionArts;
   }

   public String getName() {
      return "Excel Ats Actions";
   }

   public List<ActionData> getActionDatas() {
      return actionDatas;
   }

}
