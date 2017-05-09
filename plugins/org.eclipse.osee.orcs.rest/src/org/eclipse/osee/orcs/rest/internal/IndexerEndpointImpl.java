/*******************************************************************************
 * Copyright (c) 2015 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.asResponse;
import static org.eclipse.osee.orcs.rest.internal.OrcsRestUtil.executeCallable;
import com.google.common.collect.Sets;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.jaxrs.OseeWebApplicationException;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.BranchReadable;
import org.eclipse.osee.orcs.rest.model.IndexResources;
import org.eclipse.osee.orcs.rest.model.IndexerEndpoint;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.QueryIndexer;

/**
 * @author Roberto E. Escobar
 */
public class IndexerEndpointImpl implements IndexerEndpoint {

   private final OrcsApi orcsApi;

   public IndexerEndpointImpl(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   private QueryIndexer getIndexer() {
      return orcsApi.getQueryIndexer();
   }

   private QueryFactory newQuery() {
      return orcsApi.getQueryFactory();
   }

   private BranchQuery newBranchQuery() {
      return newQuery().branchQuery();
   }

   @Override
   public Response indexBranches(String branchIdsStr, boolean missingItemsOnly) {
      List<BranchId> branchIds = Collections.fromString(branchIdsStr, BranchId::valueOf);
      ResultSet<BranchReadable> results = newBranchQuery().andIds(branchIds).getResults();
      Callable<Integer> op = getIndexer().indexBranches(Sets.newLinkedHashSet(results), missingItemsOnly);
      Integer result = executeCallable(op);
      boolean modified = result > 0;
      return asResponse(modified);
   }

   @Override
   public Response indexResources(IndexResources options) {
      boolean modified = false;
      if (!options.getGammaIds().isEmpty()) {
         Callable<List<Future<?>>> op = getIndexer().indexResources(options.getGammaIds());
         List<Future<?>> futures = executeCallable(op);
         modified = !futures.isEmpty();
         if (options.isWaitForIndexerToComplete()) {
            for (Future<?> future : futures) {
               try {
                  future.get(1, TimeUnit.MINUTES);
               } catch (Exception ex) {
                  throw new OseeWebApplicationException(ex, Status.INTERNAL_SERVER_ERROR,
                     "Error processing indexing operation [%s]", options);
               }
            }
         }
      }
      return asResponse(modified);
   }

   @Override
   public Response deleteIndexQueueItem(int queryId) {
      Callable<Integer> op = getIndexer().deleteIndexByQueryId(queryId);
      Integer result = executeCallable(op);
      boolean modified = result > 0;
      return asResponse(modified);
   }

   @Override
   public Response deleteIndexQueue() {
      Callable<Integer> op = getIndexer().purgeAllIndexes();
      Integer result = executeCallable(op);
      boolean modified = result > 0;
      return asResponse(modified);
   }

}
