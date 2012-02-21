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
package org.eclipse.osee.orcs.rest.internal.search;

import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.rest.internal.OrcsApplication;
import org.eclipse.osee.orcs.rest.internal.search.dsl.DslTranslatorImpl;
import org.eclipse.osee.orcs.rest.internal.search.dsl.PredicateHandlerFactory;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchDsl;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchDsl.DslTranslator;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchMethod;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author John Misinco
 * @author Roberto E. Escobar
 */
public class ArtifactSearch_V1 extends ArtifactSearch {

   private final SearchDsl dsl;

   public ArtifactSearch_V1(UriInfo uriInfo, Request request, String branchUuid) {
      super(uriInfo, request, branchUuid);

      Map<SearchMethod, PredicateHandler> handlers = PredicateHandlerFactory.getHandlers();
      DslTranslator translator = new DslTranslatorImpl();
      // Can have a single instance of this
      dsl = new SearchDsl(handlers, translator);
   }

   @GET
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public SearchResult getSearchWithQueryParams(@QueryParam("alt") String alt, @QueryParam("fields") String fields, @QueryParam("q") String rawQuery, @QueryParam("fromTx") int fromTransaction, @QueryParam("inherits") boolean includeTypeInheritance, @QueryParam("cached") boolean includeCache, @QueryParam("includeDeleted") boolean includeDeleted) throws OseeCoreException {
      return search(alt, fields, rawQuery, fromTransaction, includeTypeInheritance, includeCache, includeDeleted);
   }

   //   @GET
   //   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   //   public SearchResult getSearchWithMatrixParams(@MatrixParam("alt") String alt, @MatrixParam("fields") String fields, @MatrixParam("q") String rawQuery, @MatrixParam("fromTx") int fromTransaction, @MatrixParam("inherits") boolean includeTypeInheritance, @MatrixParam("cached") boolean includeCache, @MatrixParam("includeDeleted") boolean includeDeleted) throws OseeCoreException {
   //      return search(alt, fields, rawQuery, fromTransaction, includeTypeInheritance, includeCache, includeDeleted);
   //   }

   private SearchResult search(String alt, String fields, String rawQuery, int fromTransaction, boolean includeTypeInheritance, boolean includeCache, boolean includeDeleted) throws OseeCoreException {
      long startTime = System.currentTimeMillis();

      IOseeBranch branch = TokenFactory.createBranch(getBranchUuid(), "searchBranch");

      QueryFactory qFactory = OrcsApplication.getOrcsApi().getQueryFactory(null); // Fix this

      QueryBuilder builder = dsl.build(qFactory, branch, rawQuery);

      builder.includeCache(includeCache);
      builder.includeTypeInheritance(includeTypeInheritance);
      builder.includeDeleted(includeDeleted);

      if (fromTransaction > 0) {
         builder.fromTransaction(fromTransaction);
      }

      SearchResult result = new SearchResult();
      SearchParameters params = new SearchParameters(getBranchUuid(), rawQuery, alt, fields);
      result.setPredicates(dsl.getPredicates());
      result.setSearchParams(params);
      if (fields.equals("count")) {
         int total = builder.getCount();
         result.setTotal(total);

      } else {
         //         builder.createSearch();
         //         builder.createSearchWithMatches();
         throw new UnsupportedOperationException();
      }
      result.setSearchTime(System.currentTimeMillis() - startTime);
      return result;
   }
}
