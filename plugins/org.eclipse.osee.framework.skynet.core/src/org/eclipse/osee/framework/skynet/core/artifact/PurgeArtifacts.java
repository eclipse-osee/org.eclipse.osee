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
import java.util.Set;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.DeletionFlag;
import org.eclipse.osee.framework.core.model.event.DefaultBasicGuidArtifact;
import org.eclipse.osee.framework.core.model.event.DefaultBasicIdRelation;
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
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Ryan D. Brooks
 */
public class PurgeArtifacts extends AbstractDbTxOperation {

   private static final String SELECT_ITEM_GAMMAS =
      "SELECT DISTINCT %s txs.gamma_id, txs.transaction_id, branch_id, aj.id4 FROM osee_join_id4 aj, %s item, osee_txs txs WHERE aj.query_id = ? AND %s AND item.gamma_id = txs.gamma_id AND aj.id1 = txs.branch_id";

   private static final String COUNT_ARTIFACT_VIOLATIONS =
      "SELECT art.art_id, txs.branch_id, aj.id4 FROM osee_join_id4 aj, osee_artifact art, osee_txs txs WHERE aj.query_id = ? AND aj.id2 = art.art_id AND art.gamma_id = txs.gamma_id AND txs.branch_id = aj.id1";

   private static final String DELETE_FROM_TXS_USING_JOIN_TRANSACTION =
      "DELETE FROM osee_txs txs WHERE EXISTS (select 1 from osee_join_id4 jt WHERE jt.query_id = ? AND jt.id3 = txs.branch_id AND jt.id1 = txs.gamma_id AND jt.id2 = txs.transaction_id)";

   private final Set<Artifact> artifactsToPurge;
   private final StringBuilder sb = new StringBuilder();
   private boolean success;
   private final boolean recurseChildrenBranches;

   private ArtifactEvent artifactEvent;

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge) {
      this(artifactsToPurge, false);
   }

   public PurgeArtifacts(Collection<? extends Artifact> artifactsToPurge, boolean recurseChildrenBranches) {
      super(ConnectionHandler.getJdbcClient(), "Purge Artifact", Activator.PLUGIN_ID);
      this.artifactsToPurge = new HashSet<>(artifactsToPurge);
      this.success = false;
      this.recurseChildrenBranches = recurseChildrenBranches;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, JdbcConnection connection) {
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

      try (Id4JoinQuery artJoin2 = JoinUtility.createId4JoinQuery(getJdbcClient(), connection)) {
         for (Artifact art : artifactsToPurge) {
            artJoin2.add(art.getBranch(), art, TransactionId.SENTINEL, art.getBranch().getViewId());
         }
         artJoin2.store();

         int queryId = artJoin2.getQueryId();

         try (Id4JoinQuery txJoin = JoinUtility.createId4JoinQuery(getJdbcClient(), connection)) {
            insertSelectItems(txJoin, connection, "osee_relation_link",
               "(aj.id2 = item.a_art_id OR aj.id2 = item.b_art_id)", queryId);
            insertSelectItems(txJoin, connection, "osee_attribute", "aj.id2 = item.art_id", queryId);
            insertSelectItems(txJoin, connection, "osee_artifact", "aj.id2 = item.art_id", queryId);

            txJoin.store();
            getJdbcClient().runPreparedUpdate(connection, DELETE_FROM_TXS_USING_JOIN_TRANSACTION, txJoin.getQueryId());
         }

         BranchId branch = artifactsToPurge.iterator().next().getBranch();
         artifactEvent = new ArtifactEvent(branch);
         for (Artifact artifact : artifactsToPurge) {
            EventBasicGuidArtifact guidArt = new EventBasicGuidArtifact(EventModType.Purged, artifact);
            artifactEvent.addArtifact(guidArt);

            for (RelationLink rel : artifact.getRelationsAll(DeletionFlag.EXCLUDE_DELETED)) {
               ArtifactToken artifactA = rel.getArtifactA();
               ArtifactToken artifactB = rel.getArtifactB();
               DefaultBasicIdRelation guidRelation =
                  new DefaultBasicIdRelation(branch, rel.getRelationType().getId(), rel.getId(), rel.getGammaId(),
                     new DefaultBasicGuidArtifact(branch, artifactA.getArtifactTypeId(), artifactA),
                     new DefaultBasicGuidArtifact(branch, artifactB.getArtifactTypeId(), artifactB));
               artifactEvent.addRelation(
                  new EventBasicGuidRelation(RelationEventType.Purged, artifactA, artifactB, guidRelation));
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
      }
   }

   @Override
   protected void handleTxFinally(IProgressMonitor monitor) {
      if (success) {
         // Kick Local and Remote Events
         OseeEventManager.kickPersistEvent(PurgeArtifacts.class, artifactEvent);
      }
   }

   public void insertSelectItems(Id4JoinQuery txJoin, JdbcConnection connection, String tableName, String artifactJoinSql, int queryId) {
      String query = String.format(SELECT_ITEM_GAMMAS, ServiceUtil.useOracleHints() ? " /*+ ordered */ " : "",
         tableName, artifactJoinSql);
      try (JdbcStatement chStmt = getJdbcClient().getStatement(connection)) {
         chStmt.runPreparedQuery(query, queryId);
         while (chStmt.next()) {
            txJoin.add(chStmt.getLong("gamma_id"), chStmt.getLong("transaction_id"), chStmt.getLong("branch_id"));
         }
      }
   }

   private void checkPurgeValid(JdbcConnection connection) {
      try (Id4JoinQuery artJoin = JoinUtility.createId4JoinQuery(getJdbcClient(), connection)) {
         for (Artifact art : artifactsToPurge) {
            for (IOseeBranch branch : BranchManager.getChildBranches(art.getBranch(), true)) {
               artJoin.add(branch, art, TransactionId.SENTINEL, branch.getViewId());
            }
         }
         if (!artJoin.isEmpty()) {
            artJoin.store();
            getJdbcClient().runQuery(connection, this::getArtifactViloation, COUNT_ARTIFACT_VIOLATIONS,
               artJoin.getQueryId());

            if (sb.length() > 0) {
               throw new OseeCoreException("Can't purge because the following artifacts exist on child branches.\n%s",
                  sb);
            }
         }
      }
   }

   private void getArtifactViloation(JdbcStatement stmt) {
      ArtifactId artId = ArtifactId.valueOf(stmt.getLong("art_id"));
      Long branchId = stmt.getLong("branch_id");
      if (recurseChildrenBranches) {
         BranchId branch = BranchId.valueOf(branchId);
         Artifact artifactFromId = ArtifactQuery.getArtifactFromId(artId, branch);
         artifactsToPurge.add(artifactFromId);
      } else {
         sb.append("ArtifactId[");
         sb.append(artId);
         sb.append("] BranchId[");
         sb.append(branchId);
         sb.append("]\n");
      }
   }
}