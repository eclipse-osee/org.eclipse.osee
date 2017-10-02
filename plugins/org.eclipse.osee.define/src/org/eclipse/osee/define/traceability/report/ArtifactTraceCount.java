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
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactTraceCount extends AbstractArtifactRelationReport {

   public ArtifactTraceCount() {
      super();
   }

   public String[] getHeader() {
      List<String> header = new ArrayList<>();
      header.add("Name");
      header.add("Type");
      for (RelationTypeSide relation : getRelationsToCheck()) {
         header.add(relation.getName() + " Trace Count");
      }
      header.add("Subsystem");
      return header.toArray(new String[header.size()]);
   }

   private AttributeTypeId getSubsystemAttributeType(Artifact artifact) {
      for (AttributeTypeId attributeType : artifact.getAttributeTypes()) {
         if (attributeType.equals(CoreAttributeTypes.Partition)) {
            return CoreAttributeTypes.Partition;
         } else if (attributeType.equals(CoreAttributeTypes.Csci)) {
            return CoreAttributeTypes.Csci;
         }
      }
      return null;
   }

   @Override
   public void process(IProgressMonitor monitor) {
      String[] header = getHeader();
      notifyOnTableHeader(header);
      RelationTypeSide[] relations = getRelationsToCheck();
      for (Artifact art : getArtifactsToCheck()) {
         String[] rowData = new String[header.length];
         int index = 0;
         rowData[index++] = art.getName();
         rowData[index++] = art.getArtifactTypeName();
         for (RelationTypeSide relationType : relations) {
            rowData[index++] = String.valueOf(art.getRelatedArtifactsCount(relationType));
         }
         AttributeTypeId attributeType = getSubsystemAttributeType(art);
         if (attributeType == null) {
            rowData[index++] = "Unspecified";
         } else {
            rowData[index++] = Collections.toString(",", art.getAttributesToStringList(attributeType));
         }
         notifyOnRowData(art, rowData);
      }
      notifyOnEndTable();
   }
}
