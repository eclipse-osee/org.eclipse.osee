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

package org.eclipse.osee.ats.ide.util.Import;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
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
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.util.AtsUtilClient;
import org.eclipse.osee.ats.ide.workflow.goal.GoalArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class ExcelAtsActionArtifactExtractor {

   private final List<ActionData> actionDatas = new ArrayList<>();
   private final Set<Artifact> actionArts = new HashSet<>();
   private final Map<String, ActionResult> actionNameToAction = new HashMap<>(100);
   private final boolean emailPOCs;
   private final boolean dataIsValid = true;
   private final IAtsGoal toGoal;
   private final Map<String, IAgileTeam> teamNameByTeamMap = new HashMap<>();
   private InputStreamReader inputStream;
   private XResultData rd;

   public ExcelAtsActionArtifactExtractor(boolean emailPOCs, IAtsGoal toGoal) {
      this.emailPOCs = emailPOCs;
      this.toGoal = toGoal;
   }

   public XResultData dataIsValid() {
      if (!dataIsValid) {
         return new XResultData(false);
      }
      rd = new XResultData();
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
                     actionableItemName, AtsApiService.get().getAtsBranch())) {
                     IAtsActionableItem ai =
                        AtsApiService.get().getActionableItemService().getActionableItemById(aiaArt);
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
                     teamDefs.addAll(
                        AtsApiService.get().getActionableItemService().getImpactedTeamDefs(Arrays.asList(aia)));
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
         if (!aData.version.equals("")) {
            try {
               for (IAtsTeamDefinition teamDef : teamDefs) {
                  if (AtsApiService.get().getTeamDefinitionService().getTeamDefHoldingVersions(teamDef) == null) {
                     rd.errorf("No Team Definitions Holding Versions found for Team Definition [%s]", teamDef);
                  }
                  IAtsTeamDefinition teamDefHolVer =
                     AtsApiService.get().getTeamDefinitionService().getTeamDefHoldingVersions(teamDef);
                  if (AtsApiService.get().getVersionService().getVersion(teamDefHolVer, aData.version) == null) {
                     rd.errorf("No version [%s] configured for Team Definition [%s]", aData.version, teamDef);
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
                  AtsUser user = AtsApiService.get().getUserService().getUserByName(assignee);
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
               rd.errorf("Invalid team name [%s]", aData.agileTeamName);
            }
            IAgileBacklog backlog = getAgileBacklog(aData.agileTeamName);
            if (backlog == null) {
               rd.errorf("No backlog for team [%s]", aData.agileTeamName);
            }
         }

         if (Strings.isValid(aData.agileSprintName)) {
            IAgileSprint sprint = getAgileSprint(aData.agileTeamName, aData.agileSprintName);
            if (sprint == null) {
               rd.errorf("Invalid sprint name [%s] for team [%s]", aData.agileSprintName, aData.agileTeamName);
            }
         }

         if (Strings.isValid(aData.agilePoints)) {
            IAgileTeam aTeam = getAgileTeamByName(aData.agileTeamName);
            if (aTeam == null) {
               rd.errorf("Invalid team name [%s] for points [%s]", aData.agileTeamName, aData.agilePoints);
            } else {
               AttributeTypeId pointsAttrType =
                  AtsApiService.get().getAgileService().getAgileTeamPointsAttributeType(aTeam);
               if (pointsAttrType == null) {
                  rd.errorf("Points not configured for team [%s]", aData.agileTeamName);
               }
            }
         }
      }
      return rd;
   }

   public void createArtifactsAndNotify(IAtsChangeSet changes) {
      AtsUtilClient.setEmailEnabled(false);
      Set<IAtsTeamWorkflow> teamWfs = new HashSet<>();
      Date createdDate = new Date();
      try {
         AtsUser createdBy = AtsApiService.get().getUserService().getCurrentUser();
         for (ActionData aData : actionDatas) {
            ActionResult actionResult = actionNameToAction.get(aData.title);
            if (actionResult == null) {
               ChangeTypes changeType = getChangeType(aData);
               String priorityStr = getPriority(aData);
               ActionResult aResult = AtsApiService.get().getActionService().createAction(null, aData.title, aData.desc,
                  changeType, priorityStr, false, null,
                  AtsApiService.get().getActionableItemService().getActionableItems(aData.actionableItems), createdDate,
                  createdBy, null, changes);
               actionNameToAction.put(aData.title, aResult);
               for (IAtsTeamWorkflow teamWf : aResult.getTeamWfs()) {
                  processTeamWorkflow(changes, aData, teamWf);
                  teamWfs.add(teamWf);
               }
               actionArts.add(AtsApiService.get().getQueryServiceIde().getArtifact(aResult.getActionArt()));
            } else {
               Set<IAtsActionableItem> aias = new HashSet<>();
               for (String actionableItemName : aData.actionableItems) {
                  for (Artifact aiaArt : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.ActionableItem,
                     actionableItemName, AtsApiService.get().getAtsBranch())) {
                     IAtsActionableItem ai =
                        AtsApiService.get().getActionableItemService().getActionableItemById(aiaArt);
                     if (ai != null) {
                        aias.add(ai);
                     }
                  }
               }
               Map<IAtsTeamDefinition, Collection<IAtsActionableItem>> teamDefToAias = getTeamDefToAias(aias);
               for (Entry<IAtsTeamDefinition, Collection<IAtsActionableItem>> entry : teamDefToAias.entrySet()) {
                  IAtsTeamWorkflow teamWf = AtsApiService.get().getActionService().createTeamWorkflow(
                     actionResult.getAction(), entry.getKey(), entry.getValue(), aData.assignees, changes, createdDate,
                     createdBy, null, CreateTeamOption.Duplicate_If_Exists);
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
                     AtsApiService.get().getUserService().getCurrentUser(), teamWf, AtsNotifyType.Assigned));
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Error adding ATS Notification Event", ex);
               }
            }
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
            AtsApiService.get().getTeamDefinitionService().getTeamDefHoldingVersions(teamWf.getTeamDefinition());
         IAtsVersion version = AtsApiService.get().getVersionService().getVersion(teamDefHoldVer, aData.version);
         if (version == null) {
            rd.errorf("No version [%s] configured for Team Definition [%s]", aData.version, teamWf.getTeamDefinition());
         }
         AtsApiService.get().getVersionService().setTargetedVersion(teamWf, version, changes);
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
         AttributeTypeToken attrType = AtsApiService.get().getAgileService().getAgileTeamPointsAttributeType(aTeam);
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
         IAgileTeam aTeam = AtsApiService.get().getAgileService().getAgileTeamByName(agileTeamName);
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
         for (IAgileSprint teamSprint : AtsApiService.get().getAgileService().getAgileSprints(aTeam)) {
            if (teamSprint.getName().equals(agileSprintName)) {
               return teamSprint;
            }
         }
      }
      return null;
   }

   private IAgileBacklog getAgileBacklog(String agileTeamName) {
      IAgileTeam aTeam = AtsApiService.get().getAgileService().getAgileTeamByName(agileTeamName);
      if (aTeam != null) {
         return AtsApiService.get().getAgileService().getAgileBacklog(aTeam);
      }
      return null;
   }

   private void addToGoal(Collection<TeamWorkFlowArtifact> newTeamArts, IAtsChangeSet changes) {
      if (toGoal != null) {
         GoalArtifact goal = (GoalArtifact) AtsApiService.get().getQueryService().getArtifact(toGoal);
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
            AtsApiService.get().getTeamDefinitionService().getImpactedTeamDefs(Arrays.asList(aia)).iterator().next();
         if (teamDefToAias.containsKey(teamDef)) {
            teamDefToAias.get(teamDef).add(aia);
         } else {
            teamDefToAias.put(teamDef, Arrays.asList(aia));
         }
      }
      return teamDefToAias;
   }

   public void process(URI source) {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         XResultData rd = new XResultData();
         try {
            xmlReader.setContentHandler(new ExcelSaxHandler(new InternalRowProcessor(actionDatas, rd), true));
            inputStream = new InputStreamReader(source.toURL().openStream(), "UTF-8");
            xmlReader.parse(new InputSource(inputStream));
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            rd.errorf("Exception in parsing import (see log for details) %s\n",
               (Strings.isValid(ex.getLocalizedMessage()) ? ex.getLocalizedMessage() : ""));
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }
         if (!rd.isEmpty()) {
            XResultDataUI.report(rd, "Action Import Validation Errors");
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
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

   public final static class ActionData {
      protected String title = "";
      protected String desc = "";
      protected String priorityStr = "";
      protected String changeType = "";
      protected Set<String> assigneeStrs = new HashSet<>();
      protected List<AtsUser> assignees = new LinkedList<>();
      protected AtsUser originator = null;
      protected Set<String> actionableItems = new HashSet<>();
      protected String version = "";
      protected Double estimatedHours = null;
      protected List<JaxAttribute> attributes = new LinkedList<>();
      protected String agilePoints = "";
      protected String agileTeamName = "";
      protected String agileSprintName = "";

   }

   private final static class InternalRowProcessor implements RowProcessor {

      private static enum Columns {
         Title("Title"),
         Description("Description"),
         ActionableItems("ActionableItems"),
         Assignees("ActionableItems"),
         Originator("Originator"),
         Priority("Priority"),
         ChangeType("AllowDeny"),
         Version("Version"),
         EstimatedHours("EstimatedHours"),
         Goal("Goal"),
         AgileTeamName("Agile Team Name"),
         AgileSprintName("Agile Sprint Name"),
         AgilePoints("Agile Points");

         private final String colName;

         private Columns(String name) {
            colName = name;
         }

         public String getColName() {
            return colName;
         }
      };

      private String[] headerRow;
      private int rowNum = 0;
      private final List<ActionData> actionDatas;
      private final XResultData resultData;

      protected InternalRowProcessor(List<ActionData> actionDatas, XResultData resultData) {
         this.actionDatas = actionDatas;
         this.resultData = resultData;
      }

      @Override
      public void processEmptyRow() {
         // do nothing
      }

      @Override
      public void processCommentRow(String[] row) {
         // do nothing
      }

      @Override
      public void reachedEndOfWorksheet() {
         // do nothing
      }

      @Override
      public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
         // do nothing
      }

      @Override
      public void foundStartOfWorksheet(String sheetName) {
         // do nothing
      }

      @Override
      public void processHeaderRow(String[] headerRow) {
         this.headerRow = headerRow.clone();
      }

      @Override
      public void processRow(String[] cols) {
         rowNum++;

         boolean fullRow = false;
         for (int i = 0; i < cols.length; i++) {
            if (Strings.isValid(cols[i])) {
               fullRow = true;
               break;
            }
         }
         if (!fullRow) {
            resultData.warning("Empty Row Found => " + rowNum + " skipping...");
            return;
         }

         ActionData aData = new ActionData();
         for (int i = 0; i < cols.length; i++) {
            String header = headerRow[i];
            if (header != null) {
               if (header.equalsIgnoreCase(Columns.Title.name())) {
                  if (cols[i].equals("")) {
                     return;
                  }
                  aData.title = cols[i];
               } else if (header.equalsIgnoreCase(Columns.Priority.name())) {
                  aData.priorityStr = cols[i];
               } else if (header.equalsIgnoreCase(Columns.Version.name())) {
                  aData.version = cols[i] == null ? "" : cols[i];
               } else if (header.equalsIgnoreCase(Columns.ChangeType.name())) {
                  aData.changeType = cols[i];
               } else if (header.equalsIgnoreCase(Columns.Description.name())) {
                  aData.desc = cols[i] == null ? "" : cols[i];
               } else if (header.equalsIgnoreCase(Columns.EstimatedHours.name())) {
                  if (Strings.isValid(cols[i])) {
                     aData.estimatedHours = Double.valueOf(cols[i]);
                  }
               } else if (header.equalsIgnoreCase(Columns.ActionableItems.name())) {
                  processActionableItems(cols, aData, i);
               } else if (header.equalsIgnoreCase(Columns.Assignees.name())) {
                  processAssignees(cols, aData, i);
               } else if (header.equalsIgnoreCase(Columns.Originator.name())) {
                  processOriginator(cols, aData, i);
               } else if (header.equalsIgnoreCase(Columns.AgileTeamName.getColName())) {
                  if (Strings.isValid(cols[i])) {
                     aData.agileTeamName = cols[i];
                  }
               } else if (header.equalsIgnoreCase(Columns.AgileSprintName.getColName())) {
                  if (Strings.isValid(cols[i])) {
                     aData.agileSprintName = cols[i];
                  }
               } else if (header.equalsIgnoreCase(Columns.AgilePoints.getColName())) {
                  if (Strings.isValid(cols[i])) {
                     aData.agilePoints = cols[i];
                  }
               } else {
                  String attrTypeName = header;
                  if (Strings.isValid(attrTypeName)) {
                     AttributeTypeToken attributeType = AttributeTypeManager.getType(attrTypeName);
                     if (attributeType == null) {
                        resultData.errorf("Invalid Attribute Type Name => %s\n", header);
                     } else {
                        if (!AtsArtifactTypes.Task.isValidAttributeType(attributeType)) {
                           resultData.errorf("Invalid Attribute Type for Task => %s\n", header);
                        } else {
                           String value = cols[i];
                           if (Strings.isValid(value)) {
                              JaxAttribute attr = new JaxAttribute();
                              attr.setAttrType(attributeType);
                              attr.getValues().add(value);
                              aData.attributes.add(attr);
                           }
                        }
                     }
                  } else {
                     resultData.errorf("Unhandled column => %s\n", header);
                  }
               }
            }
         }
         actionDatas.add(aData);
      }

      private void processActionableItems(String[] cols, ActionData aData, int i) {
         for (String str : cols[i].split(";")) {
            if (!str.equals("")) {
               aData.actionableItems.add(str);
            }
         }
      }

      private void processAssignees(String[] cols, ActionData aData, int i) {
         if (cols[i] != null) {
            for (String str : cols[i].split(";")) {
               if (!str.equals("")) {
                  aData.assigneeStrs.add(str);
               }
            }
         }
      }

      private void processOriginator(String[] cols, ActionData aData, int i) {
         String origStr = cols[i];
         if (Strings.isValid(origStr)) {
            AtsUser orig = AtsApiService.get().getUserService().getUserByName(origStr);
            if (orig == null) {
               resultData.errorf("Invalid name for originator [%s] rown %s", origStr, i);
            } else {
               aData.originator = orig;
            }
         }
      }
   }

   public List<ActionData> getActionDatas() {
      return actionDatas;
   }
}
