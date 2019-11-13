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
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Callable;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.data.UserToken;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;

/**
 * @author Roberto E. Escobar
 */
public final class SessionManagerImpl implements ISessionManager {

   private final SessionFactory sessionFactory;
   private final Cache<String, Session> sessionCache;
   private final IAuthenticationManager authenticationManager;
   private final WriteDataAccessor<Session> storeDataAccessor;

   public SessionManagerImpl(SessionFactory sessionFactory, Cache<String, Session> sessionCache, IAuthenticationManager authenticationManager, WriteDataAccessor<Session> storeDataAccessor) {
      this.sessionFactory = sessionFactory;
      this.sessionCache = sessionCache;
      this.authenticationManager = authenticationManager;
      this.storeDataAccessor = storeDataAccessor;
   }

   @Override
   public OseeSessionGrant createSession(final OseeCredential credential) {
      Conditions.checkNotNull(credential, "credential");
      OseeSessionGrant sessionGrant = null;
      final String newSessionId = GUID.create();
      boolean isAuthenticated = authenticationManager.authenticate(credential);
      if (isAuthenticated) {
         final UserToken userToken = authenticationManager.asUserToken(credential);

         Callable<Session> callable = new Callable<Session>() {

            @Override
            public Session call() throws Exception {

               Date creationDate = GlobalTime.GreenwichMeanTimestamp();
               Session session = sessionFactory.createNewSession(newSessionId, userToken.getUserId(), creationDate,
                  credential.getClientVersion(), credential.getClientAddress(), credential.getClientAddress(),
                  Integer.valueOf(credential.getClientPort()));

               // if the user is BootStrap we do not want to insert into database since tables may not exist
               if (SystemUser.BootStrap.notEqual(userToken)) {
                  storeDataAccessor.create(Collections.singleton(session));
               }

               return session;
            }
         };

         try {
            Session session = sessionCache.get(newSessionId, callable);
            sessionGrant = sessionFactory.createSessionGrant(session, userToken, authenticationManager.getProtocol());
         } catch (Exception e) {
            OseeCoreException.wrapAndThrow(e);
         }

      }
      return sessionGrant;
   }

   @Override
   public void releaseSession(String sessionId) {
      releaseSessionImmediate(sessionId);
   }

   @Override
   public Session getSessionById(String sessionId) {
      Conditions.checkNotNull(sessionId, "sessionId");
      Session session = null;
      try {
         session = sessionCache.get(sessionId);
      } catch (Exception e) {
         session = null;
      }
      return session;
   }

   @Override
   public Collection<ISession> getSessionByClientAddress(String clientAddress) {
      Conditions.checkNotNull(clientAddress, "clientAddress");
      Set<ISession> sessions = new HashSet<>();
      Iterable<Session> all = null;
      try {
         all = sessionCache.getAll();
      } catch (Exception e) {
         OseeCoreException.wrapAndThrow(e);
      }
      if (all != null) {
         for (Session session : all) {
            if (session.getClientAddress().equals(clientAddress)) {
               sessions.add(session);
            }
         }
      }
      return sessions;
   }

   @Override
   public Collection<ISession> getSessionsByUserId(String userId) {
      Conditions.checkNotNull(userId, "userId");
      Collection<ISession> toReturn = new HashSet<>();
      for (ISession session : getAllSessions()) {
         if (session.getUserId().equals(userId)) {
            toReturn.add(session);
         }
      }
      return toReturn;
   }

   @Override
   public Collection<ISession> getAllSessions() {
      Collection<ISession> toReturn = new HashSet<>();
      try {
         Iterable<Session> all = sessionCache.getAll();
         for (Session session : all) {
            toReturn.add(session);
         }
      } catch (Exception e) {
         OseeCoreException.wrapAndThrow(e);
      }
      return toReturn;
   }

   @Override
   public void releaseSessionImmediate(String... sessionIds) {
      Conditions.checkNotNull(sessionIds, "sessionIds");

      Set<Session> sessions = new HashSet<>();
      for (String sessionId : sessionIds) {
         Session session = getSessionById(sessionId);
         if (session != null) {
            sessions.add(session);
         }
      }

      if (!sessions.isEmpty()) {
         storeDataAccessor.delete(sessions);
      }
      sessionCache.invalidate(Arrays.asList(sessionIds));
   }

}
