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

import java.sql.SQLException;
import java.util.logging.Level;
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
class TaggerRunnable implements Runnable, ITagCollector {
   private static final int MAXIMUM_CACHED_TAGS = 1000;

   private SearchTag searchTag;
   private long gammaId;
   private int tagCount;
   private long elapsedTime;
   private ITagListener listener;

   protected TaggerRunnable(ITagListener listener, long gammaId) {
      this.listener = listener;
      this.searchTag = null;
      this.gammaId = gammaId;
      this.tagCount = 0;
      this.elapsedTime = 0;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      long start = System.currentTimeMillis();
      try {
         AttributeData attributeData = AttributeDataStore.getAttribute(gammaId);
         if (attributeData != null) {
            this.searchTag = new SearchTag(attributeData.getArtId(), attributeData.getGammaId());
            Activator.getInstance().getTaggerManager().tagIt(attributeData, this);
            store();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class.getName(), Level.SEVERE, String.format("Unable to tag [%s]", searchTag), ex);
      } finally {
         this.elapsedTime = System.currentTimeMillis() - start;
         if (searchTag != null) {
            searchTag.clear();
            searchTag = null;
         }
         if (listener != null) {
            listener.onComplete(getGammaId());
         }
      }
   }

   public long getProcessingTime() {
      return elapsedTime;
   }

   public int getTotalTags() {
      return tagCount;
   }

   public long getGammaId() {
      return gammaId;
   }

   public void store() throws SQLException {
      SearchTagDataStore.storeTags(searchTag);
      searchTag.clear();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.utility.ITagCollector#addTag(java.lang.String, java.lang.Long)
    */
   @Override
   public void addTag(String word, Long codedTag) {
      searchTag.addTag(codedTag);
      tagCount++;
      if (searchTag.size() >= MAXIMUM_CACHED_TAGS) {
         try {
            store();
         } catch (SQLException ex) {
            OseeLog.log(Activator.class.getName(), Level.SEVERE, String.format("Unable to store tags [%s]", searchTag),
                  ex);
         }
      }
   }
}
