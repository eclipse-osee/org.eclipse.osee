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
package org.eclipse.osee.define.report;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import org.eclipse.osee.define.report.internal.SafetyInformationAccumulator;
import org.eclipse.osee.define.report.internal.TraceAccumulator;
import org.eclipse.osee.define.report.internal.TraceMatch;
import org.eclipse.osee.define.report.internal.util.ComponentUtil;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

public class SafetyReportGenerator {
   private SafetyInformationAccumulator accumulator;
   private QueryFactory queryFactory;
   private ComponentUtil componentUtil;
   private final TraceMatch match = new TraceMatch("\\^SRS\\s*([^;]+);?", "\\[?(\\{[^\\}]+\\})(.*)");
   private final TraceAccumulator traces = new TraceAccumulator(".*\\.(java|ada|ads|adb|c|h)", match);
   private long branchUuid;
   private final Log logger;

   public static int SYSTEM_REQUIREMENT_INDEX = 3;
   public static int SUBSYSTEM_FUNCTION_INDEX = 5;
   public static int SUBSYSTEM_INDEX = 7;
   public static int SOFTWARE_REQUIREMENT_INDEX = 11;
   public static int CODE_UNIT_INDEX = 17;
   private final String[] columnHeadings = {
      "System Function",
      "Safety Criticality",
      "SFHA Hazard(s)",
      "Paragraph #",
      "System Requirement Name",
      "Subsystem Function",
      "Safety Criticality",
      CoreAttributeTypes.Subsystem.getName(),
      "Paragraph #",
      "Subsystem Requirement Name",
      "Development Assurance Level",
      CoreArtifactTypes.SoftwareRequirement.getName(),
      "Development Assurance Level",
      "Boeing Equivalent SW Qual Level",
      "Category",
      "SW Partition",
      "SW CSU",
      "SW Code Unit"};

   public SafetyReportGenerator(Log logger) {
      this.logger = logger;
   }

   private void init(OrcsApi orcsApi, long branchUuid, ISheetWriter writer) {
      accumulator = new SafetyInformationAccumulator(this, writer);
      queryFactory = orcsApi.getQueryFactory();
      this.branchUuid = branchUuid;
      componentUtil = new ComponentUtil(branchUuid, orcsApi);
   }

   public void runOperation(OrcsApi providedOrcs, long branchUuid, String codeRoot, Writer providedWriter) throws IOException {
      ISheetWriter writer = new ExcelXmlWriter(providedWriter);

      init(providedOrcs, branchUuid, writer);

      boolean doTracability = false;
      if (codeRoot != null && codeRoot.isEmpty() != true) {
         doTracability = true;
      }

      if (doTracability) {
         File root = new File(codeRoot);
         traces.extractTraces(root);
      }
      ArtifactReadable functionsFolder =
         queryFactory.fromBranch(branchUuid).andIsOfType(CoreArtifactTypes.Folder).andNameEquals(
            "System Functions").getResults().getExactlyOne();
      processSystemFunctions(functionsFolder, writer);

      writer.endWorkbook();
   }

   private void processSystemFunctions(ArtifactReadable functionsFolder, ISheetWriter writer) throws IOException {
      writer.startSheet("report", columnHeadings.length);
      writer.writeRow((Object[]) columnHeadings);

      for (ArtifactReadable systemFunction : functionsFolder.getDescendants()) {
         if (systemFunction.isOfType(CoreArtifactTypes.SystemFunction)) {
            writer.writeCell(systemFunction.getName());
            accumulator.reset(systemFunction);
            accumulator.buildSubsystemsRequirementsMap(systemFunction);
            String criticality;
            if (systemFunction.getAttributes(CoreAttributeTypes.SafetyCriticality).size() != 1) {
               logger.debug("found too many attributes on %s", systemFunction.toString());
               criticality = systemFunction.getAttributes(
                  CoreAttributeTypes.SafetyCriticality).iterator().next().getDisplayableString();
            } else {
               criticality = systemFunction.getSoleAttributeAsString(CoreAttributeTypes.SafetyCriticality);
            }

            writer.writeCell(criticality);
            writer.writeCell(getSFHAHazards(systemFunction));
            for (ArtifactReadable systemRequirement : systemFunction.getRelated(
               CoreRelationTypes.Design__Requirement)) {
               writer.writeCell(systemRequirement.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber, ""),
                  SYSTEM_REQUIREMENT_INDEX);
               writer.writeCell(systemRequirement.getName());
               accumulator.output();
            }
            writer.endRow();
         }
      }
      writer.endSheet();
   }

   private String getSFHAHazards(ArtifactReadable systemFunction) {
      StringBuilder sb = new StringBuilder();
      for (ArtifactReadable assessment : systemFunction.getRelated(CoreRelationTypes.Safety__Safety_Assessment)) {
         if (sb.length() > 0) {
            sb.append(", ");
         }
         sb.append(assessment.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber, ""));
         sb.append(" ");
         sb.append(assessment.getSoleAttributeAsString(CoreAttributeTypes.Name, ""));
         sb.append(" ");
         sb.append(assessment.getSoleAttributeAsString(CoreAttributeTypes.Sfha, ""));
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
