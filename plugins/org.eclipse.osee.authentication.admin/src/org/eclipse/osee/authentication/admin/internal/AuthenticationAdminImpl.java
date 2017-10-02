/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.authentication.admin.internal;

import static org.eclipse.osee.authentication.admin.internal.AuthenticationUtil.normalize;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.authentication.admin.AuthenticatedUser;
import org.eclipse.osee.authentication.admin.AuthenticationAdmin;
import org.eclipse.osee.authentication.admin.AuthenticationConfiguration;
import org.eclipse.osee.authentication.admin.AuthenticationConfigurationBuilder;
import org.eclipse.osee.authentication.admin.AuthenticationProvider;
import org.eclipse.osee.authentication.admin.AuthenticationRequest;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class AuthenticationAdminImpl implements AuthenticationAdmin {

   private final Map<String, AuthenticationProvider> authenticationProviders =
      new ConcurrentHashMap<String, AuthenticationProvider>();

   private Log logger;
   private AuthenticationConfiguration config;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(Map<String, Object> properties) {
      logger.trace("Starting AuthenticationAdminImpl...");
      update(properties);
   }

   public void stop() {
      logger.trace("Stopping AuthenticationAdminImpl...");
      config = null;
   }

   public void update(Map<String, Object> properties) {
      logger.trace("Updating AuthenticationAdmin Config...");

      config = AuthenticationConfigurationBuilder.newBuilder()//
         .properties(properties)//
         .build();
   }

   public void addAuthenticationProvider(AuthenticationProvider authenticationProvider) {
      String providerId = authenticationProvider.getAuthenticationScheme();
      providerId = normalize(providerId);
      authenticationProviders.put(providerId, authenticationProvider);
   }

   public void removeAuthenticationProvider(AuthenticationProvider authenticationProvider) {
      String providerId = authenticationProvider.getAuthenticationScheme();
      providerId = normalize(providerId);
      authenticationProviders.remove(providerId);
   }

   @Override
   public Iterable<String> getAvailableSchemes() {
      return AuthenticationUtil.unmodifiableSortedIterable(authenticationProviders.keySet());
   }

   @Override
   public Iterable<String> getAllowedSchemes() {
      return config.getAllowedSchemes();
   }

   @Override
   public String getDefaultScheme() {
      String toReturn = config.getDefaultScheme();
      if (!Strings.isValid(toReturn)) {
         Iterator<String> iterator = getAllowedSchemes().iterator();
         toReturn = iterator.hasNext() ? iterator.next() : "";
      }
      return toReturn;
   }

   private AuthenticationProvider getAuthenticator(String schemeType) {
      String toMatch = normalize(schemeType);
      return authenticationProviders.get(toMatch);
   }

   @Override
   public AuthenticatedUser authenticate(AuthenticationRequest authenticationRequest) {
      String schemeType = authenticationRequest.getScheme();
      if (!Strings.isValid(schemeType)) {
         schemeType = getDefaultScheme();
      }
      checkSchemeAllowed(schemeType);

      AuthenticationProvider provider = getAuthenticator(schemeType);
      AuthenticatedUser principal = provider.authenticate(authenticationRequest);

      Conditions.checkExpressionFailOnTrue(principal == null,
         "Authentication Error - scheme [%s] returned null principal", schemeType);

      return principal;
   }

   @Override
   public boolean isSchemeAllowed(String schemeType) {
      boolean isAllowed = false;
      if (Strings.isValid(schemeType)) {
         String toMatch = normalize(schemeType);
         for (String scheme : getAllowedSchemes()) {
            isAllowed = scheme.equals(toMatch);
            if (isAllowed) {
               break;
            }
         }
      }
      return isAllowed;
   }

   private void checkSchemeAllowed(String schemeType) {
      Conditions.checkExpressionFailOnTrue(!isSchemeAllowed(schemeType),
         "Authentication Error - scheme [%s] is not allowed. Schemes available %s.", schemeType, getAllowedSchemes());
   }
}
