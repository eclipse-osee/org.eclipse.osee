/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.framework.core.server.internal;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.server.OseeInfo;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcTransaction;
import org.eclipse.osee.orcs.OseeDb;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerDataStore {

   private final JdbcClient jdbcClient;

   public ApplicationServerDataStore(JdbcClient jdbcClient) {
      this.jdbcClient = jdbcClient;
   }

   public void create(OseeServerInfo info) {
      executeTx(TxType.CREATE, info);
   }

   public void update(OseeServerInfo info) {
      executeTx(TxType.UPDATE, info);
   }

   public void delete(OseeServerInfo info) {
      executeTx(TxType.DELETE, info);
   }

   private void executeTx(TxType op, OseeServerInfo info) {
      jdbcClient.runTransaction(new ServerLookupTx(jdbcClient, op, info));
   }

   private static enum TxType {
      CREATE,
      UPDATE,
      DELETE;
   }

   public String getDatabaseGuid() {
      return OseeInfo.getDatabaseGuid(jdbcClient);
   }

   private static final class ServerLookupTx extends JdbcTransaction {

      private static final String DELETE_FROM_LOOKUP_TABLE_BY_ID = "DELETE FROM osee_server_lookup WHERE server_id = ?";

      private final TxType txType;
      private final OseeServerInfo data;
      private final JdbcClient jdbcClient;

      public ServerLookupTx(JdbcClient jdbcClient, TxType txType, OseeServerInfo data) {
         this.jdbcClient = jdbcClient;
         this.txType = txType;
         this.data = data;
      }

      @Override
      public void handleTxWork(JdbcConnection connection) {
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

      private void create(JdbcConnection connection) {
         List<Object[]> insertData = new ArrayList<>();
         String serverId = data.getServerId();
         String uri = data.getUri();
         Timestamp dateStarted = data.getDateStarted();
         int acceptingRequests = data.isAcceptingRequests() ? 1 : 0;

         for (String version : data.getVersion()) {
            insertData.add(new Object[] {serverId, version, uri, dateStarted, acceptingRequests});
         }
         if (!insertData.isEmpty()) {
            jdbcClient.runBatchUpdate(connection, OseeDb.OSEE_SERVER_LOOKUP_TABLE.getInsertSql(), insertData);
         }
      }

      private void update(JdbcConnection connection) {
         delete(connection);
         create(connection);
      }

      private void delete(JdbcConnection connection) {
         List<Object[]> deleteData = new ArrayList<>();
         deleteData.add(new Object[] {data.getServerId()});
         if (!deleteData.isEmpty()) {
            jdbcClient.runBatchUpdate(connection, DELETE_FROM_LOOKUP_TABLE_BY_ID, deleteData);
         }
      }

   }

}
