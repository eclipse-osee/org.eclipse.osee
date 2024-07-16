/*********************************************************************
 * Copyright (c) 2013 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.db.internal.search.engines;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.RelationalConstants;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.executor.CancellableRunnable;
import org.eclipse.osee.framework.core.executor.ExecutorAdmin;
import org.eclipse.osee.framework.core.executor.HasCancellation;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.AttributeData;
import org.eclipse.osee.orcs.core.ds.CountingLoadDataHandler;
import org.eclipse.osee.orcs.core.ds.LoadDataHandler;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.RelationData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.db.internal.loader.LoadUtil;
import org.eclipse.osee.orcs.db.internal.loader.data.AttributeDataImpl;
import org.eclipse.osee.orcs.db.internal.search.QueryFilterFactory;
import org.eclipse.osee.orcs.db.internal.search.QuerySqlContext;
import org.eclipse.osee.orcs.db.internal.search.util.AttributeDataMatcher;
import org.eclipse.osee.orcs.db.internal.search.util.LoadDataBuffer;
import org.eclipse.osee.orcs.db.internal.sql.SqlContext;
import org.eclipse.osee.orcs.db.internal.sql.join.AbstractJoinQuery;

/**
 * @author Roberto E. Escobar
 */
public class QueryFilterFactoryImpl implements QueryFilterFactory {
   private final Log logger;
   private final ExecutorAdmin executorAdmin;
   private final AttributeDataMatcher matcher;

   public QueryFilterFactoryImpl(Log logger, ExecutorAdmin executorAdmin, AttributeDataMatcher matcher) {
      this.logger = logger;
      this.executorAdmin = executorAdmin;
      this.matcher = matcher;
   }

   @Override
   public CountingLoadDataHandler createHandler(QueryData queryData, QuerySqlContext queryContext,
      LoadDataHandler handler) {
      List<CriteriaAttributeKeywords> criterias = queryData.getCriteriaByType(CriteriaAttributeKeywords.class);
      CountingLoadDataHandler countingHandler;
      if (criterias.isEmpty()) {
         // Nothing to Loading
         countingHandler = new ObjectCountingHandler(handler);
      } else {
         int initialSize = computeFetchSize(queryContext);
         LoadDataBuffer buffer = new LoadDataBuffer(initialSize);

         Consumer consumer = new ConsumerImpl(criterias);
         countingHandler = new AttributeDataProducer(buffer, handler, consumer);
      }
      return countingHandler;
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

      void onData(final AttributeData<?> data, final LoadDataHandler handler);

      void onLoadEnd();

   }

   private static final AttributeData<?> END_OF_QUEUE = new AttributeDataImpl<>(null);

   private final class ConsumerImpl implements Consumer {

      private final List<CriteriaAttributeKeywords> criterias;

      private final AtomicBoolean executorStarted = new AtomicBoolean();

      private final LinkedBlockingQueue<AttributeData<?>> dataToProcess = new LinkedBlockingQueue<>();
      private Future<?> future;

