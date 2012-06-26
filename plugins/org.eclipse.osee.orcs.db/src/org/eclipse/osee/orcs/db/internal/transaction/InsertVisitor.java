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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.DataProxy;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.OrcsVisitor;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.loader.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public class InsertVisitor implements OrcsVisitor {

   private static final String INSERT_ARTIFACT =
      "INSERT INTO osee_artifact (gamma_id, art_id, art_type_id, guid, human_readable_id) VALUES (?,?,?,?,?)";

   private static final String INSERT_ATTRIBUTE =
      "INSERT INTO osee_attribute (art_id, attr_id, attr_type_id, value, gamma_id, uri) VALUES (?, ?, ?, ?, ?, ?)";

   private static final String INSERT_RELATION_TABLE =
      "INSERT INTO osee_relation_link (rel_link_id, rel_link_type_id, a_art_id, b_art_id, rationale, gamma_id) VALUES (?,?,?,?,?,?)";

   private final IdFactory idFactory;
   private final IdentityService identityService;

   private final HashCollection<String, Object[]> dataItemInserts;
   private final HashCollection<OseeSql, Object[]> txNotCurrents;
   private final List<DaoToSql> binaryStores;

   private final Map<Integer, String> dataInsertOrder;

   public InsertVisitor(IdFactory idFactory, IdentityService identityService, HashCollection<String, Object[]> dataItemInserts, HashCollection<OseeSql, Object[]> txNotCurrents, List<DaoToSql> binaryStores, Map<Integer, String> dataInsertOrder) {
      super();
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
      if (data.isStorageAllowed()) {
         int localTypeId = getLocalTypeId(data.getTypeUuid());
         addInsertToBatch(1, INSERT_ARTIFACT, getGammaId(data), data.getLocalId(), localTypeId, data.getGuid(),
            data.getHumanReadableId());
         addTxNotCurrentToBatch(OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ARTIFACTS, data.getLocalId(), data.getModType());
      }
   }

   @Override
   public void visit(AttributeData data) throws OseeCoreException {
      if (data.isStorageAllowed()) {
         long gammaId = getGammaId(data);
         int localTypeId = getLocalTypeId(data.getTypeUuid());
         DataProxy dataProxy = data.getDataProxy();

         DaoToSql daoToSql = new DaoToSql(gammaId, dataProxy, !useExistingBackingData(data));
         addBinaryStore(daoToSql);
         addInsertToBatch(2, INSERT_ATTRIBUTE, data.getArtifactId(), data.getLocalId(), localTypeId,
            daoToSql.getValue(), gammaId, daoToSql.getUri());

         addTxNotCurrentToBatch(OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_ATTRIBUTES, data.getLocalId(), data.getModType());
      }
   }

   @Override
   public void visit(RelationData data) throws OseeCoreException {
      if (data.isStorageAllowed()) {
         int localTypeId = getLocalTypeId(data.getTypeUuid());
         addInsertToBatch(3, INSERT_RELATION_TABLE, data.getLocalId(), localTypeId, data.getArtIdA(), data.getArtIdB(),
            data.getRationale(), getGammaId(data));

         addTxNotCurrentToBatch(OseeSql.TX_GET_PREVIOUS_TX_NOT_CURRENT_RELATIONS, data.getLocalId(), data.getModType());
      }
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

   private void addInsertToBatch(int insertPriority, String insertSql, Object... data) {
      dataItemInserts.put(insertSql, data);
      dataInsertOrder.put(insertPriority, insertSql);
   }

   private void addTxNotCurrentToBatch(OseeSql insertSql, Object... data) {
      txNotCurrents.put(insertSql, data);
   }

   private void addBinaryStore(DaoToSql binaryTx) {
      binaryStores.add(binaryTx);
   }
}
