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

import java.util.Collection;
import org.eclipse.osee.framework.core.access.AccessTypeMatch;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationTypeToken;

/**
 * @author Donald G. Dunne
 */
public class ArtifactAndChildrenArtifactTypesAccessType implements AccessType {

   private AllowDeny allowDeny;
   private ArtifactToken artifact;
   private Collection<ArtifactTypeToken> childArtTypes;

   public ArtifactAndChildrenArtifactTypesAccessType(AllowDeny allowDeny, ArtifactToken artifact, Collection<ArtifactTypeToken> childArtTypes) {
      this.allowDeny = allowDeny;
      this.artifact = artifact;
      this.childArtTypes = childArtTypes;
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

   public Collection<ArtifactTypeToken> getChildArtTypes() {
      return childArtTypes;
   }

   public void setChildArtTypes(Collection<ArtifactTypeToken> childArtTypes) {
      this.childArtTypes = childArtTypes;
   }

   @Override
   public String toString() {
      return "ArtAndChildArtTypes [allow=" + allowDeny + ", art=" + artifact + ", childArtTypes=" + childArtTypes + "]";
   }

   @Override
   public AccessTypeMatch computeMatch(ArtifactToken otherArt, AttributeTypeToken attrType, RelationTypeToken relType, IParentProvider parentProvider) {
      boolean match = false;
      boolean computed = false;
      if (otherArt.equals(artifact)) {
         computed = true;
         match = true;
      } else {
         // If artifact is a childArtType being matched, recursively check up tree for artifact
         for (ArtifactTypeToken childArtType : childArtTypes) {
            if (artifact.getArtifactType().inheritsFrom(childArtType)) {
               if (checkIfDecendentOf(otherArt, artifact, parentProvider)) {
                  computed = true;
                  match = true;
                  break;
               }
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

   protected static boolean checkIfDecendentOf(ArtifactToken decendent, ArtifactToken artToMatch, IParentProvider parentProvider) {
      if (decendent.equals(artToMatch)) {
         return true;
      }
      // Check for parent match recursively
      ArtifactToken parent = parentProvider.getParent(decendent);
      if (parent != null && parent.isValid()) {
         return checkIfDecendentOf(parent, artToMatch, parentProvider);
      }
      return false;
   }

}
