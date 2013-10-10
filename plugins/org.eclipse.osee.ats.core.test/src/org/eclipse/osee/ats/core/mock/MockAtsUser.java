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
package org.eclipse.osee.ats.core.mock;

import org.eclipse.osee.ats.api.user.IAtsUser;
import org.eclipse.osee.framework.jdk.core.type.Identity;

/**
 * @author Donald G. Dunne
 */
public class MockAtsUser implements IAtsUser {

   private final String name;
   private String email, userId;
   private boolean active;

   public void setActive(boolean active) {
      this.active = active;
   }

   public MockAtsUser(String name) {
      this.name = name;
      userId = name;
   }

   @Override
   public String getName() {
      return name;
   }

   @Override
   public String getGuid() {
      return name;
   }

   @Override
   public String getDescription() {
      return name;
   }

   @Override
   public String getEmail() {
      return email;
   }

   public void setEmail(String email) {
      this.email = email;
   }

   @Override
   public String getUserId() {
      return userId;
   }

   public void setUserId(String userId) {
      this.userId = userId;
   }

   @Override
   public String toString() {
      return String.format("User [%s - %s - %s]", getName(), getUserId(), getEmail());
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 1;
      result = prime * result + ((userId == null) ? 0 : userId.hashCode());
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
      if (getClass() != obj.getClass()) {
         return false;
      }
      MockAtsUser other = (MockAtsUser) obj;
      if (userId == null) {
         if (other.userId != null) {
            return false;
         }
      } else if (!userId.equals(other.userId)) {
         return false;
      }
      return true;
   }

   @Override
   public int compareTo(Object other) {
      if (other != null && other instanceof IAtsUser && ((IAtsUser) other).getName() != null && getName() != null) {
         return getName().compareTo(((IAtsUser) other).getName());
      }
      return -1;
   }

   @Override
   public boolean isActive() {
      return active;
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
      return toString();
   }

}
