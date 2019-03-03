/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.rest.internal;

import java.io.File;
import java.io.IOException;
import java.io.Writer;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.CaseInsensitiveString;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author David W. Miller
 */
public class TraceReportGenerator {
   private TraceInformationAccumulator accumulator;
   private QueryFactory queryFactory;
   private final TraceMatch match;
   private final TraceAccumulator traces;
   private final TraceMatch traceMatch;
   private final TraceAccumulator testTraces;
   private final ArtifactTypeToken alternateArtifactType;
   private final AttributeTypeToken alternateAttributeType;

   public static int SOFTWARE_SHEETREQ_INDEX = 3;
   public static int SOFTWARE_REQUIREMENT_INDEX = 4;
   public static int SOFTWARE_COMPONENT_INDEX = 7;
   public static int SOFTWARE_PROCEDURE_INDEX = 9;
   public static int SOFTWARE_SCRIPT_INDEX = 11;
   public static int SOFTWARE_CODEUNIT_INDEX = 13;
   public static int SOFTWARE_TRACEUNIT_INDEX = 14;

   private final String[] columnHeadings = {
      "Doors ID",
      "Paragraph #",
      "System Requirement Name",
      "Qualification Information",
      CoreArtifactTypes.SoftwareRequirement.getName(),
      "ArtifactId",
      "Equivalent SW Qual Level",
      "Traced Component Name",
      "Trace Component ArtifactId",
      "Traced Test Procedure Name",
      "Traced Test Procedure ArtifactId",
      "Traced Test Script Name",
      "Traced Test Script ArtifactId",
      "Traced Code Unit Name",
      "Traced Test Unit Name"};

   private final String[] columnHeadingsComponent =
      {"Doors ID", "Paragraph #", "System Requirement Name", CoreArtifactTypes.Component.getName(), "ArtifactId"};

   private final String[] columnHeadingsTest =
      {"Doors ID", "Paragraph #", "System Requirement Name", CoreArtifactTypes.TestPlanElement.getName(), "ArtifactId"};

   private void init(OrcsApi orcsApi, ISheetWriter writer) {
      accumulator = new TraceInformationAccumulator(this, writer);
      queryFactory = orcsApi.getQueryFactory();
   }

   public TraceReportGenerator(ArtifactTypeToken artifactType, AttributeTypeToken attributeType) {
      String traceFile = ".*\\.(ada$|cpp$|c$|h$)";
      String traceMatchReg = "\\^SRS\\s*([^;]+);?";
      String testFile = ".*\\.(java$)";
      String testMatch = "addTraceability\\s*\\(\"SRS\\s*([^\"]+)\"\\)";

      alternateArtifactType = artifactType;
      alternateAttributeType = attributeType;

      match = new TraceMatch(traceMatchReg, null);
      traces = new TraceAccumulator(traceFile, match);
      traceMatch = new TraceMatch(testMatch, null);
      testTraces = new TraceAccumulator(testFile, traceMatch);
   }

   public void generate(OrcsApi providedOrcs, BranchId branchId, String codeRoot, String traceRoot, Writer providedWriter) throws IOException {
      ISheetWriter writer = new ExcelXmlWriter(providedWriter);
      if (Strings.isValid(codeRoot)) {
         File root = new File(codeRoot);
         traces.extractTraces(root);
         writeTracesSheet(writer, "code traces", traces);
      }
      if (Strings.isValid(traceRoot)) {
         File troot = new File(traceRoot);
         testTraces.extractTraces(troot);
         writeTracesSheet(writer, "test traces", testTraces);
      }
      init(providedOrcs, writer);

      ArtifactReadable requirementsFolder =
         queryFactory.fromBranch(branchId).andTypeEquals(CoreArtifactTypes.Folder).andNameEquals(
            "System Requirements").getResults().getExactlyOne();
      List<ArtifactReadable> systemRequirements = requirementsFolder.getDescendants();
      processReqts(systemRequirements, writer);
      processComponents(systemRequirements, writer);
      processTests(systemRequirements, writer);
      processTestPlans(systemRequirements, writer);

      writer.endWorkbook();
   }

