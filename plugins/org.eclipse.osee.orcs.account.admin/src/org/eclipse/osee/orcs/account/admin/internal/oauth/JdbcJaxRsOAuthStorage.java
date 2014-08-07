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
package org.eclipse.osee.orcs.account.admin.internal.oauth;

import java.util.List;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseePrincipal;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthStorage;
import org.eclipse.osee.jaxrs.server.security.OAuthClient;
import org.eclipse.osee.jaxrs.server.security.OAuthCodeGrant;
import org.eclipse.osee.jaxrs.server.security.OAuthToken;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ArtifactReadable;

/**
 * @author Roberto E. Escobar
 */
public class JdbcJaxRsOAuthStorage implements JaxRsOAuthStorage {

   private Log logger;
   private IOseeDatabaseService dbService;

   private AuthCodeGrantStorage authCodeGrantStorage;
   private TokenStorage tokenStorage;
   private ClientCredentialStorage credentialStorage;
   private ClientStorageProvider clientStorageProvider;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setClientStorageProvider(ClientStorageProvider clientStorageProvider) {
      this.clientStorageProvider = clientStorageProvider;
   }

   public void start() {
      authCodeGrantStorage = new AuthCodeGrantStorage(logger, dbService);
      tokenStorage = new TokenStorage(logger, dbService);
      credentialStorage = new ClientCredentialStorage(logger, dbService);
   }

   public void stop() {
      //
   }

   private ClientStorage getClientStorage() {
      return clientStorageProvider.get();
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
   public void storeClient(OseePrincipal principal, OAuthClient client) {
      ClientStorage clientStorage = getClientStorage();

      boolean exists = clientStorage.exists(client.getGuid());
      if (exists) {
         clientStorage.update(principal, client);
      } else {
         clientStorage.insert(principal, client);
      }

      ArtifactReadable artifact = clientStorage.getClientByClientGuid(client.getGuid()).getExactlyOne();

      long clientId = client.getClientUuid();
      long applicationId = artifact.getLocalId();

      OAuthClientCredential credential = asCredential(client, applicationId);
      if (credentialStorage.getByClientIdAndApplicationId(clientId, applicationId) != null) {
         credentialStorage.update(credential);
      } else {
         credentialStorage.insert(credential);
      }
   }

   OAuthClientCredential asCredential(OAuthClient client, long applicationId) {
      long clientId = client.getClientUuid();
      long subjectId = client.getSubjectId();

      String clientKey = client.getClientId();
      String clientSecret = client.getClientSecret();

      List<String> clientCerts = client.getApplicationCertificates();
      return credentialStorage.newCredential(clientId, applicationId, subjectId, clientKey, clientSecret, clientCerts);
   }

   @Override
   public void removeClient(OseePrincipal principal, OAuthClient client) {
      getClientStorage().delete(principal, client);

      OAuthClientCredential credential = asCredential(client, -1L);
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
      ArtifactReadable artifact = clientStorage.getClientByClientGuid(guid).getOneOrNull();
      if (artifact != null) {
         Integer applicationId = artifact.getLocalId();

         OAuthClientCredential credential = credentialStorage.getByApplicationId(applicationId);
         client = clientStorage.newClient(artifact, credential);
      }
      return client;
   }

   @Override
   public OAuthClient getClientByClientKey(String clientKey) {
      OAuthClientCredential credential = credentialStorage.getByClientKey(clientKey);
      OAuthClient client = null;
      if (credential != null) {
         long applicationId = credential.getApplicationId();

         ClientStorage clientStorage = getClientStorage();
         ArtifactReadable artifact = clientStorage.getClientByApplicationId(applicationId).getOneOrNull();
         if (artifact != null) {
            client = clientStorage.newClient(artifact, credential);
         }
      }
      return client;
   }

}
