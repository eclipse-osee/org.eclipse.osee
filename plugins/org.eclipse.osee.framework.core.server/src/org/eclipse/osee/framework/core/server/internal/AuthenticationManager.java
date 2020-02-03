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

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.IAuthenticationProvider;
import org.eclipse.osee.framework.core.server.OseeServerProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationManager implements IAuthenticationManager {

   private final Map<String, IAuthenticationProvider> authenticationProviders = new ConcurrentHashMap<>();

   private static AtomicBoolean logged = new AtomicBoolean(false);

   public void addAuthenticationProvider(IAuthenticationProvider authenticationProvider) {
      final String providerId = authenticationProvider.getProtocol();
      authenticationProviders.put(providerId, authenticationProvider);
   }

   public void removeAuthenticationProvider(IAuthenticationProvider authenticationProvider) {
      authenticationProviders.remove(authenticationProvider).toString();
   }

   @Override
   public boolean authenticate(OseeCredential credential) {
      boolean result = false;
      if (isSafeUser(credential)) {
         result = true;
      } else {
         IAuthenticationProvider provider = getAuthenticationProvider();
         if (provider != null) {
            result = provider.authenticate(credential);
         }
      }
      return result;
   }

   @Override
   public String[] getProtocols() {
      Set<String> keys = authenticationProviders.keySet();
      return keys.toArray(new String[keys.size()]);
   }

   @Override
   public UserToken asUserToken(OseeCredential credential) {
      UserToken toReturn = null;
      if (isAnonymousLogin(credential)) {
         toReturn = SystemUser.Anonymous;
      } else if (isBootStrap(credential)) {
         toReturn = SystemUser.BootStrap;
      } else {
         IAuthenticationProvider provider = getAuthenticationProvider();
         if (provider != null) {
            toReturn = provider.asOseeUserId(credential);
         }
      }
      return toReturn;
   }

   private IAuthenticationProvider getAuthenticationProvider() {
      String key = getProtocol();
      if (Strings.isValid(key)) {
         IAuthenticationProvider provider = authenticationProviders.get(key);
         if (logged.compareAndSet(false, true)) {
            System.err.println(
               String.format("Authentication Provider [%s]-[%s]", key, provider.getClass().getSimpleName()));
         }
         if (provider != null) {
            return provider;
         }
      }
      throw new OseeAuthenticationException("Invalid authentication protocol [%s]", key);
   }

   private boolean isAnonymousLogin(OseeCredential credential) {
      return credential.getUserName().equals(SystemUser.Anonymous.getName());
   }

   private boolean isBootStrap(OseeCredential credential) {
      return credential.getUserName().equals(SystemUser.BootStrap.getName());
   }

   private boolean isSafeUser(OseeCredential credential) {
      return isAnonymousLogin(credential) || isBootStrap(credential);
   }

   @Override
   public String getProtocol() {
      return OseeServerProperties.getAuthenticationProtocol();
   }

}
