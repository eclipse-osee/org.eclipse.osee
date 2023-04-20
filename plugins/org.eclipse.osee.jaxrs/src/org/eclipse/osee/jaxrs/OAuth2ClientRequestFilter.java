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

package org.eclipse.osee.jaxrs;

import static org.eclipse.osee.jaxrs.OAuth2Util.newException;
import java.net.URI;
import javax.crypto.SecretKey;
import javax.ws.rs.client.ClientRequestContext;
import javax.ws.rs.client.ClientRequestFilter;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.ext.Provider;
import org.apache.cxf.rs.security.oauth2.common.ClientAccessToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
@Provider
public class OAuth2ClientRequestFilter implements ClientRequestFilter {

   public static interface ClientAccessTokenCache {

      ClientAccessToken get(URI requestedUri);

      void store(URI requestedUri, ClientAccessToken token);

   }

   private final OAuth2Flows flows;
   private final OAuth2Serializer serializer;

   public OAuth2ClientRequestFilter(OAuth2Flows flows, OAuth2Serializer serializer) {
      super();
      this.flows = flows;
      this.serializer = serializer;
   }

   private String redirectUri;
   private String scopes;
   private String secretKeyEncoded;
   private String secretKeyAlgorithm;

   private JaxRsTokenStore tokenStore;
   private JaxRsConfirmAccessHandler handler;

   private ClientAccessTokenCache cache;

   private volatile SecretKey secretKey;
   private boolean failOnRefreshTokenError;

   public void setRedirectUri(String redirectUri) {
      this.redirectUri = OAuth2Util.OAUTH2_OOB_CALLBACK.equals(redirectUri) ? null : redirectUri;
   }

   public void setScopes(String scopes) {
      this.scopes = scopes;
   }

   public void setTokenStore(JaxRsTokenStore tokenStore) {
      this.tokenStore = tokenStore;
   }

   public void setTokenHandler(JaxRsConfirmAccessHandler handler) {
      this.handler = handler;
   }

   public void setSecretKeyEncoded(String secretKeyEncoded) {
      this.secretKeyEncoded = secretKeyEncoded;
   }

   public void setSecretKeyAlgorithm(String secretKeyAlgorithm) {
      this.secretKeyAlgorithm = secretKeyAlgorithm;
   }

   public void setFailOnRefreshTokenError(boolean failOnRefreshTokenError) {
      this.failOnRefreshTokenError = failOnRefreshTokenError;
   }

   private boolean isTokenEncryptionEnabled() {
      return Strings.isValid(secretKeyEncoded);
   }

   private boolean isFailOnRefreshTokenError() {
      return failOnRefreshTokenError;
   }

   public void setClientAccessTokenCache(ClientAccessTokenCache cache) {
      this.cache = cache;
   }

   private SecretKey getSecretKey() {
      if (secretKey == null) {
         secretKey = serializer.decodeSecretKey(secretKeyEncoded, secretKeyAlgorithm);
      }
      return secretKey;
   }

   @Override
   public void filter(ClientRequestContext context) {
      URI requestedUri = context.getUri();
      ClientAccessToken accessToken = getAccessToken(requestedUri);
      String tokenType = accessToken.getTokenType();

      String authHeader;
      if (OAuthConstants.BEARER_TOKEN_TYPE.equals(tokenType)) {
         authHeader = OAuth2Util.asAuthorizationHeader(accessToken);
      } else if (OAuthConstants.HAWK_TOKEN_TYPE.equals(tokenType) && requestedUri != null) {
         String httpMethod = context.getMethod();
         authHeader = OAuth2Util.asAuthorizationHeader(accessToken, httpMethod, requestedUri);
      } else {
         throw newException("Unsupported token type exception [%s]", tokenType);
      }
      context.getHeaders().addFirst(HttpHeaders.AUTHORIZATION, authHeader);
   }

   private ClientAccessToken getAccessToken(URI requestedUri) {
      ClientAccessToken token = null;
      if (cache != null) {
         token = cache.get(requestedUri);
      }
      if (token == null) {
         token = getFromStorage(requestedUri);
      }
      if (!isValidToken(token)) {
         token = getNewToken(token);
         if (isValidToken(token)) {
            if (cache != null) {
               cache.store(requestedUri, token);
            }
            store(requestedUri, token);
         }
      }
      return token;
   }

   private ClientAccessToken getFromStorage(URI requestedUri) {
      ClientAccessToken token = null;
      if (tokenStore != null) {
         String storedToken = tokenStore.getToken(requestedUri);
         if (Strings.isValid(storedToken)) {
            if (isTokenEncryptionEnabled()) {
               token = serializer.decryptAccessToken(storedToken, getSecretKey());
            } else {
               token = serializer.fromJson(storedToken);
            }
         }
      }
      return token;
   }

   private void store(URI requestedUri, ClientAccessToken token) {
      if (tokenStore != null) {
         String tokenString;
         if (isTokenEncryptionEnabled()) {
            tokenString = serializer.encryptAccessToken(token, getSecretKey());
         } else {
            tokenString = serializer.toJson(token);
         }
         tokenStore.storeToken(requestedUri, tokenString);
      }
   }

   private ClientAccessToken getNewToken(ClientAccessToken oldToken) {
      ClientAccessToken newToken = null;
      if (oldToken != null && isExpired(oldToken) && Strings.isValid(oldToken.getRefreshToken())) {
         try {
            newToken = flows.refreshFlow(oldToken, flows.generateState());
         } catch (Exception ex) {
            if (isFailOnRefreshTokenError()) {
               throw newException(ex, "Error while attempting to refresh access token");
            } else {
               // do nothing if refresh token fails - ignore and attempt to get a new access token
            }
         }
      }
      if (!isValidToken(newToken)) {
         newToken = flows.authorizationCodeFlow(handler, flows.generateState(), scopes, redirectUri);
      }
      return newToken;
   }

   private boolean isValidToken(ClientAccessToken token) {
      return token != null && !isExpired(token);
   }

   private boolean isExpired(ClientAccessToken storedToken) {
      return OAuthUtils.isExpired(storedToken.getIssuedAt(), storedToken.getExpiresIn());
   }
}