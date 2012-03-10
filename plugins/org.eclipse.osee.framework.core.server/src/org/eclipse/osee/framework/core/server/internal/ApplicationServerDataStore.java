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
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.logger.Log;

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

   private final Log logger;
   private final IOseeDatabaseService dbService;

   public ApplicationServerDataStore(Log logger, IOseeDatabaseService dbService) {
      this.logger = logger;
      this.dbService = dbService;
   }

   private Log getLogger() {
      return logger;
   }

   private IOseeDatabaseService getDbService() {
      return dbService;
   }

   public void removeByServerId(Collection<OseeServerInfo> infos) throws OseeCoreException {
      if (!infos.isEmpty()) {
         List<Object[]> data = new ArrayList<Object[]>();
         for (OseeServerInfo info : infos) {
            data.add(new Object[] {info.getServerId()});
         }
         getDbService().runBatchUpdate(DELETE_FROM_LOOKUP_TABLE_BY_ID, data);
      }
   }

   public boolean deregisterWithDb(OseeServerInfo applicationServerInfo) {
      boolean status = false;
      try {
         String address = applicationServerInfo.getServerAddress();
         int port = applicationServerInfo.getPort();
         List<Object[]> data = new ArrayList<Object[]>();
         for (String version : applicationServerInfo.getVersion()) {
            data.add(new Object[] {address, port, version});
         }
         getDbService().runBatchUpdate(DELETE_FROM_LOOKUP_TABLE, data);
         status = true;
      } catch (OseeCoreException ex) {
         getLogger().info("Server lookup table is not initialized");
      }
      return status;
   }

   public boolean registerWithDb(OseeServerInfo applicationServerInfo) {
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
         getDbService().runBatchUpdate(INSERT_LOOKUP_TABLE, data);
         status = true;
      } catch (OseeCoreException ex) {
         getLogger().info("Server lookup table is not initialized");
      }
      return status;
   }

   @SuppressWarnings("unchecked")
   public boolean updateServerState(OseeServerInfo applicationServerInfo, boolean state) throws OseeCoreException {
      getDbService().runPreparedUpdate(UPDATE_LOOKUP_TABLE, state ? 1 : 0, applicationServerInfo.getServerAddress(),
         applicationServerInfo.getPort());
      return true;
   }

   public boolean isCompatibleVersion(String serverVersion, String clientVersion) {
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

   public Collection<OseeServerInfo> getApplicationServerInfos(String clientVersion) throws OseeCoreException {
      CompositeKeyHashMap<String, Integer, OseeServerInfo> servers =
         new CompositeKeyHashMap<String, Integer, OseeServerInfo>();
      if (Strings.isValid(clientVersion)) {
         IOseeStatement chStmt = getDbService().getStatement();
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
                                 versions.toArray(new String[versions.size()]), chStmt.getTimestamp("start_time"),
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

   public Collection<OseeServerInfo> getAllApplicationServerInfos() throws OseeCoreException {
      Collection<OseeServerInfo> infos = new ArrayList<OseeServerInfo>();
      IOseeStatement chStmt = getDbService().getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_FROM_LOOKUP_TABLE);
         while (chStmt.next()) {
            String serverVersion = chStmt.getString("version_id");
            String serverAddress = chStmt.getString("server_address");
            int port = chStmt.getInt("port");
            OseeServerInfo info =
               new OseeServerInfo(chStmt.getString("server_id"), serverAddress, port, new String[] {serverVersion},
                  chStmt.getTimestamp("start_time"), chStmt.getInt("accepts_requests") != 0 ? true : false);
            infos.add(info);
         }
      } finally {
         chStmt.close();
      }
      return infos;
   }

   public Set<String> getOseeVersionsByServerId(String serverId) throws OseeDataStoreException {
      Set<String> supportedVersions = new HashSet<String>();
      IOseeStatement chStmt = getDbService().getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_SUPPORTED_VERSIONS_FROM_LOOKUP_TABLE_BY_SERVER_ID, serverId);
         while (chStmt.next()) {
            String version = chStmt.getString("version_id");
            if (Strings.isValid(version)) {
               supportedVersions.add(version);
            }
         }
      } catch (Exception ex) {
         getLogger().info("Server lookup table is not initialized");
      } finally {
         chStmt.close();
      }
      return supportedVersions;
   }

   public int getNumberOfSessions(String serverId) throws OseeCoreException {
      return getDbService().runPreparedQueryFetchObject(0, GET_NUMBER_OF_SESSIONS, serverId);
   }
}
