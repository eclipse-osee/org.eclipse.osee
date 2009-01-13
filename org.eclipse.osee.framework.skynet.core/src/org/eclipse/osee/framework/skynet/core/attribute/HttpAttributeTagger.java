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
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.server.HttpUrlBuilder;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.db.connection.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.messaging.event.skynet.event.SkynetAttributeChange;
import org.eclipse.osee.framework.skynet.core.SkynetActivator;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.event.ArtifactModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.ArtifactTransactionModifiedEvent;
import org.eclipse.osee.framework.skynet.core.event.BranchEventType;
import org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener;
import org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener;
import org.eclipse.osee.framework.skynet.core.event.IBranchEventListener;
import org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener;
import org.eclipse.osee.framework.skynet.core.event.ITransactionsDeletedEventListener;
import org.eclipse.osee.framework.skynet.core.event.OseeEventManager;
import org.eclipse.osee.framework.skynet.core.event.Sender;
import org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts;

/**
 * @author Roberto E. Escobar
 */
public class HttpAttributeTagger {
   private static final HttpAttributeTagger instance = new HttpAttributeTagger();
   private static final String XML_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><AttributeTag>";
   private static final String XML_FINISH = "</AttributeTag>";
   private static final String PREFIX = "<entry gammaId=\"";
   private static final String POSTFIX = "\"/>\n";
   private final ExecutorService executor;
   private final EventRelay eventRelay;

   private HttpAttributeTagger() {
      this.executor = Executors.newSingleThreadExecutor();
      this.eventRelay = new EventRelay();
      OseeEventManager.addListener(eventRelay);
   }

   public static HttpAttributeTagger getInstance() {
      return instance;
   }

   public void deregisterFromEventManager() {
      OseeEventManager.removeListener(eventRelay);
   }

   private final class TagService implements Runnable {
      private final Set<Integer> changedGammas;

      public TagService() {
         this.changedGammas = new HashSet<Integer>();
      }

      public void add(int attributeGammaId) {
         changedGammas.add(attributeGammaId);
      }

      public int size() {
         return changedGammas.size();
      }

      public void run() {
         long start = System.currentTimeMillis();
         StringBuffer response = new StringBuffer();
         ByteArrayInputStream inputStream = null;
         try {
            Map<String, String> parameters = new HashMap<String, String>();
            parameters.put("sessionId", ClientSessionManager.getSessionId());
            if (SkynetDbInit.isDbInit()) {
               parameters.put("wait", "true");
            }
            StringBuilder payload = new StringBuilder(XML_START);
            for (int data : changedGammas) {
               payload.append(PREFIX);
               payload.append(data);
               payload.append(POSTFIX);
            }
            payload.append(XML_FINISH);

            inputStream = new ByteArrayInputStream(payload.toString().getBytes("UTF-8"));
            String url =
                  HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SEARCH_TAGGING_CONTEXT,
                        parameters);
            response.append(HttpProcessor.put(new URL(url), inputStream, "application/xml", "UTF-8"));
            OseeLog.log(SkynetActivator.class, Level.FINEST, String.format("Transmitted to Tagger in [%d ms]",
                  System.currentTimeMillis() - start));
         } catch (Exception ex) {
            if (response.length() > 0) {
               response.append("\n");
            }
            response.append(ex.getLocalizedMessage());
            OseeLog.log(SkynetActivator.class, Level.SEVERE, response.toString(), ex);
         } finally {
            changedGammas.clear();
            if (inputStream != null) {
               try {
                  inputStream.close();
               } catch (IOException ex) {
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, ex.toString(), ex);
               }
            }
         }
      }
   }

   private final class EventRelay implements IFrameworkTransactionEventListener, IBranchEventListener, IArtifactsPurgedEventListener, IArtifactsChangeTypeEventListener, ITransactionsDeletedEventListener {
      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.event.IBranchEventListener#handleBranchEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.BranchEventType, int)
       */
      @Override
      public void handleBranchEvent(Sender sender, BranchEventType branchModType, int branchId) {
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.event.IBranchEventListener#handleLocalBranchToArtifactCacheUpdateEvent(org.eclipse.osee.framework.skynet.core.event.Sender)
       */
      @Override
      public void handleLocalBranchToArtifactCacheUpdateEvent(Sender sender) {
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsPurgedEventListener#handleArtifactsPurgedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
       */
      @Override
      public void handleArtifactsPurgedEvent(Sender sender, LoadedArtifacts loadedArtifacts) {
         //         if (sender.isRemote()) {
         //            return;
         //         }
         //         try {
         //            loadedArtifacts.
         //            //TODO: implements
         //            //            Map<String, String> parameters = new HashMap<String, String>();
         //            //            parameters.put("sessionId", ClientSessionManager.getSessionId());
         //            //            parameters.put("queryId", Integer.toString(transactionJoinId));
         //            //            String url =
         //            //                  HttpUrlBuilder.getInstance().getOsgiServletServiceUrl(OseeServerContext.SEARCH_TAGGING_CONTEXT,
         //            //                        parameters);
         //            //            String response = HttpProcessor.delete(new URL(url));
         //
         //         } catch (Exception ex) {
         //            OseeLog.log(SkynetActivator.class, Level.WARNING, "Error Deleting Tags during purge.", ex);
         //         }
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.event.IArtifactsChangeTypeEventListener#handleArtifactsChangeTypeEvent(org.eclipse.osee.framework.skynet.core.event.Sender, int, org.eclipse.osee.framework.skynet.core.utility.LoadedArtifacts)
       */
      @Override
      public void handleArtifactsChangeTypeEvent(Sender sender, int toArtifactTypeId, LoadedArtifacts loadedArtifacts) {
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.event.ITransactionsDeletedEventListener#handleTransactionsDeletedEvent(org.eclipse.osee.framework.skynet.core.event.Sender, int[])
       */
      @Override
      public void handleTransactionsDeletedEvent(Sender sender, int[] transactionIds) {
      }

      /* (non-Javadoc)
       * @see org.eclipse.osee.framework.skynet.core.event.IFrameworkTransactionEventListener#handleFrameworkTransactionEvent(org.eclipse.osee.framework.skynet.core.event.Sender, org.eclipse.osee.framework.skynet.core.event.FrameworkTransactionData)
       */
      @Override
      public void handleFrameworkTransactionEvent(Sender sender, FrameworkTransactionData txData) throws OseeCoreException {
         if (sender.isRemote()) {
            return;
         }
         TagService taggingInfo = new TagService();
         for (ArtifactTransactionModifiedEvent event : txData.getXModifiedEvents()) {
            if (event instanceof ArtifactModifiedEvent) {
               for (SkynetAttributeChange change : ((ArtifactModifiedEvent) event).getAttributeChanges()) {
                  if (AttributeTypeManager.getType(change.getTypeId()).isTaggable()) {
                     taggingInfo.add(change.getGammaId());
                  }
               }
            }
         }
         if (taggingInfo.size() > 0) {
            Future<?> future = executor.submit(taggingInfo);
            if (SkynetDbInit.isDbInit()) {
               try {
                  future.get();
               } catch (Exception ex) {
                  OseeLog.log(SkynetActivator.class, Level.SEVERE, "Error while waiting for tagger to complete.", ex);
               }
            }
         }
      }
   }
}
