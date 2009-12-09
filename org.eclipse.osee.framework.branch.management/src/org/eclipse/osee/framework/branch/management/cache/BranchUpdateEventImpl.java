/*
 * Created on Dec 9, 2009
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.framework.branch.management.cache;

import java.io.UnsupportedEncodingException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import org.eclipse.osee.framework.branch.management.IBranchUpdateEvent;
import org.eclipse.osee.framework.branch.management.internal.Activator;
import org.eclipse.osee.framework.core.data.BranchCacheStoreRequest;
import org.eclipse.osee.framework.core.data.OseeServerContext;
import org.eclipse.osee.framework.core.data.OseeServerInfo;
import org.eclipse.osee.framework.core.enums.CacheOperation;
import org.eclipse.osee.framework.core.enums.CoreTranslatorId;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.model.Branch;
import org.eclipse.osee.framework.core.server.IApplicationServerLookupProvider;
import org.eclipse.osee.framework.core.services.IDataTranslationServiceProvider;
import org.eclipse.osee.framework.core.util.HttpMessage;
import org.eclipse.osee.framework.jdk.core.util.HttpUrlBuilder;
import org.eclipse.osee.framework.jdk.core.util.HttpProcessor.AcquireResult;
import org.eclipse.osee.framework.logging.OseeLog;

/**
 * @author Roberto E. Escobar
 */
public class BranchUpdateEventImpl implements IBranchUpdateEvent {

   private final IDataTranslationServiceProvider txProvider;
   private final IApplicationServerLookupProvider lookUpProvider;

   public BranchUpdateEventImpl(IDataTranslationServiceProvider translationService, IApplicationServerLookupProvider lookUpProvider) {
      super();
      this.txProvider = translationService;
      this.lookUpProvider = lookUpProvider;
   }

   public void send(Collection<Branch> branches) throws OseeCoreException {
      BranchCacheStoreRequest request = BranchCacheStoreRequest.fromCache(branches);
      request.setServerUpdateMessage(true);

      Map<String, String> parameters = new HashMap<String, String>();
      parameters.put("function", CacheOperation.STORE.name());

      for (OseeServerInfo serverInfo : lookUpProvider.getApplicationServerLookupService().getAvailableServers()) {
         try {
            String urlString =
                  HttpUrlBuilder.createURL(serverInfo.getServerAddress(), serverInfo.getPort(),
                        OseeServerContext.CACHE_CONTEXT, parameters);
            AcquireResult updateResponse =
                  HttpMessage.send(urlString, txProvider.getTranslationService(),
                        CoreTranslatorId.BRANCH_CACHE_STORE_REQUEST, request, null);
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
