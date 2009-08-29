/*
 * Created on Aug 14, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.ui.skynet;

import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.osee.framework.core.enums.RelationSide;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.relation.RelationType;

/**
 * @author b1528444
 *
 */
class WrapperForRelationLink implements IAdaptable{

   private RelationType relationType;
   private Artifact artifactA;
   private Artifact artifactB;
   private Artifact other;
   
   public WrapperForRelationLink(RelationType relationType, Artifact other, Artifact artifactA, Artifact artifactB) {
      this.relationType = relationType;
      this.artifactA = artifactA;
      this.artifactB = artifactB;
      this.other = other;
   }

   RelationType getRelationType() {
      return relationType;
   }

   Artifact getArtifactA() {
      return artifactA;
   }
   
   Artifact getArtifactB() {
      return artifactB;
   }
   
   Artifact getOther() {
      return other;
   }
   
   RelationSide getRelationSide(){
      if(other.equals(artifactB)){
         return RelationSide.SIDE_B;
      } else {
         return RelationSide.SIDE_A;
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.core.runtime.IAdaptable#getAdapter(java.lang.Class)
    */
   @Override
   public Object getAdapter(Class arg0) {
      if(arg0.equals(Artifact.class)){
         return other;
      }
      return null;
   }
}
