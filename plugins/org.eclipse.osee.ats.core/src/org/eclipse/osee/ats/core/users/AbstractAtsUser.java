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
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsUser implements IAtsUser {

   private String userId;

   protected AbstractAtsUser(String userId) {
      this.userId = userId;
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
      return "";
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
      return true;
   }
}