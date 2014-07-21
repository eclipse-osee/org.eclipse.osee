/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider;

import java.util.Collections;
import java.util.List;
import javax.crypto.SecretKey;
import org.apache.cxf.rs.security.oauth2.common.AccessTokenRegistration;
import org.apache.cxf.rs.security.oauth2.common.Client;
import org.apache.cxf.rs.security.oauth2.common.OAuthError;
import org.apache.cxf.rs.security.oauth2.common.OAuthPermission;
import org.apache.cxf.rs.security.oauth2.common.ServerAccessToken;
import org.apache.cxf.rs.security.oauth2.common.UserSubject;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeDataProvider;
import org.apache.cxf.rs.security.oauth2.grants.code.AuthorizationCodeRegistration;
import org.apache.cxf.rs.security.oauth2.grants.code.ServerAuthorizationCodeGrant;
import org.apache.cxf.rs.security.oauth2.provider.OAuthServiceException;
import org.apache.cxf.rs.security.oauth2.tokens.bearer.BearerAccessToken;
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.crypto.ModelEncryptionSupport;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.endpoints.ClientDataProvider;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthStorage;

/**
 * @author Roberto E. Escobar
 */
public class CxfOAuthDataProvider implements AuthorizationCodeDataProvider, ClientDataProvider {

   private final JaxRsOAuthStorage storage;

   private boolean isRefreshTokenAllowed;
   private long accessTokenExpiration;
   private long refreshTokenExpiration;
   private long codeGrantExpiration;
   private SecretKey secretKey;

   public CxfOAuthDataProvider(JaxRsOAuthStorage storage) {
      super();
      this.storage = storage;
   }

   public SecretKey getSecretKey() {
      return secretKey;
   }

   public void setSecretKey(SecretKey secretKey) {
      this.secretKey = secretKey;
   }

   public boolean isRefreshTokenAllowed() {
      return isRefreshTokenAllowed;
   }

   public long getAccessTokenExpiration() {
      return accessTokenExpiration;
   }

   public long getRefreshTokenExpiration() {
      return refreshTokenExpiration;
   }

   public long getCodeGrantExpiration() {
      return codeGrantExpiration;
   }

   public void setRefreshTokenAllowed(boolean isRefreshTokenAllowed) {
      this.isRefreshTokenAllowed = isRefreshTokenAllowed;
   }

   public void setAccessTokenExpiration(long accessTokenExpiration) {
      this.accessTokenExpiration = accessTokenExpiration;
   }

   public void setRefreshTokenExpiration(long refreshTokenExpiration) {
      this.refreshTokenExpiration = refreshTokenExpiration;
   }

   public void setCodeGrantExpiration(long codeGrantExpiration) {
      this.codeGrantExpiration = codeGrantExpiration;
   }

   @Override
   public Client createClient() {
      return null;
   }

   @Override
   public Client getClient(String clientId) {
      return storage.getClient(clientId);
   }

   @Override
   public ServerAuthorizationCodeGrant createCodeGrant(AuthorizationCodeRegistration reg) {
      long expiresIn = getCodeGrantExpiration();

      ServerAuthorizationCodeGrant grant = new ServerAuthorizationCodeGrant(reg.getClient(), expiresIn);
      grant.setAudience(reg.getAudience());
      grant.setRedirectUri(reg.getRedirectUri());
      grant.setClientCodeVerifier(reg.getClientCodeVerifier());
      grant.setSubject(reg.getSubject());
      grant.setApprovedScopes(getApprovedScopes(reg.getRequestedScope(), reg.getApprovedScope()));
      grant.setClientCodeVerifier(reg.getClientCodeVerifier());

      String encrypted = ModelEncryptionSupport.encryptCodeGrant(grant, getSecretKey());
      grant.setCode(encrypted);

      storage.storeCodeGrant(encrypted);
      return grant;
   }

   @Override
   public ServerAuthorizationCodeGrant removeCodeGrant(String code) {
      String codeGrant = storage.getCodeGrant(code);
      ServerAuthorizationCodeGrant grant = null;
      if (codeGrant != null) {
         storage.removeCodeGrant(codeGrant);
         grant = ModelEncryptionSupport.decryptCodeGrant(this, codeGrant, getSecretKey());
      }
      return grant;
   }

