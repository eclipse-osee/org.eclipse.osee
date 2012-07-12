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
 * @author John R. Misinco
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

   /**
    * @param alt The return data format. Possible values:
    * <ul>
    * <li>xml
    * <li>text (not supported)
    * <li>json (not supported)
    * <li>xhtml (not supported)
    * </ul>
    * @param fields What to do with the query. For example, once the search has produced a result set, what aspect of
    * the result set should be returned? Possible values:
    * <ul>
    * <li>count = Return an integer value counting the number of items returned by the query. Note that none of the
    * artifacts or attributes of the search result will be returned to the client. Only a count of the search result
    * items.
    * </ul>
    * @param rawQuery A query string with a custom syntax (see below). The query string syntax must be properly <a
    * href="http://en.wikipedia.org/wiki/Percent-encoding">URL-encoded</a>
    * @param fromTransaction (Optional) Transaction ID for a historical context. Use this parameter if you want the
    * search results to represent a specific point in time in the past. Without this parameter search results will
    * reflect the most current state of the data.
    * @param includeTypeInheritance (Optional) Boolean parameter that configures the search to use hiearchical type
    * inheritance if possible.
    * @param includeCache (Optional) Boolean parameter that configures the search to ???
    * @param includeDeleted (Optional) Boolean parameter that configures the search to include deleted artifacts in its
    * result set and analysis.
    * @return A {@link SearchResult} object containing the results of the search, configuration and analysis
    * information.
    * @throws OseeCoreException<br>
    * <br>
    * <b>rawQuery Syntax</b><br>
    * The query syntax is composed of one or more predicates - atomic search instructions - that can be appended
    * together as compound AND or OR query statements. Each predicate is surrounded by square brackets '[' and ']' which
    * makes writing a formal definitoin of the syntax a little confusing since the square bracket traditionally
    * surrounds optional items in a syntax. Because of this please note in the formal definition below literal square
    * brackets - those which belong as actual characters in the syntax - are escaped with a back slash character '\'.<br>
    * <b>Example Request String</b><br>
    * <FONT FACE="Courier New">
    * http://localhost:8089/oseex/branch/AyH_fDpMERA+zDfML4gA/artifact/search/v1?alt=xml&fields=
    * count&q=[t:attrType%26tp: 123%26op:==%26v:Smith]%26[t:attrType%26tp:456%26op:==%26v:John]</FONT><br>
    * <i>Note: "%26" = "&"</i>
    * <ul>
    * <li><b>http://localhost:8089/oseex/branch/AyH_fDpMERA+zDfML4gA/artifact/search/v1</b> ==> URL address
    * <li><b>alt=xml</b> ==> Search results will be formatted as XML
    * <li><b>fields=count</b> ==> Result returned will be a count of the search results
    * <li><b>q=[...]%26[...]</b> ==> The query string. In this example there are two search predicates "AND"-ed
    * together.
    * <li><b>t:attrType%26tp: 123%26op:==%26v:Smith</b> ==> Presumably "123" is the UUID for the "Last Name" attribute
    * type. This predicate can be translated into English as
    * <b>"Find all the artifacts with a <i>Last Name</i> <i>attribute</i> that equals <i>'Smith'</i>."</b>
    * <li><b>t:attrType%26tp:456%26op:==%26v:John</b> ==> Presumably "456" is the UUID for the "First Name" attribute
    * type. <b>"Find all the artifacts with a <i>First Name</i> <i>attribute</i> that equals <i>'John'</i>."</b>
    * </ul>
    */
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
