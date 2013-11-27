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
package org.eclipse.osee.ats.impl.internal.user;

import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.Identity;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Donald G. Dunne
 */
public class AtsUser implements IAtsUser {

   private final ArtifactReadable user;

   public AtsUser(ArtifactReadable user) {
      this.user = user;
   }

   @Override
   public String getName() {
      return user.getName();
   }

   @Override
   public String getGuid() {
      return user.getGuid();
   }

   @Override
   public String getDescription() {
      return user.getName();
   }

   @Override
   public int compareTo(Object other) {
      int result = other != null ? -1 : 1;
      if (other instanceof IAtsUser) {
         String otherName = ((IAtsUser) other).getName();
         String thisName = user != null ? getName() : null;
         if (thisName == null && otherName == null) {
            result = 0;
         } else if (thisName != null && otherName == null) {
            result = 1;
         } else if (thisName != null && otherName != null) {
            result = thisName.compareTo(otherName);
         }
      }
      return result;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 0;
      int userIdHashCode = 0;
      try {
         userIdHashCode = (getUserId() == null) ? 0 : getUserId().hashCode();
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
   public boolean matches(Identity<?>... identities) {
      for (Identity<?> identity : identities) {
         if (equals(identity)) {
            return true;
         }
      }
      return false;
   }

   @Override
   public String toStringWithId() {
      return String.format("[%s][%s]", getName(), getGuid());
   }

   @Override
   public Object getStoreObject() {
      return user;
   }

}
