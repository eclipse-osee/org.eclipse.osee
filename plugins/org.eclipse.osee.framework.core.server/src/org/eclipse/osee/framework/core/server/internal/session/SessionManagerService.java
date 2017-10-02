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

import java.util.Collection;
import org.eclipse.osee.cache.admin.Cache;
import org.eclipse.osee.cache.admin.CacheAdmin;
import org.eclipse.osee.cache.admin.CacheConfiguration;
import org.eclipse.osee.framework.core.data.OseeCredential;
import org.eclipse.osee.framework.core.data.OseeSessionGrant;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcService;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public final class SessionManagerService implements ISessionManager {

   private Log logger;
   private JdbcService jdbcService;
   private IAuthenticationManager authenticationManager;
   private CacheAdmin cacheAdmin;
   private ISessionManager proxiedSessionManager;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setJdbcService(JdbcService jdbcService) {
      this.jdbcService = jdbcService;
   }

   public void setAuthenticationManager(IAuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   public void setCacheAdmin(CacheAdmin cacheAdmin) {
      this.cacheAdmin = cacheAdmin;
   }

   public void start()  {
      final JdbcClient jdbcClient = jdbcService.getClient();

      SessionFactory sessionFactory = new SessionFactory(logger, jdbcService);
      ISessionQuery sessionQuery = new DatabaseSessionQuery(jdbcClient);
      DatabaseSessionAccessor accessor = new DatabaseSessionAccessor(sessionFactory, sessionQuery, jdbcClient);

      CacheConfiguration config = CacheConfiguration.newConfiguration();
      Cache<String, Session> sessionCache = null;
      try {
         sessionCache = cacheAdmin.createLoadingCache(config, accessor, accessor);
      } catch (Exception e) {
         OseeCoreException.wrapAndThrow(e);
      }

      proxiedSessionManager = new SessionManagerImpl(sessionFactory, sessionCache, authenticationManager, accessor);
   }

   public void stop() {
      // do nothing
   }

   @Override
   public OseeSessionGrant createSession(OseeCredential credential)  {
      return proxiedSessionManager.createSession(credential);
   }

   @Override
   public void releaseSession(String sessionId)  {
      proxiedSessionManager.releaseSession(sessionId);
   }

   @Override
   public ISession getSessionById(String sessionId)  {
      return proxiedSessionManager.getSessionById(sessionId);
   }

   @Override
   public Collection<ISession> getSessionByClientAddress(String clientAddress)  {
      return proxiedSessionManager.getSessionByClientAddress(clientAddress);
   }

   @Override
   public Collection<ISession> getSessionsByUserId(String userId)  {
      return proxiedSessionManager.getSessionsByUserId(userId);
   }

   @Override
   public Collection<ISession> getAllSessions()  {
      return proxiedSessionManager.getAllSessions();
   }

   @Override
   public void releaseSessionImmediate(String... sessionId)  {
      proxiedSessionManager.releaseSessionImmediate(sessionId);
   }

}