/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.client.internal.search;

import java.net.URI;
import java.util.List;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriBuilder;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.services.URIProvider;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.rest.model.search.artifact.OutputFormat;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResult;
import org.eclipse.osee.rest.client.WebClientProvider;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * @author John Misinco
 */
public class QueryExecutorV1 implements QueryExecutor {

   private final WebClientProvider clientProvider;
   private final URIProvider uriProvider;

   public QueryExecutorV1(URIProvider uriProvider, WebClientProvider clientProvider) {
      super();
      this.uriProvider = uriProvider;
      this.clientProvider = clientProvider;
   }

   @Override
   public int getCount(IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      SearchResponse result = performSearch(RequestType.COUNT, OutputFormat.XML, branch, predicates, options);
      return result.getTotal();
   }

   @Override
   public SearchResult getResults(RequestType request, IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      SearchResponse result = performSearch(request, OutputFormat.XML, branch, predicates, options);
      return result;
   }

   private SearchResponse performSearch(RequestType requestType, OutputFormat outputFormat, IOseeBranch branch, List<Predicate> predicates, QueryOptions options) throws OseeCoreException {
      int fromTx = 0;
      if (options.isHistorical()) {
         fromTx = options.getFromTransaction();
      }

      boolean includeDeleted = false;
      if (options.areDeletedIncluded()) {
         includeDeleted = true;
      }

      SearchRequest params =
         new SearchRequest(branch.getGuid(), predicates, outputFormat.name().toLowerCase(),
            requestType.name().toLowerCase(), fromTx, includeDeleted);

      UriBuilder path =
         UriBuilder.fromUri(uriProvider.getApplicationServerURI()).path("oseex/branch/{branch-uuid}/artifact/search/v1");
      URI uri = path.build(branch.getGuid());

      WebResource resource = clientProvider.createResource(uri);
      SearchResponse searchResult = null;
      try {
         searchResult =
            resource.accept(MediaType.APPLICATION_JSON_TYPE).type(MediaType.APPLICATION_JSON_TYPE).post(
               SearchResponse.class, params);
      } catch (UniformInterfaceException ex) {
         throw clientProvider.handleException(ex);
      }
      return searchResult;
   }
}
