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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.xml.Jaxp;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.resource.management.IResource;
import org.eclipse.osee.framework.resource.management.IResourceLocator;
import org.eclipse.osee.framework.resource.management.Options;
import org.eclipse.osee.framework.resource.management.StandardOptions;
import org.eclipse.osee.framework.search.engine.Activator;
import org.eclipse.osee.framework.search.engine.ISearchTagger;
import org.eclipse.osee.framework.search.engine.data.SearchTag;
import org.eclipse.osee.framework.search.engine.utility.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;
import org.eclipse.osee.framework.search.engine.utility.AttributeDataStore.AttributeData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngineTagger implements ISearchTagger {
   private static final int MAXIMUM_CACHED_TAGS = 1000;
   private ExecutorService executor;

   public SearchEngineTagger() {
      this.executor = Executors.newSingleThreadExecutor();
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchTagger#tagAttribute(int, long)
    */
   @Override
   public void tagAttribute(int attrId, long gammaId) {
      this.executor.execute(new TagRunnable(attrId, gammaId));
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchTagger#tagFromXmlStream(java.io.InputStream)
    */
   @Override
   public void tagFromXmlStream(InputStream inputStream) {
      try {
         Document document = Jaxp.readXmlDocument(inputStream);
         List<Element> elements = Jaxp.getChildDirects(document.getDocumentElement(), "attribute");
         for (Element element : elements) {
            String attrId = element.getAttribute("attrId");
            String gammaId = element.getAttribute("gammaId");
            if (Strings.isValid(attrId) && Strings.isValid(gammaId)) {
               tagAttribute(Integer.parseInt(attrId), Long.parseLong(gammaId));
            }
         }
      } catch (Exception ex) {
      }
   }
   private final class TagRunnable implements Runnable, ITagCollector {
      private SearchTag searchTag;
      private Options options;

      private TagRunnable(int attrId, long gammaId) {
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
