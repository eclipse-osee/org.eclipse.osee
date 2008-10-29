/*
 * Created on Oct 27, 2008
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.session.management.internal;

import java.util.Map;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.login.LoginException;
import javax.security.auth.spi.LoginModule;

/**
 * @author Roberto E. Escobar
 */
public class JvmLoginModule implements LoginModule {

   /* (non-Javadoc)
    * @see javax.security.auth.spi.LoginModule#abort()
    */
   @Override
   public boolean abort() throws LoginException {
      return false;
   }

   /* (non-Javadoc)
    * @see javax.security.auth.spi.LoginModule#commit()
    */
   @Override
   public boolean commit() throws LoginException {
      return false;
   }

   /* (non-Javadoc)
    * @see javax.security.auth.spi.LoginModule#initialize(javax.security.auth.Subject, javax.security.auth.callback.CallbackHandler, java.util.Map, java.util.Map)
    */
   @Override
   public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState, Map<String, ?> options) {
   }

   /* (non-Javadoc)
    * @see javax.security.auth.spi.LoginModule#login()
    */
   @Override
   public boolean login() throws LoginException {
      return false;
   }

   /* (non-Javadoc)
    * @see javax.security.auth.spi.LoginModule#logout()
    */
   @Override
   public boolean logout() throws LoginException {
      return false;
   }

}
