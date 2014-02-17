/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.framework.manager.servlet.internal;

import java.net.URI;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.util.HttpMessage;
import org.eclipse.osee.framework.core.util.HttpProcessor;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.logger.Log;

/**
 * TODO: Temporary class used for backward server compatibility need to remove this and update servers using either
 * distributed cache or JMS
 * 
 * @author Roberto E. Escobar
 */
public class ServerBranchUpdateNotifier {

   private final Log logger;
   private final IDataTranslationService translationService;
   private final IApplicationServerLookup lookupService;
   private final IApplicationServerManager manager;
   private final ExecutorAdmin executor;

   private final Collection<Branch> branches;

   public ServerBranchUpdateNotifier(Log logger, IDataTranslationService translationService, IApplicationServerManager manager, IApplicationServerLookup lookUpProvider, Collection<Branch> branches, ExecutorAdmin executor) {
      this.logger = logger;
      this.translationService = translationService;
      this.lookupService = lookUpProvider;
      this.manager = manager;
      this.branches = branches;
      this.executor = executor;
   }

   public void notifyServers() throws Exception {
      logger.trace("Sending server update event for [%s]", branches);

      BranchCacheStoreRequest request = BranchCacheStoreRequest.fromCache(branches);
      request.setServerUpdateMessage(true);

      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", CacheOperation.STORE.name());

      List<Future<Void>> futures = new LinkedList<Future<Void>>();

      for (URI serverUri : lookupService.getAvailableServerUris()) {
         if (!manager.getServerUri().equals(serverUri)) {
            ServerUpdateWorker worker = new ServerUpdateWorker(serverUri, parameters, request);
            Future<Void> future = executor.schedule(worker);
            futures.add(future);
         }
      }

      // wait for all requests to finish
      for (Future<Void> future : futures) {
         future.get();
      }
   }

   private final class ServerUpdateWorker implements Callable<Void> {

      private final URI serverUri;
      private final Map<String, String> parameters;
      private final BranchCacheStoreRequest request;

      public ServerUpdateWorker(URI serverUri, Map<String, String> parameters, BranchCacheStoreRequest request) {
         this.serverUri = serverUri;
         this.parameters = parameters;
         this.request = request;
      }

      @Override
      public Void call() throws Exception {
         if (HttpProcessor.isAlive(serverUri)) {
            try {
               String urlString = HttpUrlBuilder.createURL(serverUri, OseeServerContext.CACHE_CONTEXT, parameters);

               AcquireResult updateResponse =
                  HttpMessage.send(urlString, translationService, CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST, request,
                     null);
               if (!updateResponse.wasSuccessful()) {
                  logger.error("Unsuccessful response when relaying branch updates to %s", serverUri);
               }
            } catch (Exception ex) {
               logger.error(ex, "Error relaying branch updates to %s", serverUri);
            }
         }
         return null;
      }
   }
}
