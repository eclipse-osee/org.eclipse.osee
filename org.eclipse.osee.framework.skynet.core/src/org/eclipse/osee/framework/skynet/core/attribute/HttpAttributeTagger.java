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
package org.eclipse.osee.framework.skynet.core.attribute;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransactionEventCompleted;
import org.eclipse.osee.framework.db.connection.core.transaction.IDbTransactionEvent;
import org.eclipse.osee.framework.db.connection.core.transaction.IDbTransactionListener;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.jdk.core.util.OseeApplicationServerContext;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IAttributeSaveListener;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.linking.HttpUrlBuilder;

/**
 * @author Roberto E. Escobar
 */
public class HttpAttributeTagger implements IAttributeSaveListener, IDbTransactionListener {
   private static final HttpAttributeTagger instance = new HttpAttributeTagger();
   private static final String XML_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><AttributeTag>";
   private static final String XML_FINISH = "</AttributeTag>";
   private static final String PREFIX = "<entry gammaId=\"";
   private static final String POSTFIX = "\"/>\n";
   private final ExecutorService executor;
   private final StringBuffer taggingInfo;
   private int count;

   protected HttpAttributeTagger() {
      this.taggingInfo = new StringBuffer();
      this.executor = Executors.newSingleThreadExecutor();
      this.count = 0;
      ConnectionHandler.addDbTransactionListener(this);
   }

   public static HttpAttributeTagger getInstance() {
      return instance;
   }

   private void addAttributeGammaForTagging(int attributeGammaId) {
      this.taggingInfo.append(PREFIX);
      this.taggingInfo.append(attributeGammaId);
      this.taggingInfo.append(POSTFIX);
      this.count++;
   }

   private void sendToTagger(boolean isCommitted) {
      if (this.taggingInfo.length() > 0) {
         if (isCommitted) {
            Future<?> future = this.executor.submit(new TagService(taggingInfo.toString()));
            if (SkynetDbInit.isDbInit()) {
               try {
                  future.get();
               } catch (Exception ex) {
                  OseeLog.log(TagService.class, Level.SEVERE, "Error while waiting for tagger to complete.", ex);
               }
            }
         }
         this.taggingInfo.delete(0, taggingInfo.length());
         this.count = 0;
      }
   }

   public int getGammaQueueSize() {
      return count;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IAttributeSaveListener#notifyOnAttributeSave(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public void notifyOnAttributeSave(Artifact artifact) throws Exception {
      List<Attribute<?>> attributes = artifact.getAttributes(false);
      for (Attribute<?> attribute : attributes) {
         if (attribute.isDirty() && attribute.getAttributeType().isTaggable()) {
            addAttributeGammaForTagging(attribute.getGammaId());
         }
      }
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.db.connection.core.transaction.IDbTransactionListener#onEvent(org.eclipse.osee.framework.db.connection.core.transaction.IDbTransactionEvent)
    */
   @Override
   public void onEvent(IDbTransactionEvent event) {
      if (event instanceof DbTransactionEventCompleted) {
         DbTransactionEventCompleted eventCompleted = (DbTransactionEventCompleted) event;
         sendToTagger(eventCompleted.isCommitted());
      }
   }

   private final class TagService implements Runnable {
      private String toSend;

      public TagService(String toSend) {
         this.toSend = toSend;
      }

      public void run() {
         long start = System.currentTimeMillis();
         StringBuffer response = new StringBuffer();
         ByteArrayInputStream inputStream = null;
         try {
            Map<String, String> parameters = new HashMap<String, String>();
            if (SkynetDbInit.isDbInit()) {
               parameters.put("wait", "true");
            }
            StringBuffer payload = new StringBuffer();
            payload.append(XML_START);
            payload.append(toSend);
            payload.append(XML_FINISH);

            inputStream = new ByteArrayInputStream(payload.toString().getBytes("UTF-8"));
            String url =
                  HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(
                        OseeApplicationServerContext.SEARCH_TAGGING_CONTEXT, parameters);
            response.append(HttpProcessor.put(new URL(url), inputStream, "application/xml", "UTF-8"));
            OseeLog.log(TagService.class, Level.FINEST, String.format("Transmitted to Tagger in [%d ms]",
                  System.currentTimeMillis() - start));
         } catch (Exception ex) {
            if (response.length() > 0) {
               response.append("\n");
            }
            response.append(ex.getLocalizedMessage());
            OseeLog.log(TagService.class, Level.SEVERE, response.toString(), ex);
         } finally {
            this.toSend = null;
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (IOException ex) {
                  OseeLog.log(TagService.class, Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      }
   }
}
