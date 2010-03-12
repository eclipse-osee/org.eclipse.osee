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
package org.eclipse.osee.framework.search.engine.internal;

import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.server.CoreServerActivator;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.search.engine.ITaggerStatistics;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;

/**
 * @author Roberto E. Escobar
 */
public final class SearchEngineTagger implements ISearchEngineTagger {
   private static final int CACHE_LIMIT = 1000;
   private ExecutorService executor;
   private Map<Integer, FutureTask<?>> futureTasks;
   private TaggerStatistics statistics;

   public SearchEngineTagger() {
      this.statistics = new TaggerStatistics();
      this.futureTasks = Collections.synchronizedMap(new HashMap<Integer, FutureTask<?>>());
      this.executor = Executors.newFixedThreadPool(3, CoreServerActivator.createNewThreadFactory("tagger.worker"));

      Timer timer = new Timer("Start-Up Tagger");
      timer.schedule(new StartUpRunnable(this), 3000);
   }

   @Override
   public int deleteTags(int joinQueryId) throws Exception {
      DeleteTagsTx deleteTransaction = new DeleteTagsTx(joinQueryId);
      deleteTransaction.execute();
      return deleteTransaction.rowsDeleted();
   }

   @Override
   public void tagByQueueQueryId(int queryId) {
      tagByQueueQueryId(null, queryId);
   }

   @Override
   public void tagByQueueQueryId(ITagListener listener, int queryId) {
      TaggerRunnable runnable = new TaggerRunnable(queryId, false, CACHE_LIMIT);
      runnable.addListener(statistics);
      if (listener != null) {
         runnable.addListener(listener);
         listener.onTagQueryIdSubmit(queryId);
      }
      FutureTask<Object> futureTask = new FutureTaggingTask(runnable);
      this.futureTasks.put(queryId, futureTask);
      this.executor.submit(futureTask);
   }

   @Override
   public void tagByBranchId(ITagListener listener, int branchId) throws OseeCoreException {
      this.executor.submit(new BranchTaggerRunnable(this, listener, branchId, false, CACHE_LIMIT));
   }

   @Override
   public void tagByBranchId(int branchId) throws OseeCoreException {
      tagByBranchId(null, branchId);
   }

   @Override
   public void tagFromXmlStream(ITagListener listener, InputStream inputStream) throws Exception {
      InputStreamTagProcessor inputStreamTagProcessor =
            new InputStreamTagProcessor(this, listener, inputStream, false, CACHE_LIMIT);
      inputStreamTagProcessor.execute();
   }

   @Override
   public void tagFromXmlStream(InputStream inputStream) throws Exception {
      tagFromXmlStream(null, inputStream);
   }

   @Override
   public int getWorkersInQueue() {
      return futureTasks.size();
   }

   @Override
   public void clearStatistics() {
      this.statistics.clear();
   }

   @Override
   public ITaggerStatistics getStatistics() {
      try {
         return this.statistics.clone();
      } catch (CloneNotSupportedException ex) {
         return TaggerStatistics.EMPTY_STATS;
      }
   }

   @Override
   public int stopTaggingByQueueQueryId(int... queryId) {
      int toReturn = 0;
      for (int item : queryId) {
         FutureTask<?> task = futureTasks.get(item);
         if (task != null) {
            if (task.isDone()) {
               toReturn++;
            } else {
               if (task.cancel(true)) {
                  toReturn++;
               }
            }
         }
      }
      return toReturn;
   }

   @Override
   public int stopAllTagging() {
      int index = 0;
      Set<Integer> list = futureTasks.keySet();
      int[] toProcess = new int[list.size()];
      for (Integer item : list) {
         toProcess[index] = item;
         index++;
      }
      return stopTaggingByQueueQueryId(toProcess);
   }

   private final class FutureTaggingTask extends FutureTask<Object> {
      private TaggerRunnable runnable;

      public FutureTaggingTask(TaggerRunnable runnable) {
         super(runnable, null);
         this.runnable = runnable;
      }

      @Override
      protected void done() {
         futureTasks.remove(runnable.getTagQueueQueryId());
      }
   }

   private final class DeleteTagsTx extends DbTransaction {

      private final int queryId;
      private int updated;

      public DeleteTagsTx(int queryId) throws OseeCoreException {
         super();
         this.queryId = queryId;
         this.updated = -1;
      }

      public int rowsDeleted() {
         return updated;
      }

      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         this.updated = SearchTagDataStore.deleteTags(connection, queryId);
      }
   }
}
