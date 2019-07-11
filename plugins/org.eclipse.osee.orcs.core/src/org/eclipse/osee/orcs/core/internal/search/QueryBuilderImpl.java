/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal.search;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeRaw;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaIdQuery;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedRecursive;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaTokenQuery;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author Roberto E. Escobar
 */
public final class QueryBuilderImpl implements QueryBuilder {
   private final CallableQueryFactory artQueryFactory;
   private final OrcsSession session;
   private final QueryEngine queryEngine;
   private final CriteriaFactory criteriaFactory;
   private final QueryData queryData;

   public QueryBuilderImpl(CallableQueryFactory artQueryFactory, CriteriaFactory criteriaFactory, OrcsSession session, QueryData queryData) {
      this.criteriaFactory = criteriaFactory;
      this.queryData = queryData;
      this.artQueryFactory = artQueryFactory;
      this.session = session;
      this.queryEngine = artQueryFactory.getQueryEngine();
   }

   @Override
   public List<Map<String, Object>> asArtifactMaps() {
      getQueryData().addCriteria(new CriteriaTokenQuery(AttributeTypeToken.SENTINEL));
      return queryEngine.asArtifactMaps(getQueryData());
   }

   @Override
   public ArtifactToken loadArtifactToken() {
      return loadArtifact(this::loadArtifactTokens);
   }

   @Override
   public List<ArtifactToken> loadArtifactTokens() {
      return loadArtifactTokens(Name);
   }

   @Override
   public Map<ArtifactId, ArtifactToken> loadArtifactTokenMap() {
      getQueryData().addCriteria(new CriteriaTokenQuery(Name));
      return queryEngine.loadArtifactTokenMap(getQueryData());
   }

   @Override
   public List<ArtifactToken> loadArtifactTokens(AttributeTypeId attributeType) {
      getQueryData().addCriteria(new CriteriaTokenQuery(attributeType));
      return queryEngine.loadArtifactTokens(getQueryData());
   }

   @Override
   public Map<ArtifactId, ArtifactReadable> loadArtifactMap() {
      Map<ArtifactId, ArtifactReadable> artifacts = new HashMap<>(10000);
      getResults().forEach(artifact -> artifacts.put(artifact, artifact));
      return artifacts;
   }

   @Override
   public ArtifactId loadArtifactId() {
      return loadArtifact(this::loadArtifactIds);
   }

   @Override
   public ArtifactId loadArtifactIdOrSentinel() {
      return loadArtifactOrSentinel(this::loadArtifactIds, ArtifactId.SENTINEL);
   }

   @Override
   public ArtifactToken loadArtifactTokenOrSentinel() {
      return loadArtifactOrSentinel(this::loadArtifactTokens, ArtifactToken.SENTINEL);
   }

   private <T> T loadArtifact(Supplier<List<T>> supplier) {
      List<T> artifacts = supplier.get();
      if (artifacts.size() != 1) {
         throw new OseeCoreException("Expected exactly 1 artifact not %s", artifacts.size());
      }
      return artifacts.get(0);
   }

   private <T> T loadArtifactOrSentinel(Supplier<List<T>> supplier, T sentinel) {
      List<T> artifacts = supplier.get();
      if (artifacts.size() > 1) {
         throw new OseeCoreException("Expected at most 1 artifact not %s", artifacts.size());
      } else if (artifacts.size() == 1) {
         return artifacts.get(0);
      }
      return sentinel;
   }

   @Override
   public List<ArtifactId> loadArtifactIds() {
      getQueryData().addCriteria(new CriteriaIdQuery());
      return queryEngine.loadArtifactIds(getQueryData());
   }

