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
public class RelationTypeAccessType implements AccessType {

   private AllowDeny allowDeny;
   private RelationTypeToken relationType;
   private ArtifactTypeToken artifactType;

   public RelationTypeAccessType(AllowDeny allowDeny, RelationTypeToken relationType, ArtifactTypeToken artifactType) {
      this.allowDeny = allowDeny;
      this.relationType = relationType;
      this.artifactType = artifactType;
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

   public RelationTypeToken getRelationType() {
      return relationType;
   }

   public void setRelationType(RelationTypeToken relationType) {
      this.relationType = relationType;
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   @Override
   public String toString() {
      return "RelType [allow=" + allowDeny + ", relType=" + relationType + ", artType=" + artifactType + "]";
   }

   @Override
   public AccessTypeMatch computeMatch(ArtifactToken otherArt, AttributeTypeToken attrType, RelationTypeToken relType, IParentProvider parentProvider) {
      if (otherArt.getArtifactType().inheritsFrom(artifactType)) {
         if (relType.equals(relationType)) {
            if (allowDeny == AllowDeny.Allow) {
               return AccessTypeMatch.Allow;
            } else if (allowDeny == AllowDeny.Deny) {
               return AccessTypeMatch.Deny;
            }
         }
      }
      return AccessTypeMatch.NoMatch;
   }

}
