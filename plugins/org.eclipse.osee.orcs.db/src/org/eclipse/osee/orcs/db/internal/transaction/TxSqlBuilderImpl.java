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
package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.internal.transaction.TransactionWriter.SqlOrderEnum;

/**
 * @author Roberto E. Escobar
 */
public class TxSqlBuilderImpl implements OrcsVisitor, TxSqlBuilder {

   private final SqlJoinFactory sqlJoinFactory;
   private final IdentityManager idManager;
   private final JdbcClient jdbcClient;

   private TransactionId txId;
   private List<DataProxy<?>> binaryStores;
   private HashCollection<SqlOrderEnum, Object[]> dataItemInserts;
   private Map<SqlOrderEnum, IdJoinQuery> txNotCurrentsJoin;

   public TxSqlBuilderImpl(SqlJoinFactory sqlJoinFactory, IdentityManager idManager, JdbcClient jdbcClient) {
      this.sqlJoinFactory = sqlJoinFactory;
      this.idManager = idManager;
      this.jdbcClient = jdbcClient;
      clear();
   }

   @Override
   public Set<Entry<SqlOrderEnum, IdJoinQuery>> getTxNotCurrents() {
      return txNotCurrentsJoin != null ? txNotCurrentsJoin.entrySet() : Collections.<Entry<SqlOrderEnum, IdJoinQuery>> emptySet();
   }

   @Override
   public List<Object[]> getInsertData(SqlOrderEnum key) {
      List<Object[]> toReturn = null;
      if (dataItemInserts != null) {
         toReturn = dataItemInserts.getValues(key);
      }
      return toReturn != null ? toReturn : Collections.<Object[]> emptyList();
   }

   @Override
   public List<DataProxy<?>> getBinaryStores() {
      return binaryStores != null ? binaryStores : Collections.emptyList();
   }

   @Override
   public void clear() {
      txId = TransactionId.SENTINEL;
      dataItemInserts = null;
      txNotCurrentsJoin = null;
      binaryStores = null;
   }

   @Override
   public void accept(TransactionReadable tx, OrcsChangeSet changeSet) {
      txId = tx;
      binaryStores = new ArrayList<>();
      dataItemInserts = new HashCollection<>();
      txNotCurrentsJoin = new HashMap<>();

      addRow(SqlOrderEnum.TXS_DETAIL, txId, tx.getComment(), tx.getDate(), tx.getAuthor(), tx.getBranch(),
         tx.getTxType().getId(), OseeCodeVersion.getVersionId());
      changeSet.accept(this);
   }

   @Override
   public void visit(ArtifactData data) {
      boolean isOtherChange =
         !data.getVersion().isInStorage() || data.hasTypeUuidChange() || data.hasModTypeChange() || data.isExistingVersionUsed();
      boolean isApplicOnly = data.getDirtyState().isApplicOnly();

      if (!isNewAndDeleted(data)) {
         if (isOtherChange || isApplicOnly) {
            boolean reuseGamma = reuseGamma(data);
            updateTxValues(data);
            if (!isApplicOnly && !reuseGamma) {
               updateGamma(data);
               addRow(SqlOrderEnum.ARTIFACTS, data.getLocalId(), data.getType(), data.getVersion().getGammaId(),
                  data.getGuid());
            }
            addTxs(SqlOrderEnum.ARTIFACTS, data);
         }
      }
   }

   @Override
   public <T> void visit(AttributeData<T> data) {
      if (!isNewAndDeleted(data)) {
         boolean createNewGamma = !reuseGamma(data);
         updateTxValues(data);
         if (createNewGamma && !data.getDirtyState().isApplicOnly()) {
            updateGamma(data);

            DataProxy<?> dataProxy = data.getDataProxy();
            dataProxy.setGamma(data.getVersion().getGammaId(), createNewGamma);
            binaryStores.add(dataProxy);

            if (RelationalConstants.DEFAULT_ITEM_ID == data.getLocalId()) {
               int id = idManager.getNextAttributeId();
               data.setLocalId(id);
            }
            addRow(SqlOrderEnum.ATTRIBUTES, data.getLocalId(), data.getType(), data.getVersion().getGammaId(),
               data.getArtifactId(), dataProxy.getStorageString(), dataProxy.getUri());
         }
         addTxs(SqlOrderEnum.ATTRIBUTES, data);
      }
   }

   @Override
   public void visit(TupleData data) {
      updateTxValues(data);

      if (data.getElement3() == null) {
         addRow(SqlOrderEnum.TUPLES2, data.getTupleType(), data.getElement1(), data.getElement2(),
            data.getVersion().getGammaId());
         addTxs(SqlOrderEnum.TUPLES2, data);
      } else if (data.getElement4() == null) {
         addRow(SqlOrderEnum.TUPLES3, data.getTupleType(), data.getElement1(), data.getElement2(), data.getElement3(),
            data.getVersion().getGammaId());
         addTxs(SqlOrderEnum.TUPLES3, data);
      } else {
         addRow(SqlOrderEnum.TUPLES4, data.getTupleType(), data.getElement1(), data.getElement2(), data.getElement3(),
            data.getElement4(), data.getVersion().getGammaId());
         addTxs(SqlOrderEnum.TUPLES4, data);
      }
   }

