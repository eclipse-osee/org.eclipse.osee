/*
 * Created on Oct 22, 2014
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.skynet.core.utility;

import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.skynet.core.UserManager;

public class ActivityLogUtil {

   private static Long accountId;
   private static Long clientId;

   public static Long getClientId() {
      if (clientId == null) {
         clientId = Long.valueOf(ClientSessionManager.getSessionId().hashCode());
      }
      return clientId;
   }

   public static Long getAccountId() {
      if (accountId == null) {
         accountId = Long.valueOf(UserManager.getUser().getArtId());
      }
      return accountId;
   }
}
