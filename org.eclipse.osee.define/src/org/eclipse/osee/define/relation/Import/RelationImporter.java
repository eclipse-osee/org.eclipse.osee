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
package org.eclipse.osee.define.relation.Import;

import static org.eclipse.osee.framework.skynet.core.artifact.search.Operator.EQUAL;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.AttributeValueSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ISearchPrimitive;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class RelationImporter implements RowProcessor {
   private static final ArtifactPersistenceManager artifactManager = ArtifactPersistenceManager.getInstance();
   private static final int leadingColumnCount = 2;
   private final ExcelSaxHandler excelHandler;
   private final XMLReader xmlReader;
   private Artifact[] columnArtifacts;
   private IProgressMonitor monitor;
   private boolean done;
   private final Branch branch;

   /**
    * @throws SAXException
    */
   public RelationImporter(Branch branch) throws SAXException {
      this.branch = branch;
      excelHandler = new ExcelSaxHandler(this, true, true);

      xmlReader = XMLReaderFactory.createXMLReader();
      xmlReader.setContentHandler(excelHandler);
   }

   public void extractRelationsFromSheet(InputStream importStream, IProgressMonitor monitor) throws IOException, SAXException {
      this.monitor = monitor;
      xmlReader.parse(new InputSource(importStream));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.define.artifact.Import.RowProcessor#processRow(java.lang.String[])
    */
   public void processRow(String[] row) {
      if (done) return;
      try {
         monitor.worked(1);
         List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
         criteria.add(new ArtifactTypeSearch("System Requirement", EQUAL));
         criteria.add(new AttributeValueSearch("Imported Paragraph Number", row[1], EQUAL));
         Collection<Artifact> artifacts = artifactManager.getArtifacts(criteria, true, branch);

         Artifact rowArtifact;
         try {
            rowArtifact = getSoleArtifact(artifacts);
         } catch (IllegalArgumentException ex) {
            System.out.println(ex.getMessage());
            return;
         }

         if (rowArtifact == null) {
            System.out.println("Skipping " + row[0] + " becuase no matching artifact was found");
         } else {
            if (!row[0].equals(rowArtifact.getDescriptiveName())) {
               System.out.printf("Warning %s != %s%n", row[0], rowArtifact.getDescriptiveName());
            }
            monitor.subTask(rowArtifact.getDescriptiveName());
            for (int i = 0; i < columnArtifacts.length; i++) {
               String rationale = row[i + leadingColumnCount];
               if (rationale != null) {
                  if (rationale.equalsIgnoreCase("x")) {
                     rationale = "";
                  }
                  columnArtifacts[i].relate(RelationSide.ALLOCATION__REQUIREMENT, rowArtifact, rationale, true);
               }
            }
         }
      } catch (SQLException ex) {
         System.out.println(ex);
      }
   }

   private Artifact getSoleArtifact(Collection<Artifact> artifacts) {
      Artifact artifactResult = null;
      boolean soleArtifact = true;
      for (Artifact artifact : artifacts) {
         if (soleArtifact) {
            soleArtifact = false;
         } else {
            throw new IllegalArgumentException("Found more than one match for: " + artifact);
         }
         artifactResult = artifact;
      }
      return artifactResult;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.define.artifact.Import.RowProcessor#processHeaderRow(java.lang.String[])
    */
   public void processHeaderRow(String[] row) {
      monitor.setTaskName("Aquire Column Artifacts");
      List<ISearchPrimitive> criteria = new LinkedList<ISearchPrimitive>();
      columnArtifacts = new Artifact[row.length - leadingColumnCount];
      for (int i = 0; i < columnArtifacts.length; i++) {
         monitor.worked(1);
         try {
            criteria.add(new ArtifactTypeSearch("Component", EQUAL));
            criteria.add(new AttributeValueSearch("Name", row[i + leadingColumnCount], EQUAL));
            Collection<Artifact> artifacts = artifactManager.getArtifacts(criteria, true, branch);
            criteria.clear();

            columnArtifacts[i] = getSoleArtifact(artifacts);
            monitor.subTask(columnArtifacts[i].getDescriptiveName());
         } catch (SQLException ex) {
            System.out.println(ex);
         }
      }
      System.out.println(Arrays.deepToString(columnArtifacts));
      monitor.setTaskName("Relate Row Artifacts");
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.define.artifact.Import.RowProcessor#processEmptyRow()
    */
   public void processEmptyRow() {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.define.artifact.Import.RowProcessor#processCommentRow(java.lang.String[])
    */
   public void processCommentRow(String[] row) {
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.define.artifact.Import.RowProcessor#reachedEndOfWorksheet()
    */
   public void reachedEndOfWorksheet() {
      monitor.done();
      done = true;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.define.artifact.Import.RowProcessor#detectedTotalRowCount(int)
    */
   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
      monitor.beginTask("Importing Relations", rowCount + columnCount - leadingColumnCount);
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.define.artifact.Import.RowProcessor#foundStartOfWorksheet(java.lang.String)
    */
   public void foundStartOfWorksheet(String sheetName) {
   }
}
