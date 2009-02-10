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
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbTransaction;
import org.eclipse.osee.framework.db.connection.OseeConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.db.connection.info.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.attribute.Attribute;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * @author Ryan D. Brooks
 */
public class PurgeDbTransaction extends DbTransaction {

   private static final String INSERT_SELECT_RELATIONS =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_relation_link rel1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND (al1.art_id = rel1.a_art_id OR al1.art_id = rel1.b_art_id) AND rel1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id";

   private static final String INSERT_SELECT_ATTRIBUTES =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_attribute att1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = att1.art_id AND att1.gamma_id = txs1.gamma_id AND txs1.transaction_id = txd1.transaction_id AND txd1.branch_id = al1.branch_id order by al1.branch_id, al1.art_id";

   private static final String INSERT_SELECT_ARTIFACTS =
         "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id) SELECT ?, ?, txs1.gamma_id, txs1.transaction_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id";

   private static final String COUNT_ARTIFACT_VIOLATIONS =
         "SELECT art1.art_id, txd1.branch_id FROM osee_join_artifact al1, osee_artifact art1, osee_artifact_version arv1, osee_txs txs1, osee_tx_details txd1 WHERE al1.query_id = ? AND al1.art_id = art1.art_id AND art1.art_id = arv1.art_id AND arv1.gamma_id = txs1.gamma_id AND txd1.branch_id = al1.branch_id AND txd1.transaction_id = txs1.transaction_id";
   private static final String DELETE_FROM_TXS_USING_JOIN_TRANSACTION =
         "DELETE FROM osee_txs txs1 WHERE EXISTS ( select 1 from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.transaction_id = txs1.transaction_id AND jt1.gamma_id = txs1.gamma_id)";
   private static final String DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION =
         "DELETE FROM osee_tx_details txd1 WHERE EXISTS ( select 1 from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.transaction_id = txd1.transaction_id AND not exists ( select * from osee_txs txs1 where txs1.transaction_id = jt1.transaction_id))";
   private static final String DELETE_FROM_RELATION_VERSIONS =
         "DELETE FROM osee_relation_link rel1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = rel1.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ATTRIBUTE_VERSIONS =
         "DELETE FROM osee_attribute attr1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = attr1.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ARTIFACT_VERSIONS =
         "DELETE FROM osee_artifact_version artv1 WHERE EXISTS ( select * from osee_join_transaction jt1 WHERE jt1.query_id = ? AND jt1.gamma_id = artv1.gamma_id AND not exists ( select * from osee_txs txs1 where txs1.gamma_id = jt1.gamma_id))";
   private static final String DELETE_FROM_ARTIFACT =
         "DELETE FROM osee_artifact art1 WHERE EXISTS ( select * from osee_join_artifact ja1 WHERE ja1.query_id = ? AND ja1.art_id = art1.art_id AND not exists ( select * from osee_artifact_version artv1 where artv1.art_id = ja1.art_id))";

   private final Collection<? extends Artifact> artifactsToPurge;

   /**
    * @param artifactsToPurge
    */
   public PurgeDbTransaction(Collection<? extends Artifact> artifactsToPurge) throws OseeCoreException {
      this.artifactsToPurge = artifactsToPurge;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.DbTransaction#handleTxWork(java.sql.Connection)
    */
   @Override
   protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
      //first determine if the purge is legal.
      List<Object[]> batchParameters = new ArrayList<Object[]>();
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      try {
         for (Artifact art : artifactsToPurge) {
            for (Branch branch : art.getBranch().getChildBranches(true)) {
               batchParameters.add(new Object[] {queryId, insertTime, art.getArtId(), branch.getBranchId(),
                     SQL3DataType.INTEGER});
            }
         }
         if (batchParameters.size() > 0) {
            ArtifactLoader.insertIntoArtifactJoin(connection, batchParameters);
            ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
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
                  throw new OseeCoreException(String.format(
                        "Unable to purge because the following artifacts exist on child branches.\n%s", sb.toString()));
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
            batchParameters.add(new Object[] {queryId, insertTime, art.getArtId(), art.getBranch().getBranchId(),
                  SQL3DataType.INTEGER});
         }
         ArtifactLoader.insertIntoArtifactJoin(connection, batchParameters);

         //run the insert select queries to populate the osee_join_transaction table  (this will take care of the txs table)    
         int transactionJoinId = ArtifactLoader.getNewQueryId();
         ConnectionHandler.runPreparedUpdate(connection, INSERT_SELECT_RELATIONS, transactionJoinId, insertTime,
               queryId);
         ConnectionHandler.runPreparedUpdate(connection, INSERT_SELECT_ATTRIBUTES, transactionJoinId, insertTime,
               queryId);
         ConnectionHandler.runPreparedUpdate(connection, INSERT_SELECT_ARTIFACTS, transactionJoinId, insertTime,
               queryId);

         //delete from the txs table
         int txsDeletes =
               ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_TXS_USING_JOIN_TRANSACTION,
                     transactionJoinId);

         int txdDeletes =
               ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION,
                     transactionJoinId);

         int relationVersions =
               ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_RELATION_VERSIONS, transactionJoinId);
         int attributeVersions =
               ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_ATTRIBUTE_VERSIONS, transactionJoinId);
         int artifactVersions =
               ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_ARTIFACT_VERSIONS, transactionJoinId);
         int artifact = ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_ARTIFACT, queryId);

         OseeLog.log(
               SkynetActivator.class,
               Level.INFO,
               String.format(
                     "Purge Row Deletes: txs rows [%d], rel ver rows [%d], attr ver rows [%d] art ver rows [%d] art rows [%d].  txs vs. total versions [%d vs %d]",
                     txsDeletes, relationVersions, attributeVersions, artifactVersions, artifact, txsDeletes,
                     (relationVersions + attributeVersions + artifactVersions)));

         ConnectionHandler.runPreparedUpdate(connection, "DELETE FROM osee_join_transaction where query_id = ?",
               transactionJoinId);

         for (Artifact art : artifactsToPurge) {
            art.setDeleted();
            for (RelationLink rel : art.getRelationsAll(false)) {
               rel.markAsPurged();
            }
            for (Attribute<?> attr : art.internalGetAttributes()) {
               attr.markAsPurged();
            }
         }

         // Kick Local and Remote Events
         OseeEventManager.kickArtifactsPurgedEvent("PurgeDbTransaction", new LoadedArtifacts(artifactsToPurge));

      } finally {
         ArtifactLoader.clearQuery(connection, queryId);
      }
   }
}