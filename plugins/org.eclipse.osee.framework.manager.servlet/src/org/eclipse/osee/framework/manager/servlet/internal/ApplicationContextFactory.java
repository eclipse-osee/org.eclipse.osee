/*
 * Created on Sep 14, 2012
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.manager.servlet.internal;

import org.eclipse.osee.orcs.ApplicationContext;

public class ApplicationContextFactory {

   private ApplicationContextFactory() {
      // TODO Improve session management
   }

   public static ApplicationContext createContext(final String sessionId) {
      return new ApplicationContext() {

         @Override
         public String getSessionId() {
            return sessionId;
         }
      };
   }
}
