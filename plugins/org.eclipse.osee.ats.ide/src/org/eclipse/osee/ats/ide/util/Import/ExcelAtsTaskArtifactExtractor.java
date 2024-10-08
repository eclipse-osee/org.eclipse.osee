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
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Donald G. Dunne
 */
@SuppressWarnings("deprecation")
public class ExcelAtsTaskArtifactExtractor {

   private final AbstractWorkflowArtifact sma;
   private IProgressMonitor monitor;
   private final NewTaskData newTaskData;

   public ExcelAtsTaskArtifactExtractor(TeamWorkFlowArtifact artifact, NewTaskData newTaskData) {
      this.newTaskData = newTaskData;
      this.sma = artifact;
   }

   public void process(URI source, XResultData rd) throws Throwable {
      XMLReader xmlReader = XMLReaderFactory.createXMLReader();
      try (InputStreamReader inputStream = new InputStreamReader(source.toURL().openStream(), "UTF-8");) {
         IProgressMonitor monitor = getMonitor();
         if (monitor == null) {
            monitor = new NullProgressMonitor();
         }
         xmlReader.setContentHandler(
            new ExcelSaxHandler(new InternalRowProcessor(monitor, newTaskData, sma, rd), true));
         xmlReader.parse(new InputSource(inputStream));
      } catch (Exception ex) {
         rd.errorf("Exception processing Excel input %s\n", Lib.exceptionToString(ex));
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
      private final Date createdDate;
      private final AtsUser createdBy;
      private final NewTaskData newTaskData;
      private XResultData rd;
      private Integer badColumn;
      private boolean skipRestOfRows;

      protected InternalRowProcessor(IProgressMonitor monitor, NewTaskData newTaskData, AbstractWorkflowArtifact sma) {
         this.monitor = monitor;
         this.newTaskData = newTaskData;
         createdDate = new Date();
         createdBy = AtsApiService.get().getUserService().getCurrentUser();
      }

      protected InternalRowProcessor(IProgressMonitor monitor, NewTaskData newTaskData, AbstractWorkflowArtifact sma, XResultData rd) {
         this(monitor, newTaskData, sma);
         this.rd = rd;
         this.rd.setLogToSysErr(true);
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
         rowNum++;
      }

      @Override
      public void processRow(String[] row) {
         if (skipRestOfRows) {
            return;
         }
         rowNum++;
         monitor.setTaskName("Processing Row " + rowNum);
         JaxAtsTask jTask = JaxAtsTask.create(newTaskData, "", createdBy, createdDate);

         monitor.subTask("Validating...");
         if (!"Title".equals(headerRow[0])) {
            rd.errorf("Title column must be first\n", rowNum);
            skipRestOfRows = true;
            return;
         }

         for (int i = 0; i < row.length; i++) {
            if (badColumn != null && i >= badColumn) {
               break;
            }
            String header = headerRow[i];
            if (header == null) {
               badColumn = i;
               // if header is null, rest of spreadsheet is N/A
               break;
            } else if (header.equalsIgnoreCase("Created By")) {
               if (validRow(jTask)) {
                  processCreatedBy(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Title")) {
               boolean validTitle = processTitle(row, jTask, i);
               if (!validTitle) {
                  newTaskData.getTasks().remove(jTask);
                  skipRestOfRows = true;
               }
            } else if (header.equalsIgnoreCase("Assignees")) {
               if (validRow(jTask)) {
                  processAssignees(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Resolution")) {
               if (validRow(jTask)) {
                  processResolution(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Description")) {
               if (validRow(jTask)) {
                  processDescription(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Related to State")) {
               if (validRow(jTask)) {
                  processRelatedToState(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Notes")) {
               if (validRow(jTask)) {
                  processNotes(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Percent Complete")) {
               if (validRow(jTask)) {
                  processPercentComplete(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Hours Spent")) {
               if (validRow(jTask)) {
                  processHoursSpent(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Estimated Hours")) {
               if (validRow(jTask)) {
                  processEstimatedHours(row, jTask, i);
               }
            } else {
               if (validRow(jTask)) {
                  String attrTypeName = header;
                  if (Strings.isValid(attrTypeName)) {
                     AttributeTypeToken attributeType = AttributeTypeManager.getType(attrTypeName);
                     if (attributeType == null) {
                        rd.error("Invalid Attribute Type Name => " + header);
                     } else {
                        if (!AtsArtifactTypes.Task.isValidAttributeType(attributeType)) {
                           rd.error("Invalid Attribute Type for Task => " + header);
                        } else {
                           String value = row[i];
                           if (Strings.isValid(value)) {
                              jTask.addAttribute(attributeType, value);
                           }
                        }
                     }
                  } else {
                     rd.error("Unhandled column => " + header);
                  }
               }
            }
         }
      }

      /**
       * Use title cell to validate row. This enables extractor to ignore remainder of file in case it has corrupted
       * cells elsewhere in spreadsheet.
       */
      private boolean validRow(JaxAtsTask task) {
         boolean valid = Strings.isValid(task.getName());
         if (!valid) {
            newTaskData.getTasks().remove(task);
            skipRestOfRows = true;
         }
         return valid;
      }

      private boolean processTitle(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            monitor.subTask(String.format("Title \"%s\"", str));
            if (newTaskData.isFixTitles()) {
               if (!Strings.isPrintable(str)) {
                  str = Strings.removeNonPrintableCharacters(str);
                  rd.logf("On row: %d, removed non-printable title characters\n", rowNum);
               }
               if (str.contains("[\r\n]+")) {
                  str = jTask.getName().replaceAll("[\r\n]+", "");
                  rd.logf("On row: %d, removed title newlines\n", rowNum);
               }
               if (str.length() > 250) {
                  String desc = row[i];
                  str = Strings.truncate(str, 250, true);
                  jTask.setDescription(desc);
                  rd.logf("On row: %d, truncated title and put full in description\n", rowNum);
               }
            } else {
               if (!Strings.isPrintable(str)) {
                  rd.errorf("On row: %d, title field has non-printable characters\n", rowNum);
                  return false;
               }
               if (str.contains("[\r\n]+")) {
                  rd.errorf("On row: %d, title field cannot have new line characters\n", rowNum);
                  return false;
               }
               if (str.length() > 250) {
                  rd.errorf("On row: %d, title field cannot be longer than 250\n", rowNum);
                  return false;
               }
            }
            if (Strings.isInValid(str)) {
               rd.errorf("On row: %d, title is invalid\n", rowNum);
            }
            jTask.setName(str);
            return true;
         } else {
            rd.errorf("On row: %d, title field cannot be empty\n", rowNum);
            return false;
         }
      }

      private void processNotes(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            jTask.addAttribute(AtsAttributeTypes.WorkflowNotes, str);
         }
      }

      private void processRelatedToState(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            jTask.addAttribute(AtsAttributeTypes.RelatedToState, str);
         }
      }

      private void processDescription(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         if (Strings.isInValid(str)) {
            rd.errorf("Description column can not be blank for row %s\n", rowNum);
            return;
         }
         jTask.addAttribute(AtsAttributeTypes.Description, str);
      }

      private void processResolution(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            jTask.addAttribute(AtsAttributeTypes.Resolution, str);
         }
      }

      private void processCreatedBy(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            AtsUser user = null;
            try {
               user = AtsApiService.get().getUserService().getUserByUserId(str);
            } catch (Exception ex) {
               // do nothing
               rd.errorf("On row: %d, the user entered in createdBy does not exist\n", rowNum);
               return;
            }
            if (user == null) {
               try {
                  user = AtsApiService.get().getUserService().getUserByName(str);
               } catch (Exception ex) {
                  rd.errorf("On row: %d, the user entered in createdBy does not exist\n", rowNum);
                  return;
               }
            }
            if (user != null) {
               jTask.setCreatedByUserId(user.getUserId());
            } else {
               rd.errorf("On row: %d, the user entered in createdBy does not exist\n", rowNum);
               return;
            }
         } else {
            rd.errorf("On row: %d, createdBy field cannot be empty\n", rowNum);
         }
      }

      private void processEstimatedHours(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         double hours = 0;
         if (Strings.isValid(str)) {
            try {
               hours = Double.valueOf(str);
            } catch (Exception ex) {
               rd.errorf("Invalid Estimated Hours \"%s\" for row %d\n", str, rowNum);
            }
            jTask.addAttribute(AtsAttributeTypes.EstimatedHours, hours);
         }
      }

      private void processHoursSpent(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         double hours = 0;
         if (Strings.isValid(str)) {
            try {
               hours = Double.valueOf(str);
            } catch (Exception ex) {
               rd.errorf("Invalid Hours Spent \"%s\" for row %d\n", str, rowNum);
            }
            jTask.setHoursSpent(hours);
         }
      }

      private void processPercentComplete(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         Double percent = 0.0;
         if (Strings.isValid(str)) {
            try {
               percent = Double.valueOf(str);
               if (percent < 1) {
                  percent = percent * 100;
               }
            } catch (Exception ex) {
               rd.errorf("Invalid Percent Complete \"%s\" for row %d\n", str, rowNum);
            }
            jTask.addAttribute(AtsAttributeTypes.PercentComplete, percent);
         }
      }

      private void processAssignees(String[] row, JaxAtsTask jTask, int i) {
         if (row[i] == null) {
            rd.errorf("Assignee column can not be blank for row %s\n", rowNum);
            return;
         }
         for (String userName : row[i].split(";")) {
            userName = userName.replaceAll("^\\s+", "");
            userName = userName.replaceAll("\\+$", "");
            AtsUser user = null;
            if (!Strings.isValid(userName)) {
               user = AtsApiService.get().getUserService().getCurrentUser();
            } else {
               try {
                  user = AtsApiService.get().getUserService().getUserByName(userName);
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, ex);
               }
            }
            if (user == null) {
               user = AtsApiService.get().getUserService().getCurrentUser();
               rd.errorf("Invalid Assignee \"%s\" for row %d.  Using current user\n", userName, rowNum);
            }
            jTask.addAssigneeUserIds(user.getUserId());
         }
      }

   }
}