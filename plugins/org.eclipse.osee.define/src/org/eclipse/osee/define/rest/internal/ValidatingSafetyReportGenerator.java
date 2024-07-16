/*********************************************************************
 * Copyright (c) 2013 Boeing
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

package org.eclipse.osee.define.rest.internal;

import static org.eclipse.osee.framework.core.data.CoreActivityTypes.OSEE_ERROR;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.util.ComponentUtil;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */
public class ValidatingSafetyReportGenerator {
   private ValidatingSafetyInformationAccumulator accumulator;
   private QueryFactory queryFactory;
   private ComponentUtil componentUtil;
   private final TraceMatch match = new TraceMatch("\\^SRS\\s*([^;]+);?", "\\[?(\\{[^\\}]+\\})(.*)");
   private final TraceAccumulator traces = new TraceAccumulator(".*\\.(java|ada|ads|adb|c|h)", match);
   private final ActivityLog activityLog;

   public static int SYSTEM_REQUIREMENT_INDEX = 8;
   public static int SUBSYSTEM_FUNCTION_INDEX = 9;
   public static int SUBSYSTEM_INDEX = 13;
   public static int SOFTWARE_REQUIREMENT_INDEX = 19;
   public static int CODE_UNIT_INDEX = 28;
   private final String[] columnHeadings = {
      "System Function",
      "Severity Category",
      "SFHA Hazard(s)",
      "Hazard Level Test",
      "Software Safety Impact",
      "System FDAL",
      "System FDAL Rationale",
      "Paragraph #",
      "System Requirement Name",
      "Subsystem Function",
      "Subsystem Severity Category",
      "Subsystem FDAL",
      "Subsystem FDAL Rationale",
      CoreAttributeTypes.Subsystem.getName(),
      "Paragraph #",
      "Subsystem Requirement Name",
      "Subsystem Requirement IDAL",
      "Subsystem Requirement IDAL Rationale",
      "Subsystem Requirement Level Check",
      CoreArtifactTypes.SoftwareRequirementMsWord.getName(),
      "IDAL",
      "IDAL Rationale",
      "Software Control Category",
      "Software Control Category Rationale",
      "Boeing Equivalent SW Qual Level",
      "Functional Category",
      "SW Partition",
      "SW CSU",
      "SW Code Unit"};