   private void writeTracesSheet(ISheetWriter writer, String name, TraceAccumulator accum) throws IOException {
      writer.startSheet(name, 2);
      writer.writeCell("Trace requirement name");
      writer.writeCell("Traced file");
      writer.endRow();
      for (CaseInsensitiveString key : accum.getTraceMarks()) {
         for (String result : accum.getFiles(key.toString())) {
            writer.writeCell(key);
            writer.writeCell(result);
            writer.endRow();
         }
      }
      writer.endSheet();
   }

   private void processReqts(List<ArtifactReadable> systemRequirements, ISheetWriter writer) throws IOException {
      writer.startSheet("Software Reqts", columnHeadings.length);
      writer.writeRow((Object[]) columnHeadings);

      for (ArtifactReadable systemReqt : systemRequirements) {
         if (systemReqt.isOfType(CoreArtifactTypes.SystemRequirementHTML)) {
            outputCommonCells(systemReqt, writer);
            writer.writeCell(accumulator.getAttributesToStringList(systemReqt, CoreAttributeTypes.QualificationMethod));
            accumulator.outputSubsystemsRequirementsMap(systemReqt, writer);
         }
      }
      writer.endSheet();
   }

   private void processComponents(List<ArtifactReadable> systemRequirements, ISheetWriter writer) throws IOException {
      writer.startSheet("Components", columnHeadingsComponent.length);
      writer.writeRow((Object[]) columnHeadingsComponent);

      for (ArtifactReadable systemReqt : systemRequirements) {
         if (systemReqt.isOfType(CoreArtifactTypes.SystemRequirementHTML)) {
            outputCommonCells(systemReqt, writer);
            accumulator.outputSubsystemsComponentsMap(systemReqt, writer);
         }
      }
      writer.endSheet();
   }

   private void processTests(List<ArtifactReadable> systemRequirements, ISheetWriter writer) throws IOException {
      writer.startSheet("Tests", columnHeadingsTest.length);
      writer.writeRow((Object[]) columnHeadingsTest);

      for (ArtifactReadable systemReqt : systemRequirements) {
         if (systemReqt.isOfType(CoreArtifactTypes.SystemRequirementHTML)) {
            outputCommonCells(systemReqt, writer);
            accumulator.outputSubsystemsTestsMap(systemReqt, writer);
         }
      }
      writer.endSheet();
   }

   private void processTestPlans(List<ArtifactReadable> systemRequirements, ISheetWriter writer) throws IOException {
      writer.startSheet("Test Plans", columnHeadingsTest.length);
      writer.writeRow((Object[]) columnHeadingsTest);

      for (ArtifactReadable systemReqt : systemRequirements) {
         if (systemReqt.isOfType(CoreArtifactTypes.SystemRequirementHTML)) {
            outputCommonCells(systemReqt, writer);
            accumulator.outputSubsystemsTestPlansMap(systemReqt, writer);
         }
      }
      writer.endSheet();
   }

   private void outputCommonCells(ArtifactReadable systemRequirement, ISheetWriter writer) throws IOException {
      writer.writeCell(systemRequirement.getIdString());
      writer.writeCell(systemRequirement.getSoleAttributeAsString(CoreAttributeTypes.ParagraphNumber));
      writer.writeCell(systemRequirement.getName());
   }

   public Collection<String> getRequirementToCodeUnitsValues(ArtifactReadable softwareRequirement) {
      return traces.getFiles(accumulator.handleEquivalentName(softwareRequirement));
   }

   public Collection<String> getRequirementToTraceUnitsValues(ArtifactReadable softwareRequirement) {
      return testTraces.getFiles(accumulator.handleEquivalentName(softwareRequirement));
   }

   public ArtifactTypeToken getAlternateArtifactType() {
      return alternateArtifactType;
   }

   public AttributeTypeToken getAlternateAttributeType() {
      return alternateAttributeType;
   }
}
