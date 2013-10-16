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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.DatabaseTransactions;
import org.eclipse.osee.framework.database.core.IDbTransactionWork;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.logger.Log;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerDataStore {

   private static final String GET_NUMBER_OF_SESSIONS =
      "SELECT count(1) FROM osee_session WHERE managed_by_server_id = ?";

   private static final String SELECT_FROM_LOOKUP_TABLE =
      "SELECT * FROM osee_server_lookup ORDER BY server_uri desc, version_id desc";

   private static final String SELECT_FROM_LOOKUP_TABLE_BY_SERVER_ID =
      "SELECT * FROM osee_server_lookup where server_id = ? ORDER BY version_id desc";

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

   public void create(Iterable<? extends OseeServerInfo> infos) throws OseeCoreException {
      executeTx(TxType.CREATE, infos);
   }

   public void update(Iterable<? extends OseeServerInfo> infos) throws OseeCoreException {
      executeTx(TxType.UPDATE, infos);
   }

   public void delete(Iterable<? extends OseeServerInfo> infos) throws OseeCoreException {
      executeTx(TxType.DELETE, infos);
   }

   private void executeTx(TxType op, Iterable<? extends OseeServerInfo> infos) throws OseeCoreException {
      IDbTransactionWork tx = new ServerLookupTx(getDbService(), op, infos);
      DatabaseTransactions.execute(getDbService(), getDbService().getConnection(), tx);
   }

   public Collection<? extends OseeServerInfo> getAll() throws OseeCoreException {
      Map<String, OseeServerInfoMutable> infos = new HashMap<String, OseeServerInfoMutable>();
      IOseeStatement chStmt = getDbService().getStatement();
      try {
         chStmt.runPreparedQuery(SELECT_FROM_LOOKUP_TABLE);
         while (chStmt.next()) {
            String serverId = chStmt.getString("server_id");
            String serverVersion = chStmt.getString("version_id");
            boolean isAcceptingRequests = chStmt.getInt("accepts_requests") != 0 ? true : false;

            OseeServerInfoMutable info = infos.get(serverId);
            if (info == null) {
               String uri = chStmt.getString("server_uri");
               Timestamp timestamp = chStmt.getTimestamp("start_time");
               info =
                  new OseeServerInfoMutable(serverId, uri, new String[] {serverVersion}, timestamp, isAcceptingRequests);
               infos.put(serverId, info);
            } else {
               boolean acceptingRequests = info.isAcceptingRequests() && isAcceptingRequests;
               info.addVersion(serverVersion);
               info.setAcceptingRequests(acceptingRequests);
            }
         }
      } finally {
         chStmt.close();
      }
      return infos.values();
   }

   public void refresh(OseeServerInfoMutable info) {
      Set<String> original = info.getVersionSet();
      boolean origAcceptingRequests = info.isAcceptingRequests();
      IOseeStatement chStmt = null;
      try {
         chStmt = getDbService().getStatement();
         chStmt.runPreparedQuery(SELECT_FROM_LOOKUP_TABLE_BY_SERVER_ID, info.getServerId());
         info.setVersions(Collections.<String> emptySet());
         while (chStmt.next()) {
            String serverVersion = chStmt.getString("version_id");
            boolean isAcceptingRequests = chStmt.getInt("accepts_requests") != 0 ? true : false;

            boolean acceptingRequests = info.isAcceptingRequests() && isAcceptingRequests;
            info.addVersion(serverVersion);
            info.setAcceptingRequests(acceptingRequests);
         }
      } catch (Exception ex) {
         getLogger().info("Server lookup table is not initialized");
         info.setVersions(original);
         info.setAcceptingRequests(origAcceptingRequests);
      } finally {
         if (chStmt != null) {
            chStmt.close();
         }
      }
   }

   public int getNumberOfSessions(String serverId) throws OseeCoreException {
      return getDbService().runPreparedQueryFetchObject(0, GET_NUMBER_OF_SESSIONS, serverId);
   }

   private static enum TxType {
      CREATE,
      UPDATE,
      DELETE;
   }

   private static final class ServerLookupTx implements IDbTransactionWork {

      private static final String INSERT_LOOKUP_TABLE =
         "INSERT INTO osee_server_lookup (server_id, version_id, server_uri, start_time, accepts_requests) VALUES (?,?,?,?,?)";

      private static final String DELETE_FROM_LOOKUP_TABLE_BY_ID = "DELETE FROM osee_server_lookup WHERE server_id = ?";

      private final TxType txType;
      private final Iterable<? extends OseeServerInfo> datas;
      private final IOseeDatabaseService dbService;

      public ServerLookupTx(IOseeDatabaseService dbService, TxType txType, Iterable<? extends OseeServerInfo> datas) {
         this.dbService = dbService;
         this.txType = txType;
         this.datas = datas;
      }

      @Override
      public String getName() {
         return String.format("[%s] Application Server Info", txType);
      }

      @Override
      public void handleTxWork(OseeConnection connection) throws OseeCoreException {
         switch (txType) {
            case CREATE:
               create(connection);
               break;
            case UPDATE:
               update(connection);
               break;
            case DELETE:
               delete(connection);
               break;
            default:
               break;
         }
      }

      private void create(OseeConnection connection) throws OseeCoreException {
         List<Object[]> insertData = new ArrayList<Object[]>();
         for (OseeServerInfo data : datas) {
            String serverId = data.getServerId();
            URI serverUri = data.getUri();
            String uri = serverUri.toString();
            Timestamp dateStarted = data.getDateStarted();
            int acceptingRequests = data.isAcceptingRequests() ? 1 : 0;

            for (String version : data.getVersion()) {
               insertData.add(new Object[] {serverId, version, uri, dateStarted, acceptingRequests});
            }
         }
         if (!insertData.isEmpty()) {
            dbService.runBatchUpdate(connection, INSERT_LOOKUP_TABLE, insertData);
         }
      }

      private void update(OseeConnection connection) throws OseeCoreException {
         delete(connection);
         create(connection);
      }

      private void delete(OseeConnection connection) throws OseeCoreException {
         List<Object[]> deleteData = new ArrayList<Object[]>();
         for (OseeServerInfo data : datas) {
            deleteData.add(new Object[] {data.getServerId()});
         }
         if (!deleteData.isEmpty()) {
            dbService.runBatchUpdate(connection, DELETE_FROM_LOOKUP_TABLE_BY_ID, deleteData);
         }
      }

      @Override
      public void handleTxException(Exception ex) {
         //
      }

      @Override
      public void handleTxFinally() {
         //
      }
   };

}
