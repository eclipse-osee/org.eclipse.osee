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
import java.sql.SQLException;
import java.util.Collection;
import java.util.LinkedList;
import java.util.TreeSet;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ISheetWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap;
import org.eclipse.osee.framework.ui.skynet.blam.operation.AbstractBlam;

/**
 * @author Ryan D. Brooks
 */
public class PublishSoftwareTestPlan extends AbstractBlam {
   private HashCollection<String, Artifact> swReqsByPartition;
   private CharBackedInputStream charBak;
   private ISheetWriter excelWriter;

   /**
    * 
    */
   public PublishSoftwareTestPlan() {
      swReqsByPartition = new HashCollection<String, Artifact>(false, LinkedList.class);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.ui.skynet.blam.operation.BlamOperation#runOperation(org.eclipse.osee.framework.ui.skynet.blam.BlamVariableMap, org.eclipse.osee.framework.skynet.core.artifact.Branch)
    */
   public void runOperation(BlamVariableMap variableMap, IProgressMonitor monitor) throws Exception {
      init();
      Branch branch = variableMap.getBranch("Branch");
      ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();

      monitor.beginTask("Publish Software Test Plan", IProgressMonitor.UNKNOWN);

      if (monitor.isCanceled()) return;

      monitor.subTask("Aquiring Software Requirements"); // bulk load for performance reasons
      artifactManager.getArtifactsFromSubtypeName("Software Requirement", branch);

      if (monitor.isCanceled()) return;

      Artifact root = artifactManager.getDefaultHierarchyRootArtifact(branch);
      Artifact swReqTopFolder = root.getChild("Software Requirements");
      recurseSwReqs(swReqTopFolder);

      publishTables();

      excelWriter.endWorkbook();
      IFile iFile = OseeData.getIFile("SDD_Traceability.xml");
      AIFile.writeToFile(iFile, charBak);
   }

   /**
    * @throws IOException
    */
   private void publishTables() throws IOException {
      TreeSet<String> partitions = new TreeSet<String>(swReqsByPartition.keySet());
      for (String partition : partitions) {
         excelWriter.writeRow();
         excelWriter.writeRow();
         excelWriter.writeRow(partition);
         excelWriter.writeRow("Requirement Name", "Qualification Method", "Test Level", "Qualification Facility");
         publishRequirementsTrace(swReqsByPartition.getValues(partition));
      }
   }

   private void publishRequirementsTrace(Collection<Artifact> requirements) throws IllegalStateException, IOException {
      for (Artifact requirement : requirements) {
         excelWriter.writeRow(requirement.getDescriptiveName(),
               requirement.getSoleAttributeValue("Qualification Method"));
      }
   }

   private void init() throws IOException {
      swReqsByPartition.clear();
      charBak = new CharBackedInputStream();
      excelWriter = new ExcelXmlWriter(charBak.getWriter());
      excelWriter.startSheet("traceability", 4);
   }

   private void recurseSwReqs(Artifact artifact) throws SQLException {
      for (Artifact child : artifact.getChildren()) {
         if (artifact.getArtifactTypeName().equals("Software Requirement")) {
            for (Attribute partition : child.getAttributeManager("Partition").getAttributes()) {
               swReqsByPartition.put(partition.getStringData(), child);
            }
         }
         recurseSwReqs(child);
      }
   }
}