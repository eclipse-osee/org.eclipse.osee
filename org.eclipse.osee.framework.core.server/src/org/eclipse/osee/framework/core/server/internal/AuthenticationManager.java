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
import org.eclipse.osee.framework.core.data.OseeCredential;
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
         final String providerId = authenticationProvider.getId();
         if (!authenticationProviders.containsKey(providerId)) {
            authenticationProviders.put(providerId, authenticationProvider);
         }
      }
   }

   @Override
   public void removeAuthenticationProvider(IAuthenticationProvider authenticationProvider) {
      synchronized (authenticationProviders) {
         authenticationProviders.remove(authenticationProvider);
      }
   }

   @Override
   public boolean authenticate(String authenticationProtocol, OseeCredential credential) throws OseeAuthenticationException {
      IAuthenticationProvider provider = getAuthenticationProvider(authenticationProtocol);
      if (provider != null) {
         return provider.authenticate(credential);
      }
      throw new OseeInvalidAuthenticationProtocolException(String.format("Invalid protocol [%s]",
            authenticationProtocol));
   }

   private IAuthenticationProvider getAuthenticationProvider(String protocol) {
      return authenticationProviders.get(protocol);
   }
}
