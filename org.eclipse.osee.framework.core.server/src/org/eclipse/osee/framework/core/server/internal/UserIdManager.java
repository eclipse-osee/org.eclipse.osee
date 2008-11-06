/*
 * Created on Nov 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.core.server.internal;

import org.eclipse.osee.framework.core.data.SystemUser;

/**
 * @author Roberto E. Escobar
 */
public class UserIdManager {

   public static boolean isGuestLogin(String loginId) {
      return loginId.equals(SystemUser.Guest.getName());
   }

   public static boolean isBootStrap(String loginId) {
      return loginId.equals(SystemUser.BootStrap.getName());
   }

   public static boolean isSafeUser(String loginId) {
      return isGuestLogin(loginId) || isBootStrap(loginId);
   }

   public static String getUserIdFromLoginId(String loginId) {
      String toReturn = "Unknown";
      if (isGuestLogin(loginId)) {
         toReturn = SystemUser.Guest.getUserID();
      } else if (isBootStrap(loginId)) {
         toReturn = SystemUser.BootStrap.getUserID();
      } else {
         //         ConnectionHandler.runPreparedQueryFetchString(defaultValue, query, data);
      }
      return toReturn;
   }
}
