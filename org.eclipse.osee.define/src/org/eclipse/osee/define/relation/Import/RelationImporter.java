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

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.DefinePlugin;
import org.eclipse.osee.framework.jdk.core.util.io.xml.ExcelSaxHandler;
import org.eclipse.osee.framework.jdk.core.util.io.xml.RowProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.relation.CoreRelationEnumeration;
import org.eclipse.osee.framework.skynet.core.relation.order.RelationOrderBaseTypes;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

/**
 * @author Ryan D. Brooks
 */
public class RelationImporter implements RowProcessor {
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

   public void processRow(String[] row) {
      if (done) {
         return;
      }
      try {
         monitor.worked(1);
         Collection<Artifact> artifacts =
               ArtifactQuery.getArtifactListFromTypeAndAttribute(Requirements.SUBSYSTEM_REQUIREMENT,
                     "Imported Paragraph Number", row[1], branch);

         Artifact rowArtifact;
         try {
            rowArtifact = getSoleArtifact(artifacts);
         } catch (IllegalArgumentException ex) {
            OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
            return;
         }

         if (rowArtifact == null) {
            System.out.println("Skipping " + row[0] + " becuase no matching artifact was found");
         } else {
            if (!row[0].equals(rowArtifact.getName())) {
               System.out.printf("Warning %s != %s%n", row[0], rowArtifact.getName());
            }
            monitor.subTask(rowArtifact.getName());
            for (int i = 0; i < columnArtifacts.length; i++) {
               String rationale = row[i + leadingColumnCount];
               if (rationale != null) {
                  if (rationale.equalsIgnoreCase("x")) {
                     rationale = "";
                  }
                  columnArtifacts[i].addRelation(RelationOrderBaseTypes.USER_DEFINED,
                        CoreRelationEnumeration.ALLOCATION__REQUIREMENT, rowArtifact, rationale);
                  columnArtifacts[i].persist();
               }
            }
         }
      } catch (Exception ex) {
         OseeLog.log(DefinePlugin.class, Level.SEVERE, ex);
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

   public void processHeaderRow(String[] row) {
      monitor.setTaskName("Aquire Column Artifacts");
      columnArtifacts = new Artifact[row.length - leadingColumnCount];
      for (int i = 0; i < columnArtifacts.length; i++) {
         monitor.worked(1);
         try {
            Collection<Artifact> artifacts =
                  ArtifactQuery.getArtifactListFromTypeAndName(Requirements.COMPONENT, row[i + leadingColumnCount],
                        branch);

            columnArtifacts[i] = getSoleArtifact(artifacts);
            monitor.subTask(columnArtifacts[i].getName());
         } catch (Exception ex) {
            System.out.println(ex);
         }
      }
      System.out.println(Arrays.deepToString(columnArtifacts));
      monitor.setTaskName("Relate Row Artifacts");
   }

   public void processEmptyRow() {
   }

   public void processCommentRow(String[] row) {
   }

   public void reachedEndOfWorksheet() {
      monitor.done();
      done = true;
   }

   public void detectedRowAndColumnCounts(int rowCount, int columnCount) {
      monitor.beginTask("Importing Relations", rowCount + columnCount - leadingColumnCount);
   }

   public void foundStartOfWorksheet(String sheetName) {
   }
}
