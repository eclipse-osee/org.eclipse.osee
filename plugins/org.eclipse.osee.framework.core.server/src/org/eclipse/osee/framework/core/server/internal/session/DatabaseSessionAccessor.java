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
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.DatabaseTransactions;
import org.eclipse.osee.framework.database.core.IDbTransactionWork;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public final class DatabaseSessionAccessor implements CacheDataLoader<String, Session>, CacheKeysLoader<String>, WriteDataAccessor<Session> {

   private static final String INSERT_SESSION =
      "INSERT INTO osee_session (session_id, user_id, client_machine_name, client_address, client_port, client_version, created_on, last_interaction_date, last_interaction) VALUES (?,?,?,?,?,?,?,?,?)";

   private static final String DELETE_SESSION = "DELETE FROM osee_session WHERE session_id = ?";

   private final SessionFactory sessionFactory;
   private final ISessionQuery sessionQuery;
   private final IOseeDatabaseService databaseService;

   public DatabaseSessionAccessor(SessionFactory sessionFactory, ISessionQuery sessionQuery, IOseeDatabaseService databaseService) {
      super();
      this.sessionFactory = sessionFactory;
      this.sessionQuery = sessionQuery;
      this.databaseService = databaseService;
   }

   private SessionFactory getFactory() {
      return sessionFactory;
   }

   private IOseeDatabaseService getDatabaseService() {
      return databaseService;
   }

   private ISessionQuery getSessionQuery() {
      return sessionQuery;
   }

   private void executeTx(SessionTxType op, Iterable<Session> sessions) throws OseeCoreException {
      IDbTransactionWork tx = new SessionTx(getDatabaseService(), op, sessions);
      DatabaseTransactions.execute(getDatabaseService(), getDatabaseService().getConnection(), tx);
   }

   @Override
   public void create(Iterable<Session> sessions) throws OseeCoreException {
      executeTx(SessionTxType.CREATE, sessions);
   }

   @Override
   public void update(Iterable<Session> sessions) throws OseeCoreException {
      executeTx(SessionTxType.UPDATE, sessions);
   }

   @Override
   public void delete(Iterable<Session> sessions) throws OseeCoreException {
      executeTx(SessionTxType.DELETE, sessions);
   }

   @Override
   public Map<String, Session> load(Iterable<? extends String> sessionIds) throws OseeCoreException {
      final Map<String, Session> sessions = new LinkedHashMap<String, Session>();
      ISessionCollector collector = new ISessionCollector() {

         @Override
         public void collect(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String lastInteractionDetails) {
            Session session =
               getFactory().createLoadedSession(guid, userId, creationDate, clientVersion, clientMachineName,
                  clientAddress, clientPort, lastInteractionDate, lastInteractionDetails);
            sessions.put(guid, session);
         }
      };

      getSessionQuery().selectSessionsById(collector, sessionIds);
      return sessions;
   }

   @Override
   public Session load(String sessionId) throws OseeCoreException {
      Map<String, Session> loaded = load(Collections.singleton(sessionId));
      Session toReturn = null;
      if (!loaded.values().isEmpty()) {
         toReturn = loaded.values().iterator().next();
      }
      return toReturn;
   }

   @Override
   public Session reload(String key, Session oldValue) throws OseeCoreException {
      return load(key);
   };

   @Override
   public Iterable<? extends String> getAllKeys() throws OseeCoreException {
      final Set<String> ids = new LinkedHashSet<String>();
      ISessionCollector idCollector = new ISessionCollector() {

         @Override
         public void collect(String guid, String userId, Date creationDate, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String lastInteractionDetails) {
            ids.add(guid);
         }
      };
      getSessionQuery().selectAllServerManagedSessions(idCollector);
      return ids;
   }

   private static enum SessionTxType {
      CREATE,
      UPDATE,
      DELETE;
   }

   private static final class SessionTx implements IDbTransactionWork {

      private final IOseeDatabaseService dbService;
      private final SessionTxType txType;
      private final Iterable<Session> sessions;

      public SessionTx(IOseeDatabaseService dbService, SessionTxType txType, Iterable<Session> sessions) {
         this.dbService = dbService;
         this.txType = txType;
         this.sessions = sessions;
      }

      @Override
      public String getName() {
         return String.format("[%s] Session", txType);
      }

      @Override
      public void handleTxWork(OseeConnection connection) throws OseeCoreException {
         switch (txType) {
            case CREATE:
               create(connection);
               break;
            case UPDATE:
               update(connection);
               break;
            case DELETE:
               delete(connection);
               break;
            default:
               break;
         }
      }

      private void create(OseeConnection connection) throws OseeCoreException {
         List<Object[]> insertData = new ArrayList<Object[]>();
         for (Session session : sessions) {
            insertData.add(toInsert(session));
         }
         if (!insertData.isEmpty()) {
            dbService.runBatchUpdate(connection, INSERT_SESSION, insertData);
         }
      }

      private void update(OseeConnection connection) {
         // do not update session table for now

         //         List<Object[]> updateData = new ArrayList<Object[]>();
         //         for (Session session : sessions) {
         //            updateData.add(toUpdate(session));
         //         }
         //         if (!updateData.isEmpty()) {
         //            dbService.runBatchUpdate(connection, UPDATE_SESSION, updateData);
         //         }
      }

      private void delete(OseeConnection connection) throws OseeCoreException {
         List<Object[]> deleteData = new ArrayList<Object[]>();
         for (Session session : sessions) {
            deleteData.add(toDelete(session));
         }
         if (!deleteData.isEmpty()) {
            dbService.runBatchUpdate(connection, DELETE_SESSION, deleteData);
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
            session.getCreationDate(),
            session.getLastInteractionDate(),
            session.getLastInteractionDetails()};
      }

      private Object[] toUpdate(Session session) {
         return new Object[] {session.getLastInteractionDate(), session.getLastInteractionDetails(), session.getGuid()};
      }

      private Object[] toDelete(Session session) {
         return new Object[] {session.getGuid()};
      }

      @Override
      public void handleTxException(Exception ex) {
         //
      }

      @Override
      public void handleTxFinally() {
         //
      }
   }

}
