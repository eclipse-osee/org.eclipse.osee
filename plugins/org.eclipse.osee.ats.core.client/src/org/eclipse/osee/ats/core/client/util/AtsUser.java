/*
 * Created on Feb 28, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.client.util;

import java.util.logging.Level;
import org.eclipse.osee.ats.core.client.internal.Activator;
import org.eclipse.osee.ats.core.model.IAtsChildren;
import org.eclipse.osee.ats.core.model.IAtsUser;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.User;

public class AtsUser implements IAtsUser {

   private final User user;

   public AtsUser(User user) {
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
      return user.getDescription();
   }

   @Override
   public String getHumanReadableId() {
      return user.getHumanReadableId();
   }

   @Override
   public IAtsChildren getAtsChildren() {
      return null;
   }

   @Override
   public Integer getIdInt() {
      return user.getArtId();
   }

   @Override
   public int compareTo(Object other) {
      try {
         String otherName = null;
         if (other instanceof IAtsUser) {
            otherName = ((IAtsUser) other).getName();
         } else if (other instanceof IAtsUser) {
            otherName = ((IAtsUser) other).getName();
         }
         if (otherName != null && getUserId() != null) {
            return getName().compareTo(otherName);
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return -1;
   }

   @Override
   public int hashCode() {
      final int prime = 31;
      int result = 0;
      try {
         result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return result;
   }

   @Override
   public boolean equals(Object obj) {
      if (this == obj) {
         return true;
      }
      try {
         String objUserId = null;
         if (obj instanceof IAtsUser) {
            objUserId = ((IAtsUser) obj).getUserId();
         } else if (obj instanceof User) {
            objUserId = ((User) obj).getUserId();
         }
         if (!Strings.isValid(objUserId)) {
            return false;
         }
         if (getUserId() == null) {
            if (objUserId != null) {
               return false;
            }
         } else if (!getUserId().equals(objUserId)) {
            return false;
         }
      } catch (OseeCoreException ex) {
         OseeLog.log(Activator.class, Level.SEVERE, ex);
      }
      return true;
   }

   @Override
   public String getUserId() throws OseeCoreException {
      return user.getUserId();
   }

   @Override
   public String getEmail() throws OseeCoreException {
      return user.getEmail();
   }

   @Override
   public boolean isActive() throws OseeCoreException {
      return user.isActive();
   }

   @Override
   public String toString() {
      try {
         return String.format("%s (%s)", getName(), getUserId());
      } catch (Exception ex) {
         return "Exception: " + ex.getLocalizedMessage();
      }
   }
}
