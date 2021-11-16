/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.orcs.db.internal.callable;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.RelationTypeToken;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.core.model.change.ChangeItemUtil;
import org.eclipse.osee.framework.core.model.change.ChangeVersion;
import org.eclipse.osee.framework.jdk.core.type.CompositeKeyHashMap;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConnection;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * This class is used to load the history of transactions for a given artifact
 *
 * @author Branden W. Phillips
 */
public class LoadArtifactHistory extends AbstractDatastoreTxCallable<List<ChangeItem>> {

   private final ArtifactId artifact;
   private final BranchId branch;
   private final OrcsTokenService tokenService;
   private final HashMap<Long, ApplicabilityToken> applicTokens;

   /**
    * SQL Query Summary<br/>
    * Recursively loads the branch ids, parent branch ids, baseline transaction ids<br/>
    * Selects all artifact transactions on the given branch<br/>
    * Selects artifact transactions on the ancestory branches before the current branch was created<br/>
    * Selects all attribute transactions on the given branch<br/>
    * Selects attribute transactions on the ancestory branches before the current branch was created<br/>
    * Selects all relation transactions for artifact a on the given branch<br/>
    * Selects relation transactions for artifact a on the ancestory branches before the current branch was created<br/>
    * Selects all relation transactions for artifact b on the given branch<br/>
    * Selects relation transactions for artifact b on the ancestory branches before the current branch was created<br/>
    */
   private static final String query =
      "WITH %s branches (branch_id, PARENT_BRANCH_ID, baseline_transaction_id) AS (\n" + //
         "SELECT child.branch_id, child.parent_branch_id, child.BASELINE_TRANSACTION_ID\n" + //
         "FROM osee_branch child\n" + //
         "WHERE child.branch_ID = ?\n" + //
         "UNION ALL\n" + //
         "SELECT parent.branch_id, parent.parent_branch_id, parent.baseline_transaction_id\n" + //
         "FROM osee_branch parent\n" + //
         "JOIN branches ON parent.branch_id = branches.parent_branch_id)\n" + //
         "SELECT transaction_id, b.parent_branch_id AS branch_id, 1 as table_type, art_type_id as item_type_id, art_id as item_id, 0 as item_first, 0 as item_second, 'na' as item_value, 'na' AS item_uri, item.gamma_id, mod_type, app_id\n" + //
         "FROM osee_artifact item, osee_txs txs , branches b\n" + //
         "WHERE txs.gamma_id = item.gamma_id AND item.ART_ID = ? AND txs.branch_id = b.parent_branch_id AND txs.TRANSACTION_ID < b.baseline_transaction_id\n" + //
         "UNION ALL\n" + //
         "SELECT transaction_id, b.branch_id AS branch_id, 1 as table_type, art_type_id as item_type_id, art_id as item_id, 0 as item_first, 0 as item_second, 'na' as item_value, 'na' AS item_uri, item.gamma_id, mod_type, app_id\n" + //
         "FROM osee_artifact item, osee_txs txs , branches b\n" + //
         "WHERE txs.gamma_id = item.gamma_id AND item.ART_ID = ? AND txs.BRANCH_ID = ? AND txs.branch_id = b.branch_id\n" + //
         "UNION ALL\n" + //
         "SELECT transaction_id, b.parent_branch_id AS branch_id, 2 as table_type, attr_type_id as item_type_id, attr_id as item_id, art_id as item_first, 0 as item_second, value as item_value, uri AS item_uri, item.gamma_id, mod_type, app_id\n" + //
         "FROM osee_attribute item, osee_txs txs, branches b\n" + //
         "WHERE txs.gamma_id = item.gamma_id AND item.ART_ID = ? AND txs.branch_id = b.parent_branch_id AND txs.TRANSACTION_ID < b.baseline_transaction_id\n" + //
         "UNION ALL \n" + //
         "SELECT transaction_id, b.branch_id AS branch_id, 2 as table_type, attr_type_id as item_type_id, attr_id as item_id, art_id as item_first, 0 as item_second, value as item_value, uri AS item_uri, item.gamma_id, mod_type, app_id\n" + //
         "FROM osee_attribute item, osee_txs txs, branches b\n" + //
         "WHERE txs.gamma_id = item.gamma_id AND item.ART_ID = ? AND txs.BRANCH_ID = ? AND txs.branch_id = b.branch_id\n" + //
         "UNION ALL\n" + //
         "SELECT transaction_id, b.parent_branch_id AS branch_id, 3 as table_type, rel_link_type_id as item_type_id, rel_link_id as item_id, a_art_id as item_first, b_art_id as item_second, rationale as item_value, 'na' AS item_uri, item.gamma_id, mod_type, app_id\n" + //
         "FROM osee_relation_link item, osee_txs txs , branches b\n" + //
         "WHERE txs.gamma_id = item.gamma_id AND item.a_art_id = ? AND txs.branch_id = b.parent_branch_id AND txs.TRANSACTION_ID < b.baseline_transaction_id\n" + //
         "UNION ALL\n" + //
         "SELECT transaction_id, b.branch_id AS branch_id, 3 as table_type, rel_link_type_id as item_type_id, rel_link_id as item_id, a_art_id as item_first, b_art_id as item_second, rationale as item_value, 'na' AS item_uri, item.gamma_id, mod_type, app_id\n" + //
         "FROM osee_relation_link item, osee_txs txs , branches b\n" + //
         "WHERE txs.gamma_id = item.gamma_id AND item.a_art_id = ? AND txs.BRANCH_ID = ? AND txs.branch_id = b.branch_id\n" + //
         "UNION ALL\n" + //
         "SELECT transaction_id, b.parent_branch_id AS branch_id, 3 as table_type, rel_link_type_id as item_type_id, rel_link_id as item_id, a_art_id as item_first, b_art_id as item_second, rationale as item_value, 'na' AS item_uri, item.gamma_id, mod_type, app_id\n" + //
         "FROM osee_relation_link item, osee_txs txs , branches b\n" + //
         "WHERE txs.gamma_id = item.gamma_id AND item.b_art_id = ? AND txs.branch_id = b.parent_branch_id AND txs.TRANSACTION_ID < b.baseline_transaction_id\n" + //
         "UNION ALL\n" + //
         "SELECT transaction_id, b.branch_id AS branch_id, 3 as table_type, rel_link_type_id as item_type_id, rel_link_id as item_id, a_art_id as item_first, b_art_id as item_second, rationale as item_value, 'na' AS item_uri, item.gamma_id, mod_type, app_id\n" + //
         "FROM osee_relation_link item, osee_txs txs , branches b\n" + //
         "WHERE txs.gamma_id = item.gamma_id AND item.b_art_id = ? AND txs.BRANCH_ID = ? AND txs.branch_id = b.branch_id\n" + //
         "ORDER BY TRANSACTION_ID, table_type, item_type_id, mod_type";

