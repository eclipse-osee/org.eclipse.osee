/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/

package org.eclipse.osee.framework.ui.plugin.security;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;
import org.eclipse.osee.framework.plugin.core.util.ExtensionDefinedObjects;
import org.eclipse.osee.framework.ui.plugin.security.UserCredentials.UserCredentialEnum;

/**
 * @author Roberto E. Escobar
 */
public class OseeAuthentication extends AbstractAuthentication {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(OseeAuthentication.class);
   private static final String EXTENSION_ID = "org.eclipse.osee.framework.ui.plugin.AuthenticationProvider";
   private static final String ELEMENT_NAME = "AuthenticationClass";
   private static OseeAuthentication instance = null;
   private static URL DEFAULT_URL;
   private boolean logAsGuest = false;

   static {
      try {
         DEFAULT_URL = new URL("http://osee.authentication/");
      } catch (MalformedURLException e) {
      }
   }

   public enum AuthenticationStatus {
      Success, UserNotFound, InvalidPassword, NoResponse;
   }

   private IAuthenticationStrategy authenticationProvider;
   private ExtensionDefinedObjects<IAuthenticationStrategy> authenticationStrategies;

   private OseeAuthentication() {
      super();
      authenticationProvider = null;
      authenticationStrategies =
            new ExtensionDefinedObjects<IAuthenticationStrategy>(EXTENSION_ID, ELEMENT_NAME, "classname");
      authenticateFromStorage();
   }

   public static OseeAuthentication getInstance() {
      if (instance == null) {
         instance = new OseeAuthentication();
      }
      return instance;
   }

   public AuthenticationStatus authenticate(String userName, String password, String domain, boolean isStorageAllowed) {
      IAuthenticationStrategy authenticationStrategy = getAuthenticationStrategy();
      authenticationStrategy.authenticate(userName, password, domain);
      authenticationStatus = authenticationStrategy.getAuthenticationStatus();
      userCredentials = authenticationStrategy.getCredentials();

      if (authenticationStatus.equals(AuthenticationStatus.Success) && isStorageAllowed) {
         storeCredentials(userCredentials, userName, password);
      }
      return authenticationStatus;
   }

   public IAuthenticationStrategy getAuthenticationStrategy() {
      IAuthenticationStrategy toReturn = null;

      // Load the authentication provider if we don't have one yet
      if (authenticationProvider == null) {
         String providerId = ConfigUtil.getConfigFactory().getOseeConfig().getAuthenticationProviderId();
         try {
            authenticationProvider = authenticationStrategies.getObjectById(providerId);
            if (authenticationProvider == null) {
               throw new IllegalStateException();
            }
         } catch (Exception ex) {
            logger.log(Level.SEVERE, String.format("Authentication Provider [ %s ] not found defaulting to guest. ",
                  providerId));
            logAsGuest = true;
         }
      }

      if (false != logAsGuest) {
         toReturn = GuestAuthentication.getInstance();
      } else {
         toReturn = authenticationProvider;
      }
      return toReturn;
   }

   @SuppressWarnings("unchecked")
   private void authenticateFromStorage() {
      Map info = Platform.getAuthorizationInfo(DEFAULT_URL, "", "");

      if (info != null) {
         userCredentials = UserCredentials.toCredentials(new HashMap(info));
         authenticate(userCredentials.getField(UserCredentialEnum.UserName),
               userCredentials.getField(UserCredentialEnum.Password),
               userCredentials.getField(UserCredentialEnum.Domain), false);
      }
   }

   private void storeCredentials(UserCredentials userCredentials, String userName, String password) {
      try {
         userCredentials.setFieldAndValidity(UserCredentialEnum.UserName, true, userName);
         userCredentials.setFieldAndValidity(UserCredentialEnum.Password, true, password);
         Platform.addAuthorizationInfo(DEFAULT_URL, "", "", userCredentials.toMap());
      } catch (CoreException e) {
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see osee.plugin.core.security.IAuthentication#isLoginAllowed()
    */
   public boolean isLoginAllowed() {
      return getAuthenticationStrategy().isLoginAllowed();
   }

   public void setLogAsGuest(boolean logAsGuest) {
      this.logAsGuest = logAsGuest;
   }

   public void logOff() {
      super.clear();
      storeCredentials(userCredentials, "", "");
   }
}
