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

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.IDbConnectionListener;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.BaseStatus;
import org.eclipse.osee.framework.logging.IHealthStatus;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ServiceHealthManager {

   private final static ServiceHealthManager instance = new ServiceHealthManager();

   private final Map<String, ServiceStatus> services;
   private final Set<IServiceListener> serviceListeners;
   private DataStoreMonitor dataStoreMonitor;

   private ServiceHealthManager() {
      this.services = Collections.synchronizedMap(new HashMap<String, ServiceStatus>());
      this.serviceListeners = Collections.synchronizedSet(new HashSet<IServiceListener>());
      this.dataStoreMonitor = new DataStoreMonitor();
      ConnectionHandler.addListener(this.dataStoreMonitor);
   }

   private static ServiceHealthManager getInstance() {
      return instance;
   }

   /**
    * @param name
    * @param healthStatus
    */
   public static void updateStatus(String serviceName, IHealthStatus healthStatus) {
      getInstance().internalUpdateStatus(serviceName, healthStatus);
   }

   public static void addListener(IServiceListener listener) {
      getInstance().internalAddListener(listener);
   }

   public static void removeListener(IServiceListener listener) {
      getInstance().internalRemoveListener(listener);
   }

   public void internalAddListener(IServiceListener listener) {
      if (listener != null) {
         serviceListeners.add(listener);
      }
   }

   public void internalRemoveListener(IServiceListener listener) {
      if (listener != null) {
         serviceListeners.remove(listener);
      }
   }

   private void internalUpdateStatus(String name, IHealthStatus healthStatus) {
      ServiceStatus serviceStatus = services.get(name);
      if (healthStatus != null) {
         if (serviceStatus == null) {
            serviceStatus = new ServiceStatus(name);
            services.put(name, serviceStatus);
         }
         serviceStatus.setHealthStatus(healthStatus);
         notifyListeners(serviceStatus);
      }
   }

   private void notifyListeners(ServiceStatus serviceStatus) {
      synchronized (serviceListeners) {
         for (IServiceListener listener : serviceListeners) {
            try {
               listener.onStatusChange(serviceStatus);
            } catch (Exception ex) {
               OseeLog.log(CoreClientActivator.class, Level.WARNING, String.format("Error notifying listener [%s]",
                     listener));
            }
         }
      }
   }

   public static Collection<ServiceStatus> getServiceStatus() {
      return getInstance().services.values();
   }

   public static boolean areServicesAvailable() {
      boolean result = true;
      for (ServiceStatus serviceStatus : getServiceStatus()) {
         result &= serviceStatus.isHealthOk();
      }
      return result;
   }

   public static String getServicesStatusMessage() {
      StringBuilder message = new StringBuilder();
      Collection<ServiceStatus> serviceInfos = getServiceStatus();
      for (ServiceStatus serviceInfo : serviceInfos) {
         if (!serviceInfo.isHealthOk()) {
            message.append(serviceInfo.getName());
            message.append(" is not available. Error: ");
            message.append(serviceInfo.getErrorMessage());
            message.append("\n");
         }
      }
      return message.toString();
   }

   private final class DataStoreMonitor implements IDbConnectionListener {

      private static final String DATABASE_SERVICE = "Osee Repository";

      private String databaseDetails;

      protected DataStoreMonitor() {
         this.databaseDetails = null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.db.connection.IDbConnectionListener#onConnectionStatusUpdate(boolean)
       */
      @Override
      public void onConnectionStatusUpdate(final boolean isAlive) {
         SafeRunner.run(new ISafeRunnable() {

            @Override
            public void handleException(Throwable exception) {
            }

            @Override
            public void run() throws Exception {
               Throwable throwable = null;
               String detailsMessage = databaseDetails;
               if (detailsMessage == null) {
                  try {
                     databaseDetails = getDbConnectionDetails();
                  } catch (Exception ex) {
                     detailsMessage =
                           String.format("%s- %s", ClientSessionManager.getDataStoreName(),
                                 ClientSessionManager.getDataStoreLoginName());
                     throwable = ex;
                  }
               }

               if (!isAlive && throwable == null) {
                  Connection connection = null;
                  try {
                     connection = ConnectionHandler.getPooledConnection();
                  } catch (Exception ex) {
                     throwable = ex;
                  } finally {
                     if (connection != null) {
                        connection.close();
                     }
                  }
               }
               ServiceHealthManager.updateStatus(DATABASE_SERVICE, new BaseStatus(isAlive ? Level.INFO : Level.SEVERE,
                     databaseDetails, throwable));
            }
         });
      }

      private String getDbConnectionDetails() throws OseeDataStoreException, SQLException {
         String dbStatus = null;
         Connection connection = null;
         try {
            connection = ConnectionHandler.getPooledConnection();
            DatabaseMetaData dbData = connection.getMetaData();
            dbStatus =
                  getDbStatus(dbData.getDatabaseProductName(), dbData.getDatabaseProductVersion(), dbData.getURL());
         } finally {
            if (connection != null) {
               try {
                  connection.close();
               } catch (SQLException ex) {
               }
            }
         }
         return dbStatus;
      }

      private String getDbStatus(String dbName, String dbVersion, String url) {
         StringBuffer dbInfo = new StringBuffer();
         if (Strings.isValid(dbName)) {
            dbName = dbName.replaceAll("\n", "");
            dbName = dbName.replaceAll("\r", "");
         }
         dbInfo.append(dbName);
         dbInfo.append(" ");
         dbInfo.append(dbVersion);
         dbInfo.append(" - ");
         Pattern pattern = Pattern.compile("//(.*)?.*?/(.*)");
         Matcher matcher = pattern.matcher(url);
         if (matcher.find()) {
            dbInfo.append(matcher.group(1));
            dbInfo.append(" - ");
            dbInfo.append(matcher.group(2));
         }
         dbInfo.append("  ");
         return dbInfo.toString();
      }
   }
}
