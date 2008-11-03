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
import org.eclipse.osee.framework.core.data.OseeSession;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;

/**
 * @author Roberto E. Escobar
 */
public class SessionDataStore {

   private static final String INSERT_SESSION =
         "INSERT INTO osee_session (session_id, user_id, client_machine_name, client_address, client_port, client_version, created_on, last_interaction_date, last_interaction) VALUES (?,?,?,?,?,?,?,?,?)";

   private static final String DELETE_SESSION = "DELETE FROM osee_session WHERE session_id = ?";

   private static final String UPDATE_SESSION =
         "UPDATE osee_session SET last_interaction_date = ?, last_interaction = ? WHERE session_id = ?";

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

   public static void createSessions(OseeSession... sessions) throws OseeDataStoreException {
      OseeConnection connection = null;
      try {
         if (sessions != null && sessions.length > 0) {
            connection = OseeDbConnection.getConnection();
            List<Object[]> data = new ArrayList<Object[]>();
            for (OseeSession session : sessions) {
               data.add(new Object[] {session.getSessionId(), session.getUserId(), session.getClientMachineName(),
                     session.getClientAddress(), session.getPort(), session.getVersion(), session.getCreation(),
                     session.getLastInteractionDate(), session.getLastInteraction()});
            }
            ConnectionHandler.runBatchUpdate(connection, INSERT_SESSION, data);
         }
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
   }

   public static void updateSessions(OseeSession... sessions) throws OseeDataStoreException {
      OseeConnection connection = null;
      try {
         if (sessions != null && sessions.length > 0) {
            connection = OseeDbConnection.getConnection();
            List<Object[]> data = new ArrayList<Object[]>();
            for (OseeSession session : sessions) {
               data.add(new Object[] {session.getLastInteractionDate(), session.getLastInteraction(),
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
}
