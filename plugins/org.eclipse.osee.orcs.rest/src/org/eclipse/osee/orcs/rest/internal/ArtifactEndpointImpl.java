/*********************************************************************
 * Copyright (c) 2017 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.define.rest.importing.parsers.WordTemplateContentToMarkdownConverter;
import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTokenWithIcon;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.ArtifactWithRelations;
import org.eclipse.osee.framework.core.data.AttributeReadable;
import org.eclipse.osee.framework.core.data.AttributeTypeJoin;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.util.ArtifactSearchOptions;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.framework.jdk.core.type.Pair;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.jdbc.JdbcStatement;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.data.OrcsPurgeResult;
import org.eclipse.osee.orcs.rest.internal.search.artifact.dsl.DslFactory;
import org.eclipse.osee.orcs.rest.internal.search.artifact.dsl.SearchQueryBuilder;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.AttributeEndpoint;
import org.eclipse.osee.orcs.rest.model.TxBuilderInput;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMatch;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
import org.eclipse.osee.orcs.search.ArtifactTable;
import org.eclipse.osee.orcs.search.ArtifactTableOptions;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * A new instance of this REST endpoint is created for each REST call so this class does not require a thread-safe
 * design
 *
 * @author Ryan D. Brooks
 */
public class ArtifactEndpointImpl implements ArtifactEndpoint {

   private final OrcsApi orcsApi;
   private final BranchId branch;
   private final UriInfo uriInfo;
   private final OrcsTokenService tokenService;

   public ArtifactEndpointImpl(OrcsApi orcsApi, BranchId branch, UriInfo uriInfo) {
      this.orcsApi = orcsApi;
      this.uriInfo = uriInfo;
      this.branch = branch;
      this.tokenService = orcsApi.tokenService();
   }

   @Override
   public List<ArtifactReadable> getSearchResults(String search, ArtifactId viewId,
      List<ArtifactTypeToken> artifactTypes, List<AttributeTypeToken> attributeTypes, boolean exactMatch,
      boolean searchById, long pageNum, long pageSize) {
      return getSearchQueryBuilder(search, viewId, artifactTypes, attributeTypes, exactMatch, searchById, pageNum,
         pageSize).asArtifacts();
   }

   @Override
   public List<ArtifactTokenWithIcon> getSearchResultTokens(String search, ArtifactId viewId,
      List<ArtifactTypeToken> artifactTypes, List<AttributeTypeToken> attributeTypes, boolean exactMatch,
      boolean searchById, long pageNum, long pageSize) {
      return getSearchQueryBuilder(search, viewId, artifactTypes, attributeTypes, exactMatch, searchById, pageNum,
         pageSize).asArtifactTokens().stream().map(a -> new ArtifactTokenWithIcon(a)).collect(Collectors.toList());
   }

   @Override
   public int getSearchResultCount(String search, ArtifactId viewId, List<ArtifactTypeToken> artifactTypes,
      List<AttributeTypeToken> attributeTypes, boolean exactMatch, boolean searchById) {
      return getSearchQueryBuilder(search, viewId, artifactTypes, attributeTypes, exactMatch, searchById, 0,
         0).getCount();
   }

