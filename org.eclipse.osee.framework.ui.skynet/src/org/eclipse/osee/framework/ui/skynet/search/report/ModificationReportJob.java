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
package org.eclipse.osee.framework.ui.skynet.search.report;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.revision.RevisionManager;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;
import org.eclipse.swt.program.Program;

/**
 * @author Robert A. Fisher
 */
public class ModificationReportJob extends ReportJob {
   private final HashMap<Artifact, String[]> matrix;
   private final String[] header = new String[] {"Artifact Name", "HRID", "Modification Count"};

   public ModificationReportJob() {
      super("Modification Report");
      matrix = new HashMap<Artifact, String[]>();
   }

   @Override
   public void generateReport(List<Artifact> selectedArtifacts, IProgressMonitor monitor) throws OseeCoreException {
      matrix.clear();

      int columnIndex = 2;
      for (Artifact artifact : selectedArtifacts) {
         if (artifact != null) {
            processArtifact(artifact, monitor);
         }
         columnIndex++;
      }
      writeMatrix();
   }

   private void processArtifact(Artifact artifact, IProgressMonitor monitor) throws OseeCoreException {
      monitor.subTask("Processing " + artifact.getDescriptiveName());

      String[] row = new String[3];

      row[0] = artifact.getDescriptiveName();
      row[1] = artifact.getHumanReadableId();
      row[2] = Integer.toString(RevisionManager.getInstance().getTransactionsPerArtifact(artifact).size());

      matrix.put(artifact, row);

      // Recursively process the full DH tree
      for (Artifact child : artifact.getChildren())
         processArtifact(child, monitor);
   }

   private void writeMatrix() throws OseeCoreException {
      try {
         CharBackedInputStream charBak = new CharBackedInputStream();
         ExcelXmlWriter excelWriter = new ExcelXmlWriter(charBak.getWriter());
         excelWriter.startSheet("Modification Report", 3);

         excelWriter.writeRow(header);
         for (String[] row : matrix.values()) {
            excelWriter.writeRow(row);
         }
         excelWriter.endWorkbook();

         IFile iFile = OseeData.getIFile("Modification_Report_" + Lib.getDateTimeString() + ".xml");
         AIFile.writeToFile(iFile, charBak);

         // Ensure Excel is used since the file assocation for xml could be off
         Program.findProgram("xls").execute(iFile.getLocation().toOSString());
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }
}
