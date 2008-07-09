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

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.search.engine.Activator;
import org.eclipse.osee.framework.search.engine.data.SearchTag;
import org.eclipse.osee.framework.search.engine.utility.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;
import org.eclipse.osee.framework.search.engine.utility.AttributeDataStore.AttributeData;

/**
 * @author Roberto E. Escobar
 */
class AttributeTagger implements Runnable, ITagCollector {
   private static final int MAXIMUM_CACHED_TAGS = 1000;

   private SearchTag searchTag;
   private Options options;

   protected AttributeTagger(int attrId, long gammaId) {
      this.searchTag = new SearchTag(attrId, gammaId);
      this.options = new Options();
      this.options.put(StandardOptions.DecompressOnAquire.name(), true);
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      System.out.println("Tagging: " + searchTag.toString());
      try {
         List<AttributeData> attributes = AttributeDataStore.getAttribute(searchTag);
         for (AttributeData attributeData : attributes) {
            // Tag String portion
            TagProcessor.collectFromString(attributeData.getValue(), this);

            // Tag Resource Portion
            if (attributeData.isUriValid()) {
               IResourceLocator locator =
                     Activator.getInstance().getResourceLocatorManager().getResourceLocator(attributeData.getUri());
               IResource resource = Activator.getInstance().getResourceManager().acquire(locator, options);

               InputStream inputStream = null;
               try {
                  inputStream = resource.getContent();
                  String mimeType = getContentType(resource, inputStream);
                  if (mimeType.contains("text")) {
                     // need to process word content so only string portion available
                     // other files ? 1025 8211
                  }
                  TagProcessor.collectFromInputStream(inputStream, this);
               } finally {
                  if (inputStream != null) {
                     inputStream.close();
                  }
               }
            }
         }
         store();
      } catch (Exception ex) {
         OseeLog.log(Activator.class.getName(), Level.SEVERE, String.format("Unable to tag [%s]", searchTag), ex);
      }
   }

   private String getContentType(IResource resource, InputStream inputStream) throws IOException {
      String mimeType = HttpURLConnection.guessContentTypeFromStream(inputStream);
      if (mimeType == null) {
         mimeType = HttpURLConnection.guessContentTypeFromName(resource.getLocation().toString());
         if (mimeType == null) {
            mimeType = "application/*";
         }
      }
      return mimeType;
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
      searchTag.clear();
   }
}
