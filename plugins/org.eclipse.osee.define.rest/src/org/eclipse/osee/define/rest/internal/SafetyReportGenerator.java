/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal;

import static org.eclipse.osee.framework.core.data.CoreActivityTypes.OSEE_ERROR;
import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import org.eclipse.osee.activity.api.ActivityLog;
import org.eclipse.osee.define.rest.internal.util.ComponentUtil;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */
public class SafetyReportGenerator {
   private SafetyInformationAccumulator accumulator;
   private QueryFactory queryFactory;
   private ComponentUtil componentUtil;
   private final TraceMatch match = new TraceMatch("\\^SRS\\s*([^;]+);?", "\\[?(\\{[^\\}]+\\})(.*)");
   private final TraceAccumulator traces = new TraceAccumulator(".*\\.(java|ada|ads|adb|c|h)", match);
   private final ActivityLog activityLog;

   public static int SYSTEM_REQUIREMENT_INDEX = 6;
   public static int SUBSYSTEM_FUNCTION_INDEX = 7;
   public static int SUBSYSTEM_INDEX = 11;
   public static int SOFTWARE_REQUIREMENT_INDEX = 16;
   public static int CODE_UNIT_INDEX = 23;
   private final String[] columnHeadings = {
      "System Function",
      "Severity Category",
      "SFHA Hazard(s)",
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
      CoreArtifactTypes.SoftwareRequirementMsWord.getName(),
      "IDAL",
      "IDAL Rationale",
      "Boeing Equivalent SW Qual Level",
      "Functional Category",
      "SW Partition",
      "SW CSU",
      "SW Code Unit"};

   public SafetyReportGenerator(ActivityLog activityLog) {
      this.activityLog = activityLog;
   }

   private void init(OrcsApi orcsApi, BranchId branchId, ISheetWriter writer) {
      accumulator = new SafetyInformationAccumulator(this, writer);
      queryFactory = orcsApi.getQueryFactory();
      componentUtil = new ComponentUtil(branchId, orcsApi);
   }

   public void runOperation(OrcsApi providedOrcs, BranchId branchId, String codeRoot, Writer providedWriter) throws IOException {
      ISheetWriter writer = new ExcelXmlWriter(providedWriter);

      init(providedOrcs, branchId, writer);

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

      for (ArtifactReadable systemFunction : functionsFolder.getDescendants()) {
         if (systemFunction.isOfType(CoreArtifactTypes.SystemFunctionMsWord)) {
            writer.writeCell(systemFunction.getName());
            accumulator.reset(systemFunction);
            accumulator.buildSubsystemsRequirementsMap(systemFunction);
            String sevCat;
            if (systemFunction.getAttributes(CoreAttributeTypes.SeverityCategory).size() != 1) {
               activityLog.createEntry(OSEE_ERROR, "found too many attributes on " + systemFunction.toString());
               sevCat = systemFunction.getAttributes(
                  CoreAttributeTypes.SeverityCategory).iterator().next().getDisplayableString();
            } else {
               sevCat = systemFunction.getSoleAttributeAsString(CoreAttributeTypes.SeverityCategory);
            }
            writer.writeCell(sevCat);
            writeSFHAInfo(systemFunction, sevCat, writer);

            writer.writeCell(systemFunction.getSoleAttributeAsString(CoreAttributeTypes.FDAL, ""));
            writer.writeCell(systemFunction.getSoleAttributeAsString(CoreAttributeTypes.FdalRationale, ""));

            StringBuilder paraNums = new StringBuilder();
            StringBuilder reqNames = new StringBuilder();
            boolean firstTime = true;
            for (ArtifactReadable systemRequirement : systemFunction.getRelated(
               CoreRelationTypes.Design_Requirement)) {
               if (!firstTime) {
                  paraNums.append(", ");
                  reqNames.append(", ");
               } else {
                  firstTime = false;
               }
               paraNums.append(systemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
               reqNames.append(systemRequirement.getName());
            }
            writer.writeCell(paraNums);
            writer.writeCell(reqNames, SYSTEM_REQUIREMENT_INDEX);
            accumulator.output();
            writer.endRow();
         }
      }
      writer.endSheet();
   }

   private void writeSFHAInfo(ArtifactReadable systemFunction, String sevCat, ISheetWriter writer) throws IOException {
      ResultSet<ArtifactReadable> results = systemFunction.getRelated(CoreRelationTypes.Assessment_SafetyAssessment);
      if (results.isEmpty()) {
         writer.writeCell("No SFHA Hazards found");
      } else {
         writer.writeCell(getSFHAHazards(results));
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

   public Collection<String> getRequirementToCodeUnitsValues(ArtifactReadable softwareRequirement) {
      return traces.getFiles(softwareRequirement.getName());
   }

   public ComponentUtil getComponentUtil() {
      return componentUtil;
   }
}
