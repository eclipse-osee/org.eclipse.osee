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

import org.eclipse.osee.display.api.search.AsyncSearchListener;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class SearchExecutionCallback implements ExecutionCallback<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> {

   private final AsyncSearchListener callback;
   private final ArtifactProviderCache cache;

   public SearchExecutionCallback(ArtifactProviderCache cache, AsyncSearchListener callback) {
      this.cache = cache;
      this.callback = callback;
   }

   @Override
   public void onSuccess(ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> result) {
      cache.cacheResults(result);
      callback.onSearchComplete(result);
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