/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer;

import java.util.Collection;
import java.util.function.Consumer;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.jdk.core.type.Id;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumer;
import org.eclipse.osee.orcs.db.internal.sql.join.IdJoinQuery;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.db.internal.sql.join.TagQueueJoinQuery;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public final class IndexBranchesDatabaseCallable extends AbstractDatastoreCallable<Integer> {
   private static final int BATCH_SIZE = 1000;

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES =
      "SELECT att.gamma_id FROM osee_join_id oji, osee_attribute att WHERE oji.query_id = ? AND oji.id = att.attr_type_id";

   private static final String COUNT_ALL_TAGGABLE_ATTRIBUTES =
      FIND_ALL_TAGGABLE_ATTRIBUTES.replace("att.gamma_id", "count(1)");

   private static final String FIND_MISSING =
      FIND_ALL_TAGGABLE_ATTRIBUTES + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private static final String COUNT_MISSING = FIND_MISSING.replaceFirst("att.gamma_id", "count(1)");

   private static final String FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH =
      "SELECT DISTINCT att.gamma_id FROM osee_join_id jid1, osee_join_id jid2, osee_txs txs, osee_attribute att WHERE jid1.query_id = ? AND jid1.id = txs.branch_id AND txs.gamma_id = att.gamma_id AND att.attr_type_id = jid2.id and jid2.query_id = ?";

   private static final String COUNT_TAGGABLE_ATTRIBUTES_BY_BRANCH =
      FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH.replace("DISTINCT att.gamma_id", "count(DISTINCT att.gamma_id)");

   private static final String FIND_MISSING_BY_BRANCH =
      FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private static final String COUNT_MISSING_BY_BRANCH =
      COUNT_TAGGABLE_ATTRIBUTES_BY_BRANCH + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private final SqlJoinFactory joinFactory;
   private final AttributeTypes types;
   private final IndexingTaskConsumer consumer;
   private final IndexerCollector collector;
   private final Collection<BranchReadable> branches;
   private final Collection<? extends AttributeTypeId> typesToTag;
   private final boolean tagOnlyMissingGammas;

   public IndexBranchesDatabaseCallable(Log logger, OrcsSession session, JdbcClient service, SqlJoinFactory joinFactory, AttributeTypes types, IndexingTaskConsumer consumer, IndexerCollector collector, Collection<? extends AttributeTypeId> typesToTag, Collection<BranchReadable> branches, boolean tagOnlyMissingGammas) {
      super(logger, session, service);
      this.joinFactory = joinFactory;
      this.types = types;
      this.consumer = consumer;
      this.collector = collector;
      this.typesToTag = typesToTag;
      this.branches = branches;
      this.tagOnlyMissingGammas = tagOnlyMissingGammas;
   }

   @Override
   public Integer call() throws Exception {
      getLogger().info(getParamInfo());

      try (IdJoinQuery branchJoin = joinFactory.createIdJoinQuery();
         IdJoinQuery typeJoin = joinFactory.createIdJoinQuery()) {
         Triplet<String, String, Object[]> data = createQueries(branches, branchJoin, typeJoin);
         String countQuery = data.getFirst();
         String searchQuery = data.getSecond();
         Object[] params = data.getThird();

         for (AttributeTypeId attributeType : typesToTag) {
            if (types.isTaggable(attributeType)) {
               typeJoin.add(attributeType);
            }
         }

         typeJoin.store();
         branchJoin.store();

         if (collector != null) {
            int totalAttributes = getJdbcClient().fetch(-1, countQuery, params);
            collector.onIndexTotalTaskItems(totalAttributes);
         }

         fetchAndProcessGammas(searchQuery, params);
      }
      return branches.size();
   }

   public void storeAndAddQueryId(TagQueueJoinQuery joinQuery) {
      try {
         if (!joinQuery.isEmpty()) {
            joinQuery.store();
            consumer.submitTaskId(getSession(), types, collector, joinQuery.getQueryId());
         }
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void fetchAndProcessGammas(String query, Object... params) {
      TagQueueJoinQuery queryHolder[] = new TagQueueJoinQuery[1];
      queryHolder[0] = joinFactory.createTagQueueJoinQuery();
      Consumer<JdbcStatement> consumer = stmt -> {
         long gammaId = stmt.getLong("gamma_id");
         queryHolder[0].add(gammaId);
         if (queryHolder[0].size() >= BATCH_SIZE) {
            storeAndAddQueryId(queryHolder[0]);
            queryHolder[0] = joinFactory.createTagQueueJoinQuery();
         }
      };
      getJdbcClient().runQuery(consumer, query, params);
      storeAndAddQueryId(queryHolder[0]);
   }

   private String getParamInfo() {
      StringBuilder builder = new StringBuilder();
      builder.append("Tagging");
      if (tagOnlyMissingGammas) {
         builder.append(" (Only Missing)");
      }
      builder.append(" Attributes for ");
      if (branches.isEmpty()) {
         builder.append("All Branches");
      } else {
         builder.append("Branch(es) ").append(branches);
      }
      return builder.toString();
   }

   private Triplet<String, String, Object[]> createQueries(Collection<? extends Id> branches, IdJoinQuery branchJoin, IdJoinQuery typeJoin) {
      Object[] params;
      String countQuery;
      String searchQuery;
      if (branches.isEmpty()) {
         params = new Object[] {typeJoin.getQueryId()};
         if (tagOnlyMissingGammas) {
            countQuery = COUNT_MISSING;
            searchQuery = FIND_MISSING;
         } else {
            countQuery = COUNT_ALL_TAGGABLE_ATTRIBUTES;
            searchQuery = FIND_ALL_TAGGABLE_ATTRIBUTES;
         }
      } else {
         branchJoin.addAll(branches);
         params = new Object[] {branchJoin.getQueryId(), typeJoin.getQueryId()};
         if (tagOnlyMissingGammas) {
            countQuery = COUNT_MISSING_BY_BRANCH;
            searchQuery = FIND_MISSING_BY_BRANCH;
         } else {
            countQuery = COUNT_TAGGABLE_ATTRIBUTES_BY_BRANCH;
            searchQuery = FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH;
         }
      }
      return new Triplet<String, String, Object[]>(countQuery, searchQuery, params);
   }
}