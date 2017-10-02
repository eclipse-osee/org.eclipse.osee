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
import java.util.function.Consumer;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Roberto E. Escobar
 */
public final class DatabaseSessionQuery implements ISessionQuery {
   private static final String SELECT_ALL_SESSIONS = "select * from osee_session";
   private static final String SELECT_SESSION_BY_ID = "select * from osee_session WHERE session_id = ?";

   private final JdbcClient jdbcClient;

   public DatabaseSessionQuery(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   @Override
   public void selectAllServerManagedSessions(ISessionCollector collector)  {
      querySessions(collector, SELECT_ALL_SESSIONS);
   }

   @Override
   public void selectSessionById(ISessionCollector collector, String id) {
      querySessions(collector, SELECT_SESSION_BY_ID, id);
   }

   private void querySessions(ISessionCollector collector, String sql, Object... params)  {
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
}