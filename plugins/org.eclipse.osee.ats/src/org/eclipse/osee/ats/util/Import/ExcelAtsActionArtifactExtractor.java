/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.util.Import;

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
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.api.ai.IAtsActionableItem;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotificationEventFactory;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.task.JaxAttribute;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.team.CreateTeamOption;
import org.eclipse.osee.ats.api.team.IAtsTeamDefinition;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.api.workflow.ActionResult;
import org.eclipse.osee.ats.api.workflow.IAtsGoal;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.client.artifact.GoalArtifact;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilClient;
import org.eclipse.osee.ats.core.config.ActionableItems;
import org.eclipse.osee.ats.core.config.TeamDefinitions;
import org.eclipse.osee.ats.core.util.AtsObjects;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
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
   private boolean dataIsValid = true;
   private final IAtsGoal toGoal;

   public ExcelAtsActionArtifactExtractor(boolean emailPOCs, IAtsGoal toGoal) {
      this.emailPOCs = emailPOCs;
      this.toGoal = toGoal;
   }

   public XResultData dataIsValid() throws OseeCoreException {
      if (!dataIsValid) {
         return new XResultData(false);
      }
      XResultData rd = new XResultData();
      int rowNum = 1; // Header is row 1
      for (ActionData aData : actionDatas) {
         rowNum++;
         if (aData.title.equals("")) {
            rd.error("Row " + rowNum + "; Invalid Title");
         }
         Set<IAtsTeamDefinition> teamDefs = new HashSet<>();
         if (aData.actionableItems.isEmpty()) {
            rd.error("Row " + rowNum + ": Must have at least one ActionableItem defined");
         } else {
            for (String actionableItemName : aData.actionableItems) {
               try {
                  Collection<IAtsActionableItem> aias = new ArrayList<>();
                  for (Artifact aiaArt : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.ActionableItem,
                     actionableItemName, AtsClientService.get().getAtsBranch())) {
                     IAtsActionableItem ai = AtsClientService.get().getConfigItemFactory().getActionableItem(aiaArt);
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
                     teamDefs.addAll(ActionableItems.getImpactedTeamDefs(Arrays.asList(aia)));
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
                  if (teamDef.getTeamDefinitionHoldingVersions() == null) {
                     rd.errorf("No Team Definitions Holding Versions found for Team Definition [%s]", teamDef);
                  }
                  if (teamDef.getTeamDefinitionHoldingVersions().getVersion(aData.version) == null) {
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
                  IAtsUser user = AtsClientService.get().getUserService().getUserByName(assignee);
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
      }
      return rd;
   }

   public void createArtifactsAndNotify(IAtsChangeSet changes) {
      AtsUtilClient.setEmailEnabled(false);
      Set<IAtsTeamWorkflow> teamWfs = new HashSet<>();
      Date createdDate = new Date();
      try {
         IAtsUser createdBy = AtsClientService.get().getUserService().getCurrentUser();
         for (ActionData aData : actionDatas) {
            ActionResult result = actionNameToAction.get(aData.title);
            Collection<IAtsTeamWorkflow> newTeamWfs = new HashSet<>();
            if (result == null) {
               result = AtsClientService.get().getActionFactory().createAction(null, aData.title, aData.desc,
                  ChangeType.getChangeType(aData.changeType), aData.priorityStr, false, null,
                  ActionableItems.getActionableItems(aData.actionableItems, AtsClientService.get()), createdDate,
                  createdBy, null, changes);
               newTeamWfs = AtsClientService.get().getWorkItemService().getTeams(result);
               addToGoal(org.eclipse.osee.framework.jdk.core.util.Collections.castAll(TeamWorkFlowArtifact.class,
                  AtsObjects.getArtifacts(newTeamWfs)), changes);
               actionNameToAction.put(aData.title, result);
               actionArts.add((Artifact) result.getActionArt());
            } else {
               Set<IAtsActionableItem> aias = new HashSet<>();
               for (String actionableItemName : aData.actionableItems) {
                  for (Artifact aiaArt : ArtifactQuery.getArtifactListFromTypeAndName(AtsArtifactTypes.ActionableItem,
                     actionableItemName, AtsClientService.get().getAtsBranch())) {
                     IAtsActionableItem ai = AtsClientService.get().getConfigItemFactory().getActionableItem(aiaArt);
                     if (ai != null) {
                        aias.add(ai);
                     }
                  }
               }
               Map<IAtsTeamDefinition, Collection<IAtsActionableItem>> teamDefToAias = getTeamDefToAias(aias);
               for (Entry<IAtsTeamDefinition, Collection<IAtsActionableItem>> entry : teamDefToAias.entrySet()) {

                  IAtsTeamWorkflow teamWorkflow = AtsClientService.get().getActionFactory().createTeamWorkflow(
                     result.getAction(), entry.getKey(), entry.getValue(), aData.assignees, changes, createdDate,
                     createdBy, null, CreateTeamOption.Duplicate_If_Exists);
                  changes.setSoleAttributeValue(teamWorkflow, AtsAttributeTypes.Description, aData.desc);
                  if (Strings.isValid(aData.priorityStr) && !aData.priorityStr.equals("<Select>")) {
                     changes.setSoleAttributeValue(teamWorkflow, AtsAttributeTypes.PriorityType, aData.priorityStr);
                  }
                  changes.setSoleAttributeValue(teamWorkflow, AtsAttributeTypes.ChangeType, aData.changeType);

                  for (JaxAttribute attr : aData.attributes) {
                     AttributeTypeId attrType = AttributeTypeManager.getType(attr.getAttrTypeName());
                     changes.setAttributeValues(teamWorkflow, attrType, attr.getValues());
                  }
                  newTeamWfs.add((TeamWorkFlowArtifact) teamWorkflow.getStoreObject());
                  addToGoal(Collections.singleton((TeamWorkFlowArtifact) teamWorkflow.getStoreObject()), changes);
               }
            }
            if (!aData.version.equals("")) {
               for (IAtsTeamWorkflow team : newTeamWfs) {
                  IAtsVersion version =
                     team.getTeamDefinition().getTeamDefinitionHoldingVersions().getVersion(aData.version);
                  if (version == null) {
                     throw new OseeArgumentException("No version [%s] configured for Team Definition [%s]",
                        aData.version, team.getTeamDefinition());
                  }
                  AtsClientService.get().getVersionService().setTargetedVersion(team, version, changes);
               }
            }
            if (aData.estimatedHours != null) {
               for (IAtsTeamWorkflow team : newTeamWfs) {
                  changes.setSoleAttributeValue((ArtifactId) team, AtsAttributeTypes.EstimatedHours,
                     aData.estimatedHours);
               }
            }
            if (aData.assigneeStrs.size() > 0) {
               for (IAtsTeamWorkflow team : newTeamWfs) {
                  team.getStateMgr().setAssignees(aData.assignees);
               }
            }
            for (IAtsTeamWorkflow team : newTeamWfs) {
               changes.add(team);
            }
            teamWfs.addAll(newTeamWfs);
         }
         AtsUtilClient.setEmailEnabled(true);
         if (emailPOCs) {
            for (IAtsTeamWorkflow teamWf : teamWfs) {
               try {
                  changes.addWorkItemNotificationEvent(AtsNotificationEventFactory.getWorkItemNotificationEvent(
                     AtsClientService.get().getUserService().getCurrentUser(), teamWf, AtsNotifyType.Assigned));
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

   private void addToGoal(Collection<TeamWorkFlowArtifact> newTeamArts, IAtsChangeSet changes) throws OseeCoreException {
      if (toGoal != null) {
         GoalArtifact goal = (GoalArtifact) AtsClientService.get().getArtifact(toGoal);
         if (goal == null) {
            throw new OseeArgumentException("Goal artifact does not exist for goal %s", toGoal.toStringWithId());
         }
         for (Artifact art : newTeamArts) {
            goal.addMember(art);
         }
         changes.add(goal);
      }
   }

   public Map<IAtsTeamDefinition, Collection<IAtsActionableItem>> getTeamDefToAias(Collection<IAtsActionableItem> aias) throws OseeCoreException {
      Map<IAtsTeamDefinition, Collection<IAtsActionableItem>> teamDefToAias =
         new HashMap<IAtsTeamDefinition, Collection<IAtsActionableItem>>();
      for (IAtsActionableItem aia : aias) {
         IAtsTeamDefinition teamDef = TeamDefinitions.getImpactedTeamDefs(Arrays.asList(aia)).iterator().next();
         if (teamDefToAias.containsKey(teamDef)) {
            teamDefToAias.get(teamDef).add(aia);
         } else {
            teamDefToAias.put(teamDef, Arrays.asList(aia));
         }
      }
      return teamDefToAias;
   }

   public void process(URI source) throws OseeCoreException {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         XResultData rd = new XResultData();
         try {
            xmlReader.setContentHandler(new ExcelSaxHandler(new InternalRowProcessor(actionDatas, rd), true));
            xmlReader.parse(new InputSource(new InputStreamReader(source.toURL().openStream(), "UTF-8")));
         } catch (SAXException ex) {
            OseeLog.log(Activator.class, Level.SEVERE, ex);
            rd.error("Exception in parsing import (see log for details) " + (Strings.isValid(
               ex.getLocalizedMessage()) ? ex.getLocalizedMessage() : ""));
         }
         if (!rd.isEmpty()) {
            XResultDataUI.report(rd, "Action Import Validation Errors");
            dataIsValid = MessageDialog.openConfirm(AWorkbench.getDisplay().getActiveShell(), "Validation Results",
               "Validation Issues Reported, Continue Anyway?");
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

   private final static class ActionData {
      protected String title = "";
      protected String desc = "";
      protected String priorityStr = "";
      protected String changeType = "";
      protected Set<String> assigneeStrs = new HashSet<>();
      protected List<IAtsUser> assignees = new LinkedList<>();
      protected Set<String> actionableItems = new HashSet<>();
      protected String version = "";
      protected Double estimatedHours = null;
      protected List<JaxAttribute> attributes = new LinkedList<>();
   }

   private final static class InternalRowProcessor implements RowProcessor {

      private static enum Columns {
         Title,
         Description,
         ActionableItems,
         Assignees,
         Priority,
         ChangeType,
         Version,
         EstimatedHours,
         Goal
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
                     aData.estimatedHours = new Double(cols[i]);
                  }
               } else if (header.equalsIgnoreCase(Columns.ActionableItems.name())) {
                  processActionableItems(cols, aData, i);
               } else if (header.equalsIgnoreCase(Columns.Assignees.name())) {
                  processAssignees(cols, aData, i);
               } else {
                  String attrTypeName = header;
                  if (Strings.isValid(attrTypeName)) {
                     AttributeType attributeType = AttributeTypeManager.getType(attrTypeName);
                     if (attributeType == null) {
                        OseeLog.log(Activator.class, Level.SEVERE, "Invalid Attribute Type Name => " + header);
                     } else {
                        if (!ArtifactTypeManager.getArtifactTypesFromAttributeType(attributeType,
                           AtsClientService.get().getAtsBranch()).contains(AtsArtifactTypes.Task)) {
                           OseeLog.log(Activator.class, Level.SEVERE, "Invalid Attribute Type for Task => " + header);
                        } else {
                           String value = cols[i];
                           if (Strings.isValid(value)) {
                              JaxAttribute attr = new JaxAttribute();
                              attr.setAttrTypeName(attrTypeName);
                              attr.getValues().add(value);
                              aData.attributes.add(attr);
                           }
                        }
                     }
                  } else {
                     OseeLog.log(Activator.class, Level.SEVERE, "Unhandled column => " + header);
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
   }
}
