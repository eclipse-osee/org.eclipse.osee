/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.rest.internal.user;

import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
@XmlRootElement
public class AtsUser implements IAtsUser {

   private ArtifactReadable user;

   public AtsUser(ArtifactReadable user) {
      this.user = user;
   }

   @Override
   public String getName() {
      return user.getName();
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 0;
      int userIdHashCode = 0;
      try {
         userIdHashCode = getUserId() == null ? 0 : getUserId().hashCode();
      } catch (OseeCoreException ex) {
         // Do nothing;
      }
      result = prime * result + userIdHashCode;
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (obj == null) {
         return false;
      }
      try {
         String objUserId = null;
         if (obj instanceof IAtsUser) {
            objUserId = ((IAtsUser) obj).getUserId();
         } else {
            return false;
         }
         String thisUserId = getUserId();
         if (thisUserId == null) {
            if (objUserId != null) {
               return false;
            }
         } else if (!thisUserId.equals(objUserId)) {
            return false;
         }
      } catch (OseeCoreException ex) {
         return false;
      }
      return true;
   }

   @Override
   public String getUserId() throws OseeCoreException {
      return user.getSoleAttributeAsString(CoreAttributeTypes.UserId);
   }

   @Override
   public String getEmail() throws OseeCoreException {
      return user.getSoleAttributeAsString(CoreAttributeTypes.Email, "");
   }

   @Override
   public boolean isActive() throws OseeCoreException {
      return user.getSoleAttributeValue(CoreAttributeTypes.Active, false);
   }

   @Override
   public String toString() {
      try {
         return String.format("%s (%s)", getName(), getUserId());
      } catch (Exception ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }

   @Override
   @JsonIgnore
   public ArtifactToken getStoreObject() {
      return user;
   }

   @Override
   public void setStoreObject(ArtifactToken artifact) {
      if (artifact instanceof ArtifactReadable) {
         this.user = (ArtifactReadable) artifact;
      }
   }

   @Override
   public Long getId() {
      return this.user.getId();
   }

   @Override
   public IArtifactType getArtifactType() {
      return user.getArtifactType();
   }

   @Override
   public boolean isOfType(ArtifactTypeId... artifactTypes) {
      return user.isOfType(artifactTypes);
   }

}