   @Override
   public ServerAccessToken createAccessToken(AccessTokenRegistration reg) {
      Client client = reg.getClient();
      List<String> approvedScopes = getApprovedScopes(reg.getRequestedScope(), reg.getApprovedScope());
      List<OAuthPermission> permissions = convertScopeToPermissions(client, approvedScopes);

      BearerAccessToken token = new BearerAccessToken(client, getAccessTokenExpiration());
      token.setSubject(reg.getSubject());

      token.setAudience(reg.getAudience());
      token.setGrantType(reg.getGrantType());
      token.setParameters(Collections.singletonMap("param", "value"));
      token.setScopes(permissions);

      String encryptedRefreshToken = null;
      if (isRefreshTokenAllowed()) {
         RefreshToken refreshToken = new RefreshToken(client, getRefreshTokenExpiration());
         encryptedRefreshToken = ModelEncryptionSupport.encryptRefreshToken(refreshToken, getSecretKey());
         token.setRefreshToken(encryptedRefreshToken);
      }

      String encryptedAccessToken = ModelEncryptionSupport.encryptAccessToken(token, getSecretKey());
      token.setTokenKey(encryptedAccessToken);

      storage.storeAccessToken(encryptedAccessToken);
      if (encryptedRefreshToken != null) {
         storage.storeRefreshToken(encryptedRefreshToken, encryptedAccessToken);
      }
      return token;
   }

   @Override
   public ServerAccessToken getAccessToken(String accessToken) {
      return ModelEncryptionSupport.decryptAccessToken(this, accessToken, getSecretKey());
   }

   @Override
   public ServerAccessToken refreshAccessToken(Client client, String refreshToken, List<String> requestedScopes) {
      if (!isRefreshTokenAllowed()) {
         OAuthError error = new OAuthError(OAuthConstants.INVALID_REQUEST, "Refresh tokens are not allowed.");
         throw new OAuthServiceException(error);
      }
      SecretKey secretKey = getSecretKey();

      String encryptedAccessToken = storage.getAccessTokenByRefreshToken(refreshToken);
      if (encryptedAccessToken != null) {
         storage.removeRefreshToken(refreshToken);
      }

      ServerAccessToken token = ModelEncryptionSupport.decryptAccessToken(this, encryptedAccessToken, secretKey);
      storage.removeAccessToken(token.getTokenKey());

      RefreshToken newRefreshToken = new RefreshToken(token.getClient(), getRefreshTokenExpiration());
      String newEncryptedRefreshToken = ModelEncryptionSupport.encryptRefreshToken(newRefreshToken, secretKey);
      token.setRefreshToken(newEncryptedRefreshToken);

      String newEncryptedAccessToken = ModelEncryptionSupport.encryptAccessToken(token, secretKey);
      storage.storeAccessToken(newEncryptedAccessToken);
      storage.storeRefreshToken(newEncryptedRefreshToken, newEncryptedAccessToken);
      token.setTokenKey(newEncryptedAccessToken);
      return token;
   }

   @Override
   public void removeAccessToken(ServerAccessToken accessToken) {
      storage.removeAccessToken(accessToken.getTokenKey());
   }

   @Override
   public void revokeToken(Client client, String token, String tokenTypeHint) {
      // the fast way: if it is the refresh token then there will be a matching value for it
      String accessToken = storage.getAccessTokenByRefreshToken(token);
      if (accessToken != null) {
         storage.removeRefreshToken(token);
      }
      // if no matching value then the token parameter is access token key
      storage.removeAccessToken(accessToken == null ? token : accessToken);
   }

   @Override
   public ServerAccessToken getPreauthorizedToken(Client client, List<String> requestedScopes, UserSubject subject, String grantType) {
      // This is an optimization useful in cases where a client requests an authorization code: 
      // if a user has already provided a given client with a pre-authorized token then challenging 
      // a user with yet another form asking for the authorization is redundant
      String clientId = client.getClientId();
      String subjectId = subject.getId();
      String encryptedToken = storage.getPreauthorizedToken(clientId, subjectId, grantType);
      ServerAccessToken token = null;
      if (encryptedToken != null) {
         token = ModelEncryptionSupport.decryptAccessToken(this, encryptedToken, getSecretKey());
      }
      return token;
   }

   @Override
   public List<OAuthPermission> convertScopeToPermissions(Client client, List<String> requestedScope) {
      return Collections.emptyList();
   }

   private List<String> getApprovedScopes(List<String> requestedScopes, List<String> approvedScopes) {
      return approvedScopes.isEmpty() ? requestedScopes : approvedScopes;
   }

}