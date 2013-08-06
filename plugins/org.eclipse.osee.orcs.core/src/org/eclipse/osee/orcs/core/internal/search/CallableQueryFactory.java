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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryCollector;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilder;
import org.eclipse.osee.orcs.core.internal.ArtifactBuilderFactory;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class CallableQueryFactory {

   private final Log logger;
   private final QueryEngine queryEngine;
   private final QueryCollector collector;
   private final ArtifactBuilderFactory builderFactory;

   public CallableQueryFactory(Log logger, QueryEngine queryEngine, QueryCollector collector, ArtifactBuilderFactory builderFactory) {
      super();
      this.logger = logger;
      this.queryEngine = queryEngine;
      this.collector = collector;
      this.builderFactory = builderFactory;
   }

   public CancellableCallable<Integer> createCount(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<Integer>(session, queryData) {
         @Override
         protected Integer innerCall() throws Exception {
            Integer results = queryEngine.createArtifactCount(getSession(), getQueryData()).call();
            setItemsFound(results);
            return results;
         }
      };
   }

   public CancellableCallable<ResultSet<HasLocalId>> createLocalIdSearch(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<HasLocalId>>(session, queryData) {

         @Override
         protected ResultSet<HasLocalId> innerCall() throws Exception {
            final List<HasLocalId> results = new LinkedList<HasLocalId>();
            LoadDataHandlerAdapter handler = new LoadDataHandlerAdapter() {
               @Override
               public void onData(ArtifactData data) {
                  results.add(data);
               }
            };
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.ATTRIBUTE);
            queryEngine.createArtifactQuery(getSession(), getQueryData(), handler).call();
            setItemsFound(results.size());
            return new ResultSetList<HasLocalId>(results);
         }
      };
   }

   public CancellableCallable<ResultSet<ArtifactReadable>> createSearch(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<ArtifactReadable>>(session, queryData) {

         @Override
         protected ResultSet<ArtifactReadable> innerCall() throws Exception {
            ArtifactBuilder handler = builderFactory.createArtifactBuilder();
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.FULL);
            queryEngine.createArtifactQuery(getSession(), getQueryData(), handler).call();
            List<ArtifactReadable> results = handler.getArtifacts();
            setItemsFound(results.size());
            return new ResultSetList<ArtifactReadable>(results);
         }
      };
   }

   public CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>>(session, queryData) {

         @Override
         protected ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> innerCall() throws Exception {
            ArtifactBuilder builder = builderFactory.createArtifactBuilder();
            ArtifactMatchDataHandler handler = new ArtifactMatchDataHandler(builder);
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.FULL);
            queryEngine.createArtifactQuery(getSession(), getQueryData(), handler).call();

            List<Match<ArtifactReadable, AttributeReadable<?>>> results = handler.getResults();
            setItemsFound(results.size());
            return new ResultSetList<Match<ArtifactReadable, AttributeReadable<?>>>(results);
         }
      };
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
