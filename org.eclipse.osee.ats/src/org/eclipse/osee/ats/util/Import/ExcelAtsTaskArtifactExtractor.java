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
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.editor.SMAManager.TransitionOption;
import org.eclipse.osee.ats.util.AtsNotifyUsers;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.ui.skynet.Import.AbstractArtifactExtractor;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class ExcelAtsTaskArtifactExtractor extends AbstractArtifactExtractor implements RowProcessor {
   private ExcelSaxHandler excelHandler;
   private String[] headerRow;
   private final StateMachineArtifact sma;
   private IProgressMonitor monitor;
   private int rowNum;
   private final boolean emailPOCs;
   private final SMAManager smaMgr;
   private final SkynetTransaction transaction;

   public String getDescription() {
      return "Extract each row as a task";
   }

   public ExcelAtsTaskArtifactExtractor(TeamWorkFlowArtifact artifact, boolean emailPOCs, SkynetTransaction transaction) {
      this.emailPOCs = emailPOCs;
      this.transaction = transaction;
      if (!(artifact instanceof StateMachineArtifact)) {
         throw new IllegalArgumentException("Artifact must be StateMachineArtifact");
      }

      sma = artifact;
      smaMgr = new SMAManager(sma);
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.RowProcessor#processHeaderRow(java.lang.String[])
    */
   public void processHeaderRow(String[] headerRow) {
      this.headerRow = headerRow.clone();
   }

   /**
    * import Artifacts
    * 
    * @param row
    */
   public void processRow(String[] row) {
      try {
         rowNum++;
         monitor.setTaskName("Processing Row " + rowNum);
         TaskArtifact taskArt = smaMgr.getTaskMgr().createNewTask("");

         monitor.subTask("Validating...");
         boolean fullRow = false;
         for (int i = 0; i < row.length; i++)
            if (row[i] != null && !row[i].equals("")) {
               fullRow = true;
               break;
            }
         if (!fullRow) {
            OseeLog.log(AtsPlugin.class, Level.SEVERE, "Empty Row Found => " + rowNum + " skipping...");
            return;
         }
         AtsPlugin.setEmailEnabled(false);
         for (int i = 0; i < row.length; i++) {
            if (headerRow[i] == null) {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, "Null header column => " + i);
            } else if (headerRow[i].equalsIgnoreCase("Originator")) {
               String userName = row[i];
               User u = null;
               if (userName == null || userName.equals(""))
                  u = UserManager.getUser();
               else
                  u = UserManager.getUserByName(userName);
               if (u == null) OseeLog.log(AtsPlugin.class, Level.SEVERE, String.format(
                     "Invalid Originator \"%s\" for row %d\nSetting to current user.", userName, rowNum));
               taskArt.getSmaMgr().getLog().setOriginator(u);
            } else if (headerRow[i].equalsIgnoreCase("Assignees")) {
               Set<User> assignees = new HashSet<User>();
               for (String userName : row[i].split(";")) {
                  userName = userName.replaceAll("^\\s+", "");
                  userName = userName.replaceAll("\\+$", "");
                  User user = null;
                  if (userName == null || userName.equals(""))
                     user = UserManager.getUser();
                  else {
                     try {
                        user = UserManager.getUserByName(userName);
                     } catch (OseeCoreException ex) {
                        OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
                     }
                  }
                  if (user == null) {
                     OseeLog.log(AtsPlugin.class, Level.SEVERE, String.format(
                           "Invalid Assignee \"%s\" for row %d.  Using current user.", userName, rowNum));
                     user = UserManager.getUser();
                  }
                  assignees.add(user);
               }
               taskArt.getSmaMgr().getStateMgr().setAssignees(assignees);
            } else if (headerRow[i].equalsIgnoreCase("Resolution")) {
               String str = row[i];
               if (str != null && !str.equals("")) {
                  taskArt.setSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), str);
               }
            } else if (headerRow[i].equalsIgnoreCase("Description")) {
               String str = row[i];
               if (str != null && !str.equals("")) {
                  taskArt.setSoleAttributeValue(ATSAttributes.DESCRIPTION_ATTRIBUTE.getStoreName(), str);
               }
            } else if (headerRow[i].equalsIgnoreCase("Related to State")) {
               String str = row[i];
               if (str != null && !str.equals("")) {
                  taskArt.setSoleAttributeValue(ATSAttributes.RELATED_TO_STATE_ATTRIBUTE.getStoreName(), str);
               }
            } else if (headerRow[i].equalsIgnoreCase("Notes")) {
               String str = row[i];
               if (str != null && !str.equals("")) {
                  taskArt.setSoleAttributeValue(ATSAttributes.SMA_NOTE_ATTRIBUTE.getStoreName(), str);
               }
            } else if (headerRow[i].equalsIgnoreCase("Title")) {
               String str = row[i];
               if (str != null && !str.equals("")) {
                  if (monitor != null) {
                     monitor.subTask(String.format("Title \"%s\"", str));
                  }
                  taskArt.setDescriptiveName(str);
               }
            } else if (headerRow[i].equalsIgnoreCase("Percent Complete")) {
               String str = row[i];
               Double percent;
               if (str != null && !str.equals("")) {
                  try {
                     percent = new Double(str);
                     if (percent < 1) percent = percent * 100;
                  } catch (Exception ex) {
                     throw new IllegalArgumentException(String.format("Invalid Percent Complete \"%s\" for row %d",
                           str, rowNum));
                  }
                  int percentInt = percent.intValue();
                  smaMgr.getStateMgr().updateMetrics(0, percentInt, true);
               }
            } else if (headerRow[i].equalsIgnoreCase("Hours Spent")) {
               String str = row[i];
               double hours = 0;
               if (str != null && !str.equals("")) {
                  try {
                     hours = new Double(str);
                  } catch (Exception ex) {
                     throw new IllegalArgumentException(String.format("Invalid Hours Spent \"%s\" for row %d", str,
                           rowNum));
                  }
                  smaMgr.getStateMgr().updateMetrics(hours, smaMgr.getStateMgr().getPercentComplete(), true);
               }
            } else if (headerRow[i].equalsIgnoreCase("Estimated Hours")) {
               String str = row[i];
               double hours = 0;
               if (str != null && !str.equals("")) {
                  try {
                     hours = new Double(str);
                  } catch (Exception ex) {
                     throw new IllegalArgumentException(String.format("Invalid Estimated Hours \"%s\" for row %d", str,
                           rowNum));
                  }
                  taskArt.setSoleAttributeValue(ATSAttributes.ESTIMATED_HOURS_ATTRIBUTE.getStoreName(), hours);
               }
            } else {
               OseeLog.log(AtsPlugin.class, Level.SEVERE, "Unhandled column => " + headerRow[i]);
            }
         }
         AtsPlugin.setEmailEnabled(true);
         if (taskArt.isCompleted()) {
            taskArt.transitionToCompleted(0, transaction, TransitionOption.None);
         }
         // always persist
         taskArt.persistAttributesAndRelations(transaction);
         if (emailPOCs && !taskArt.isCompleted() && !taskArt.isCancelled()) {
            AtsNotifyUsers.notify(sma, AtsNotifyUsers.NotifyType.Assigned);
         }
      } catch (Exception ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   public void discoverArtifactAndRelationData(File artifactsFile, Branch branch) throws OseeCoreException {
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

   public IProgressMonitor getMonitor() {
      return monitor;
   }

   public void setMonitor(IProgressMonitor monitor) {
      this.monitor = monitor;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getFileFilter()
    */
   public FileFilter getFileFilter() {
      return null;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#getName()
    */
   @Override
   public String getName() {
      return "Excel Ats Tasks";
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor#usesTypeList()
    */
   @Override
   public boolean usesTypeList() {
      return false;
   }
}