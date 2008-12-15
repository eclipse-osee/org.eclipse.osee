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

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadFactory;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerLookup implements IApplicationServerLookup {

   private static ThreadFactory threadFactory = null;

   public OseeServerInfo getServerInfoBy(String version) throws OseeDataStoreException {
      List<OseeServerInfo> infos = ApplicationServerDataStore.getApplicationServerInfos(version);

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
      return getBestAvailable(healthyServers);
   }

   private static void cleanUpServers(final List<OseeServerInfo> unHealthyServers) {
      if (!unHealthyServers.isEmpty()) {
         if (threadFactory == null) {
            threadFactory = CoreServerActivator.createNewThreadFactory("Server Status Thread Factory");
         }

         Thread thread = threadFactory.newThread(new Runnable() {
            public void run() {
               try {
                  ApplicationServerDataStore.removeByServerId(unHealthyServers);
               } catch (OseeDataStoreException ex) {
                  OseeLog.log(CoreServerActivator.class, Level.SEVERE, String.format(
                        "Error removing unhealthy server entries: [%s]", unHealthyServers), ex);
               }
            }
         });
         thread.start();
      }
   }

   private OseeServerInfo getBestAvailable(List<OseeServerInfo> infos) {
      OseeServerInfo result = null;
      if (infos.size() == 1) {
         result = infos.get(0);
      } else {
         int minSessions = Integer.MAX_VALUE;
         for (OseeServerInfo info : infos) {
            try {
               int numberOfSessions = ApplicationServerDataStore.getNumberOfSessions(info.getServerId());
               if (minSessions > numberOfSessions) {
                  result = info;
                  minSessions = numberOfSessions;
               }
            } catch (OseeDataStoreException ex) {
               OseeLog.log(CoreServerActivator.class, Level.SEVERE, ex);
            }
         }
      }
      return result;
   }

   private boolean isServerAlive(OseeServerInfo info) {
      boolean result = false;
      try {
         String serverAddress = String.format("http://%s:%s", info.getServerAddress(), info.getPort());
         URL url = new URL(serverAddress);

         HttpURLConnection connection = (HttpURLConnection) url.openConnection();
         int response = connection.getResponseCode();
         if (response == HttpURLConnection.HTTP_NOT_FOUND) {
            result = true;
         }
      } catch (Exception ex) {
         //Do Nothing
      }
      return result;
   }
}
