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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.JoinItem;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.TransactionJoinQuery;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.Activator;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.data.SearchTag;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;

/**
 * @author Roberto E. Escobar
 */
class TaggerRunnable implements Runnable {
   private static final int MAXIMUM_CACHED_TAGS = 1000;

   private Set<ITagListener> listeners;
   private Deque<SearchTag> searchTags;

   private int tagQueueQueryId;
   private long processingTime;
   private long waitStart;
   private long waitTime;

   TaggerRunnable(int tagQueueQueryId) {
      this.listeners = new HashSet<ITagListener>();
      this.searchTags = new LinkedList<SearchTag>();
      this.tagQueueQueryId = tagQueueQueryId;
      this.waitStart = System.currentTimeMillis();
      this.waitTime = 0;
      this.processingTime = 0;
   }

   public void addListener(ITagListener listener) {
      if (listener != null) {
         this.listeners.add(listener);
      }
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      this.waitTime = System.currentTimeMillis() - this.waitStart;
      long processStart = System.currentTimeMillis();
      try {
         Collection<AttributeData> attributeDatas = AttributeDataStore.getAttribute(getTagQueueQueryId());
         try {
            processAttributes(attributeDatas);
            store(this.searchTags);
            removeQueryIdFromTagQueue();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to store tags - tagQueueQueryId [%d]",
                  getTagQueueQueryId()), ex);
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to tag - tagQueueQueryId [%d]",
               getTagQueueQueryId()), ex);
      } finally {
         this.processingTime = System.currentTimeMillis() - processStart;
         for (SearchTag searchTag : searchTags) {
            searchTag.clearCache();
         }
         notifyOnTagQueryIdTagComplete();
      }
      listeners.clear();
   }

   private void processAttributes(Collection<AttributeData> attributeDatas) throws SQLException {
      TagCollector collector = new TagCollector();
      deleteOldSearchTags(attributeDatas);
      for (AttributeData attributeData : attributeDatas) {
         long startItemTime = System.currentTimeMillis();
         SearchTag searchTag = new SearchTag(attributeData.getGammaId());
         this.searchTags.add(searchTag);
         try {
            collector.setCurrent(searchTag);
            Activator.getInstance().getTaggerManager().tagIt(attributeData, collector);
            checkSizeStoreIfNeeeded();
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to tag - [%s]", searchTag), ex);
         } finally {
            collector.clearCurrent();
            notifyOnAttributeTagComplete(searchTag.getGammaId(), searchTag.getTotalTags(),
                  (System.currentTimeMillis() - startItemTime));
         }
      }
   }

   private void deleteOldSearchTags(Collection<AttributeData> attributeDatas) throws SQLException {
      TransactionJoinQuery txJoin = JoinUtility.createTransactionJoinQuery();
      Connection connection = null;
      try {
         connection = OseeDbConnection.getConnection();
         for (AttributeData attributeData : attributeDatas) {
            txJoin.add((int) attributeData.getGammaId(), -1);
         }
         txJoin.store();
         SearchTagDataStore.deleteTags(txJoin.getQueryId());
      } finally {
         txJoin.delete(connection);
         if (connection != null) {
            connection.close();
         }
      }
   }

   private void removeQueryIdFromTagQueue() throws Exception {
      Connection connection = null;
      try {
         connection = OseeDbConnection.getConnection();
         JoinUtility.deleteQuery(JoinItem.TAG_GAMMA_QUEUE, getTagQueueQueryId());
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
   }

   private void notifyOnAttributeTagComplete(long gammaId, int totalTags, long processingTime) {
      for (ITagListener listener : listeners) {
         try {
            listener.onAttributeTagComplete(tagQueueQueryId, gammaId, totalTags, processingTime);
         } catch (Exception ex) {
            OseeLog.log(TaggerRunnable.class, Level.SEVERE, String.format("Error notifying listener: [%s] ",
                  listener.getClass().getName()), ex);
         }
      }
   }

   private void notifyOnTagQueryIdTagComplete() {
      for (ITagListener listener : listeners) {
         try {
            listener.onTagQueryIdTagComplete(tagQueueQueryId, waitTime, processingTime);
         } catch (Exception ex) {
            OseeLog.log(TaggerRunnable.class, Level.SEVERE, String.format("Error notifying listener: [%s] ",
                  listener.getClass().getName()), ex);
         }
      }
   }

   private void notifyOnAttributeAddTagEvent(long gammaId, String word, long codedTag) {
      for (ITagListener listener : listeners) {
         try {
            listener.onAttributeAddTagEvent(tagQueueQueryId, gammaId, word, codedTag);
         } catch (Exception ex) {
            OseeLog.log(TaggerRunnable.class, Level.SEVERE, String.format("Error notifying listener: [%s] ",
                  listener.getClass().getName()), ex);
         }
      }
   }

   public int getTagQueueQueryId() {
      return tagQueueQueryId;
   }

   private void checkSizeStoreIfNeeeded() throws SQLException {
      int cummulative = 0;
      boolean needsStorage = false;
      for (SearchTag item : this.searchTags) {
         cummulative += item.cacheSize();
         if (cummulative >= MAXIMUM_CACHED_TAGS) {
            needsStorage = true;
            break;
         }
      }
      if (needsStorage) {
         store(this.searchTags);
      }
   }

   private void store(Collection<SearchTag> toStore) throws SQLException {
      store(toStore.toArray(new SearchTag[toStore.size()]));
   }

   private void store(SearchTag... toStore) throws SQLException {
      SearchTagDataStore.storeTags(toStore);
      for (SearchTag item : toStore) {
         item.clearCache();
      }
   }

   private final class TagCollector implements ITagCollector {
      private SearchTag currentTag;

      public void setCurrent(SearchTag searchTag) {
         this.currentTag = searchTag;
      }

      public void clearCurrent() {
         this.currentTag = null;
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.search.engine.utility.ITagCollector#addTag(java.lang.String, java.lang.Long)
       */
      @Override
      public void addTag(String word, Long codedTag) {
         if (currentTag != null) {
            currentTag.addTag(codedTag);
            notifyOnAttributeAddTagEvent(currentTag.getGammaId(), word, codedTag);
         }
      }
   }
}
