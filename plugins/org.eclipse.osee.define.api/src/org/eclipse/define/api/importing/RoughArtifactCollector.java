/*******************************************************************************
 * Copyright (c) 2019 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.define.api.importing;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * @author Roberto E. Escobar
 * @author David W. Miller
 */
public class RoughArtifactCollector {

   private final List<RoughArtifact> roughArtifacts;
   private final List<RoughRelation> roughRelations;
   private final RoughArtifact parentRoughArtifact;

   public RoughArtifactCollector(RoughArtifact parentRoughArtifact) {
      this.parentRoughArtifact = parentRoughArtifact;
      roughArtifacts = new ArrayList<>();
      roughRelations = new ArrayList<>();
   }

   public void reset() {
      if (parentRoughArtifact != null) {
         parentRoughArtifact.clear();
      }
      roughArtifacts.clear();
      roughRelations.clear();
   }

   public void addRoughArtifact(RoughArtifact roughArtifact) {
      roughArtifacts.add(roughArtifact);
   }

   public void addChildRoughArtifact(RoughArtifact roughArtifact) {
      parentRoughArtifact.addChild(roughArtifact);
      addRoughArtifact(roughArtifact);
   }

   public void addRoughRelation(RoughRelation roughRelation) {
      roughRelations.add(roughRelation);
   }

   public RoughArtifact getParentRoughArtifact() {
      return parentRoughArtifact;
   }

   public void addAllRoughArtifacts(Collection<RoughArtifact> roughArtifact) {
      roughArtifacts.addAll(roughArtifact);
   }

   public void addAllRoughRelations(Collection<RoughRelation> roughRelation) {
      roughRelations.addAll(roughRelation);
   }

   public List<RoughArtifact> getRoughArtifacts() {
      return roughArtifacts;
   }

   public List<RoughRelation> getRoughRelations() {
      return roughRelations;
   }

   public boolean removeArtifact(RoughArtifact roughArtifact) {
      return roughArtifacts.remove(roughArtifact);
   }

   @Override
   public String toString() {
      return roughArtifacts.toString();
   }
}