   @Override
   public ResultSet<ArtifactReadable> getResults() {
      try {
         return artQueryFactory.createSearch(session, getQueryData()).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ArtifactReadable getArtifact() {
      return getResults().getExactlyOne();
   }

   @Override
   public ArtifactReadable getArtifactOrNull() {
      return getResults().getAtMostOneOrNull();
   }

   @Override
   public ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getMatches() {
      try {
         return artQueryFactory.createSearchWithMatches(session, getQueryData()).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public int getCount() {
      return queryEngine.getArtifactCount(getQueryData());
   }

   @Override
   public boolean exists() {
      return getCount() > 0;
   }

   @Override
   public ResultSet<? extends ArtifactId> getResultsIds() {
      try {
         return artQueryFactory.createLocalIdSearch(session, getQueryData()).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public ArtifactToken getArtifactOrSentinal() {
      ArtifactToken art = getArtifactOrNull();
      if (art == null) {
         return ArtifactToken.SENTINEL;
      }
      return art;
   }

   @Override
   public ArtifactToken getAtMostOneOrSentinal() {
      ResultSet<ArtifactReadable> artifacts = getResults();
      if (artifacts.isEmpty()) {
         return ArtifactToken.SENTINEL;
      } else if (artifacts.size() > 1) {
         throw new OseeStateException(String.format("Expected 0..1, found %s", artifacts.size()));
      }
      return artifacts.iterator().next();
   }

   private QueryData getQueryData() {
      return queryData;
   }

   private Options getOptions() {
      return queryData.getOptions();
   }

   @Override
   public QueryBuilder includeDeletedAttributes() {
      return includeDeletedAttributes(true);
   }

   @Override
   public QueryBuilder includeDeletedAttributes(boolean enabled) {
      OptionsUtil.setIncludeDeletedAttributes(getOptions(), enabled);
      return this;
   }

   @Override
   public boolean areDeletedAttributesIncluded() {
      return OptionsUtil.areDeletedAttributesIncluded(getOptions());
   }

   @Override
   public QueryBuilder includeDeletedRelations() {
      return includeDeletedRelations(true);
   }

   @Override
   public QueryBuilder includeDeletedRelations(boolean enabled) {
      OptionsUtil.setIncludeDeletedRelations(getOptions(), enabled);
      return this;
   }

   @Override
   public boolean areDeletedRelationsIncluded() {
      return OptionsUtil.areDeletedRelationsIncluded(getOptions());
   }

   @Override
   public QueryBuilder includeDeletedArtifacts() {
      return includeDeletedArtifacts(true);
   }

   @Override
   public QueryBuilder includeDeletedArtifacts(boolean enabled) {
      OptionsUtil.setIncludeDeletedArtifacts(getOptions(), enabled);
      return this;
   }

   @Override
   public boolean areDeletedArtifactsIncluded() {
      return OptionsUtil.areDeletedArtifactsIncluded(getOptions());
   }

   @Override
   public QueryBuilder fromTransaction(TransactionId transaction) {
      OptionsUtil.setFromTransaction(getOptions(), transaction);
      return this;
   }

   @Override
   public TransactionId getFromTransaction() {
      return OptionsUtil.getFromTransaction(getOptions());
   }

   @Override
   public QueryBuilder headTransaction() {
      OptionsUtil.setHeadTransaction(getOptions());
      return this;
   }

   @Override
   public boolean isHeadTransaction() {
      return OptionsUtil.isHeadTransaction(getOptions());
   }

   @Override
   public QueryBuilder excludeDeleted() {
      includeDeletedArtifacts(false);
      return this;
   }

   @Override
   public QueryBuilder andUuid(long id) {
      return andId(ArtifactId.valueOf(id));
   }

   @Override
   public QueryBuilder andId(long id) {
      return andId(ArtifactId.valueOf(id));
   }

   @Override
   public QueryBuilder andId(ArtifactId id) {
      return addAndCheck(queryData, new CriteriaArtifactIds(id));
   }

   @Override
   public QueryBuilder andIds(Collection<? extends ArtifactId> ids) {
      return addAndCheck(queryData, new CriteriaArtifactIds(ids));
   }

   @Override
   public QueryBuilder andUuids(Collection<Long> artifactIds) {
      return andIds(artifactIds.stream().map(id -> ArtifactId.valueOf(id)).collect(Collectors.toList()));
   }

   @Override
   public QueryBuilder andIdsL(Collection<Long> artifactIds) {
      return andIds(artifactIds.stream().map(id -> ArtifactId.valueOf(id)).collect(Collectors.toList()));
   }

   @Override
   public QueryBuilder andGuid(String id) {
      return andGuids(Collections.singleton(id));
   }

   @Override
   public QueryBuilder andGuids(Collection<String> ids) {
      Set<String> guids = new HashSet<>();
      Set<String> invalids = new HashSet<>();
      for (String id : ids) {
         if (GUID.isValid(id)) {
            guids.add(id);
         } else {
            invalids.add(id);
         }
      }

      Conditions.checkExpressionFailOnTrue(!invalids.isEmpty(), "Invalid guids detected - %s", invalids);
      if (!guids.isEmpty()) {
         Criteria guidCriteria = criteriaFactory.createArtifactGuidCriteria(guids);
         addAndCheck(getQueryData(), guidCriteria);
      }
      return this;
   }

   @Override
   public QueryBuilder andIsOfType(ArtifactTypeId... artifactType) {
      return andIsOfType(Arrays.asList(artifactType));
   }

   @Override
   public QueryBuilder andIsOfType(Collection<? extends ArtifactTypeId> artifactType) {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteriaWithInheritance(artifactType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andTypeEquals(ArtifactTypeId... artifactType) {
      return andTypeEquals(Arrays.asList(artifactType));
   }

   @Override
   public QueryBuilder andTypeEquals(Collection<? extends ArtifactTypeId> artifactType) {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteria(artifactType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andExists(AttributeTypeId... attributeType) {
      return andExists(Arrays.asList(attributeType));
   }

   @Override
   public QueryBuilder andExists(Collection<AttributeTypeId> attributeTypes) {
      Criteria criteria = criteriaFactory.createExistsCriteria(attributeTypes);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andNotExists(AttributeTypeId attributeType) {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(attributeType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andNotExists(AttributeTypeId attributeType, String value) {
      Criteria criteria = new CriteriaAttributeTypeNotExists(attributeType, value);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andNotExists(Collection<AttributeTypeId> attributeTypes) {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(attributeTypes);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andExists(IRelationType relationType) {
      Criteria criteria = criteriaFactory.createExistsCriteria(relationType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andNotExists(IRelationType relationType) {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(relationType);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andNotExists(RelationTypeSide relationTypeSide) {
      Criteria criteria = criteriaFactory.createNotExistsCriteria(relationTypeSide);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andExists(RelationTypeSide relationTypeSide) {
      Criteria criteria = criteriaFactory.createExistsCriteria(relationTypeSide);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder and(AttributeTypeId attributeType, Collection<String> values, QueryOption... options) {
      return and(Collections.singleton(attributeType), values, options);
   }

   @Override
   public QueryBuilder andAttributeIs(AttributeTypeId attributeType, String value, QueryOption... options) {
      Criteria criteria =
         new CriteriaAttributeRaw(Collections.singleton(attributeType), Collections.singleton(value), options);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder and(AttributeTypeId attributeType, String value, QueryOption... options) {
      return and(Collections.singleton(attributeType), Collections.singleton(value), options);
   }

   @Override
   public QueryBuilder and(Collection<AttributeTypeId> attributeTypes, String value, QueryOption... options) {
      return and(attributeTypes, Collections.singleton(value), options);
   }

   @Override
   public QueryBuilder and(Collection<AttributeTypeId> attributeTypes, Collection<String> value, QueryOption... options) {
      Criteria criteria = criteriaFactory.createAttributeCriteria(attributeTypes, value, options);
      return addAndCheck(getQueryData(), criteria);
   }

   @Override
   public QueryBuilder andNameEquals(String artifactName) {
      return and(CoreAttributeTypes.Name, artifactName, QueryOption.EXACT_MATCH_OPTIONS);
   }

   @Override
   public QueryBuilder andIds(ArtifactId... ids) {
      return andIds(Arrays.asList(ids));
   }

   @Override
   public QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, ArtifactReadable... artifacts) {
      return andRelatedTo(relationTypeSide, Arrays.asList(artifacts));
   }

   @Override
   public QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, Collection<? extends ArtifactId> artifacts) {
      return addAndCheck(getQueryData(), new CriteriaRelatedTo(relationTypeSide, artifacts));
   }

   @Override
   public QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      return addAndCheck(getQueryData(), new CriteriaRelatedTo(relationTypeSide, artifactId));
   }

   @Override
   public QueryBuilder andRelatedRecursive(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      return addAndCheck(getQueryData(), new CriteriaRelatedRecursive(relationTypeSide, artifactId));
   }

   @Override
   public QueryBuilder followRelation(RelationTypeSide relationTypeSide) {
      Criteria criteria = criteriaFactory.createFollowRelationType(relationTypeSide);
      addAndCheck(getQueryData(), criteria);
      queryData.newCriteriaSet();
      return this;
   }

   private QueryBuilder addAndCheck(QueryData queryData, Criteria criteria) {
      criteria.checkValid(getOptions());
      queryData.addCriteria(criteria);
      return this;
   }

   @Override
   public QueryBuilder andIsHeirarchicalRootArtifact() {
      andId(CoreArtifactTokens.DefaultHierarchyRoot);
      return this;
   }
}