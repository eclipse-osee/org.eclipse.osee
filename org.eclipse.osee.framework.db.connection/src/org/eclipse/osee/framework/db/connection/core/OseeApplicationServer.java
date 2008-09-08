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
package org.eclipse.osee.framework.db.connection.core;

import java.net.URL;
import java.net.URLConnection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.IApplicationServerConnectionListener;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Andrew M. Finkbeiner
 */
public class OseeApplicationServer {
   private static Set<IApplicationServerConnectionListener> listeners =
         Collections.synchronizedSet(new HashSet<IApplicationServerConnectionListener>());

   private static final String key = "osee.resource.server";
   private static String oseeServer = null;
   private static boolean serverStatus = false;

   public static void setApplicationOseeServer(String string) throws SQLException {
      OseeInfo.putValue(key, string);
   }

   public static String getOseeApplicationServer() throws SQLException {
      checkAndUpdateStatus();
      if (Strings.isValid(oseeServer) != true) {
         throw new SQLException("Invalid resource server address in DB. Check OSEE_INFO table.");
      }
      return oseeServer;
   }

   public static boolean isApplicationServerAlive() {
      try {
         checkAndUpdateStatus();
      } catch (Exception ex) {
      }
      return serverStatus;
   }

   private static void checkAndUpdateStatus() throws SQLException {
      try {
         if (oseeServer == null) {
            String overrideValue = OseeProperties.getInstance().getOseeApplicationServerOverride();
            oseeServer = Strings.isValid(overrideValue) ? overrideValue : OseeInfo.getValue(key);
         }
      } finally {
         boolean canConnect = false;
         try {
            URL url = new URL(oseeServer);
            URLConnection connection = url.openConnection();
            connection.connect();
            canConnect = true;
         } catch (Exception ex) {
         }
         if (canConnect != serverStatus) {
            serverStatus = canConnect;
            notifyConnectionListeners();
         }
      }
   }

   private static void notifyConnectionListeners() {
      synchronized (listeners) {
         for (IApplicationServerConnectionListener listener : listeners) {
            try {
               listener.onConnectionStatusChange(serverStatus);
            } catch (Exception ex) {
               OseeLog.log(OseeApplicationServer.class, Level.WARNING, String.format("Error notifying listener [%s]",
                     listener));
            }
         }
      }
   }

   public static void addListener(IApplicationServerConnectionListener listener) {
      if (listener != null) {
         listeners.add(listener);
      }
   }

   public static void removeListener(IApplicationServerConnectionListener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }
}
