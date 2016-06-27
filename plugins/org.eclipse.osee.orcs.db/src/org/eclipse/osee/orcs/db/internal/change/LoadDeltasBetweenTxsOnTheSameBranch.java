/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.change;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.model.change.ChangeItem;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcConstants;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.data.TransactionReadableDelta;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.change.ChangeItemLoader.ChangeItemFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.TransactionJoinQuery;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 * @author Ryan Schmitt
 * @author Jeff C. Phillips
 */
public class LoadDeltasBetweenTxsOnTheSameBranch extends AbstractDatastoreCallable<List<ChangeItem>> {

   private static final String SELECT_CHANGES_BETWEEN_TRANSACTIONS =
      "select gamma_id, mod_type, app_id from osee_txs where branch_id = ? and transaction_id > ? and transaction_id <= ?";

   private final HashMap<Long, Pair<ModificationType, ApplicabilityId>> changeByGammaId = new HashMap<>();

   private final SqlJoinFactory joinFactory;
   private final TransactionReadableDelta txDelta;
   private final ChangeItemLoader changeItemLoader;

   public LoadDeltasBetweenTxsOnTheSameBranch(Log logger, OrcsSession session, JdbcClient jdbcClient, SqlJoinFactory joinFactory, TransactionReadableDelta txDelta) {
      super(logger, session, jdbcClient);
      this.joinFactory = joinFactory;
      this.txDelta = txDelta;
      this.changeItemLoader = new ChangeItemLoader(jdbcClient, changeByGammaId);
   }

   private Long getBranchId() {
      return getEndTx().getBranchId();
   }

   private TransactionReadable getEndTx() {
      return txDelta.getEndTx();
   }

   private TransactionReadable getStartTx() {
      return txDelta.getStartTx();
   }

   @Override
   public List<ChangeItem> call() throws Exception {
      List<ChangeItem> changeData = new LinkedList<>();

      Conditions.checkExpressionFailOnTrue(!txDelta.areOnTheSameBranch(),
         "Unable to compute deltas between transactions on different branches [%s]", txDelta);

      TransactionJoinQuery txJoin = joinFactory.createTransactionJoinQuery();

      loadChangesAtEndTx(txJoin);

      int txJoinId = txJoin.getQueryId();
      try {
         loadByItemId(changeData, txJoinId, changeItemLoader.createArtifactChangeItemFactory());
         loadByItemId(changeData, txJoinId, changeItemLoader.createAttributeChangeItemFactory());
         loadByItemId(changeData, txJoinId, changeItemLoader.createRelationChangeItemFactory());
      } finally {
         try {
            txJoin.delete();
         } finally {
            changeByGammaId.clear();
         }
      }
      return changeData;
   }

   private void loadChangesAtEndTx(TransactionJoinQuery txJoin) throws OseeCoreException {
      JdbcStatement chStmt = getJdbcClient().getStatement();
      try {
         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, SELECT_CHANGES_BETWEEN_TRANSACTIONS, getBranchId(),
            getStartTx().getGuid(), getEndTx().getGuid());
         while (chStmt.next()) {
            checkForCancelled();
            Long gammaId = chStmt.getLong("gamma_id");
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));
            ApplicabilityId appId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));

            txJoin.add(gammaId, -1);
            changeByGammaId.put(gammaId, new Pair<ModificationType, ApplicabilityId>(modType, appId));
         }
         txJoin.store();
      } finally {
         chStmt.close();
      }
   }

   private void loadByItemId(Collection<ChangeItem> changeData, int txJoinId, ChangeItemFactory factory) throws OseeCoreException {

      IdJoinQuery idJoin = joinFactory.createIdJoinQuery();

      HashMap<Integer, ChangeItem> changesByItemId = new HashMap<>();
      changeItemLoader.loadItemIdsBasedOnGammas(factory, txJoinId, changesByItemId, idJoin);

      idJoin.store();

      loadCurrentData(factory.getItemTableName(), factory.getItemIdColumnName(), idJoin.getQueryId(), changesByItemId,
         getStartTx());

      idJoin.delete();

      changeData.addAll(changesByItemId.values());
   }

   private void loadCurrentData(String tableName, String columnName, int queryId, HashMap<Integer, ChangeItem> changesByItemId, TransactionReadable transactionLimit) throws OseeCoreException {
      JdbcStatement chStmt = getJdbcClient().getStatement();
      try {
         String query = "select txs.gamma_id, txs.mod_type, txs.app_id, item." + columnName + " from osee_join_id idj, " //
            + tableName + " item, osee_txs txs where idj.query_id = ? and idj.id = item." + columnName + //
            " and item.gamma_id = txs.gamma_id and txs.branch_id = ? and txs.transaction_id <= ?";

         chStmt.runPreparedQuery(JdbcConstants.JDBC__MAX_FETCH_SIZE, query, queryId, transactionLimit.getBranchId(),
            transactionLimit.getGuid());

         while (chStmt.next()) {
            checkForCancelled();

            Integer itemId = chStmt.getInt(columnName);
            Long gammaId = chStmt.getLong("gamma_id");
            ApplicabilityId appId = ApplicabilityId.valueOf(chStmt.getLong("app_id"));
            ModificationType modType = ModificationType.getMod(chStmt.getInt("mod_type"));

            ChangeItem change = changesByItemId.get(itemId);
            change.getDestinationVersion().setModType(modType);
            change.getDestinationVersion().setGammaId(gammaId);
            change.getDestinationVersion().setApplicabilityId(appId);
            change.getBaselineVersion().copy(change.getDestinationVersion());
         }
      } finally {
         chStmt.close();
      }
   }
}