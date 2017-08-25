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

import static org.eclipse.osee.framework.core.enums.DeletionFlag.INCLUDE_DELETED;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.internal.Activator;
import org.eclipse.osee.framework.skynet.core.internal.ServiceUtil;
import org.eclipse.osee.framework.skynet.core.utility.AbstractDbTxOperation;
import org.eclipse.osee.framework.skynet.core.utility.Id4JoinQuery;
import org.eclipse.osee.framework.skynet.core.utility.JoinUtility;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;

/**
 * @author Theron Virgin
 */
public class UpdateMergeBranch extends AbstractDbTxOperation {

   private static final String TX_CURRENT_SETTINGS = "CASE" + //
      " WHEN txs1.mod_type = " + ModificationType.DELETED.getIdString() + " THEN " + TxChange.DELETED + //
      " WHEN txs1.mod_type = " + ModificationType.ARTIFACT_DELETED.getIdString() + " THEN " + TxChange.ARTIFACT_DELETED + //
      " ELSE " + TxChange.CURRENT + //
      " END";

   private static final String UPDATE_ARTIFACTS =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id, app_id) SELECT ?, txs1.gamma_id, txs1.mod_type, " + TX_CURRENT_SETTINGS + ", ?, txs1.app_id FROM osee_attribute attr1, osee_txs txs1 WHERE attr1.art_id = ? AND txs1.gamma_id = attr1.gamma_id AND txs1.branch_id = ? AND txs1.tx_current <> ? AND NOT EXISTS (SELECT 'x' FROM osee_txs txs2, osee_attribute attr2 WHERE txs2.branch_id = ? AND txs2.transaction_id = ? AND txs2.gamma_id = attr2.gamma_id AND attr2.attr_id = attr1.attr_id)";

   private static final String PURGE_ATTRIBUTE_FROM_MERGE_BRANCH =
      "DELETE from osee_txs txs WHERE EXISTS (SELECT 'x' FROM osee_attribute attr WHERE txs.gamma_id = attr.gamma_id AND txs.branch_id = ? AND attr.art_id = ?)";
   private static final String PURGE_RELATION_FROM_MERGE_BRANCH =
      "DELETE from osee_txs txs WHERE EXISTS (SELECT 'x' FROM osee_relation_link rel WHERE txs.gamma_id = rel.gamma_id AND txs.branch_id = ? AND (rel.a_art_id = ? or rel.b_art_id = ?))";
   private static final String PURGE_ARTIFACT_FROM_MERGE_BRANCH =
      "DELETE from osee_txs txs WHERE EXISTS (SELECT 'x' FROM osee_artifact art WHERE txs.gamma_id = art.gamma_id AND txs.branch_id = ? AND art.art_id = ?)";

   private static final boolean DEBUG =
      "TRUE".equalsIgnoreCase(Platform.getDebugOption("org.eclipse.osee.framework.skynet.core/debug/Merge"));

   private final BranchId mergeBranch;
   private final ArrayList<ArtifactId> expectedArtIds;
   private final BranchId destBranch;
   private final BranchId sourceBranch;

   public UpdateMergeBranch(JdbcClient jdbcClient, BranchId mergeBranch, ArrayList<ArtifactId> expectedArtIds, BranchId destBranch, BranchId sourceBranch) {
      super(jdbcClient, "Update Merge Branch", Activator.PLUGIN_ID);
      this.destBranch = destBranch;
      this.expectedArtIds = expectedArtIds;
      this.mergeBranch = mergeBranch;
      this.sourceBranch = sourceBranch;
   }

   @Override
   protected void doTxWork(IProgressMonitor monitor, JdbcConnection connection) throws OseeCoreException {
      Collection<ArtifactId> allMergeBranchArtifacts = getAllMergeArtifacts(mergeBranch);
      long time = System.currentTimeMillis();
      Collection<ArtifactId> allMergeBranchArtifactsCopy = new HashSet<>(allMergeBranchArtifacts);
      Collection<Artifact> goodMergeBranchArtifacts =
         ArtifactQuery.getArtifactListFromBranch(mergeBranch, INCLUDE_DELETED);
      int count = 0;
      //Delete any damaged artifacts (from a source revert) on the merge branch
      for (Artifact artifact : goodMergeBranchArtifacts) {
         allMergeBranchArtifactsCopy.remove(artifact);
      }
      if (!allMergeBranchArtifactsCopy.isEmpty()) {
         for (ArtifactId artifact : allMergeBranchArtifactsCopy) {
            purgeArtifactFromBranch(connection, mergeBranch, artifact);
            count++;
         }
      }
      if (DEBUG) {
         System.out.println(
            String.format("          Deleting %d Damaged Artifacts took %s", count, Lib.getElapseString(time)));
         time = System.currentTimeMillis();
         count = 0;
      }

      //Delete any artifacts that shouldn't be on the merge branch but are
      for (ArtifactId artid : expectedArtIds) {
         allMergeBranchArtifacts.remove(artid);
      }
      if (!allMergeBranchArtifacts.isEmpty()) {
         for (ArtifactId artifact : allMergeBranchArtifacts) {
            count++;
            purgeArtifactFromBranch(connection, mergeBranch, artifact);
         }
      }
      if (DEBUG) {
         System.out.println(
            String.format("          Deleting %d unused Artifacts took %s", count, Lib.getElapseString(time)));
         time = System.currentTimeMillis();
         count = 0;
      }
      int numberAttrUpdated = 0;
      //Copy over any missing attributes
      TransactionId baselineTransaction = BranchManager.getBaseTransaction(mergeBranch);
      for (Artifact artifact : goodMergeBranchArtifacts) {
         numberAttrUpdated += getJdbcClient().runPreparedUpdate(connection, UPDATE_ARTIFACTS, baselineTransaction,
            mergeBranch, artifact.getArtId(), sourceBranch, TxChange.NOT_CURRENT, mergeBranch, baselineTransaction);
      }
      if (DEBUG) {
         System.out.println(String.format("          Adding %d Attributes to Existing Artifacts took %s",
            numberAttrUpdated, Lib.getElapseString(time)));
         time = System.currentTimeMillis();
      }

      //Add any artifacts that should be on the merge branch but aren't
      for (Artifact artifact : goodMergeBranchArtifacts) {
         expectedArtIds.remove(artifact);
      }
      if (!expectedArtIds.isEmpty()) {
         addArtifactsToBranch(connection, sourceBranch, destBranch, mergeBranch, expectedArtIds);
      }

      if (DEBUG) {
         System.out.println(String.format("          Adding %d new Artifacts took %s", expectedArtIds.size(),
            Lib.getElapseString(time)));
         time = System.currentTimeMillis();
      }
   }

   private final static String INSERT_ATTRIBUTE_GAMMAS =
      "INSERT INTO OSEE_TXS (transaction_id, gamma_id, mod_type, tx_current, branch_id, app_id) SELECT ?, atr1.gamma_id, txs1.mod_type, ?, ?, txs1.app_id FROM osee_attribute atr1, osee_txs txs1, osee_join_id4 ald1 WHERE txs1.branch_id = ? AND txs1.tx_current in (1,2) AND txs1.gamma_id = atr1.gamma_id AND atr1.art_id = ald1.id2 and ald1.query_id = ?";
   private final static String INSERT_ARTIFACT_GAMMAS =
      "INSERT INTO OSEE_TXS (transaction_id, gamma_id, mod_type, tx_current, branch_id, app_id) SELECT ?, arv1.gamma_id, txs1.mod_type, ?, ?, txs1.app_id FROM osee_artifact arv1, osee_txs txs1, osee_join_id4 ald1 WHERE txs1.branch_id = ? AND txs1.tx_current in (1,2) AND txs1.gamma_id = arv1.gamma_id AND arv1.art_id = ald1.id2 and ald1.query_id = ?";

   private void addArtifactsToBranch(JdbcConnection connection, BranchId sourceBranch, BranchId destBranch, BranchId mergeBranch, Collection<ArtifactId> artIds) throws OseeCoreException {
      if (artIds == null || artIds.isEmpty()) {
         throw new IllegalArgumentException("Artifact IDs can not be null or empty");
      }

      try (Id4JoinQuery joinQuery = JoinUtility.createId4JoinQuery(getJdbcClient(), connection)) {
         for (ArtifactId artId : artIds) {
            joinQuery.add(sourceBranch, artId, TransactionId.SENTINEL, sourceBranch.getViewId());
         }
         joinQuery.store();
         TransactionId startTx = BranchManager.getBaseTransaction(mergeBranch);
         insertGammas(connection, INSERT_ATTRIBUTE_GAMMAS, startTx, joinQuery.getQueryId(), sourceBranch, mergeBranch);
         insertGammas(connection, INSERT_ARTIFACT_GAMMAS, startTx, joinQuery.getQueryId(), sourceBranch, mergeBranch);
      } catch (OseeCoreException ex) {
         throw new OseeCoreException("Source Branch %s Artifact Ids: %s", sourceBranch.getId(),
            Collections.toString(",", artIds));
      }
   }

   private void insertGammas(JdbcConnection connection, String sql, TransactionId baseTx, int queryId, BranchId sourceBranch, BranchId mergeBranch) throws OseeCoreException {
      getJdbcClient().runPreparedUpdate(connection, sql, baseTx, TxChange.CURRENT, mergeBranch, sourceBranch, queryId);
   }

   private Collection<ArtifactId> getAllMergeArtifacts(BranchId branch) throws OseeCoreException {
      Collection<ArtifactId> artSet = new HashSet<>();

      getJdbcClient().runQuery(stmt -> artSet.add(ArtifactId.valueOf(stmt.getLong("art_id"))),
         ServiceUtil.getSql(OseeSql.MERGE_GET_ARTIFACTS_FOR_BRANCH), branch);

      getJdbcClient().runQuery(stmt -> artSet.add(ArtifactId.valueOf(stmt.getLong("art_id"))),
         ServiceUtil.getSql(OseeSql.MERGE_GET_ATTRIBUTES_FOR_BRANCH), branch);

      getJdbcClient().runQuery(stmt -> {
         artSet.add(ArtifactId.valueOf(stmt.getLong("a_art_id")));
         artSet.add(ArtifactId.valueOf(stmt.getLong("b_art_id")));
      }, ServiceUtil.getSql(OseeSql.MERGE_GET_RELATIONS_FOR_BRANCH), branch);

      return artSet;
   }

   /**
    * Removes an artifact, it's attributes and any relations that have become invalid from the removal of this artifact
    * from the database. It also removes all history associated with this artifact (i.e. all transactions and gamma ids
    * will also be removed from the database only for the branch it is on).
    */
   private void purgeArtifactFromBranch(JdbcConnection connection, BranchId branch, ArtifactId artId) throws OseeCoreException {
      //Remove from Baseline
      getJdbcClient().runPreparedUpdate(connection, PURGE_ATTRIBUTE_FROM_MERGE_BRANCH, branch, artId);
      getJdbcClient().runPreparedUpdate(connection, PURGE_RELATION_FROM_MERGE_BRANCH, branch, artId, artId);
      getJdbcClient().runPreparedUpdate(connection, PURGE_ARTIFACT_FROM_MERGE_BRANCH, branch, artId);
   }
}