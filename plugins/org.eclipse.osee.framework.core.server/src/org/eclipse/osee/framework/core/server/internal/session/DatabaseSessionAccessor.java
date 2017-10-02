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

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.cache.admin.CacheDataLoader;
import org.eclipse.osee.cache.admin.CacheKeysLoader;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;

/**
 * @author Roberto E. Escobar
 */
public final class DatabaseSessionAccessor implements CacheDataLoader<String, Session>, CacheKeysLoader<String>, WriteDataAccessor<Session> {

   private static final String INSERT_SESSION =
      "INSERT INTO osee_session (session_id, user_id, client_machine_name, client_address, client_port, client_version, created_on) VALUES (?,?,?,?,?,?,?)";

   private static final String DELETE_SESSION = "DELETE FROM osee_session WHERE session_id = ?";

   private final SessionFactory sessionFactory;
   private final ISessionQuery sessionQuery;
   private final JdbcClient jdbcClient;

   public DatabaseSessionAccessor(SessionFactory sessionFactory, ISessionQuery sessionQuery, JdbcClient jdbcClient) {
      super();
      this.sessionFactory = sessionFactory;
      this.sessionQuery = sessionQuery;
      this.jdbcClient = jdbcClient;
   }

   private SessionFactory getFactory() {
      return sessionFactory;
   }

   private JdbcClient getJdbcClient() {
      return jdbcClient;
   }

   private ISessionQuery getSessionQuery() {
      return sessionQuery;
   }

   private void executeTx(SessionTxType op, Iterable<Session> sessions)  {
      jdbcClient.runTransaction(new SessionTx(getJdbcClient(), op, sessions));
   }

   @Override
   public void create(Iterable<Session> sessions)  {
      executeTx(SessionTxType.CREATE, sessions);
   }

   @Override
   public void delete(Iterable<Session> sessions)  {
      executeTx(SessionTxType.DELETE, sessions);
   }

   @Override
   public Map<String, Session> load(Iterable<? extends String> sessionIds)  {
      final Map<String, Session> sessions = new LinkedHashMap<>();
      ISessionCollector collector = new ISessionCollector() {

         @Override
         public void collect(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort) {
            Session session = getFactory().createLoadedSession(guid, userId, creationDate, clientVersion,
               clientMachineName, clientAddress, clientPort);
            sessions.put(guid, session);
         }
      };

      getSessionQuery().selectSessionById(collector, sessionIds.iterator().next());
      return sessions;
   }

   @Override
   public Session load(String sessionId)  {
      Map<String, Session> loaded = load(Collections.singleton(sessionId));
      Session toReturn = null;
      if (!loaded.values().isEmpty()) {
         toReturn = loaded.values().iterator().next();
      }
      return toReturn;
   }

   @Override
   public Session reload(String key, Session oldValue)  {
      return load(key);
   };

   @Override
   public Iterable<? extends String> getAllKeys()  {
      final Set<String> ids = new LinkedHashSet<>();
      ISessionCollector idCollector = new ISessionCollector() {

         @Override
         public void collect(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort) {
            ids.add(guid);
         }
      };
      getSessionQuery().selectAllServerManagedSessions(idCollector);
      return ids;
   }

   private static enum SessionTxType {
      CREATE,
      DELETE;
   }

   private static final class SessionTx extends JdbcTransaction {

      private final JdbcClient jdbcClient;
      private final SessionTxType txType;
      private final Iterable<Session> sessions;

      public SessionTx(JdbcClient jdbcClient, SessionTxType txType, Iterable<Session> sessions) {
         this.jdbcClient = jdbcClient;
         this.txType = txType;
         this.sessions = sessions;
      }

      @Override
      public void handleTxWork(JdbcConnection connection)  {
         switch (txType) {
            case CREATE:
               create(connection);
               break;
            case DELETE:
               delete(connection);
               break;
            default:
               break;
         }
      }

      private void create(JdbcConnection connection)  {
         List<Object[]> insertData = new ArrayList<>();
         for (Session session : sessions) {
            insertData.add(toInsert(session));
         }
         if (!insertData.isEmpty()) {
            jdbcClient.runBatchUpdate(connection, INSERT_SESSION, insertData);
         }
      }

      private void delete(JdbcConnection connection)  {
         List<Object[]> deleteData = new ArrayList<>();
         for (Session session : sessions) {
            deleteData.add(toDelete(session));
         }
         if (!deleteData.isEmpty()) {
            jdbcClient.runBatchUpdate(connection, DELETE_SESSION, deleteData);
         }
      }

      private Object[] toInsert(Session session) {
         return new Object[] {
            session.getGuid(),
            session.getUserId(),
            session.getClientMachineName(),
            session.getClientAddress(),
            session.getClientPort(),
            session.getClientVersion(),
            session.getCreationDate()};
      }

      private Object[] toDelete(Session session) {
         return new Object[] {session.getGuid()};
      }

   }

}
