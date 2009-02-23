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
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactType;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.VariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;
import org.eclipse.swt.program.Program;

/**
 * @author Ryan D. Brooks
 */
public class SubsystemToLowLevelReqTraceReport extends AbstractBlam {
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;
   private final HashMap<String, List<Artifact>> subsysToSubsysReqsMap;
   private List<Artifact> lowLevelReqs;
   private HashSet<Artifact> components;
   private String reqtypeName;

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

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.VariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch, org.eclipse.core.runtime.IProgressMonitor)
    */
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
      ArtifactQuery.getArtifactsFromType(Requirements.SUBSYSTEM_REQUIREMENT, branch);
      monitor.worked(30);

      Artifact root = ArtifactPersistenceManager.getDefaultHierarchyRootArtifact(branch);
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
         row[0] = correct(lowLevelReq.getSoleAttributeValue("Imported Paragraph Number", ""));
         row[1] = lowLevelReq.getDescriptiveName();
         if (lowLevelReq.isOfType(reqtypeName)) {
            row[2] = lowLevelReq.getAttributesToString("Qualification Method");

            for (Artifact subSysReq : lowLevelReq.getRelatedArtifacts(CoreRelationEnumeration.REQUIREMENT_TRACE__HIGHER_LEVEL)) {
               row[3] = getAssociatedSubSystem(subSysReq);
               row[4] = correct(subSysReq.getSoleAttributeValue("Imported Paragraph Number", ""));
               row[5] = subSysReq.getDescriptiveName();
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
               row[0] = correct(higherLevelReq.getSoleAttributeValue("Imported Paragraph Number", ""));
               row[1] = higherLevelReq.getDescriptiveName();

               for (Artifact lowerLevelReq : higherLevelReq.getRelatedArtifacts(CoreRelationEnumeration.REQUIREMENT_TRACE__LOWER_LEVEL)) {
                  if (lowLevelReqs.contains(lowerLevelReq)) {
                     row[2] = correct(lowerLevelReq.getSoleAttributeValue("Imported Paragraph Number", ""));
                     row[3] = lowerLevelReq.getDescriptiveName();
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
      RelationManager.getRelatedArtifacts(artifacts, 999, true, CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);
      for (Artifact artifact : artifacts) {
         if (!artifact.isOfType("Folder")) {
            lowLevelReqs.add(artifact);
         }
         lowLevelReqs.addAll(artifact.getDescendants());
      }
   }

   private void initAllocationComponents(List<Artifact> artifacts) throws OseeCoreException {
      RelationManager.getRelatedArtifacts(artifacts, 999, true, CoreRelationEnumeration.DEFAULT_HIERARCHICAL__CHILD);
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
         String subSysName = subsysFolder.getDescriptiveName();
         List<Artifact> subsysReqs = subsysFolder.getDescendants();
         subsysToSubsysReqsMap.put(subSysName, subsysReqs);
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam#getXWidgetsXml()
    */
   @Override
   public String getXWidgetsXml() {
      return "<xWidgets><XWidget xwidgetType=\"XBranchSelectWidget\" displayName=\"Branch\" toolTip=\"Select a requirements branch.\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Lower Level Requirements\" /><XWidget xwidgetType=\"XListDropViewer\" displayName=\"Allocation Components\" /><XWidget xwidgetType=\"XArtifactTypeListViewer\" displayName=\"Low Level Requirement Type\" defaultValue=\"Software Requirement\" /></xWidgets>";
   }
}