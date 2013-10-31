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
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.notify.AtsNotifyType;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.core.AtsCore;
import org.eclipse.osee.ats.core.client.notify.AtsNotificationManager;
import org.eclipse.osee.ats.core.client.task.AbstractTaskableArtifact;
import org.eclipse.osee.ats.core.client.task.TaskArtifact;
import org.eclipse.osee.ats.core.client.task.TaskManager;
import org.eclipse.osee.ats.core.client.team.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.core.client.util.AtsUtilCore;
import org.eclipse.osee.ats.core.client.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.Activator;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.Result;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.ui.plugin.util.AWorkbench;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class ExcelAtsTaskArtifactExtractor {

   private final AbstractWorkflowArtifact sma;
   private final boolean emailPOCs;
   private final IAtsChangeSet changes;

   private IProgressMonitor monitor;

   public ExcelAtsTaskArtifactExtractor(TeamWorkFlowArtifact artifact, boolean emailPOCs, IAtsChangeSet changes) {
      this.emailPOCs = emailPOCs;
      this.changes = changes;
      this.sma = artifact;
   }

   public void process(URI source) throws OseeCoreException {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         IProgressMonitor monitor = getMonitor();
         if (monitor == null) {
            monitor = new NullProgressMonitor();
         }
         xmlReader.setContentHandler(new ExcelSaxHandler(new InternalRowProcessor(monitor, changes, sma, emailPOCs),
            true));
         xmlReader.parse(new InputSource(new InputStreamReader(source.toURL().openStream(), "UTF-8")));
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
   }

   public String getDescription() {
      return "Extract each row as a task";
   }

   public IProgressMonitor getMonitor() {
      return monitor;
   }

   public void setMonitor(IProgressMonitor monitor) {
      this.monitor = monitor;
   }

   public FileFilter getFileFilter() {
      return null;
   }

   public String getName() {
      return "Excel Ats Tasks";
   }

   private final static class InternalRowProcessor implements RowProcessor {
      private String[] headerRow;
      private int rowNum;
      private final IProgressMonitor monitor;
      private final AbstractWorkflowArtifact sma;
      private final IAtsChangeSet changes;
      private final boolean emailPOCs;
      private final Date createdDate;
      private final IAtsUser createdBy;

      protected InternalRowProcessor(IProgressMonitor monitor, IAtsChangeSet changes, AbstractWorkflowArtifact sma, boolean emailPOCs) throws OseeCoreException {
         this.monitor = monitor;
         this.changes = changes;
         this.emailPOCs = emailPOCs;
         this.sma = sma;
         createdDate = new Date();
         createdBy = AtsClientService.get().getUserAdmin().getCurrentUser();
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
      public void processRow(String[] row) throws OseeCoreException {
         rowNum++;
         monitor.setTaskName("Processing Row " + rowNum);
         TaskArtifact taskArt = ((AbstractTaskableArtifact) sma).createNewTask("", createdDate, createdBy, null);

         monitor.subTask("Validating...");
         boolean valid = validateRow(row);
         if (!valid) {
            return;
         }
         AtsUtilCore.setEmailEnabled(false);
         for (int i = 0; i < row.length; i++) {
            if (headerRow[i] == null) {
               OseeLog.log(Activator.class, Level.SEVERE, "Null header column => " + i);
            } else if (headerRow[i].equalsIgnoreCase("Originator")) {
               processOriginator(row, taskArt, i);
            } else if (headerRow[i].equalsIgnoreCase("Assignees")) {
               processAssignees(row, taskArt, i);
            } else if (headerRow[i].equalsIgnoreCase("Resolution")) {
               processResolution(row, taskArt, i);
            } else if (headerRow[i].equalsIgnoreCase("Description")) {
               processDescription(row, taskArt, i);
            } else if (headerRow[i].equalsIgnoreCase("Related to State")) {
               processRelatedToState(row, taskArt, i);
            } else if (headerRow[i].equalsIgnoreCase("Notes")) {
               processNotes(row, taskArt, i);
            } else if (headerRow[i].equalsIgnoreCase("Title")) {
               processTitle(row, taskArt, i);
            } else if (headerRow[i].equalsIgnoreCase("Percent Complete")) {
               processPercentComplete(row, i);
            } else if (headerRow[i].equalsIgnoreCase("Hours Spent")) {
               processHoursSpent(row, i);
            } else if (headerRow[i].equalsIgnoreCase("Estimated Hours")) {
               processEstimatedHours(row, taskArt, i);
            } else {
               OseeLog.log(Activator.class, Level.SEVERE, "Unhandled column => " + headerRow[i]);
            }
         }
         AtsUtilCore.setEmailEnabled(true);
         if (taskArt.isCompleted()) {
            Result result = TaskManager.transitionToCompleted(taskArt, 0.0, 0, changes);
            if (result.isFalse()) {
               AWorkbench.popup(result);
            }
         }
         // always persist
         changes.add(taskArt);
         if (emailPOCs && !taskArt.isCompleted() && !taskArt.isCancelled()) {
            AtsNotificationManager.notify(sma, AtsNotifyType.Assigned);
         }
      }

      private boolean validateRow(String[] row) {
         boolean fullRow = false;
         for (int i = 0; i < row.length; i++) {
            if (Strings.isValid(row[i])) {
               fullRow = true;
               break;
            }
         }
         if (!fullRow) {
            OseeLog.log(Activator.class, Level.SEVERE, "Empty Row Found => " + rowNum + " skipping...");
         }
         return fullRow;
      }

      private void processTitle(String[] row, TaskArtifact taskArt, int i) throws OseeCoreException {
         String str = row[i];
         if (Strings.isValid(str)) {
            monitor.subTask(String.format("Title \"%s\"", str));
            taskArt.setName(str);
         }
      }

      private void processNotes(String[] row, TaskArtifact taskArt, int i) throws OseeCoreException {
         String str = row[i];
         if (Strings.isValid(str)) {
            taskArt.setSoleAttributeValue(AtsAttributeTypes.SmaNote, str);
         }
      }

      private void processRelatedToState(String[] row, TaskArtifact taskArt, int i) throws OseeCoreException {
         String str = row[i];
         if (Strings.isValid(str)) {
            taskArt.setSoleAttributeValue(AtsAttributeTypes.RelatedToState, str);
         }
      }

      private void processDescription(String[] row, TaskArtifact taskArt, int i) throws OseeCoreException {
         String str = row[i];
         if (Strings.isValid(str)) {
            taskArt.setSoleAttributeValue(AtsAttributeTypes.Description, str);
         }
      }

      private void processResolution(String[] row, TaskArtifact taskArt, int i) throws OseeCoreException {
         String str = row[i];
         if (Strings.isValid(str)) {
            taskArt.setSoleAttributeValue(AtsAttributeTypes.Resolution, str);
         }
      }

      private void processEstimatedHours(String[] row, TaskArtifact taskArt, int i) throws OseeArgumentException, OseeCoreException {
         String str = row[i];
         double hours = 0;
         if (Strings.isValid(str)) {
            try {
               hours = new Double(str);
            } catch (Exception ex) {
               throw new OseeArgumentException("Invalid Estimated Hours \"%s\" for row %d", str, rowNum);
            }
            taskArt.setSoleAttributeValue(AtsAttributeTypes.EstimatedHours, hours);
         }
      }

      private void processHoursSpent(String[] row, int i) throws OseeArgumentException, OseeCoreException {
         String str = row[i];
         double hours = 0;
         if (Strings.isValid(str)) {
            try {
               hours = new Double(str);
            } catch (Exception ex) {
               throw new OseeArgumentException("Invalid Hours Spent \"%s\" for row %d", str, rowNum);
            }
            sma.getStateMgr().updateMetrics(sma.getStateDefinition(), hours,
               sma.getStateMgr().getPercentComplete(sma.getCurrentStateName()), true);
            AtsCore.getLogFactory().writeToStore(sma);
         }
      }

      private void processPercentComplete(String[] row, int i) throws OseeArgumentException, OseeCoreException {
         String str = row[i];
         Double percent;
         if (Strings.isValid(str)) {
            try {
               percent = new Double(str);
               if (percent < 1) {
                  percent = percent * 100;
               }
            } catch (Exception ex) {
               throw new OseeArgumentException("Invalid Percent Complete \"%s\" for row %d", str, rowNum);
            }
            int percentInt = percent.intValue();
            sma.getStateMgr().updateMetrics(sma.getStateDefinition(), 0, percentInt, true);
            AtsCore.getLogFactory().writeToStore(sma);
         }
      }

      private void processAssignees(String[] row, TaskArtifact taskArt, int i) throws OseeCoreException {
         Set<IAtsUser> assignees = new HashSet<IAtsUser>();
         for (String userName : row[i].split(";")) {
            userName = userName.replaceAll("^\\s+", "");
            userName = userName.replaceAll("\\+$", "");
            IAtsUser user = null;
            if (!Strings.isValid(userName)) {
               user = AtsClientService.get().getUserAdmin().getCurrentUser();
            } else {
               try {
                  user = AtsClientService.get().getUserAdmin().getUserByName(userName);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            if (user == null) {
               OseeLog.logf(Activator.class, Level.SEVERE, "Invalid Assignee \"%s\" for row %d.  Using current user.",
                  userName, rowNum);
               user = AtsClientService.get().getUserAdmin().getCurrentUser();
            }
            assignees.add(user);
         }
         taskArt.getStateMgr().setAssignees(assignees);
      }

      private void processOriginator(String[] row, TaskArtifact taskArt, int i) throws OseeCoreException {
         String userName = row[i];
         IAtsUser user = null;
         if (!Strings.isValid(userName)) {
            user = AtsClientService.get().getUserAdmin().getCurrentUser();
         } else {
            user = AtsClientService.get().getUserAdmin().getUserByName(userName);
         }
         if (user == null) {
            OseeLog.logf(Activator.class, Level.SEVERE,
               "Invalid Originator \"%s\" for row %d\nSetting to current user.", userName, rowNum);
         }
         taskArt.internalSetCreatedBy(user);
      }
   }
}