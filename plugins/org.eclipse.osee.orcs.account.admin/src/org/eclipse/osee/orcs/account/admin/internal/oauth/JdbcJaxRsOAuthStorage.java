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

import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.jaxrs.server.security.JaxRsOAuthStorage;
import org.eclipse.osee.jaxrs.server.security.OAuthCodeGrant;
import org.eclipse.osee.jaxrs.server.security.OAuthToken;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class JdbcJaxRsOAuthStorage implements JaxRsOAuthStorage {

   private Log logger;
   private IOseeDatabaseService dbService;

   private AuthCodeGrantStorage authCodeGrantStorage;
   private TokenStorage tokenStorage;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void start() {
      authCodeGrantStorage = new AuthCodeGrantStorage(logger, dbService);
      tokenStorage = new TokenStorage(logger, dbService);
   }

   public void stop() {
      //
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

}