      public ConsumerImpl(List<CriteriaAttributeKeywords> criterias) {
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
      public void onData(AttributeData<?> data, LoadDataHandler handler) {
         addToQueue(data, handler);
      }

      private void addToQueue(AttributeData<?> data, LoadDataHandler handler) {
         dataToProcess.offer(data);
         if (executorStarted.compareAndSet(false, true)) {
            future = executorAdmin.submit("AttributeData loader", createConsumer(handler));
         }
      }

      private Runnable createConsumer(final LoadDataHandler handler) {
         return new CancellableRunnable() {
            @Override
            public void run() {
               try {
                  boolean isEndOfQueue = false;
                  Map<ArtifactId, CriteriaMatchTracker> artIdToCriteriaTracker = Maps.newHashMap();
                  while (!isEndOfQueue) {
                     Set<AttributeData<?>> toProcess = new HashSet<>();
                     AttributeData<?> entry = dataToProcess.take();
                     dataToProcess.drainTo(toProcess);
                     toProcess.add(entry);
                     for (AttributeData<?> item : toProcess) {
                        if (END_OF_QUEUE != item) {
                           CriteriaMatchTracker tracker = artIdToCriteriaTracker.get(item.getArtifactId());
                           if (tracker == null) {
                              tracker = new CriteriaMatchTracker(criterias);
                              artIdToCriteriaTracker.put(item.getArtifactId(), tracker);
                           }
                           checkForCancelled();
                           List<MatchLocation> matches = process(this, item, handler, tracker.remainingCriteriaToMatch);
                           tracker.addMatches(item, matches);
                        } else {
                           isEndOfQueue = true;
                        }
                     }
                  }
                  for (CriteriaMatchTracker tracker : artIdToCriteriaTracker.values()) {
                     // matched all criteria
                     tracker.finish(handler);
                  }
               } catch (Exception ex) {
                  OseeCoreException.wrapAndThrow(ex);
               }
            }
         };
      }

      private List<MatchLocation> process(HasCancellation cancellation, AttributeData<?> data, LoadDataHandler handler,
         Set<CriteriaAttributeKeywords> remaining) throws Exception {
         List<MatchLocation> locations = Lists.newLinkedList();
         for (CriteriaAttributeKeywords criteria : criterias) {
            cancellation.checkForCancelled();
            Collection<String> valuesToMatch = criteria.getValues();
            Collection<AttributeTypeToken> typesFilter = criteria.getTypes();
            QueryOption[] options = criteria.getOptions();
            List<MatchLocation> matches = matcher.process(cancellation, data, valuesToMatch, typesFilter, options);
            if (Conditions.hasValues(matches)) {
               remaining.remove(criteria);
               locations.addAll(matches);
            }
         }
         return locations;
      }

      @Override
      public void onLoadEnd() {
         dataToProcess.offer(END_OF_QUEUE);
         try {
            waitForResults();
         } catch (Exception ex) {
            OseeCoreException.wrapAndThrow(ex);
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
            // Wait for execution
            future.get();
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

      // A ConcurrentSkipListSet is being used to provide a set sorted by artifact id that may be used concurrently
      private final Set<ArtifactId> acceptedArtIds =
         new ConcurrentSkipListSet<>(Comparator.comparingLong(ArtifactId::getId));

      public AttributeDataProducer(LoadDataBuffer buffer, LoadDataHandler handler, Consumer consumer) {
         super(handler, buffer);
         this.consumer = consumer;
      }

      private void reset() {
         acceptedArtIds.clear();
         getBuffer().clear();
      }

      @Override
      public void onLoadStart() {
         reset();
         consumer.onLoadStart();
         super.onLoadStart();
      }

      @Override
      public <T> void onData(AttributeData<T> data) {
         super.onData(data);
         consumer.onData(data, this);
      }

      @Override
      public <T> void onData(AttributeData<T> data, MatchLocation match) {
         ArtifactId artId = data.getArtifactId();
         acceptedArtIds.add(artId);
         forwardArtifacts(artId);
         super.onData(data, match);
      }

      private void forwardArtifacts(ArtifactId artifactId) {
         LoadDataBuffer buffer = getBuffer();
         LoadDataHandler handler = getHandler();
         if (handler != null) {
            ArtifactData art = buffer.removeArtifactByArtId(artifactId);
            Iterable<AttributeData<?>> attrs = buffer.removeAttributesByArtId(artifactId);
            Iterable<RelationData> rels = buffer.removeRelationsByArtId(artifactId);
            if (art != null) {
               handler.onData(art);
            }
            if (attrs != null) {
               for (AttributeData<?> attr : attrs) {
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

      private void forwardArtifacts() {
         // Ensure all data required by the artifact is forwarded to the handler
         // This needs to be done in order to avoid missing relation data
         // coming in after the artifact data has been forwarded.
         for (ArtifactId artifactId : acceptedArtIds) {
            forwardArtifacts(artifactId);
         }
      }

      @Override
      public void onLoadEnd() {
         try {
            consumer.onLoadEnd();
            forwardArtifacts();
         } catch (Exception ex) {
            logger.error(ex, "Error waiting for query post process results");
            OseeCoreException.wrapAndThrow(ex);
         } finally {
            getCounter().getAndSet(acceptedArtIds.size());
            reset();
            super.onLoadEnd();
         }
      }
   }

   private static class CriteriaMatchTracker {
      private final Map<AttributeData<?>, Set<MatchLocation>> matches = Maps.newHashMap();
      private final Set<CriteriaAttributeKeywords> remainingCriteriaToMatch;

      public CriteriaMatchTracker(List<CriteriaAttributeKeywords> criteria) {
         remainingCriteriaToMatch = Sets.newHashSet(criteria);
      }

      public void addMatches(AttributeData<?> attr, Collection<MatchLocation> matches) {
         if (Conditions.hasValues(matches)) {
            Set<MatchLocation> set = this.matches.get(attr);
            if (set == null) {
               set = Sets.newHashSet();
               this.matches.put(attr, set);
            }
            set.addAll(matches);
         }
      }

      public void finish(LoadDataHandler handler) {
         if (remainingCriteriaToMatch.isEmpty()) {
            for (Entry<AttributeData<?>, Set<MatchLocation>> entry : matches.entrySet()) {
               Set<MatchLocation> locations = entry.getValue();
               AttributeData<?> attributeData = entry.getKey();
               for (MatchLocation location : locations) {
                  handler.onData(attributeData, location);
               }
            }
         }
      }
   }

   private static class ObjectCountingHandler extends CountingLoadDataHandler {

      public ObjectCountingHandler(LoadDataHandler handler) {
         super(handler);
      }

      @Override
      public void onData(ArtifactData data) {
         incrementCount();
         super.onData(data);
      }

      @Override
      public void onDynamicData(Map<String, Object> data) {
         incrementCount();
         super.onDynamicData(data);
      }

   }

   private static class BufferedLoadDataHandler extends ObjectCountingHandler {

      private final LoadDataBuffer buffer;

      public BufferedLoadDataHandler(LoadDataHandler handler, LoadDataBuffer buffer) {
         super(handler);
         this.buffer = buffer;
      }

      protected LoadDataBuffer getBuffer() {
         return buffer;
      }

      @Override
      public void onData(ArtifactData data) {
         buffer.addData(data);
      }

      @Override
      public <T> void onData(AttributeData<T> data) {
         buffer.addData(data);
      }

      @Override
      public void onData(RelationData data) {
         buffer.addData(data);
      }

   }
}
