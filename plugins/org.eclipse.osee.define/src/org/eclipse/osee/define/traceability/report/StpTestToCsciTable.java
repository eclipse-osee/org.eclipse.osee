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

import java.util.Collection;
import java.util.TreeSet;
import org.eclipse.osee.define.traceability.ArtifactOperations;
import org.eclipse.osee.define.traceability.RequirementTraceabilityData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class StpTestToCsciTable implements ISimpleTable {

   private final RequirementTraceabilityData source;

   public StpTestToCsciTable(RequirementTraceabilityData source) {
      this.source = source;
   }

   @Override
   public String getWorksheetName() {
      return "STP - Test to CSCI Requirements Traceability";
   }

   @Override
   public int getColumnCount() {
      return 5;
   }

   @Override
   public String getHeader() {
      StringBuilder builder = new StringBuilder();
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"55.5\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"88.5\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"114\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"99.75\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"105\"/>");
      builder.append("<Row ss:Height=\"13.5\">");
      builder.append(
         "<Cell ss:MergeAcross=\"4\" ss:StyleID=\"s31\"><Data ss:Type=\"String\">Table X.X: Test to CSCI Requirements Traceability</Data></Cell>");
      builder.append("</Row>");
      builder.append("<Row ss:Height=\"14.25\">");
      builder.append("<Cell ss:MergeDown=\"1\" ss:StyleID=\"s25\"><Data ss:Type=\"String\">Test</Data></Cell>");
      builder.append(
         "<Cell ss:MergeAcross=\"1\" ss:StyleID=\"s25\"><Data ss:Type=\"String\">CSCI Requirement</Data></Cell>");
      builder.append("<Cell ss:MergeDown=\"1\" ss:StyleID=\"s25\"><Data ss:Type=\"String\">Qual Method</Data></Cell>");
      builder.append(
         "<Cell ss:MergeDown=\"1\" ss:StyleID=\"s25\"><Data ss:Type=\"String\">Qual Facility</Data></Cell>");
      builder.append("</Row>");
      builder.append("<Row ss:Height=\"14.25\">");
      builder.append("<Cell ss:Index=\"2\" ss:StyleID=\"s29\"><Data ss:Type=\"String\">Paragraph #</Data></Cell>");
      builder.append("<Cell ss:StyleID=\"s29\"><Data ss:Type=\"String\">Paragraph Title</Data></Cell>");
      builder.append("</Row>");
      return builder.toString();
   }

   @Override
   public String getHeaderStyles() {
      StringBuilder builder = new StringBuilder();
      builder.append("<Styles><Style ss:ID=\"Default\" ss:Name=\"Normal\"><Alignment ss:Vertical=\"Bottom\"/>");
      builder.append("<Borders/><Font/><Interior/><NumberFormat/><Protection/></Style>");
      builder.append("<Style ss:ID=\"s25\"><Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/>");
      builder.append("<Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style>");
      builder.append("<Style ss:ID=\"s29\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/>");
      builder.append("<Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/>");
      builder.append("</Style><Style ss:ID=\"s31\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/>");
      builder.append("<Borders><Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders>");
      builder.append("<Font x:Family=\"Swiss\" ss:Bold=\"1\"/>");
      builder.append("</Style>");
      builder.append("<Style ss:ID=\"s38\"><Alignment ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/></Style>");
      builder.append("</Styles>");
      return builder.toString();
   }

   @Override
   public void generateBody(ExcelXmlWriter sheetWriter) throws Exception {
      Collection<Artifact> directRequirements = source.getDirectSwRequirements();
      HashCollection<String, Artifact> partitionMap = ArtifactOperations.sortByPartition(directRequirements);

      for (String partition : new TreeSet<String>(partitionMap.keySet())) {
         Collection<Artifact> artifacts = partitionMap.getValues(partition);
         artifacts = ArtifactOperations.sortByParagraphNumbers(artifacts);
         for (Artifact artifact : artifacts) {
            processRow(sheetWriter, partition, artifact);
         }
      }
   }

   private void processRow(ExcelXmlWriter sheetWriter, String partition, Artifact artifact) throws Exception {
      ArtifactOperations operator = new ArtifactOperations(artifact);
      String paragraphTitle = operator.getName();
      String paragraphNumber = operator.getParagraphNumber();
      String qualMethod = operator.getQualificationMethod();
      String qualFacility = operator.getQualificationFacility();

      sheetWriter.writeRow(partition, paragraphNumber, paragraphTitle, qualMethod, qualFacility);
   }

}
