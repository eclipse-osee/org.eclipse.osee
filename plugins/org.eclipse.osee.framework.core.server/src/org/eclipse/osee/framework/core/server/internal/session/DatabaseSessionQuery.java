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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;

/**
 * @author Roberto E. Escobar
 */
public final class DatabaseSessionQuery implements ISessionQuery {
   private static final String SELECT_SESSIONS_BY_SERVER_ID =
      "select * from osee_session WHERE managed_by_server_id = ?";

   private static final String SELECT_SESSIONS_BY_NOT_SERVER_ID =
      "select * from osee_session WHERE NOT managed_by_server_id = ?";

   private final String serverId;
   private final IOseeDatabaseService databaseService;

   public DatabaseSessionQuery(String serverId, IOseeDatabaseService databaseService) {
      super();
      this.serverId = serverId;
      this.databaseService = databaseService;
   }

   private String getServerId() {
      return serverId;
   }

   private IOseeDatabaseService getDatabaseService() {
      return databaseService;
   }

   @Override
   public void selectServerManagedSessions(ISessionCollector collector) throws OseeCoreException {
      querySessions(collector, SELECT_SESSIONS_BY_SERVER_ID, getServerId());
   }

   @Override
   public void selectNonServerManagedSessions(ISessionCollector collector) throws OseeCoreException {
      querySessions(collector, SELECT_SESSIONS_BY_NOT_SERVER_ID, getServerId());
   }

   private void querySessions(ISessionCollector collector, String sql, Object... params) throws OseeCoreException {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(sql, serverId);
         while (chStmt.next()) {
            String sessionGuid = chStmt.getString("session_id");
            String userId = chStmt.getString("user_id");
            Date creationDate = chStmt.getTimestamp("created_on");
            String clientVersion = chStmt.getString("client_version");
            String clientMachineName = chStmt.getString("client_machine_name");
            String clientAddress = chStmt.getString("client_address");
            int clientPort = chStmt.getInt("client_port");
            Date lastInteractionDate = chStmt.getTimestamp("last_interaction_date");
            String lastInteractionDetails = chStmt.getString("last_interaction");
            int sessionId = Session.guidAsInteger(sessionGuid);
            collector.collect(sessionId, sessionGuid, userId, creationDate, serverId, clientVersion, clientMachineName,
               clientAddress, clientPort, lastInteractionDate, lastInteractionDetails);
         }
      } finally {
         chStmt.close();
      }
   }
}