   public LoadArtifactHistory(Log logger, OrcsSession session, QueryFactory queryFactory, OrcsTokenService tokenService, JdbcClient jdbcClient, ArtifactId artifact, BranchId branch) {
      super(logger, session, jdbcClient);
      this.artifact = artifact;
      this.branch = branch;
      this.tokenService = tokenService;
      this.applicTokens = queryFactory.applicabilityQuery().getApplicabilityTokens(branch);
   }

   /**
    * This method handles the processing for the artifact history loading query. This relies on the transactions coming
    * in in order of history, starting with oldest. For each transaction, the changes should go Artifact, Attribute,
    * then Relation.
    */
   @Override
   protected List<ChangeItem> handleTxWork(JdbcConnection connection) {
      CompositeKeyHashMap<Long, Long, ChangeVersion> lastVersionMap = new CompositeKeyHashMap<>();
      List<ChangeItem> changeItems = new LinkedList<>();

      Consumer<JdbcStatement> consumer = stmt -> {
         TransactionId txId = TransactionId.valueOf(stmt.getLong("transaction_id"));
         BranchId branchId = BranchId.valueOf(stmt.getLong("branch_id"));
         TransactionToken txToken = TransactionToken.valueOf(txId, branchId);
         int tableType = stmt.getInt("table_type"); /** 1 for Artifact, 2 for Attribute, 3 for Relation */
         Long itemTypeId = stmt.getLong("item_type_id");
         Long itemId = stmt.getLong("item_id");
         /** 0 for Artifact Changes, ArtId for Attribute Changes, ArtAId for RelationChanges */
         Long itemFirst = stmt.getLong("item_first");
         /** 0 for Artifact Changes, 0 For Attribute Changes, ArtIdB for Relation Changes */
         Long itemSecond = stmt.getLong("item_second");
         String value = stmt.getString("item_value");
         String uri = stmt.getString("item_uri");
         GammaId gammaId = GammaId.valueOf(stmt.getLong("gamma_id"));
         ModificationType modType = ModificationType.valueOf(stmt.getInt("mod_type"));
         ApplicabilityId appId = ApplicabilityId.valueOf(stmt.getLong("app_id"));
         ApplicabilityToken applicToken = getApplicabilityToken(appId);

         switch (tableType) {
            case 1: {
               ArtifactId artifactId = ArtifactId.valueOf(itemId);
               ArtifactTypeId artTypeId = tokenService.getArtifactType(itemTypeId);
               ChangeItem artChange =
                  ChangeItemUtil.newArtifactChange(artifactId, artTypeId, gammaId, modType, applicToken, txToken);
               ChangeVersion currentVersion = artChange.getCurrentVersion();
               currentVersion.setTransactionToken(txToken);
               currentVersion.setUri(uri);

               ChangeVersion lastVersion = lastVersionMap.get(artifactId.getId(), branchId.getId());
               if (lastVersion != null) {
                  /**
                   * If a version of this artifact was found on this branch the baseline version is set, and if that
                   * artifact change is new, we set to modified since this means an attribute or relation was changed
                   */
                  artChange.setBaselineVersion(lastVersion);
                  if (modType.equals(ModificationType.NEW)) {
                     currentVersion.setModType(ModificationType.MODIFIED);
                  }
               }
               lastVersionMap.put(artifactId.getId(), branchId.getId(), currentVersion);
               changeItems.add(artChange);
               break;
            }
            case 2: {
               AttributeId attrId = AttributeId.valueOf(itemId);
               AttributeTypeId attrTypeId = tokenService.getAttributeType(itemTypeId);
               ArtifactId artifactId = ArtifactId.valueOf(itemFirst);
               ChangeItem attrChange = ChangeItemUtil.newAttributeChange(attrId, attrTypeId, artifactId, gammaId,
                  modType, value, applicToken, txToken);
               ChangeVersion currentVersion = attrChange.getCurrentVersion();
               currentVersion.setTransactionToken(txToken);
               currentVersion.setUri(uri);

               ChangeVersion lastVersion = lastVersionMap.get(itemId, branchId.getId());
               if (lastVersion != null) {
                  attrChange.setBaselineVersion(lastVersion);
               } else if (modType.isDeleted()) {
                  /**
                   * If there was no version of this specific attribute found on the branch, and it's a deleted change,
                   * we ignore it because that change is not relevant to the artifact anymore
                   */
                  break;
               } else {
                  /**
                   * If there was no version of this specific attribute found on the branch, and it's some other change
                   * type, we set to new. Oftentimes the transaction carries its' old modification type over, since this
                   * specific attribute is new to this branch, it must be a new change
                   */
                  currentVersion.setModType(ModificationType.NEW);
               }
               lastVersionMap.put(itemId, branchId.getId(), currentVersion);
               changeItems.add(attrChange);
               break;
            }
            case 3: {
               RelationId relId = RelationId.valueOf(itemId);
               RelationTypeToken relTypeToken = tokenService.getRelationType(itemTypeId);
               ArtifactId artifactIdA = ArtifactId.valueOf(itemFirst);
               ArtifactId artifactIdB = ArtifactId.valueOf(itemSecond);
               ChangeItem relationChange = ChangeItemUtil.newRelationChange(relId, relTypeToken, gammaId, modType,
                  artifactIdA, artifactIdB, value, applicToken, txToken);
               ChangeVersion currentVersion = relationChange.getCurrentVersion();
               currentVersion.setTransactionToken(txToken);

               ChangeVersion lastVersion = lastVersionMap.get(relId.getId(), branchId.getId());
               if (lastVersion == null && modType.isDeleted()) {
                  /**
                   * Much like for attribute changes, if there was no record of this specific attribute on the branch
                   * and the modification is a deleted type then it is no longer relevant to the artifact
                   */
                  break;
               } else if (lastVersion == null) {
                  /**
                   * If there is no previous record of this relation on the branch, it is considered new to the branch
                   */
                  currentVersion.setModType(ModificationType.NEW);
               }
               lastVersionMap.put(relId.getId(), branchId.getId(), currentVersion);
               changeItems.add(relationChange);
               break;
            }
         }
      };

      String query = String.format(LoadArtifactHistory.query, getJdbcClient().getDbType().getRecursiveWithSql());
      getJdbcClient().runQueryWithMaxFetchSize(consumer, query, branch, artifact, artifact, branch, artifact, artifact,
         branch, artifact, artifact, branch, artifact, artifact, branch);

      return changeItems;
   }

   private ApplicabilityToken getApplicabilityToken(ApplicabilityId appId) {
      ApplicabilityToken toReturn = applicTokens.get(appId.getId());
      if (toReturn != null) {
         return toReturn;
      }
      return ApplicabilityToken.BASE;
   }
}
