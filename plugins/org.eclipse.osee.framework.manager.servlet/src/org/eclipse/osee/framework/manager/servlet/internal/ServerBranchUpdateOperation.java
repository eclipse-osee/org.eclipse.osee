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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.message.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.server.IApplicationServerLookup;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.translation.IDataTranslationService;
import org.eclipse.osee.framework.core.util.HttpMessage;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.logger.Log;

/**
 * TODO: Temporary class used for backward server compatibility need to remove this and update servers using either
 * distributed cache or JMS
 * 
 * @author Roberto E. Escobar
 */
public class ServerBranchUpdateOperation extends AbstractOperation {

   private final Log logger;
   private final IDataTranslationService translationService;
   private final IApplicationServerLookup lookupService;
   private final IApplicationServerManager manager;

   private final Collection<Branch> branches;

   public ServerBranchUpdateOperation(Log logger, IDataTranslationService translationService, IApplicationServerManager manager, IApplicationServerLookup lookUpProvider, Collection<Branch> branches) {
      super("Server Sync", "org.eclipse.osee.framework.manager.servlet");
      this.logger = logger;
      this.translationService = translationService;
      this.lookupService = lookUpProvider;
      this.manager = manager;
      this.branches = branches;
   }

   @Override
   protected void doWork(IProgressMonitor monitor) throws Exception {
      logger.trace("Sending server update event for [%s]", branches);

      BranchCacheStoreRequest request = BranchCacheStoreRequest.fromCache(branches);
      request.setServerUpdateMessage(true);

      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", CacheOperation.STORE.name());

      for (OseeServerInfo serverInfo : lookupService.getAvailableServers()) {
         if (!manager.getId().equals(serverInfo.getServerId()) && serverInfo.isAcceptingRequests()) {
            try {
               String urlString =
                  HttpUrlBuilder.createURL(serverInfo.getUri(), OseeServerContext.CACHE_CONTEXT, parameters);

               AcquireResult updateResponse =
                  HttpMessage.send(urlString, translationService, CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST, request,
                     null);
               if (!updateResponse.wasSuccessful()) {
                  logger.error("Error relaying branch updates to servers");
               }
            } catch (Exception ex) {
               logger.error(ex, "Error relaying branch updates to servers");
            }
         }
      }
   }
}
