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
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.core.transaction.DbTransactionEventCompleted;
import org.eclipse.osee.framework.db.connection.core.transaction.IDbTransactionEvent;
import org.eclipse.osee.framework.db.connection.core.transaction.IDbTransactionListener;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.IAttributeSaveListener;
import org.eclipse.osee.framework.skynet.core.linking.HttpProcessor;
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
   private ExecutorService executor;
   private StringBuffer taggingInfo;

   private HttpAttributeTagger() {
      this.taggingInfo = new StringBuffer();
      this.executor = Executors.newSingleThreadExecutor();
   }

   public static HttpAttributeTagger getInstance() {
      return instance;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.skynet.core.artifact.IAttributeSaveListener#notifyOnAttributeSave(org.eclipse.osee.framework.skynet.core.artifact.Artifact)
    */
   @Override
   public void notifyOnAttributeSave(Artifact artifact) throws Exception {
      List<Attribute<?>> attributes = artifact.getAttributes();
      for (Attribute<?> attribute : attributes) {
         if (attribute.isDirty() && attribute.getAttributeType().isTaggable()) {
            this.taggingInfo.append(PREFIX);
            this.taggingInfo.append(attribute.getGammaId());
            this.taggingInfo.append(POSTFIX);
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
         if (this.taggingInfo.length() > 0) {
            if (eventCompleted.isCommitted()) {
               this.executor.submit(new TagService(taggingInfo.toString()));
            }
            this.taggingInfo.delete(0, taggingInfo.length());
         }
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

            StringBuffer payload = new StringBuffer();
            payload.append(XML_START);
            payload.append(toSend);
            payload.append(XML_FINISH);

            inputStream = new ByteArrayInputStream(payload.toString().getBytes("UTF-8"));
            String url = HttpUrlBuilder.getInstance().getOsgiServletServiceUrl("search", parameters);
            response.append(HttpProcessor.put(new URL(url), inputStream, "application/xml", "UTF-8"));
            OseeLog.log(TagService.class, Level.INFO, String.format("Transmitted to Tagger in [%d ms]",
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
