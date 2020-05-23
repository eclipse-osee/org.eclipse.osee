/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.ide.traceability.report;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public class ArtifactsWithoutRelations extends AbstractArtifactRelationReport {

   public ArtifactsWithoutRelations() {
      super();
   }

   @Override
   public void process(IProgressMonitor monitor) {
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
