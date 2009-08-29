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
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.IRelationEnumeration;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactToRelatedArtifact extends AbstractArtifactRelationReport {

   public ArtifactToRelatedArtifact() {
      super();
   }

   public String[] getHeader() {
      List<String> toReturn = new ArrayList<String>();
      toReturn.add("Name");
      toReturn.add("Related By");
      toReturn.add("Requirement");
      return toReturn.toArray(new String[toReturn.size()]);
   }

   public List<String[]> getRelatedRows(IRelationEnumeration[] relations, Artifact artifact) throws OseeCoreException {
      List<String[]> toReturn = new ArrayList<String[]>();
      int maxSize = 0;
      List<List<String>> items = new ArrayList<List<String>>();
      for (IRelationEnumeration relationEnum : relations) {
         List<String> entries = new ArrayList<String>();
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
   public void process(IProgressMonitor monitor) throws OseeCoreException {
      notifyOnTableHeader(getHeader());
      IRelationEnumeration[] relations = getRelationsToCheck();
      for (Artifact artifact : getArtifactsToCheck()) {
         String name = artifact.getName();
         for (IRelationEnumeration relationEnum : relations) {
            String typeName = relationEnum.getName();
            for (Artifact relArtifact : artifact.getRelatedArtifacts(relationEnum)) {
               notifyOnRowData(name, typeName, relArtifact.getName());
            }
         }
      }
      notifyOnEndTable();
   }
}
