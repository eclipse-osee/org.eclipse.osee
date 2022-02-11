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

package org.eclipse.osee.framework.skynet.core.event.model;

import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author David W. Miller, Torin Grenda
 */
public class EventTopicRelationTransfer {

   private RelationEventType relationEventType;
   private ArtifactToken artAId;
   private ArtifactTypeId artAIdType;
   private ArtifactToken artBId;
   private ArtifactTypeId artBIdType;
   private String rationale;
   private Long relTypeId;
   private GammaId gammaId;
   private RelationId relationId;
   private BranchId branch;

   public RelationEventType getRelationEventType() {
      return relationEventType;
   }

   public void setRelationEventType(RelationEventType relationEventType) {
      this.relationEventType = relationEventType;
   }

   public ArtifactTypeId getArtAIdType() {
      return artAIdType;
   }

   public void setArtAIdType(ArtifactTypeId artAIdType) {
      this.artAIdType = artAIdType;
   }

   public ArtifactTypeId getArtBIdType() {
      return artBIdType;
   }

   public void setArtBIdType(ArtifactTypeId artBIdType) {
      this.artBIdType = artBIdType;
   }

   public Long getRelTypeId() {
      return relTypeId;
   }

   public void setRelTypeId(Long relTypeId) {
      this.relTypeId = relTypeId;
   }

   public GammaId getGammaId() {
      return gammaId;
   }

   public void setGammaId(GammaId gammaId) {
      this.gammaId = gammaId;
   }

   public RelationId getRelationId() {
      return relationId;
   }

   public void setRelationId(RelationId relationId) {
      this.relationId = relationId;
   }

   public BranchId getBranch() {
      return branch;
   }

   public void setBranch(BranchId branch) {
      this.branch = branch;
   }

   public ArtifactToken getArtAId() {
      return artAId;
   }

   public void setArtAId(ArtifactToken artAId) {
      this.artAId = artAId;
   }

   public ArtifactToken getArtBId() {
      return artBId;
   }

   public void setArtBId(ArtifactToken artBId) {
      this.artBId = artBId;
   }

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   @Override
   public String toString() {
      try {
         return String.format("[%s - B:%s - TG:%s - GI:%s - RI:%s - A:%s - B:%s]", relationEventType,
            getBranch().getIdString(), getBranch().getIdString(), getGammaId(), getRelationId(), getArtAId(),
            getArtBId());
      } catch (Exception ex) {
         return String.format("EventTopicRelation exception: " + ex.getLocalizedMessage());
      }
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      long result = 1;
      result = prime * result + (rationale == null ? 0 : rationale.hashCode());
      result = prime * result + (getArtAId() == null ? 0 : getArtAId().hashCode());
      result = prime * result + (getArtBId() == null ? 0 : getArtBId().hashCode());
      return Math.toIntExact(result);
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }

      if (getClass() != obj.getClass()) {
         return false;
      }
      EventTopicRelationTransfer other = (EventTopicRelationTransfer) obj;
      if (relationEventType == null) {
         if (other.relationEventType != null) {
            return false;
         }
      } else if (!relationEventType.equals(other.relationEventType)) {
         return false;
      }

      if (getArtAId() == null) {
         if (other.getArtAId() != null) {
            return false;
         }
      } else if (!getArtAId().equals(other.getArtAId())) {
         return false;
      }

      if (getArtBId() == null) {
         if (other.getArtBId() != null) {
            return false;
         }
      } else if (!getArtBId().equals(other.getArtBId())) {
         return false;
      }
      return true;
   }

   public boolean is(RelationEventType... relationEventTypes) {
      for (RelationEventType eventModType : relationEventTypes) {
         if (this.relationEventType == eventModType) {
            return true;
         }
      }
      return false;
   }

}
