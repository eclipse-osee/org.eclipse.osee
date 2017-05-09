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

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicUuidRelation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.model.ArtifactEvent;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidArtifact;
import org.eclipse.osee.framework.skynet.core.event.model.EventBasicGuidRelation;
import org.eclipse.osee.framework.skynet.core.event.model.EventModType;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.relation.RelationEventType;
import org.eclipse.osee.framework.skynet.core.relation.RelationLink;
import org.eclipse.osee.framework.skynet.core.relation.RelationManager;
import org.eclipse.osee.framework.skynet.core.utility.AbstractDbTxOperation;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.Id4JoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.framework.skynet.core.utility.TransactionJoinQuery;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifacts extends AbstractDbTxOperation {

   private static final String SELECT_ITEM_GAMMAS =
      "SELECT %s txs.gamma_id, txs.transaction_id, aj.id1, aj.id4 FROM osee_join_id4 aj, %s item, osee_txs txs WHERE aj.query_id = ? AND %s AND item.gamma_id = txs.gamma_id AND aj.id1 = txs.branch_id";

   private static final String COUNT_ARTIFACT_VIOLATIONS =
      "SELECT art.art_id, txs.branch_id, aj.id4 FROM osee_join_id4 aj, osee_artifact art, osee_txs txs WHERE aj.query_id = ? AND aj.id2 = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = aj.id1";

   private static final String DELETE_FROM_TXS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_txs txs WHERE EXISTS (select 1 from osee_join_transaction jt WHERE jt.query_id = ? AND jt.branch_id = txs.branch_id AND jt.gamma_id = txs.gamma_id AND jt.transaction_id = txs.transaction_id)";

   private static final String DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_tx_details txd WHERE EXISTS (select 1 from osee_join_transaction jt WHERE jt.query_id = ? AND jt.branch_id = txd.branch_id AND jt.transaction_id = txd.transaction_id AND not exists (select * from osee_txs txs where jt.branch_id = txs.branch_id and jt.transaction_id = txs.transaction_id))";

   private final List<Artifact> artifactsToPurge;
   private boolean success;
   private final boolean recurseChildrenBranches;

   private ArtifactEvent artifactEvent;

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge) throws OseeCoreException {
      this(artifactsToPurge, false);
   }

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge, boolean recurseChildrenBranches) throws OseeCoreException {
      super(ConnectionHandler.getJdbcClient(), "Purge Artifact", Activator.PLUGIN_ID);
      this.artifactsToPurge = new LinkedList<>(artifactsToPurge);
      this.success = false;
      this.recurseChildrenBranches = recurseChildrenBranches;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, JdbcConnection connection) throws OseeCoreException {
      if (artifactsToPurge == null || artifactsToPurge.isEmpty()) {
         return;
      }

      checkPurgeValid(connection);

      // now load the artifacts to be purged
      Set<Artifact> childreArtifactsToPurge = new HashSet<>();
      for (Artifact art : artifactsToPurge) {
         childreArtifactsToPurge.addAll(art.getDescendants(DeletionFlag.INCLUDE_DELETED));
      }
      artifactsToPurge.addAll(childreArtifactsToPurge);

      Id4JoinQuery artJoin2 = JoinUtility.createId4JoinQuery(getJdbcClient());
      try {
         for (Artifact art : artifactsToPurge) {
            artJoin2.add(art.getBranch(), art, TransactionId.SENTINEL, art.getBranch().getViewId());
         }
         artJoin2.store(connection);

         int queryId = artJoin2.getQueryId();

         TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery(getJdbcClient());

         insertSelectItems(txJoin, connection, "osee_relation_link",
            "(aj.id2 = item.a_art_id OR aj.id2 = item.b_art_id)", queryId);
         insertSelectItems(txJoin, connection, "osee_attribute", "aj.id2 = item.art_id", queryId);
         insertSelectItems(txJoin, connection, "osee_artifact", "aj.id2 = item.art_id", queryId);

         try {
            txJoin.store(connection);
            getJdbcClient().runPreparedUpdate(connection, DELETE_FROM_TXS_USING_JOIN_TRANSACTION, txJoin.getQueryId());
            getJdbcClient().runPreparedUpdate(connection, DELETE_FROM_TX_DETAILS_USING_JOIN_TRANSACTION,
               txJoin.getQueryId());
         } finally {
            txJoin.delete(connection);
         }

         BranchId branch = artifactsToPurge.iterator().next().getBranch();
         artifactEvent = new ArtifactEvent(branch);
         for (Artifact artifact : artifactsToPurge) {
            EventBasicGuidArtifact guidArt = new EventBasicGuidArtifact(EventModType.Purged, artifact);
            artifactEvent.addArtifact(guidArt);

            for (RelationLink rel : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
               DefaultBasicUuidRelation guidRelation =
                  new DefaultBasicUuidRelation(branch, rel.getRelationType().getId(), rel.getId(), rel.getGammaId(),
                     new DefaultBasicGuidArtifact(branch, rel.getArtifactA().getArtifactTypeId(), rel.getArtifactA()),
                     new DefaultBasicGuidArtifact(branch, rel.getArtifactB().getArtifactTypeId(), rel.getArtifactB()));
               artifactEvent.addRelation(new EventBasicGuidRelation(RelationEventType.Purged, rel.getAArtifactId(),
                  rel.getBArtifactId(), guidRelation));
               rel.markAsPurged();
            }
            for (Attribute<?> attr : artifact.internalGetAttributes()) {
               attr.markAsPurged();
            }
            ArtifactCache.deCache(artifact);
            RelationManager.deCache(artifact);
            artifact.internalSetDeleted();
         }
         success = true;
      } finally {
         artJoin2.delete(connection);
      }
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) throws OseeCoreException {
      if (success) {
         // Kick Local and Remote Events
         OseeEventManager.kickPersistEvent(PurgeArtifacts.class, artifactEvent);
      }
   }

   public void insertSelectItems(TransactionJoinQuery txJoin, JdbcConnection connection, String tableName, String artifactJoinSql, int queryId) throws OseeCoreException {
      String query = String.format(SELECT_ITEM_GAMMAS, ServiceUtil.useOracleHints() ? " /*+ ordered */ " : "",
         tableName, artifactJoinSql);
      JdbcStatement chStmt = getJdbcClient().getStatement(connection);
      try {
         chStmt.runPreparedQuery(query, queryId);
         while (chStmt.next()) {
            txJoin.add(chStmt.getLong("gamma_id"), chStmt.getLong("transaction_id"), chStmt.getLong("id1"));
         }
      } finally {
         chStmt.close();
      }
   }

   private void checkPurgeValid(JdbcConnection connection) {
      Id4JoinQuery artJoin = JoinUtility.createId4JoinQuery(getJdbcClient());
      for (Artifact art : artifactsToPurge) {
         for (IOseeBranch branch : BranchManager.getChildBranches(art.getBranch(), true)) {
            artJoin.add(branch, art, TransactionId.SENTINEL, branch.getViewId());
         }
      }
      if (!artJoin.isEmpty()) {
         try {
            artJoin.store(connection);
            JdbcStatement chStmt = getJdbcClient().getStatement(connection);
            try {
               chStmt.runPreparedQuery(COUNT_ARTIFACT_VIOLATIONS, artJoin.getQueryId());
               boolean failed = false;
               StringBuilder sb = new StringBuilder();
               while (chStmt.next()) {
                  int artId = chStmt.getInt("art_id");
                  long branchUuid = chStmt.getLong("branch_id");
                  if (recurseChildrenBranches) {
                     BranchId branch = BranchId.valueOf(branchUuid);
                     Artifact artifactFromId = ArtifactQuery.getArtifactFromId(artId, branch);
                     artifactsToPurge.add(artifactFromId);
                  } else {
                     failed = true;
                     sb.append("ArtifactId[");
                     sb.append(artId);
                     sb.append("] BranchId[");
                     sb.append(branchUuid);
                     sb.append("]\n");
                  }
               }
               if (failed) {
                  throw new OseeCoreException(
                     "Unable to purge because the following artifacts exist on child branches.\n%s", sb.toString());
               }
            } finally {
               chStmt.close();
            }
         } finally {
            artJoin.delete(connection);
         }
      }
   }

}