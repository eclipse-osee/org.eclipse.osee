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

package org.eclipse.osee.orcs.account.admin.internal.oauth;

import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import java.util.List;
import org.eclipse.osee.account.admin.OseePrincipal;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthStorage;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;
import org.eclipse.osee.jaxrs.server.security.OAuthCodeGrant;
import org.eclipse.osee.jaxrs.server.security.OAuthToken;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;

/**
 * @author Roberto E. Escobar
 */
public class JdbcJaxRsOAuthStorage implements JaxRsOAuthStorage {

   private Log logger;
   private JdbcService jdbcService;

   private AuthCodeGrantStorage authCodeGrantStorage;
   private TokenStorage tokenStorage;
   private ClientCredentialStorage credentialStorage;
   private ClientStorage clientStorage;
   private OrcsApi orcsApi;

   public void setOrcsApi(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void start() {
      JdbcClient jdbcClient = jdbcService.getClient();
      authCodeGrantStorage = new AuthCodeGrantStorage(logger, jdbcClient);
      tokenStorage = new TokenStorage(logger, jdbcClient);
      credentialStorage = new ClientCredentialStorage(logger, jdbcClient);
      clientStorage = new ClientStorage(orcsApi, COMMON);
   }

   private ClientStorage getClientStorage() {
      return clientStorage;
   }

   @Override
   public OAuthCodeGrant getCodeGrant(String code) {
      return authCodeGrantStorage.getByCode(code);
   }

   @Override
   public void storeCodeGrant(OAuthCodeGrant code) {
      authCodeGrantStorage.insert(code);
   }

   @Override
   public void removeCodeGrant(OAuthCodeGrant code) {
      authCodeGrantStorage.delete(code);
   }

   @Override
   public Iterable<OAuthToken> getAccessTokensByRefreshToken(String refreshToken) {
      return tokenStorage.getAccessTokenByRefreshToken(refreshToken);
   }

   @Override
   public OAuthToken getPreauthorizedToken(long clientUuid, long subjectUuid, String grantType) {
      return tokenStorage.getPreauthorizedToken(clientUuid, subjectUuid, grantType);
   }

   @Override
   public void storeToken(OAuthToken... tokens) {
      tokenStorage.insertTokens(tokens);
   }

   @Override
   public void relateTokens(OAuthToken parentToken, OAuthToken childToken) {
      tokenStorage.relateTokens(parentToken, childToken);
   }

   @Override
   public void removeToken(Iterable<OAuthToken> tokens) {
      tokenStorage.deleteToken(tokens);
   }

   @Override
   public void removeTokenByKey(String tokenKey) {
      tokenStorage.deleteTokenByKey(tokenKey);
   }

   @Override
   public ArtifactId storeClient(OseePrincipal principal, OAuthClient client) {
      ArtifactId clientArtId;
      ClientStorage clientStorage = getClientStorage();

      boolean exists = clientStorage.exists(client.getClientUuid());
      if (exists) {
         clientStorage.update(principal, client);
         clientArtId = ArtifactId.valueOf(client.getClientUuid());
      } else {
         clientArtId = clientStorage.insert(principal, client);
      }

      Long clientId = clientArtId.getId();
      ArtifactReadable artifact = clientStorage.getClientByClientId(clientArtId).getExactlyOne();
      Long applicationId = artifact.getId();

      OAuthClientCredential credential = asCredential(clientId, client, applicationId);
      if (credentialStorage.getByClientIdAndApplicationId(clientId, artifact) != null) {
         credentialStorage.update(credential);
      } else {
         credentialStorage.insert(credential);
      }
      return clientArtId;
   }

   OAuthClientCredential asCredential(Long clientUuid, OAuthClient client, Long applicationId) {
      long subjectId = client.getSubjectId();

      String clientKey = client.getClientId();
      String clientSecret = client.getClientSecret();

      List<String> clientCerts = client.getApplicationCertificates();
      return credentialStorage.newCredential(clientUuid, applicationId, subjectId, clientKey, clientSecret,
         clientCerts);
   }

   @Override
   public void removeClient(OseePrincipal principal, OAuthClient client) {
      getClientStorage().delete(principal, client);

      OAuthClientCredential credential = asCredential(client.getClientUuid(), client, -1L);
      credentialStorage.delete(credential);
   }

   @Override
   public long getClientUuidByKey(String clientKey) {
      OAuthClientCredential credential = credentialStorage.getByClientKey(clientKey);
      return credential != null ? credential.getClientId() : -1L;
   }

   @Override
   public OAuthClient getClientByClientGuid(String guid) {
      OAuthClient client = null;
      ClientStorage clientStorage = getClientStorage();
      ArtifactReadable artifact = clientStorage.getClientByClientGuid(guid).getOneOrDefault(ArtifactReadable.SENTINEL);
      if (artifact.isValid()) {
         OAuthClientCredential credential = credentialStorage.getByApplicationId(artifact.getId());
         client = clientStorage.newClient(artifact, credential);
      }
      return client;
   }

   @Override
   public OAuthClient getClientByClientId(ArtifactId id) {
      OAuthClient client = null;
      ClientStorage clientStorage = getClientStorage();
      ArtifactReadable artifact = clientStorage.getClientByClientId(id).getOneOrDefault(ArtifactReadable.SENTINEL);
      if (artifact.isValid()) {
         OAuthClientCredential credential = credentialStorage.getByApplicationId(artifact.getId());
         client = clientStorage.newClient(artifact, credential);
      }
      return client;
   }

   @Override
   public OAuthClient getClientByClientKey(String clientKey) {
      OAuthClientCredential credential = credentialStorage.getByClientKey(clientKey);
      OAuthClient client = null;
      if (credential != null) {
         ArtifactId applicationId = ArtifactId.valueOf(credential.getApplicationId());

         ClientStorage clientStorage = getClientStorage();
         ArtifactReadable artifact =
            clientStorage.getClientByApplicationId(applicationId).getOneOrDefault(ArtifactReadable.SENTINEL);
         if (artifact.isValid()) {
            client = clientStorage.newClient(artifact, credential);
         }
      }
      return client;
   }
}