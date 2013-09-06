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
package org.eclipse.osee.orcs.db.internal.search.engines;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.executor.admin.HasCancellation;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.database.core.AbstractJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.db.internal.loader.LoadUtil;
import org.eclipse.osee.orcs.db.internal.loader.data.AttributeDataImpl;
import org.eclipse.osee.orcs.db.internal.search.QueryFilterFactory;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.util.ArtifactDataCountHandler;
import org.eclipse.osee.orcs.db.internal.search.util.AttributeDataMatcher;
import org.eclipse.osee.orcs.db.internal.search.util.BufferedLoadDataHandler;
import org.eclipse.osee.orcs.db.internal.search.util.LoadDataBuffer;
import org.eclipse.osee.orcs.db.internal.sql.RelationalConstants;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;

/**
 * @author Roberto E. Escobar
 */
public class QueryFilterFactoryImpl implements QueryFilterFactory {

   private final Log logger;
   private final ExecutorAdmin executorAdmin;
   private final AttributeDataMatcher matcher;

   public QueryFilterFactoryImpl(Log logger, ExecutorAdmin executorAdmin, AttributeDataMatcher matcher) {
      super();
      this.logger = logger;
      this.executorAdmin = executorAdmin;
      this.matcher = matcher;
   }

   @Override
   public boolean isFilterRequired(QueryData queryData) {
      return queryData.hasCriteriaType(CriteriaAttributeKeywords.class);
   }

   @Override
   public ArtifactDataCountHandler createHandler(HasCancellation cancellation, QueryData queryData, QuerySqlContext queryContext, LoadDataHandler handler) throws Exception {
      CriteriaSet criteriaSet = queryData.getCriteriaSet();
      Set<CriteriaAttributeKeywords> criterias = criteriaSet.getCriteriaByType(CriteriaAttributeKeywords.class);
      ArtifactDataCountHandler countingHandler;
      if (criterias.isEmpty()) {
         countingHandler = new ArtifactDataCountHandler(handler);
      } else {
         countingHandler = createFilteringHandler(cancellation, criterias, queryContext, handler);
      }
      return countingHandler;
   }

   private ArtifactDataCountHandler createFilteringHandler(final HasCancellation cancellation, final Set<CriteriaAttributeKeywords> criterias, QuerySqlContext queryContext, final LoadDataHandler handler) throws Exception {
      int initialSize = computeFetchSize(queryContext);
      LoadDataBuffer buffer = new LoadDataBuffer(initialSize);
      Consumer consumer = new ConsumerImpl(cancellation, criterias);
      return new AttributeDataProducer(buffer, handler, consumer);
   }

