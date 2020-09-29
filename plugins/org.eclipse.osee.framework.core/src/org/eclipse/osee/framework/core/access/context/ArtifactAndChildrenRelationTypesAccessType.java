/*******************************************************************************
 * Copyright (c) 2020 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.access.context;

import org.eclipse.osee.framework.core.access.AccessTypeMatch;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Donald G. Dunne
 */
public class ArtifactAndChildrenRelationTypesAccessType implements AccessType {

   private AllowDeny allowDeny;
   private ArtifactToken artifact;
   private RelationTypeToken relationType;
   private ArtifactTypeToken artAandBType;

   public ArtifactAndChildrenRelationTypesAccessType(AllowDeny allowDeny, ArtifactToken artifact, RelationTypeToken relationType, ArtifactTypeToken artAandBType) {
      this.allowDeny = allowDeny;
      this.artifact = artifact;
      this.relationType = relationType;
      this.artAandBType = artAandBType;
   }

   public AllowDeny getAllowDeny() {
      return allowDeny;
   }

   public void setAllowDeny(AllowDeny allowDeny) {
      this.allowDeny = allowDeny;
   }

   @Override
   public boolean isArtifactType() {
      return true;
   }

   public ArtifactToken getArtifact() {
      return artifact;
   }

   public void setArtifact(ArtifactToken artifact) {
      this.artifact = artifact;
   }

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public void setRelationType(RelationTypeToken relationType) {
      this.relationType = relationType;
   }

   public ArtifactTypeToken getArtAandBType() {
      return artAandBType;
   }

   public void setArtAandBType(ArtifactTypeToken artAandBType) {
      this.artAandBType = artAandBType;
   }

   @Override
   public String toString() {
      return "ArtAndChildRelTypes [allow=" + allowDeny + ", art=" + artifact + ", relType=" + relationType + ", artAandBType=" + artAandBType + "]";
   }

   @Override
   public AccessTypeMatch computeMatch(ArtifactToken otherArt, AttributeTypeToken attrType, RelationTypeToken relType, IParentProvider parentProvider) {
      boolean match = false;
      boolean computed = false;
      if (otherArt.equals(artifact)) {
         match = true;
         computed = true;
      } else {
         // If artifact is a childArtType being matched, recursively check up tree for artifact
         if (artifact.getArtifactType().inheritsFrom(artAandBType)) {
            if (ArtifactAndChildrenArtifactTypesAccessType.checkIfDecendentOf(otherArt, artifact, parentProvider)) {
               computed = true;
               match = true;
            }
         }
      }
      if (computed && match) {
         if (allowDeny == AllowDeny.Allow) {
            return AccessTypeMatch.Allow;
         } else if (allowDeny == AllowDeny.Deny) {
            return AccessTypeMatch.Deny;
         }
      }
      return AccessTypeMatch.NotComputed;
   }

}
