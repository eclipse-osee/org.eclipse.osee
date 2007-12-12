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
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.artifact.ATSAttributes;
import org.eclipse.osee.ats.artifact.StateMachineArtifact;
import org.eclipse.osee.ats.artifact.TaskArtifact;
import org.eclipse.osee.ats.editor.SMAManager;
import org.eclipse.osee.ats.util.NotifyUsersJob;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.ui.skynet.Import.AbstractArtifactExtractor;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class ExcelAtsTaskArtifactExtractor extends AbstractArtifactExtractor implements RowProcessor {
   private static final SkynetAuthentication skynetAuth = SkynetAuthentication.getInstance();
   private static final String description = "Extract each row as a task";
   private ExcelSaxHandler excelHandler;
   private String[] headerRow;
   private StateMachineArtifact sma;
   private IProgressMonitor monitor;
   private int rowNum;
   private final boolean emailPOCs;
   private static SkynetAuthentication skyAuth = SkynetAuthentication.getInstance();
   private SMAManager smaMgr;

   public static String getDescription() {
      return description;
   }

   public ExcelAtsTaskArtifactExtractor(String hrid, Branch branch, boolean emailPOCs) throws SQLException, IllegalArgumentException {
      super(branch);
      this.emailPOCs = emailPOCs;
      Collection<Artifact> arts =
            ArtifactPersistenceManager.getInstance().getArtifactsFromHrid(hrid,
                  BranchPersistenceManager.getInstance().getAtsBranch());
      if (arts.size() == 0) throw new IllegalArgumentException("Can't find artifact associated with " + hrid);
      if (arts.size() > 1) throw new IllegalArgumentException("Found two artifacts for HRID " + hrid);
      if (!(arts.iterator().next() instanceof StateMachineArtifact)) throw new IllegalArgumentException(
            "Artifact must be StateMachineArtifact");
      sma = (StateMachineArtifact) arts.iterator().next();
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
         TaskArtifact taskArt = smaMgr.getTaskMgr().createNewTask("", false);

         monitor.subTask("Validating...");
         boolean fullRow = false;
         for (int i = 0; i < row.length; i++)
            if (row[i] != null && !row[i].equals("")) {
               fullRow = true;
               break;
            }
         if (!fullRow) {
            OSEELog.logSevere(AtsPlugin.class, "Empty Row Found => " + rowNum + " skipping...", false);
            return;
         }

         AtsPlugin.setEmailEnabled(false);
         for (int i = 0; i < row.length; i++) {
            if (headerRow[i] == null) {
               OSEELog.logSevere(AtsPlugin.class, "Null header column => " + i, false);
            } else if (headerRow[i].equalsIgnoreCase("Originator")) {
               String userName = row[i];
               User u = null;
               if (userName == null || userName.equals(""))
                  u = skynetAuth.getAuthenticatedUser();
               else
                  u = skyAuth.getUserByName(userName, false);
               if (u == null) OSEELog.logSevere(AtsPlugin.class, String.format(
                     "Invalid originator \"%s\" for row %d\nSetting to current user.", userName, rowNum), false);
               sma.getLog().setOriginator(u);
            } else if (headerRow[i].equalsIgnoreCase("Assignee")) {
               String userName = row[i];
               User u = null;
               if (userName == null || userName.equals(""))
                  u = skynetAuth.getAuthenticatedUser();
               else
                  u = skyAuth.getUserByName(userName, false);
               if (u == null) throw new IllegalArgumentException(String.format("Invalid Assignee \"%s\" for row %d",
                     userName, rowNum));
               taskArt.getCurrentState().setAssignee(u);
            } else if (headerRow[i].equalsIgnoreCase(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName())) {
               String str = row[i];
               if (str != null && !str.equals("")) {
                  taskArt.setSoleAttributeValue(ATSAttributes.RESOLUTION_ATTRIBUTE.getStoreName(), str);
               }
            } else if (headerRow[i].equalsIgnoreCase(ATSAttributes.TITLE_ATTRIBUTE.getStoreName())) {
               String str = row[i];
               if (str != null && !str.equals("")) {
                  if (monitor != null) {
                     monitor.subTask(String.format("Title \"%s\"", str));
                  }
                  taskArt.setDescriptiveName(str);
               }
            } else if (headerRow[i].equalsIgnoreCase(ATSAttributes.PERCENT_COMPLETE_ATTRIBUTE.getStoreName())) {
               String str = row[i];
               Double percent;
               if (str != null && !str.equals("")) {
                  try {
                     percent = new Double(str);
                     percent = percent * 100;
                  } catch (Exception ex) {
                     throw new IllegalArgumentException(String.format("Invalid Percent Complete \"%s\" for row %d",
                           str, rowNum));
                  }
                  int percentInt = percent.intValue();
                  taskArt.getCurrentStateDam().setPercentComplete(percentInt);
               }
            } else if (headerRow[i].equalsIgnoreCase(ATSAttributes.HOURS_SPENT_ATTRIBUTE.getStoreName())) {
               String str = row[i];
               double hours = 0;
               if (str != null && !str.equals("")) {
                  try {
                     hours = new Double(str);
                  } catch (Exception ex) {
                     throw new IllegalArgumentException(String.format("Invalid Hours Spent \"%s\" for row %d", str,
                           rowNum));
                  }
                  taskArt.getCurrentStateDam().setHoursSpent(hours);
               }
            } else if (headerRow[i].equalsIgnoreCase("group")) {
               System.out.println("groups not handled yet");
            } else {
               OSEELog.logSevere(AtsPlugin.class, "Unhandled column => " + headerRow[i], false);
            }
         }
         AtsPlugin.setEmailEnabled(true);

         if (taskArt.isCompleted()) taskArt.transitionToCompleted(false);
         taskArt.persist(true);
         if (emailPOCs && !taskArt.isCompleted() && !taskArt.isCancelled()) {
            NotifyUsersJob job = new NotifyUsersJob(sma, NotifyUsersJob.NotifyType.Assignee);
            job.setPriority(Job.SHORT);
            job.schedule();
         }
      } catch (SQLException ex) {
         OSEELog.logException(AtsPlugin.class, ex, true);
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.define.artifact.Import.ArtifactExtractor#discoverArtifactAndRelationData(java.io.File)
    */
   public void discoverArtifactAndRelationData(File artifactsFile) throws Exception {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      excelHandler = new ExcelSaxHandler(this, true);
      xmlReader.setContentHandler(excelHandler);
      xmlReader.parse(new InputSource(new InputStreamReader(new FileInputStream(artifactsFile), "UTF-8")));
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
}