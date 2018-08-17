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

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.function.Supplier;
import javax.ws.rs.core.UriInfo;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
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
   private final QueryBuilder query;
   private final UriInfo uriInfo;

   public ArtifactEndpointImpl(OrcsApi orcsApi, BranchId branch, UserId account, UriInfo uriInfo) {
      this.orcsApi = orcsApi;
      this.query = orcsApi.getQueryFactory().fromBranch(branch);
      this.account = account;
      this.uriInfo = uriInfo;
      this.branch = branch;
   }

   @Override
   public SearchResponse getSearchWithMatrixParams(SearchRequest params) {
      long startTime = System.currentTimeMillis();

      SearchQueryBuilder searchQueryBuilder = DslFactory.createQueryBuilder();
      QueryBuilder builder = searchQueryBuilder.build(orcsApi.getQueryFactory(), params);

      builder.includeDeletedArtifacts(params.isIncludeDeleted());

      if (params.getFromTx() > 0) {
         builder.fromTransaction(TransactionId.valueOf(params.getFromTx()));
      }

      SearchResponse result = new SearchResponse();
      RequestType request = params.getRequestType();
      if (request != null) {
         List<ArtifactId> ids = new LinkedList<>();
         switch (request) {
            case COUNT:
               int total = builder.getCount();
               result.setTotal(total);
               break;
            case IDS:
               for (ArtifactId art : builder.getResultsIds()) {
                  ids.add(art);
               }
               result.setIds(ids);
               result.setTotal(ids.size());
               break;
            case MATCHES:
               ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> matches = builder.getMatches();
               List<SearchMatch> searchMatches = new LinkedList<>();
               for (Match<ArtifactReadable, AttributeReadable<?>> match : matches) {
                  ArtifactId artId = match.getItem();
                  ids.add(artId);
                  for (AttributeReadable<?> attribute : match.getElements()) {
                     List<MatchLocation> locations = match.getLocation(attribute);
                     searchMatches.add(new SearchMatch(artId, attribute, locations));
                  }
               }
               result.setIds(ids);
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
      ArtifactReadable rootArtifact = query.andIsHeirarchicalRootArtifact().getResults().getExactlyOne();
      ResultSet<ArtifactReadable> children = rootArtifact.getChildren();
      HtmlWriter writer = new HtmlWriter(uriInfo, orcsApi);
      return writer.toHtml(children);
   }

   @Override
   public String getArtifactAsHtml(ArtifactId artifactId) {
      HtmlWriter writer = new HtmlWriter(uriInfo, orcsApi);
      return writer.toHtml(query.andId(artifactId).getResults());
   }

   @Override
   public ArtifactToken getArtifactToken(ArtifactId artifactId) {
      return query.andId(artifactId).loadArtifactToken();
   }

   @Override
   public AttributeEndpoint getAttributes(ArtifactId artifactId) {
      return new AttributeEndpointImpl(artifactId, branch, orcsApi, query, uriInfo);
   }

   private <T> T getArtifactXByAttribute(AttributeTypeId attributeType, String value, boolean exists, ArtifactTypeId artifactType, Supplier<T> queryMethod) {
      if (artifactType.isValid()) {
         query.andTypeEquals(artifactType);
      }
      if (exists) {
         query.and(attributeType, value);
      } else {
         query.andNotExists(attributeType, value);
      }
      return queryMethod.get();
   }

   /**
    * if exists = false then return all artifact of given type that do not have an attribute of the given type with the
    * specified value. This includes artifacts that lack attributes of the given type as well as those that have that
    * type but with a different value.
    */
   @Override
   public List<ArtifactToken> getArtifactTokensByAttribute(AttributeTypeId attributeType, String value, boolean exists, ArtifactTypeId artifactType) {
      return getArtifactXByAttribute(attributeType, value, exists, artifactType, query::loadArtifactTokens);
   }

   /**
    * if exists = false then return all artifact of given type that do not have an attribute of the given type with the
    * specified value. This includes artifacts that lack attributes of the given type as well as those that have that
    * type but with a different value.
    */
   @Override
   public List<ArtifactId> getArtifactIdsByAttribute(AttributeTypeId attributeType, String value, boolean exists, ArtifactTypeId artifactType) {
      return getArtifactXByAttribute(attributeType, value, exists, artifactType, query::loadArtifactIds);
   }

   @Override
   public List<ArtifactToken> getArtifactTokensByType(ArtifactTypeId artifactType) {
      return query.andTypeEquals(artifactType).loadArtifactTokens();
   }

   @Override
   public List<ArtifactId> getArtifactIdsByType(ArtifactTypeId artifactType) {
      return query.andTypeEquals(artifactType).loadArtifactIds();
   }

   @Override
   public List<ArtifactToken> createArtifacts(BranchId branch, ArtifactTypeId artifactType, ArtifactId parent, List<String> names) {
      ResultSet<ArtifactReadable> results =
         query.andTypeEquals(artifactType).and(CoreAttributeTypes.Name, names).getResults();
      if (!results.isEmpty()) {
         List<ArtifactReadable> duplicates = results.getList();
         throw new OseeCoreException("Found %s artifacts of type %s with duplicate names: %s", results.size(),
            artifactType, duplicates);
      }

      List<ArtifactToken> tokens = new ArrayList<>(names.size());
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - create artifacts");

      IArtifactType artifactTypeToken = orcsApi.getOrcsTypes().getArtifactTypes().get(artifactType);
      for (String name : names) {
         tokens.add(tx.createArtifact(parent, artifactTypeToken, name));
      }
      tx.commit();
      return tokens;
   }

   @Override
   public TransactionId deleteArtifact(BranchId branch, ArtifactId artifact) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - delete artifact");
      tx.deleteArtifact(artifact);
      return tx.commit();
   }

   @Override
   public TransactionId setSoleAttributeValue(BranchId branch, ArtifactId artifact, AttributeTypeId attributeType, String value) {
      TransactionBuilder tx =
         orcsApi.getTransactionFactory().createTransaction(branch, account, "rest - setSoleAttributeValue");
      tx.setSoleAttributeFromString(artifact, attributeType, value);
      return tx.commit();
   }
}