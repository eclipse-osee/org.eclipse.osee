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
package org.eclipse.osee.framework.skynet.core.artifact;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifacts extends DbTransaction {

   private static final String INSERT_SELECT_ITEM =
      "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT /*+ ordered FIRST_ROWS */ ?, ?, txs.gamma_id, txs.transaction_id FROM osee_join_artifact aj, %s item, osee_txs txs WHERE aj.query_id = ? AND %s AND item.gamma_id = txs.gamma_id AND aj.branch_id = txs.branch_id";
   private static final String COUNT_ARTIFACT_VIOLATIONS =
      "SELECT art.art_id, txs.branch_id FROM osee_join_artifact aj, osee_artifact art, osee_txs txs WHERE aj.query_id = ? AND aj.art_id = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = aj.branch_id";
   private static final String DELETE_FROM_TXS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_txs txs1 WHERE EXISTS ( select 1 from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.transaction_id = txs1.transaction_id AND jt1.gamma_id = txs1.gamma_id)";
   private static final String DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_tx_details txd1 WHERE EXISTS ( select 1 from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.transaction_id = txd1.transaction_id AND not exists ( select * from osee_txs txs1 where txs1.transaction_id = jt1.transaction_id))";
   private static final String DELETE_FROM_RELATION_VERSIONS =
      "DELETE FROM osee_relation_link rel1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = rel1.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ATTRIBUTE_VERSIONS =
      "DELETE FROM osee_attribute attr1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = attr1.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ARTIFACT_VERSIONS =
      "DELETE FROM osee_artifact art WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = art.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";

   private final Collection<? extends Artifact> artifactsToPurge;

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge) {
      this.artifactsToPurge = artifactsToPurge;
   }

   @Override
   protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
      if (artifactsToPurge == null || artifactsToPurge.size() == 0) {
         return;
      }
      //first determine if the purge is legal.
      List<Object[]> batchParameters = new ArrayList<Object[]>();
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      try {
         for (Artifact art : artifactsToPurge) {
            for (Branch branch : art.getBranch().getChildBranches(true)) {
               batchParameters.add(new Object[] {
                  queryId,
                  insertTime,
                  art.getArtId(),
                  branch.getId(),
                  SQL3DataType.INTEGER});
            }
         }
         if (batchParameters.size() > 0) {
            ArtifactLoader.insertIntoArtifactJoin(connection, batchParameters);
            IOseeStatement chStmt = ConnectionHandler.getStatement(connection);
            try {
               chStmt.runPreparedQuery(COUNT_ARTIFACT_VIOLATIONS, queryId);
               boolean failed = false;
               StringBuilder sb = new StringBuilder();
               while (chStmt.next()) {
                  failed = true;
                  sb.append("ArtifactId[");
                  sb.append(chStmt.getInt("art_id"));
                  sb.append("] BranchId[");
                  sb.append(chStmt.getInt("branch_id"));
                  sb.append("]\n");
               }
               if (failed) {
                  throw new OseeCoreException(
                     "Unable to purge because the following artifacts exist on child branches.\n%s", sb.toString());
               }
            } finally {
               ArtifactLoader.clearQuery(connection, queryId);
               chStmt.close();
            }
         }

         // now load the artifacts to be purged
         batchParameters.clear();
         queryId = ArtifactLoader.getNewQueryId();
         insertTime = GlobalTime.GreenwichMeanTimestamp();

         // insert into the artifact_join_table
         for (Artifact art : artifactsToPurge) {
            batchParameters.add(new Object[] {
               queryId,
               insertTime,
               art.getArtId(),
               art.getBranch().getId(),
               SQL3DataType.INTEGER});
         }
         ArtifactLoader.insertIntoArtifactJoin(connection, batchParameters);

         //run the insert select queries to populate the osee_join_transaction table  (this will take care of the txs table)
         int transactionJoinId = ArtifactLoader.getNewQueryId();
         //run the insert select queries to populate the osee_join_transaction table  (this will take care of the txs table)

         insertSelectItems(connection, "osee_relation_link",
            "(aj.art_id = item.a_art_id OR aj.art_id = item.b_art_id)", transactionJoinId, insertTime, queryId);
         insertSelectItems(connection, "osee_attribute", "aj.art_id = item.art_id", transactionJoinId, insertTime,
            queryId);
         insertSelectItems(connection, "osee_artifact", "aj.art_id = item.art_id", transactionJoinId, insertTime,
            queryId);

         int txsDeletes =
            ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_TXS_USING_JOIN_TRANSACTION, transactionJoinId);

         int txDetails =
            ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION,
               transactionJoinId);

         int relationVersions =
            ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_RELATION_VERSIONS, transactionJoinId);
         int attributeVersions =
            ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_ATTRIBUTE_VERSIONS, transactionJoinId);
         int artifactVersions =
            ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_ARTIFACT_VERSIONS, transactionJoinId);

         OseeLog.log(
            Activator.class,
            Level.INFO,
            String.format(
               "Purge Row Deletes: txs rows [%d], rel ver rows [%d], attr ver rows [%d] art ver rows [%d].  txs vs. total versions [%d vs %d]",
               txsDeletes, relationVersions, attributeVersions, artifactVersions, txDetails,
               (relationVersions + attributeVersions + artifactVersions)));

         ConnectionHandler.runPreparedUpdate(connection, "DELETE FROM osee_join_transaction where query_id = ?",
            transactionJoinId);

         Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();
         for (Artifact artifact : artifactsToPurge) {
            artifactChanges.add(new EventBasicGuidArtifact(EventModType.Purged, artifact));
            ArtifactCache.deCache(artifact);
            artifact.internalSetDeleted();
            for (RelationLink rel : artifact.getRelationsAll(false)) {
               rel.markAsPurged();
            }
            for (Attribute<?> attr : artifact.internalGetAttributes()) {
               attr.markAsPurged();
            }
         }

         // Kick Local and Remote Events
         ArtifactEvent artifactEvent = new ArtifactEvent(artifactsToPurge.iterator().next().getBranch());
         for (EventBasicGuidArtifact guidArt : artifactChanges) {
            artifactEvent.getArtifacts().add(guidArt);
         }
         OseeEventManager.kickPersistEvent(PurgeArtifacts.class, artifactEvent);

      } finally {
         ArtifactLoader.clearQuery(connection, queryId);
      }
   }

   public void insertSelectItems(OseeConnection connection, String tableName, String artifactJoinSql, int transactionJoinId, Timestamp insertTime, int queryId) throws OseeCoreException {
      IOseeDatabaseService databaseService = Activator.getInstance().getOseeDatabaseService();
      String sql = String.format(INSERT_SELECT_ITEM, tableName, artifactJoinSql);
      databaseService.runPreparedUpdate(connection, sql, transactionJoinId, insertTime, queryId);
   }
}