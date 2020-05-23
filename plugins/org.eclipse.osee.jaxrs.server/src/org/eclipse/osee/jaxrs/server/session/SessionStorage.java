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
public class SessionStorage extends AbstractDatabaseStorage<SessionData> {

   private static final String INSERT_SESSION =
      "INSERT INTO osee_oauth_session (session_id, issued_at, expires_in, subject_token) VALUES (?,?,?,?)";

   private static final String SELECT_SESSION_BY_ID = "select * from osee_oauth_session where session_id = ?";

   private static final String DELETE_SESSION_BY_ID = "DELETE FROM osee_oauth_session WHERE session_id = ?";

   public SessionStorage(Log logger, JdbcClient jdbcClient) {
      super(logger, jdbcClient);
   }

   @Override
   protected Object[] asInsert(SessionData session) {
      return new Object[] {session.getGuid(), session.getIssuedAt(), session.getExpiresIn(), session.getSubjectToken()};
   }

   @Override
   protected Object[] asUpdate(SessionData item) {
      return null;
   }

   @Override
   protected Object[] asDelete(SessionData item) {
      return new Object[] {item.getGuid()};
   }

   public void insertSessions(SessionData... sessions) {
      insertItems(INSERT_SESSION, sessions);
   }

   public SessionData getSession(String sessionId) {
      return selectOneOrNull(SELECT_SESSION_BY_ID, sessionId);
   }

   public void deleteSessioin(Iterable<SessionData> datas) {
      deleteItems(DELETE_SESSION_BY_ID, datas);
   }

   @Override
   protected SessionData readData(JdbcStatement chStmt) {
      final String sessionId = chStmt.getString("session_id");
      final Long issuedAt = chStmt.getLong("issued_at");
      final Long expiresIn = chStmt.getLong("expires_in");
      final String subjectToken = chStmt.getString("subject_token");

      SessionData sessionData = new SessionData(sessionId);
      sessionData.setIssuedAt(issuedAt);
      sessionData.setExpiresIn(expiresIn);
      sessionData.setSubjectToken(subjectToken);

      return sessionData;
   }
}
