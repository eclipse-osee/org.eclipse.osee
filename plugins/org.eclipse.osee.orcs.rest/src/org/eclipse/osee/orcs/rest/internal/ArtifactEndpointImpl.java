/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.rest.internal;

import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.DefaultHierarchical_Child;
import static org.eclipse.osee.framework.core.enums.CoreRelationTypes.SupportingRequirement_LowerLevelRequirement;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.TransactionToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.MultipleItemsExist;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.orcs.OrcsAdmin;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.rest.internal.search.artifact.dsl.DslFactory;
import org.eclipse.osee.orcs.rest.internal.search.artifact.dsl.SearchQueryBuilder;
import org.eclipse.osee.orcs.rest.model.ArtifactEndpoint;
import org.eclipse.osee.orcs.rest.model.AttributeEndpoint;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchMatch;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchRequest;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResponse;
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
   private final UserId account;
   private final UriInfo uriInfo;

   public ArtifactEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account, UriInfo uriInfo) {
      this.orcsApi = orcsApi;
      this.account = account;
      this.uriInfo = uriInfo;
      this.branch = branch;
   }

   @Override
   public SearchResponse getSearchWithMatrixParams(SearchRequest params) {
      long startTime = System.currentTimeMillis();

      SearchQueryBuilder searchQueryBuilder = DslFactory.createQueryBuilder(orcsApi);
      QueryBuilder builder = searchQueryBuilder.build(orcsApi.getQueryFactory(), params);

      builder.includeDeletedArtifacts(params.isIncludeDeleted());

      if (params.getFromTx() > 0) {
         builder.fromTransaction(TransactionId.valueOf(params.getFromTx()));
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
   public ArtifactToken getArtifactToken(ArtifactId artifactId) {
      return orcsApi.getQueryFactory().fromBranch(branch).andId(artifactId).asArtifactToken();
   }

   @Override
   public AttributeEndpoint getAttributes(ArtifactId artifactId) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      return new AttributeEndpointImpl(artifactId, branch, orcsApi, query, uriInfo);
   }

   private <T> T getArtifactXByAttribute(QueryBuilder query, AttributeTypeId attributeType, String value, boolean exists, ArtifactTypeToken artifactType, Supplier<T> queryMethod) {
      if (artifactType.isValid()) {
         query.andTypeEquals(artifactType);
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
   public List<ArtifactToken> getArtifactTokensByAttribute(AttributeTypeId attributeType, String value, boolean exists, ArtifactTypeToken artifactType) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      return getArtifactXByAttribute(query, attributeType, value, exists, artifactType, query::asArtifactTokens);
   }

   /**
    * if exists = false then return all artifacts of given type that do not have an attribute of the given type with the
    * specified value. This includes artifacts that lack attributes of the given type as well as those that have that
    * type but with a different value.
    */
   @Override
   public List<ArtifactId> getArtifactIdsByAttribute(AttributeTypeId attributeType, String value, boolean exists, ArtifactTypeToken artifactType) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch);
      return getArtifactXByAttribute(query, attributeType, value, exists, artifactType, query::asArtifactIds);
   }

   /**
    * if exists = false then return all artifacts of given type that do not have an attribute of the given type with the
    * specified value. This includes artifacts that lack attributes of the given type as well as those that have that
    * type but with a different value.
    */
   @Override
   public List<Map<String, Object>> getArtifactMaps(AttributeTypeId attributeType, String representation, String value, boolean exists, ArtifactTypeToken artifactType, ArtifactId view) {
      QueryBuilder query = orcsApi.getQueryFactory().fromBranch(branch, view);
      return getArtifactXByAttribute(query, attributeType, value, exists, artifactType, query::asArtifactMaps);
   }

   @Override
   public List<ArtifactToken> getArtifactTokensByType(ArtifactTypeToken artifactType) {
      return orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(artifactType).asArtifactTokens();
   }

   @Override
   public List<ArtifactToken> expGetArtifactTokens(ArtifactTypeToken artifactType, ArtifactId parent, ArtifactId view) {
      List<ArtifactReadable> artifacts =
         orcsApi.getQueryFactory().fromBranch(branch, view).andRelatedRecursive(DefaultHierarchical_Child,
            parent).follow(SupportingRequirement_LowerLevelRequirement).asArtifacts();
      return Collections.cast(artifacts);
   }

   @Override
   public List<ArtifactId> getArtifactIdsByType(ArtifactTypeToken artifactType) {
      return orcsApi.getQueryFactory().fromBranch(branch).andTypeEquals(artifactType).asArtifactIds();
   }

   @Override
   public List<ArtifactToken> createArtifacts(BranchId branch, ArtifactTypeToken artifactType, ArtifactId parent, List<String> names) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - create artifacts");
      List<ArtifactToken> tokens = tx.createArtifacts(artifactType, parent, names);
      tx.commit();
      return tokens;
   }

   @Override
   public ArtifactToken createArtifact(BranchId branch, ArtifactTypeToken artifactType, ArtifactId parent, String name) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - create artifact");
      ArtifactToken token = tx.createArtifact(parent, artifactType, name);
      tx.commit();
      return token;
   }

   @Override
   public List<ArtifactToken> changeArtifactType(BranchId branch, ArtifactTypeToken oldType, ArtifactTypeToken newType, List<String> names) {
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
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - delete artifact");
      tx.deleteArtifact(artifact);
      return tx.commit();
   }

   @Override
   public TransactionToken setSoleAttributeValue(BranchId branch, ArtifactId artifact, AttributeTypeToken attributeType, String value) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - setSoleAttributeValue");
      tx.setSoleAttributeFromString(artifact, attributeType, value);
      return tx.commit();
   }
}