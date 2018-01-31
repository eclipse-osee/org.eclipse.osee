/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.data.TransactionReadable;
import org.eclipse.osee.orcs.search.TransactionQuery;

/**
 * @author Roberto E. Escobar
 */
public class TransactionQueryImpl extends TxQueryBuilderImpl<TransactionQuery> implements TransactionQuery {
   private final QueryEngine queryEngine;

   public TransactionQueryImpl(QueryEngine queryEngine, TransactionCriteriaFactory criteriaFactory, QueryData queryData) {
      super(criteriaFactory, queryData);
      this.queryEngine = queryEngine;
   }

   @Override
   public ResultSet<TransactionReadable> getResults() {
      List<TransactionReadable> txs = new ArrayList<>();
      query(txs);
      return ResultSets.newResultSet(txs);
   }

   @Override
   public ResultSet<TransactionToken> getTokens() {
      List<TransactionToken> txs = new ArrayList<>();
      query(txs);
      return ResultSets.newResultSet(txs);
   }

   @Override
   public ResultSet<TransactionId> getResultsAsIds() {
      List<TransactionId> txs = new ArrayList<>();
      query(txs);
      return ResultSets.newResultSet(txs);
   }

   private void query(List<? super TransactionReadable> txs) {
      QueryData queryData = build();
      OptionsUtil.setLoadLevel(queryData.getOptions(), LoadLevel.ALL);
      queryEngine.runTxQuery(queryData, txs);
   }

   @Override
   public int getCount() {
      return queryEngine.getTxCount(build());
   }

   @Override
   public boolean exists() {
      return getCount() > 0;
   }
}