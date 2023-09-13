/*********************************************************************
 * Copyright (c) 2022 Boeing
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
package org.eclipse.osee.accessor.types;

import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

public class RelatedArtifact {

   private RelationTypeToken relation;
   private ArtifactId relatedId;
   private String side;
   public static RelatedArtifact SENTINEL = new RelatedArtifact();
   public RelatedArtifact() {
      this.setRelation(RelationTypeToken.SENTINEL);
      this.setRelatedId(ArtifactId.SENTINEL);
      this.setSide("");
   }

   /**
    * @return the relatedId
    */
   public ArtifactId getRelatedId() {
      return relatedId;
   }

   /**
    * @param relatedId the relatedId to set
    */
   public void setRelatedId(ArtifactId relatedId) {
      this.relatedId = relatedId;
   }

   /**
    * @return the relation
    */
   public RelationTypeToken getRelation() {
      return relation;
   }

   /**
    * @param relation the relation to set
    */
   public void setRelation(RelationTypeToken relation) {
      this.relation = relation;
   }

   /**
    * @return the isSideA
    */
   public String getSide() {
      return side;
   }

   /**
    * @param isSideA the isSideA to set
    */
   public void setSide(String side) {
      this.side = side;
   }

}
