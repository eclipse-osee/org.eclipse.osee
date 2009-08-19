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
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughArtifact;
import org.eclipse.osee.framework.skynet.core.importing.RoughRelation;

/**
 * @author Roberto E. Escobar
 */
public class RoughArtifactCollector {

   private final RoughArtifact rootRoughArtifact;
   private final List<RoughArtifact> roughArtifacts;
   private final List<RoughRelation> roughRelations;
   private final Artifact destinationArtifact;

   public RoughArtifactCollector(Artifact destinationArtifact) {
      this.destinationArtifact = destinationArtifact;
      this.rootRoughArtifact = null; // TODO fix this
      //      new RoughArtifact(destinationArtifact);
      this.roughArtifacts = new ArrayList<RoughArtifact>();
      this.roughRelations = new ArrayList<RoughRelation>();
   }

   public Artifact getDestinationArtifact() {
      return destinationArtifact;
   }

   public RoughArtifact getRootRoughArtifact() {
      return rootRoughArtifact;
   }

   public void addRoughArtifact(RoughArtifact roughArtifact) {
      roughArtifacts.add(roughArtifact);
   }

   public void addRoughRelation(RoughRelation roughRelation) {
      roughRelations.add(roughRelation);
   }

   public void addAllRoughArtifacts(Collection<RoughArtifact> artifacts) {
      roughArtifacts.addAll(artifacts);
   }

   public void addAllRoughRelations(Collection<RoughRelation> relations) {
      roughRelations.addAll(relations);
   }

   public List<RoughArtifact> getRoughArtifacts() {
      return roughArtifacts;
   }

   public List<RoughRelation> getRoughRelations() {
      return roughRelations;
   }
}
