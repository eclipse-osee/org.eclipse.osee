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
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.cache.IOseeCache;
import org.eclipse.osee.framework.core.model.cache.IOseeDataAccessor;
import org.eclipse.osee.framework.database.IOseeDatabaseService;

/**
 * @author Roberto E. Escobar
 */
public final class DatabaseSessionAccessor implements IOseeDataAccessor<Session> {

   private static final String INSERT_SESSION =
      "INSERT INTO osee_session (managed_by_server_id, session_id, user_id, client_machine_name, client_address, client_port, client_version, created_on, last_interaction_date, last_interaction) VALUES (?,?,?,?,?,?,?,?,?,?)";

   private static final String UPDATE_SESSION =
      "UPDATE osee_session SET managed_by_server_id = ?, last_interaction_date = ?, last_interaction = ? WHERE session_id = ?";

   private static final String DELETE_SESSION = "DELETE FROM osee_session WHERE session_id = ?";

   private final String serverId;
   private final SessionFactory sessionFactory;
   private final ISessionQuery sessionQuery;
   private final IOseeDatabaseService databaseService;

   public DatabaseSessionAccessor(String serverId, SessionFactory sessionFactory, ISessionQuery sessionQuery, IOseeDatabaseService databaseService) {
      super();
      this.serverId = serverId;
      this.sessionFactory = sessionFactory;
      this.sessionQuery = sessionQuery;
      this.databaseService = databaseService;
   }

   private SessionFactory getFactory() {
      return sessionFactory;
   }

   private String getServerId() {
      return serverId;
   }

   private IOseeDatabaseService getDatabaseService() {
      return databaseService;
   }

   private ISessionQuery getSessionQuery() {
      return sessionQuery;
   }

   private Object[] toInsert(Session session) {
      return new Object[] {session.getManagedByServerId(), session.getGuid(), session.getName(),
         session.getClientMachineName(), session.getClientAddress(), session.getClientPort(),
         session.getClientVersion(), session.getCreationDate(), session.getLastInteractionDate(),
         session.getLastInteractionDetails()};
   }

   private Object[] toUpdate(Session session) {
      return new Object[] {session.getManagedByServerId(), session.getLastInteractionDate(),
         session.getLastInteractionDetails(), session.getGuid()};
   }

   private Object[] toDelete(Session session) {
      return new Object[] {session.getGuid()};
   }

   @Override
   public void load(IOseeCache<Session> cache) throws OseeCoreException {
      CacheSessionCollector collector = new CacheSessionCollector(cache);
      getSessionQuery().selectServerManagedSessions(collector);
   }

   @Override
   public void store(Collection<Session> sessions) throws OseeCoreException {
      List<Object[]> insertData = new ArrayList<Object[]>();
      List<Object[]> updateData = new ArrayList<Object[]>();
      List<Object[]> deleteData = new ArrayList<Object[]>();

      for (Session session : sessions) {
         switch (session.getStorageState()) {
            case CREATED:
               session.setId(Session.guidAsInteger(session.getGuid()));
               insertData.add(toInsert(session));
               break;
            case MODIFIED:
               updateData.add(toUpdate(session));
               break;
            case PURGED:
               deleteData.add(toDelete(session));
               break;
            default:
               break;
         }
      }
      getDatabaseService().runBatchUpdate(INSERT_SESSION, insertData);
      getDatabaseService().runBatchUpdate(UPDATE_SESSION, updateData);
      getDatabaseService().runBatchUpdate(DELETE_SESSION, deleteData);

      for (Session session : sessions) {
         session.clearDirty();
      }
   }

   private final class CacheSessionCollector implements ISessionCollector {
      private final IOseeCache<Session> cache;

      public CacheSessionCollector(IOseeCache<Session> cache) {
         this.cache = cache;
      }

      @Override
      public void collect(int sessionId, String guid, String userId, Date creationDate, String managedByServerId, String clientVersion, String clientMachineName, String clientAddress, int clientPort, Date lastInteractionDate, String lastInteractionDetails) throws OseeCoreException {
         Session cachedSession =
            getFactory().createOrUpdate(cache, sessionId, StorageState.LOADED, guid, userId, creationDate,
               getServerId(), clientVersion, clientMachineName, clientAddress, clientPort, lastInteractionDate,
               lastInteractionDetails);
         cachedSession.clearDirty();
      }
   }
}
