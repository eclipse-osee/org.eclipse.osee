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
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelXmlWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;

/**
 * @author Ryan D. Brooks
 */
public class RelationMatrixExportJob extends ReportJob {
   private final HashMap<Artifact, String[]> matrix;
   private final String relationTypeName;
   private RelationType relationType;
   private int columnCount;
   private String[] header;

   public RelationMatrixExportJob(RelationType relationType) {
      super(relationType.getTypeName() + " Report");
      this.relationTypeName = relationType.getTypeName();
      matrix = new HashMap<Artifact, String[]>();
      this.relationType = relationType;
   }

   @Override
   public void generateReport(List<Artifact> selectedArtifacts, IProgressMonitor monitor) throws OseeCoreException {
      matrix.clear();
      columnCount = selectedArtifacts.size() + 2; // use first column is the artifact name and 2nd is its identifier
      header = new String[columnCount];
      header[0] = "Artifact Name";
      header[1] = "Artifact ID";

      int columnIndex = 2;
      for (Artifact columnArtifact : selectedArtifacts) {
         if (columnArtifact != null) {
            saveRelationsForColumn(columnArtifact, columnIndex);
         }
         columnIndex++;
      }
      writeMatrix();
   }

   private void saveRelationsForColumn(Artifact columnArtifact, int columnIndex) throws OseeCoreException {
      header[columnIndex] = columnArtifact.getDescriptiveName();

      for (RelationLink relation : columnArtifact.getRelations(relationType)) {
         String[] row = getAssociatedRow(relation.getArtifactOnOtherSide(columnArtifact));
         if (relation.getRationale().equals("")) {
            row[columnIndex] = "X";
         } else {
            row[columnIndex] = relation.getRationale();
         }
      }
   }

   private String[] getAssociatedRow(Artifact artifact) throws OseeCoreException {
      String[] row = matrix.get(artifact);
      if (row == null) {
         row = new String[columnCount];
         row[0] = artifact.getDescriptiveName();
         row[1] = artifact.getSoleAttributeValue("Imported Paragraph Number", "");
         matrix.put(artifact, row);
      }
      return row;
   }

   private void writeMatrix() throws OseeCoreException {
      try {
         CharBackedInputStream charBak = new CharBackedInputStream();
         ExcelXmlWriter excelWriter = new ExcelXmlWriter(charBak.getWriter());
         excelWriter.startSheet(relationTypeName + " Matrix", columnCount);

         excelWriter.writeRow(header);
         for (String[] row : matrix.values()) {
            excelWriter.writeRow(row);
         }
         excelWriter.endWorkbook();

         IFile iFile = OseeData.getIFile(relationTypeName + ".xml");
         AIFile.writeToFile(iFile, charBak);
      } catch (IOException ex) {
         throw new OseeCoreException(ex);
      }
   }
}
