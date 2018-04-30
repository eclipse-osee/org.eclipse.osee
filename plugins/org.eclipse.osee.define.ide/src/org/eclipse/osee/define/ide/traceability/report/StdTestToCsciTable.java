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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.eclipse.osee.define.ide.traceability.ArtifactOperations;
import org.eclipse.osee.define.ide.traceability.RequirementTraceabilityData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.HashCollectionSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class StdTestToCsciTable implements ISimpleTable {

   private final RequirementTraceabilityData source;

   public StdTestToCsciTable(RequirementTraceabilityData source) {
      this.source = source;
   }

   @Override
   public String getHeader() {
      StringBuilder builder = new StringBuilder();
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"32.25\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"169.5\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"82.5\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"275.25\"/>");
      builder.append("<Column ss:StyleID=\"s38\" ss:AutoFitWidth=\"0\" ss:Width=\"169.5\"/>");
      builder.append("<Row ss:Height=\"13.5\">");
      builder.append(
         "<Cell ss:MergeAcross=\"4\" ss:StyleID=\"s21\"><Data ss:Type=\"String\">Table X.X: Test to CSCI Requirements Traceability</Data></Cell>");
      builder.append("</Row>");
      builder.append("<Row ss:Height=\"14.25\">");
      builder.append("<Cell ss:MergeDown=\"1\" ss:StyleID=\"m15149990\"><Data ss:Type=\"String\">Test</Data></Cell>");
      builder.append(
         "<Cell ss:MergeDown=\"1\" ss:StyleID=\"m15150000\"><Data ss:Type=\"String\">Test Script / Test Procedure</Data></Cell>");
      builder.append(
         "<Cell ss:MergeAcross=\"2\" ss:StyleID=\"m15150010\"><Data ss:Type=\"String\">CSCI Requirement</Data></Cell>");
      builder.append("</Row>");
      builder.append("<Row ss:Height=\"14.25\">");
      builder.append("<Cell ss:Index=\"3\" ss:StyleID=\"s28\"><Data ss:Type=\"String\">Paragraph #</Data></Cell>");
      builder.append("<Cell ss:StyleID=\"s28\"><Data ss:Type=\"String\">Paragraph Title</Data></Cell>");
      builder.append("<Cell ss:StyleID=\"s28\"><Data ss:Type=\"String\">Requirement Type</Data></Cell>");
      builder.append("</Row>");
      return builder.toString();
   }

   @Override
   public String getHeaderStyles() {
      StringBuilder builder = new StringBuilder();
      builder.append("<Styles><Style ss:ID=\"Default\" ss:Name=\"Normal\">");
      builder.append("<Alignment ss:Vertical=\"Bottom\"/>");
      builder.append("<Borders/><Font/><Interior/><NumberFormat/><Protection/></Style>");
      builder.append(
         "<Style ss:ID=\"m15149990\"><Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"m15150000\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\" ss:WrapText=\"1\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"m15150010\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Left\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Right\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("<Border ss:Position=\"Top\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"s21\">");
      builder.append("<Alignment ss:Horizontal=\"Center\" ss:Vertical=\"Bottom\"/><Borders>");
      builder.append("<Border ss:Position=\"Bottom\" ss:LineStyle=\"Continuous\" ss:Weight=\"3\"/>");
      builder.append("</Borders><Font x:Family=\"Swiss\" ss:Bold=\"1\"/></Style><Style ss:ID=\"s28\">");
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
      return "STD - Test to CSCI Requirements Traceability";
   }

   @Override
   public int getColumnCount() {
      return 5;
   }

   @Override
   public void generateBody(ExcelXmlWriter sheetWriter) throws Exception {
      Collection<Artifact> directRequirements = source.getAllSwRequirements();
      HashCollectionSet<String, Artifact> partitionMap = ArtifactOperations.sortByPartition(directRequirements);

      HashCollection<Artifact, String> requirementsToQualificationMethod = getQualificationMethods(source);

      HashCollectionSet<String, String> partitionToQualificationMethod = new HashCollectionSet<>(TreeSet::new);
      HashCollectionSet<String, Artifact> qualificationMethodToRequirements = new HashCollectionSet<>(TreeSet::new);
      for (String partition : partitionMap.keySet()) {
         for (Artifact artifact : partitionMap.getValues(partition)) {

            Collection<String> values = requirementsToQualificationMethod.getValues(artifact);
            if (values != null) {
               for (String codeUnit : values) {
                  if (Strings.isValid(codeUnit)) {
                     int index = codeUnit.lastIndexOf(File.separator);
                     codeUnit = codeUnit.substring(index + 1, codeUnit.length());
                  }
                  partitionToQualificationMethod.put(partition, codeUnit);
                  qualificationMethodToRequirements.put(codeUnit, artifact);
               }
            }

         }
      }

      for (String partition : new TreeSet<String>(partitionToQualificationMethod.keySet())) {
         List<String> units = new ArrayList<>(partitionToQualificationMethod.getValues(partition));
         Collections.sort(units);
         for (String codeUnit : units) {
            Set<Artifact> artifacts = new HashSet<>();
            for (Artifact req : qualificationMethodToRequirements.getValues(codeUnit)) {
               List<String> attributeValues = req.getAttributeValues(CoreAttributeTypes.Partition);
               if (attributeValues.contains(partition)) {
                  artifacts.add(req);
               }
            }
            processRow(sheetWriter, partition, codeUnit, ArtifactOperations.sortByParagraphNumbers(artifacts));
         }
      }
   }

   private HashCollection<Artifact, String> getQualificationMethods(RequirementTraceabilityData source) {
      HashCollection<Artifact, String> toReturn = new HashCollection<>();
      HashCollectionSet<Artifact, String> requirementsToCodeUnits = source.getRequirementsToCodeUnits();

      // Combine Test Scripts and Test Procedures
      for (Artifact requirement : source.getAllSwRequirements()) {
         List<String> testScripts = new ArrayList<String>();
         requirementsToCodeUnits.forEachValue(requirement, testScripts::add);
         if (testScripts != null) {
            toReturn.put(requirement, testScripts);
         }
      }
      return toReturn;
   }

   private void processRow(ISheetWriter sheetWriter, String partition, String codeUnit, Collection<Artifact> artifacts) throws Exception {
      List<String> paragraphTitles = new ArrayList<>();
      List<String> paragraphNumbers = new ArrayList<>();
      List<String> artifactTypes = new ArrayList<>();
      for (Artifact artifact : artifacts) {
         ArtifactOperations operator = new ArtifactOperations(artifact);
         String name = operator.getName();
         String number = operator.getParagraphNumber();

         if (paragraphTitles.contains(name) != true && paragraphNumbers.contains(number) != true) {
            paragraphTitles.add(name);
            artifactTypes.add(artifact.getArtifactTypeName());
            if (Strings.isValid(number) != true) {
               number = "-1";
            }
            paragraphNumbers.add(number);
         }
      }
      String paragraphTitle = org.eclipse.osee.framework.jdk.core.util.Collections.toString(",\n", paragraphTitles);
      String paragraphNumber = org.eclipse.osee.framework.jdk.core.util.Collections.toString(",\n", paragraphNumbers);
      String artifactType = org.eclipse.osee.framework.jdk.core.util.Collections.toString(",\n", artifactTypes);
      sheetWriter.writeRow(partition, codeUnit, paragraphNumber, paragraphTitle, artifactType);
   }
}
