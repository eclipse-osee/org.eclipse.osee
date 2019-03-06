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
package org.eclipse.osee.framework.skynet.core.revision;

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ConflictStatus;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.exception.BranchMergeException;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.OseeArgumentException;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.attribute.AttributeTypeManager;
import org.eclipse.osee.framework.skynet.core.conflict.ArtifactConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflict;
import org.eclipse.osee.framework.skynet.core.conflict.AttributeConflictBuilder;
import org.eclipse.osee.framework.skynet.core.conflict.Conflict;
import org.eclipse.osee.framework.skynet.core.conflict.ConflictBuilder;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.skynet.core.utility.Id4JoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.IdJoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;

/**
 * @author Theron Virgin
 * @author Jeff C. Phillips
 */
public class ConflictManagerInternal {

   private static final String MULTIPLICITY_DETECTION = "with " + //
      "attr_c as (select attr_src.art_id as src_art_id, " + //
      "attr_src.attr_id as src_attr_id, " + //
      "attr_src.attr_type_id as src_attr_tid, " + //
      "txs_src.gamma_id as src_txs_gid " + //
      "from osee_txs txs_src, osee_attribute attr_src where " + //
      "branch_id = ? and " + //
      "tx_current = 1 and " + //
      "transaction_id > ? and " + //
      "txs_src.gamma_id = attr_src.gamma_id), " + //
      "sj_c as (select attr_c.src_art_id as sj_art_id, " + //
      "attr_c.src_attr_id as sj_attr_id, " + //
      "attr_c.src_attr_tid as sj_attr_tid " + //
      "from attr_c, osee_join_id jid where " + //
      "attr_c.src_attr_tid = jid.id and " + //
      "jid.query_id = ?) " + //
      "select attr_d.art_id as art_id, " + //
      "sj_c.sj_attr_id as source_attr_id, " + //
      "attr_d.attr_id as dest_attr_id " + //
      "from sj_c, osee_attribute attr_d, osee_txs txs_d where " + //
      "txs_d.branch_id = ? and " + //
      "txs_d.tx_current = 1 and " + //
      "txs_d.gamma_id = attr_d.gamma_id and " + //
      "attr_d.attr_type_id = sj_c.sj_attr_tid and " + //
      "attr_d.art_id = sj_c.sj_art_id and " + //
      "attr_d.attr_id <> sj_c.sj_attr_id";

   private static final String CONFLICT_CLEANUP =
      "DELETE FROM osee_conflict t1 WHERE merge_branch_id = ? AND NOT EXISTS (SELECT 'X' FROM osee_join_id4 WHERE query_id = ? AND t1.conflict_id = id2 AND (t1.conflict_type = id3 or id3 is NULL))";

   private static final String GET_DESTINATION_BRANCHES =
      "SELECT dest_branch_id FROM osee_merge WHERE source_branch_id = ?";

   private static final String GET_MERGE_DATA =
      "SELECT commit_transaction_id, merge_branch_id FROM osee_merge WHERE source_branch_id = ? AND dest_branch_id = ? and commit_transaction_id > 0";

   private static final String GET_COMMIT_TRANSACTION_COMMENT =
      "SELECT transaction_id FROM osee_tx_details WHERE osee_comment = ? AND branch_id = ?";

