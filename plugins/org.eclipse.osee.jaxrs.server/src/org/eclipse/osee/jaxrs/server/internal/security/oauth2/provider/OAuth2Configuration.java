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

package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.get;
import static org.eclipse.osee.jaxrs.server.internal.JaxRsUtils.getBoolean;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jaxrs.server.internal.JaxRsUtils;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2Configuration {

   public static final String NAMESPACE = "jaxrs.oauth2.provider";

   private static String qualify(String value) {
      return String.format("%s.%s", NAMESPACE, value);
   }

   //@formatter:off
   public static final String OAUTH2_PROVIDER__SERVICE_ENABLED = qualify("enabled");
   public static final String OAUTH2_PROVIDER__ACCESS_TOKEN_EXPIRATION = qualify("access.token.expiration");
   public static final String OAUTH2_PROVIDER__CODE_GRANT_EXPIRATION = qualify("code.grant.expiration");
   public static final String OAUTH2_PROVIDER__REFRESH_TOKEN_EXPIRATION = qualify("refresh.token.expiration");
   public static final String OAUTH2_PROVIDER__SESSION_TOKEN_EXPIRATION = qualify("session.token.expiration");
   public static final String OAUTH2_PROVIDER__NONCE_ALLOWED_WINDOW = qualify("nonce.allowed.window");
   public static final String OAUTH2_PROVIDER__LOGIN_REDIRECT_URI = qualify("login.redirect.uri");
   public static final String OAUTH2_PROVIDER__LOGIN_REDIRECT_ERROR_URI = qualify("login.redirect.error.uri");
   public static final String OAUTH2_PROVIDER__IGNORE_LOGIN_REDIRECT_BASE_PATH = qualify("ignore.login.redirect.base.path");
   public static final String OAUTH2_PROVIDER__REALM = qualify("realm");
   public static final String OAUTH2_PROVIDER__AUDIENCE_IS_ENDPOINT_ADDRESS = qualify("audience.is.endpoint.address");
   public static final String OAUTH2_PROVIDER__BLOCK_UNSECURED_REQUESTS = qualify("block.unsecured.requests");
   public static final String OAUTH2_PROVIDER__CAN_SUPPORT_PUBLIC_CLIENTS = qualify("can.support.public.clients");
   public static final String OAUTH2_PROVIDER__USE_DEFAULT_LOGIN_PAGE = qualify("use.default.login.page");
   public static final String OAUTH2_PROVIDER__FILTER_CHECKS_FORM_DATA = qualify("filter.checks.form.data.for.token");
   public static final String OAUTH2_PROVIDER__HAWK_TOKEN_SUPPORTED = qualify("hawk.token.supported");
   public static final String OAUTH2_PROVIDER__PARTIAL_MATCH_SCOPE_VALIDATION = qualify("partial.match.scope.validation");
   public static final String OAUTH2_PROVIDER__REFRESH_TOKENS_ALLOWED = qualify("refresh.tokens.allowed");
   public static final String OAUTH2_PROVIDER__REPORT_CLIENT_ID = qualify("report.client.id");
   public static final String OAUTH2_PROVIDER__USE_REGISTERED_REDIRECT_URI = qualify("use.registered.redirect.uri");
   public static final String OAUTH2_PROVIDER__USE_USER_SUBJECT = qualify("use.user.subject");
   public static final String OAUTH2_PROVIDER__WRITE_CUSTOM_ERRORS = qualify("write.custom.errors");
   public static final String OAUTH2_PROVIDER__WRITE_OPTIONAL_PARAMETERS = qualify("write.optional.parameters");

   public static final String OAUTH2_PROVIDER__SECRET_KEY_ALGORITHM = qualify("secret.key.algorithm");
   public static final String OAUTH2_PROVIDER__ENCODED_SECRET_KEY = qualify("secret.key");
   //@formatter:on

   public static final boolean DEFAULT_OAUTH2_PROVIDER__SERVICE_ENABLED = false;
   public static final long DEFAULT_OAUTH2_PROVIDER__ACCESS_TOKEN_EXPIRATION = 10L * 60L * 1000L;
   public static final long DEFAULT_OAUTH2_PROVIDER__CODE_GRANT_EXPIRATION = 1L * 60L * 1000L;
   public static final long DEFAULT_OAUTH2_PROVIDER__REFRESH_TOKEN_EXPIRATION = 30L * 60L * 1000L;
   public static final long DEFAULT_OAUTH2_PROVIDER__SESSION_TOKEN_EXPIRATION = 60L;
   public static final long DEFAULT_OAUTH2_PROVIDER__NONCE_ALLOWED_WINDOW = 2000L;
   public static final URI DEFAULT_OAUTH2_PROVIDER__LOGIN_REDIRECT_URI = null;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__IGNORE_LOGIN_REDIRECT_BASE_PATH = false;
   public static final URI DEFAULT_OAUTH2_PROVIDER__LOGIN_ERROR_REDIRECT_URI = null;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__IGNORE_LOGIN_ERROR_REDIRECT_BASE_PATH = false;
   public static final String DEFAULT_OAUTH2_PROVIDER__REALM = "OAuth2-OSEE";
   public static final boolean DEFAULT_OAUTH2_PROVIDER__AUDIENCE_IS_ENDPOINT_ADDRESS = false;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__BLOCK_UNSECURED_REQUESTS = false;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__CAN_SUPPORT_PUBLIC_CLIENTS = true;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__USE_DEFAULT_LOGIN_PAGE = true;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__FILTER_CHECKS_FORM_DATA = false;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__HAWK_TOKEN_SUPPORTED = false;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__PARTIAL_MATCH_SCOPE_VALIDATION = false;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__REFRESH_TOKENS_ALLOWED = true;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__REPORT_CLIENT_ID = true;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__USE_REGISTERED_REDIRECT_URI = true;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__USE_USER_SUBJECT = false;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__WRITE_CUSTOM_ERRORS = true;
   public static final boolean DEFAULT_OAUTH2_PROVIDER__WRITE_OPTIONAL_PARAMETERS = true;
   public static final String DEFAULT_OAUTH2_PROVIDER__SECRET_KEY_ALGORITHM = null;
   public static final String DEFAULT_OAUTH2_PROVIDER__ENCODED_SECRET_KEY = null;

   public static OAuth2Configuration fromProperties(Map<String, Object> props) {
      OAuth2Configuration config = new OAuth2Configuration(props);
      return config;
   }

   private final Map<String, Object> props;

   private OAuth2Configuration(Map<String, Object> props) {
      this.props = props;
   }

   public boolean isEnabled() {
      return getBoolean(props, OAUTH2_PROVIDER__SERVICE_ENABLED, DEFAULT_OAUTH2_PROVIDER__SERVICE_ENABLED);
   }

   public boolean isServiceEnabled() {
      return getBoolean(props, OAUTH2_PROVIDER__SERVICE_ENABLED, DEFAULT_OAUTH2_PROVIDER__SERVICE_ENABLED);
   }

   public long getSessionTokenExpiration() {
      return JaxRsUtils.getLong(props, OAUTH2_PROVIDER__SESSION_TOKEN_EXPIRATION,
         DEFAULT_OAUTH2_PROVIDER__SESSION_TOKEN_EXPIRATION);
   }

   public long getAccessTokenExpiration() {
      return JaxRsUtils.getLong(props, OAUTH2_PROVIDER__ACCESS_TOKEN_EXPIRATION,
         DEFAULT_OAUTH2_PROVIDER__ACCESS_TOKEN_EXPIRATION);
   }

   public long getCodeGrantExpiration() {
      return JaxRsUtils.getLong(props, OAUTH2_PROVIDER__CODE_GRANT_EXPIRATION,
         DEFAULT_OAUTH2_PROVIDER__CODE_GRANT_EXPIRATION);
   }

   public long getRefreshTokenExpiration() {
      return JaxRsUtils.getLong(props, OAUTH2_PROVIDER__REFRESH_TOKEN_EXPIRATION,
         DEFAULT_OAUTH2_PROVIDER__REFRESH_TOKEN_EXPIRATION);
   }

   public long getNonceAllowedWindow() {
      return JaxRsUtils.getLong(props, OAUTH2_PROVIDER__NONCE_ALLOWED_WINDOW,
         DEFAULT_OAUTH2_PROVIDER__NONCE_ALLOWED_WINDOW);
   }

   public URI getLoginRedirectURI() {
      URI loginRedirectURI = DEFAULT_OAUTH2_PROVIDER__LOGIN_REDIRECT_URI;
      String value = get(props, OAUTH2_PROVIDER__LOGIN_REDIRECT_URI, null);
      if (Strings.isValid(value)) {
         try {
            loginRedirectURI = new URI(value);
         } catch (URISyntaxException ex) {
            // do nothing;
         }
      }
      return loginRedirectURI;
   }

   public URI getLoginRedirectErrorURI() {
      URI loginRedirectErrorURI = DEFAULT_OAUTH2_PROVIDER__LOGIN_ERROR_REDIRECT_URI;
      String value = get(props, OAUTH2_PROVIDER__LOGIN_REDIRECT_ERROR_URI, null);
      if (Strings.isValid(value)) {
         try {
            loginRedirectErrorURI = new URI(value);
         } catch (URISyntaxException ex) {
            // do nothing;
         }
      }
      return loginRedirectErrorURI;
   }

   public boolean isIgnoreLoginRedirectBasePath() {
      return getBoolean(props, OAUTH2_PROVIDER__IGNORE_LOGIN_REDIRECT_BASE_PATH,
         DEFAULT_OAUTH2_PROVIDER__IGNORE_LOGIN_REDIRECT_BASE_PATH);
   }

   public String getRealm() {
      return get(props, OAUTH2_PROVIDER__REALM, DEFAULT_OAUTH2_PROVIDER__REALM);
   }

   public boolean isAudienceIsEndpointAddress() {
      return getBoolean(props, OAUTH2_PROVIDER__AUDIENCE_IS_ENDPOINT_ADDRESS,
         DEFAULT_OAUTH2_PROVIDER__AUDIENCE_IS_ENDPOINT_ADDRESS);
   }

   public boolean isBlockUnsecureRequests() {
      return getBoolean(props, OAUTH2_PROVIDER__BLOCK_UNSECURED_REQUESTS,
         DEFAULT_OAUTH2_PROVIDER__BLOCK_UNSECURED_REQUESTS);
   }

   public boolean isCanSupportPublicClients() {
      return getBoolean(props, OAUTH2_PROVIDER__CAN_SUPPORT_PUBLIC_CLIENTS,
         DEFAULT_OAUTH2_PROVIDER__CAN_SUPPORT_PUBLIC_CLIENTS);
   }

   public boolean isFilterChecksFormDataForToken() {
      return getBoolean(props, OAUTH2_PROVIDER__FILTER_CHECKS_FORM_DATA,
         DEFAULT_OAUTH2_PROVIDER__FILTER_CHECKS_FORM_DATA);
   }

   public boolean isHawkTokenSupported() {
      return getBoolean(props, OAUTH2_PROVIDER__HAWK_TOKEN_SUPPORTED, DEFAULT_OAUTH2_PROVIDER__HAWK_TOKEN_SUPPORTED);
   }

   public boolean isPartialMatchScopeValidation() {
      return getBoolean(props, OAUTH2_PROVIDER__PARTIAL_MATCH_SCOPE_VALIDATION,
         DEFAULT_OAUTH2_PROVIDER__PARTIAL_MATCH_SCOPE_VALIDATION);
   }

   public boolean isRefreshTokenAllowed() {
      return getBoolean(props, OAUTH2_PROVIDER__REFRESH_TOKENS_ALLOWED,
         DEFAULT_OAUTH2_PROVIDER__REFRESH_TOKENS_ALLOWED);
   }

   public boolean isReportClientId() {
      return getBoolean(props, OAUTH2_PROVIDER__REPORT_CLIENT_ID, DEFAULT_OAUTH2_PROVIDER__REPORT_CLIENT_ID);
   }

   public boolean isUseRegisteredRedirectUriIfPossible() {
      return getBoolean(props, OAUTH2_PROVIDER__USE_REGISTERED_REDIRECT_URI,
         DEFAULT_OAUTH2_PROVIDER__USE_REGISTERED_REDIRECT_URI);
   }

   public boolean isUseUserSubject() {
      return getBoolean(props, OAUTH2_PROVIDER__USE_USER_SUBJECT, DEFAULT_OAUTH2_PROVIDER__USE_USER_SUBJECT);
   }

   public boolean isWriteCustomErrors() {
      return getBoolean(props, OAUTH2_PROVIDER__WRITE_CUSTOM_ERRORS, DEFAULT_OAUTH2_PROVIDER__WRITE_CUSTOM_ERRORS);
   }

   public boolean isWriteOptionalParameters() {
      return getBoolean(props, OAUTH2_PROVIDER__WRITE_OPTIONAL_PARAMETERS,
         DEFAULT_OAUTH2_PROVIDER__WRITE_OPTIONAL_PARAMETERS);
   }

   public String getSecretKeyAlgorithm() {
      return get(props, OAUTH2_PROVIDER__SECRET_KEY_ALGORITHM, DEFAULT_OAUTH2_PROVIDER__SECRET_KEY_ALGORITHM);
   }

   public String getEncodedSecretKey() {
      return get(props, OAUTH2_PROVIDER__ENCODED_SECRET_KEY, DEFAULT_OAUTH2_PROVIDER__ENCODED_SECRET_KEY);
   }
}