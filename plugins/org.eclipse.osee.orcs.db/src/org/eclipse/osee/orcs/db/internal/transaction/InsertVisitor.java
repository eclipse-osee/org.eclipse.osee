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

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.enums.ModificationType;
import org.eclipse.osee.framework.core.enums.TxChange;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.VersionData;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.loader.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public class InsertVisitor implements OrcsVisitor {

   private static final String INSERT_ARTIFACT =
      "INSERT INTO osee_artifact (art_id, art_type_id, gamma_id, guid, human_readable_id) VALUES (?,?,?,?,?)";

   private static final String INSERT_ATTRIBUTE =
      "INSERT INTO osee_attribute (attr_id, attr_type_id, gamma_id, art_id, value, uri) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String INSERT_RELATION_TABLE =
      "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, gamma_id, a_art_id, b_art_id, rationale) VALUES (?,?,?,?,?,?)";

   private static final String INSERT_INTO_TRANSACTION_TABLE =
      "INSERT INTO osee_txs (transaction_id, gamma_id, mod_type, tx_current, branch_id) VALUES (?, ?, ?, ?, ?)";

   private final IdFactory idFactory;
   private final IdentityService identityService;

   private final HashCollection<String, Object[]> dataItemInserts;
   private final HashCollection<OseeSql, Object[]> txNotCurrents;
   private final List<DaoToSql> binaryStores;

   private final Map<Integer, String> dataInsertOrder;
   private final int txId;

   public InsertVisitor(int txId, IdFactory idFactory, IdentityService identityService, HashCollection<String, Object[]> dataItemInserts, HashCollection<OseeSql, Object[]> txNotCurrents, List<DaoToSql> binaryStores, Map<Integer, String> dataInsertOrder) {
      super();
      this.txId = txId;
      this.idFactory = idFactory;
      this.identityService = identityService;
      this.dataItemInserts = dataItemInserts;
      this.txNotCurrents = txNotCurrents;
      this.binaryStores = binaryStores;
      this.dataInsertOrder = dataInsertOrder;
   }

   private int getLocalTypeId(long typeUuidId) throws OseeCoreException {
      return identityService.getLocalId(typeUuidId);
   }

   @Override
   public void visit(ArtifactData data) throws OseeCoreException {
      boolean willGammaBeCreated = isGammaCreationAllowed(data);

      updateTxValues(data);

      if (willGammaBeCreated) {
         int localTypeId = getLocalTypeId(data.getTypeUuid());
         addRow(1, INSERT_ARTIFACT, data.getLocalId(), localTypeId, data.getVersion().getGammaId(), data.getGuid(),
            data.getHumanReadableId());
      }
      addTxs(data, OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS);
   }

   @Override
   public void visit(AttributeData data) throws OseeCoreException {
      boolean willGammaBeCreated = isGammaCreationAllowed(data);
      updateTxValues(data);

      if (willGammaBeCreated) {
         DataProxy dataProxy = data.getDataProxy();

         DaoToSql daoToSql = new DaoToSql(data.getVersion().getGammaId(), dataProxy, !useExistingBackingData(data));
         addBinaryStore(daoToSql);

         if (RelationalConstants.DEFAULT_ITEM_ID == data.getLocalId()) {
            int localId = idFactory.getNextAttributeId();
            data.setLocalId(localId);
         }
         int localTypeId = getLocalTypeId(data.getTypeUuid());
         addRow(2, INSERT_ATTRIBUTE, data.getLocalId(), localTypeId, data.getVersion().getGammaId(),
            data.getArtifactId(), daoToSql.getValue(), daoToSql.getUri());
      }
      addTxs(data, OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES);
   }

   @Override
   public void visit(RelationData data) throws OseeCoreException {
      boolean willGammaBeCreated = isGammaCreationAllowed(data);
      updateTxValues(data);

      if (willGammaBeCreated) {
         if (RelationalConstants.DEFAULT_ITEM_ID == data.getLocalId()) {
            int localId = idFactory.getNextRelationId();
            data.setLocalId(localId);
         }
         int localTypeId = getLocalTypeId(data.getTypeUuid());
         addRow(3, INSERT_RELATION_TABLE, data.getLocalId(), localTypeId, data.getVersion().getGammaId(),
            data.getArtIdA(), data.getArtIdB(), data.getRationale());
      }
      addTxs(data, OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS);
   }

   private void addTxs(OrcsData orcsData, OseeSql selectTxNotCurrent) {
      VersionData data = orcsData.getVersion();
      ModificationType modType = orcsData.getModType();

      addRow(Integer.MAX_VALUE, INSERT_INTO_TRANSACTION_TABLE, data.getTransactionId(), data.getGammaId(),
         modType.getValue(), TxChange.getCurrent(modType).getValue(), data.getBranchId());

      txNotCurrents.put(selectTxNotCurrent, params(orcsData.getLocalId(), data.getBranchId()));
   }

   private void updateTxValues(OrcsData orcsData) throws OseeCoreException {
      VersionData data = orcsData.getVersion();

      orcsData.setModType(computeModType(orcsData.getModType()));
      data.setGammaId(getGammaId(orcsData));
      data.setTransactionId(txId);
   }

   private ModificationType computeModType(ModificationType original) {
      ModificationType toReturn = original;
      if (ModificationType.REPLACED_WITH_VERSION == toReturn) {
         toReturn = ModificationType.MODIFIED;
      }
      return toReturn;
   }

   private long getGammaId(OrcsData data) throws OseeCoreException {
      long toReturn = data.getVersion().getGammaId();
      if (RelationalConstants.GAMMA_SENTINEL == toReturn || isGammaCreationAllowed(data)) {
         toReturn = idFactory.getNextGammaId();
      } else {
         toReturn = data.getVersion().getGammaId();
      }
      return toReturn;
   }

   protected boolean isGammaCreationAllowed(OrcsData data) {
      return !useExistingBackingData(data);
   }

   private boolean useExistingBackingData(OrcsData data) {
      return data.getModType().isExistingVersionUsed();
   }

   private void addRow(int insertPriority, String insertSql, Object... data) {
      dataItemInserts.put(insertSql, data);
      dataInsertOrder.put(insertPriority, insertSql);
   }

   private Object[] params(Object... data) {
      return data;
   }

   private void addBinaryStore(DaoToSql binaryTx) {
      binaryStores.add(binaryTx);
   }

   private boolean isStorageAllowed(OrcsData data) {
      boolean persist = true;
      if (data.getModType().isDeleted() && !data.getVersion().isInStorage()) {
         persist = false;
      }
      return persist;
   }
}
