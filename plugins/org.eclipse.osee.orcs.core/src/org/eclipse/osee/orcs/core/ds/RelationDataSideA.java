/*********************************************************************
 * Copyright (c) 2021 Boeing
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

package org.eclipse.osee.orcs.core.ds;

import java.util.Objects;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Audrey Denk
 */
public class RelationDataSideA {

   private final ArtifactId artA;
   private final RelationTypeToken relType;
   private int minOrder;
   private int maxOrder;
   public RelationDataSideA(ArtifactId artA, RelationTypeToken relType, int minOrder, int maxOrder) {
      this.artA = artA;
      this.relType = relType;
      this.minOrder = minOrder;
      this.maxOrder = maxOrder;
   }

   public ArtifactId getArtA() {
      return artA;
   }

   public RelationTypeToken getRelType() {
      return relType;
   }

   public int getMinOrder() {
      return minOrder;
   }

   public void setMinOrder(int minOrder) {
      this.minOrder = minOrder;
   }

   public int getMaxOrder() {
      return maxOrder;
   }

   public void setMaxOrder(int maxOrder) {
      this.maxOrder = maxOrder;
   }

   @Override
   public int hashCode() {
      return Objects.hash(artA.getIdString(), relType.getIdString());
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      if (getClass() != obj.getClass()) {
         return false;
      }
      RelationDataSideA other = (RelationDataSideA) obj;
      return Objects.equals(artA, other.artA) && Objects.equals(relType, other.relType);
   }

   @Override
   public String toString() {
      return "RelationOrderData [artA=" + artA.getIdString() + ", relType=" + relType.getIdString() + "]";
   }
}