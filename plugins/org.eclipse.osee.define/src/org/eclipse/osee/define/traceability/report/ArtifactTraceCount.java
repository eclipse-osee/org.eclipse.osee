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
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.IRelationEnumeration;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.AttributeType;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTraceCount extends AbstractArtifactRelationReport {

   public ArtifactTraceCount() {
      super();
   }

   public String[] getHeader() {
      List<String> header = new ArrayList<String>();
      header.add("Name");
      header.add("Type");
      for (IRelationEnumeration relation : getRelationsToCheck()) {
         header.add(relation.getName() + " Trace Count");
      }
      header.add("Subsystem");
      return header.toArray(new String[header.size()]);
   }

   private String getSubsystemAttributeType(Artifact artifact) {
      Collection<String> attributeTypes = new ArrayList<String>();
      try {
         for (AttributeType type : artifact.getAttributeTypes()) {
            attributeTypes.add(type.getName());
         }
      } catch (Exception ex) {
         // Do Nothing;
      }

      if (attributeTypes.contains(Requirements.PARTITION)) {
         return Requirements.PARTITION;
      } else if (attributeTypes.contains(Requirements.CSCI)) {
         return Requirements.CSCI;
      }
      return EMPTY_STRING;
   }

   public void process(IProgressMonitor monitor) throws OseeCoreException {
      String[] header = getHeader();
      notifyOnTableHeader(header);
      IRelationEnumeration[] relations = getRelationsToCheck();
      for (Artifact art : getArtifactsToCheck()) {
         String[] rowData = new String[header.length];
         int index = 0;
         rowData[index++] = art.getName();
         rowData[index++] = art.getArtifactTypeName();
         for (IRelationEnumeration relationType : relations) {
            rowData[index++] = String.valueOf(art.getRelatedArtifactsCount(relationType));
         }
         String attributeType = getSubsystemAttributeType(art);
         if (Strings.isValid(attributeType)) {
            rowData[index++] = Collections.toString(",", art.getAttributesToStringList(attributeType));
         } else {
            rowData[index++] = "Unspecified";
         }
         notifyOnRowData(rowData);
      }
      notifyOnEndTable();
   }
}
