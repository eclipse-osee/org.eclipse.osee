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

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IOseeUserInfo;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeInvalidSessionException;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Roberto E. Escobar
 */
public final class SessionManagerImpl implements ISessionManager {

   private final String serverId;
   private final SessionFactory sessionFactory;
   private final ISessionQuery sessionQuery;
   private final SessionCache sessionCache;
   private final IAuthenticationManager authenticationManager;

   public SessionManagerImpl(String serverId, SessionFactory sessionFactory, ISessionQuery sessionQuery, SessionCache sessionCache, IAuthenticationManager authenticationManager) {
      this.serverId = serverId;
      this.sessionFactory = sessionFactory;
      this.sessionQuery = sessionQuery;
      this.sessionCache = sessionCache;
      this.authenticationManager = authenticationManager;
   }

   @Override
   public OseeSessionGrant createSession(OseeCredential credential) throws OseeCoreException {
      Conditions.checkNotNull(credential, "credential");
      OseeSessionGrant sessionGrant = null;

      boolean isAuthenticated = authenticationManager.authenticate(credential);
      if (isAuthenticated) {
         IOseeUserInfo oseeUserInfo = authenticationManager.asOseeUser(credential);

         String managedByServerId = serverId;
         Date creationDate = GlobalTime.GreenwichMeanTimestamp();
         Session session =
            sessionFactory.create(GUID.create(), oseeUserInfo.getUserID(), creationDate, managedByServerId,
               credential.getVersion(), credential.getClientMachineName(), credential.getClientAddress(),
               credential.getPort(), creationDate, StorageState.CREATED.name().toLowerCase());
         sessionCache.cache(session);
         sessionGrant = sessionFactory.createSessionGrant(session, oseeUserInfo);
      }
      return sessionGrant;
   }

   @Override
   public void releaseSession(String sessionId) throws OseeCoreException {
      Conditions.checkNotNull(sessionId, "sessionId");
      Session session = getSessionById(sessionId);
      if (session != null) {
         session.setStorageState(StorageState.PURGED);
      }
   }

   @Override
   public void updateSessionActivity(String sessionId, String interactionName) throws OseeCoreException {
      Conditions.checkNotNull(sessionId, "sessionId");
      Session session = getSessionById(sessionId);
      if (session != null) {
         session.setLastInteractionDetails(Strings.isValid(interactionName) ? interactionName : "unknown");
         session.setLastInteractionDate(GlobalTime.GreenwichMeanTimestamp());
      } else {
         throw new OseeInvalidSessionException(String.format("Session was invalid: [%s]", sessionId));
      }
   }

   @Override
   public Session getSessionById(String sessionId) throws OseeCoreException {
      Conditions.checkNotNull(sessionId, "sessionId");
      return sessionCache.getByGuid(sessionId);
   }

   @Override
   public Collection<ISession> getSessionByClientAddress(String clientAddress) throws OseeCoreException {
      Conditions.checkNotNull(clientAddress, "clientAddress");
      Set<ISession> sessions = new HashSet<ISession>();
      for (Session session : sessionCache.getRawValues()) {
         if (session.getClientAddress().equals(clientAddress)) {
            sessions.add(session);
         }
      }
      return sessions;
   }

   @Override
   public Collection<ISession> getSessionsByUserId(String userId, boolean includeNonServerManagedSessions) throws OseeCoreException {
      Conditions.checkNotNull(userId, "userId");
      Collection<ISession> toReturn = new HashSet<ISession>();
      for (ISession session : getAllSessions(includeNonServerManagedSessions)) {
         if (session.getUserId().equals(userId)) {
            toReturn.add(session);
         }
      }
      return toReturn;
   }

   @Override
   public Collection<ISession> getAllSessions(boolean includeNonServerManagedSessions) throws OseeCoreException {
      Collection<ISession> toReturn = new HashSet<ISession>();
      toReturn.addAll(sessionCache.getRawValues());
      if (includeNonServerManagedSessions) {
         ISessionCollector collector = new DefaultSessionCollector(serverId, sessionFactory, toReturn);
         sessionQuery.selectNonServerManagedSessions(collector);
      }
      return toReturn;
   }

   @Override
   public void releaseSessionImmediate(String... sessionIds) throws OseeCoreException {
      Conditions.checkNotNull(sessionIds, "sessionIds");

      for (String sessionId : sessionIds) {
         releaseSession(sessionId);
      }
      sessionCache.storeByGuid(Arrays.asList(sessionIds));
   }

}
