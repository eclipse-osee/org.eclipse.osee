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
package org.eclipse.osee.framework.skynet.core.event;

import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.model.RelationType;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.ui.plugin.event.UnloadedRelation;

/**
 * @author Donald G. Dunne
 */
public class LoadedRelation {

   private final Artifact artifactA;
   private final Artifact artifactB;
   private final UnloadedRelation unloadedRelation;
   private final RelationType relationType;
   private final Branch branch;

   public LoadedRelation(Artifact artifactA, Artifact artifactB, RelationType relationType, Branch branch, UnloadedRelation unloadedRelation) {
      this.artifactA = artifactA;
      this.artifactB = artifactB;
      this.relationType = relationType;
      this.branch = branch;
      this.unloadedRelation = unloadedRelation;
   }

   @Override
   public String toString() {
      return "LoadedRelation - ArtA: " + this.artifactA + " - ArtB: " + this.artifactB + " - RelType: " + this.relationType + " - " + (unloadedRelation != null ? unloadedRelation.toString() : "");
   }

   /**
    * @return the artifactA
    */
   public Artifact getArtifactA() {
      return artifactA;
   }

   /**
    * @return the artifactB
    */
   public Artifact getArtifactB() {
      return artifactB;
   }

   /**
    * @return the unloadedRelation
    */
   public UnloadedRelation getUnloadedRelation() {
      return unloadedRelation;
   }

   /**
    * @return the relationType
    */
   public RelationType getRelationType() {
      return relationType;
   }

   /**
    * @return the branch
    */
   public Branch getBranch() {
      return branch;
   }

}
