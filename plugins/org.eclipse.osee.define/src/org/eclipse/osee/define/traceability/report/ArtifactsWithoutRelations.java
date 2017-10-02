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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactsWithoutRelations extends AbstractArtifactRelationReport {

   public ArtifactsWithoutRelations() {
      super();
   }

   @Override
   public void process(IProgressMonitor monitor)  {
      notifyOnTableHeader("Item Name", "Type");
      RelationTypeSide[] relations = getRelationsToCheck();
      for (Artifact artifact : getArtifactsToCheck()) {
         int count = 0;
         for (RelationTypeSide relation : relations) {
            count += artifact.getRelatedArtifactsCount(relation);
         }
         if (count <= 0) {
            notifyOnRowData(artifact, artifact.getName(), artifact.getArtifactTypeName());
         }
      }
      notifyOnEndTable();
   }
}
