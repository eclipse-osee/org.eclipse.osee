/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.db.internal.transaction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.AttributeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.data.OseeCodeVersion;
import org.eclipse.osee.framework.core.data.RelationId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.Tuple2Type;
import org.eclipse.osee.framework.core.data.Tuple3Type;
import org.eclipse.osee.framework.core.data.TupleTypeId;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxCurrent;
import org.eclipse.osee.framework.core.sql.OseeSql;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.ItemDoesNotExist;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.SqlTable;
import org.eclipse.osee.orcs.OseeDb;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.BranchCategoryData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.TupleData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.sql.join.Id4JoinQuery;
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

   private TransactionToken txId;
   private List<DataProxy<?>> binaryStores;
   private HashCollection<SqlOrderEnum, Object[]> dataItemInserts;
   private Map<SqlOrderEnum, IdJoinQuery> txNotCurrentsJoin;
   private Map<SqlOrderEnum, Id4JoinQuery> txNotCurrentsJoin4;

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
   public Set<Entry<SqlOrderEnum, Id4JoinQuery>> getTxNotCurrents4() {
      return txNotCurrentsJoin4 != null ? txNotCurrentsJoin4.entrySet() : Collections.<Entry<SqlOrderEnum, Id4JoinQuery>> emptySet();
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
      txId = TransactionToken.SENTINEL;
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
      txNotCurrentsJoin4 = new HashMap<>();

      addRow(SqlOrderEnum.TXS_DETAIL, tx.getBranch(), txId, tx.getAuthor(), tx.getDate(), tx.getComment(),
         tx.getTxType(), -1, OseeCodeVersion.getVersionId());
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
               addRow(SqlOrderEnum.ARTIFACTS, data.getId(), data.getVersion().getGammaId(), data.getType(),
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

            if (data.isInvalid()) {
               AttributeId id = idManager.getNextAttributeId();
               data.setLocalId(id);
            }
            addRow(SqlOrderEnum.ATTRIBUTES, data.getId(), data.getVersion().getGammaId(), data.getArtifactId(),
               data.getType(), dataProxy.getStorageString(), dataProxy.getUri());
         }
         addTxs(SqlOrderEnum.ATTRIBUTES, data);
      }
   }

   @Override
   public void visit(TupleData data) {
      updateTxValues(data);
      TupleTypeId tupleType = data.getTupleType();

      if (tupleType instanceof Tuple2Type) {
         if (!data.isExistingVersionUsed()) {
            addRow(SqlOrderEnum.TUPLES2, tupleType, data.getElement1(), data.getElement2(),
               data.getVersion().getGammaId());
         }
         addTxs(SqlOrderEnum.TUPLES2, data);
      } else if (tupleType instanceof Tuple3Type) {
         if (!data.isExistingVersionUsed()) {
            addRow(SqlOrderEnum.TUPLES3, tupleType, data.getElement1(), data.getElement2(), data.getElement3(),
               data.getVersion().getGammaId());
         }
         addTxs(SqlOrderEnum.TUPLES3, data);
      } else {
         if (!data.isExistingVersionUsed()) {
            addRow(SqlOrderEnum.TUPLES4, tupleType, data.getElement1(), data.getElement2(), data.getElement3(),
               data.getElement4(), data.getVersion().getGammaId());
         }
         addTxs(SqlOrderEnum.TUPLES4, data);
      }
   }

   @Override
   public void deleteTuple(BranchId branch, SqlTable tupleTable, GammaId gammaId) {
      SqlOrderEnum sqlEnum;
      if (tupleTable.equals(OseeDb.TUPLE2)) {
         sqlEnum = SqlOrderEnum.TUPLES2;
      } else if (tupleTable.equals(OseeDb.TUPLE3)) {
         sqlEnum = SqlOrderEnum.TUPLES3;
      } else if (tupleTable.equals(OseeDb.TUPLE4)) {
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

      addRow(SqlOrderEnum.TXS, branch, gammaId, txId, TxCurrent.DELETED, ModificationType.DELETED, applicability);

      IdJoinQuery join = txNotCurrentsJoin.get(tupleTable);
      if (join == null) {
         join = createJoin();
         txNotCurrentsJoin.put(tupleTable, join);
      }
      join.add(gammaId);
   }

   @Override
   public void deleteBranchCategory(BranchId branch, GammaId gammaId) {
      ApplicabilityId applicability = jdbcClient.fetch(ApplicabilityId.SENTINEL,
         "SELECT app_id from osee_txs where branch_id = ? and gamma_id = ?", branch, gammaId);

      addRow(SqlOrderEnum.TXS, txId, gammaId, ModificationType.DELETED, TxCurrent.DELETED, branch, applicability);

      IdJoinQuery join = txNotCurrentsJoin.get(SqlOrderEnum.BRANCH_CATEGORY);
      if (join == null) {
         join = createJoin();
         txNotCurrentsJoin.put(SqlOrderEnum.BRANCH_CATEGORY, join);
      }
      join.add(gammaId);

   }

   @Override
   public void visit(BranchCategoryData data) {
      updateTxValues(data);

      if (!data.isExistingVersionUsed()) {
         addRow(SqlOrderEnum.BRANCH_CATEGORY, data.getBranchId(), data.getCategory(), data.getVersion().getGammaId());
      }
      addTxs(SqlOrderEnum.BRANCH_CATEGORY, data);

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

         if (!reuseGamma && data.getType().isNewRelationTable()) {
            if (data.getVersion().getGammaId().isValid() && !data.isDirty()) {
               reuseGamma = true;
            } else {
               GammaId gId = GammaId.valueOf(
                  jdbcClient.fetch(0L, OseeSql.SELECT_RELATION_GAMMA_RT_A_ART_B_ART_ORDER_REL_ART.getSql(),
                     data.getType().getId(), data.getArtifactIdA().getId(), data.getArtifactIdB().getId(),
                     data.getRelOrder(), data.getRelationArtifact().getId()));
               if (gId.isValid()) {
                  data.getVersion().setGammaId(gId);
                  reuseGamma = true;
               }
            }
         }
         updateTxValues(data);
         if (!reuseGamma && !data.getDirtyState().isApplicOnly()) {
            updateGamma(data);
            if (data.isInvalid()) {
               RelationId id = idManager.getNextRelationId();
               data.setLocalId(id);
            }
            if (data.getType().isNewRelationTable()) {
               addRow(SqlOrderEnum.RELATIONS2, data.getType(), data.getArtifactIdA(), data.getArtifactIdB(),
                  data.getRelationArtifact(), data.getRelOrder(), data.getVersion().getGammaId());
            } else {
               addRow(SqlOrderEnum.RELATIONS, data.getType(), data.getArtifactIdA(), data.getArtifactIdB(),
                  data.getVersion().getGammaId(), data.getId(), data.getRationale());
            }
         }
         if (data.getType().isNewRelationTable()) {
            addTxs(SqlOrderEnum.RELATIONS2, data);
         } else {
            addTxs(SqlOrderEnum.RELATIONS, data);
         }
      }
   }

   private void addTxs(SqlOrderEnum key, OrcsData orcsData) {
      VersionData data = orcsData.getVersion();
      ModificationType modType = orcsData.getModType();

      addRow(SqlOrderEnum.TXS, txId.getBranch(), data.getGammaId(), txId, TxCurrent.getCurrent(modType), modType,
         orcsData.getApplicabilityId());
      //TODO: could filter based on relation/
      if (key.hasTxNotCurrentQuery()) {
         if (key.toString().equals("RELATIONS2")) {
            Id4JoinQuery join4 = txNotCurrentsJoin4.get(key);
            if (join4 == null) {
               join4 = sqlJoinFactory.createId4JoinQuery();
               txNotCurrentsJoin4.put(key, join4);
            }
            join4.add(((RelationData) orcsData).getType(), ((RelationData) orcsData).getArtifactIdA(),
               ((RelationData) orcsData).getArtifactIdB(), ((RelationData) orcsData).getRelOrder());
         } else {

            IdJoinQuery join = txNotCurrentsJoin.get(key);
            if (join == null) {

               join = createJoin();
               txNotCurrentsJoin.put(key, join);

            }
            if (key.toString().startsWith("TUPLE") || key.toString().equals("BRANCH_CATEGORY")) {
               join.add(orcsData.getVersion().getGammaId());
            } else {
               join.add(orcsData.getLocalId());
            }
         }
      }
   };

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

   protected Id4JoinQuery create4Join() {
      return sqlJoinFactory.createId4JoinQuery();
   }

   protected boolean reuseGamma(OrcsData data) {
      return data.getModType().isExistingVersionUsed() || data.isExistingVersionUsed();
   }

   private void addRow(SqlOrderEnum sqlKey, Object... data) {
      dataItemInserts.put(sqlKey, data);
   }
}