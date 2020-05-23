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

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Roberto E. Escobar
 */
public abstract class AbstractArtifactRelationReport extends AbstractReport {

   private final Set<Artifact> artifactsToCheck;
   private final List<RelationTypeSide> relationsToCheck;

   public AbstractArtifactRelationReport() {
      super();
      this.artifactsToCheck = new HashSet<>();
      this.relationsToCheck = new ArrayList<>();
   }

   public void setArtifacts(Collection<Artifact> artifacts) {
      if (artifacts != null) {
         this.artifactsToCheck.addAll(artifacts);
      }
   }

   public void addRelationToCheck(RelationTypeSide relation) {
      if (relation != null) {
         this.relationsToCheck.add(relation);
      }
   }

   public Artifact[] getArtifactsToCheck() {
      return artifactsToCheck.toArray(new Artifact[artifactsToCheck.size()]);
   }

   public RelationTypeSide[] getRelationsToCheck() {
      return relationsToCheck.toArray(new RelationTypeSide[relationsToCheck.size()]);
   }

   @Override
   public void clear() {
      artifactsToCheck.clear();
      relationsToCheck.clear();
      super.clear();
   }
}
