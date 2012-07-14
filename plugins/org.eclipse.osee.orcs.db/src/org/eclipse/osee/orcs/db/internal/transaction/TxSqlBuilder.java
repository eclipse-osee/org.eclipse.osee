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
import org.eclipse.osee.framework.core.services.IdentityService;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.orcs.core.ds.ArtifactTransactionData;
import org.eclipse.osee.orcs.db.internal.loader.IdFactory;
import org.eclipse.osee.orcs.db.internal.sql.OseeSql;

/**
 * @author Roberto E. Escobar
 */
public class TxSqlBuilder {

   private final HashCollection<String, Object[]> dataItemInserts = new HashCollection<String, Object[]>();
   private final HashCollection<OseeSql, Object[]> txNotCurrents = new HashCollection<OseeSql, Object[]>();
   private final List<DaoToSql> binaryStores = new ArrayList<DaoToSql>();
   private List<String> orderedSql;
   private final Map<Integer, String> dataInsertOrder = new HashMap<Integer, String>();

   private final IdFactory idFactory;
   private final IdentityService identityService;
   private final Collection<ArtifactTransactionData> artifactTransactionData;

   public TxSqlBuilder(IdFactory idFactory, IdentityService identityService, Collection<ArtifactTransactionData> artifactTransactionData) {
      super();
      this.idFactory = idFactory;
      this.identityService = identityService;
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

   public List<DaoToSql> getBinaryTxs() {
      return binaryStores;
   }

   public void build(int txNumber) throws OseeCoreException {
      InsertVisitor visitor =
         new InsertVisitor(txNumber, idFactory, identityService, dataItemInserts, txNotCurrents, binaryStores,
            dataInsertOrder);
      for (ArtifactTransactionData txData : artifactTransactionData) {
         txData.accept(visitor);
      }
      orderedSql = getOrderedSql();
   }

   private List<String> getOrderedSql() {
      List<String> orderedSql = new ArrayList<String>(dataInsertOrder.size());
      List<Integer> keys = new ArrayList<Integer>(dataInsertOrder.keySet());
      Collections.sort(keys);
      for (int priority : keys) {
         orderedSql.add(dataInsertOrder.get(priority));
      }
      return orderedSql;
   }
}
