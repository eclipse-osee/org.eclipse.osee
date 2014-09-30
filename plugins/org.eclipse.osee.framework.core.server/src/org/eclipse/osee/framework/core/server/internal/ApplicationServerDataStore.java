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
import java.util.List;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.DatabaseTransactions;
import org.eclipse.osee.framework.database.core.IDbTransactionWork;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Roberto E. Escobar
 */
public class ApplicationServerDataStore {

   private final IOseeDatabaseService dbService;

   public ApplicationServerDataStore(IOseeDatabaseService dbService) {
      this.dbService = dbService;
   }

   private IOseeDatabaseService getDbService() {
      return dbService;
   }

   public void create(OseeServerInfo info) throws OseeCoreException {
      executeTx(TxType.CREATE, info);
   }

   public void update(OseeServerInfo info) throws OseeCoreException {
      executeTx(TxType.UPDATE, info);
   }

   public void delete(OseeServerInfo info) throws OseeCoreException {
      executeTx(TxType.DELETE, info);
   }

   private void executeTx(TxType op, OseeServerInfo info) throws OseeCoreException {
      IDbTransactionWork tx = new ServerLookupTx(getDbService(), op, info);
      DatabaseTransactions.execute(getDbService(), getDbService().getConnection(), tx);
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
      private final OseeServerInfo data;
      private final IOseeDatabaseService dbService;

      public ServerLookupTx(IOseeDatabaseService dbService, TxType txType, OseeServerInfo data) {
         this.dbService = dbService;
         this.txType = txType;
         this.data = data;
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
         String serverId = data.getServerId();
         URI serverUri = data.getUri();
         String uri = serverUri.toString();
         Timestamp dateStarted = data.getDateStarted();
         int acceptingRequests = data.isAcceptingRequests() ? 1 : 0;

         for (String version : data.getVersion()) {
            insertData.add(new Object[] {serverId, version, uri, dateStarted, acceptingRequests});
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
         deleteData.add(new Object[] {data.getServerId()});
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
