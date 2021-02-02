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
import org.apache.cxf.rs.security.oauth2.tokens.refresh.RefreshToken;
import org.apache.cxf.rs.security.oauth2.utils.OAuthConstants;
import org.apache.cxf.rs.security.oauth2.utils.OAuthUtils;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters.AccessToken;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters.AuthorizationCode;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters.OAuthEncryption;
import org.eclipse.osee.jaxrs.server.internal.security.oauth2.provider.adapters.RefreshOAuthToken;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthStorage;
import org.eclipse.osee.jaxrs.server.security.OAuthCodeGrant;
import org.eclipse.osee.jaxrs.server.security.OAuthToken;
import org.eclipse.osee.jaxrs.server.security.OAuthTokenType;
import org.eclipse.osee.jaxrs.server.session.SessionData;

/**
 * @author Roberto E. Escobar
 */
public class OAuth2DataProvider implements AuthorizationCodeDataProvider {

   private final OAuthEncryption serializer;
   private final JaxRsOAuthStorage storage;
   private final ClientProvider clientProvider;
   private final SubjectProvider subjectProvider;

   private boolean isRefreshTokenAllowed;
   private long accessTokenExpiration;
   private long refreshTokenExpiration;
   private long codeGrantExpiration;

   private String secretKeyEncoded;
   private String secretKeyAlgorithm;

   private volatile SecretKey secretKey;

   public OAuth2DataProvider(ClientProvider clientProvider, SubjectProvider subjectProvider, OAuthEncryption serializer, JaxRsOAuthStorage storage) {
      super();
      this.clientProvider = clientProvider;
      this.subjectProvider = subjectProvider;
      this.serializer = serializer;
      this.storage = storage;
   }

   public void setSecretKeyEncoded(String secretKeyEncoded) {
      this.secretKeyEncoded = secretKeyEncoded;
   }

