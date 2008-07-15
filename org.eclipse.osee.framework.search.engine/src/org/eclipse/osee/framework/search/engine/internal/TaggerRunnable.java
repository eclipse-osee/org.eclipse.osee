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

   protected TaggerRunnable(long gammaId) {
      this.searchTag = null;
      this.gammaId = gammaId;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      long start = System.currentTimeMillis();
      try {
         AttributeData attributeData = AttributeDataStore.getInstance().getAttribute(gammaId);
         if (attributeData != null) {
            this.searchTag = new SearchTag(attributeData.getArtId(), attributeData.getGammaId());
            Activator.getInstance().getTaggerManager().tagIt(attributeData, this);
            store();
         }
      } catch (Exception ex) {
         OseeLog.log(Activator.class.getName(), Level.SEVERE, String.format("Unable to tag [%s]", searchTag), ex);
      } finally {
         OseeLog.log(TaggerRunnable.class, Level.INFO, String.format("Tagged: [%d] in [%d] ms", gammaId,
               System.currentTimeMillis() - start));
         if (searchTag != null) {
            searchTag.clear();
            searchTag = null;
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.utility.ITagCollector#addTag(java.lang.Long)
    */
   @Override
   public void addTag(Long codedTag) {
      searchTag.addTag(codedTag);
      if (searchTag.size() >= MAXIMUM_CACHED_TAGS) {
         try {
            store();
         } catch (SQLException ex) {
            OseeLog.log(Activator.class.getName(), Level.SEVERE, String.format("Unable to store tags [%s]", searchTag),
                  ex);
         }
      }
   }

   public void store() throws SQLException {
      SearchTagDataStore.storeTags(searchTag);
   }
}
