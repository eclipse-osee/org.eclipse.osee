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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;

/**
 * @author Ryan D. Brooks
 */
public abstract class AbstractArtifactExtractor implements ArtifactExtractor {
   private final ArrayList<RoughArtifact> roughArtifacts;
   private final ArrayList<RoughRelation> roughRelations;
   private final Branch branch;

   public AbstractArtifactExtractor(Branch branch) {
      super();
      this.roughArtifacts = new ArrayList<RoughArtifact>();
      this.roughRelations = new ArrayList<RoughRelation>();
      this.branch = branch;
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.ArtifactExtractor#getRoughArtifacts()
    */
   public List<RoughArtifact> getRoughArtifacts() {
      return roughArtifacts;
   }

   /* (non-Javadoc)
    * @see osee.define.artifact.Import.ArtifactExtractor#getRoughRelations(osee.define.artifact.Import.RoughArtifact)
    */
   public List<RoughRelation> getRoughRelations(RoughArtifact parent) throws Exception {
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

   private void determineParentChildRelations() throws SQLException {
      for (RoughArtifact roughArtifact : roughArtifacts) {
         if (roughArtifact.hasHierarchicalRelation()) {
            determineParentChildRelationsFor(roughArtifact);
         }
      }
   }

   private void determineParentChildRelationsFor(RoughArtifact roughReq) throws SQLException {
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

   /**
    * @return the branch
    */
   public Branch getBranch() {
      return branch;
   }
}