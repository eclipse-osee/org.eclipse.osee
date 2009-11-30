/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.define.blam.operation;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.ArtifactType;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.AIFile;
import org.eclipse.osee.framework.skynet.core.utility.OseeData;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.skynet.blam.AbstractBlam;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class SubsystemToLowLevelReqTraceReport extends AbstractBlam {
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;
   private final HashMap<String, List<Artifact>> subsysToSubsysReqsMap;
   private final List<Artifact> lowLevelReqs;
   private final HashSet<Artifact> components;
   private String reqtypeName;

   @Override
   public String getName() {
      return "Subsystem To Low Level Req Trace Report";
   }

   public SubsystemToLowLevelReqTraceReport() {
      subsysToSubsysReqsMap = new HashMap<String, List<Artifact>>();
      components = new HashSet<Artifact>();
      lowLevelReqs = new ArrayList<Artifact>(1000);
   }

   private void init() throws IOException {
      subsysToSubsysReqsMap.clear();
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      components.clear();
      lowLevelReqs.clear();
   }

   
   @Override
   public void runOperation(VariableMap variableMap, IProgressMonitor monitor) throws Exception {
      monitor.beginTask("Generate Report", 100);

      init();
      ArtifactType reqType = variableMap.getArtifactType("Low Level Requirement Type");
      reqtypeName = reqType.getName();
      initLowLevelRequirements(variableMap.getArtifacts("Lower Level Requirements"));
      initAllocationComponents(variableMap.getArtifacts("Allocation Components"));

      Branch branch = variableMap.getBranch("Branch");

      monitor.subTask("Loading Higher Level Requirements"); // bulk load to improve performance
      monitor.worked(1);
      ArtifactQuery.getArtifactListFromType(Requirements.SUBSYSTEM_REQUIREMENT, branch);
      monitor.worked(30);

      Artifact root = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(branch);
      orderSubsystemReqs(root.getChild(Requirements.SUBSYSTEM_REQUIREMENTS));

      generateLowLevelToSubsystemTrace();
      generateSubsystemToLowLevelReqTrace();

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("Subsystem_To_" + reqtypeName + "_Trace.xml");
      AIFile.writeToFile(iFile, charBak);
      Program.launch(iFile.getLocation().toOSString());
   }

   private void generateLowLevelToSubsystemTrace() throws IOException, OseeCoreException {
      excelWriter.startSheet("5.1", 7);

      excelWriter.writeRow("5.1  " + reqtypeName + "s Traceability to Subsystem Requirements");
      excelWriter.writeRow(reqtypeName, null, null, "Traceable Subsystem Requirement");
      excelWriter.writeRow("Paragraph #", "Paragraph Title", "Qualification Method", "PIDS", "Paragraph #",
            "Paragraph Title", Requirements.SUBSYSTEM);

      String[] row = new String[7];

      for (Artifact lowLevelReq : lowLevelReqs) {
         row[0] = correct(lowLevelReq.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, ""));
         row[1] = lowLevelReq.getName();
         if (lowLevelReq.isOfType(reqtypeName)) {
            row[2] = lowLevelReq.getAttributesToString("Qualification Method");

            for (Artifact subSysReq : lowLevelReq.getRelatedArtifacts(CoreRelationTypes.Requirement_Trace__Higher_Level)) {
               row[3] = getAssociatedSubSystem(subSysReq);
               row[4] = correct(subSysReq.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, ""));
               row[5] = subSysReq.getName();
               row[6] = subSysReq.getSoleAttributeValue(Requirements.SUBSYSTEM, "");
               excelWriter.writeRow(row);
               row[0] = row[1] = row[2] = null;
            }
         } else {
            row[2] = lowLevelReq.getArtifactTypeName();
         }

         if (row[0] != null) { // if this requirement is not traced to any lower level req (i.e. the for loop didn't run)
            row[3] = row[4] = row[5] = row[6] = null;
            excelWriter.writeRow(row);
         }
      }

      excelWriter.endSheet();
   }

   private void generateSubsystemToLowLevelReqTrace() throws IOException, OseeCoreException {
      excelWriter.startSheet("5.2", 6);

      excelWriter.writeRow("5.2  Subsystem Requirements Allocation Traceability to " + reqtypeName + "s");
      excelWriter.writeRow();

      int count = 1;
      for (Entry<String, List<Artifact>> entry : subsysToSubsysReqsMap.entrySet()) {
         String subSysName = entry.getKey();
         List<Artifact> subsysReqs = entry.getValue();

         excelWriter.writeRow();
         excelWriter.writeRow();
         excelWriter.writeRow("5.2." + count++ + " " + subSysName + " Requirements Allocation Traceability to " + reqtypeName + "s");
         excelWriter.writeRow(Requirements.SUBSYSTEM_REQUIREMENT, null, "Traceable " + reqtypeName, null);
         excelWriter.writeRow("Paragraph #", "Paragraph Title", "Paragraph #", "Paragraph Title");

         String[] row = new String[4];

         for (Artifact higherLevelReq : subsysReqs) {
            if (isAllocated(higherLevelReq)) {
               row[0] = correct(higherLevelReq.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, ""));
               row[1] = higherLevelReq.getName();

               for (Artifact lowerLevelReq : higherLevelReq.getRelatedArtifacts(CoreRelationTypes.Requirement_Trace__Lower_Level)) {
                  if (lowLevelReqs.contains(lowerLevelReq)) {
                     row[2] = correct(lowerLevelReq.getSoleAttributeValue(CoreAttributeTypes.PARAGRAPH_NUMBER, ""));
                     row[3] = lowerLevelReq.getName();
                     excelWriter.writeRow(row);
                     row[0] = row[1] = null;
                  }
               }
               if (row[0] != null) { // if this requirement is not traced to any low level requirement(i.e. the for loop didn't run)
                  row[2] = row[3] = null;
                  excelWriter.writeRow(row);
               }
            }
         }
      }
      excelWriter.endSheet();
   }

   private boolean isAllocated(Artifact higherLevelReq) throws OseeCoreException {
      for (Artifact component : higherLevelReq.getRelatedArtifacts("Allocation")) {
         if (components.contains(component)) {
            return true;
         }
      }
      return false;
   }

   private void initLowLevelRequirements(List<Artifact> artifacts) throws OseeCoreException {
      RelationManager.getRelatedArtifacts(artifacts, 999, true, CoreRelationTypes.Default_Hierarchical__Child);
      for (Artifact artifact : artifacts) {
         if (!artifact.isOfType("Folder")) {
            lowLevelReqs.add(artifact);
         }
         lowLevelReqs.addAll(artifact.getDescendants());
      }
   }

   private void initAllocationComponents(List<Artifact> artifacts) throws OseeCoreException {
      RelationManager.getRelatedArtifacts(artifacts, 999, true, CoreRelationTypes.Default_Hierarchical__Child);
      for (Artifact artifact : artifacts) {
         if (!artifact.isOfType("Folder")) {
            components.add(artifact);
         }
         components.addAll(artifact.getDescendants());
      }
   }

   private String correct(String value) {
      return value.equals("") ? null : value;
   }

   private String getAssociatedSubSystem(Artifact subSysReq) {
      for (Entry<String, List<Artifact>> entry : subsysToSubsysReqsMap.entrySet()) {
         String subSysName = entry.getKey();
         List<Artifact> subsysReqs = entry.getValue();
         if (subsysReqs.contains(subSysReq)) {
            return subSysName;
         }
      }
      return "N/A";
   }

   private void orderSubsystemReqs(Artifact subsysTopFolder) throws OseeCoreException {
      for (Artifact subsysFolder : subsysTopFolder.getChildren()) {
         String subSysName = subsysFolder.getName();
         List<Artifact> subsysReqs = subsysFolder.getDescendants();
         subsysToSubsysReqsMap.put(subSysName, subsysReqs);
      }
   }

   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" toolTip=\"Select a requirements branch.\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Lower Level Requirements\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Allocation Components\" /><XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"Low Level Requirement Type\" defaultValue=\"Software Requirement\" /></xWidgets>";
   }

   @Override
   public Collection<String> getCategories() {
      return Arrays.asList("Define.Publish");
   }
}