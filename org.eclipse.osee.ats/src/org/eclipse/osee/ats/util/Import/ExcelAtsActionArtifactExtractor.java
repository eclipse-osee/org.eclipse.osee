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

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.AtsCache;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.ats.util.AtsPriority;
import org.eclipse.osee.ats.util.AtsRelation;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.exception.OseeCoreException;
import org.eclipse.osee.framework.ui.skynet.Import.AbstractArtifactExtractor;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.xresults.XResultData;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class ExcelAtsActionArtifactExtractor extends AbstractArtifactExtractor implements RowProcessor {
   private static final String description = "Extract each row as an Action";
   private ExcelSaxHandler excelHandler;
   private String[] headerRow;
   private int rowNum = 0;
   private final boolean emailPOCs;
   private enum Columns {
      Title, Description, ActionableItems, Assignees, Priority, ChangeType, UserCommunity, Version
   };

   public static String getDescription() {
      return description;
   }
   private Set<ActionData> actionDatas = new HashSet<ActionData>();
   private Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();

   public ExcelAtsActionArtifactExtractor(Branch branch, boolean emailPOCs) throws SQLException, IllegalArgumentException {
      super(branch);
      this.emailPOCs = emailPOCs;
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processHeaderRow(java.lang.String[])
    */
   public void processHeaderRow(String[] headerRow) {
      this.headerRow = headerRow.clone();
   }

   private class ActionData {
      public String title = "";
      public String desc = "";
      public String priorityStr = "";
      public Set<String> userComms = new HashSet<String>();
      public String changeType = "";
      public Set<String> assigneeStrs = new HashSet<String>();
      public Set<User> assignees = new HashSet<User>();
      public Set<String> actionableItems = new HashSet<String>();
      public String version = "";
   }

   /**
    * import Artifacts
    * 
    * @param cols
    */
   public void processRow(String[] cols) {

      rowNum++;
      System.out.println("Processing Row " + rowNum);

      boolean fullRow = false;
      for (int i = 0; i < cols.length; i++)
         if (cols[i] != null && !cols[i].equals("")) {
            fullRow = true;
            break;
         }
      if (!fullRow) {
         OSEELog.logSevere(AtsPlugin.class, "Empty Row Found => " + rowNum + " skipping...", false);
         return;
      }

      System.out.println("Reading rows...");
      ActionData aData = new ActionData();
      for (int i = 0; i < cols.length; i++) {
         if (headerRow[i] == null) {
            OSEELog.logSevere(AtsPlugin.class, "Null header column => " + i, false);
         } else if (headerRow[i].equalsIgnoreCase(Columns.Title.name())) {
            if (cols[i].equals("")) return;
            aData.title = cols[i];
         } else if (headerRow[i].equalsIgnoreCase(Columns.Priority.name())) {
            aData.priorityStr = cols[i];
         } else if (headerRow[i].equalsIgnoreCase(Columns.Version.name())) {
            aData.version = (cols[i] == null ? "" : cols[i]);
         } else if (headerRow[i].equalsIgnoreCase(Columns.ChangeType.name())) {
            aData.changeType = cols[i];
         } else if (headerRow[i].equalsIgnoreCase(Columns.Description.name())) {
            aData.desc = (cols[i] == null ? "" : cols[i]);
         } else if (headerRow[i].equalsIgnoreCase(Columns.UserCommunity.name())) {
            for (String str : cols[i].split(";")) {
               if (!str.equals("")) aData.userComms.add(str);
            }
         } else if (headerRow[i].equalsIgnoreCase(Columns.ActionableItems.name())) {
            for (String str : cols[i].split(";")) {
               if (!str.equals("")) aData.actionableItems.add(str);
            }
         } else if (headerRow[i].equalsIgnoreCase(Columns.Assignees.name())) {
            if (cols[i] != null) for (String str : cols[i].split(";")) {
               if (!str.equals("")) aData.assigneeStrs.add(str);
            }
         } else {
            OSEELog.logSevere(AtsPlugin.class, "Unhandled column => " + headerRow[i], true);
         }
      }
      actionDatas.add(aData);
   }

   public boolean dataIsValid() throws OseeCoreException, SQLException {
      System.out.println("Validating...");
      XResultData rd = new XResultData(AtsPlugin.getLogger());
      int rowNum = 1; // Header is row 1
      for (ActionData aData : actionDatas) {
         rowNum++;
         if (aData.title.equals("")) rd.logError("Row " + rowNum + "; Invalid Title");
         if (aData.actionableItems.size() == 0)
            rd.logError("Row " + rowNum + ": Must have at least one ActionableItem defined");
         else {
            for (String actionableItemName : aData.actionableItems) {
               try {
                  if (AtsCache.getArtifactsByName(actionableItemName, ActionableItemArtifact.class).size() > 0) {
                     rd.logError("Row " + rowNum + ": Couldn't find actionable item for \"" + actionableItemName + "\"");
                  }
               } catch (Exception ex) {
                  rd.logError("Row " + rowNum + " - " + ex.getLocalizedMessage());
                  OSEELog.logException(AtsPlugin.class, ex, false);
               }
            }
         }
         if (!aData.version.equals("")) {
            try {
               if (AtsCache.getSoleArtifactByName(aData.version, VersionArtifact.class) == null) {
                  rd.logError("Row " + rowNum + ": Can't find single version \"" + aData.version + "\"");
               }
            } catch (Exception ex) {
               rd.logError("Row " + rowNum + " - " + ex.getLocalizedMessage());
               OSEELog.logException(AtsPlugin.class, ex, false);
            }
         }
         // If no assignees, ATS will auto-assign to correct person
         // Else if assignees, confirm that they are valid
         if (aData.assigneeStrs.size() > 0) {
            for (String assignee : aData.assigneeStrs) {
               User user = SkynetAuthentication.getUserByName(assignee, false);
               if (user == null)
                  rd.logError("Row " + rowNum + ": Couldn't retrieve user \"" + assignee + "\"");
               else
                  aData.assignees.add(user);
            }
         }
      }
      if (!rd.toString().equals("")) {
         rd.report("Ats Action Import Errors");
         return false;
      }
      return true;
   }

   public void createArtifactsAndNotify() {
      AtsPlugin.setEmailEnabled(false);
      System.out.println("Creating...");
      Set<TeamWorkFlowArtifact> teamWfs = new HashSet<TeamWorkFlowArtifact>();
      try {
         for (ActionData aData : actionDatas) {
            ActionArtifact actionArt =
                  NewActionJob.createAction(null, aData.title, aData.desc, ChangeType.getChangeType(aData.changeType),
                        AtsPriority.PriorityType.getPriority(aData.priorityStr), aData.userComms, false, null,
                        ActionableItemArtifact.getActionableItems(aData.actionableItems));
            actionArts.add(actionArt);
            if (!aData.version.equals("")) {
               VersionArtifact verArt = AtsCache.getSoleArtifactByName(aData.version, VersionArtifact.class);

               for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts())
                  verArt.addRelation(AtsRelation.TeamWorkflowTargetedForVersion_Workflow, team);
            }
            if (aData.assigneeStrs.size() > 0) {
               for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
                  team.getSmaMgr().getStateMgr().setAssignees(aData.assignees);
               }
            }
            for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
               team.persistAttributesAndRelations();
            }
            teamWfs.addAll(actionArt.getTeamWorkFlowArtifacts());
         }
         AtsPlugin.setEmailEnabled(true);
         if (emailPOCs) {
            for (TeamWorkFlowArtifact team : teamWfs) {
               AtsNotifyUsers.notify(team, AtsNotifyUsers.NotifyType.Assigned);
            }
         }
      } catch (Exception ex) {
         OSEELog.logException(AtsPlugin.class, ex, false);
      } finally {
         AtsPlugin.setEmailEnabled(true);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   public void discoverArtifactAndRelationData(File artifactsFile) throws OseeCoreException, SQLException {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         excelHandler = new ExcelSaxHandler(this, true);
         xmlReader.setContentHandler(excelHandler);
         xmlReader.parse(new InputSource(new InputStreamReader(new FileInputStream(artifactsFile), "UTF-8")));
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processEmptyRow()
    */
   public void processEmptyRow() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processCommentRow(java.lang.String[])
    */
   public void processCommentRow(String[] row) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#reachedEndOfWorksheet()
    */
   public void reachedEndOfWorksheet() {
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#detectedTotalRowCount(int)
    */
   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#foundStartOfWorksheet(java.lang.String)
    */
   public void foundStartOfWorksheet(String sheetName) {
   }

   /**
    * @return the actionArts
    */
   public Set<ActionArtifact> getActionArts() {
      return actionArts;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getFileFilter()
    */
   public FileFilter getFileFilter() {
      return null;
   }
}