   public static List<Conflict> getConflictsPerBranch(TransactionToken commitTransaction, IProgressMonitor monitor) {
      monitor.beginTask(String.format("Loading Merge Manager for Transaction %d", commitTransaction.getId()), 100);
      monitor.subTask("Finding Database stored conflicts");
      ArrayList<Conflict> conflicts = new ArrayList<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.CONFLICT_GET_HISTORICAL_ATTRIBUTES),
            commitTransaction.getId());
         while (chStmt.next()) {
            IOseeBranch sourceBranch = BranchManager.getBranchToken(chStmt.getLong("source_branch_id"));
            if (BranchManager.isArchived(sourceBranch)) {
               sourceBranch = null;
            }
            AttributeConflict attributeConflict =
               new AttributeConflict(GammaId.valueOf(chStmt.getLong("source_gamma_id")),
                  GammaId.valueOf(chStmt.getLong("dest_gamma_id")), ArtifactId.valueOf(chStmt.getLong("art_id")), null,
                  commitTransaction, chStmt.getString("source_value"), AttributeId.valueOf(chStmt.getInt("attr_id")),
                  AttributeTypeManager.getTypeById(chStmt.getLong("attr_type_id")),
                  BranchId.valueOf(chStmt.getLong("merge_branch_id")), sourceBranch,
                  BranchManager.getBranchToken(chStmt.getLong("dest_branch_id")));
            attributeConflict.setStatus(ConflictStatus.valueOf(chStmt.getInt("status")));
            conflicts.add(attributeConflict);
         }
      } finally {
         chStmt.close();
         monitor.done();
      }
      return conflicts;
   }

   public static List<Conflict> getConflictsPerBranch(IOseeBranch sourceBranch, IOseeBranch destinationBranch, TransactionToken baselineTransaction, IProgressMonitor monitor) {
      List<ConflictBuilder> conflictBuilders = new ArrayList<>();
      List<Conflict> conflicts = new ArrayList<>();
      Set<ArtifactId> artIdSet = new HashSet<>();
      Set<ArtifactId> artIdSetDontShow = new HashSet<>();
      Set<ArtifactId> artIdSetDontAdd = new HashSet<>();

      if (sourceBranch == null || destinationBranch == null) {
         throw new OseeArgumentException("Source Branch = %s Destination Branch = %s", sourceBranch, destinationBranch);
      }

      // Check to see if the branch has already been committed, then use the
      // transaction version
      TransactionToken commitTransactionId = getCommitTransaction(sourceBranch, destinationBranch);
      if (commitTransactionId.isValid()) {
         return getConflictsPerBranch(commitTransactionId, monitor);
      }
      monitor.beginTask(
         String.format("Loading Merge Manager for Branch %s into Branch %s", sourceBranch, destinationBranch), 100);
      monitor.subTask("Finding Database stored conflicts");

      TransactionToken commonTransaction = findCommonTransaction(sourceBranch, destinationBranch);

      // check for multiplicity conflicts
      Collection<AttributeTypeId> singleMultiplicityTypes = AttributeTypeManager.getSingleMultiplicityTypes();
      loadMultiplicityConflicts(singleMultiplicityTypes, sourceBranch, destinationBranch, conflictBuilders, artIdSet);

      loadArtifactVersionConflicts(ServiceUtil.getSql(OseeSql.CONFLICT_GET_ARTIFACTS_DEST), sourceBranch,
         destinationBranch, baselineTransaction, conflictBuilders, artIdSet, artIdSetDontShow, artIdSetDontAdd, monitor,
         commonTransaction);
      loadArtifactVersionConflicts(ServiceUtil.getSql(OseeSql.CONFLICT_GET_ARTIFACTS_SRC), sourceBranch,
         destinationBranch, baselineTransaction, conflictBuilders, artIdSet, artIdSetDontShow, artIdSetDontAdd, monitor,
         commonTransaction);
      loadAttributeConflictsNew(sourceBranch, destinationBranch, baselineTransaction, conflictBuilders, artIdSet,
         monitor, commonTransaction);

      artIdSet.removeAll(artIdSetDontAdd);
      if (artIdSet.isEmpty()) {
         return conflicts;
      }

      monitor.subTask("Creating and/or maintaining the Merge Branch");
      IOseeBranch mergeBranch =
         BranchManager.getOrCreateMergeBranch(sourceBranch, destinationBranch, new ArrayList<>(artIdSet));

      if (mergeBranch == null) {
         throw new BranchMergeException("Could not create the Merge Branch.");
      }
      monitor.worked(15);

      preloadConflictArtifacts(sourceBranch, destinationBranch, mergeBranch, artIdSet, monitor);

      // Don't create the conflicts for attributes on an artifact that is
      // deleted etc.
      for (ConflictBuilder conflictBuilder : conflictBuilders) {
         Conflict conflict = getConflict(conflictBuilder, mergeBranch, artIdSetDontShow);
         if (conflict != null) {
            conflicts.add(conflict);
         }
      }
      cleanUpConflictDB(conflicts, mergeBranch, monitor);
      return conflicts;
   }

   private static Conflict getConflict(ConflictBuilder conflictBuilder, IOseeBranch mergeBranch, Set<ArtifactId> artIdSetDontShow) {
      Conflict conflict = conflictBuilder.getConflict(mergeBranch, artIdSetDontShow);
      if (conflict != null) {
         conflict.computeStatus();
      }
      return conflict;
   }

   private static Collection<Artifact> preloadConflictArtifacts(BranchId sourceBranch, BranchId destBranch, IOseeBranch mergeBranch, Collection<ArtifactId> artIdSet, IProgressMonitor monitor) {
      monitor.subTask("Preloading Artifacts Associated with the Conflicts");

      Collection<Artifact> artifacts = ArtifactQuery.getArtifactListFrom(artIdSet, sourceBranch, INCLUDE_DELETED);
      artifacts.addAll(ArtifactQuery.getArtifactListFrom(artIdSet, destBranch, INCLUDE_DELETED));
      artifacts.addAll(ArtifactQuery.getArtifactListFrom(artIdSet, mergeBranch, INCLUDE_DELETED));

      monitor.worked(25);
      return artifacts;
   }

   private static void loadMultiplicityConflicts(Collection<AttributeTypeId> types, BranchId source, BranchId dest, List<ConflictBuilder> conflictBuilders, Set<ArtifactId> artIdSet) {
      JdbcClient jdbcClient = ConnectionHandler.getJdbcClient();
      List<Object[]> batchParams = new LinkedList<>();
      try (IdJoinQuery joinQuery = JoinUtility.createIdJoinQuery()) {
         joinQuery.addAndStore(types);

         Consumer<JdbcStatement> consumer = stmt -> {
            ArtifactId artId = ArtifactId.valueOf(stmt.getLong("art_id"));
            Long sAttrId = stmt.getLong("source_attr_id");
            Long dAttrId = stmt.getLong("dest_attr_id");
            artIdSet.add(artId);
            batchParams.add(new Object[] {dAttrId, sAttrId, artId});
         };
         jdbcClient.runQuery(consumer, MULTIPLICITY_DETECTION, source, BranchManager.getBaseTransaction(source),
            joinQuery.getQueryId(), dest);
      }
      if (!batchParams.isEmpty()) {
         String updateSql = "update osee_attribute set attr_id = ? where attr_id = ? and art_id = ?";
         jdbcClient.runBatchUpdate(updateSql, batchParams);
         // update cached source artifacts
         for (Object[] params : batchParams) {
            ArtifactQuery.reloadArtifactFromId((ArtifactId) params[2], source);
         }
      }
   }

   private static void loadArtifactVersionConflicts(String sql, IOseeBranch sourceBranch, IOseeBranch destinationBranch, TransactionToken baselineTransaction, Collection<ConflictBuilder> conflictBuilders, Set<ArtifactId> artIdSet, Set<ArtifactId> artIdSetDontShow, Set<ArtifactId> artIdSetDontAdd, IProgressMonitor monitor, TransactionToken commonTransaction) {
      boolean hadEntries = false;

      monitor.subTask("Finding Artifact Version Conflicts");

      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(sql, sourceBranch, BranchManager.getBaseTransaction(sourceBranch), destinationBranch,
            commonTransaction.getBranch(), commonTransaction, commonTransaction);

         ArtifactConflictBuilder artifactConflictBuilder;
         ArtifactId artId = ArtifactId.SENTINEL;
         while (chStmt.next()) {
            hadEntries = true;
            ArtifactId nextArtId = ArtifactId.valueOf(chStmt.getLong("art_id"));
            GammaId sourceGamma = GammaId.valueOf(chStmt.getLong("source_gamma"));
            GammaId destGamma = GammaId.valueOf(chStmt.getLong("dest_gamma"));
            ModificationType sourceModType = ModificationType.valueOf(chStmt.getInt("source_mod_type"));
            ModificationType destModType = ModificationType.valueOf(chStmt.getInt("dest_mod_type"));
            long artTypeId = chStmt.getLong("art_type_id");

            if (artId.notEqual(nextArtId)) {
               artId = nextArtId;

               if (destModType == ModificationType.DELETED && sourceModType == ModificationType.MODIFIED || //
                  destModType == ModificationType.MODIFIED && sourceModType == ModificationType.DELETED) {

                  artifactConflictBuilder = new ArtifactConflictBuilder(sourceGamma, destGamma, artId,
                     baselineTransaction, sourceBranch, destinationBranch, sourceModType, destModType, artTypeId);

                  conflictBuilders.add(artifactConflictBuilder);
                  artIdSet.add(artId);
               } else if (destModType == ModificationType.DELETED && sourceModType == ModificationType.DELETED) {
                  artIdSetDontShow.add(artId);
                  artIdSetDontAdd.add(artId);
               }
            }
         }
      } finally {
         chStmt.close();
      }

      if (hadEntries) {
         monitor.worked(20);
         artIdSetDontShow.addAll(artIdSet);
      }
   }

   private static void loadAttributeConflictsNew(IOseeBranch sourceBranch, IOseeBranch destinationBranch, TransactionToken baselineTransaction, Collection<ConflictBuilder> conflictBuilders, Set<ArtifactId> artIdSet, IProgressMonitor monitor, TransactionToken commonTransaction) {
      monitor.subTask("Finding the Attribute Conflicts");

      AttributeConflictBuilder attributeConflictBuilder;
      try (JdbcStatement chStmt = ConnectionHandler.getStatement()) {
         chStmt.runPreparedQuery(ServiceUtil.getSql(OseeSql.CONFLICT_GET_ATTRIBUTES), sourceBranch,
            BranchManager.getBaseTransaction(sourceBranch), destinationBranch, commonTransaction.getBranch(),
            commonTransaction);
         AttributeId attrId = AttributeId.SENTINEL;

         while (chStmt.next()) {
            AttributeId nextAttrId = AttributeId.valueOf(chStmt.getInt("attr_id"));
            ArtifactId artId = ArtifactId.valueOf(chStmt.getLong("art_id"));
            GammaId sourceGamma = GammaId.valueOf(chStmt.getLong("source_gamma"));
            GammaId destGamma = GammaId.valueOf(chStmt.getLong("dest_gamma"));
            AttributeTypeId attrTypeId = AttributeTypeManager.getTypeById(chStmt.getLong("attr_type_id"));
            String sourceValue = chStmt.getString("source_value") != null ? chStmt.getString(
               "source_value") : chStmt.getString("dest_value");

            if (attrId.notEqual(nextAttrId) && isAttributeConflictValid(destGamma, sourceBranch)) {
               attrId = nextAttrId;
               attributeConflictBuilder = new AttributeConflictBuilder(sourceGamma, destGamma, artId,
                  baselineTransaction, sourceBranch, destinationBranch, sourceValue, attrId, attrTypeId);

               conflictBuilders.add(attributeConflictBuilder);
               artIdSet.add(artId);
            }
         }
      }
      monitor.worked(30);
   }

   /**
    * Checks source branch hierarchy to see if the conflict gamma exists. If it does, its not a real conflict because
    * the source branch has already seen this change.
    *
    * @return Returns True if the AttributeConflict candidate is really a conflict.
    */
   private static boolean isAttributeConflictValid(GammaId destinationGammaId, BranchId sourceBranch) {
      boolean isValidConflict = true;
      // We just need the largest value at first so the complete source branch
      // will be searched
      TransactionId parentTransactionNumber = TransactionId.valueOf(Integer.MAX_VALUE);

      for (BranchId branch : BranchManager.getAncestors(sourceBranch)) {
         if (!BranchManager.isParentSystemRoot(branch)) {
            isValidConflict &= isAttributeConflictValidOnBranch(destinationGammaId, branch, parentTransactionNumber);
            TransactionId sourceTx = BranchManager.getSourceTransaction(branch);
            if (sourceTx != null) {
               parentTransactionNumber = sourceTx;
            }
         }

         if (!isValidConflict) {
            break;
         }
      }
      return isValidConflict;
   }

   /**
    * @return Returns True if the destination gamma does not exist on a branch else false if it does.
    */
   private static boolean isAttributeConflictValidOnBranch(GammaId destinationGammaId, BranchId branch, TransactionId endTransaction) {
      String sql =
         "SELECT count(1) FROM osee_txs txs WHERE txs.gamma_id = ? AND txs.branch_id = ? AND txs.transaction_id <= ?";
      return ConnectionHandler.getJdbcClient().fetch(0, sql, destinationGammaId, branch, endTransaction) == 0;
   }

   private static void cleanUpConflictDB(Collection<Conflict> conflicts, BranchId branch, IProgressMonitor monitor) {
      monitor.subTask("Cleaning up old conflict data");
      if (conflicts != null && conflicts.size() != 0 && branch.isValid()) {
         try (Id4JoinQuery joinQuery = JoinUtility.createId4JoinQuery()) {
            for (Conflict conflict : conflicts) {
               joinQuery.add(branch, conflict.getObjectId(),
                  TransactionId.valueOf(conflict.getConflictType().getValue()));
            }
            joinQuery.store();
            ConnectionHandler.runPreparedUpdate(CONFLICT_CLEANUP, branch, joinQuery.getQueryId());
         }
      }
      monitor.worked(10);
   }

   public static Collection<IOseeBranch> getDestinationBranchesMerged(BranchId sourceBranch) {
      List<IOseeBranch> destinationBranches = new LinkedList<>();
      JdbcStatement chStmt = ConnectionHandler.getStatement();
      try {
         chStmt.runPreparedQuery(GET_DESTINATION_BRANCHES, sourceBranch);
         while (chStmt.next()) {
            destinationBranches.add(BranchManager.getBranchToken(chStmt.getLong("dest_branch_id")));
         }
      } finally {
         chStmt.close();
      }
      if (destinationBranches.size() > 1) {
         Collections.sort(destinationBranches);
      }
      return destinationBranches;
   }

   private static TransactionToken getCommitTransaction(IOseeBranch sourceBranch, BranchId destBranch) {
      TransactionToken transactionId = TransactionToken.SENTINEL;
      try (JdbcStatement stmt = ConnectionHandler.getStatement()) {
         stmt.runPreparedQuery(GET_MERGE_DATA, sourceBranch, destBranch);
         if (stmt.next()) {
            transactionId = TransactionToken.valueOf(stmt.getLong("commit_transaction_id"), destBranch);
         }
         if (transactionId.isInvalid()) {
            stmt.runPreparedQuery(GET_COMMIT_TRANSACTION_COMMENT, BranchManager.COMMIT_COMMENT + sourceBranch.getName(),
               destBranch);
            if (stmt.next()) {
               transactionId = TransactionToken.valueOf(stmt.getLong("transaction_id"), destBranch);
            }
         }
      }
      return transactionId;
   }

   /**
    * The purpose of this function is find the transaction (Branch Create) that holds the last common values for two
    * branches that share a common history. If two branches share the same history than the point at which they diverged
    * should provide the reference for detecting conflicts based on the gamma at that point.
    */
   public static TransactionToken findCommonTransaction(BranchId sourceBranch, BranchId destBranch) {
      Collection<BranchId> sourceBranches = BranchManager.getAncestors(sourceBranch);
      Collection<BranchId> destBranches = BranchManager.getAncestors(destBranch);
      BranchId commonAncestor = null;
      for (BranchId branch : sourceBranches) {
         if (destBranches.contains(branch)) {
            commonAncestor = branch;
            break;
         }
      }
      if (commonAncestor == null) {
         throw new OseeCoreException("Cannot find a common ancestor for Branch %s and Branch %s", sourceBranch,
            destBranch);
      }
      TransactionToken sourceTransaction = null;
      TransactionToken destTransaction = null;
      if (commonAncestor.equals(destBranch)) {
         destTransaction = TransactionManager.getHeadTransaction(commonAncestor);
      } else {
         for (BranchId branch : destBranches) {
            if (BranchManager.isParent(branch, commonAncestor)) {
               destTransaction = BranchManager.getBaseTransaction(branch);
               break;
            }
         }
      }
      for (BranchId branch : sourceBranches) {
         if (BranchManager.isParent(branch, commonAncestor)) {
            sourceTransaction = BranchManager.getBaseTransaction(branch);
            break;
         }
      }

      TransactionToken toReturn = null;
      if (sourceTransaction == null && destTransaction != null) {
         toReturn = destTransaction;
      } else if (sourceTransaction != null && destTransaction == null) {
         toReturn = sourceTransaction;
      } else if (sourceTransaction != null && destTransaction != null) {
         toReturn = sourceTransaction.getId() <= destTransaction.getId() ? sourceTransaction : destTransaction;
      }
      return toReturn;
   }

}
