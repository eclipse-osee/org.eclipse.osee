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
package org.eclipse.osee.framework.core.server.internal;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.core.exception.OseeInvalidAuthenticationProtocolException;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.IAuthenticationProvider;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationManager implements IAuthenticationManager {

   private final Map<String, IAuthenticationProvider> authenticationProviders;

   public AuthenticationManager() {
      this.authenticationProviders = Collections.synchronizedMap(new HashMap<String, IAuthenticationProvider>());
   }

   @Override
   public void addAuthenticationProvider(IAuthenticationProvider authenticationProvider) {
      synchronized (authenticationProviders) {
         final String providerId = authenticationProvider.getProtocol();
         if (!authenticationProviders.containsKey(providerId)) {
            authenticationProviders.put(providerId, authenticationProvider);
         }
      }
   }

   @Override
   public boolean authenticate(OseeCredential credential) throws OseeAuthenticationException {
      if (isSafeUser(credential)) {
         return true;
      } else {
         IAuthenticationProvider provider = authenticationProviders.get(credential.getAuthenticationProtocol());
         if (provider != null) {
            return provider.authenticate(credential);
         }
      }
      throw new OseeInvalidAuthenticationProtocolException(String.format("Invalid protocol [%s]",
            credential.getAuthenticationProtocol()));
   }

   @Override
   public void removeAuthenticationProvider(IAuthenticationProvider authenticationProvider) {
      synchronized (authenticationProviders) {
         authenticationProviders.remove(authenticationProvider);
      }
   }

   @Override
   public String[] getProtocols() {
      Set<String> keys = authenticationProviders.keySet();
      return keys.toArray(new String[keys.size()]);
   }

   @Override
   public IOseeUserInfo asOseeUser(OseeCredential credential) throws OseeAuthenticationException {
      if (isGuestLogin(credential)) {
         return SystemUser.Guest;
      } else if (isBootStrap(credential)) {
         return SystemUser.BootStrap;
      } else {
         IAuthenticationProvider provider = authenticationProviders.get(credential.getAuthenticationProtocol());
         if (provider != null) {
            return provider.asOseeUserId(credential);
         }
      }
      throw new OseeInvalidAuthenticationProtocolException(String.format("Invalid protocol [%s]",
            credential.getAuthenticationProtocol()));
   }

   private boolean isGuestLogin(OseeCredential credential) {
      return credential.getUserName().equals(SystemUser.Guest.getName());
   }

   private boolean isBootStrap(OseeCredential credential) {
      return credential.getUserName().equals(SystemUser.BootStrap.getName());
   }

   private boolean isSafeUser(OseeCredential credential) {
      return isGuestLogin(credential) || isBootStrap(credential);
   }

}
