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

import java.util.LinkedList;
import java.util.List;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Request;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.rest.internal.OrcsApplication;
import org.eclipse.osee.orcs.rest.internal.search.dsl.DslFactory;
import org.eclipse.osee.orcs.rest.internal.search.dsl.DslTranslator;
import org.eclipse.osee.orcs.rest.internal.search.dsl.SearchQueryBuilder;
import org.eclipse.osee.orcs.rest.model.search.RequestType;
import org.eclipse.osee.orcs.rest.model.search.SearchMatch;
import org.eclipse.osee.orcs.rest.model.search.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.SearchResponse;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author John R. Misinco
 * @author Roberto E. Escobar
 */
public class ArtifactSearch_V1 extends ArtifactSearch {

   private final SearchQueryBuilder searchQueryBuilder;

   public ArtifactSearch_V1(UriInfo uriInfo, Request request, String branchUuid) {
      super(uriInfo, request, branchUuid);
      searchQueryBuilder = DslFactory.createQueryBuilder();
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
    * @param includeCache (Optional) Boolean parameter that configures the search to ???
    * @param includeDeleted (Optional) Boolean parameter that configures the search to include deleted artifacts in its
    * result set and analysis.
    * @return A {@link SearchResponse} object containing the results of the search, configuration and analysis
    * information.
    * @throws OseeCoreException<br>
    * <br>
    * <b>rawQuery Syntax</b><br>
    * The query syntax is composed of one or more predicates - atomic search instructions - that can be appended
    * together as compound AND or OR query statements. Each predicate is surrounded by square brackets '[' and ']' which
    * makes writing a formal definition of the syntax a little confusing since the square bracket traditionally
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
   public SearchResponse getSearchWithQueryParams(@QueryParam("alt") String alt, @QueryParam("fields") String fields, @QueryParam("q") String rawQuery, @QueryParam("fromTx") int fromTransaction, @QueryParam("includeDeleted") boolean includeDeleted) throws OseeCoreException {
      DslTranslator translator = DslFactory.createTranslator();
      SearchRequest params =
         new SearchRequest(getBranchUuid(), translator.translate(rawQuery), alt, fields, fromTransaction,
            includeDeleted);
      return search(params);
   }

   @POST
   @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   @Consumes({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
   public SearchResponse getSearchWithMatrixParams(SearchRequest parameters) throws OseeCoreException {
      return search(parameters);
   }

   private SearchResponse search(SearchRequest params) throws OseeCoreException {
      long startTime = System.currentTimeMillis();

      QueryFactory qFactory = OrcsApplication.getOrcsApi().getQueryFactory(null); // Fix this

      QueryBuilder builder = searchQueryBuilder.build(qFactory, params);

      builder.includeDeletedArtifacts(params.isIncludeDeleted());

      if (params.getFromTx() > 0) {
         builder.fromTransaction(params.getFromTx());
      }

      SearchResponse result = new SearchResponse();
      RequestType request = RequestType.valueOf(params.getFields().toUpperCase());
      List<Integer> localIds = new LinkedList<Integer>();
      switch (request) {
         case COUNT:
            int total = builder.getCount();
            result.setTotal(total);
            break;
         case IDS:
            for (HasLocalId art : builder.getResultsAsLocalIds()) {
               localIds.add(art.getLocalId());
            }
            result.setIds(localIds);
            result.setTotal(localIds.size());
            break;
         case MATCHES:
            ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> matches = builder.getMatches();
            List<SearchMatch> searchMatches = new LinkedList<SearchMatch>();
            for (Match<ArtifactReadable, AttributeReadable<?>> match : matches) {
               int artId = match.getItem().getLocalId();
               localIds.add(artId);
               for (AttributeReadable<?> attribute : match.getElements()) {
                  int attrId = attribute.getLocalId();
                  List<MatchLocation> locations = match.getLocation(attribute);
                  searchMatches.add(new SearchMatch(artId, attrId, locations));
               }
            }
            result.setIds(localIds);
            result.setMatches(searchMatches);
            result.setTotal(searchMatches.size());
            break;
         default:
            throw new UnsupportedOperationException();
      }

      result.setSearchRequest(params);
      result.setSearchTime(System.currentTimeMillis() - startTime);
      return result;
   }
}
