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
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.TransactionRecord;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ArtifactJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsChangeSet;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.IdentityManager;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.transaction.TransactionWriter.SqlOrderEnum;

/**
 * @author Roberto E. Escobar
 */
public class TxSqlBuilderImpl implements OrcsVisitor, TxSqlBuilder {

   private final IOseeDatabaseService dbService;
   private final IdentityManager idManager;

   private int txId;
   private List<DaoToSql> binaryStores;
   private HashCollection<SqlOrderEnum, Object[]> dataItemInserts;
   private Map<SqlOrderEnum, ArtifactJoinQuery> txNotCurrentsJoin;

   public TxSqlBuilderImpl(IOseeDatabaseService dbService, IdentityManager idManager) {
      super();
      this.dbService = dbService;
      this.idManager = idManager;
      clear();
   }

   @Override
   public Set<Entry<SqlOrderEnum, ArtifactJoinQuery>> getTxNotCurrents() {
      return txNotCurrentsJoin != null ? txNotCurrentsJoin.entrySet() : Collections.<Entry<SqlOrderEnum, ArtifactJoinQuery>> emptySet();
   }

   @Override
   public List<Object[]> getInsertData(SqlOrderEnum key) {
      List<Object[]> toReturn = null;
      if (dataItemInserts != null) {
         toReturn = (List<Object[]>) dataItemInserts.getValues(key);
      }
      return toReturn != null ? toReturn : Collections.<Object[]> emptyList();
   }

   @Override
   public List<DaoToSql> getBinaryStores() {
      return binaryStores != null ? binaryStores : Collections.<DaoToSql> emptyList();
   }

   @Override
   public void clear() {
      txId = RelationalConstants.TRANSACTION_SENTINEL;
      dataItemInserts = null;
      txNotCurrentsJoin = null;
      binaryStores = null;
   }

   @Override
   public void accept(TransactionRecord tx, OrcsChangeSet changeSet) throws OseeCoreException {
      txId = tx.getId();
      binaryStores = new ArrayList<DaoToSql>();
      dataItemInserts = new HashCollection<SqlOrderEnum, Object[]>();
      txNotCurrentsJoin = new HashMap<SqlOrderEnum, ArtifactJoinQuery>();

      addRow(SqlOrderEnum.TXS_DETAIL, txId, tx.getComment(), tx.getTimeStamp(), tx.getAuthor(), tx.getBranchId(),
         tx.getTxType().getId());
      changeSet.accept(this);
   }

   @Override
   public void visit(ArtifactData data) throws OseeCoreException {
      if (!isNewAndDeleted(data)) {
         if (!data.getVersion().isInStorage() || data.hasTypeUuidChange() || data.hasModTypeChange() || data.getModType() == ModificationType.REPLACED_WITH_VERSION) {
            boolean isRowAllowed = isGammaCreationAllowed(data);
            updateTxValues(data);
            if (isRowAllowed) {
               updateGamma(data);
               int localTypeId = getLocalTypeId(data.getTypeUuid());
               addRow(SqlOrderEnum.ARTIFACTS, data.getLocalId(), localTypeId, data.getVersion().getGammaId(),
                  data.getGuid());
            }
            addTxs(SqlOrderEnum.ARTIFACTS, data);
         }
      }
   }

   @Override
   public void visit(AttributeData data) throws OseeCoreException {
      if (!isNewAndDeleted(data)) {
         boolean isRowAllowed = isGammaCreationAllowed(data);
         updateTxValues(data);
         if (isRowAllowed) {
            updateGamma(data);

            DataProxy dataProxy = data.getDataProxy();
            DaoToSql daoToSql = new DaoToSql(data.getVersion().getGammaId(), dataProxy, isGammaCreationAllowed(data));
            addBinaryStore(daoToSql);

            if (RelationalConstants.DEFAULT_ITEM_ID == data.getLocalId()) {
               int localId = idManager.getNextAttributeId();
               data.setLocalId(localId);
            }
            int localTypeId = getLocalTypeId(data.getTypeUuid());
            addRow(SqlOrderEnum.ATTRIBUTES, data.getLocalId(), localTypeId, data.getVersion().getGammaId(),
               data.getArtifactId(), daoToSql.getValue(), daoToSql.getUri());
         }
         addTxs(SqlOrderEnum.ATTRIBUTES, data);
      }
   }

