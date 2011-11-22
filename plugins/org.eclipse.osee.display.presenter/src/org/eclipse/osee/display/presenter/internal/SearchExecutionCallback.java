/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.display.presenter.internal;

import java.util.concurrent.CancellationException;
import org.eclipse.osee.display.api.search.AsyncSearchListener;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class SearchExecutionCallback implements ExecutionCallback<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> {

   private final AsyncSearchListener callback;
   private final ArtifactProviderCache cache;
   private final Log logger;

   public SearchExecutionCallback(Log logger, ArtifactProviderCache cache, AsyncSearchListener callback) {
      this.logger = logger;
      this.cache = cache;
      this.callback = callback;
   }

   @Override
   public void onSuccess(ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> result) {
      cache.cacheResults(result);
      try {
         callback.onSearchComplete(result.getList());
      } catch (OseeWrappedException ex) {
         if (ex.getCause() instanceof CancellationException) {
            callback.onSearchCancelled();
         }
      } catch (Exception ex) {
         logger.error(ex, "Error in async search");
      }
   }

   @Override
   public void onFailure(Throwable throwable) {
      cache.cacheSearch(null);
      callback.onSearchFailed(throwable);
   }

   @Override
   public void onCancelled() {
      cache.cacheSearch(null);
      callback.onSearchCancelled();
   }
}