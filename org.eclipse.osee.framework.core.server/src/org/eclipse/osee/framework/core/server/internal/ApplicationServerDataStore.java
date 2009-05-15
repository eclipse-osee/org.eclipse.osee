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

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerDataStore {

   private static final String INSERT_LOOKUP_TABLE =
         "INSERT INTO osee_server_lookup (server_id, version_id, server_address, port, start_time, accepts_requests) VALUES (?,?,?,?,?,?)";

   private static final String UPDATE_LOOKUP_TABLE =
         "UPDATE osee_server_lookup SET accepts_requests = ? WHERE server_address = ? AND port = ?";

   private static final String DELETE_FROM_LOOKUP_TABLE =
         "DELETE FROM osee_server_lookup WHERE server_address = ? AND port = ? AND version_id=?";

   private static final String DELETE_FROM_LOOKUP_TABLE_BY_ID = "DELETE FROM osee_server_lookup WHERE server_id = ?";

   private static final String GET_NUMBER_OF_SESSIONS =
         "SELECT count(1) FROM osee_session WHERE managed_by_server_id = ?";

   private static final String SELECT_FROM_LOOKUP_TABLE = "SELECT * FROM osee_server_lookup";

   private static final String SELECT_SUPPORTED_VERSIONS_FROM_LOOKUP_TABLE_BY_SERVER_ID =
         "SELECT version_id FROM osee_server_lookup where server_id = ?";

   static void removeByServerId(List<OseeServerInfo> infos) throws OseeDataStoreException {
      if (!infos.isEmpty()) {
         List<Object[]> data = new ArrayList<Object[]>();
         for (OseeServerInfo info : infos) {
            data.add(new Object[] {info.getServerId()});
         }
         ConnectionHandler.runBatchUpdate(DELETE_FROM_LOOKUP_TABLE_BY_ID, data);
      }
   }

   static boolean deregisterWithDb(OseeServerInfo applicationServerInfo) {
      boolean status = false;
      try {
         String address = applicationServerInfo.getServerAddress();
         int port = applicationServerInfo.getPort();
         List<Object[]> data = new ArrayList<Object[]>();
         for (String version : applicationServerInfo.getVersion()) {
            data.add(new Object[] {address, port, version});
         }
         ConnectionHandler.runBatchUpdate(DELETE_FROM_LOOKUP_TABLE, data);
         status = true;
      } catch (OseeCoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.INFO, "Server lookup table not initialized");
      }
      return status;
   }

   static boolean registerWithDb(OseeServerInfo applicationServerInfo) {
      boolean status = false;
      try {
         String serverId = applicationServerInfo.getServerId();
         String address = applicationServerInfo.getServerAddress();
         int port = applicationServerInfo.getPort();
         Timestamp dateStarted = applicationServerInfo.getDateStarted();
         int acceptingRequests = applicationServerInfo.isAcceptingRequests() ? 1 : 0;
         List<Object[]> data = new ArrayList<Object[]>();
         for (String version : applicationServerInfo.getVersion()) {
            data.add(new Object[] {serverId, version, address, port, dateStarted, acceptingRequests});
         }
         ConnectionHandler.runBatchUpdate(INSERT_LOOKUP_TABLE, data);
         status = true;
      } catch (OseeCoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.INFO, "Server lookup table not initialized");
      }
      return status;
   }

   static boolean updateServerState(OseeServerInfo applicationServerInfo, boolean state) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(UPDATE_LOOKUP_TABLE, state ? 1 : 0, applicationServerInfo.getServerAddress(),
            applicationServerInfo.getPort());
      return true;
   }

   static boolean isCompatibleVersion(String serverVersion, String clientVersion) {
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

   static Collection<OseeServerInfo> getApplicationServerInfos(String clientVersion) throws OseeDataStoreException {
      CompositeKeyHashMap<String, Integer, OseeServerInfo> servers =
            new CompositeKeyHashMap<String, Integer, OseeServerInfo>();
      if (Strings.isValid(clientVersion)) {
         ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
         try {
            chStmt.runPreparedQuery(SELECT_FROM_LOOKUP_TABLE);
            while (chStmt.next()) {
               String serverVersion = chStmt.getString("version_id");
               if (Strings.isValid(serverVersion)) {
                  if (isCompatibleVersion(serverVersion, clientVersion)) {
                     String serverAddress = chStmt.getString("server_address");
                     int port = chStmt.getInt("port");
                     OseeServerInfo info = servers.get(serverAddress, port);
                     if (info == null) {
                        info =
                              new OseeServerInfo(chStmt.getString("server_id"), serverAddress, port,
                                    new String[] {serverVersion}, chStmt.getTimestamp("start_time"),
                                    chStmt.getInt("accepts_requests") != 0 ? true : false);
                        servers.put(serverAddress, port, info);
                     } else {
                        Set<String> versions = new HashSet<String>(Arrays.asList(info.getVersion()));
                        if (!versions.contains(serverVersion)) {
                           versions.add(serverVersion);
                           info =
                                 new OseeServerInfo(chStmt.getString("server_id"), serverAddress, port,
                                       versions.toArray(new String[versions.size()]),
                                       chStmt.getTimestamp("start_time"),
                                       chStmt.getInt("accepts_requests") != 0 ? true : false);
                           servers.put(serverAddress, port, info);
                        }
                     }
                  }
               }
            }
         } finally {
            chStmt.close();
         }
      }
      return servers.values();
   }

   static Set<String> getOseeVersionsByServerId(String serverId) throws OseeDataStoreException {
      Set<String> supportedVersions = new HashSet<String>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         chStmt.runPreparedQuery(SELECT_SUPPORTED_VERSIONS_FROM_LOOKUP_TABLE_BY_SERVER_ID, serverId);
         while (chStmt.next()) {
            String version = chStmt.getString("version_id");
            if (Strings.isValid(version)) {
               supportedVersions.add(version);
            }
         }
      } catch (Exception ex) {
         OseeLog.log(CoreServerActivator.class, Level.INFO, "Server lookup table is not initialized");
      } finally {
         chStmt.close();
      }
      return supportedVersions;
   }

   static int getNumberOfSessions(String serverId) throws OseeDataStoreException {
      return ConnectionHandler.runPreparedQueryFetchInt(0, GET_NUMBER_OF_SESSIONS, serverId);
   }
}
