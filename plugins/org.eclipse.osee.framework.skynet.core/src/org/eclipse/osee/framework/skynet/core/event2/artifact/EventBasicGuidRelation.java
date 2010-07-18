/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.skynet.core.event2.artifact;

import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author Donald G. Dunne
 */
public class EventBasicGuidRelation extends DefaultBasicGuidRelation {

   private final RelationEventType relationEventType;
   private final int artAId;
   private final int artBId;
   private String rationale;

   public EventBasicGuidRelation(RelationEventType relationEventType, int artAId, int artBId, DefaultBasicGuidRelation guidRel) {
      this(relationEventType, guidRel.getBranchGuid(), guidRel.getRelTypeGuid(), guidRel.getRelationId(),
            guidRel.getGammaId(), artAId, guidRel.getArtA(), artBId, guidRel.getArtB());
   }

   public EventBasicGuidRelation(RelationEventType relationEventType, String branchGuid, String relTypeGuid, int relationId, int gammaId, int artAId, DefaultBasicGuidArtifact artA, int artBId, DefaultBasicGuidArtifact artB) {
      super(branchGuid, relTypeGuid, relationId, gammaId, artA, artB);
      this.relationEventType = relationEventType;
      this.artAId = artAId;
      this.artBId = artBId;
   }

   public RelationEventType getModType() {
      return relationEventType;
   }

   public String toString() {
      return String.format("[%s - B:%s - TG:%s - GI:%d - RI:%d - A:%s - B:%s]", relationEventType, getBranchGuid(),
            getBranchGuid(), getGammaId(), getRelationId(), getArtA(), getArtB());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((relationEventType == null) ? 0 : relationEventType.hashCode());
      result = prime * result + artAId;
      result = prime * result + ((getArtA() == null) ? 0 : getArtA().hashCode());
      result = prime * result + artBId;
      result = prime * result + ((getArtB() == null) ? 0 : getArtB().hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) return true;
      if (!super.equals(obj)) return false;
      if (getClass() != obj.getClass()) return false;
      EventBasicGuidRelation other = (EventBasicGuidRelation) obj;
      if (relationEventType == null) {
         if (other.relationEventType != null) return false;
      } else if (!relationEventType.equals(other.relationEventType)) return false;
      if (artAId != other.artAId) return false;
      if (getArtA() == null) {
         if (other.getArtA() != null) return false;
      } else if (!getArtA().equals(other.getArtA())) return false;
      if (artBId != other.artBId) return false;
      if (getArtB() == null) {
         if (other.getArtB() != null) return false;
      } else if (!getArtB().equals(other.getArtB())) return false;
      return true;
   }

   public int getArtAId() {
      return artAId;
   }

   public int getArtBId() {
      return artBId;
   }

   public String getRationale() {
      return rationale;
   }

   public void setRationale(String rationale) {
      this.rationale = rationale;
   }

   public boolean is(RelationEventType... relationEventTypes) {
      for (RelationEventType eventModType : relationEventTypes) {
         if (this.relationEventType == eventModType) return true;
      }
      return false;
   }

}