   @Override
   public void updateAfterBinaryStorePersist() throws OseeCoreException {
      List<Object[]> insertData = getInsertData(SqlOrderEnum.ATTRIBUTES);
      for (int index = 0; index < binaryStores.size() && index < insertData.size(); index++) {
         DaoToSql dao = binaryStores.get(index);
         Object[] rowData = insertData.get(index);
         int end = rowData.length;
         rowData[end - 2] = dao.getValue();
         rowData[end - 1] = dao.getUri();
      }
   }

   @Override
   public void visit(RelationData data) throws OseeCoreException {
      if (!isNewAndDeleted(data)) {
         boolean isRowAllowed = isGammaCreationAllowed(data);
         updateTxValues(data);
         if (isRowAllowed) {
            updateGamma(data);
            if (RelationalConstants.DEFAULT_ITEM_ID == data.getLocalId()) {
               int localId = idManager.getNextRelationId();
               data.setLocalId(localId);
            }
            int localTypeId = getLocalTypeId(data.getTypeUuid());
            addRow(SqlOrderEnum.RELATIONS, data.getLocalId(), localTypeId, data.getVersion().getGammaId(),
               data.getArtIdA(), data.getArtIdB(), data.getRationale());
         }
         addTxs(SqlOrderEnum.RELATIONS, data);
      }
   }

   private void addTxs(SqlOrderEnum key, OrcsData orcsData) {
      VersionData data = orcsData.getVersion();
      ModificationType modType = orcsData.getModType();

      addRow(SqlOrderEnum.TXS, data.getTransactionId(), data.getGammaId(), modType.getValue(),
         TxChange.getCurrent(modType).getValue(), data.getBranchId());

      if (key.hasTxNotCurrentQuery()) {
         ArtifactJoinQuery join = txNotCurrentsJoin.get(key);
         if (join == null) {
            join = createJoin();
            txNotCurrentsJoin.put(key, join);
         }
         join.add(orcsData.getLocalId(), data.getBranchId(), RelationalConstants.TRANSACTION_SENTINEL);
      }
   }

   private boolean isNewAndDeleted(OrcsData data) {
      return !data.getVersion().isInStorage() && data.getModType().isDeleted();
   }

   private void updateTxValues(OrcsData orcsData) {
      orcsData.setModType(computeModType(orcsData.getModType()));
      orcsData.getVersion().setTransactionId(txId);
   }

   private void updateGamma(OrcsData data) throws OseeCoreException {
      VersionData version = data.getVersion();
      long gammaId = version.getGammaId();
      if (RelationalConstants.GAMMA_SENTINEL == gammaId || isGammaCreationAllowed(data)) {
         long newGamma = idManager.getNextGammaId();
         version.setGammaId(newGamma);
      }
   }

   private ModificationType computeModType(ModificationType original) {
      ModificationType toReturn = original;
      if (ModificationType.REPLACED_WITH_VERSION == toReturn) {
         toReturn = ModificationType.MODIFIED;
      }
      return toReturn;
   }

   protected ArtifactJoinQuery createJoin() {
      return JoinUtility.createArtifactJoinQuery(dbService);
   }

   protected boolean isGammaCreationAllowed(OrcsData data) {
      return !data.getModType().isExistingVersionUsed();
   }

   private void addRow(SqlOrderEnum sqlKey, Object... data) {
      dataItemInserts.put(sqlKey, data);
   }

   private void addBinaryStore(DaoToSql binaryTx) {
      binaryStores.add(binaryTx);
   }

   private int getLocalTypeId(long typeUuidId) throws OseeCoreException {
      return idManager.getLocalId(typeUuidId);
   }

}
