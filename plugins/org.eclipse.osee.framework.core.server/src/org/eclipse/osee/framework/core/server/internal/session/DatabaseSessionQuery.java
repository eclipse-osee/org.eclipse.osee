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
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.CharJoinQuery;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class DatabaseSessionQuery implements ISessionQuery {
   private static final String SELECT_ALL_SESSIONS = "select * from osee_session";

   private static final String SELECT_SESSIONS_BY_SESSION_ID =
      "select * from osee_session, osee_join_char_id jid WHERE session_id = jid.id and jid.query_id = ?";

   private final IOseeDatabaseService databaseService;

   public DatabaseSessionQuery(IOseeDatabaseService databaseService) {
      super();
      this.databaseService = databaseService;
   }

   private IOseeDatabaseService getDatabaseService() {
      return databaseService;
   }

   @Override
   public void selectAllServerManagedSessions(ISessionCollector collector) throws OseeCoreException {
      querySessions(collector, SELECT_ALL_SESSIONS);
   }

   @Override
   public void selectSessionsById(ISessionCollector collector, Iterable<? extends String> ids) throws OseeCoreException {
      CharJoinQuery joinQuery = JoinUtility.createCharJoinQuery(getDatabaseService());
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
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(sql, params);
         while (chStmt.next()) {
            String sessionGuid = chStmt.getString("session_id");
            String userId = chStmt.getString("user_id");
            Date creationDate = chStmt.getTimestamp("created_on");
            String clientVersion = chStmt.getString("client_version");
            String clientMachineName = chStmt.getString("client_machine_name");
            String clientAddress = chStmt.getString("client_address");
            int clientPort = chStmt.getInt("client_port");
            collector.collect(sessionGuid, userId, creationDate, clientVersion, clientMachineName, clientAddress,
               clientPort);
         }
      } finally {
         chStmt.close();
      }
   }

}