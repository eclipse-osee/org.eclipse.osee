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
package org.eclipse.osee.define.traceability.report;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.define.internal.Activator;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactToRelatedArtifact extends AbstractArtifactRelationReport {

   public ArtifactToRelatedArtifact() {
      super();
   }

   public String[] getHeader() {
      List<String> toReturn = new ArrayList<>();
      toReturn.add("Name");
      toReturn.add("Related By");
      toReturn.add("Requirement");
      return toReturn.toArray(new String[toReturn.size()]);
   }

   public List<String[]> getRelatedRows(RelationTypeSide[] relations, Artifact artifact) {
      List<String[]> toReturn = new ArrayList<>();
      int maxSize = 0;
      List<List<String>> items = new ArrayList<>();
      for (RelationTypeSide relationEnum : relations) {
         List<String> entries = new ArrayList<>();
         for (Artifact relArtifact : artifact.getRelatedArtifacts(relationEnum)) {
            entries.add(relArtifact.getName());
         }
         items.add(entries);
         maxSize = Math.max(maxSize, entries.size());
      }
      String unitName = artifact.getName();
      int width = relations.length;
      for (int rowNumber = 0; rowNumber < maxSize; rowNumber++) {
         String[] row = new String[width + 1];
         row[0] = unitName;
         for (int index = 0; index < width; index++) {
            List<String> entry = items.get(index);
            row[index + 1] = rowNumber < entry.size() ? entry.get(rowNumber) : EMPTY_STRING;
         }
         toReturn.add(row);
      }
      return toReturn;
   }

   @Override
   public void process(IProgressMonitor monitor) {
      notifyOnTableHeader(getHeader());
      RelationTypeSide[] relations = getRelationsToCheck();
      for (Artifact artifact : getArtifactsToCheck()) {
         String name = artifact.getName();
         for (RelationTypeSide relationEnum : relations) {
            String typeName = relationEnum.getName();
            try {
               for (Artifact relArtifact : artifact.getRelatedArtifacts(relationEnum)) {
                  notifyOnRowData(artifact, name, typeName, relArtifact.getName());
               }
            } catch (OseeCoreException ex) {
               OseeLog.log(Activator.class, Level.WARNING, ex);
            }
         }
      }
      notifyOnEndTable();
   }
}
