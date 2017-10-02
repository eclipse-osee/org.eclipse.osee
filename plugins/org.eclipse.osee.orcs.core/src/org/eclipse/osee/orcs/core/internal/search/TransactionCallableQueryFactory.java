/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import com.google.common.collect.Lists;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryCollector;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.TxOrcsData;
import org.eclipse.osee.orcs.data.TransactionReadable;

/**
 * @author Roberto E. Escobar
 */
public class TransactionCallableQueryFactory {

   private final Log logger;
   private final QueryEngine queryEngine;
   private final QueryCollector collector;

   public TransactionCallableQueryFactory(Log logger, QueryEngine queryEngine, QueryCollector collector) {
      super();
      this.logger = logger;
      this.queryEngine = queryEngine;
      this.collector = collector;
   }

   public CancellableCallable<Integer> createTransactionCount(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<Integer>(session, queryData) {
         @Override
         protected Integer innerCall() throws Exception {
            Integer results = queryEngine.createTxCount(getSession(), getQueryData()).call();
            setItemsFound(results);
            return results;
         }
      };
   }

   public CancellableCallable<ResultSet<TransactionReadable>> createTransactionSearch(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<TransactionReadable>>(session, queryData) {

         @Override
         protected ResultSet<TransactionReadable> innerCall() throws Exception {
            TransactionBuilder<TransactionReadable> handler = new TransactionBuilder<TransactionReadable>() {
               @Override
               public TransactionReadable create(TxOrcsData data) {
                  // For now we assume the row is also readable
                  // This will change once write branch API is added.
                  return data;
               }
            };
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.ALL);
            queryEngine.createTxQuery(getSession(), getQueryData(), handler).call();
            ResultSet<TransactionReadable> results = handler.getTransactions();
            setItemsFound(results.size());
            return results;
         }
      };
   }

   public CancellableCallable<ResultSet<Long>> createTransactionAsIdSearch(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<Long>>(session, queryData) {

         @Override
         protected ResultSet<Long> innerCall() throws Exception {
            final Set<Long> txs = new LinkedHashSet<>();
            LoadDataHandler handler = new LoadDataHandlerAdapter() {
               @Override
               public void onData(TxOrcsData data) {
                  txs.add(data.getId());
               }
            };
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.ALL);
            queryEngine.createTxQuery(getSession(), getQueryData(), handler).call();
            setItemsFound(txs.size());
            return ResultSets.newResultSet(txs);
         }
      };
   }
   private abstract class TransactionBuilder<T> extends LoadDataHandlerAdapter {

      private Map<Long, T> dataMap;
      private LinkedList<T> results;

      @Override
      public void onLoadStart()  {
         super.onLoadStart();
         dataMap = new LinkedHashMap<>();
      }

      @Override
      public void onLoadEnd()  {
         super.onLoadEnd();
         results = Lists.newLinkedList(dataMap.values());
         dataMap.clear();
      }

      @Override
      public void onData(TxOrcsData data)  {
         Long key = data.getId();
         T branch = dataMap.get(key);
         if (branch == null) {
            branch = create(data);
            dataMap.put(key, branch);
         }
      }

      public ResultSet<T> getTransactions() {
         return ResultSets.newResultSet(results);
      }

      public abstract T create(TxOrcsData data) ;

   }

   private abstract class AbstractSearchCallable<T> extends CancellableCallable<T> {

      private final OrcsSession session;
      private final QueryData queryData;
      private int itemsFound = 0;

      public AbstractSearchCallable(OrcsSession session, QueryData queryData) {
         super();
         this.session = session;
         this.queryData = queryData;
      }

      protected OrcsSession getSession() {
         return session;
      }

      protected QueryData getQueryData() {
         return queryData;
      }

      protected void setItemsFound(int itemsFound) {
         this.itemsFound = itemsFound;
      }

      @Override
      public final T call() throws Exception {
         long startTime = System.currentTimeMillis();
         long endTime = startTime;
         T result = null;
         try {
            if (logger.isTraceEnabled()) {
               logger.trace("%s [start] - [%s]", getClass().getSimpleName(), queryData);
            }
            result = innerCall();
         } finally {
            endTime = System.currentTimeMillis() - startTime;
         }
         if (result != null) {
            notifyStats(endTime);
         }
         if (logger.isTraceEnabled()) {
            logger.trace("%s [%s] - completed [%s]", getClass().getSimpleName(), Lib.asTimeString(endTime), queryData);
         }
         return result;
      }

      private void notifyStats(long processingTime) {
         if (collector != null) {
            try {
               collector.collect(session, itemsFound, processingTime, queryData);
            } catch (Exception ex) {
               logger.error(ex, "Error reporting search to search collector\n%s", queryData);
            }
         }
      }

      protected abstract T innerCall() throws Exception;

   }

}
