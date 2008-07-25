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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.JoinItem;
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

   private int tagQueueQueryId;
   private long elapsedTime;
   private List<ITagListener> listeners;
   private Deque<SearchTag> searchTags;
   private TagCollector collector;

   protected TaggerRunnable(int tagQueueQueryId) {
      this.listeners = new ArrayList<ITagListener>();
      this.collector = new TagCollector();
      this.tagQueueQueryId = tagQueueQueryId;
      this.searchTags = new LinkedList<SearchTag>();
      this.elapsedTime = 0;
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
      long start = System.currentTimeMillis();
      Collection<AttributeData> attributeDatas = null;
      try {
         attributeDatas = AttributeDataStore.getAttribute(getTagQueueQueryId());
         try {

            for (AttributeData attributeData : attributeDatas) {
               SearchTag searchTag = new SearchTag(attributeData.getGammaId());
               this.collector.setCurrent(searchTag);
               this.searchTags.add(searchTag);
               long tagItem = System.currentTimeMillis();
               try {
                  SearchTagDataStore.deleteTags(searchTag);
                  Activator.getInstance().getTaggerManager().tagIt(attributeData, collector);
               } catch (Exception ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to tag - [%s]", searchTag));
               } finally {
                  notifyOnTagQueryIdTagComplete(searchTag.getGammaId(), searchTag.getRunningTotal(),
                        (tagItem - System.currentTimeMillis()));
               }
               checkSizeStoreIfNeeeded();
            }
            store(this.searchTags);
            Connection connection = null;
            try {
               connection = OseeDbConnection.getConnection();
               JoinUtility.deleteQuery(JoinItem.TAG_GAMMA_QUEUE, getTagQueueQueryId());
            } finally {
               if (connection != null) {
                  connection.close();
               }
            }
         } catch (Exception ex) {
            OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to store tags - tagQueueQueryId [%d]",
                  getTagQueueQueryId()));
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to tag - tagQueueQueryId [%d]",
               getTagQueueQueryId()), ex);
      } finally {
         this.collector.clearCurrent();
         this.elapsedTime = System.currentTimeMillis() - start;
         for (SearchTag searchTag : searchTags) {
            searchTag.clearCache();
         }
         notifyOnTagQueryIdTagComplete();
      }
      listeners.clear();
   }

   private void notifyOnTagQueryIdTagComplete() {
      if (listeners != null) {
         for (ITagListener listener : listeners) {
            try {
               listener.onTagQueryIdTagComplete(tagQueueQueryId, elapsedTime);
            } catch (Exception ex) {
               OseeLog.log(TaggerRunnable.class, Level.SEVERE, String.format("Error notifying listener: [%s] ",
                     listener.getClass().getName()), ex);
            }
         }
      }
   }

   private void notifyOnTagQueryIdTagComplete(long gammaId, int totalTags, long processingTime) {
      if (listeners != null) {
         for (ITagListener listener : listeners) {
            try {
               listener.onAttributeTagComplete(tagQueueQueryId, gammaId, totalTags, processingTime);
            } catch (Exception ex) {
               OseeLog.log(TaggerRunnable.class, Level.SEVERE, String.format("Error notifying listener: [%s] ",
                     listener.getClass().getName()), ex);
            }
         }
      }
   }

   public long getQueryIdProcessingTime() {
      return elapsedTime;
   }

   public int getTagQueueQueryId() {
      return tagQueueQueryId;
   }

   public Collection<SearchTag> getSearchTags() {
      return searchTags;
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
            if (currentTag.cacheSize() >= MAXIMUM_CACHED_TAGS) {
               try {
                  store(currentTag);
               } catch (SQLException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to store tags [%s]", currentTag), ex);
               }
            }
         }
      }
   }
}
