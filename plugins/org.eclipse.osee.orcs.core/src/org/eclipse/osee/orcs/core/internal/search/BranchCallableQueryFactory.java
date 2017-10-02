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

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.BranchData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryCollector;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.data.BranchReadable;

/**
 * @author Roberto E. Escobar
 */
public class BranchCallableQueryFactory {

   private final Log logger;
   private final QueryEngine queryEngine;
   private final QueryCollector collector;

   public BranchCallableQueryFactory(Log logger, QueryEngine queryEngine, QueryCollector collector) {
      super();
      this.logger = logger;
      this.queryEngine = queryEngine;
      this.collector = collector;
   }

   public CancellableCallable<Integer> createBranchCount(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<Integer>(session, queryData) {
         @Override
         protected Integer innerCall() throws Exception {
            Integer results = queryEngine.createBranchCount(getSession(), getQueryData()).call();
            setItemsFound(results);
            return results;
         }
      };
   }

   public CancellableCallable<ResultSet<BranchReadable>> createBranchSearch(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<BranchReadable>>(session, queryData) {

         @Override
         protected ResultSet<BranchReadable> innerCall() throws Exception {
            BranchBuilder<BranchReadable> handler = new BranchBuilder<BranchReadable>() {
               @Override
               public BranchReadable createBranch(BranchData data) {
                  // For now we assume the row is also readable
                  // This will change once write branch API is added.
                  return (BranchReadable) data;
               }
            };
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.ALL);
            queryEngine.createBranchQuery(getSession(), getQueryData(), handler).call();
            List<BranchReadable> results = handler.getBranches();
            setItemsFound(results.size());
            return ResultSets.newResultSet(results);
         }
      };
   }

   public CancellableCallable<ResultSet<IOseeBranch>> createBranchAsIdSearch(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<IOseeBranch>>(session, queryData) {

         @Override
         protected ResultSet<IOseeBranch> innerCall() throws Exception {
            BranchBuilder<IOseeBranch> handler = new BranchBuilder<IOseeBranch>() {

               @Override
               public IOseeBranch createBranch(BranchData data) {
                  return data;
               }

            };
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.ALL);
            queryEngine.createBranchQuery(getSession(), getQueryData(), handler).call();
            List<IOseeBranch> results = handler.getBranches();
            setItemsFound(results.size());
            return ResultSets.newResultSet(results);
         }
      };
   }

   private abstract class BranchBuilder<T extends BranchId> extends LoadDataHandlerAdapter {

      private Map<Long, T> branchMap;
      private List<T> results;

      @Override
      public void onLoadStart() {
         super.onLoadStart();
         branchMap = new LinkedHashMap<>();
      }

      @Override
      public void onLoadEnd() {
         super.onLoadEnd();
         results = new LinkedList<>(branchMap.values());
         branchMap.clear();
      }

      @Override
      public void onData(BranchData data) {
         Long key = data.getId();
         T branch = branchMap.get(key);
         if (branch == null) {
            branch = createBranch(data);
            branchMap.put(key, branch);
         }
      }

      public List<T> getBranches() {
         return results;
      }

      public abstract T createBranch(BranchData data);

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
