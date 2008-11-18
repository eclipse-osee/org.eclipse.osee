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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
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

   private static final String SELECT_FROM_LOOKUP_TABLE = "SELECT * FROM osee_server_lookup where version_id%s";

   static boolean deregisterWithDb(OseeServerInfo applicationServerInfo) {
      boolean status = false;
      try {
         ConnectionHandler.runPreparedUpdate(DELETE_FROM_LOOKUP_TABLE, applicationServerInfo.getServerAddress(),
               applicationServerInfo.getPort(), applicationServerInfo.getVersion());
         status = true;
      } catch (OseeCoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.WARNING, "Unable to deregister server from lookup table.", ex);
      }
      return status;
   }

   static boolean registerWithDb(OseeServerInfo applicationServerInfo) {
      boolean status = false;
      try {
         ConnectionHandler.runPreparedUpdate(INSERT_LOOKUP_TABLE, applicationServerInfo.getServerId(),
               applicationServerInfo.getVersion(), applicationServerInfo.getServerAddress(),
               applicationServerInfo.getPort(), applicationServerInfo.getDateStarted(), 1);
         status = true;
      } catch (OseeCoreException ex) {
         OseeLog.log(CoreServerActivator.class, Level.WARNING, "Unable to register server into lookup table.", ex);
      }
      return status;
   }

   static boolean updateServerState(OseeServerInfo applicationServerInfo, boolean state) throws OseeDataStoreException {
      ConnectionHandler.runPreparedUpdate(UPDATE_LOOKUP_TABLE, state ? 1 : 0, applicationServerInfo.getServerAddress(),
            applicationServerInfo.getPort());
      return true;
   }

   static List<OseeServerInfo> getApplicationServerInfos(String version) throws OseeDataStoreException {
      List<OseeServerInfo> toReturn = new ArrayList<OseeServerInfo>();
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement();
      try {
         String query = String.format(SELECT_FROM_LOOKUP_TABLE, version.contains("%") ? " LIKE ?" : " = ?");
         chStmt.runPreparedQuery(query, version);
         while (chStmt.next()) {
            toReturn.add(new OseeServerInfo(chStmt.getString("server_id"), chStmt.getString("server_address"),
                  chStmt.getInt("port"), chStmt.getString("version_id"), chStmt.getTimestamp("start_time"),
                  chStmt.getInt("accepts_requests") != 0 ? true : false));
         }

      } finally {
         chStmt.close();
      }
      return toReturn;
   }
}
