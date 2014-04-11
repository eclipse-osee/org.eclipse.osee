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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.database.core.AbstractDbTxOperation;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.SQL3DataType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.time.GlobalTime;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifacts extends AbstractDbTxOperation {

   private static final String INSERT_SELECT_ITEM =
      "INSERT INTO osee_join_transaction (query_id, insert_time, gamma_id, transaction_id, branch_id) SELECT /*+ ordered */ ?, ?, txs.gamma_id, txs.transaction_id, aj.branch_id FROM osee_join_artifact aj, %s item, osee_txs txs WHERE aj.query_id = ? AND %s AND item.gamma_id = txs.gamma_id AND aj.branch_id = txs.branch_id";

   private static final String COUNT_ARTIFACT_VIOLATIONS =
      "SELECT art.art_id, txs.branch_id FROM osee_join_artifact aj, osee_artifact art, osee_txs txs WHERE aj.query_id = ? AND aj.art_id = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = aj.branch_id";

   private static final String DELETE_FROM_TXS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_txs txs WHERE EXISTS (select 1 from osee_join_transaction jt WHERE jt.query_id = ? AND jt.branch_id = txs.branch_id AND jt.gamma_id = txs.gamma_id AND jt.transaction_id = txs.transaction_id)";

   private static final String DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_tx_details txd WHERE EXISTS (select 1 from osee_join_transaction jt WHERE jt.query_id = ? AND jt.branch_id = txd.branch_id AND jt.transaction_id = txd.transaction_id AND not exists (select * from osee_txs txs where jt.branch_id = txs.branch_id and jt.transaction_id = txs.transaction_id))";

   private final List<Artifact> artifactsToPurge;
   private boolean success;
   private final boolean recurseChildrenBranches;

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge) throws OseeCoreException {
      this(artifactsToPurge, false);
   }

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge, boolean recurseChildrenBranches) throws OseeCoreException {
      super(ServiceUtil.getOseeDatabaseService(), "Purge Artifact", Activator.PLUGIN_ID);
      this.artifactsToPurge = new LinkedList<Artifact>(artifactsToPurge);
      this.success = false;
      this.recurseChildrenBranches = recurseChildrenBranches;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, OseeConnection connection) throws OseeCoreException {
      if (artifactsToPurge == null || artifactsToPurge.isEmpty()) {
         return;
      }
      //first determine if the purge is legal.
      List<Object[]> batchParameters = new ArrayList<Object[]>();
      int queryId = ArtifactLoader.getNewQueryId();
      Timestamp insertTime = GlobalTime.GreenwichMeanTimestamp();

      try {
         for (Artifact art : artifactsToPurge) {
            for (Branch branch : art.getFullBranch().getChildBranches(true)) {
               batchParameters.add(new Object[] {
                  queryId,
                  insertTime,
                  art.getArtId(),
                  branch.getUuid(),
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
                  int artId = chStmt.getInt("art_id");
                  long branchId = chStmt.getLong("branch_id");
                  if (recurseChildrenBranches) {
                     Branch branch = BranchManager.getBranch(branchId);
                     Artifact artifactFromId = ArtifactQuery.getArtifactFromId(artId, branch);
                     artifactsToPurge.add(artifactFromId);
                  } else {
                     failed = true;
                     sb.append("ArtifactId[");
                     sb.append(artId);
                     sb.append("] BranchId[");
                     sb.append(branchId);
                     sb.append("]\n");
                  }
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
               art.getFullBranch().getUuid(),
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

         ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_TXS_USING_JOIN_TRANSACTION, transactionJoinId);

         ConnectionHandler.runPreparedUpdate(connection, DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION,
            transactionJoinId);

         ConnectionHandler.runPreparedUpdate(connection, "DELETE FROM osee_join_transaction where query_id = ?",
            transactionJoinId);

         for (Artifact artifact : artifactsToPurge) {
            ArtifactCache.deCache(artifact);
            artifact.internalSetDeleted();
            for (RelationLink rel : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
               rel.markAsPurged();
            }
            for (Attribute<?> attr : artifact.internalGetAttributes()) {
               attr.markAsPurged();
            }
         }
         success = true;
      } finally {
         ArtifactLoader.clearQuery(connection, queryId);
      }
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      if (success) {
         Set<EventBasicGuidArtifact> artifactChanges = new HashSet<EventBasicGuidArtifact>();
         for (Artifact artifact : artifactsToPurge) {
            artifactChanges.add(new EventBasicGuidArtifact(EventModType.Purged, artifact));
         }
         // Kick Local and Remote Events
         ArtifactEvent artifactEvent = new ArtifactEvent(artifactsToPurge.iterator().next().getBranch());
         for (EventBasicGuidArtifact guidArt : artifactChanges) {
            artifactEvent.getArtifacts().add(guidArt);
         }
         OseeEventManager.kickPersistEvent(PurgeArtifacts.class, artifactEvent);
      }
   }

   @SuppressWarnings("unchecked")
   public void insertSelectItems(OseeConnection connection, String tableName, String artifactJoinSql, int transactionJoinId, Timestamp insertTime, int queryId) throws OseeCoreException {
      String sql = String.format(INSERT_SELECT_ITEM, tableName, artifactJoinSql);
      getDatabaseService().runPreparedUpdate(connection, sql, transactionJoinId, insertTime, queryId);
   }

}