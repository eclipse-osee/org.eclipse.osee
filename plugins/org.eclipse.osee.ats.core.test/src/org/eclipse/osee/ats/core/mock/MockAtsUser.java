/*
 * Created on Feb 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.mock;

import org.eclipse.osee.ats.core.model.IAtsUser;

public class MockAtsUser implements IAtsUser {

   private final String name;
   private String email, userId;
   private boolean active;

   public void setActive(boolean active) {
      this.active = active;
   }

   public MockAtsUser(String name) {
      this.name = name;
      this.userId = name;
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
   public String getHumanReadableId() {
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

}