   private QueryBuilder getSearchQueryBuilder(String search, ArtifactId viewId, List<ArtifactTypeToken> artifactTypes,
      List<AttributeTypeToken> attributeTypes, boolean exactMatch, boolean searchById, long pageNum, long pageSize) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch, viewId);
      if (searchById && Strings.isNumeric(search)) {
         return query.andId(ArtifactId.valueOf(search));
      }
      if (!artifactTypes.isEmpty()) {
         query.andTypeEquals(artifactTypes);
      }
      if (Strings.isValid(search)) {
         List<QueryOption> options = new LinkedList<>();

         if (exactMatch) {
            options.addAll(Arrays.asList(QueryOption.EXACT_MATCH_OPTIONS));
         } else {
            options.addAll(Arrays.asList(QueryOption.CONTAINS_MATCH_OPTIONS));
         }

         if (!attributeTypes.isEmpty()) {
            query = query.and(attributeTypes, search, options.toArray(QueryOption[]::new));
         } else {
            query =
               query.and(Arrays.asList(QueryBuilder.ANY_ATTRIBUTE_TYPE), search, options.toArray(QueryOption[]::new));
         }
      }
      query.setOrderByAttribute(CoreAttributeTypes.Name);
      if (pageNum != 0L && pageSize != 0L) {
         query = query.isOnPage(pageNum, pageSize);
      }
      return query;
   }

   @Override
   public SearchResponse getSearchWithMatrixParams(SearchRequest params) {
      long startTime = System.currentTimeMillis();

      SearchQueryBuilder searchQueryBuilder = DslFactory.createQueryBuilder(orcsApi);
      QueryBuilder builder = searchQueryBuilder.build(orcsApi.getQueryFactory(), params);

      builder.includeDeletedArtifacts(params.isIncludeDeleted());

      if (params.getFromTx().isGreaterThan(TransactionId.valueOf(0))) {
         builder.fromTransaction(params.getFromTx());
      }

      SearchResponse result = new SearchResponse();
      RequestType request = params.getRequestType();
      if (request != null) {
         switch (request) {
            case COUNT:
               int total = builder.getCount();
               result.setTotal(total);
               break;
            case IDS:
               List<ArtifactId> ids = builder.asArtifactIds();
               result.setIds(ids);
               result.setTotal(ids.size());
               break;
            case MATCHES:
               ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> matches = builder.getMatches();
               List<SearchMatch> searchMatches = new LinkedList<>();
               List<ArtifactId> matchIds = new LinkedList<>();
               for (Match<ArtifactReadable, AttributeReadable<?>> match : matches) {
                  ArtifactId artId = match.getItem();
                  matchIds.add(artId);
                  for (AttributeReadable<?> attribute : match.getElements()) {
                     List<MatchLocation> locations = match.getLocation(attribute);
                     searchMatches.add(new SearchMatch(artId, attribute, locations));
                  }
               }
               result.setIds(matchIds);
               result.setMatches(searchMatches);
               result.setTotal(searchMatches.size());
               break;
            default:
               throw new UnsupportedOperationException();
         }
      }
      result.setSearchRequest(params);
      result.setSearchTime(System.currentTimeMillis() - startTime);
      return result;
   }

   @Override
   public String getRootChildrenAsHtml() {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      ArtifactReadable rootArtifact = query.andIsHeirarchicalRootArtifact().getArtifact();
      HtmlWriter writer = new HtmlWriter(uriInfo, orcsApi);
      return writer.toHtml(rootArtifact.getChildren());
   }

   @Override
   public String getArtifactAsHtml(ArtifactId artifactId) {
      HtmlWriter writer = new HtmlWriter(uriInfo, orcsApi);
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      return writer.toHtml(query.andId(artifactId).getResults());
   }

   @Override
   public ArtifactReadable getArtifactAsJson(@PathParam("artifactId") ArtifactId artifactId,
      @DefaultValue("-1") @QueryParam("view") ArtifactId view) {
      return orcsApi.getQueryFactory().fromBranch(branch, view).andId(artifactId).asArtifact();
   }

   @Override
   public ArtifactToken getArtifactToken(ArtifactId artifactId) {
      return orcsApi.getQueryFactory().fromBranch(branch).andId(artifactId).asArtifactToken();
   }

   @Override
   public ArtifactToken getArtifactTokenOrSentinel(ArtifactId artifactId) {
      return orcsApi.getQueryFactory().fromBranch(branch).andId(artifactId).asArtifactTokenOrSentinel();
   }

   @Override
   public AttributeEndpoint getAttributes(ArtifactId artifactId) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      return new AttributeEndpointImpl(artifactId, branch, orcsApi, query, uriInfo);
   }

   private <T> T getArtifactXByAttribute(QueryBuilder query, AttributeTypeToken attributeType, String value,
      boolean exists, ArtifactTypeToken artifactType, Supplier<T> queryMethod) {
      if (artifactType.isValid()) {
         query.andIsOfType(artifactType);
      }
      if (attributeType.isValid()) {
         if (exists) {
            query.andAttributeIs(attributeType, value);
         } else {
            query.andNotExists(attributeType, value);
         }
      }
      return queryMethod.get();
   }

   /**
    * if exists = false then return all artifact of given type that do not have an attribute of the given type with the
    * specified value. This includes artifacts that lack attributes of the given type as well as those that have that
    * type but with a different value.
    */
   @Override
   public List<ArtifactToken> getArtifactTokensByAttribute(AttributeTypeToken attributeType, String value,
      boolean exists, ArtifactTypeToken artifactType) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      return getArtifactXByAttribute(query, attributeType, value, exists, artifactType, query::asArtifactTokens);
   }

   /**
    * if exists = false then return all artifacts of given type that do not have an attribute of the given type with the
    * specified value. This includes artifacts that lack attributes of the given type as well as those that have that
    * type but with a different value.
    */
   @Override
   public List<ArtifactId> getArtifactIdsByAttribute(AttributeTypeToken attributeType, String value, boolean exists,
      ArtifactTypeToken artifactType) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      return getArtifactXByAttribute(query, attributeType, value, exists, artifactType, query::asArtifactIds);
   }

   /**
    * if exists = false then return all artifacts of given type that do not have an attribute of the given type with the
    * specified value. This includes artifacts that lack attributes of the given type as well as those that have that
    * type but with a different value.
    */
   @Override
   public List<Map<String, Object>> getArtifactMaps(AttributeTypeToken attributeType, String representation,
      String value, boolean exists, ArtifactTypeToken artifactType, ArtifactId view) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch, view);
      return getArtifactXByAttribute(query, attributeType, value, exists, artifactType, query::asArtifactMaps);
   }

   @Override
   public List<ArtifactToken> getArtifactTokensByType(ArtifactTypeToken artifactType) {
      return orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(artifactType).asArtifactTokens();
   }

   @Override
   public List<ArtifactToken> expGetArtifactTokens(ArtifactTypeToken artifactType, ArtifactId parent, ArtifactId view) {
      //      orcsApi.getAdminOps().registerMissingOrcsTypeJoins();
      //      List<ArtifactReadable> artifacts =
      //         orcsApi.getQueryFactory().fromBranch(branch, view).andRelatedRecursive(DefaultHierarchical_Child,
      //            parent).follow(SupportingRequirement_LowerLevelRequirement).asArtifacts();
      //      return Collections.cast(artifacts);
      QueryBuilder subQuery = new QueryData(QueryType.SELECT, orcsApi.tokenService()) //
         .followFork(CoreRelationTypes.InterfaceMessagePubNode_Node, artifactType, null) //
         .follow(CoreRelationTypes.InterfaceMessageSubMessageContent_SubMessage)//
         .follow(CoreRelationTypes.InterfaceSubMessageContent_Structure) //
         .follow(CoreRelationTypes.InterfaceStructureContent_DataElement) //
         .follow(CoreRelationTypes.InterfaceElementPlatformType_PlatformType);
      List<ArtifactReadable> results = orcsApi.getQueryFactory().fromBranch(BranchId.valueOf(9031454494668930248L)) //
         //.andIsOfType(CoreArtifactTypes.InterfaceConnection) //
         .andId(ArtifactId.valueOf(200255)) //
         .followFork(CoreRelationTypes.InterfaceConnectionNode_Node, CoreArtifactTypes.InterfaceNode, null)//
         .followFork(CoreRelationTypes.InterfaceConnectionMessage_Message, CoreArtifactTypes.InterfaceMessage,
            subQuery).asArtifacts();
      System.out.println(results.toString());
      return null;
   }

   @Override
   public List<ArtifactToken> getChangedArtifactTokens(ArtifactId view, AttributeTypeJoin typeJoin,
      String commentPattern) {
      return orcsApi.getQueryFactory().fromBranch(branch, view).andTxComment(commentPattern,
         typeJoin).asArtifactTokens();
   }

   @Override
   public List<ArtifactId> getArtifactIdsByType(ArtifactTypeToken artifactType) {
      return orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(artifactType).asArtifactIds();
   }

   @Override
   public List<ArtifactToken> getArtifactTokensByApplicability(ApplicabilityId appId) {
      return orcsApi.getQueryFactory().fromBranch(branch, appId).asArtifactTokens();
   }

   @Override
   public List<ArtifactToken> createArtifacts(BranchId branch, ArtifactTypeToken artifactType, ArtifactId parent,
      List<String> names) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, "rest - create artifacts");
      List<ArtifactToken> tokens = tx.createArtifacts(artifactType, parent, names);
      tx.commit();
      return tokens;
   }

   @Override
   public ArtifactToken createArtifact(BranchId branch, ArtifactTypeToken artifactType, ArtifactId parent,
      String name) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, "rest - create artifact");
      ArtifactToken token = tx.createArtifact(parent, artifactType, name);
      tx.commit();
      return token;
   }

   @Override
   public List<ArtifactToken> changeArtifactType(BranchId branch, ArtifactTypeToken oldType, ArtifactTypeToken newType,
      List<String> names) {
      OrcsAdmin adminOps = orcsApi.getAdminOps();
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      List<ArtifactToken> artifacts = new ArrayList<>();
      for (String name : names) {
         try {
            artifacts.add(query.andTypeEquals(oldType).andNameEquals(name).asArtifactToken());
         } catch (MultipleItemsExist ex) {
            throw new MultipleItemsExist(ex.getLocalizedMessage() + "  named " + name);
         }
      }
      adminOps.changeArtifactTypeOutsideofHistory(newType, artifacts);
      return artifacts;
   }

   @Override
   public TransactionToken deleteArtifact(BranchId branch, ArtifactId artifact) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, "rest - delete artifact");
      tx.deleteArtifact(artifact);
      return tx.commit();
   }

   @Override
   public Response purgeArtifact(ArtifactId artifact) {
      OrcsPurgeResult result = orcsApi.getPurgeOps().purgeArtifact(artifact);
      if (result.isError()) {
         return Response.status(IStatus.ERROR, result.getMessage()).build();
      }
      return Response.ok(result.getMessage()).build();
   }

   @Override
   public TransactionToken deleteAttributesOfType(BranchId branch, ArtifactId artifact, ArtifactTypeToken artifactType,
      AttributeTypeToken attributeType) {
      List<ArtifactReadable> artifacts = new ArrayList<>();
      TransactionToken rtn = TransactionToken.SENTINEL;
      if (attributeType.isInvalid()) {
         return rtn;
      }
      String txComment = "Remove all attributes of type: " + attributeType.getName() + " from ";

      if (artifact.isValid()) {
         artifacts.add(orcsApi.getQueryFactory().fromBranch(branch).andId(artifact).asArtifactOrSentinel());
         txComment = txComment + " artifact: " + artifact.getIdString();
      } else {
         if (artifactType.isValid()) {
            artifacts.addAll(orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(artifactType).asArtifacts());
            txComment = txComment + " all artifacts of type: " + artifactType.getName();
         }
      }
      if (!artifacts.isEmpty()) {
         TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, txComment);
         for (ArtifactReadable artifactToChange : artifacts) {
            tx.deleteAttributes(artifactToChange, attributeType);
         }
         rtn = tx.commit();
      }
      return rtn;
   }

   @Override
   public TransactionToken setSoleAttributeValue(BranchId branch, ArtifactId artifact, AttributeTypeToken attributeType,
      String value) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, "rest - setSoleAttributeValue");
      tx.setSoleAttributeFromString(artifact, attributeType, value);
      return tx.commit();
   }

   private QueryBuilder getQueryBuilder(ArtifactSearchOptions searchCriteria) {
      QueryBuilder fromBranch;
      QueryOption matchCase = QueryOption.getCaseType(searchCriteria.isCaseSensitive());
      QueryOption matchWordOrder = QueryOption.getTokenOrderType(searchCriteria.isMatchWordOrder());
      QueryOption matchExact = QueryOption.TOKEN_DELIMITER__ANY;

      if (searchCriteria.isExactMatch()) {
         matchCase = QueryOption.CASE__MATCH;
         matchWordOrder = QueryOption.TOKEN_MATCH_ORDER__MATCH;
         matchExact = QueryOption.TOKEN_DELIMITER__EXACT;
      }

      if (!searchCriteria.getArtIds().isEmpty()) {
         fromBranch = orcsApi.getQueryFactory().fromBranch(branch).andIds(searchCriteria.getArtIds());

      } else {

         if (searchCriteria.getView().isValid()) {
            fromBranch = orcsApi.getQueryFactory().fromBranch(branch, searchCriteria.getView());
         } else if (searchCriteria.getApplic().isValid()) {
            fromBranch = orcsApi.getQueryFactory().fromBranch(branch, searchCriteria.getApplic());
         } else {
            fromBranch = orcsApi.getQueryFactory().fromBranch(branch);
         }

         if (!searchCriteria.getArtTypeIds().isEmpty()) {
            fromBranch.andIsOfType(searchCriteria.getArtTypeIds());
         }
         if (Strings.isValid(searchCriteria.getSearchString())) {

            if (searchCriteria.getAttrTypeIds().isEmpty()) {
               List<AttributeTypeToken> attrs = searchCriteria.getAttrTypeIds();
               attrs.add(QueryBuilder.ANY_ATTRIBUTE_TYPE);
               searchCriteria.setAttrTypeIds(attrs);
            }
            fromBranch.and(searchCriteria.getAttrTypeIds(), searchCriteria.getSearchString(), matchCase, matchWordOrder,
               matchExact);

         }
      }
      if (searchCriteria.getIncludeDeleted().areDeletedAllowed()) {
         fromBranch.includeDeletedArtifacts();
      }
      return fromBranch;
   }

   @Override
   public List<ArtifactId> findArtifactIds(ArtifactSearchOptions searchOptions) {
      return getQueryBuilder(searchOptions).asArtifactIds();
   }

   @Override
   public List<ArtifactToken> findArtifactTokens(ArtifactSearchOptions searchOptions) {
      return getQueryBuilder(searchOptions).asArtifactTokens();
   }

   @Override
   public ArtifactTable getArtifactTable(AttributeTypeToken attributeType, List<AttributeTypeToken> attributeColumns,
      String value, boolean exists, ArtifactTypeToken artifactType, ArtifactId view) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch, view);
      query.setTableOptions(new ArtifactTableOptions(attributeColumns));
      return getArtifactXByAttribute(query, attributeType, value, exists, artifactType, query::asArtifactsTable);
   }

   /**
    * returns a tree of parent/child relationships downward.
    */
   @Override
   public List<ArtifactReadable> getRelatedArtifactsTree(BranchId branch, ArtifactId artifact) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      return query.andId(artifact).followAll().asArtifacts();
   }

   @Override
   public ArtifactWithRelations getRelatedDirect(BranchId branch, ArtifactId artifact, ArtifactId viewId,
      boolean includeRelations) {
      viewId = viewId == null ? ArtifactId.SENTINEL : viewId;
      QueryBuilder query =
         orcsApi.getQueryFactory().fromBranch(branch, viewId).includeApplicabilityTokens().andId(artifact);
      if (includeRelations) {
         query = query.followAll(true);
      }
      ArtifactReadable art = query.asArtifactOrSentinel();
      return new ArtifactWithRelations(art, this.tokenService, includeRelations);
   }

   @Override
   public TxBuilderInput getTxBuilderInput(ArtifactTypeToken artifactTypeId) {
      return new TxBuilderInput(branch,
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(artifactTypeId).asArtifacts());
   }

   @Override
   public List<List<ArtifactId>> getPathToArtifact(BranchId branch, ArtifactId artifactId, ArtifactId viewId) {

      // List of artIds to return from the query
      List<Pair<ArtifactId, ArtifactId>> pairings = new ArrayList<>();
      List<ArtifactId> childArtIds = new ArrayList<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         pairings.add(new Pair<ArtifactId, ArtifactId>(ArtifactId.valueOf(stmt.getLong("b_art_id")),
            ArtifactId.valueOf(stmt.getLong("a_art_id"))));
         childArtIds.add(ArtifactId.valueOf(stmt.getLong("b_art_id")));
      };

      String query = "with " + orcsApi.getJdbcService().getClient().getDbType().getPostgresRecurse() //
         + " allRels (a_art_id, b_art_id, gamma_id, rel_type) as (select a_art_id, b_art_id, txs.gamma_id, rel_type " //
         + "from osee_txs txs, osee_relation rel " //
         + "where txs.branch_id = ? and txs.tx_current = 1 and txs.gamma_id = rel.gamma_id " //
         + orcsApi.getJdbcService().getClient().getDbType().getCteRecursiveUnion() //
         + " select a_art_id, b_art_id, txs.gamma_id, rel_link_type_id rel_type " //
         + "from osee_txs txs, osee_relation_link rel " //
         + "where txs.branch_id = ? and txs.tx_current = 1 and txs.gamma_id = rel.gamma_id), " //
         + "cte_query (b_art_id, a_art_id, rel_type) as ( " //
         + "select b_art_id, a_art_id, rel_type " //
         + "from allRels " //
         + "where b_art_id = ? " //
         + orcsApi.getJdbcService().getClient().getDbType().getCteRecursiveUnion() //
         + " select e.b_art_id, e.a_art_id, e.rel_type " //
         + "from allRels e " //
         + "inner join cte_query c on c.a_art_id = e.b_art_id) " //
         + "select * " //
         + "from cte_query";

      // run query to return list of artifacts that belong on the path from the top of the hierarchy to the input artifact
      orcsApi.getJdbcService().getClient().runQuery(consumer, query, branch, branch, artifactId);

      // organize the mixed list of pairs into a list of lists of artIds (list of paths)
      List<List<ArtifactId>> paths = new ArrayList<>();
      while (childArtIds.contains(artifactId)) {
         paths.add(findPath(artifactId, childArtIds, pairings));
         // increment while condition (i.e. one path has been found)
         childArtIds.remove(artifactId);
         // remove the pair matching the first two artIds of the path that has just been found to avoid retracing the same path
         pairings.remove(
            new Pair<ArtifactId, ArtifactId>(paths.get(paths.size() - 1).get(0), paths.get(paths.size() - 1).get(1)));
      }

      return paths;
   }

   private static List<ArtifactId> findPath(ArtifactId artId, List<ArtifactId> childArtIds,
      List<Pair<ArtifactId, ArtifactId>> pairings) {
      List<ArtifactId> path = new ArrayList<>();
      // loop through the pairs to find a pair that includes the input artId
      for (Pair<ArtifactId, ArtifactId> pair : pairings) {
         if (pair.getFirst().equals(artId)) {
            path.add(pair.getFirst());
            // if we are not at the end of the path (i.e. the current parent has a parent)
            if (childArtIds.contains(pair.getSecond())) {
               path.addAll(findPath(pair.getSecond(), childArtIds, pairings));
            }
            // we are at the top of the hierarchy
            else {
               path.add(pair.getSecond());
            }
            return path;
         }
      }
      return path;
   }

   @Override
   public String convertWordTemplateContentToMarkdownContent(BranchId branchId, ArtifactId artifactId,
      Boolean includeErrorLog, Boolean flushMarkdownContentAttributeAndImageArtifacts) {

      // Require user to have OseeAdmin role before performing any operations
      orcsApi.userService().requireRole(CoreUserGroups.OseeAdmin);

      StringBuilder artifactEpErrorLog = new StringBuilder();

      // List of artIds to return from the query
      List<Pair<ArtifactId, ArtifactId>> pairings = new ArrayList<>();
      List<ArtifactId> childArtIds = new ArrayList<>();
      Consumer<JdbcStatement> consumer = stmt -> {
         pairings.add(new Pair<ArtifactId, ArtifactId>(ArtifactId.valueOf(stmt.getLong("b_art_id")),
            ArtifactId.valueOf(stmt.getLong("a_art_id"))));
         childArtIds.add(ArtifactId.valueOf(stmt.getLong("b_art_id")));
      };

      String query = "with " + orcsApi.getJdbcService().getClient().getDbType().getPostgresRecurse() //
         + " allRels (a_art_id, b_art_id, gamma_id, rel_type) as (select a_art_id, b_art_id, txs.gamma_id, rel_type " //
         + "from osee_txs txs, osee_relation rel " //
         + "where txs.branch_id = ? and txs.tx_current = 1 and txs.gamma_id = rel.gamma_id " //
         + orcsApi.getJdbcService().getClient().getDbType().getCteRecursiveUnion() //
         + " select a_art_id, b_art_id, txs.gamma_id, rel_link_type_id rel_type " //
         + "from osee_txs txs, osee_relation_link rel " //
         + "where txs.branch_id = ? and txs.tx_current = 1 and txs.gamma_id = rel.gamma_id), " //
         + "cte_query (b_art_id, a_art_id, rel_type) as ( " //
         + "select b_art_id, a_art_id, rel_type " //
         + "from allRels " //
         + "where a_art_id = ? " //
         + orcsApi.getJdbcService().getClient().getDbType().getCteRecursiveUnion() //
         + " select e.b_art_id, e.a_art_id, e.rel_type " //
         + "from allRels e " //
         + "inner join cte_query c on c.b_art_id = e.a_art_id) " //
         + "select * " //
         + "from cte_query";

      // run query to return list of artifacts that belong on the path from the top of the hierarchy to the input artifact
      orcsApi.getJdbcService().getClient().runQuery(consumer, query, branch, branch, artifactId);

      StringBuilder resultBuilder = new StringBuilder();
      // add the input artifactId to the list of art ids to convert
      childArtIds.add(artifactId);

      // Iterate through artifact ids
      artifactLoop: for (ArtifactId currArtId : childArtIds) {

         // Query for current art using id
         ArtifactReadable currArt =
            orcsApi.getQueryFactory().fromBranch(branch).andId(currArtId).getResults().getExactlyOne();

         /*
          * Flush
          */
         if (flushMarkdownContentAttributeAndImageArtifacts) {
            TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branchId,
               "WTC to Markdown conversion - flush all Markdown Content attributes and Image artifacts for the entire hierarchy specified");
            if (currArt.getArtifactType().equals(CoreArtifactTypes.Image)) {
               tx.deleteArtifact(currArt);
            } else {
               tx.deleteAttributes(currArt.getToken(), CoreAttributeTypes.MarkdownContent);
            }
            tx.commit();
            continue artifactLoop;
         }

         /*
          * Conversion
          */
         List<AttributeReadable<Object>> attrs = new ArrayList<>();
         for (AttributeReadable<Object> attr : currArt.getAttributes(CoreAttributeTypes.WordTemplateContent)) {
            attrs.add(attr);
         }
         if (attrs.size() == 1) {
            AttributeReadable<?> attribute = attrs.iterator().next();
            Object content = attribute.getValue();
            if (content instanceof String) {
               String contentAsString = (String) content;
               WordTemplateContentToMarkdownConverter conv =
                  new WordTemplateContentToMarkdownConverter(orcsApi, branch, currArt.getArtifactId(), includeErrorLog);
               String mdContent = conv.run(contentAsString);
               String result = String.format(
                  "`````````````````````````````````\n" + "Before:\n" + "%s\n\n" + "After:\n" + "%s\n" + "`````````````````````````````````",
                  contentAsString, mdContent);
               resultBuilder.append(result).append("\n");

               // Save md content to artifact
               TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branchId,
                  "WTC to Markdown conversion - save converted word template content as markdown content");

               // Add/Update MD content attribute
               List<AttributeReadable<Object>> mdAttrs = new ArrayList<>();
               for (AttributeReadable<Object> mdAttr : currArt.getAttributes(CoreAttributeTypes.WordTemplateContent)) {
                  mdAttrs.add(mdAttr);
               }
               if (mdAttrs.size() == 1) {
                  tx.setSoleAttributeFromString(currArt.getToken(), CoreAttributeTypes.MarkdownContent, mdContent);
               } else if (mdAttrs.size() < 1) {
                  tx.createAttribute(currArt.getToken(), CoreAttributeTypes.MarkdownContent, mdContent);
               } else {
                  throw new Error(
                     "More than 1 attribute set for Markdown Content. Artifact Id: " + currArt.getArtifactId().getId());
               }

               // Remove wtc attribute
               //tx.deleteAttributes(currArt.getToken(), CoreAttributeTypes.WordTemplateContent);
               tx.commit();

               artifactEpErrorLog.append(conv.getErrorLog());
            } else {
               throw new IllegalArgumentException("Content is not a String: " + content);
            }
         } else if (attrs.size() > 1) {
            throw new Error("More than 1 attribute set for WTC. Artifact Id: " + currArt.getArtifactId().getId());

         } else {
            artifactEpErrorLog.append("\n\n0 attributes set for WTC. Artifact Id: " + currArt.getArtifactId().getId());
         }
      }
      if (flushMarkdownContentAttributeAndImageArtifacts) {
         return "Flushed all Markdown Content attributes and Image artifacts for the entire hierarchy specified!";
      }

      String result = resultBuilder.toString();
      result += includeErrorLog ? artifactEpErrorLog.toString() : "";
      return result;
   };

}
