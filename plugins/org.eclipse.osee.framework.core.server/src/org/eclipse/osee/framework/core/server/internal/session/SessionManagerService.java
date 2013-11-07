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
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.server.IAuthenticationManager;
import org.eclipse.osee.framework.core.server.ISession;
import org.eclipse.osee.framework.core.server.ISessionManager;
import org.eclipse.osee.framework.core.server.internal.BuildTypeDataProvider;
import org.eclipse.osee.framework.core.server.internal.BuildTypeIdentifier;
import org.eclipse.osee.framework.database.DatabaseInfoRegistry;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public final class SessionManagerService implements ISessionManager {

   private Log logger;
   private DatabaseInfoRegistry registry;
   private IOseeDatabaseService dbService;
   private IApplicationServerManager serverManager;
   private IAuthenticationManager authenticationManager;
   private CacheAdmin cacheAdmin;
   private ISessionManager proxiedSessionManager;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   public void setDbInfoRegistry(DatabaseInfoRegistry registry) {
      this.registry = registry;
   }

   public void setDbService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   public void setServerManager(IApplicationServerManager serverManager) {
      this.serverManager = serverManager;
   }

   public void setAuthenticationManager(IAuthenticationManager authenticationManager) {
      this.authenticationManager = authenticationManager;
   }

   public void setCacheAdmin(CacheAdmin cacheAdmin) {
      this.cacheAdmin = cacheAdmin;
   }

   private IOseeDatabaseService getDbService() {
      return dbService;
   }

   private IApplicationServerManager getServerManager() {
      return serverManager;
   }

   private IAuthenticationManager getAuthenticationManager() {
      return authenticationManager;
   }

   public void start() throws OseeCoreException {
      BuildTypeIdentifier identifier = new BuildTypeIdentifier(new BuildTypeDataProvider());

      SessionFactory sessionFactory = new SessionFactory(logger, registry, dbService, identifier);

      ISessionQuery sessionQuery = new DatabaseSessionQuery(getDbService());

      DatabaseSessionAccessor accessor = new DatabaseSessionAccessor(sessionFactory, sessionQuery, getDbService());

      CacheConfiguration config = CacheConfiguration.newConfiguration();
      Cache<String, Session> sessionCache = null;
      try {
         sessionCache = cacheAdmin.createLoadingCache(config, accessor, accessor);
      } catch (Exception e) {
         OseeExceptions.wrapAndThrow(e);
      }

      proxiedSessionManager =
         new SessionManagerImpl(sessionFactory, sessionCache, getAuthenticationManager(), accessor);
   }

   public void stop() {
      // do nothing
   }

   @Override
   public OseeSessionGrant createSession(OseeCredential credential) throws OseeCoreException {
      return proxiedSessionManager.createSession(credential);
   }

   @Override
   public void releaseSession(String sessionId) throws OseeCoreException {
      proxiedSessionManager.releaseSession(sessionId);
   }

   @Override
   public void updateSessionActivity(String sessionId, String interactionName) throws OseeCoreException {
      proxiedSessionManager.updateSessionActivity(sessionId, interactionName);
   }

   @Override
   public ISession getSessionById(String sessionId) throws OseeCoreException {
      return proxiedSessionManager.getSessionById(sessionId);
   }

   @Override
   public Collection<ISession> getSessionByClientAddress(String clientAddress) throws OseeCoreException {
      return proxiedSessionManager.getSessionByClientAddress(clientAddress);
   }

   @Override
   public Collection<ISession> getSessionsByUserId(String userId) throws OseeCoreException {
      return proxiedSessionManager.getSessionsByUserId(userId);
   }

   @Override
   public Collection<ISession> getAllSessions() throws OseeCoreException {
      return proxiedSessionManager.getAllSessions();
   }

   @Override
   public void releaseSessionImmediate(String... sessionId) throws OseeCoreException {
      proxiedSessionManager.releaseSessionImmediate(sessionId);
   }

}