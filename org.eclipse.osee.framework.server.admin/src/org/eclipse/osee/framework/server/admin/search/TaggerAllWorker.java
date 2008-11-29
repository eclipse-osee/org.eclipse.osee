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
package org.eclipse.osee.framework.server.admin.search;

import java.sql.Connection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.data.JoinUtility;
import org.eclipse.osee.framework.core.data.JoinUtility.TagQueueJoinQuery;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.exception.OseeDataStoreException;
import org.eclipse.osee.framework.search.engine.TagListenerAdapter;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

/**
 * @author Roberto E. Escobar
 */
class TaggerAllWorker extends BaseCmdWorker {
   private static final int BATCH_SIZE = 1000;

   private TagProcessListener processor;

   TaggerAllWorker() {
      super();
      this.processor = null;
   }

   private void fetchAndProcessGammas(Connection connection, int branchId, TagProcessListener processor) throws OseeDataStoreException {
      ConnectionHandlerStatement chStmt = new ConnectionHandlerStatement(connection);
      try {
         chStmt.runPreparedQuery(AttributeDataStore.getAllTaggableGammasByBranchQuery(branchId),
               AttributeDataStore.getAllTaggableGammasByBranchQueryData(branchId));
         TagQueueJoinQuery joinQuery = JoinUtility.createTagQueueJoinQuery();
         while (chStmt.next() && isExecutionAllowed()) {
            long gammaId = chStmt.getLong("gamma_id");
            joinQuery.add(gammaId);
            if (joinQuery.size() >= BATCH_SIZE) {
               processor.storeAndAddQueryId(connection, joinQuery);
               joinQuery = JoinUtility.createTagQueueJoinQuery();
            }
         }
         processor.storeAndAddQueryId(connection, joinQuery);
      } finally {
         chStmt.close();
      }
   }

   protected void doWork(final long startTime) throws Exception {
      Connection connection = null;
      try {
         String arg = getCommandInterpreter().nextArgument();
         int branchId = -1;
         if (arg != null && arg.length() > 0) {
            branchId = Integer.parseInt(arg);
         }
         println(String.format("Tagging Attributes For: [%s]", branchId > -1 ? "Branch " + branchId : "All Branches"));
         connection = OseeDbConnection.getConnection();

         int totalAttributes = AttributeDataStore.getTotalTaggableItems(connection, branchId);
         processor = new TagProcessListener(startTime, totalAttributes);
         fetchAndProcessGammas(connection, branchId, processor);
         if (!processor.isProcessingDone()) {
            synchronized (processor) {
               processor.wait();
            }
         }

         if (!isExecutionAllowed() && !processor.isProcessingDone()) {
            processor.cancelProcessing(connection);
         }
         processor.printStats();
      } finally {
         processor = null;
         if (connection != null) {
            connection.close();
         }
      }
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#setExecutionAllowed(boolean)
    */
   @Override
   public void setExecutionAllowed(boolean value) {
      super.setExecutionAllowed(value);
      if (!value && processor != null) {
         synchronized (processor) {
            processor.notify();
         }
      }
   }

   private final class TagProcessListener extends TagListenerAdapter {

      private final Map<Integer, TagQueueJoinQuery> queryIdMap;
      private int attributesProcessed;
      private int queriesProcessed;
      private long startTime;
      private int totalAttributes;

      public TagProcessListener(long startTime, int totalAttributes) {
         this.queryIdMap = Collections.synchronizedMap(new HashMap<Integer, TagQueueJoinQuery>());
         this.startTime = startTime;
         this.totalAttributes = totalAttributes;
         this.attributesProcessed = 0;
         this.queriesProcessed = 0;
      }

      /**
       * @param connection
       */
      public void cancelProcessing(Connection connection) {
         Set<Integer> list = queryIdMap.keySet();
         int[] toStop = new int[list.size()];
         int index = 0;
         for (Integer item : list) {
            toStop[index] = item;
            index++;
         }
         Activator.getInstance().getSearchTagger().stopTaggingByQueueQueryId(toStop);
      }

      public void storeAndAddQueryId(Connection connection, TagQueueJoinQuery joinQuery) throws OseeDataStoreException {
         if (joinQuery.size() > 0) {
            joinQuery.store(connection);
            this.queryIdMap.put(joinQuery.getQueryId(), joinQuery);
            Activator.getInstance().getSearchTagger().tagByQueueQueryId(this, joinQuery.getQueryId());
         }
      }

      public boolean isProcessingDone() {
         return queriesProcessed == totalQueries();
      }

      public int totalQueries() {
         int remainder = totalAttributes % 1000;
         return totalAttributes / 1000 + (remainder > 0 ? 1 : 0);
      }

      public void printStats() {
         if (isVerbose()) {
            println(String.format("QueryIds: [ %d of %d] Attributes: [%d of %d] - Elapsed Time = %s.",
                  queriesProcessed, totalQueries(), attributesProcessed, totalAttributes, getElapsedTime(startTime)));
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.search.engine.ITagListener#onItemTagged(int,
       *      long, int, long)
       */
      @Override
      public void onAttributeTagComplete(int queryId, long gammaId, int totalTags, long processingTime) {
         if (queryIdMap.containsKey(queryId)) {
            attributesProcessed++;
            if (attributesProcessed % 1000 == 0) {
               printStats();
            }
         }
      }

      /*
       * (non-Javadoc)
       * 
       * @see org.eclipse.osee.framework.search.engine.ITagListener#onTagWorkerEnd(int,
       *      long)
       */
      @Override
      synchronized public void onTagQueryIdTagComplete(int queryId, long waitTime, long processingTime) {
         TagQueueJoinQuery joinQuery = this.queryIdMap.get(queryId);
         if (joinQuery != null) {
            this.queryIdMap.remove(joinQuery);
            queriesProcessed++;
            if (isProcessingDone()) {
               this.notify();
            }
         }
      }
   }
}