   public void setSecretKeyAlgorithm(String secretKeyAlgorithm) {
      this.secretKeyAlgorithm = secretKeyAlgorithm;
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

   private SecretKey getSecretKey() {
      if (secretKey == null) {
         secretKey = serializer.decodeSecretKey(secretKeyEncoded, secretKeyAlgorithm);
      }
      return secretKey;
   }

   private long getClientId(Client client) {
      return clientProvider.getClientId(client);
   }

   private long getSubjectId(UserSubject subject) {
      return subjectProvider.getSubjectId(subject);
   }

   @Override
   public Client getClient(String clientId) {
      Client client = clientProvider.getClient(clientId);
      if (client == null) {
         OAuthError error = new OAuthError(OAuthConstants.INVALID_CLIENT, "Client Id not found.");
         throw new OAuthServiceException(error);
      }
      return client;
   }

   @Override
   public ServerAuthorizationCodeGrant createCodeGrant(AuthorizationCodeRegistration reg) {
      long expiresIn = getCodeGrantExpiration();

      long uuid = Lib.generateUuid();
      long clientId = getClientId(reg.getClient());
      long subjectId = getSubjectId(reg.getSubject());

      AuthorizationCode grant = new AuthorizationCode(uuid, clientId, subjectId);
      grant.setCode(OAuthUtils.generateRandomTokenKey());
      grant.setIssuedAt(OAuthUtils.getIssuedAt());
      grant.setExpiresIn(expiresIn);
      grant.setClient(reg.getClient());
      grant.setSubject(reg.getSubject());

      grant.setAudience(reg.getAudience());
      grant.setRedirectUri(reg.getRedirectUri());
      grant.setCodeVerifier(reg.getNonce());
      reg.setClientCodeChallenge(reg.getClientCodeChallenge());
      grant.setApprovedScopes(getApprovedScopes(reg.getRequestedScope(), reg.getApprovedScope()));
      grant.setCodeVerifier(reg.getNonce());
      reg.setClientCodeChallenge(reg.getClientCodeChallenge());

      String encrypted = serializer.encryptCodeGrant(grant, getSecretKey());
      grant.setCode(encrypted);

      storage.storeCodeGrant(grant);
      return grant;
   }

   @Override
   public ServerAuthorizationCodeGrant removeCodeGrant(String code) {
      OAuthCodeGrant codeGrant = storage.getCodeGrant(code);
      ServerAuthorizationCodeGrant toReturn = null;
      if (codeGrant != null) {
         String encryptedCode = codeGrant.getCode();
         toReturn = serializer.decryptCodeGrant(this, encryptedCode, getSecretKey());
         storage.removeCodeGrant(codeGrant);
      }
      return toReturn;
   }

   @Override
   public ServerAccessToken createAccessToken(AccessTokenRegistration reg) {
      Client client = reg.getClient();
      List<String> approvedScopes = getApprovedScopes(reg.getRequestedScope(), reg.getApprovedScope());
      List<OAuthPermission> permissions = convertScopeToPermissions(client, approvedScopes);

      long uuid = Lib.generateUuid();
      long clientId = getClientId(reg.getClient());
      long subjectId = getSubjectId(reg.getSubject());

      OAuthTokenType type = OAuthTokenType.BEARER_TOKEN;

      AccessToken accessToken = new AccessToken(uuid, clientId, subjectId, type);
      accessToken.setTokenKey(OAuthUtils.generateRandomTokenKey());
      accessToken.setIssuedAt(OAuthUtils.getIssuedAt());
      accessToken.setExpiresIn(getAccessTokenExpiration());
      accessToken.setClient(client);
      accessToken.setSubject(reg.getSubject());

      accessToken.setTokenType(type.getType());
      accessToken.setGrantType(reg.getGrantType());
      accessToken.setScopes(permissions);

      RefreshOAuthToken refreshToken = null;
      if (isRefreshTokenAllowed()) {
         refreshToken = newRefreshToken(accessToken, clientId, subjectId, getSecretKey());
         accessToken.setRefreshToken(refreshToken.getTokenKey());
      }

      String encryptedAccessToken = serializer.encryptAccessToken(accessToken, getSecretKey());
      accessToken.setTokenKey(encryptedAccessToken);

      if (refreshToken != null) {
         storage.storeToken(accessToken, refreshToken);
         storage.relateTokens(refreshToken, accessToken);
      } else {
         storage.storeToken(accessToken);
      }
      return accessToken;
   }

   public String createSessionToken(SessionData session) {
      return serializer.encryptSessionToken(session, getSecretKey());
   }

   @Override
   public ServerAccessToken getAccessToken(String accessToken) {
      return serializer.decryptAccessToken(this, accessToken, getSecretKey());
   }

   @Override
   public ServerAccessToken refreshAccessToken(Client client, String refreshToken, List<String> requestedScopes) {
      if (!isRefreshTokenAllowed()) {
         OAuthError error = new OAuthError(OAuthConstants.INVALID_REQUEST, "Refresh tokens are not allowed.");
         throw new OAuthServiceException(error);
      }
      SecretKey secretKey = getSecretKey();

      RefreshToken oldRefreshToken = serializer.decryptRefreshToken(this, refreshToken, secretKey);

      Iterable<OAuthToken> tokens = storage.getAccessTokensByRefreshToken(refreshToken);
      storage.removeToken(tokens);
      storage.removeTokenByKey(refreshToken);

      long uuid = Lib.generateUuid();
      long clientId = getClientId(oldRefreshToken.getClient());
      long subjectId = getSubjectId(oldRefreshToken.getSubject());

      OAuthTokenType type = OAuthTokenType.BEARER_TOKEN;

      AccessToken newAccessToken = new AccessToken(uuid, clientId, subjectId, type);
      newAccessToken.setTokenKey(OAuthUtils.generateRandomTokenKey());
      newAccessToken.setIssuedAt(OAuthUtils.getIssuedAt());
      newAccessToken.setExpiresIn(getAccessTokenExpiration());
      newAccessToken.setClient(oldRefreshToken.getClient());
      newAccessToken.setSubject(oldRefreshToken.getSubject());

      newAccessToken.setTokenType(type.getType());
      newAccessToken.setGrantType(oldRefreshToken.getGrantType());
      newAccessToken.setScopes(oldRefreshToken.getScopes());

      RefreshOAuthToken newRefreshToken = newRefreshToken(newAccessToken, clientId, subjectId, getSecretKey());
      newAccessToken.setRefreshToken(newRefreshToken.getTokenKey());

      String newEncryptedAccessToken = serializer.encryptAccessToken(newAccessToken, secretKey);
      newAccessToken.setTokenKey(newEncryptedAccessToken);

      storage.storeToken(newAccessToken, newRefreshToken);
      storage.relateTokens(newRefreshToken, newAccessToken);
      return newAccessToken;
   }

   private RefreshOAuthToken newRefreshToken(AccessToken token, long clientId, long subjectId, SecretKey secretKey) {
      long refreshUuid = Lib.generateUuid();

      RefreshOAuthToken toReturn = new RefreshOAuthToken(refreshUuid, clientId, subjectId);
      toReturn.setTokenKey(OAuthUtils.generateRandomTokenKey());
      toReturn.setIssuedAt(OAuthUtils.getIssuedAt());
      toReturn.setExpiresIn(getRefreshTokenExpiration());
      toReturn.setClient(token.getClient());
      toReturn.setSubject(token.getSubject());

      toReturn.setAudiences(token.getAudiences());
      toReturn.setGrantType(token.getGrantType());
      toReturn.setScopes(token.getScopes());
      String encryptedRefreshToken = serializer.encryptRefreshToken(toReturn, secretKey);
      toReturn.setTokenKey(encryptedRefreshToken);
      return toReturn;
   }

   public void removeAccessToken(ServerAccessToken accessToken) {
      storage.removeTokenByKey(accessToken.getTokenKey());
   }

   @Override
   public void revokeToken(Client client, String tokenKey, String tokenTypeHint) {
      Iterable<OAuthToken> tokens = storage.getAccessTokensByRefreshToken(tokenKey);
      storage.removeToken(tokens);
      storage.removeTokenByKey(tokenKey);
   }

   @Override
   public ServerAccessToken getPreauthorizedToken(Client client, List<String> requestedScopes, UserSubject subject, String grantType) {
      // This is an optimization useful in cases where a client requests an authorization code:
      // if a user has already provided a given client with a pre-authorized token then challenging
      // a user with yet another form asking for the authorization is redundant
      long clientId = getClientId(client);
      long subjectId = getSubjectId(subject);
      OAuthToken accessToken = storage.getPreauthorizedToken(clientId, subjectId, grantType);
      ServerAccessToken token = null;
      boolean isExpired = false;

      if (accessToken != null) {
         isExpired = OAuthUtils.isExpired(accessToken.getIssuedAt(), accessToken.getExpiresIn());
         if (isExpired) {
            revokeToken(client, accessToken.getTokenKey(), accessToken.getTokenType());
         } else {
            token = getTokenHelper(client, grantType, accessToken, token);
         }
      }

      if (token != null) {
         boolean isRolesOutdated = isRolesOutdated(subject, token);
         if (isRolesOutdated) {
            revokeAllTokens(client, subjectId, grantType);
            token = null;
         }
      }

      return token;

   }

   private ServerAccessToken getTokenHelper(Client client, String grantType, OAuthToken accessToken, ServerAccessToken token) {
      boolean isExpired;
      switch (accessToken.getType()) {
         case BEARER_TOKEN:
         case HAWK_TOKEN:
            token = serializer.decryptAccessToken(this, accessToken.getTokenKey(), getSecretKey());
            break;
         case REFRESH_TOKEN:
            Iterable<OAuthToken> accessTokens = storage.getAccessTokensByRefreshToken(accessToken.getTokenKey());
            for (OAuthToken entry : accessTokens) {
               isExpired = OAuthUtils.isExpired(entry.getIssuedAt(), entry.getExpiresIn());

               if (!isExpired && entry.getGrantType().equals(grantType)) {
                  token = serializer.decryptAccessToken(this, entry.getTokenKey(), getSecretKey());
               } else if (isExpired) {
                  revokeToken(client, entry.getTokenKey(), entry.getTokenType());
               }
               if (token != null) {
                  break;
               }
            }
            break;
         default:
            // Do nothing
            break;
      }
      return token;
   }

   private boolean isRolesOutdated(UserSubject subject, ServerAccessToken token) {
      List<String> oldRoles = token.getSubject().getRoles();
      List<String> newRoles = subject.getRoles();

      boolean equalLists = oldRoles.size() == newRoles.size() && oldRoles.containsAll(newRoles);

      return !equalLists;
   }

   private void revokeAllTokens(Client client, long subjectId, String grantType) {
      long clientId = getClientId(client);
      OAuthToken preauthorizedToken = storage.getPreauthorizedToken(clientId, subjectId, grantType);
      while (preauthorizedToken != null) {
         Iterable<OAuthToken> accessTokens = storage.getAccessTokensByRefreshToken(preauthorizedToken.getTokenKey());

         for (OAuthToken entry : accessTokens) {
            revokeToken(client, entry.getTokenKey(), "");
         }
         revokeToken(client, preauthorizedToken.getTokenKey(), "");

         preauthorizedToken = storage.getPreauthorizedToken(clientId, subjectId, grantType);
      }
   }

   @Override
   public List<OAuthPermission> convertScopeToPermissions(Client client, List<String> requestedScope) {
      return Collections.emptyList();
   }

   private List<String> getApprovedScopes(List<String> requestedScopes, List<String> approvedScopes) {
      return approvedScopes.isEmpty() ? requestedScopes : approvedScopes;
   }

   @Override
   public List<ServerAccessToken> getAccessTokens(Client arg0, UserSubject arg1) throws OAuthServiceException {
      return null;
   }

   @Override
   public List<RefreshToken> getRefreshTokens(Client arg0, UserSubject arg1) throws OAuthServiceException {
      return null;
   }

   @Override
   public List<ServerAuthorizationCodeGrant> getCodeGrants(Client arg0, UserSubject arg1) throws OAuthServiceException {
      return null;
   }
}