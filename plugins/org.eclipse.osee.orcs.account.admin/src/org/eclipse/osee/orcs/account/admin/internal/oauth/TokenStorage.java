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

import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.jaxrs.server.database.AbstractDatabaseStorage;
import org.eclipse.osee.jaxrs.server.security.OAuthToken;
import org.eclipse.osee.jaxrs.server.security.OAuthTokenType;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class TokenStorage extends AbstractDatabaseStorage<OAuthToken> {

   private static final long NULL_PARENT_ID = -1L;

   private static final String SELECT_TOKEN_BY_PARENT_TOKEN = //
      "WITH parent_token (id, refresh_key) AS (SELECT id, token_key FROM osee_oauth_token WHERE token_key = ? AND type_id = ?)" //
         + "SELECT tk1.*, parent_token.refresh_key FROM osee_oauth_token tk1, parent_token WHERE tk1.parent_token_id = parent_token.id AND tk1.type_id = ?";

   private static final String SELECT_TOKEN_BY_CLIENT_ID_SUBJECT_ID_AND_GRANT_TYPE =
      "SELECT t1.*, t2.token_key AS refresh_key FROM osee_oauth_token t1 " //
         + "LEFT OUTER JOIN osee_oauth_token t2 ON t2.id = t1.parent_token_id WHERE t1.client_id = ? AND t1.subject_id = ? AND t1.grant_type = ?";

   private static final String INSERT_TOKEN =
      "INSERT INTO osee_oauth_token (id, client_id, subject_id, issued_at, expires_in, token_key, token_type, grant_type, audience, parent_token_id, type_id) VALUES (?,?,?,?,?,?,?,?,?,?,?)";

   private static final String DELETE_TOKEN_BY_ID = "DELETE FROM osee_oauth_token WHERE id = ?";

   private static final String DELETE_TOKEN_BY_TOKEN_KEY = "DELETE FROM osee_oauth_token WHERE token_key = ?";

   private static final String UPDATE_TOKEN_PARENT_BY_ID =
      "UPDATE osee_oauth_token SET parent_token_id = ? WHERE id = ?";

   public TokenStorage(Log logger, JdbcClient jdbcClient) {
      super(logger, jdbcClient);
   }

   @Override
   protected Object[] asInsert(OAuthToken data) {
      OAuthTokenType type = data.getType();
      return new Object[] {
         data.getUuid(),
         data.getClientId(),
         data.getSubjectId(),
         data.getIssuedAt(),
         data.getExpiresIn(),
         data.getTokenKey(),
         data.getTokenType(),
         data.getGrantType(),
         asVarcharOrNull(data.getAudience()),
         NULL_PARENT_ID,
         type.getValue()};
   }

   @Override
   protected Object[] asUpdate(OAuthToken data) {
      return new Object[] {data.getUuid()};
   }

   @Override
   protected Object[] asDelete(OAuthToken data) {
      return new Object[] {data.getUuid()};
   }

   public OAuthToken getPreauthorizedToken(long clientUuid, long subjectUuid, String grantType) {
      return selectOneOrNull(SELECT_TOKEN_BY_CLIENT_ID_SUBJECT_ID_AND_GRANT_TYPE, clientUuid, subjectUuid, grantType);
   }

   public ResultSet<OAuthToken> getAccessTokenByRefreshToken(String tokenKey) {
      return selectItems(SELECT_TOKEN_BY_PARENT_TOKEN, tokenKey, OAuthTokenType.REFRESH_TOKEN.getValue(),
         OAuthTokenType.BEARER_TOKEN.getValue());
   }

   public void insertTokens(OAuthToken... tokens) {
      insertItems(INSERT_TOKEN, tokens);
   }

   public void relateTokens(final OAuthToken parentToken, final OAuthToken childToken) {
      Object[] data = new Object[] {parentToken.getUuid(), childToken.getUuid()};
      execute(new AbstractCallable<Object[], Void>(UPDATE_TOKEN_PARENT_BY_ID, data) {

         @Override
         protected Void innerCall() throws Exception {
            getJdbcClient().runPreparedUpdate(query, data);
            return null;
         }

      });
   }

   public void deleteToken(Iterable<OAuthToken> datas) {
      deleteItems(DELETE_TOKEN_BY_ID, datas);
   }

   public void deleteTokenByKey(final String tokenKey) {
      execute(new AbstractCallable<String, Void>(DELETE_TOKEN_BY_TOKEN_KEY, tokenKey) {

         @Override
         protected Void innerCall() throws Exception {
            getJdbcClient().runPreparedUpdate(DELETE_TOKEN_BY_TOKEN_KEY, tokenKey);
            return null;
         }

      });
   }

   @Override
   protected OAuthToken readData(JdbcStatement chStmt) {
      final long uuid = chStmt.getLong("id");
      final long clientId = chStmt.getLong("client_id");
      final long subjectId = chStmt.getLong("subject_id");
      final long issuedAt = chStmt.getLong("issued_at");
      final long expiresIn = chStmt.getLong("expires_in");
      final String tokenKey = chStmt.getString("token_key");
      final String tokenType = chStmt.getString("token_type");
      final String grantType = chStmt.getString("grant_type");
      final String audience = chStmt.getString("audience");
      final OAuthTokenType type = OAuthTokenType.fromValue(chStmt.getInt("type_id"));

      final String refreshToken = chStmt.getString("refresh_key");
      return new OAuthToken() {

         @Override
         public long getUuid() {
            return uuid;
         }

         @Override
         public long getSubjectId() {
            return subjectId;
         }

         @Override
         public long getClientId() {
            return clientId;
         }

         @Override
         public long getIssuedAt() {
            return issuedAt;
         }

         @Override
         public long getExpiresIn() {
            return expiresIn;
         }

         @Override
         public String getTokenKey() {
            return tokenKey;
         }

         @Override
         public String getTokenType() {
            return tokenType;
         }

         @Override
         public String getGrantType() {
            return grantType;
         }

         @Override
         public String getAudience() {
            return audience;
         }

         @Override
         public OAuthTokenType getType() {
            return type;
         }

         @Override
         public String getRefreshToken() {
            return refreshToken;
         }

      };
   }

}
