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
package org.eclipse.osee.framework.core.data;

import java.util.Comparator;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.jdk.core.type.Id;

/**
 * @author Donald G. Dunne
 */
public class RelationLinkRow extends TransactionRow {
   private RelationId relationId;
   private String rationale;
   private final RelationTypeToken relationType;
   private boolean dirty;
   private final ArtifactId aArtId;
   private final ArtifactId bArtId;
   private ArtifactId relArtId;
   private int relOrder;
   private final ApplicabilityId applicabilityId;

   public RelationLinkRow(ArtifactId aArtId, ArtifactId bArtId, BranchId branch, RelationTypeToken relationType, //
      RelationId relationId, GammaId gammaId, String rationale, int relOrder, ArtifactId relArtId, ModificationType modType, ApplicabilityId applicabilityId) {
      this.relationType = relationType;
      this.relationId = relationId;
      this.rationale = rationale == null ? "" : rationale;
      this.aArtId = aArtId;
      this.bArtId = bArtId;
      this.applicabilityId = applicabilityId;
      this.branch = branch;
      this.modType = modType;
      this.gammaId = gammaId;
   }

   public RelationId getRelationId() {
      return relationId;
   }

   public void setRelationId(RelationId relationId) {
      this.relationId = relationId;
   }

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public boolean isDirty() {
      return dirty;
   }

   public void setDirty(boolean dirty) {
      this.dirty = dirty;
   }

   public ArtifactId getRelArtId() {
      return relArtId;
   }

   public void setRelArtId(ArtifactId relArtId) {
      this.relArtId = relArtId;
   }

   public int getRelOrder() {
      return relOrder;
   }

   public void setRelOrder(int relOrder) {
      this.relOrder = relOrder;
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public ApplicabilityId getApplicabilityId() {
      return applicabilityId;
   }

   public ArtifactId getaArtId() {
      return aArtId;
   }

   public ArtifactId getbArtId() {
      return bArtId;
   }

   @Override
   public Id getItemId() {
      return getRelationId();
   }

   public static final class RelationLinkRowComparator implements Comparator<RelationLinkRow> {
      private static final Comparator<RelationLinkRow> COMPARATOR = Comparator.comparing( //
         (RelationLinkRow r) -> r.getRelationType().getId()) //
         .thenComparing(r -> r.getRelationId().getId()) //
         .thenComparing(r -> r.getTx().getId()) //
         .thenComparing(r -> r.getGammaId().getId()) //
         .thenComparing(r -> r.getModType().getId());

      @Override
      public int compare(RelationLinkRow a, RelationLinkRow b) {
         return COMPARATOR.compare(a, b);
      }
   }

}