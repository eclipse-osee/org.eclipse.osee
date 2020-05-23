/*********************************************************************
 * Copyright (c) 2014 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.rs;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.get;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.getBoolean;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.getInt;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.getLong;
import java.util.Map;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2ResourceServerConfig {

   private static final String NAMESPACE = "jaxrs.oauth2.rs";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   public static final String OAUTH2_RS__SERVICE_ENABLED = qualify("enabled");
   public static final String OAUTH2_RS__REALM = qualify("realm");
   public static final String OAUTH2_RS__AUDIENCE_IS_ENDPOINT_ADDRESS = qualify("audience.is.endpoint.address");
   public static final String OAUTH2_RS__FILTER_CHECKS_FORM_DATA = qualify("filter.checks.form.data.for.token");
   public static final String OAUTH2_RS__USE_USER_SUBJECT = qualify("use.user.subject");
   public static final String OAUTH2_RS__RESOURCE_SERVER_KEY = qualify("resource.server.key");
   public static final String OAUTH2_RS__RESOURCE_SERVER_SECRET = qualify("resource.server.secret");
   public static final String OAUTH2_RS__TOKEN_VALIDATION_URI = qualify("token.validation.uri");
   public static final String OAUTH2_RS__IS_CACHE_TOKENS_ALLOWED = qualify("is.cache.tokens.allowed");
   public static final String OAUTH2_RS__TOKEN_CACHE_MAX_SIZE = qualify("token.cache.max.size");
   public static final String OAUTH2_RS__TOKEN_CACHE_EVICT_TIMEOUT_MILLIS = qualify("token.cache.evict.timeout");

   public static final boolean DEFAULT_OAUTH2_RS__SERVICE_ENABLED = false;
   public static final String DEFAULT_OAUTH2_RS__REALM = "OAuth2-Resource-Server-OSEE";
   public static final boolean DEFAULT_OAUTH2_RS__AUDIENCE_IS_ENDPOINT_ADDRESS = false;
   public static final boolean DEFAULT_OAUTH2_RS__FILTER_CHECKS_FORM_DATA = false;
   public static final boolean DEFAULT_OAUTH2_RS__USE_USER_SUBJECT = false;
   public static final boolean DEFAULT_OAUTH2_RS__IS_CACHE_TOKENS_ALLOWED = true;
   public static final int DEFAULT_OAUTH2_RS__TOKEN_CACHE_MAX_SIZE = 5000; // 5000 tokens
   public static final long DEFAULT_OAUTH2_RS__TOKEN_CACHE_EVICT_TIMEOUT_MILLIS = 4L * 60L * 60L * 1000L; // 4 hours

   public static final long OAUTH2_RS__MAX_TOKEN_CACHE_EVICT_TIMEOUT_MILLIS = 24L * 60L * 60L * 1000L; // one day

   public static OAuth2ResourceServerConfig fromProperties(Map<String, Object> props) {
      return new OAuth2ResourceServerConfig(props);
   }

   private final Map<String, Object> props;

   private OAuth2ResourceServerConfig(Map<String, Object> props) {
      this.props = props;
   }

   public boolean isEnabled() {
      return getBoolean(props, OAUTH2_RS__SERVICE_ENABLED, DEFAULT_OAUTH2_RS__SERVICE_ENABLED);
   }

   public String getRealm() {
      return get(props, OAUTH2_RS__REALM, DEFAULT_OAUTH2_RS__REALM);
   }

   public boolean isAudienceIsEndpointAddress() {
      return getBoolean(props, OAUTH2_RS__AUDIENCE_IS_ENDPOINT_ADDRESS,
         DEFAULT_OAUTH2_RS__AUDIENCE_IS_ENDPOINT_ADDRESS);
   }

   public boolean isFilterChecksFormDataForToken() {
      return getBoolean(props, OAUTH2_RS__FILTER_CHECKS_FORM_DATA, DEFAULT_OAUTH2_RS__FILTER_CHECKS_FORM_DATA);
   }

   public boolean isUseUserSubject() {
      return getBoolean(props, OAUTH2_RS__USE_USER_SUBJECT, DEFAULT_OAUTH2_RS__FILTER_CHECKS_FORM_DATA);
   }

   public String getResourceServerKey() {
      return get(props, OAUTH2_RS__RESOURCE_SERVER_KEY, null);
   }

   public String getResourceServerSecret() {
      return get(props, OAUTH2_RS__RESOURCE_SERVER_SECRET, null);
   }

   public String getValidationServerUri() {
      return get(props, OAUTH2_RS__TOKEN_VALIDATION_URI, null);
   }

   public boolean isCacheTokensAllowed() {
      return getBoolean(props, OAUTH2_RS__IS_CACHE_TOKENS_ALLOWED, DEFAULT_OAUTH2_RS__IS_CACHE_TOKENS_ALLOWED);
   }

   public int getTokenCacheMaxSize() {
      return getInt(props, OAUTH2_RS__TOKEN_CACHE_MAX_SIZE, DEFAULT_OAUTH2_RS__TOKEN_CACHE_MAX_SIZE);
   }

   public long getTokenCacheEvictTimeoutMillis() {
      return getLong(props, OAUTH2_RS__TOKEN_CACHE_EVICT_TIMEOUT_MILLIS,
         DEFAULT_OAUTH2_RS__TOKEN_CACHE_EVICT_TIMEOUT_MILLIS);
   };

}
