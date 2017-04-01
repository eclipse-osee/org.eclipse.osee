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
package org.eclipse.osee.define.traceability.report;

import java.util.List;
import org.eclipse.osee.define.traceability.ArtifactOperations;
import org.eclipse.osee.define.traceability.RequirementTraceabilityData;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class StpCsciToTestTable implements ISimpleTable {

   private final RequirementTraceabilityData source;

   public StpCsciToTestTable(RequirementTraceabilityData source) {
      this.source = source;
   }

   @Override
   public String getHeader() {
      StringBuilder builder = new StringBuilder();
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"95.25\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"387.75\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"31.5\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"53.25\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"47.25\"/>");
      builder.append("<Row ss:Height=\"13.5\">");
      builder.append(
         "<Cell ss:MergeAcross=\"4\" ss:StyleID=\"s21\"><Data ss:Type=\"String\">Table X.X: CSCI Requirements to Test Traceability</Data></Cell>");
      builder.append("</Row><Row ss:Height=\"14.25\">");
      builder.append(
         "<Cell ss:MergeAcross=\"1\" ss:StyleID=\"m15143714\"><Data ss:Type=\"String\">CSCI Requirement</Data></Cell>");
      builder.append("<Cell ss:MergeDown=\"1\" ss:StyleID=\"m15143754\"><Data ss:Type=\"String\">Test</Data></Cell>");
      builder.append(
         "<Cell ss:MergeDown=\"1\" ss:StyleID=\"m15143724\"><Data ss:Type=\"String\">Qual Method</Data></Cell>");
      builder.append(
         "<Cell ss:MergeDown=\"1\" ss:StyleID=\"m15143734\"><Data ss:Type=\"String\">Qual Facility</Data></Cell>");
      builder.append("</Row><Row ss:Height=\"14.25\">");
      builder.append("<Cell ss:StyleID=\"s26\"><Data ss:Type=\"String\">Paragraph #</Data></Cell>");
      builder.append("<Cell ss:StyleID=\"s26\"><Data ss:Type=\"String\">Paragraph Title</Data></Cell>");
      builder.append("</Row>");
      return builder.toString();
   }

   @Override
   public String getHeaderStyles() {
      StringBuilder builder = new StringBuilder();
      builder.append("<Styles><Style ss:ID=\"Default\" ss:Name=\"Normal\">");
      builder.append("<Alignment ss:Vertical=\"Bottom\"/><Borders/><Font/>");
      builder.append("<Interior/><NumberFormat/><Protection/></Style><Style ss:ID=\"m15143714\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"m15143724\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"m15143734\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"m15143754\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"s21\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"s26\">");
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
      return "STP - CSCI Requirements to Test Traceability";
   }

   @Override
   public int getColumnCount() {
      return 5;
   }

   @Override
   public void generateBody(ExcelXmlWriter sheetWriter) throws Exception {
      List<Artifact> requirements = ArtifactOperations.sortByParagraphNumbers(source.getDirectSwRequirements());
      for (Artifact artifact : requirements) {
         processRow(sheetWriter, artifact);
      }
   }

   private void processRow(ExcelXmlWriter sheetWriter, Artifact artifact) throws Exception {
      ArtifactOperations operator = new ArtifactOperations(artifact);
      String paragraphTitle = operator.getName();
      String paragraphNumber = operator.getParagraphNumber();
      String qualMethod = operator.getQualificationMethod();
      String qualFacility = operator.getQualificationFacility();
      String partition = org.eclipse.osee.framework.jdk.core.util.Collections.toString(",\n", operator.getPartitions());
      sheetWriter.writeRow(paragraphNumber, paragraphTitle, partition, qualMethod, qualFacility);
   }
}
