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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.IOseeStatement;
import org.eclipse.osee.framework.database.core.IdJoinQuery;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.TagQueueJoinQuery;
import org.eclipse.osee.framework.jdk.core.type.Triplet;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.db.internal.IdentityLocator;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumer;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public final class IndexBranchesDatabaseCallable extends AbstractDatastoreCallable<Integer> {
   private static final int BATCH_SIZE = 1000;

   private static final String FIND_ALL_TAGGABLE_ATTRIBUTES =
      "SELECT att.gamma_id FROM osee_join_id oji, osee_attribute att WHERE oji.query_id = ? AND oji.id = att.attr_type_id";

   private static final String COUNT_ALL_TAGGABLE_ATTRIBUTES = FIND_ALL_TAGGABLE_ATTRIBUTES.replace("att.gamma_id",
      "count(1)");

   private static final String FIND_MISSING =
      FIND_ALL_TAGGABLE_ATTRIBUTES + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private static final String COUNT_MISSING = FIND_MISSING.replaceFirst("att.gamma_id", "count(1)");

   private static final String FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH =
      "SELECT DISTINCT att.gamma_id FROM osee_join_id jid1, osee_join_id jid2, osee_txs txs, osee_attribute att WHERE jid1.query_id = ? AND jid1.id = txs.branch_id AND txs.gamma_id = att.gamma_id AND att.attr_type_id = jid2.id and jid2.query_id = ?";

   private static final String COUNT_TAGGABLE_ATTRIBUTES_BY_BRANCH = FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH.replace(
      "DISTINCT att.gamma_id", "count(DISTINCT att.gamma_id)");

   private static final String FIND_MISSING_BY_BRANCH =
      FIND_TAGGABLE_ATTRIBUTES_BY_BRANCH + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private static final String COUNT_MISSING_BY_BRANCH =
      COUNT_TAGGABLE_ATTRIBUTES_BY_BRANCH + " AND att.gamma_id NOT IN (SELECT gamma_id FROM osee_search_tags)";

   private final IdentityLocator idService;
   private final AttributeTypes types;
   private final IndexingTaskConsumer consumer;
   private final IndexerCollector collector;
   private final Collection<BranchReadable> branches;
   private final Collection<? extends IAttributeType> typesToTag;
   private final boolean tagOnlyMissingGammas;

   public IndexBranchesDatabaseCallable(Log logger, OrcsSession session, IOseeDatabaseService service, IdentityLocator idService, AttributeTypes types, IndexingTaskConsumer consumer, IndexerCollector collector, Collection<? extends IAttributeType> typesToTag, Collection<BranchReadable> branches, boolean tagOnlyMissingGammas) {
      super(logger, session, service);
      this.idService = idService;
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

      Set<Long> branchUuids = new HashSet<Long>();
      for (BranchReadable branch : branches) {
         branchUuids.add((long) branch.getLocalId());
      }

      IdJoinQuery branchJoin = JoinUtility.createIdJoinQuery();
      IdJoinQuery typeJoin = JoinUtility.createIdJoinQuery();
      try {
         Triplet<String, String, Object[]> data = createQueries(branchUuids, branchJoin, typeJoin);
         String countQuery = data.getFirst();
         String searchQuery = data.getSecond();
         Object[] params = data.getThird();

         for (IAttributeType attributeType : typesToTag) {
            if (types.isTaggable(attributeType)) {
               typeJoin.add(attributeType.getGuid());
            }
         }

         typeJoin.store();
         branchJoin.store();

         if (collector != null) {
            int totalAttributes = ConnectionHandler.runPreparedQueryFetchInt(-1, countQuery, params);
            collector.onIndexTotalTaskItems(totalAttributes);
         }

         fetchAndProcessGammas(searchQuery, params);
      } finally {
         typeJoin.delete();
         branchJoin.delete();
      }
      return branchUuids.size();
   }

   public void storeAndAddQueryId(TagQueueJoinQuery joinQuery) throws Exception {
      if (!joinQuery.isEmpty()) {
         joinQuery.store();
         consumer.submitTaskId(getSession(), types, collector, joinQuery.getQueryId());
      }
   }

   private void fetchAndProcessGammas(String query, Object... params) throws Exception {
      IOseeStatement chStmt = getDatabaseService().getStatement();
      try {
         chStmt.runPreparedQuery(query, params);
         TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery(getDatabaseService());
         while (chStmt.next()) {
            long gammaId = chStmt.getLong("gamma_id");
            joinQuery.add(gammaId);
            if (joinQuery.size() >= BATCH_SIZE) {
               storeAndAddQueryId(joinQuery);
               joinQuery = JoinUtility.createTagQueueJoinQuery(getDatabaseService());
            }
         }
         storeAndAddQueryId(joinQuery);
      } finally {
         chStmt.close();
      }
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

   private Triplet<String, String, Object[]> createQueries(Collection<Long> branchUuids, IdJoinQuery branchJoin, IdJoinQuery typeJoin) {
      Object[] params;
      String countQuery;
      String searchQuery;
      if (branchUuids.isEmpty()) {
         params = new Object[] {typeJoin.getQueryId()};
         if (tagOnlyMissingGammas) {
            countQuery = COUNT_MISSING;
            searchQuery = FIND_MISSING;
         } else {
            countQuery = COUNT_ALL_TAGGABLE_ATTRIBUTES;
            searchQuery = FIND_ALL_TAGGABLE_ATTRIBUTES;
         }
      } else {
         for (Long id : branchUuids) {
            branchJoin.add(id);
         }
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