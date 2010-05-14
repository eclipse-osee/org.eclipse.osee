/*
 * Created on Mar 24, 2010
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.event2.artifact;

import org.eclipse.osee.framework.core.data.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.data.DefaultBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;

/**
 * @author Donald G. Dunne
 */
public class EventBasicGuidRelation extends DefaultBasicGuidRelation {

   private final RelationEventType relationEventType;

   public EventBasicGuidRelation(RelationEventType relationEventType, DefaultBasicGuidRelation guidRel) {
      super(guidRel.getBranchGuid(), guidRel.getRelTypeGuid(), guidRel.getGammaId(), guidRel.getArtA(),
            guidRel.getArtB());
      this.relationEventType = relationEventType;
   }

   public EventBasicGuidRelation(RelationEventType relationEventType, String branchGuid, String relTypeGuid, int gammaId, DefaultBasicGuidArtifact artA, DefaultBasicGuidArtifact artB) {
      super(branchGuid, relTypeGuid, gammaId, artA, artB);
      this.relationEventType = relationEventType;
   }

   public RelationEventType getModType() {
      return relationEventType;
   }

   public String toString() {
      return String.format("[%s - B:%s - TG:%s - GI:%d - A:%s - B:%s]", relationEventType, getBranchGuid(),
            getBranchGuid(), getGammaId(), getArtA(), getArtB());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = super.hashCode();
      result = prime * result + ((relationEventType == null) ? 0 : relationEventType.hashCode());
      result = prime * result + ((getArtA() == null) ? 0 : getArtA().hashCode());
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
      if (getArtA() == null) {
         if (other.getArtA() != null) return false;
      } else if (!getArtA().equals(other.getArtA())) return false;
      if (getArtB() == null) {
         if (other.getArtB() != null) return false;
      } else if (!getArtB().equals(other.getArtB())) return false;
      return true;
   }

}
