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
public class ArtifactRow extends TransactionRow {

   private final ArtifactId artId;
   private final ArtifactTypeToken artType;
   private Integer txsRowCount = null;

   public ArtifactRow(BranchId branch, GammaId gammaId, ArtifactId artId, ModificationType modType, ArtifactTypeToken artType) {
      this.branch = branch;
      this.gammaId = gammaId;
      this.artId = artId;
      this.modType = modType;
      this.artType = artType;
   }

   public ArtifactId getArtId() {
      return artId;
   }

   @Override
   public String toString() {
      return "Artifact: [artId=" + artId + ", type=" + artType + ", gammaId=" + gammaId + ", artId=" + artId + ", modType=" + modType + "]";
   }

   public ArtifactTypeToken getArtType() {
      return artType;
   }

   public Integer getTxsRowCount() {
      return txsRowCount;
   }

   public void setTxsRowCount(Integer txsRowCount) {
      this.txsRowCount = txsRowCount;
   }

   @Override
   public Id getItemId() {
      return getArtId();
   }

   public static final class ArtifactRowComparator implements Comparator<ArtifactRow> {
      private static final Comparator<ArtifactRow> COMPARATOR = Comparator.comparing( //
         (ArtifactRow r) -> r.getArtId().getId()) //
         .thenComparing(r -> r.getTx().getId()) //
         .thenComparing(r -> r.getGammaId().getId()) //
         .thenComparing(r -> r.getModType().getId());

      @Override
      public int compare(ArtifactRow a, ArtifactRow b) {
         return COMPARATOR.compare(a, b);
      }
   }

}
