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
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.Future;
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
      ConsumerFactory factory = new ConsumerFactoryImpl(criterias);
      return new AttributeDataProducer(logger, cancellation, buffer, handler, factory);
   }

   private int computeFetchSize(SqlContext sqlContext) {
      int fetchSize = RelationalConstants.MIN_FETCH_SIZE;
      for (AbstractJoinQuery join : sqlContext.getJoins()) {
         fetchSize = Math.max(fetchSize, join.size());
      }
      return LoadUtil.computeFetchSize(fetchSize);
   }

   private interface ConsumerFactory {
      List<Future<?>> createConsumer(final AttributeData data, final LoadDataHandler handler) throws Exception;
   }

   private class ConsumerFactoryImpl implements ConsumerFactory {
      private final Set<CriteriaAttributeKeywords> criterias;

      public ConsumerFactoryImpl(Set<CriteriaAttributeKeywords> criterias) {
         super();
         this.criterias = criterias;
      }

      @Override
      public List<Future<?>> createConsumer(final AttributeData data, final LoadDataHandler handler) throws Exception {
         List<Future<?>> futures = new LinkedList<Future<?>>();
         for (CriteriaAttributeKeywords criteria : criterias) {
            final Collection<String> valuesToMatch = criteria.getValues();
            final Collection<? extends IAttributeType> typesFilter = criteria.getTypes();
            final QueryOption[] options = criteria.getOptions();

            Callable<?> consumer = new CancellableCallable<Void>() {
               @Override
               public Void call() throws Exception {
                  checkForCancelled();
                  matcher.process(this, handler, data, valuesToMatch, typesFilter, options);
                  return null;
               }
            };
            Future<?> future = executorAdmin.schedule(consumer);
            futures.add(future);
         }
         return futures;
      }
   }

   private static final class AttributeDataProducer extends BufferedLoadDataHandler {

      private final Log logger;
      private final HasCancellation cancellation;
      private final ConsumerFactory consumerFactory;

      private List<Future<?>> futures;
      private final Set<Integer> acceptedArtIds = new CopyOnWriteArraySet<Integer>();

      public AttributeDataProducer(Log logger, HasCancellation cancellation, LoadDataBuffer buffer, LoadDataHandler handler, ConsumerFactory consumerFactory) {
         super(handler, buffer);
         this.logger = logger;
         this.cancellation = cancellation;
         this.consumerFactory = consumerFactory;
      }

      private void reset() {
         acceptedArtIds.clear();
         getBuffer().clear();
         futures = null;
      }

      @Override
      public void onLoadStart() throws OseeCoreException {
         reset();
         super.onLoadStart();
      }

      @Override
      public void onData(AttributeData data) throws OseeCoreException {
         super.onData(data);
         if (futures == null) {
            futures = new LinkedList<Future<?>>();
         }
         try {
            futures.addAll(consumerFactory.createConsumer(data, this));
         } catch (Exception ex) {
            OseeExceptions.wrapAndThrow(ex);
         } finally {
            if (futures != null && cancellation.isCancelled()) {
               for (Future<?> future : futures) {
                  future.cancel(true);
               }
            }
         }
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
      public void onLoadEnd() {
         try {
            waitForResults();
            forwardArtifacts();
         } catch (Exception ex) {
            logger.error(ex, "Error waiting for query post process results");
         } finally {
            getCounter().getAndSet(acceptedArtIds.size());
            reset();
            super.onLoadEnd();
         }
      }

      private void waitForResults() throws Exception {
         if (futures != null) {
            for (Future<?> future : futures) {
               if (cancellation.isCancelled()) {
                  future.cancel(true);
               } else {
                  // Wait for execution
                  future.get();
               }
            }
         }
      }
   }
}
