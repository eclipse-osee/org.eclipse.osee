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
package org.eclipse.osee.framework.core.server.internal;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.core.server.internal.SessionData.SessionState;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class SessionDataStore {

   private static final String INSERT_SESSION =
         "INSERT INTO osee_session (managed_by_server_id, session_id, user_id, client_machine_name, client_address, client_port, client_version, created_on, last_interaction_date, last_interaction) VALUES (?,?,?,?,?,?,?,?,?,?)";

   private static final String DELETE_SESSION = "DELETE FROM osee_session WHERE session_id = ?";

   private static final String UPDATE_SESSION =
         "UPDATE osee_session SET managed_by_server_id = ?, last_interaction_date = ?, last_interaction = ? WHERE session_id = ?";

   private static final String LOAD_SESSIONS_BY_SERVER_ID = "select * from osee_session WHERE managed_by_server_id = ?";

   //   private static final String FIND_SESSION = "select * from osee_session WHERE session_id = ?";

   private SessionDataStore() {
   }

   public static void deleteSession(String... sessionIds) throws OseeDataStoreException {
      OseeConnection connection = null;
      try {
         if (sessionIds != null) {
            connection = OseeDbConnection.getConnection();
            List<Object[]> data = new ArrayList<Object[]>();
            for (String session : sessionIds) {
               data.add(new Object[] {session});
            }
            ConnectionHandler.runBatchUpdate(connection, DELETE_SESSION, data);
         }
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
   }

   public static void createSessions(String serverId, OseeSession... sessions) throws OseeDataStoreException {
      OseeConnection connection = null;
      try {
         if (sessions != null && sessions.length > 0) {
            connection = OseeDbConnection.getConnection();
            List<Object[]> data = new ArrayList<Object[]>();
            for (OseeSession session : sessions) {
               data.add(new Object[] {serverId, session.getSessionId(), session.getUserId(),
                     session.getClientMachineName(), session.getClientAddress(), session.getPort(),
                     session.getVersion(), session.getCreation(), session.getLastInteractionDate(),
                     session.getLastInteraction()});
            }
            ConnectionHandler.runBatchUpdate(connection, INSERT_SESSION, data);
         }
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
   }

   public static void updateSessions(String serverId, OseeSession... sessions) throws OseeDataStoreException {
      OseeConnection connection = null;
      try {
         if (sessions != null && sessions.length > 0) {
            connection = OseeDbConnection.getConnection();
            List<Object[]> data = new ArrayList<Object[]>();
            for (OseeSession session : sessions) {
               data.add(new Object[] {serverId, session.getLastInteractionDate(), session.getLastInteraction(),
                     session.getSessionId()});
            }
            ConnectionHandler.runBatchUpdate(connection, UPDATE_SESSION, data);
         }
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
   }

   /**
    * @param sessionId
    * @throws OseeDataStoreException
    */
   public static void loadSessions(String serverId, Map<String, SessionData> sessionCache) throws OseeDataStoreException {
      if (Strings.isValid(serverId)) {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(LOAD_SESSIONS_BY_SERVER_ID, serverId);
            while (chStmt.next()) {
               String sessionId = chStmt.getString("session_id");
               if (!sessionCache.containsKey(sessionId)) {
                  OseeSession session =
                        new OseeSession(chStmt.getString("session_id"), chStmt.getString("user_id"),
                              chStmt.getTimestamp("created_on"), chStmt.getString("client_machine_name"),
                              chStmt.getString("client_address"), chStmt.getInt("client_port"),
                              chStmt.getString("client_version"), chStmt.getTimestamp("last_interaction_date"),
                              chStmt.getString("last_interaction"));
                  sessionCache.put(sessionId, new SessionData(SessionState.CURRENT, session));
               }
            }
         } finally {
            chStmt.close();
         }
      }
   }
}
