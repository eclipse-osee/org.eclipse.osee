/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.attribute;

import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IRelationType;

/**
 * @author Donald G. Dunne
 */
public final class RelationRow {

   private final BranchId branch;
   private Long rel_id;
   private IRelationType relationType;
   private Long a_art_id, b_art_id;
   private String rationale;
   private GammaId gamma_id;

   public RelationRow(BranchId branch, Long rel_id, IRelationType relationType, Long a_art_id, Long b_art_id, String rationale, GammaId gamma_id) {
      super();
      this.branch = branch;
      this.rel_id = rel_id;
      this.relationType = relationType;
      this.a_art_id = a_art_id;
      this.b_art_id = b_art_id;
      this.rationale = rationale;
      this.gamma_id = gamma_id;
   }

   public Long getA_art_id() {
      return a_art_id;
   }

   public void setA_art_id(Long a_art_id) {
      this.a_art_id = a_art_id;
   }

   public Long getB_art_id() {
      return b_art_id;
   }

   public void setB_art_id(Long b_art_id) {
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

   public Long getRel_id() {
      return rel_id;
   }

   public void setRel_id(Long rel_id) {
      this.rel_id = rel_id;
   }

   public IRelationType getRelationType() {
      return relationType;
   }

   public void setRelationType(IRelationType relationType) {
      this.relationType = relationType;
   }

}