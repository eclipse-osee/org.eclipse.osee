/*********************************************************************
 * Copyright (c) 2025 Boeing
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
package org.eclipse.osee.orcs.rest.internal.health.operations;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Donald G. Dunne
 */
public class RelationLink {

   ArtifactId aArt;
   ArtifactId bArt;
   RelationTypeToken relType;
   GammaId gamma;
   private RelationId relId;

   public RelationLink(RelationId relId, RelationTypeToken relType, ArtifactId aArt, ArtifactId bArt, GammaId gamma) {
      this.relId = relId;
      this.relType = relType;
      this.aArt = aArt;
      this.bArt = bArt;
      this.gamma = gamma;
   }

   public ArtifactId getaArt() {
      return aArt;
   }

   public void setaArt(ArtifactId aArt) {
      this.aArt = aArt;
   }

   public ArtifactId getbArt() {
      return bArt;
   }

   public void setbArt(ArtifactId bArt) {
      this.bArt = bArt;
   }

   public RelationTypeToken getRelType() {
      return relType;
   }

   public void setRelType(RelationTypeToken relType) {
      this.relType = relType;
   }

   public GammaId getGamma() {
      return gamma;
   }

   public void setGamma(GammaId gamma) {
      this.gamma = gamma;
   }

   public RelationId getRelId() {
      return relId;
   }

   public void setRelId(RelationId relId) {
      this.relId = relId;
   }

}
