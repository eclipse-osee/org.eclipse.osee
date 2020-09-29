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
public class AttributeTypeAccessType implements AccessType {

   private AllowDeny allowDeny;
   private AttributeTypeToken attributeType;
   private ArtifactTypeToken artifactType;

   public AttributeTypeAccessType(AllowDeny allowDeny, AttributeTypeToken attributeType, ArtifactTypeToken artifactType) {
      this.allowDeny = allowDeny;
      this.attributeType = attributeType;
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

   public AttributeTypeToken getAttributeType() {
      return attributeType;
   }

   public void setAttributeType(AttributeTypeToken attributeType) {
      this.attributeType = attributeType;
   }

   public ArtifactTypeToken getArtifactType() {
      return artifactType;
   }

   public void setArtifactType(ArtifactTypeToken artifactType) {
      this.artifactType = artifactType;
   }

   @Override
   public String toString() {
      return "AttrType [allow=" + allowDeny + ", attrType=" + attributeType + ", artType=" + artifactType + "]";
   }

   @Override
   public AccessTypeMatch computeMatch(ArtifactToken otherArt, AttributeTypeToken attrType, RelationTypeToken relType, IParentProvider parentProvider) {
      if (otherArt.getArtifactType().inheritsFrom(artifactType)) {
         if (attributeType.equals(attrType)) {
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
