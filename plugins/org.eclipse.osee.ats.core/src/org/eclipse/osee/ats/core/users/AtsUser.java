/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.users;

import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.jdk.core.type.NamedIdBase;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;

/**
 * @author Donald G. Dunne
 */
public class AtsUser extends NamedIdBase implements IAtsUser {

   private String userId;
   private final String email;
   private final boolean isActive;

   public AtsUser(UserToken user) {
      this(user.getId(), user.getName(), user.getUserId(), user.getEmail(), user.isActive());
   }

   public AtsUser(Long id, String name, String userId, String email, boolean isActive) {
      super(id, name);
      this.userId = userId;
      this.email = email;
      this.isActive = isActive;

   }

   @Override
   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   @Override
   public String getEmail() {
      return email;
   }

   @Override
   public String toString() {
      return String.format("User [%s - %s - %s]", getName(), getUserId(), getEmail());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + (userId == null ? 0 : userId.hashCode());
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      if (!(obj instanceof IAtsUser)) {
         return false;
      }
      try {
         String thisUserId = getUserId();
         String objUserId = ((IAtsUser) obj).getUserId();
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
   public boolean isActive() {
      return isActive;
   }

   @Override
   public IArtifactType getArtifactType() {
      return CoreArtifactTypes.User;
   }

   @Override
   public boolean isOfType(ArtifactTypeId... artifactTypes) {
      return Collections.asHashSet(artifactTypes).contains(getArtifactType());
   }

}