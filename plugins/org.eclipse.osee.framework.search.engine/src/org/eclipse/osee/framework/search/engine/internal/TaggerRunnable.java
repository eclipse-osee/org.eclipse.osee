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

import java.util.Collection;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeDataStoreException;
import org.eclipse.osee.framework.database.core.DbTransaction;
import org.eclipse.osee.framework.database.core.JoinUtility;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.framework.database.core.JoinUtility.JoinItem;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.ITagListener;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;
import org.eclipse.osee.framework.search.engine.data.SearchTag;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;

/**
 * @author Roberto E. Escobar
 */
class TaggerRunnable implements Runnable {
   private final Set<ITagListener> listeners;
   private final int tagQueueQueryId;
   private final boolean isCacheAll;
   private final int cacheLimit;
   private long processingTime;
   private final long waitStart;
   private long waitTime;

   TaggerRunnable(int tagQueueQueryId, boolean isCacheAll, int cacheLimit) {
      this.listeners = new HashSet<ITagListener>();
      this.tagQueueQueryId = tagQueueQueryId;
      this.waitStart = System.currentTimeMillis();
      this.waitTime = 0;
      this.processingTime = 0;
      this.cacheLimit = cacheLimit;
      this.isCacheAll = isCacheAll;
   }

   public int getTagQueueQueryId() {
      return tagQueueQueryId;
   }

   public void addListener(ITagListener listener) {
      if (listener != null) {
         this.listeners.add(listener);
      }
   }

   @Override
   public void run() {
      this.waitTime = System.currentTimeMillis() - this.waitStart;
      long processStart = System.currentTimeMillis();
      try {
         AttributeToTagTx attributeToTagTx = new AttributeToTagTx();
         attributeToTagTx.execute();
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to tag - tagQueueQueryId [%d]",
               getTagQueueQueryId()), ex);
      } finally {
         this.processingTime = System.currentTimeMillis() - processStart;
         notifyOnTagQueryIdTagComplete();
      }
      this.listeners.clear();
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

   private final class AttributeToTagTx extends DbTransaction implements ITagCollector {
      private static final int TOTAL_RETRIES = 10;
      private final Deque<SearchTag> searchTags;
      private SearchTag currentTag;

      public AttributeToTagTx() throws OseeCoreException {
         super();
         this.searchTags = new LinkedList<SearchTag>();
         this.currentTag = null;
      }

      private Collection<AttributeData> getDataFromQueryId(OseeConnection connection, int queryId, final int numberOfRetries) throws OseeDataStoreException {
         Collection<AttributeData> attributeDatas = AttributeDataStore.getAttribute(connection, getTagQueueQueryId());
         // Re-try in case query id hasn't been committed to the database
         int retry = 0;
         while (attributeDatas.isEmpty() && retry < numberOfRetries) {
            try {
               Thread.sleep(2000);
            } catch (InterruptedException ex) {
               OseeLog.log(Activator.class, Level.WARNING, ex);
            }
            attributeDatas = AttributeDataStore.getAttribute(connection, getTagQueueQueryId());
            retry++;
         }
         return attributeDatas;
      }

      @Override
      protected void handleTxWork(OseeConnection connection) throws OseeCoreException {
         Collection<AttributeData> attributeDatas = getDataFromQueryId(connection, getTagQueueQueryId(), TOTAL_RETRIES);
         if (!attributeDatas.isEmpty()) {
            try {
               SearchTagDataStore.deleteTags(connection,
                     attributeDatas.toArray(new IAttributeLocator[attributeDatas.size()]));
               for (AttributeData attributeData : attributeDatas) {
                  long startItemTime = System.currentTimeMillis();
                  this.currentTag = new SearchTag(attributeData.getGammaId());
                  this.searchTags.add(this.currentTag);
                  try {
                     Activator.getTaggerManager().tagIt(attributeData, this);
                     checkSizeStoreIfNeeded(connection);
                  } catch (Throwable ex) {
                     OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to tag - [%s]", this.currentTag));
                  } finally {
                     notifyOnAttributeTagComplete(this.currentTag.getGammaId(), this.currentTag.getTotalTags(),
                           (System.currentTimeMillis() - startItemTime));
                     this.currentTag = null;
                  }
               }
               store(connection, this.searchTags);
               JoinUtility.deleteQuery(connection, JoinItem.TAG_GAMMA_QUEUE, getTagQueueQueryId());
            } catch (Exception ex) {
               OseeLog.log(Activator.class, Level.SEVERE, String.format("Unable to store tags - tagQueueQueryId [%d]",
                     getTagQueueQueryId()), ex);
            }
         } else {
            System.out.println(String.format("Empty gamma query id: %s", getTagQueueQueryId()));
         }
      }

      @Override
      protected void handleTxFinally() throws OseeCoreException {
         super.handleTxFinally();
         for (SearchTag searchTag : this.searchTags) {
            searchTag.clearCache();
         }
      }

      @Override
      public void addTag(String word, Long codedTag) {
         if (this.currentTag != null) {
            this.currentTag.addTag(codedTag);
            notifyOnAttributeAddTagEvent(this.currentTag.getGammaId(), word, codedTag);
         }
      }

      private void checkSizeStoreIfNeeded(OseeConnection connection) throws OseeDataStoreException {
         int cummulative = 0;
         boolean needsStorage = false;
         for (SearchTag item : this.searchTags) {
            cummulative += item.cacheSize();
            if (isCacheAll != true && cummulative >= cacheLimit) {
               needsStorage = true;
               break;
            }
         }
         if (needsStorage) {
            store(connection, this.searchTags);
         }
      }

      private void store(OseeConnection connection, Collection<SearchTag> toStore) throws OseeDataStoreException {
         SearchTagDataStore.storeTags(connection, toStore);
         for (SearchTag item : toStore) {
            item.clearCache();
         }
      }
   }
}
