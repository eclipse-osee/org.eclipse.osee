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

package org.eclipse.osee.ats.ide.util.Import;

import java.io.InputStreamReader;
import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.JaxAtsTaskFactory;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsClientService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.model.type.AttributeType;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
public class ExcelAtsTaskArtifactExtractor {

   private final AbstractWorkflowArtifact sma;
   private IProgressMonitor monitor;
   private final NewTaskData newTaskData;

   public ExcelAtsTaskArtifactExtractor(TeamWorkFlowArtifact artifact, NewTaskData newTaskData) {
      this.newTaskData = newTaskData;
      this.sma = artifact;
   }

   public void process(URI source) throws Throwable {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         IProgressMonitor monitor = getMonitor();
         if (monitor == null) {
            monitor = new NullProgressMonitor();
         }
         xmlReader.setContentHandler(new ExcelSaxHandler(new InternalRowProcessor(monitor, newTaskData, sma), true));
         xmlReader.parse(new InputSource(new InputStreamReader(source.toURL().openStream(), "UTF-8")));
      } catch (Exception ex) {
         //do nothing
      }
   }

   public void process(URI source, XResultData xResultErrorLog) throws Throwable {
      try {
         XMLReader xmlReader = XMLReaderFactory.createXMLReader();
         IProgressMonitor monitor = getMonitor();
         if (monitor == null) {
            monitor = new NullProgressMonitor();
         }
         xmlReader.setContentHandler(
            new ExcelSaxHandler(new InternalRowProcessor(monitor, newTaskData, sma, xResultErrorLog), true));
         xmlReader.parse(new InputSource(new InputStreamReader(source.toURL().openStream(), "UTF-8")));
      } catch (Exception ex) {
         //do nothing
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

   public String getName() {
      return "Excel Ats Tasks";
   }

   private final static class InternalRowProcessor implements RowProcessor {
      private String[] headerRow;
      private int rowNum;
      private final IProgressMonitor monitor;
      private final AbstractWorkflowArtifact sma;
      private final Date createdDate;
      private final IAtsUser createdBy;
      private final NewTaskData newTaskData;
      private XResultData rowErrorLog;

      protected InternalRowProcessor(IProgressMonitor monitor, NewTaskData newTaskData, AbstractWorkflowArtifact sma) {
         this.monitor = monitor;
         this.newTaskData = newTaskData;
         this.sma = sma;
         createdDate = new Date();
         createdBy = AtsClientService.get().getUserService().getCurrentUser();
         this.rowNum++;
      }

      protected InternalRowProcessor(IProgressMonitor monitor, NewTaskData newTaskData, AbstractWorkflowArtifact sma, XResultData xErrorLog) {
         this(monitor, newTaskData, sma);
         this.rowErrorLog = xErrorLog;
      }

      @Override
      public void processEmptyRow() {
         rowNum++;
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
      public void processRow(String[] row) {

         rowNum++;
         monitor.setTaskName("Processing Row " + rowNum);
         JaxAtsTask task = JaxAtsTaskFactory.get(newTaskData, "", createdBy, createdDate);

         monitor.subTask("Validating...");
         boolean valid = validateRow(row);
         if (!valid) {
            return;
         }

         for (int i = 0; i < row.length; i++) {
            String header = headerRow[i];
            if (header == null) {
               OseeLog.log(Activator.class, Level.SEVERE, "Null header column => " + i);
            } else if (header.equalsIgnoreCase("Created By")) {
               processCreatedBy(row, task, i);
            } else if (header.equalsIgnoreCase("Title")) {
               processTitle(row, task, i);
            } else if (header.equalsIgnoreCase("Assignees")) {
               processAssignees(row, task, i);
            } else if (header.equalsIgnoreCase("Resolution")) {
               processResolution(row, task, i);
            } else if (header.equalsIgnoreCase("Description")) {
               processDescription(row, task, i);
            } else if (header.equalsIgnoreCase("Related to State")) {
               processRelatedToState(row, task, i);
            } else if (header.equalsIgnoreCase("Notes")) {
               processNotes(row, task, i);
            } else if (header.equalsIgnoreCase("Percent Complete")) {
               processPercentComplete(row, i);
            } else if (header.equalsIgnoreCase("Hours Spent")) {
               processHoursSpent(row, i);
            } else if (header.equalsIgnoreCase("Estimated Hours")) {
               processEstimatedHours(row, task, i);
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
                        String value = row[i];
                        if (Strings.isValid(value)) {
                           task.addAttribute(attributeType, value);
                        }
                     }
                  }
               } else {
                  OseeLog.log(Activator.class, Level.SEVERE, "Unhandled column => " + header);
               }
            }
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

      private void processTitle(String[] row, JaxAtsTask taskArt, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            monitor.subTask(String.format("Title \"%s\"", str));
            taskArt.setName(str);
         } else {
            rowErrorLog.errorf("On row: %d, title field cannot be empty.", rowNum);
         }
      }

      private void processNotes(String[] row, JaxAtsTask taskArt, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            taskArt.addAttribute(AtsAttributeTypes.WorkflowNotes, str);
         }
      }

      private void processRelatedToState(String[] row, JaxAtsTask taskArt, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            taskArt.addAttribute(AtsAttributeTypes.RelatedToState, str);
         }
      }

      private void processDescription(String[] row, JaxAtsTask taskArt, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            taskArt.addAttribute(AtsAttributeTypes.Description, str);
         }
      }

      private void processResolution(String[] row, JaxAtsTask taskArt, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            taskArt.addAttribute(AtsAttributeTypes.Resolution, str);
         }
      }

      private void processCreatedBy(String[] row, JaxAtsTask taskArt, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            IAtsUser user = null;
            try {
               user = AtsClientService.get().getUserService().getUserById(str);
            } catch (Exception ex) {
               // do nothing
               rowErrorLog.errorf("On row: %d, the user entered in createdBy does not exist.", rowNum);
               return;
            }
            if (user == null) {
               try {
                  user = AtsClientService.get().getUserService().getUserByName(str);
               } catch (Exception ex) {
                  rowErrorLog.errorf("On row: %d, the user entered in createdBy does not exist.", rowNum);
                  return;
               }
            }
            if (user != null) {
               taskArt.setCreatedByUserId(user.getUserId());
            } else {
               rowErrorLog.errorf("On row: %d, the user entered in createdBy does not exist.", rowNum);
               return;
            }
         } else {
            rowErrorLog.errorf("On row: %d, createdBy field cannot be empty.", rowNum);
         }
      }

      private void processEstimatedHours(String[] row, JaxAtsTask taskArt, int i) {
         String str = row[i];
         double hours = 0;
         if (Strings.isValid(str)) {
            try {
               hours = new Double(str);
            } catch (Exception ex) {
               rowErrorLog.errorf("Invalid Estimated Hours \"%s\" for row %d.", str, rowNum);
            }
            taskArt.addAttribute(AtsAttributeTypes.EstimatedHours, hours);
         }
      }

      private void processHoursSpent(String[] row, int i) {
         String str = row[i];
         double hours = 0;
         if (Strings.isValid(str)) {
            try {
               hours = new Double(str);
            } catch (Exception ex) {
               rowErrorLog.errorf("Invalid Hours Spent \"%s\" for row %d.", str, rowNum);
            }
            sma.getStateMgr().updateMetrics(sma.getStateDefinition(), hours,
               sma.getStateMgr().getPercentComplete(sma.getCurrentStateName()), true,
               AtsClientService.get().getUserService().getCurrentUser());
         }
      }

      private void processPercentComplete(String[] row, int i) {
         String str = row[i];
         Double percent = 0.0;
         if (Strings.isValid(str)) {
            try {
               percent = new Double(str);
               if (percent < 1) {
                  percent = percent * 100;
               }
            } catch (Exception ex) {
               rowErrorLog.errorf("Invalid Percent Complete \"%s\" for row %d.", str, rowNum);
            }
            int percentInt = percent.intValue();
            sma.getStateMgr().updateMetrics(sma.getStateDefinition(), 0, percentInt, true,
               AtsClientService.get().getUserService().getCurrentUser());
         }
      }

      private void processAssignees(String[] row, JaxAtsTask taskArt, int i) {
         for (String userName : row[i].split(";")) {
            userName = userName.replaceAll("^\\s+", "");
            userName = userName.replaceAll("\\+$", "");
            IAtsUser user = null;
            if (!Strings.isValid(userName)) {
               user = AtsClientService.get().getUserService().getCurrentUser();
            } else {
               try {
                  user = AtsClientService.get().getUserService().getUserByName(userName);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            if (user == null) {
               OseeLog.logf(Activator.class, Level.SEVERE, "Invalid Assignee \"%s\" for row %d.  Using current user.",
                  userName, rowNum);
               user = AtsClientService.get().getUserService().getCurrentUser();
               rowErrorLog.errorf("Invalid Assignee \"%s\" for row %d.  Using current user.", userName, rowNum);
            }
            taskArt.addAssigneeUserIds(user.getUserId());
         }
      }

   }
}