   @Override
   public void deleteTuple(BranchId branch, TableEnum tupleTable, GammaId gammaId) {
      SqlOrderEnum sqlEnum;
      if (tupleTable.equals(TableEnum.TUPLE2)) {
         sqlEnum = SqlOrderEnum.TUPLES2;
      } else if (tupleTable.equals(TableEnum.TUPLE3)) {
         sqlEnum = SqlOrderEnum.TUPLES3;
      } else if (tupleTable.equals(TableEnum.TUPLE4)) {
         sqlEnum = SqlOrderEnum.TUPLES4;
      } else {
         throw new OseeStateException("Unexpected table enum [%s]", tupleTable);
      }
      deleteTuple(branch, sqlEnum, gammaId);
   }

   private void deleteTuple(BranchId branch, SqlOrderEnum tupleTable, GammaId gammaId) {
      ApplicabilityId applicability = jdbcClient.fetch(ApplicabilityId.SENTINEL,
         "SELECT app_id from osee_txs where branch_id = ? and gamma_id = ?", branch, gammaId);
      if (applicability.isInvalid()) {
         throw new ItemDoesNotExist("Tuple not found on branch [%s] with gammaId [%s]", branch, gammaId);
      }

      addRow(SqlOrderEnum.TXS, txId, gammaId, ModificationType.DELETED, TxCurrent.DELETED, branch, applicability);

      IdJoinQuery join = txNotCurrentsJoin.get(tupleTable);
      if (join == null) {
         join = createJoin();
         txNotCurrentsJoin.put(tupleTable, join);
      }
      join.add(gammaId);
   }

   @Override
   public void updateAfterBinaryStorePersist() {
      List<Object[]> insertData = getInsertData(SqlOrderEnum.ATTRIBUTES);
      for (int index = 0; index < binaryStores.size() && index < insertData.size(); index++) {
         DataProxy<?> proxy = binaryStores.get(index);
         Object[] rowData = insertData.get(index);
         int end = rowData.length;
         rowData[end - 2] = proxy.getStorageString();
         rowData[end - 1] = proxy.getUri();
      }
   }

   @Override
   public void visit(RelationData data) {
      if (!isNewAndDeleted(data)) {
         boolean reuseGamma = reuseGamma(data);
         updateTxValues(data);
         if (!reuseGamma && !data.getDirtyState().isApplicOnly()) {
            updateGamma(data);
            if (RelationalConstants.DEFAULT_ITEM_ID == data.getLocalId()) {
               int id = idManager.getNextRelationId();
               data.setLocalId(id);
            }
            addRow(SqlOrderEnum.RELATIONS, data.getLocalId(), data.getType(), data.getVersion().getGammaId(),
               data.getArtIdA(), data.getArtIdB(), data.getRationale());
         }
         addTxs(SqlOrderEnum.RELATIONS, data);
      }
   }

   private void addTxs(SqlOrderEnum key, OrcsData orcsData) {
      VersionData data = orcsData.getVersion();
      ModificationType modType = orcsData.getModType();

      addRow(SqlOrderEnum.TXS, data.getTransactionId(), data.getGammaId(), modType, TxCurrent.getCurrent(modType),
         data.getBranch(), orcsData.getApplicabilityId());

      if (key.hasTxNotCurrentQuery()) {
         IdJoinQuery join = txNotCurrentsJoin.get(key);
         if (join == null) {
            join = createJoin();
            txNotCurrentsJoin.put(key, join);
         }
         join.add(orcsData.getLocalId());
      }
   }

   private boolean isNewAndDeleted(OrcsData data) {
      return !data.getVersion().isInStorage() && data.getModType().isDeleted();
   }

   private void updateTxValues(OrcsData orcsData) {
      orcsData.setModType(computeModType(orcsData.getModType()));
      orcsData.getVersion().setTransactionId(txId);
   }

   private void updateGamma(OrcsData data) {
      VersionData version = data.getVersion();
      version.setGammaId(idManager.getNextGammaId());
   }

   private ModificationType computeModType(ModificationType original) {
      ModificationType toReturn = original;
      if (ModificationType.REPLACED_WITH_VERSION == toReturn) {
         toReturn = ModificationType.MODIFIED;
      }
      return toReturn;
   }

   protected IdJoinQuery createJoin() {
      return sqlJoinFactory.createIdJoinQuery();
   }

   protected boolean reuseGamma(OrcsData data) {
      return data.getModType().isExistingVersionUsed() || data.isExistingVersionUsed();
   }

   private void addRow(SqlOrderEnum sqlKey, Object... data) {
      dataItemInserts.put(sqlKey, data);
   }
}