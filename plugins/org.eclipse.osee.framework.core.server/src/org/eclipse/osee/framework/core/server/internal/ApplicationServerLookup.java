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

import java.net.URI;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
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
   public Collection<URI> getAvailableServerUris() throws OseeCoreException {
      Collection<? extends OseeServerInfo> infos = getDataStore().getAll();
      return getHealthyServers(infos);
   }

   private Collection<URI> getHealthyServers(Collection<? extends OseeServerInfo> infos) {
      Set<URI> healthyServers = new LinkedHashSet<URI>();
      for (OseeServerInfo info : infos) {
         if (info.isAcceptingRequests()) {
            if (info.isAcceptingRequests()) {
               healthyServers.add(info.getUri());
            }
         }
      }
      return healthyServers;
   }

}
