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
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.io.CharBackedInputStream;
import org.eclipse.osee.framework.jdk.core.util.io.xml.excel.ExcelXmlWriter;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLink;
import org.eclipse.osee.framework.skynet.core.relation.IRelationLinkDescriptor;
import org.eclipse.osee.framework.skynet.core.relation.LinkManager;
import org.eclipse.osee.framework.ui.plugin.util.AIFile;
import org.eclipse.osee.framework.ui.plugin.util.OseeData;

/**
 * @author Ryan D. Brooks
 */
public class RelationMatrixExportJob extends ReportJob {
   private final HashMap<Artifact, String[]> matrix;
   private final String relationTypeName;
   private IRelationLinkDescriptor relationType;
   private int columnCount;
   private String[] header;

   public RelationMatrixExportJob(IRelationLinkDescriptor relationType) {
      super(relationType.getName() + " Report");
      this.relationTypeName = relationType.getName();
      matrix = new HashMap<Artifact, String[]>();
      this.relationType = relationType;
   }

   @Override
   public void generateReport(List<Artifact> selectedArtifacts, IProgressMonitor monitor) throws CoreException, IOException, SQLException {
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

   private void saveRelationsForColumn(Artifact columnArtifact, int columnIndex) throws SQLException {
      header[columnIndex] = columnArtifact.getDescriptiveName();

      LinkManager linkManager = columnArtifact.getLinkManager();
      for (IRelationLink link : columnArtifact.getRelations(relationType)) {
         String[] row = getAssociatedRow(linkManager.getOtherSideAritfact(link));
         String rationale = link.getRationale();
         if (rationale == null || rationale.trim().equals("")) {
            row[columnIndex] = "X";
         } else {
            row[columnIndex] = rationale;
         }
      }
   }

   private String[] getAssociatedRow(Artifact artifact) {
      String[] row = matrix.get(artifact);
      if (row == null) {
         row = new String[columnCount];
         row[0] = artifact.getDescriptiveName();
         row[1] = artifact.getSoleAttributeValue("Imported Paragraph Number");
         matrix.put(artifact, row);
      }
      return row;
   }

   private void writeMatrix() throws IOException, CoreException {
      CharBackedInputStream charBak = new CharBackedInputStream();
      ExcelXmlWriter excelWriter = new ExcelXmlWriter(charBak.getWriter());
      excelWriter.startSheet(relationTypeName + " Matrix");

      excelWriter.writeRow(header);
      for (String[] row : matrix.values()) {
         excelWriter.writeRow(row);
      }
      excelWriter.endWorkbook();

      IFile iFile = OseeData.getIFile(relationTypeName + ".xml");
      AIFile.writeToFile(iFile, charBak);
   }
}
