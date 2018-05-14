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

import com.google.common.collect.Iterables;
import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.executor.CancellableCallable;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.type.ResultSets;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryCollector;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.artifact.Artifact;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilder;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilderFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class CallableQueryFactory {

   private final Log logger;
   private final QueryEngine queryEngine;
   private final QueryCollector collector;
   private final GraphBuilderFactory builderFactory;
   private final GraphProvider provider;
   private final ExternalArtifactManager proxyManager;

   public CallableQueryFactory(Log logger, QueryEngine queryEngine, QueryCollector collector, GraphBuilderFactory builderFactory, GraphProvider provider, ExternalArtifactManager proxyManager) {
      super();
      this.logger = logger;
      this.queryEngine = queryEngine;
      this.collector = collector;
      this.builderFactory = builderFactory;
      this.provider = provider;
      this.proxyManager = proxyManager;
   }

   public CancellableCallable<ResultSet<? extends ArtifactId>> createLocalIdSearch(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<? extends ArtifactId>>(session, queryData) {

         @Override
         protected ResultSet<? extends ArtifactId> innerCall() throws Exception {
            final List<ArtifactId> results = new LinkedList<>();
            LoadDataHandlerAdapter handler = new LoadDataHandlerAdapter() {
               @Override
               public void onData(ArtifactData data) {
                  results.add(data);
               }
            };
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.ARTIFACT_AND_ATTRIBUTE_DATA);
            queryEngine.runArtifactQuery(getQueryData(), handler);
            setItemsFound(results.size());
            return ResultSets.newResultSet(results);
         }
      };
   }

   public CancellableCallable<ResultSet<ArtifactReadable>> createSearch(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<ArtifactReadable>>(session, queryData) {

         @Override
         protected ResultSet<ArtifactReadable> innerCall() throws Exception {
            GraphBuilder handler = builderFactory.createGraphBuilder(provider);
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.ALL);
            queryEngine.runArtifactQuery(getQueryData(), handler);
            Iterable<Artifact> results = handler.getArtifacts();
            setItemsFound(Iterables.size(results));
            return proxyManager.asExternalArtifacts(getSession(), results);
         }
      };
   }

   public CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches(OrcsSession session, QueryData queryData) {
      return new AbstractSearchCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>>(session, queryData) {

         @Override
         protected ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> innerCall() throws Exception {
            GraphBuilder handler = builderFactory.createGraphBuilder(provider);
            ArtifactMatchDataHandler matchHandler = new ArtifactMatchDataHandler(getSession(), handler, proxyManager);
            OptionsUtil.setLoadLevel(getQueryData().getOptions(), LoadLevel.ALL);
            queryEngine.runArtifactQuery(getQueryData(), matchHandler);
            List<Match<ArtifactReadable, AttributeReadable<?>>> results = matchHandler.getResults();
            setItemsFound(Iterables.size(results));
            return ResultSets.newResultSet(results);
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

   public QueryEngine getQueryEngine() {
      return queryEngine;
   }
}
