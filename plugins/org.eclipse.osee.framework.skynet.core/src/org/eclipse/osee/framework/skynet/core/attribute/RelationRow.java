/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Donald G. Dunne
 */
public final class RelationRow {

   private final BranchId branch;
   private RelationId rel_id;
   private RelationTypeToken relationType;
   private ArtifactId a_art_id, b_art_id;
   private String rationale;
   private GammaId gamma_id;

   public RelationRow(BranchId branch, RelationId rel_id, RelationTypeToken relationType, ArtifactId a_art_id, ArtifactId b_art_id, String rationale, GammaId gamma_id) {
      super();
      this.branch = branch;
      this.rel_id = rel_id;
      this.relationType = relationType;
      this.a_art_id = a_art_id;
      this.b_art_id = b_art_id;
      this.rationale = rationale;
      this.gamma_id = gamma_id;
   }

   public ArtifactId getA_art_id() {
      return a_art_id;
   }

   public void setA_art_id(ArtifactId a_art_id) {
      this.a_art_id = a_art_id;
   }

   public ArtifactId getB_art_id() {
      return b_art_id;
   }

   public void setB_art_id(ArtifactId b_art_id) {
      this.b_art_id = b_art_id;
   }

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public GammaId getGamma_id() {
      return gamma_id;
   }

   public void setGamma_id(GammaId gamma_id) {
      this.gamma_id = gamma_id;
   }

   public BranchId getBranch() {
      return branch;
   }

   public RelationId getRel_id() {
      return rel_id;
   }

   public void setRel_id(RelationId rel_id) {
      this.rel_id = rel_id;
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public void setRelationType(RelationTypeToken relationType) {
      this.relationType = relationType;
   }

}