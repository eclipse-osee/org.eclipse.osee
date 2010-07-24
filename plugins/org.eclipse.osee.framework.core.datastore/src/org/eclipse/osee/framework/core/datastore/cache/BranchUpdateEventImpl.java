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
package org.eclipse.osee.framework.core.datastore.cache;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.datastore.internal.Activator;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.enums.StorageState;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.message.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.message.BranchRow;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.server.IApplicationServerLookupProvider;
import org.eclipse.osee.framework.core.server.IApplicationServerManager;
import org.eclipse.osee.framework.core.translation.IDataTranslationServiceProvider;
import org.eclipse.osee.framework.core.util.HttpMessage;
import org.eclipse.osee.framework.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class BranchUpdateEventImpl implements IBranchUpdateEvent {

   private final IDataTranslationServiceProvider txProvider;
   private final IApplicationServerLookupProvider lookUpProvider;
   private final IApplicationServerManager manager;

   public BranchUpdateEventImpl(IDataTranslationServiceProvider translationService, IApplicationServerManager manager, IApplicationServerLookupProvider lookUpProvider) {
      super();
      this.txProvider = translationService;
      this.lookUpProvider = lookUpProvider;
      this.manager = manager;
   }

   public void send(final Collection<Branch> branches) throws OseeCoreException {
      List<Branch> branchToUpdate = new ArrayList<Branch>();
      for (Branch branch : branches) {
         if (!branch.isDirty()) {
            branchToUpdate.add(branch);
         }
      }
      Operations.executeAsJob(new ServerSyncOperation(branchToUpdate), false);
   }

   private final class ServerSyncOperation extends AbstractOperation {

      private final Collection<Branch> branches;

      public ServerSyncOperation(Collection<Branch> branches) {
         super("Server Sync", Activator.PLUGIN_ID);
         this.branches = branches;
      }

      private boolean is_0_9_2_Compatible(String... versions) {
         for (String version : versions) {
            if (Strings.isValid(version)) {
               String toCheck = version.toLowerCase();
               if (!toCheck.startsWith("0.9.0") && !toCheck.startsWith("0.9.1")) {
                  return true;
               }
            }
         }
         return false;
      }

      private StorageState getCompatibleState(StorageState state) {
         StorageState toReturn = state;
         if (state == StorageState.PURGED) {
            toReturn = StorageState.DELETED;
         } else if (state == StorageState.LOADED) {
            toReturn = StorageState.MODIFIED;
         }
         return toReturn;
      }

      @Override
      protected void doWork(IProgressMonitor monitor) throws Exception {
         BranchCacheStoreRequest request = BranchCacheStoreRequest.fromCache(branches);
         request.setServerUpdateMessage(true);

         BranchCacheStoreRequest request0_9_1 = BranchCacheStoreRequest.fromCache(branches);
         request0_9_1.setServerUpdateMessage(true);
         for (BranchRow row : request0_9_1.getBranchRows()) {
            row.setStorageState(getCompatibleState(row.getStorageState()));
         }

         Map<String, String> parameters = new HashMap<String, String>();
         parameters.put("function", CacheOperation.STORE.name());

         for (OseeServerInfo serverInfo : lookUpProvider.getApplicationServerLookupService().getAvailableServers()) {
            if (!manager.getId().equals(serverInfo.getServerId()) && serverInfo.isAcceptingRequests()) {
               try {

                  String urlString =
                        HttpUrlBuilder.createURL(serverInfo.getServerAddress(), serverInfo.getPort(),
                              OseeServerContext.CACHE_CONTEXT, parameters);

                  BranchCacheStoreRequest message =
                        is_0_9_2_Compatible(serverInfo.getVersion()) ? request : request0_9_1;

                  AcquireResult updateResponse =
                        HttpMessage.send(urlString, txProvider.getTranslationService(),
                              CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST, message, null);
                  if (!updateResponse.wasSuccessful()) {
                     OseeLog.log(Activator.class, Level.SEVERE, "Error relaying branch updates to servers");
                  }
               } catch (OseeCoreException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Error relaying branch updates to servers", ex);
               } catch (UnsupportedEncodingException ex) {
                  OseeLog.log(Activator.class, Level.SEVERE, "Error relaying branch updates to servers", ex);
               }
            }
         }
      }
   };
}