   public ValidatingSafetyReportGenerator(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   private void init(OrcsApi orcsApi, BranchId branchId, ISheetWriter writer, ArtifactId view) {
      queryFactory = orcsApi.getQueryFactory();
      accumulator = new ValidatingSafetyInformationAccumulator(this, writer);
      accumulator.setupPartitions(queryFactory, branchId, view);
      componentUtil = new ComponentUtil(branchId, orcsApi);
   }

   public void runOperation(OrcsApi providedOrcs, BranchId branchId, ArtifactId view, String codeRoot, Writer providedWriter) throws IOException {
      ISheetWriter writer = new ExcelXmlWriter(providedWriter);

      init(providedOrcs, branchId, writer, view);

      boolean doTracability = false;
      if (codeRoot != null && codeRoot.isEmpty() != true) {
         doTracability = true;
      }

      if (doTracability) {
         File root = new File(codeRoot);
         traces.extractTraces(root);
      }
      ArtifactReadable functionsFolder =
         queryFactory.fromBranch(branchId).andTypeEquals(CoreArtifactTypes.Folder).andNameEquals(
            "System Functions").getResults().getExactlyOne();
      processSystemFunctions(functionsFolder, writer);

      writer.endWorkbook();
   }

   private void processSystemFunctions(ArtifactReadable functionsFolder, ISheetWriter writer) throws IOException {
      writer.startSheet("report", columnHeadings.length);
      writer.writeRow((Object[]) columnHeadings);
      String[] currentRowValues = new String[columnHeadings.length];

      for (ArtifactReadable systemFunction : functionsFolder.getDescendants()) {
         String sevCat = getSeverityCategory(systemFunction);
         boolean isNHSeverity = false;
         if (Strings.isValid(sevCat) && "NH".equals(sevCat)) {
            isNHSeverity = true;
         }
         if (systemFunction.isOfType(CoreArtifactTypes.SystemFunctionMsWord) && !isNHSeverity) {
            clearRowValues(currentRowValues);
            writeCell(systemFunction.getName(), currentRowValues, 0);
            accumulator.reset(systemFunction);
            accumulator.buildSubsystemsRequirementsMap(systemFunction);

            writeCell(sevCat, currentRowValues, 1);
            writeSFHAInfo(systemFunction, sevCat, writer, currentRowValues, 2);

            writeCell(systemFunction.getSoleAttributeAsString(CoreAttributeTypes.SoftwareSafetyImpact, ""),
               currentRowValues, 4);
            writeCell(systemFunction.getSoleAttributeAsString(CoreAttributeTypes.FDAL, ""), currentRowValues, 5);
            writeCell(systemFunction.getSoleAttributeAsString(CoreAttributeTypes.FdalRationale, ""), currentRowValues,
               6);

            StringBuilder paraNums = new StringBuilder();
            StringBuilder reqNames = new StringBuilder();
            boolean firstTime = true;
            for (ArtifactReadable systemRequirement : systemFunction.getRelated(CoreRelationTypes.Design_Requirement)) {
               if (!firstTime) {
                  paraNums.append(", ");
                  reqNames.append(", ");
               } else {
                  firstTime = false;
               }
               paraNums.append(systemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
               reqNames.append(systemRequirement.getName());
            }

            writeCell(paraNums.toString(), currentRowValues, 7);
            writeCell(reqNames.toString(), currentRowValues, SYSTEM_REQUIREMENT_INDEX);
            accumulator.output(currentRowValues);
            writer.writeRow((Object[]) currentRowValues);
         }
      }
      writer.endSheet();
   }

   private String getSeverityCategory(ArtifactReadable systemFunction) {
      ResultSet<? extends AttributeReadable<Object>> results =
         systemFunction.getAttributes(CoreAttributeTypes.SeverityCategory);
      if (results.isEmpty()) {
         activityLog.createEntry(OSEE_ERROR, "found no sevCat attribute on " + systemFunction.toString());
      } else if (systemFunction.getAttributes(CoreAttributeTypes.SeverityCategory).size() > 1) {
         activityLog.createEntry(OSEE_ERROR, "found too many sevCat attributes on " + systemFunction.toString());
         return systemFunction.getAttributes(
            CoreAttributeTypes.SeverityCategory).iterator().next().getDisplayableString();
      } else {
         return systemFunction.getSoleAttributeAsString(CoreAttributeTypes.SeverityCategory);
      }
      return "";
   }

   private void clearRowValues(String[] toClear) {
      for (int i = 0; i < toClear.length; i++) {
         toClear[i] = "";
      }
   }

   private void writeCell(String value, String[] currentRow, int col) {
      currentRow[col] = value;
   }

   private void writeSFHAInfo(ArtifactReadable systemFunction, String sevCat, ISheetWriter writer, String[] currentRowValues, int col) {
      ResultSet<ArtifactReadable> results = systemFunction.getRelated(CoreRelationTypes.Assessment_SafetyAssessment);
      if (results.isEmpty()) {
         writeCell("No SFHA Hazards found", currentRowValues, col);
         writeCell("N/A", currentRowValues, col + 1);
      } else {
         writeCell(getSFHAHazards(results), currentRowValues, col);
         writeCell(compareHazardLevel(results, sevCat), currentRowValues, col + 1);
      }

   }

   private String getSFHAHazards(ResultSet<ArtifactReadable> results) {
      StringBuilder sb = new StringBuilder();
      for (ArtifactReadable assessment : results) {
         if (sb.length() > 0) {
            sb.append(", ");
         }
         sb.append(assessment.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber, ""));
         sb.append(" ");
         sb.append(assessment.getSoleAttributeAsString(CoreAttributeTypes.Name, ""));
         sb.append(" ");
         sb.append(assessment.getSoleAttributeValue(CoreAttributeTypes.SFHA, ""));
      }
      return sb.toString();
   }

   private String compareHazardLevel(ResultSet<ArtifactReadable> results, String sevCat) {
      String toReturn = "System Function Severity Category invalid";

      try {
         Integer sevCatLevel = SafetyCriticalityLookup.getSeverityLevel(sevCat);
         toReturn = "good: " + sevCat;
         Integer sfhaSevCatLevel;

         for (ArtifactReadable assessment : results) {
            String sfha = assessment.getSoleAttributeAsString(CoreAttributeTypes.SFHA);
            if (Strings.isValid(sfha)) {
               String[] sfhaSevCat = sfha.split(" ");
               sfhaSevCatLevel = SafetyCriticalityLookup.getSeverityLevel(sfhaSevCat[2]);
               if (sevCatLevel > sfhaSevCatLevel) {
                  toReturn = "bad: " + sevCat + " > " + sfhaSevCat[2];
               }
            }

         }

      } catch (Exception ex) {
         toReturn = ex.getMessage();
      }
      return toReturn;
   }

   public Collection<String> getRequirementToCodeUnitsValues(ArtifactReadable softwareRequirement) {
      return traces.getFiles(softwareRequirement.getName());
   }

   public ComponentUtil getComponentUtil() {
      return componentUtil;
   }
}
