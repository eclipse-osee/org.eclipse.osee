/*********************************************************************
 * Copyright (c) 2015 Boeing
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

package org.eclipse.osee.jaxrs.server.session;

import org.eclipse.osee.jaxrs.server.database.AbstractDatabaseStorage;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;

/**
 * @author Angel Avila
 */
public class AuthenticitySessionStorage extends AbstractDatabaseStorage<AuthenticityToken> {
   private static final String INSERT_AUTHENTICITY =
      "INSERT INTO osee_oauth_authenticity_token (subject_id, token) VALUES (?,?)";

   private static final String SELECT_AUTHENTICTY_BY_ID =
      "select * from osee_oauth_authenticity_token where subject_id = ?";

   private static final String DELETE_TOKEN_BY_ID = "DELETE FROM osee_oauth_authenticity_token WHERE subject_id = ?";

   public AuthenticitySessionStorage(Log logger, JdbcClient jdbcClient) {
      super(logger, jdbcClient);
   }

   public void insertAuthenticityTokens(AuthenticityToken... sessions) {
      insertItems(INSERT_AUTHENTICITY, sessions);
   }

   public AuthenticityToken getSession(Long subjectId) {
      return selectOneOrNull(SELECT_AUTHENTICTY_BY_ID, subjectId);
   }

   @Override
   protected Object[] asInsert(AuthenticityToken data) {
      return new Object[] {data.getSubjectId(), data.getToken()};
   }

   @Override
   protected Object[] asUpdate(AuthenticityToken data) {
      return null;
   }

   public void removeAuthenticitySessionTokens(Iterable<AuthenticityToken> datas) {
      deleteItems(DELETE_TOKEN_BY_ID, datas);
   }

   @Override
   protected Object[] asDelete(AuthenticityToken data) {
      return new Object[] {data.getSubjectId()};
   }

   @Override
   protected AuthenticityToken readData(JdbcStatement chStmt) {
      final Long subjectId = chStmt.getLong("subject_id");
      final String token = chStmt.getString("token");

      AuthenticityToken authenticityToken = new AuthenticityToken();
      authenticityToken.setSubjectId(subjectId);
      authenticityToken.setToken(token);
      return authenticityToken;
   }
}
