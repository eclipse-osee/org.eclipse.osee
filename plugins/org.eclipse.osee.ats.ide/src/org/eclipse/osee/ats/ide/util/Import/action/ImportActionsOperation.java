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
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.api.agile.IAgileBacklog;
import org.eclipse.osee.ats.api.agile.IAgileSprint;
import org.eclipse.osee.ats.api.agile.IAgileTeam;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.team.ChangeTypes;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.api.workflow.NewActionData;
import org.eclipse.osee.ats.api.workflow.NewActionDataMulti;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.world.WorldEditor;
import org.eclipse.osee.ats.ide.world.WorldEditorSimpleProvider;
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
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Donald G. Dunne
 */
public class ImportActionsOperation {

   private final List<ActionData> actionDatas = new ArrayList<>();
   private final Set<Artifact> actionArts = new HashSet<>();
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
            createArtifactsAndNotify();
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
         if (ActionColumns.getColNames().contains(hCellName)) {
            continue;
         }

         // else, should be an attribute type name
         String attrTypeName = hCellName;
         if (Strings.isValid(attrTypeName)) {
            if (!AttributeTypeManager.typeExists(attrTypeName)) {
               rd.errorf("Invalid Column or Attribute Type Name => %s\n", attrTypeName);
               continue;
            } else {
               AttributeTypeToken attributeType = AttributeTypeManager.getType(attrTypeName);
               if (attributeType == null) {
                  rd.errorf("Invalid Column or Attribute Type Name => %s\n", attrTypeName);
                  continue;
               } else {
                  hCell.setStoreType(attributeType);
                  continue;
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

      for (ERow row : eFile.getWorkbook().getSheets().iterator().next().getRows()) {
         ActionData actionData = new ActionData();
         for (ECell cell : row.getCells()) {
            if (cell.getCol().is(ActionColumns.Title)) {
               actionData.title = validateAndGet(cell);
            } else if (cell.getCol().is(ActionColumns.Description)) {
               actionData.desc = validateAndGet(cell);
            } else if (cell.getCol().is(ActionColumns.ActionableItems)) {
               actionData.actionableItems = validateAndGet(cell, ";");
            } else if (cell.getCol().is(ActionColumns.Assignees)) {
               actionData.assigneeStrs = validateAndGet(cell, ";");
            } else if (cell.getCol().is(ActionColumns.Originator)) {
               String name = cell.getValue();
               if (Strings.isValid(name)) {
                  AtsUser user = atsApi.getUserService().getUserByName(name);
                  if (user != null) {
                     actionData.originator = user;
                  } else {
                     rd.errorf("Originator [%s] is invalid", name);
                  }
               }
            } else if (cell.getCol().is(ActionColumns.ChangeType)) {
               actionData.changeType = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumns.Priority)) {
               actionData.priorityStr = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumns.Version)) {
               actionData.version = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumns.AgilePoints)) {
               actionData.agilePoints = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumns.EstimatedHours)) {
               actionData.estimatedHours = Strings.isNumeric(cell.getValue()) ? Double.valueOf(cell.getValue()) : 0;
            } else if (cell.getCol().is(ActionColumns.AgileSprintName)) {
               actionData.agileSprintName = getOrBlank(cell);
            } else if (cell.getCol().is(ActionColumns.AgileTeamName)) {
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

   private void createArtifactsAndNotify() {
      AtsUser createdBy = atsApi.user();
      Date createdDate = new Date();
      NewActionDataMulti datas = new NewActionDataMulti(commitComment, createdBy);

      AtsUtilClient.setEmailEnabled(false);
      try {
         for (ActionData aData : actionDatas) {
            ChangeTypes changeType = getChangeType(aData);
            String priorityStr = getPriority(aData);

            Set<IAtsActionableItem> ais = atsApi.getActionableItemService().getActionableItems(aData.actionableItems);
            NewActionData data = atsApi.getActionService() //
               .createActionData(commitComment, aData.title, aData.desc) //
               .andAis(ais).andChangeType(changeType).andPriority(priorityStr) //
               .andCreatedBy(createdBy).andCreatedDate(createdDate);

            processTeamWorkflow(aData, data, ais);

            datas.add(data);
         }

         AtsUtilClient.setEmailEnabled(true);

         datas.setEmailPocs(emailPOCs);

         NewActionDataMulti newDatas = atsApi.getActionService().createActions(datas);
         if (newDatas.getRd().isErrors()) {
            XResultDataUI.report(newDatas.getRd(), getName());
         } else {
            Set<IAtsTeamWorkflow> teamWfs = new HashSet<>();
            for (NewActionData data : newDatas.getNewActionDatas()) {
               teamWfs.addAll(data.getActResult().getAtsTeamWfs());
            }
            WorldEditor.open(new WorldEditorSimpleProvider(getName(), AtsObjects.getArtifacts(teamWfs)));
         }

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

   private void processTeamWorkflow(ActionData aData, NewActionData data, Set<IAtsActionableItem> ais) {
      addToAgile(aData, data);

      for (JaxAttribute attr : aData.attributes) {
         AttributeTypeToken attrType = attr.getAttrType();
         Collection<String> values = new ArrayList<>();
         for (Object val : attr.getValues()) {
            if (val instanceof String && Strings.isValid((String) val)) {
               values.add((String) val);
            }
         }
         if (!values.isEmpty()) {
            data.andAttr(attrType, values);
         }
      }

      addToGoal(aData, data);

      if (!aData.version.equals("")) {
         IAtsTeamDefinition teamDef =
            atsApi.getActionableItemService().getTeamDefinitionInherited(ais.iterator().next());
         IAtsTeamDefinition teamDefHoldVer = atsApi.getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
         IAtsVersion version = atsApi.getVersionService().getVersion(teamDefHoldVer, aData.version);
         if (version == null) {
            rd.errorf("No version [%s] configured for Team Definition [%s]\n", aData.version,
               teamDefHoldVer.toStringWithId());
         } else {
            data.andVersion(version.getArtifactId());
         }
      }
      if (aData.estimatedHours != null) {
         data.andAttr(AtsAttributeTypes.EstimatedHours, aData.estimatedHours.toString());
      }
      if (aData.assigneeStrs.size() > 0) {
         data.andAssignees(aData.assignees);
      }
      if (aData.originator != null) {
         data.andCreatedBy(aData.originator);
      }
   }

   private void addToAgile(ActionData aData, NewActionData data) {
      // If Agile Team, add workflow to backlog
      if (Strings.isValid(aData.agileTeamName)) {
         IAgileBacklog backlog = getAgileBacklog(aData.agileTeamName);
         if (backlog != null) {
            data.andRelation(AtsRelationTypes.Goal_Goal, backlog.getArtifactId());
         }
      }

      // If Agile Sprint, add workflow to sprint
      if (Strings.isValid(aData.agileSprintName)) {
         IAgileSprint sprint = getAgileSprint(aData.agileTeamName, aData.agileSprintName);
         if (sprint != null) {
            data.andRelation(AtsRelationTypes.AgileSprintToItem_AgileSprint, sprint.getArtifactId());
         }
      }

      // If Agile points, add points to workflow
      if (Strings.isValid(aData.agilePoints)) {
         IAgileTeam aTeam = getAgileTeamByName(aData.agileTeamName);
         AttributeTypeToken attrType = atsApi.getAgileService().getAgileTeamPointsAttributeType(aTeam);
         if (attrType.getId().equals(AtsAttributeTypes.Points.getId())) {
            data.andAttr(attrType, aData.agilePoints);
         } else if (attrType.getId().equals(AtsAttributeTypes.PointsNumeric.getId())) {
            data.andAttr(attrType, aData.agilePoints);
         } else {
            throw new OseeArgumentException("Un-configured pointes types for team %s", aData);
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

   private void addToGoal(ActionData aData, NewActionData data) {
      if (toGoal != null) {
         GoalArtifact goal = (GoalArtifact) atsApi.getQueryService().getArtifact(toGoal);
         if (goal == null) {
            throw new OseeArgumentException("Goal artifact does not exist for goal %s", toGoal.toStringWithId());
         }
         data.andRelation(AtsRelationTypes.Goal_Goal, goal);
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
