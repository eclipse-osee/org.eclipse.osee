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
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.OrcsData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

public class TxSqlBuilder {
   private final HashCollection<String, Object[]> dataItemInserts = new HashCollection<String, Object[]>();
   private final HashCollection<OseeSql, Object[]> txNotCurrents = new HashCollection<OseeSql, Object[]>();
   private final List<BinaryStoreTx> binaryStores = new ArrayList<BinaryStoreTx>();
   private List<String> orderedSql;

   private SqlProvider<ArtifactData> artifactDataProvider;
   private SqlProvider<AttributeData> attributeDataProvider;
   private SqlProvider<RelationData> relationDataProvider;

   private final Collection<ArtifactTransactionData> artifactTransactionData;

   public TxSqlBuilder(Collection<ArtifactTransactionData> artifactTransactionData) {
      super();
      this.artifactTransactionData = artifactTransactionData;
   }

   public Iterable<String> getObjectSql() {
      return orderedSql != null ? orderedSql : Collections.<String> emptyList();
   }

   public List<Object[]> getObjectParameters(String sql) {
      return (List<Object[]>) dataItemInserts.getValues(sql);
   }

   public Iterable<OseeSql> getTxSql() {
      return txNotCurrents.keySet();
   }

   public List<Object[]> getTxParameters(OseeSql sqlKey) {
      return (List<Object[]>) txNotCurrents.getValues(sqlKey);
   }

   public List<BinaryStoreTx> getBinaryTxs() {
      return binaryStores;
   }

   public void build() throws OseeCoreException {
      DataCollector collector = new DataCollector();
      for (ArtifactTransactionData txData : artifactTransactionData) {
         if (isStorageAllowed(txData.getArtifactData())) {
            artifactDataProvider.getInsertData(collector, txData.getArtifactData());
            for (AttributeData attributeData : txData.getAttributeData()) {
               attributeDataProvider.getInsertData(collector, attributeData);
            }
            for (RelationData relationData : txData.getRelationData()) {
               relationDataProvider.getInsertData(collector, relationData);
            }
         }
      }
      orderedSql = collector.getOrderedSql();
   }

   private boolean isStorageAllowed(ArtifactData data) {
      boolean persist = true;
      if (data.getModType().isDeleted() && !data.getVersion().isInStorage()) {
         persist = false;
      }
      return persist;
   }

   protected boolean useExistingBackingData(OrcsData data) {
      return data.getModType().isExistingVersionUsed();
   }

   private final class DataCollector implements InsertDataCollector {
      private final Map<Integer, String> dataInsertOrder = new HashMap<Integer, String>();

      public List<String> getOrderedSql() {
         List<String> orderedSql = new ArrayList<String>(dataInsertOrder.size());
         List<Integer> keys = new ArrayList<Integer>(dataInsertOrder.keySet());
         Collections.sort(keys);
         for (int priority : keys) {
            orderedSql.add(dataInsertOrder.get(priority));
         }
         return orderedSql;
      }

      @Override
      public void addInsertToBatch(int insertPriority, String insertSql, Object... data) {
         dataItemInserts.put(insertSql, data);
         dataInsertOrder.put(insertPriority, insertSql);
      }

      @Override
      public void addTxNotCurrentToBatch(OseeSql insertSql, Object... data) {
         txNotCurrents.put(insertSql, data);
      }

      @Override
      public void addBinaryStore(BinaryStoreTx binaryTx) {
         binaryStores.add(binaryTx);
      }
   }

}
