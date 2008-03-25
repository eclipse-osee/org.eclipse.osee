/*
 * Created on Mar 5, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.config.demo.util;

import org.eclipse.osee.framework.ui.plugin.security.JvmAuthentication;

public class DemoAuthentication extends JvmAuthentication {

   private static final DemoAuthentication instance = new DemoAuthentication();
   private final boolean autoAuthenticate = true;

   private DemoAuthentication() {
   }

   public static DemoAuthentication getInstance() {
      return instance;
   }

   public boolean authenticate(String userName, String password, String domain) {
      if (autoAuthenticate) {
         System.err.println("isDeveloper: authentication skipped");
         userCredentials.setFieldAndValidity(
               org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Id, true, "Joe Smith");
         userCredentials.setFieldAndValidity(
               org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Name, true, "Joe Smith");
         authenticationStatus =
               org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication.AuthenticationStatus.Success;
      } else if (password.equals("osee") && domain.equals("osee")) {
         userCredentials.setFieldAndValidity(
               org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Id, true, userName);
         userCredentials.setFieldAndValidity(
               org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum.Name, true, userName);
         authenticationStatus =
               org.eclipse.osee.framework.ui.plugin.security.OseeAuthentication.AuthenticationStatus.Success;
         return true;
      } else {
         return false;
      }
      return true;

   }

   public boolean isLoginAllowed() {
      if (autoAuthenticate) {
         System.err.println("isDeveloper: login not required");
         return false;
      } else {
         return true;
      }
   }

}