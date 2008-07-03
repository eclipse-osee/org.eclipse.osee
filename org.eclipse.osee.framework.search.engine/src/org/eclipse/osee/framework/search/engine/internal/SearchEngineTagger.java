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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.search.engine.Activator;
import org.eclipse.osee.framework.search.engine.ISearchTagger;
import org.eclipse.osee.framework.search.engine.data.AttributeVersion;
import org.eclipse.osee.framework.search.engine.data.SearchTag;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineTagger implements ISearchTagger {
   private static final int MAXIMUM_CACHED_TAGS = 1000;
   private ExecutorService executor;

   public SearchEngineTagger() {
      this.executor = Executors.newSingleThreadExecutor();
   }

   public void submitForTagging(int attrId, long gammaId) {
      this.executor.execute(new TagRunnable(new AttributeVersion(attrId, gammaId)));
   }

   private final class TagRunnable implements Runnable, ITagCollector {
      private SearchTag searchTag;

      private TagRunnable(AttributeVersion attributeVersion) {
         this.searchTag = new SearchTag(attributeVersion);
      }

      /* (non-Javadoc)
       * @see java.lang.Runnable#run()
       */
      @Override
      public void run() {
         System.out.println("I must Tag: " + searchTag.toString());
         try {

            //         TagProcessor.collectFromString(value, this);

            String path = "";

            IResourceLocator locator = Activator.getInstance().getResourceLocatorManager().getResourceLocator(path);
            Options options = new Options();
            options.put(StandardOptions.DecompressOnAquire.name(), true);

            IResource resource = Activator.getInstance().getResourceManager().acquire(locator, options);
            TagProcessor.collectFromInputStream(resource.getContent(), this);

            store();
         } catch (Exception ex) {
            OseeLog.log(Activator.class.getName(), Level.SEVERE, String.format("Unable to tag [%s]", searchTag), ex);
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
               SearchTagDataStore.storeTags(searchTag);
            } catch (SQLException ex) {
               OseeLog.log(Activator.class.getName(), Level.SEVERE, String.format("Unable to store tags [%s]",
                     searchTag), ex);
            }
         }
      }

      public void store() throws SQLException {
         SearchTagDataStore.storeTags(searchTag);
         searchTag.clear();
      }
   }
}
