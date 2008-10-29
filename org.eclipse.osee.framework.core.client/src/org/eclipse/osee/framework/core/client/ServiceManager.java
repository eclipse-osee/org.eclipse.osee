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
package org.eclipse.osee.framework.core.client;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ServiceManager {
   private static final ServiceManager instance = new ServiceManager();

   private final Set<IServiceListener> listeners;

   private ServiceManager() {
      listeners = Collections.synchronizedSet(new HashSet<IServiceListener>());
   }

   protected static ServiceManager getInstance() {
      return instance;
   }

   protected void addListener(IServiceListener listener) {
      if (listener != null) {
         listeners.add(listener);
      }
   }

   protected void removeListener(IServiceListener listener) {
      if (listener != null) {
         listeners.remove(listener);
      }
   }

   public static List<ServiceInfo> getServicesStatus() {
      List<ServiceInfo> toReturn = new ArrayList<ServiceInfo>();
      return toReturn;
   }

   public static boolean areServicesAvailable() {
      return getInstance().getStatus();
   }

   public static String getServicesStatusMessage() {
      StringBuilder message = new StringBuilder();
      List<ServiceInfo> serviceInfos = ServiceManager.getServicesStatus();
      for (ServiceInfo serviceInfo : serviceInfos) {
         if (!serviceInfo.isAvailable()) {
            message.append(serviceInfo.getName());
            message.append(" is not available. Error: ");
            message.append(serviceInfo.getErrorMessage());
            message.append("\n");
         }
      }
      return message.toString();
   }

   private boolean getStatus() {
      return false;
   }

   private void notifyListeners() {
      synchronized (listeners) {
         for (IServiceListener listener : listeners) {
            try {
               //               listener.onConnectionStatusChange(serverStatus);
            } catch (Exception ex) {
               OseeLog.log(CoreClientActivator.class, Level.WARNING, String.format("Error notifying listener [%s]",
                     listener));
            }
         }
      }
   }

   //   boolean dbConnection = false;
   //   String dbStatus = "Unavailable";
   //   Connection connection = null;
   //   try {
   //      connection = ConnectionHandler.getPooledConnection();
   //      DatabaseMetaData dbData = connection.getMetaData();
   //      dbStatus = getDbStatus(dbData.getDatabaseProductName(), dbData.getDatabaseProductVersion(), dbData.getURL());
   //      dbConnection = true;
   //   } catch (Exception ex) {
   //      DbInformation info = OseeDbConnection.getDefaultDatabaseService();
   //      dbStatus = getDbStatus(info.getDatabaseDetails().getId(), "", info.getConnectionUrl());
   //      dbConnection = false;
   //   } finally {
   //      if (connection != null) {
   //         try {
   //            connection.close();
   //         } catch (SQLException ex) {
   //         }
   //      }
   //   }
   //   record = new DataRecord("Database", dbStatus);
   //   record.setStatus(dbConnection);
   //   configurationDetails.add(record);

   //   private String getDbStatus(String dbName, String dbVersion, String url) {
   //      StringBuffer dbInfo = new StringBuffer();
   //      if (Strings.isValid(dbName)) {
   //         dbName = dbName.replaceAll("\n", "");
   //         dbName = dbName.replaceAll("\r", "");
   //      }
   //      dbInfo.append(dbName);
   //      dbInfo.append(" ");
   //      dbInfo.append(dbVersion);
   //      dbInfo.append(" - ");
   //      Pattern pattern = Pattern.compile("//(.*)?.*?/(.*)");
   //      Matcher matcher = pattern.matcher(url);
   //      if (matcher.find()) {
   //         dbInfo.append(matcher.group(1));
   //         dbInfo.append(" - ");
   //         dbInfo.append(matcher.group(2));
   //      }
   //      dbInfo.append("  ");
   //      return dbInfo.toString();
   //   }
}
