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
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.task.JaxAtsTask;
import org.eclipse.osee.ats.api.task.NewTaskData;
import org.eclipse.osee.ats.api.user.AtsUser;
import org.eclipse.osee.ats.api.workdef.model.WorkDefinition;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.ide.internal.Activator;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
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
      CharsetDecoder decoder = StandardCharsets.UTF_8.newDecoder();
      decoder.onMalformedInput(CodingErrorAction.REPLACE);
      decoder.onUnmappableCharacter(CodingErrorAction.REPLACE);
      try (InputStreamReader inputStream = new InputStreamReader(source.toURL().openStream(), decoder);) {
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
      private final java.util.Set<String> reportedInvalidAttrTypes = new java.util.HashSet<>();
      private final ArtifactTypeToken taskArtifactType;

      protected InternalRowProcessor(IProgressMonitor monitor, NewTaskData newTaskData, AbstractWorkflowArtifact sma) {
         this.monitor = monitor;
         this.newTaskData = newTaskData;
         createdDate = new Date();
         createdBy = AtsApiService.get().getUserService().getCurrentUser();
         taskArtifactType = resolveTaskArtifactType(sma);
      }

      protected InternalRowProcessor(IProgressMonitor monitor, NewTaskData newTaskData, AbstractWorkflowArtifact sma, XResultData rd) {
         this(monitor, newTaskData, sma);
         this.rd = rd;
         this.rd.setLogToSysErr(true);
      }

      private ArtifactTypeToken resolveTaskArtifactType(AbstractWorkflowArtifact sma) {
         try {
            IAtsTeamWorkflow teamWf = (IAtsTeamWorkflow) sma;
            WorkDefinition workDef =
               AtsApiService.get().getWorkDefinitionService().computedWorkDefinitionForTaskNotYetCreated(teamWf);
            if (workDef != null && workDef.getArtType() != null && workDef.getArtType().isValid()) {
               return workDef.getArtType();
            }
         } catch (Exception ex) {
            // fall through to default
         }
         return AtsArtifactTypes.Task;
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
         rd.log("");
         rd.log("----------------------------------------");
         rd.logf("All Valid Attribute Types for Task Artifact [%s]\n", taskArtifactType.toStringWithId());
         rd.log("----------------------------------------");
         java.util.List<AttributeTypeToken> sortedTypes =
            new java.util.ArrayList<>(taskArtifactType.getValidAttributeTypes());
         sortedTypes.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
         for (AttributeTypeToken attrType : sortedTypes) {
            rd.logf("  %s\n", attrType.toStringWithId());
         }
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
         validateHeaderColumns();
      }

      /**
       * Validate header columns upfront to report invalid attribute types once under Row 1 (Header).
       */
      private void validateHeaderColumns() {
         java.util.List<String> knownColumns = java.util.Arrays.asList("Title", "Created By", "Assignees",
            "Resolution", "Description", "Related to State", "Notes", "Percent Complete", "Hours Spent",
            "Estimated Hours");
         boolean hasHeaderErrors = false;
         for (int i = 0; i < headerRow.length; i++) {
            String header = headerRow[i];
            if (header == null) {
               break;
            }
            // Skip known built-in columns
            boolean isKnown = false;
            for (String known : knownColumns) {
               if (known.equalsIgnoreCase(header)) {
                  isKnown = true;
                  break;
               }
            }
            if (isKnown) {
               continue;
            }
            // Validate as attribute type
            if (Strings.isValid(header)) {
               AttributeTypeToken attributeType = null;
               try {
                  attributeType = AttributeTypeManager.getType(header);
               } catch (Exception ex) {
                  if (!hasHeaderErrors) {
                     rd.log("Row 1 (Header):");
                     hasHeaderErrors = true;
                  }
                  reportedInvalidAttrTypes.add(header);
                  rd.errorf("  Column [%s]: Attribute type [%s] is not available\n", getColumnLabel(i), header);
                  continue;
               }
               if (attributeType == null) {
                  if (!hasHeaderErrors) {
                     rd.log("Row 1 (Header):");
                     hasHeaderErrors = true;
                  }
                  reportedInvalidAttrTypes.add(header);
                  rd.errorf("  Column [%s]: Invalid Attribute Type Name => %s\n", getColumnLabel(i), header);
               } else if (!taskArtifactType.isValidAttributeType(attributeType)) {
                  if (!hasHeaderErrors) {
                     rd.log("Row 1 (Header):");
                     hasHeaderErrors = true;
                  }
                  reportedInvalidAttrTypes.add(header);
                  rd.errorf("  Column [%s]: Invalid Attribute Type for Task [%s] => %s\n", getColumnLabel(i),
                     taskArtifactType.toStringWithId(), header);
               }
            }
         }
         if (hasHeaderErrors) {
            rd.log("");
         }
      }

      @Override
      public void processRow(String[] row) {
         rowNum++;
         // Add blank line between rows for readability
         if (rowNum > 2) {
            rd.log("");
         }
         // Sanitize all cell values to handle special/non-UTF-8 characters
         for (int i = 0; i < row.length; i++) {
            if (row[i] != null) {
               row[i] = sanitizeCellValue(row[i]);
            }
         }
         monitor.setTaskName("Processing Row " + rowNum);
         JaxAtsTask jTask = JaxAtsTask.create(newTaskData, "", createdBy, createdDate);

         monitor.subTask("Validating...");
         if (!"Title".equals(headerRow[0])) {
            rd.errorf("Structural Error: Title column must be first (found \"%s\")\n", headerRow[0]);
            newTaskData.getTasks().remove(jTask);
            return;
         }

         boolean rowHasErrors = false;
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
               if (Strings.isValid(jTask.getName())) {
                  processCreatedBy(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Title")) {
               boolean validTitle = processTitle(row, jTask, i);
               if (!validTitle) {
                  rowHasErrors = true;
               }
            } else if (header.equalsIgnoreCase("Assignees")) {
               if (Strings.isValid(jTask.getName())) {
                  processAssignees(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Resolution")) {
               if (Strings.isValid(jTask.getName())) {
                  processResolution(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Description")) {
               if (Strings.isValid(jTask.getName())) {
                  processDescription(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Related to State")) {
               if (Strings.isValid(jTask.getName())) {
                  processRelatedToState(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Notes")) {
               if (Strings.isValid(jTask.getName())) {
                  processNotes(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Percent Complete")) {
               if (Strings.isValid(jTask.getName())) {
                  processPercentComplete(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Hours Spent")) {
               if (Strings.isValid(jTask.getName())) {
                  processHoursSpent(row, jTask, i);
               }
            } else if (header.equalsIgnoreCase("Estimated Hours")) {
               if (Strings.isValid(jTask.getName())) {
                  processEstimatedHours(row, jTask, i);
               }
            } else {
               if (Strings.isValid(jTask.getName())) {
                  String attrTypeName = header;
                  if (Strings.isValid(attrTypeName)) {
                     if (reportedInvalidAttrTypes.contains(attrTypeName)) {
                        // Already reported in header validation, skip
                        continue;
                     }
                     AttributeTypeToken attributeType = null;
                     try {
                        attributeType = AttributeTypeManager.getType(attrTypeName);
                     } catch (Exception ex) {
                        // Should not happen since header validation already caught this
                        continue;
                     }
                     if (attributeType != null && taskArtifactType.isValidAttributeType(attributeType)) {
                        String value = row[i];
                        if (Strings.isValid(value)) {
                           jTask.addAttribute(attributeType, value);
                        }
                     }
                  }
               }
            }
         }
         // Remove task if row had errors (empty/invalid title)
         if (rowHasErrors || Strings.isInValid(jTask.getName())) {
            newTaskData.getTasks().remove(jTask);
         }
      }

      /**
       * Sanitize cell value by replacing Unicode replacement characters and removing non-printable control characters
       * that may result from non-UTF-8 encoded input.
       */
      private String sanitizeCellValue(String value) {
         // Replace Unicode replacement character (U+FFFD) that results from malformed byte sequences
         value = value.replace("\uFFFD", "");
         // Remove zero-width and invisible Unicode characters
         value = value.replaceAll("[\\u200B-\\u200F\\u2028-\\u202F\\u2060\\uFEFF]", "");
         // Remove ASCII control characters except tab, newline, carriage return
         value = value.replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "");
         // Remove remaining non-printable Unicode control characters (preserves valid non-ASCII like accented letters)
         value = value.replaceAll("\\p{C}", "");
         return value;
      }

      /**
       * Convert zero-based column index to a spreadsheet column label (A, B, C, ... Z, AA, AB, etc.)
       */
      private String getColumnLabel(int colIndex) {
         StringBuilder label = new StringBuilder();
         int idx = colIndex;
         while (idx >= 0) {
            label.insert(0, (char) ('A' + (idx % 26)));
            idx = (idx / 26) - 1;
         }
         return label.toString();
      }

      private boolean processTitle(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         if (Strings.isValid(str)) {
            monitor.subTask(String.format("Title \"%s\"", str));
            if (newTaskData.isFixTitles()) {
               if (!Strings.isPrintable(str)) {
                  String cleaned = Strings.removeNonPrintableCharacters(str);
                  String stripped = getStrippedCharacters(str, cleaned);
                  rd.logf("  Row %d, Col %s [Title]: Removed non-printable characters: %s\n", rowNum,
                     getColumnLabel(i), stripped);
                  str = cleaned;
               }
               if (str.matches("(?s).*[\r\n]+.*")) {
                  str = str.replaceAll("[\r\n]+", "");
                  rd.logf("  Row %d, Col %s [Title]: Removed newline characters\n", rowNum, getColumnLabel(i));
               }
               if (str.length() > 250) {
                  String desc = row[i];
                  str = Strings.truncate(str, 250, true);
                  jTask.setDescription(desc);
                  rd.logf("  Row %d, Col %s [Title]: Truncated to 250 chars [%s]\n", rowNum, getColumnLabel(i), str);
               }
            } else {
               if (!Strings.isPrintable(str)) {
                  String cleaned = Strings.removeNonPrintableCharacters(str);
                  String stripped = getStrippedCharacters(str, cleaned);
                  rd.errorf("  Row %d, Col %s [Title]: Contains non-printable characters: %s\n", rowNum,
                     getColumnLabel(i), stripped);
                  return false;
               }
               if (str.matches("(?s).*[\r\n]+.*")) {
                  rd.errorf("  Row %d, Col %s [Title]: Contains newline characters\n", rowNum, getColumnLabel(i));
                  return false;
               }
               if (str.length() > 250) {
                  String truncated = Strings.truncate(str, 250, true);
                  rd.errorf(
                     "  Row %d, Col %s [Title]: Exceeds 250 characters (length: %d); truncated title would be [%s]\n",
                     rowNum, getColumnLabel(i), str.length(), truncated);
                  return false;
               }
            }
            if (Strings.isInValid(str)) {
               rd.errorf("  Row %d, Col %s [Title]: Title is invalid\n", rowNum, getColumnLabel(i));
            }
            jTask.setName(str);
            return true;
         } else {
            rd.errorf("  Row %d, Col %s [Title]: Title cannot be empty\n", rowNum, getColumnLabel(i));
            return false;
         }
      }

      /**
       * Returns a displayable representation of characters that were stripped, showing their Unicode code points.
       */
      private String getStrippedCharacters(String original, String cleaned) {
         StringBuilder stripped = new StringBuilder();
         for (int idx = 0; idx < original.length(); idx++) {
            char c = original.charAt(idx);
            if (cleaned.indexOf(c) < 0 || isNonPrintable(c)) {
               if (stripped.length() > 0) {
                  stripped.append(", ");
               }
               stripped.append(String.format("U+%04X", (int) c));
            }
         }
         return stripped.toString();
      }

      private boolean isNonPrintable(char c) {
         return c > 0x7F || (c < 0x20 && c != '\r' && c != '\n' && c != '\t') || Character.getType(c) == Character.CONTROL;
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
            rd.errorf("  Row %d, Col %s [Description]: Cannot be blank\n", rowNum, getColumnLabel(i));
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
               rd.errorf("  Row %d, Col %s [Created By]: User \"%s\" does not exist\n", rowNum, getColumnLabel(i), str);
               return;
            }
            if (user == null) {
               try {
                  user = AtsApiService.get().getUserService().getUserByName(str);
               } catch (Exception ex) {
                  rd.errorf("  Row %d, Col %s [Created By]: User \"%s\" does not exist\n", rowNum, getColumnLabel(i),
                     str);
                  return;
               }
            }
            if (user != null) {
               jTask.setCreatedByUserId(user.getUserId());
            } else {
               rd.errorf("  Row %d, Col %s [Created By]: User \"%s\" does not exist\n", rowNum, getColumnLabel(i), str);
               return;
            }
         } else {
            rd.errorf("  Row %d, Col %s [Created By]: Cannot be empty\n", rowNum, getColumnLabel(i));
         }
      }

      private void processEstimatedHours(String[] row, JaxAtsTask jTask, int i) {
         String str = row[i];
         double hours = 0;
         if (Strings.isValid(str)) {
            try {
               hours = Double.valueOf(str);
            } catch (Exception ex) {
               rd.errorf("  Row %d, Col %s [Estimated Hours]: Invalid value \"%s\"\n", rowNum, getColumnLabel(i), str);
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
               rd.errorf("  Row %d, Col %s [Hours Spent]: Invalid value \"%s\"\n", rowNum, getColumnLabel(i), str);
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
               rd.errorf("  Row %d, Col %s [Percent Complete]: Invalid value \"%s\"\n", rowNum, getColumnLabel(i), str);
            }
            jTask.addAttribute(AtsAttributeTypes.PercentComplete, percent);
         }
      }

      private void processAssignees(String[] row, JaxAtsTask jTask, int i) {
         if (row[i] == null) {
            rd.errorf("  Row %d, Col %s [Assignees]: Cannot be blank\n", rowNum, getColumnLabel(i));
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
               rd.errorf("  Row %d, Col %s [Assignees]: Invalid user \"%s\"; using current user\n", rowNum,
                  getColumnLabel(i), userName);
            }
            jTask.addAssigneeUserIds(user.getUserId());
         }
      }

   }
}