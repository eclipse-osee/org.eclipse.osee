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
package org.eclipse.osee.framework.skynet.core.importing.operations;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughRelation;

/**
 * @author Roberto E. Escobar
 */
public class RoughArtifactCollector {

   private final List<RoughArtifact> roughArtifacts;
   private final List<RoughRelation> roughRelations;
   private final RoughArtifact parentRoughArtifact;

   public RoughArtifactCollector(RoughArtifact parentRoughArtifact) {
      this.parentRoughArtifact = parentRoughArtifact;
      roughArtifacts = new ArrayList<RoughArtifact>();
      roughRelations = new ArrayList<RoughRelation>();
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

}