   private int computeFetchSize(SqlContext sqlContext) {
      int fetchSize = RelationalConstants.MIN_FETCH_SIZE;
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         fetchSize = Math.max(fetchSize, join.size());
      }
      return LoadUtil.computeFetchSize(fetchSize);
   }

   private interface Consumer {

      void onLoadStart();

      void onData(final AttributeData data, final LoadDataHandler handler) throws OseeCoreException;

      void onLoadEnd() throws OseeCoreException;

   }

   private static final AttributeData END_OF_QUEUE = new AttributeDataImpl(null);

   private final class ConsumerImpl implements Consumer {

      private final HasCancellation cancellation;
      private final Set<CriteriaAttributeKeywords> criterias;

      private final AtomicBoolean executorStarted = new AtomicBoolean();

      private final LinkedBlockingQueue<AttributeData> dataToProcess = new LinkedBlockingQueue<AttributeData>();
      private Future<?> future;

      public ConsumerImpl(HasCancellation cancellation, Set<CriteriaAttributeKeywords> criterias) {
         super();
         this.cancellation = cancellation;
         this.criterias = criterias;
      }

      @Override
      public void onLoadStart() {
         executorStarted.set(false);
         dataToProcess.clear();
         if (future != null) {
            cancelFutures();
            future = null;
         }
      }

      @Override
      public void onData(AttributeData data, LoadDataHandler handler) throws OseeCoreException {
         try {
            if (data.getDataProxy().isInMemory()) {
               process(cancellation, data, handler);
            } else {
               addToQueue(data, handler);
            }
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            if (cancellation.isCancelled()) {
               cancelFutures();
            }
         }
      }

      private void addToQueue(AttributeData data, LoadDataHandler handler) throws Exception {
         dataToProcess.offer(data);
         try {
            if (executorStarted.compareAndSet(false, true)) {
               CancellableCallable<Void> consumer = createConsumer(handler);
               future = executorAdmin.schedule(consumer);
            }
         } finally {
            if (cancellation.isCancelled()) {
               cancelFutures();
            }
         }

      }

      private CancellableCallable<Void> createConsumer(final LoadDataHandler handler) {
         return new CancellableCallable<Void>() {
            @Override
            public Void call() throws Exception {
               boolean isEndOfQueue = false;
               while (!isEndOfQueue) {
                  Set<AttributeData> toProcess = new HashSet<AttributeData>();
                  AttributeData entry = dataToProcess.take();
                  dataToProcess.drainTo(toProcess);
                  toProcess.add(entry);
                  for (AttributeData item : toProcess) {
                     if (END_OF_QUEUE != item) {
                        checkForCancelled();
                        process(this, item, handler);
                     } else {
                        isEndOfQueue = true;
                     }
                  }
               }
               return null;
            }
         };
      }

      private void process(HasCancellation cancellation, AttributeData data, LoadDataHandler handler) throws Exception {
         for (CriteriaAttributeKeywords criteria : criterias) {
            cancellation.checkForCancelled();
            Collection<String> valuesToMatch = criteria.getValues();
            Collection<? extends IAttributeType> typesFilter = criteria.getTypes();
            QueryOption[] options = criteria.getOptions();
            matcher.process(cancellation, handler, data, valuesToMatch, typesFilter, options);
         }
      }

      @Override
      public void onLoadEnd() throws OseeCoreException {
         dataToProcess.offer(END_OF_QUEUE);
         try {
            waitForResults();
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            cancelFutures();
            executorStarted.set(false);
            if (future != null) {
               future = null;
            }
         }
      }

      private void waitForResults() throws Exception {
         if (future != null) {
            if (cancellation.isCancelled()) {
               future.cancel(true);
            } else {
               // Wait for execution
               future.get();
            }
         }
      }

      private void cancelFutures() {
         if (future != null) {
            future.cancel(true);
         }
      }

   }

   private final class AttributeDataProducer extends BufferedLoadDataHandler {

      private final Consumer consumer;

      private final Set<Integer> acceptedArtIds = new ConcurrentSkipListSet<Integer>();

      public AttributeDataProducer(LoadDataBuffer buffer, LoadDataHandler handler, Consumer consumer) {
         super(handler, buffer);
         this.consumer = consumer;
      }

      private void reset() {
         acceptedArtIds.clear();
         getBuffer().clear();
      }

      @Override
      public void onLoadStart() throws OseeCoreException {
         reset();
         consumer.onLoadStart();
         super.onLoadStart();
      }

      @Override
      public void onData(AttributeData data) throws OseeCoreException {
         super.onData(data);
         consumer.onData(data, this);
      }

      @Override
      public void onData(AttributeData data, MatchLocation match) throws OseeCoreException {
         acceptedArtIds.add(data.getArtifactId());
         forwardArtifacts(data.getArtifactId());
         super.onData(data, match);
      }

      private void forwardArtifacts(int artifactId) throws OseeCoreException {
         LoadDataBuffer buffer = getBuffer();
         LoadDataHandler handler = getHandler();
         if (handler != null) {
            ArtifactData art = buffer.removeArtifactByArtId(artifactId);
            Iterable<AttributeData> attrs = buffer.removeAttributesByArtId(artifactId);
            Iterable<RelationData> rels = buffer.removeRelationsByArtId(artifactId);
            if (art != null) {
               handler.onData(art);
            }
            if (attrs != null) {
               for (AttributeData attr : attrs) {
                  handler.onData(attr);
               }
            }
            if (rels != null) {
               for (RelationData rel : rels) {
                  handler.onData(rel);
               }
            }
         }
      }

      private void forwardArtifacts() throws OseeCoreException {
         // Ensure all data required by the artifact is forwarded to the handler
         // This needs to be done in order to avoid missing relation data
         // coming in after the artifact data has been forwarded.
         for (int artifactId : acceptedArtIds) {
            forwardArtifacts(artifactId);
         }
      }

      @Override
      public void onLoadEnd() throws OseeCoreException {
         try {
            consumer.onLoadEnd();
            forwardArtifacts();
         } catch (Exception ex) {
            logger.error(ex, "Error waiting for query post process results");
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            getCounter().getAndSet(acceptedArtIds.size());
            reset();
            super.onLoadEnd();
         }
      }
   }

}
