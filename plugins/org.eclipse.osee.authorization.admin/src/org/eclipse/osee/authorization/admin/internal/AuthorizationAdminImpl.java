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
package org.eclipse.osee.authorization.admin.internal;

import static org.eclipse.osee.authorization.admin.internal.AuthorizationUtil.normalize;
import java.security.Principal;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.eclipse.osee.authorization.admin.Authority;
import org.eclipse.osee.authorization.admin.Authorization;
import org.eclipse.osee.authorization.admin.AuthorizationAdmin;
import org.eclipse.osee.authorization.admin.AuthorizationConfiguration;
import org.eclipse.osee.authorization.admin.AuthorizationConfigurationBuilder;
import org.eclipse.osee.authorization.admin.AuthorizationConstants;
import org.eclipse.osee.authorization.admin.AuthorizationData;
import org.eclipse.osee.authorization.admin.AuthorizationOverride;
import org.eclipse.osee.authorization.admin.AuthorizationProvider;
import org.eclipse.osee.authorization.admin.AuthorizationRequest;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class AuthorizationAdminImpl implements AuthorizationAdmin {

   private static final Authority PERMIT_ALL_OVERRIDE = new PermitAllAuthority();
   private static final Authority DENY_ALL_OVERRIDE = new DenyAllAuthority();

   private final Map<String, AuthorizationProvider> providers = new ConcurrentHashMap<>();

   private Log logger;
   private AuthorizationConfiguration config;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void start(Map<String, Object> properties) {
      logger.trace("Starting AuthorizationAdminImpl...");
      update(properties);
   }

   public void stop() {
      logger.trace("Stopping AuthorizationAdminImpl...");
      config = null;
   }

   public void update(Map<String, Object> properties) {
      logger.trace("Updating AuthorizationAdminImpl...");

      config = AuthorizationConfigurationBuilder.newBuilder()//
         .properties(properties)//
         .build();
   }

   public void addAuthorizationProvider(AuthorizationProvider provider) {
      String providerId = provider.getScheme();
      providerId = AuthorizationUtil.normalize(providerId);
      providers.put(providerId, provider);
   }

   public void removeAuthorizationProvider(AuthorizationProvider provider) {
      String providerId = provider.getScheme();
      providerId = AuthorizationUtil.normalize(providerId);
      providers.remove(providerId);
   }

   @Override
   public Iterable<String> getAvailableSchemes() {
      return AuthorizationUtil.unmodifiableSortedIterable(providers.keySet());
   }

   @Override
   public Iterable<String> getAllowedSchemes() {
      return config.getAllowedSchemes();
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

   @Override
   public Authorization authorize(AuthorizationRequest request) {
      logger.debug("Authorization Requested: [%s]", request);
      String scheme = getScheme(request);
      checkSchemeAllowed(scheme);

      AuthorizationProvider provider = getAuthorizationProvider(scheme);
      checkProvider(scheme, provider);

      AuthorizationData data = provider.authorize(request);
      checkAuthorizationData(provider.getScheme(), data);

      Authority authority = getAuthority(config.getOverride(), data);
      checkAuthority(provider.getScheme(), authority);

      String authScheme = authority.getScheme();
      Date date = request.getRequestDate();
      boolean secure = request.isSecure();
      Principal principal = data != null ? data.getPrincipal() : null;
      return new AuthorizationImpl(authScheme, date, secure, principal, authority);
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

   private String getScheme(AuthorizationRequest request) {
      String toReturn = request.getAuthorizationType();
      if (!Strings.isValid(toReturn) || isNoneAllowed()) {
         toReturn = getDefaultScheme();
      }
      return toReturn;
   }

   private boolean isNoneAllowed() {
      boolean result = false;
      String toFind = AuthorizationConstants.NONE_AUTHORIZATION_PROVIDER;
      for (String allowed : getAllowedSchemes()) {
         result = toFind.equalsIgnoreCase(allowed);
         if (result) {
            break;
         }
      }
      return result;
   }

   private AuthorizationProvider getAuthorizationProvider(String scheme) {
      String toGet = normalize(scheme);
      return providers.get(toGet);
   }

   private Authority getAuthority(AuthorizationOverride override, AuthorizationData data) {
      Authority toReturn = null;
      switch (override) {
         case DENY_ALL:
            toReturn = DENY_ALL_OVERRIDE;
            break;
         case PERMIT_ALL:
            toReturn = PERMIT_ALL_OVERRIDE;
            break;
         default:
            toReturn = data.getAuthority();
            break;
      }
      return toReturn;
   }

   private void checkSchemeAllowed(String schemeType) {
      Conditions.checkExpressionFailOnTrue(!isSchemeAllowed(schemeType),
         "Authorization Error - scheme [%s] is not allowed. Schemes available %s.", schemeType, getAllowedSchemes());
   }

   private void checkProvider(String scheme, AuthorizationProvider provider) {
      Conditions.checkExpressionFailOnTrue(provider == null,
         "Authentication Error - scheme [%s] returned null provider", scheme);
   }

   private void checkAuthorizationData(String scheme, AuthorizationData data) {
      Conditions.checkExpressionFailOnTrue(data == null,
         "Authentication Error - scheme [%s] returned null authorization", scheme);
   }

   private void checkAuthority(String scheme, Authority data) {
      Conditions.checkExpressionFailOnTrue(data == null, "Authentication Error - scheme [%s] returned null authority",
         scheme);
   }
}
