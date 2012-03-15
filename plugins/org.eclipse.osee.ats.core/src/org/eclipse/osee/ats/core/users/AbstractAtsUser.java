/*
 * Created on Feb 27, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.users;

import java.util.logging.Level;
import org.eclipse.osee.ats.core.internal.Activator;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Donald G. Dunne
 */
public abstract class AbstractAtsUser implements IAtsUser {

   private String userId;

   public AbstractAtsUser(String userId) {
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
   public String getDescription() {
      return getName();
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
      if (!(obj instanceof IAtsUser)) {
         return false;
      }
      IAtsUser other = (IAtsUser) obj;
      try {
         if (userId == null) {
            if (other.getUserId() != null) {
               return false;
            } else {
               return false;
            }
         } else if (!userId.equals(other.getUserId())) {
            return false;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
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
      return true;
   }

}
