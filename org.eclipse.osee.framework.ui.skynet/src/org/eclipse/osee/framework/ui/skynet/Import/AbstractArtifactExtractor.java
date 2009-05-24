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
package org.eclipse.osee.framework.ui.skynet.Import;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractArtifactExtractor implements ArtifactExtractor {
   private final ArrayList<RoughArtifact> roughArtifacts = new ArrayList<RoughArtifact>();
   private final ArrayList<RoughRelation> roughRelations = new ArrayList<RoughRelation>();

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.ArtifactExtractor#getRoughArtifacts()
    */
   public List<RoughArtifact> getRoughArtifacts() {
      return roughArtifacts;
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.ArtifactExtractor#getRoughRelations(osee.define.artifact.Import.RoughArtifact)
    */
   public List<RoughRelation> getRoughRelations(RoughArtifact parent) throws OseeCoreException {
      determineParentChildRelations();

      if (parent != null) {
         for (RoughArtifact roughArtifact : roughArtifacts) {
            if (!roughArtifact.hasParent()) {
               parent.addChild(roughArtifact);
            }
         }
      }
      return roughRelations;
   }

   private void determineParentChildRelations() {
      for (RoughArtifact roughArtifact : roughArtifacts) {
         if (roughArtifact.hasHierarchicalRelation()) {
            determineParentChildRelationsFor(roughArtifact);
         }
      }
   }

   private void determineParentChildRelationsFor(RoughArtifact roughReq) {
      // find all children and then save then by their order
      for (RoughArtifact otherRoughReq : roughArtifacts) {
         if (roughReq != otherRoughReq) { // don't compare to self
            if (roughReq.isChild(otherRoughReq)) {
               roughReq.addChild(otherRoughReq);
            }
         }
      }
   }

   public void addRoughArtifact(RoughArtifact roughArtifact) {
      roughArtifacts.add(roughArtifact);
   }

   public void addRoughRelation(RoughRelation roughRelation) {
      roughRelations.add(roughRelation);
   }
}