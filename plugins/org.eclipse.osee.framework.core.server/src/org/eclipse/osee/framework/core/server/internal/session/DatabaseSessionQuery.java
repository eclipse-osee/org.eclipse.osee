/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.core.server.internal.session;

import java.util.Date;
import java.util.Random;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.server.internal.util.CharJoinQuery;
import org.eclipse.osee.framework.core.server.internal.util.DatabaseJoinAccessor;
import org.eclipse.osee.framework.core.server.internal.util.IJoinAccessor;
import org.eclipse.osee.framework.core.server.internal.util.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public final class DatabaseSessionQuery implements ISessionQuery {
   private static final Long DEFAULT_JOIN_EXPIRATION_SECONDS = 3L * 60L * 60L; // 3 hours
   private static final String EXPIRATION_SECS__CHAR_JOIN_QUERY = "char.join.expiration.secs";

   private static final String SELECT_ALL_SESSIONS = "select * from osee_session";

   private static final String SELECT_SESSIONS_BY_SESSION_ID =
      "select * from osee_session, osee_join_char_id jid WHERE session_id = jid.id and jid.query_id = ?";

   private final JdbcClient jdbcClient;
   private final IJoinAccessor joinAccessor;
   private final Random random = new Random();

   public DatabaseSessionQuery(JdbcClient jdbcClient) {
      super();
      this.jdbcClient = jdbcClient;
      this.joinAccessor = new DatabaseJoinAccessor(jdbcClient);
   }

   @Override
   public void selectAllServerManagedSessions(ISessionCollector collector) throws OseeCoreException {
      querySessions(collector, SELECT_ALL_SESSIONS);
   }

   @Override
   public void selectSessionsById(ISessionCollector collector, Iterable<? extends String> ids) throws OseeCoreException {
      CharJoinQuery joinQuery = createCharJoinQuery();
      try {
         for (String id : ids) {
            joinQuery.add(id);
         }
         joinQuery.store();
         querySessions(collector, SELECT_SESSIONS_BY_SESSION_ID, joinQuery.getQueryId());
      } finally {
         joinQuery.delete();
      }
   }

   private void querySessions(ISessionCollector collector, String sql, Object... params) throws OseeCoreException {
      Consumer<JdbcStatement> consumer = stmt -> {
         String sessionGuid = stmt.getString("session_id");
         String userId = stmt.getString("user_id");
         Date creationDate = stmt.getTimestamp("created_on");
         String clientVersion = stmt.getString("client_version");
         String clientMachineName = stmt.getString("client_machine_name");
         String clientAddress = stmt.getString("client_address");
         int clientPort = stmt.getInt("client_port");
         collector.collect(sessionGuid, userId, creationDate, clientVersion, clientMachineName, clientAddress,
            clientPort);
      };
      jdbcClient.runQuery(consumer, sql, params);
   }

   private int getNewQueryId() {
      return random.nextInt();
   }

   private CharJoinQuery createCharJoinQuery() {
      Long actualExpiration = getExpiresIn(null, EXPIRATION_SECS__CHAR_JOIN_QUERY);
      return new CharJoinQuery(joinAccessor, actualExpiration, getNewQueryId());
   }

   private Long getExpiresIn(Long actual, String defaultKey) {
      Long toReturn = DEFAULT_JOIN_EXPIRATION_SECONDS;
      if (actual != null) {
         toReturn = actual;
      } else {
         String expiration = OseeInfo.getCachedValue(jdbcClient, defaultKey);
         if (Strings.isNumeric(expiration)) {
            toReturn = Long.parseLong(expiration);
         }
      }
      return toReturn;
   }
}