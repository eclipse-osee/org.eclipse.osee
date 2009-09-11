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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.AtsCacheManager;
import org.eclipse.osee.ats.util.ActionManager;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsPriority;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLevel;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.results.XResultData;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class ExcelAtsActionArtifactExtractor {

   private final Set<ActionData> actionDatas;
   private final Set<ActionArtifact> actionArts;
   private final boolean emailPOCs;

   public ExcelAtsActionArtifactExtractor(boolean emailPOCs) {
      this.emailPOCs = emailPOCs;
      this.actionDatas = new HashSet<ActionData>();
      this.actionArts = new HashSet<ActionArtifact>();
   }

   public boolean dataIsValid() throws OseeCoreException {
      System.out.println("Validating...");
      XResultData rd = new XResultData();
      int rowNum = 1; // Header is row 1
      for (ActionData aData : actionDatas) {
         rowNum++;
         if (aData.title.equals("")) {
            rd.logError("Row " + rowNum + "; Invalid Title");
         }
         if (aData.actionableItems.size() == 0) {
            rd.logError("Row " + rowNum + ": Must have at least one ActionableItem defined");
         } else {
            for (String actionableItemName : aData.actionableItems) {
               try {
                  if (AtsCacheManager.getArtifactsByName(
                        ArtifactTypeManager.getType(ActionableItemArtifact.ARTIFACT_NAME), actionableItemName).size() > 0) {
                     rd.logError("Row " + rowNum + ": Couldn't find actionable item for \"" + actionableItemName + "\"");
                  }
               } catch (Exception ex) {
                  rd.logError("Row " + rowNum + " - " + ex.getLocalizedMessage());
                  OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
               }
            }
         }
         if (!aData.version.equals("")) {
            try {
               if (AtsCacheManager.getSoleArtifactByName(ArtifactTypeManager.getType(VersionArtifact.ARTIFACT_NAME),
                     aData.version) == null) {
                  rd.logError("Row " + rowNum + ": Can't find single version \"" + aData.version + "\"");
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
               User user = UserManager.getUserByName(assignee);
               if (user == null) {
                  rd.logError("Row " + rowNum + ": Couldn't retrieve user \"" + assignee + "\"");
               } else {
                  aData.assignees.add(user);
               }
            }
         }
      }
      if (!rd.toString().equals("")) {
         rd.report("Ats Action Import Errors");
         return false;
      }
      return true;
   }

   public void createArtifactsAndNotify(SkynetTransaction transaction) {
      AtsUtil.setEmailEnabled(false);
      System.out.println("Creating...");
      Set<TeamWorkFlowArtifact> teamWfs = new HashSet<TeamWorkFlowArtifact>();
      try {
         for (ActionData aData : actionDatas) {
            ActionArtifact actionArt =
                  ActionManager.createAction(null, aData.title, aData.desc, ChangeType.getChangeType(aData.changeType),
                        AtsPriority.PriorityType.getPriority(aData.priorityStr), aData.userComms, false, null,
                        ActionableItemArtifact.getActionableItems(aData.actionableItems), transaction);
            actionArts.add(actionArt);
            if (!aData.version.equals("")) {
               VersionArtifact verArt =
                     (VersionArtifact) AtsCacheManager.getSoleArtifactByName(
                           ArtifactTypeManager.getType(VersionArtifact.ARTIFACT_NAME), aData.version);

               for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
                  verArt.addRelation(AtsRelation.TeamWorkflowTargetedForVersion_Workflow, team);
               }
            }
            if (aData.assigneeStrs.size() > 0) {
               for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
                  team.getSmaMgr().getStateMgr().setAssignees(aData.assignees);
               }
            }
            for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
               team.persist(transaction);
            }
            teamWfs.addAll(actionArt.getTeamWorkFlowArtifacts());
         }
         AtsUtil.setEmailEnabled(true);
         if (emailPOCs) {
            for (TeamWorkFlowArtifact team : teamWfs) {
               AtsNotifyUsers.notify(team, AtsNotifyUsers.NotifyType.Assigned);
            }
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      } finally {
         AtsUtil.setEmailEnabled(true);
      }
   }

   public void process(URI source) throws OseeCoreException {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         xmlReader.setContentHandler(new ExcelSaxHandler(new InternalRowProcessor(actionDatas), true));
         xmlReader.parse(new InputSource(new InputStreamReader(source.toURL().openStream(), "UTF-8")));
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public String getDescription() {
      return "Extract each row as an Action";
   }

   /**
    * @return the actionArts
    */
   public Set<ActionArtifact> getActionArts() {
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
      protected Set<User> assignees = new HashSet<User>();
      protected Set<String> actionableItems = new HashSet<String>();
      protected String version = "";
   }

   private final static class InternalRowProcessor implements RowProcessor {

      private static enum Columns {
         Title, Description, ActionableItems, Assignees, Priority, ChangeType, UserCommunity, Version
      };

      private String[] headerRow;
      private int rowNum = 0;
      private final Set<ActionData> actionDatas;

      protected InternalRowProcessor(Set<ActionData> actionDatas) {
         this.actionDatas = actionDatas;
      }

      @Override
      public void processEmptyRow() {
      }

      @Override
      public void processCommentRow(String[] row) {
      }

      @Override
      public void reachedEndOfWorksheet() {
      }

      @Override
      public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
      }

      @Override
      public void foundStartOfWorksheet(String sheetName) {
      }

      @Override
      public void processHeaderRow(String[] headerRow) {
         this.headerRow = headerRow.clone();
      }

      @Override
      public void processRow(String[] cols) {
         rowNum++;
         System.out.println("Processing Row " + rowNum);

         boolean fullRow = false;
         for (int i = 0; i < cols.length; i++) {
            if (cols[i] != null && !cols[i].equals("")) {
               fullRow = true;
               break;
            }
         }
         if (!fullRow) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Empty Row Found => " + rowNum + " skipping...");
            return;
         }

         System.out.println("Reading rows...");
         ActionData aData = new ActionData();
         for (int i = 0; i < cols.length; i++) {
            if (headerRow[i] == null) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, "Null header column => " + i);
            } else if (headerRow[i].equalsIgnoreCase(Columns.Title.name())) {
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
               for (String str : cols[i].split(";")) {
                  if (!str.equals("")) {
                     aData.userComms.add(str);
                  }
               }
            } else if (headerRow[i].equalsIgnoreCase(Columns.ActionableItems.name())) {
               for (String str : cols[i].split(";")) {
                  if (!str.equals("")) {
                     aData.actionableItems.add(str);
                  }
               }
            } else if (headerRow[i].equalsIgnoreCase(Columns.Assignees.name())) {
               if (cols[i] != null) {
                  for (String str : cols[i].split(";")) {
                     if (!str.equals("")) {
                        aData.assigneeStrs.add(str);
                     }
                  }
               }
            } else {
               OseeLog.log(AtsPlugin.class, OseeLevel.SEVERE_POPUP, "Unhandled column => " + headerRow[i]);
            }
         }
         actionDatas.add(aData);
      }
   }
}