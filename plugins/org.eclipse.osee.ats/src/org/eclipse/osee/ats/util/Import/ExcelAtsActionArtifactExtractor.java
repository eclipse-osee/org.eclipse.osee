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

import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.artifact.ActionManager;
import org.eclipse.osee.ats.core.config.ActionableItemArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionArtifact;
import org.eclipse.osee.ats.core.config.TeamDefinitionManagerCore;
import org.eclipse.osee.ats.core.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.type.AtsArtifactTypes;
import org.eclipse.osee.ats.core.type.AtsAttributeTypes;
import org.eclipse.osee.ats.core.type.AtsRelationTypes;
import org.eclipse.osee.ats.core.util.AtsCacheManager;
import org.eclipse.osee.ats.core.workflow.ActionableItemManagerCore;
import org.eclipse.osee.ats.core.workflow.ChangeType;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.operation.DuplicateWorkflowBlam.CreateTeamOption;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.exception.UserNotInDatabase;
import org.eclipse.osee.framework.core.model.IBasicUser;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class ExcelAtsActionArtifactExtractor {

   private final List<ActionData> actionDatas = new ArrayList<ActionData>();
   private final Set<Artifact> actionArts = new HashSet<Artifact>();
   private final Map<String, Artifact> actionNameToAction = new HashMap<String, Artifact>(100);
   private final boolean emailPOCs;
   private boolean dataIsValid = true;

   public ExcelAtsActionArtifactExtractor(boolean emailPOCs) {
      this.emailPOCs = emailPOCs;
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
            rd.logError("Row " + rowNum + "; Invalid Title");
         }
         Set<TeamDefinitionArtifact> teamDefs = new HashSet<TeamDefinitionArtifact>();
         if (aData.actionableItems.isEmpty()) {
            rd.logError("Row " + rowNum + ": Must have at least one ActionableItem defined");
         } else {
            for (String actionableItemName : aData.actionableItems) {
               try {
                  Collection<Artifact> aias =
                     AtsCacheManager.getArtifactsByName(AtsArtifactTypes.ActionableItem, actionableItemName);
                  if (aias.size() == 0) {
                     rd.logError("Row " + rowNum + ": Couldn't find actionable item for \"" + actionableItemName + "\"");
                  } else if (aias.size() > 1) {
                     rd.logError("Row " + rowNum + ": Duplicate actionable items found with name \"" + actionableItemName + "\"");
                  }
                  ActionableItemArtifact aia = (ActionableItemArtifact) aias.iterator().next();
                  teamDefs.addAll(ActionableItemManagerCore.getImpactedTeamDefs(Arrays.asList(aia)));
                  if (teamDefs.size() == 0) {
                     rd.logError("Row " + rowNum + ": No related Team Definition for Actionable Item\"" + actionableItemName + "\"");
                  } else if (teamDefs.size() > 1) {
                     rd.logError("Row " + rowNum + ": Duplicate Team Definitions found for Actionable Item\"" + actionableItemName + "\"");
                  }

               } catch (Exception ex) {
                  rd.logError("Row " + rowNum + " - " + ex.getLocalizedMessage());
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         }
         if (!aData.version.equals("")) {
            try {
               for (TeamDefinitionArtifact teamDef : teamDefs) {
                  if (teamDef.getTeamDefinitionHoldingVersions() == null) {
                     rd.logErrorWithFormat("No Team Definitions Holding Versions found for Team Definition [%s]",
                        teamDef);
                  }
                  if (teamDef.getTeamDefinitionHoldingVersions().getVersionArtifact(aData.version, false) == null) {
                     rd.logErrorWithFormat("No version [%s] configured for Team Definition [%s]", aData.version,
                        teamDef);
                  }
               }
            } catch (Exception ex) {
               rd.logError("Row " + rowNum + " - " + ex.getLocalizedMessage());
               OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            }
         }
         // If no assignees, ATS will auto-assign to correct person
         // Else if assignees, confirm that they are valid
         if (aData.assigneeStrs.size() > 0) {
            for (String assignee : aData.assigneeStrs) {
               try {
                  assignee = assignee.replaceFirst("^ *", "");
                  assignee = assignee.replaceFirst(" *$", "");
                  User user = UserManager.getUserByName(assignee);
                  if (user == null) {
                     rd.logError("Row " + rowNum + ": Couldn't retrieve user \"" + assignee + "\"");
                  } else {
                     aData.assignees.add(user);
                  }
               } catch (UserNotInDatabase ex) {
                  rd.logError("Row " + rowNum + ": " + ex.getLocalizedMessage());
               }
            }
         }
      }
      return rd;
   }

   public void createArtifactsAndNotify(SkynetTransaction transaction) {
      AtsUtil.setEmailEnabled(false);
      Set<TeamWorkFlowArtifact> teamWfs = new HashSet<TeamWorkFlowArtifact>();
      Date createdDate = new Date();
      try {
         User createdBy = UserManager.getUser();
         for (ActionData aData : actionDatas) {
            Artifact actionArt = actionNameToAction.get(aData.title);
            Collection<TeamWorkFlowArtifact> newTeamArts = new HashSet<TeamWorkFlowArtifact>();
            if (actionArt == null) {
               actionArt =
                  ActionManager.createAction(null, aData.title, aData.desc, ChangeType.getChangeType(aData.changeType),
                     aData.priorityStr, false, null,
                     ActionableItemManagerCore.getActionableItems(aData.actionableItems), createdDate, createdBy, null,
                     transaction);
               newTeamArts = ActionManager.getTeams(actionArt);
               actionNameToAction.put(aData.title, actionArt);
               actionArts.add(actionArt);
            } else {
               Set<ActionableItemArtifact> aias = ActionableItemManagerCore.getActionableItems(aData.actionableItems);
               Map<TeamDefinitionArtifact, Collection<ActionableItemArtifact>> teamDefToAias = getTeamDefToAias(aias);
               for (Entry<TeamDefinitionArtifact, Collection<ActionableItemArtifact>> entry : teamDefToAias.entrySet()) {

                  TeamWorkFlowArtifact teamWorkflow =
                     ActionManager.createTeamWorkflow(actionArt, entry.getKey(), entry.getValue(), aData.assignees,
                        transaction, createdDate, createdBy, null, CreateTeamOption.Duplicate_If_Exists);
                  teamWorkflow.setSoleAttributeValue(AtsAttributeTypes.Description, aData.desc);
                  if (Strings.isValid(aData.priorityStr) && !aData.priorityStr.equals("<Select>")) {
                     teamWorkflow.setSoleAttributeValue(AtsAttributeTypes.PriorityType, aData.priorityStr);
                  }
                  teamWorkflow.setSoleAttributeValue(AtsAttributeTypes.ChangeType, aData.changeType);
                  newTeamArts.add(teamWorkflow);
               }
            }
            if (!aData.version.equals("")) {
               for (TeamWorkFlowArtifact team : newTeamArts) {
                  Artifact verArt =
                     team.getTeamDefinition().getTeamDefinitionHoldingVersions().getVersionArtifact(aData.version,
                        false);
                  if (verArt == null) {
                     throw new OseeArgumentException("No version [%s] configured for Team Definition [%s]",
                        aData.version, team.getTeamDefinition());
                  }
                  verArt.addRelation(AtsRelationTypes.TeamWorkflowTargetedForVersion_Workflow, team);
               }
            }
            if (aData.estimatedHours != null) {
               for (TeamWorkFlowArtifact team : newTeamArts) {
                  team.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, aData.estimatedHours);
               }
            }
            if (aData.assigneeStrs.size() > 0) {
               for (TeamWorkFlowArtifact team : newTeamArts) {
                  team.getStateMgr().setAssignees(aData.assignees);
               }
            }
            for (TeamWorkFlowArtifact team : newTeamArts) {
               team.persist(transaction);
            }
            teamWfs.addAll(newTeamArts);
         }
         AtsUtil.setEmailEnabled(true);
         if (emailPOCs) {
            for (TeamWorkFlowArtifact team : teamWfs) {
               AtsNotifyUsers.getInstance().notify(team, AtsNotifyUsers.NotifyType.Assigned);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, ex);
      } finally {
         AtsUtil.setEmailEnabled(true);
      }
   }

   public Map<TeamDefinitionArtifact, Collection<ActionableItemArtifact>> getTeamDefToAias(Collection<ActionableItemArtifact> aias) throws OseeCoreException {
      Map<TeamDefinitionArtifact, Collection<ActionableItemArtifact>> teamDefToAias =
         new HashMap<TeamDefinitionArtifact, Collection<ActionableItemArtifact>>();
      for (ActionableItemArtifact aia : aias) {
         TeamDefinitionArtifact teamDef =
            TeamDefinitionManagerCore.getImpactedTeamDefs(Arrays.asList(aia)).iterator().next();
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
            OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
            rd.logError("Exception in parsing import (see log for details) " + (Strings.isValid(ex.getLocalizedMessage()) ? ex.getLocalizedMessage() : ""));
         }
         if (!rd.isEmpty()) {
            rd.report("Action Import Validation Errors");
            dataIsValid =
               MessageDialog.openConfirm(AWorkbench.getDisplay().getActiveShell(), "Validation Results",
                  "Validation Issues Reported, Continue Anyway?");
         }
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public String getDescription() {
      return "Extract each row as an Action";
   }

   /**
    * @return the actionArts
    */
   public Set<Artifact> getActionArts() {
      return actionArts;
   }

   public FileFilter getFileFilter() {
      return null;
   }

   public String getName() {
      return "Excel Ats Actions";
   }

   private final static class ActionData {
      protected String title = "";
      protected String desc = "";
      protected String priorityStr = "";
      protected Set<String> userComms = new HashSet<String>();
      protected String changeType = "";
      protected Set<String> assigneeStrs = new HashSet<String>();
      protected Set<IBasicUser> assignees = new HashSet<IBasicUser>();
      protected Set<String> actionableItems = new HashSet<String>();
      protected String version = "";
      protected Double estimatedHours = null;
   }

   private final static class InternalRowProcessor implements RowProcessor {

      private static enum Columns {
         Title,
         Description,
         ActionableItems,
         Assignees,
         Priority,
         ChangeType,
         UserCommunity,
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
            resultData.logWarning("Empty Row Found => " + rowNum + " skipping...");
            return;
         }

         ActionData aData = new ActionData();
         for (int i = 0; i < cols.length; i++) {
            if (headerRow[i] != null) {
               if (headerRow[i].equalsIgnoreCase(Columns.Title.name())) {
                  if (cols[i].equals("")) {
                     return;
                  }
                  aData.title = cols[i];
               } else if (headerRow[i].equalsIgnoreCase(Columns.Priority.name())) {
                  aData.priorityStr = cols[i];
               } else if (headerRow[i].equalsIgnoreCase(Columns.Version.name())) {
                  aData.version = cols[i] == null ? "" : cols[i];
               } else if (headerRow[i].equalsIgnoreCase(Columns.ChangeType.name())) {
                  aData.changeType = cols[i];
               } else if (headerRow[i].equalsIgnoreCase(Columns.Description.name())) {
                  aData.desc = cols[i] == null ? "" : cols[i];
               } else if (headerRow[i].equalsIgnoreCase(Columns.UserCommunity.name())) {
                  processUserCommunities(cols, aData, i);
               } else if (headerRow[i].equalsIgnoreCase(Columns.EstimatedHours.name())) {
                  if (Strings.isValid(cols[i])) {
                     aData.estimatedHours = new Double(cols[i]);
                  }
               } else if (headerRow[i].equalsIgnoreCase(Columns.ActionableItems.name())) {
                  processActionableItems(cols, aData, i);
               } else if (headerRow[i].equalsIgnoreCase(Columns.Assignees.name())) {
                  processAssignees(cols, aData, i);
               } else {
                  resultData.logError("Unhandled column => " + headerRow[i]);
               }
            }
         }
         actionDatas.add(aData);
      }

      private void processUserCommunities(String[] cols, ActionData aData, int i) {
         for (String str : cols[i].split(";")) {
            if (!str.equals("")) {
               aData.userComms.add(str);
            }
         }
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