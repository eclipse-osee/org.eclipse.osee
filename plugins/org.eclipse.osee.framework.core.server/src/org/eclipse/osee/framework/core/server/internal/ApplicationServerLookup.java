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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerLookup implements IApplicationServerLookup {

   private Log logger;
   private IOseeDatabaseService dbService;

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
      Collection<? extends OseeServerInfo> infos = getDataStore().getAll();
      return getHealthyServers(infos);
   }

   private Collection<OseeServerInfo> getHealthyServers(Collection<? extends OseeServerInfo> infos) {
      List<OseeServerInfo> healthyServers = new ArrayList<OseeServerInfo>();
      for (OseeServerInfo info : infos) {
         if (info.isAcceptingRequests()) {
            if (isServerAlive(info)) {
               healthyServers.add(info);
            }
         }
      }
      return healthyServers;
   }

   @Override
   public OseeServerInfo getServerInfoBy(String version) throws OseeCoreException {
      Collection<? extends OseeServerInfo> infos = getVersionCompatibleServers(version);
      Collection<OseeServerInfo> healthyServers = getHealthyServers(infos);
      return getBestAvailable(healthyServers);
   }

   public Collection<OseeServerInfo> getVersionCompatibleServers(String clientVersion) throws OseeCoreException {
      Set<OseeServerInfo> toReturn = new HashSet<OseeServerInfo>();
      if (Strings.isValid(clientVersion)) {
         Collection<? extends OseeServerInfo> infos = getDataStore().getAll();
         for (OseeServerInfo info : infos) {
            if (isServerCompatible(info, clientVersion)) {
               toReturn.add(info);
            }
         }
      }
      return toReturn;
   }

   private boolean isCompatibleVersion(String serverVersion, String clientVersion) {
      boolean result = false;
      if (serverVersion.equals(clientVersion)) {
         result = true;
      } else {
         result = clientVersion.matches(serverVersion);
         if (!result) {
            result = serverVersion.matches(clientVersion);
         }
      }
      return result;
   }

   private boolean isServerCompatible(OseeServerInfo info, String clientVersion) {
      boolean result = false;
      for (String version : info.getVersion()) {
         result = isCompatibleVersion(version, clientVersion);
         if (result) {
            break;
         }
      }
      return result;
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
      return HttpProcessor.isAlive(info.getUri());
   }

}
