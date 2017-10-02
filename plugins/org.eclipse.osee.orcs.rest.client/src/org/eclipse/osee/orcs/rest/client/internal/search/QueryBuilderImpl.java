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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.orcs.rest.client.QueryBuilder;
import org.eclipse.osee.orcs.rest.model.search.artifact.Predicate;
import org.eclipse.osee.orcs.rest.model.search.artifact.RequestType;
import org.eclipse.osee.orcs.rest.model.search.artifact.SearchResult;

/**
 * @author John Misinco
 */
public class QueryBuilderImpl implements QueryBuilder {

   private final PredicateFactory predicateFactory;
   private final BranchId branch;
   private final QueryOptions options;
   private final List<Predicate> predicates;
   private final QueryExecutor executor;

   public QueryBuilderImpl(BranchId branch, List<Predicate> predicates, QueryOptions options, PredicateFactory predicateFactory, QueryExecutor executor) {
      this.branch = branch;
      this.predicates = predicates;
      this.options = options;
      this.predicateFactory = predicateFactory;
      this.executor = executor;
   }

   @Override
   public QueryBuilder includeDeleted() {
      return includeDeleted(true);
   }

   @Override
   public QueryBuilder includeDeleted(boolean enabled) {
      options.setIncludeDeleted(enabled);
      return this;
   }

   @Override
   public boolean areDeletedIncluded() {
      return options.areDeletedIncluded();
   }

   @Override
   public QueryBuilder fromTransaction(TransactionId transaction) {
      options.setFromTransaction(transaction);
      return this;
   }

   @Override
   public TransactionId getFromTransaction() {
      return options.getFromTransaction();
   }

   @Override
   public QueryBuilder headTransaction() {
      options.setHeadTransaction();
      return this;
   }

   @Override
   public boolean isHeadTransaction() {
      return options.isHeadTransaction();
   }

   @Override
   public QueryBuilder excludeDeleted() {
      return includeDeleted(false);
   }

   @Override
   public QueryBuilder andLocalId(ArtifactId artifactId) {
      return andLocalIds(Collections.singletonList(artifactId));
   }

   @Override
   public QueryBuilder andLocalIds(Collection<? extends ArtifactId> artifactIds) {
      predicates.add(predicateFactory.createLocalIdsSearch(artifactIds));
      return this;
   }

   @Override
   public QueryBuilder andGuids(String... ids) {
      return andGuids(Arrays.asList(ids));
   }

   @Override
   public QueryBuilder andGuids(List<String> ids) {
      predicates.add(predicateFactory.createUuidSearch(ids));
      return this;
   }

   @Override
   public QueryBuilder andIds(ArtifactToken... artifactToken) {
      return andIds(Arrays.asList(artifactToken));
   }

   @Override
   public QueryBuilder andIds(Collection<? extends ArtifactToken> artifactTokens) {
      predicates.add(predicateFactory.createIdSearch(artifactTokens));
      return this;
   }

   @Override
   public QueryBuilder andIsOfType(ArtifactTypeId... artifactType) {
      return andIsOfType(Arrays.asList(artifactType));
   }

   @Override
   public QueryBuilder andIsOfType(Collection<? extends ArtifactTypeId> artifactTypes) {
      predicates.add(predicateFactory.createIsOfTypeSearch(artifactTypes));
      return this;
   }

   @Override
   public QueryBuilder andTypeEquals(ArtifactTypeId... artifactType) {
      return andTypeEquals(Arrays.asList(artifactType));
   }

   @Override
   public QueryBuilder andTypeEquals(Collection<? extends ArtifactTypeId> artifactTypes) {
      predicates.add(predicateFactory.createTypeEqualsSearch(artifactTypes));
      return this;
   }

   @Override
   public QueryBuilder andExists(AttributeTypeId... attributeType) {
      return andExists(Arrays.asList(attributeType));
   }

   @Override
   public QueryBuilder andExists(Collection<? extends AttributeTypeId> attributeTypes) {
      predicates.add(predicateFactory.createAttributeExistsSearch(attributeTypes));
      return this;
   }

   @Override
   public QueryBuilder andNotExists(AttributeTypeId attributeType) {
      predicates.add(predicateFactory.createAttributeNotExistsSearch(Collections.singleton(attributeType)));
      return this;
   }

   @Override
   public QueryBuilder andNotExists(Collection<? extends AttributeTypeId> attributeTypes) {
      predicates.add(predicateFactory.createAttributeNotExistsSearch(attributeTypes));
      return this;
   }

   @Override
   public QueryBuilder andExists(IRelationType relationType) {
      predicates.add(predicateFactory.createRelationExistsSearch(Collections.singleton(relationType)));
      return this;
   }

   @Override
   public QueryBuilder andExists(RelationTypeSide relationTypeSide)  {
      predicates.add(predicateFactory.createRelationTypeSideExistsSearch(relationTypeSide));
      return this;
   }

   @Override
   public QueryBuilder andNotExists(RelationTypeSide relationTypeSide)  {
      predicates.add(predicateFactory.createRelationTypeSideNotExistsSearch(relationTypeSide));
      return this;
   }

   @Override
   public QueryBuilder andNotExists(IRelationType relationType)  {
      predicates.add(predicateFactory.createRelationNotExistsSearch(Collections.singleton(relationType)));
      return this;
   }

   @Override
   public QueryBuilder andNameEquals(String artifactName) {
      return and(CoreAttributeTypes.Name, artifactName);
   }

   @Override
   public QueryBuilder and(AttributeTypeId attributeType, Collection<String> values, QueryOption... options) {
      predicates.add(predicateFactory.createAttributeTypeSearch(Collections.singleton(attributeType), values, options));
      return this;
   }

   @Override
   public QueryBuilder and(AttributeTypeId attributeType, String value, QueryOption... options) {
      return and(Collections.singleton(attributeType), value, options);
   }

   @Override
   public QueryBuilder and(Collection<? extends AttributeTypeId> attributeTypes, String value, QueryOption... options) {
      predicates.add(predicateFactory.createAttributeTypeSearch(attributeTypes, value, options));
      return this;
   }

   @Override
   public QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, ArtifactId... artifacts) {
      return andRelatedTo(relationTypeSide, Arrays.asList(artifacts));
   }

   @Override
   public QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, Collection<ArtifactId> artifactIds) {
      predicates.add(predicateFactory.createRelatedToSearch(relationTypeSide, artifactIds));
      return this;
   }

   @Override
   public SearchResult getSearchResult(RequestType request)  {
      QueryOptions qOptions = options.clone();
      return executor.getResults(request, branch, predicates, qOptions);
   }

   @Override
   public int getCount()  {
      QueryOptions qOptions = options.clone();
      return executor.getCount(branch, predicates, qOptions);
   }

   @Override
   public List<ArtifactId> getIds() {
      return getSearchResult(RequestType.IDS).getIds();
   }

   @Override
   public QueryBuilder getQueryBuilder() {
      return this;
   }

}
