/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.traceability.ImportTraceabilityOperation;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeArgumentException;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.plugin.core.util.AIFile;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Attribute;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class SubsystemFullTraceReport extends AbstractBlam {
   private CharBackedInputStream charBak;
   private ISheetWriter writer;
   private HashCollection<Artifact, String> requirementsToCodeUnits;
   private static int SOFTWARE_REQUIREMENT_INDEX = 9;
   private static int TEST_INDEX = 13;
   private final ArrayList<String> tests = new ArrayList<String>(50);

   @Override
   public String getName() {
      return "Subsystem Full Trace Report";
   }

   private void init() throws IOException {
      charBak = new CharBackedInputStream();
      writer = new ExcelXmlWriter(charBak.getWriter());
   }

   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      List<Artifact> artifacts = variableMap.getArtifacts("Subsystem Requirements");
      if (artifacts.isEmpty()) {
         throw new OseeArgumentException("must specify a set of artifacts");
      }
      Branch branch = artifacts.get(0).getBranch();
      String scriptPath = variableMap.getString("Script Root Directory");

      init();

      if (!scriptPath.equals("")) {
         File scriptDir = new File(variableMap.getString("Script Root Directory"));
         ImportTraceabilityOperation traceOperation = new ImportTraceabilityOperation(scriptDir, branch, false);
         Operations.executeWorkAndCheckStatus(traceOperation, monitor);
         requirementsToCodeUnits = traceOperation.getRequirementToCodeUnitsMap();
      }

      writeMainSheet(prepareSubsystemRequirements(artifacts));

      writer.endWorkbook();
      IFile iFile = OseeData.getIFile("Subsystem_Trace_Report_" + Lib.getDateTimeString() + ".xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private List<Artifact> prepareSubsystemRequirements(List<Artifact> artifacts) throws OseeCoreException {
      List<Artifact> subsystemRequirements = new ArrayList<Artifact>(400);
      for (Artifact artifact : artifacts) {
         if (artifact.isOfType(CoreArtifactTypes.Folder)) {
            subsystemRequirements.addAll(artifact.getDescendants());
         } else {
            subsystemRequirements.add(artifact);
         }
      }
      return subsystemRequirements;
   }

   private void writeMainSheet(List<Artifact> artifacts) throws IOException, OseeCoreException {
      writer.startSheet("report", 18);
      writer.writeRow(CoreArtifactTypes.SystemRequirement.getName(), null, null,
         CoreArtifactTypes.SubsystemRequirement.getName(), null, null, null, null, null,
         CoreArtifactTypes.SoftwareRequirement.getName());
      writer.writeRow("Paragraph #", "Requirement Name", "Requirement Text", "Paragraph #", "Requirement Name",
         "Requirement Text", "Subsystem", CoreAttributeTypes.QualificationMethod.getName(), "Test Procedure",
         "Paragraph #", "Requirement Name", "Partitions", CoreAttributeTypes.QualificationMethod.getName(),
         "Test Script/Test Procedure");

      for (Artifact subSystemRequirement : artifacts) {
         processSubSystemRequirement(subSystemRequirement);
      }
      writer.endSheet();
   }

   private void processSubSystemRequirement(Artifact subSystemRequirement) throws IOException, OseeCoreException {
      boolean topRowForSubsystemReq = true;
      for (Artifact systemRequirement : subSystemRequirement.getRelatedArtifacts(CoreRelationTypes.Requirement_Trace__Higher_Level)) {
         writer.writeCell(systemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
         writer.writeCell(systemRequirement.getName());
         writer.writeCell(getRequirementText(systemRequirement));

         if (topRowForSubsystemReq) {
            writer.writeCell(subSystemRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""));
            writer.writeCell(subSystemRequirement.getName());
            writer.writeCell(getRequirementText(subSystemRequirement));
            writer.writeCell(subSystemRequirement.getSoleAttributeValue(CoreAttributeTypes.Subsystem, ""));
            writer.writeCell(subSystemRequirement.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod));
            writer.writeCell(Collections.toString(",",
               subSystemRequirement.getRelatedArtifacts(CoreRelationTypes.Verification__Verifier)));
            topRowForSubsystemReq = false;
         }

         for (Artifact softwareRequirement : subSystemRequirement.getRelatedArtifacts(CoreRelationTypes.Requirement_Trace__Lower_Level)) {
            processSoftwareRequirement(softwareRequirement);
         }
         writer.endRow();
      }
   }

   private String getRequirementText(Artifact req) throws OseeCoreException {
      Attribute<?> templateContent = req.getSoleAttribute(CoreAttributeTypes.WordTemplateContent);
      String ret = templateContent.getDisplayableString();
      return StringUtils.trim(ret);
   }

   private void processSoftwareRequirement(Artifact softwareRequirement) throws IOException, OseeCoreException {
      writer.writeCell(softwareRequirement.getSoleAttributeValue(CoreAttributeTypes.ParagraphNumber, ""),
         SOFTWARE_REQUIREMENT_INDEX);
      writer.writeCell(softwareRequirement.getName());
      writer.writeCell(Collections.toString(",",
         softwareRequirement.getAttributesToStringList(CoreAttributeTypes.Partition)));
      writer.writeCell(softwareRequirement.getAttributesToStringSorted(CoreAttributeTypes.QualificationMethod));

      tests.clear();
      for (Artifact testProcedure : softwareRequirement.getRelatedArtifacts(CoreRelationTypes.Validation__Validator)) {
         tests.add(testProcedure.getName());
      }
      if (requirementsToCodeUnits != null) {
         Collection<String> testScripts = requirementsToCodeUnits.getValues(softwareRequirement);
         if (testScripts != null) {
            for (String testScript : testScripts) {
               tests.add(new File(testScript).getName());
            }
         }
      }
      writer.writeCell(Collections.toString(", ", tests), TEST_INDEX);
      writer.endRow();
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XText\" displayName=\"Script Root Directory\" defaultValue=\"C:/UserData/workspaceScripts\" toolTip=\"Leave blank if test script traceability is not needed.\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Subsystem Requirements\" /></xWidgets>";
   }

   @Override
   public String getDescriptionUsage() {
      return "Generates subsystem requirement full traceability report";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Trace");
   }
}