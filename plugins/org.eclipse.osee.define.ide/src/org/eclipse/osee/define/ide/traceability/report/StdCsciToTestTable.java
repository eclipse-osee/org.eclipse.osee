/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.ide.traceability.report;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.define.ide.traceability.ArtifactOperations;
import org.eclipse.osee.define.ide.traceability.RequirementTraceabilityData;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class StdCsciToTestTable implements ISimpleTable {

   private final RequirementTraceabilityData source;

   public StdCsciToTestTable(RequirementTraceabilityData source) {
      this.source = source;
   }

   @Override
   public String getHeader() {
      StringBuilder builder = new StringBuilder();
      builder.append("<Column ss:StyleID=\"s38\" ss:Width=\"63.75\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:Width=\"160.25\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:Width=\"100.75\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:Width=\"40.75\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:Width=\"180.75\"/>");
      builder.append("<Row ss:AutoFitHeight=\"0\" ss:Height=\"13.5\">");
      builder.append(
         "<Cell ss:MergeAcross=\"4\" ss:StyleID=\"s22\"><Data ss:Type=\"String\">Table X.X: CSCI Requirements to Test Traceability</Data></Cell>");
      builder.append("</Row>");
      builder.append("<Row ss:AutoFitHeight=\"0\" ss:Height=\"14.25\">");
      builder.append(
         "<Cell ss:MergeAcross=\"2\" ss:StyleID=\"m151274392\"><Data ss:Type=\"String\">CSCI Requirement</Data></Cell>");
      builder.append("<Cell ss:MergeDown=\"1\" ss:StyleID=\"m151274402\"><Data ss:Type=\"String\">Test</Data></Cell>");
      builder.append(
         "<Cell ss:MergeDown=\"1\" ss:StyleID=\"m151274412\"><Data ss:Type=\"String\">Test Script / Test Procedure</Data></Cell>");
      builder.append("</Row>");
      builder.append("<Row ss:AutoFitHeight=\"0\" ss:Height=\"14.25\">");
      builder.append("<Cell ss:StyleID=\"s24\"><Data ss:Type=\"String\">Paragraph #</Data></Cell>");
      builder.append("<Cell ss:StyleID=\"s24\"><Data ss:Type=\"String\">Paragraph Title</Data></Cell>");
      builder.append("<Cell ss:StyleID=\"s24\"><Data ss:Type=\"String\">Requirement Type</Data></Cell>");
      builder.append("</Row>");
      return builder.toString();
   }

   @Override
   public String getHeaderStyles() {
      StringBuilder builder = new StringBuilder();
      builder.append("<Styles><Style ss:ID=\"Default\" ss:Name=\"Normal\">");
      builder.append("<Alignment ss:Vertical=\"Bottom\"/><Borders/><Font/>");
      builder.append("<Interior/><NumberFormat/>Protection/></Style><Style ss:ID=\"m151274392\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/></Borders>");
      builder.append("<Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"m151274402\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/></Borders>");
      builder.append("<Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"m151274412\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/></Borders>");
      builder.append("<Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"s22\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/></Borders>");
      builder.append("<Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"s24\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/>");
      builder.append("</Style>");
      builder.append("<Style ss:ID=\"s38\"><Alignment ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/></Style>");
      builder.append("</Styles>");
      return builder.toString();
   }

   @Override
   public String getWorksheetName() {
      return "STD - CSCI Requirements to Test Traceability";
   }

   @Override
   public int getColumnCount() {
      return 5;
   }

   @Override
   public void generateBody(ExcelXmlWriter sheetWriter) throws Exception {
      List<Artifact> requirements = ArtifactOperations.sortByParagraphNumbers(source.getAllSwRequirements());
      for (Artifact artifact : requirements) {
         ArtifactOperations operations = new ArtifactOperations(artifact);
         String paragraphTitle = operations.getName();
         String paragraphNumber = operations.getParagraphNumber();
         String artifactType = artifact.getArtifactTypeName();

         String partition =
            org.eclipse.osee.framework.jdk.core.util.Collections.toString(",\n", operations.getPartitions());

         String qualificationLine = getQualificationMethod(source, artifact);
         sheetWriter.writeRow(paragraphNumber, paragraphTitle, artifactType, partition, qualificationLine);
      }
   }

   private String getQualificationMethod(RequirementTraceabilityData source, Artifact artifact) {
      StringBuilder builder = new StringBuilder();

      String scripts = getCodeUnitLine(source, artifact);
      if (Strings.isValid(scripts) != false) {
         builder.append(scripts);
      }

      if (builder.length() == 0) {
         builder.append("None");
      }
      return builder.toString();
   }

   private String getCodeUnitLine(RequirementTraceabilityData source, Artifact artifact) {
      String toReturn = "";
      Collection<String> codeUnits = source.getRequirementsToCodeUnits().getValues(artifact);
      if (codeUnits != null) {
         List<String> units = new ArrayList<>();
         for (String codeUnit : codeUnits) {
            if (Strings.isValid(codeUnit)) {
               int index = codeUnit.lastIndexOf(File.separator);
               codeUnit = codeUnit.substring(index + 1, codeUnit.length());
            }
            units.add(codeUnit);
         }
         toReturn = org.eclipse.osee.framework.jdk.core.util.Collections.toString(",\n", units);
      }
      return toReturn;
   }

}
