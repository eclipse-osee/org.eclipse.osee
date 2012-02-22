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
import java.util.Collection;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerLookup implements IApplicationServerLookup {

   private static ThreadFactory threadFactory = null;

   private Log logger;
   private IOseeDatabaseService dbService;

   private IApplicationServerManager serverManager;
   private ApplicationServerDataStore serverDataStore;

   public void setLogger(Log logger) {
      this.logger = logger;
   }

   private Log getLogger() {
      return logger;
   }

   public void setDatabaseService(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   private IOseeDatabaseService getDatabaseService() {
      return dbService;
   }

   public void setServerManager(IApplicationServerManager serverManager) {
      this.serverManager = serverManager;
   }

   public void start() {
      serverDataStore = new ApplicationServerDataStore(getLogger(), getDatabaseService());
   }

   public void stop() {
      serverDataStore = null;
   }

   private ApplicationServerDataStore getDataStore() {
      return serverDataStore;
   }

   @Override
   public Collection<OseeServerInfo> getAvailableServers() throws OseeCoreException {
      Collection<OseeServerInfo> infos = getDataStore().getAllApplicationServerInfos();
      return getHealthyServers(infos);
   }

   private Collection<OseeServerInfo> getHealthyServers(Collection<OseeServerInfo> infos) {
      List<OseeServerInfo> healthyServers = new ArrayList<OseeServerInfo>();
      List<OseeServerInfo> unHealthyServers = new ArrayList<OseeServerInfo>();
      for (OseeServerInfo info : infos) {
         if (isServerAlive(info)) {
            if (info.isAcceptingRequests()) {
               healthyServers.add(info);
            }
         } else {
            unHealthyServers.add(info);
         }
      }
      cleanUpServers(unHealthyServers);
      return healthyServers;
   }

   @Override
   public OseeServerInfo getServerInfoBy(String version) throws OseeCoreException {
      Collection<OseeServerInfo> healthyServers = getHealthyServers(serverDataStore.getApplicationServerInfos(version));
      return getBestAvailable(healthyServers);
   }

   private synchronized void cleanUpServers(final Collection<OseeServerInfo> unHealthyServers) {
      if (!unHealthyServers.isEmpty()) {
         if (threadFactory == null) {
            threadFactory = serverManager.createNewThreadFactory("Server Status Thread Factory", Thread.NORM_PRIORITY);
         }

         Thread thread = threadFactory.newThread(new Runnable() {
            @Override
            public void run() {
               final ApplicationServerDataStore store = serverDataStore;
               if (store != null) {
                  try {
                     store.removeByServerId(unHealthyServers);
                  } catch (OseeCoreException ex) {
                     logger.error(ex, "Error removing unhealthy server entries: [%s]", unHealthyServers);
                  }
               }
            }
         });
         thread.start();
      }
   }

   private OseeServerInfo getBestAvailable(Collection<OseeServerInfo> infos) throws OseeCoreException {
      OseeServerInfo result = null;
      if (infos.size() == 1) {
         result = infos.iterator().next();
      } else {
         int minSessions = Integer.MAX_VALUE;
         for (OseeServerInfo info : infos) {
            try {
               int numberOfSessions = serverDataStore.getNumberOfSessions(info.getServerId());
               if (minSessions > numberOfSessions) {
                  result = info;
                  minSessions = numberOfSessions;
               }
            } catch (OseeDataStoreException ex) {
               logger.error(ex, "Error getting number of sessions");
            }
         }
      }
      return result;
   }

   private boolean isServerAlive(OseeServerInfo info) {
      return HttpProcessor.isAlive(info.getServerAddress(), info.getPort());
   }

}
