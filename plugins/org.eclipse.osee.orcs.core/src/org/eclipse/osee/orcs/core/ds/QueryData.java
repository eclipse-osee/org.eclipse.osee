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
package org.eclipse.osee.orcs.core.ds;

import static org.eclipse.osee.framework.core.enums.CoreAttributeTypes.Name;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.HasBranch;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.enums.TableEnum;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.QueryType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactGuids;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactIds;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaArtifactType;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeRaw;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeTypeNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedRecursive;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelatedTo;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeFollow;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeNotExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeSideExists;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaRelationTypeSideNotExists;
import org.eclipse.osee.orcs.core.internal.search.CallableQueryFactory;
import org.eclipse.osee.orcs.core.internal.types.impl.OrcsTypesImpl;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.ArtifactTypes;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;

/**
 * @author Roberto E. Escobar
 * @author Ryan D. Brooks
 */
public final class QueryData implements QueryBuilder, HasOptions, HasBranch {
   private final List<List<Criteria>> criterias;
   private final SelectData selectData;
   private final Options options;
   private final BranchId branch;
   private final ArtifactId view;
   private final QueryData parentQueryData;
   private final List<QueryData> childrenQueryData = new ArrayList<>(2);
   private AttributeTypeId attributeType = AttributeTypeToken.SENTINEL;
   private final CallableQueryFactory artQueryFactory;
   private final QueryFactory queryFactory;
   private final QueryEngine queryEngine;
   private final OrcsTypes orcsTypes;
   private final ArtifactTypes artifactTypeCache;
   private final AttributeTypes attributeTypeCache;
   private final HashMap<TableEnum, String> mainAliases = new HashMap<>(4);
   private QueryType queryType;
   private boolean followCausesChild = true;

   public QueryData(QueryData parentQueryData, QueryFactory queryFactory, QueryEngine queryEngine, CallableQueryFactory artQueryFactory, OrcsTypes orcsTypes, BranchId branch, ArtifactId view) {
      this.parentQueryData = parentQueryData;
      this.queryFactory = queryFactory;
      this.queryEngine = queryEngine;
      this.artQueryFactory = artQueryFactory;
      this.criterias = new ArrayList<>();
      this.selectData = new SelectData();
      this.options = OptionsUtil.createOptions();
      this.branch = branch;
      this.view = view;
      criterias.add(new ArrayList<>());
      this.orcsTypes = orcsTypes;
      this.artifactTypeCache = orcsTypes.getArtifactTypes();
      this.attributeTypeCache = orcsTypes.getAttributeTypes();
   }

   public QueryData(QueryFactory queryFactory, QueryEngine queryEngine, CallableQueryFactory artQueryFactory, OrcsTypes orcsTypes, BranchId branch, ArtifactId view) {
      this(null, queryFactory, queryEngine, artQueryFactory, orcsTypes, branch, view);
   }

   public QueryData(QueryData parentQueryData) {
      this(parentQueryData, parentQueryData.queryFactory, parentQueryData.queryEngine, parentQueryData.artQueryFactory,
         parentQueryData.orcsTypes, parentQueryData.branch, parentQueryData.view);
   }

   public QueryData(QueryFactory queryFactory, QueryEngine queryEngine, CallableQueryFactory artQueryFactory, OrcsTypes orcsTypes, BranchId branch) {
      this(queryFactory, queryEngine, artQueryFactory, orcsTypes, branch, ArtifactId.SENTINEL);
   }

   public QueryData(QueryFactory queryFactory, QueryEngine queryEngine, CallableQueryFactory artQueryFactory, OrcsTypes orcsTypes) {
      this(queryFactory, queryEngine, artQueryFactory, orcsTypes, BranchId.SENTINEL, ArtifactId.SENTINEL);
   }

   public QueryData(QueryType queryType, OrcsTypes orcsTypes) {
      this(null, null, null, orcsTypes);
      setQueryType(queryType);
   }

   public static QueryData mock() {
      return new QueryData(null, null, null, new OrcsTypesImpl(null, null, null, null, null), BranchId.SENTINEL);
   }

   public ArtifactId getView() {
      return view;
   }

   @Override
   public Options getOptions() {
      return options;
   }

   public boolean isSelectQueryType() {
      return queryType == QueryType.SELECT;
   }

   public boolean isCountQueryType() {
      return queryType == QueryType.COUNT;
   }

   public boolean isTokenQueryType() {
      return queryType == QueryType.TOKEN;
   }

   public boolean isIdQueryType() {
      return queryType == QueryType.ID;
   }

   public boolean isAttributesOnlyQueryType() {
      return queryType == QueryType.ATTRIBUTES_ONLY;
   }

   public List<Criteria> getAllCriteria() {
      List<Criteria> allCriterias = new ArrayList<>();
      for (List<Criteria> list : criterias) {
         allCriterias.addAll(list);
      }
      return allCriterias;
   }

   public boolean hasNoCriteria() {
      return criterias.get(0).isEmpty();
   }

   /**
    * Prove this CriteriaSet nesting is no longer needed except for orcs script follow relation
    */
   public List<Criteria> getOnlyCriteriaSet() {
      if (criterias.size() > 1) {
         throw new OseeStateException("Expected excactly one criteria set not:" + criterias.size());
      }
      return criterias.get(0);
   }

   public List<List<Criteria>> getCriteriaSets() {
      return Collections.unmodifiableList(criterias);
   }

   public List<Criteria> getLastCriteriaSet() {
      return criterias.get(criterias.size() - 1);
   }

   public List<Criteria> newCriteriaSet() {
      List<Criteria> criteriaSet = new ArrayList<>();
      criterias.add(criteriaSet);
      selectData.newSelectSet();
      return criteriaSet;
   }

   public SelectSet getSelectSet() {
      SelectSet data = selectData.getLast();
      if (data == null) {
         data = selectData.newSelectSet();
      }
      return data;
   }

   public List<SelectSet> getSelectSets() {
      return selectData.getAll();
   }

   public void addCriteria(Criteria... criterias) {
      List<Criteria> criteriaSet = getLastCriteriaSet();
      for (Criteria criteria : criterias) {
         criteriaSet.add(criteria);
      }
   }

   public void addCriteria(Criteria criteria) {
      getLastCriteriaSet().add(criteria);
   }

   public boolean hasCriteriaType(Class<? extends Criteria> type) {
      for (List<Criteria> criteriaSet : criterias) {
         for (Criteria criteria : criteriaSet) {
            if (type.isInstance(criteria)) {
               return true;
            }
         }
      }
      return false;
   }

   public <T extends Criteria> List<T> getCriteriaByType(Class<T> type) {
      List<T> matchingCriteria = new ArrayList<>(2);
      for (List<Criteria> criteriaSet : criterias) {
         for (Criteria criteria : criteriaSet) {
            if (type.isInstance(criteria)) {
               matchingCriteria.add(type.cast(criteria));
            }
         }
      }
      return matchingCriteria;
   }

   public void reset() {
      OptionsUtil.reset(options);

      List<Criteria> criteriaSet = criterias.get(0);
      criteriaSet.clear();
      criterias.clear();
      criterias.add(criteriaSet);

      selectData.reset();
      mainAliases.clear();
      childrenQueryData.clear();
      followCausesChild = true;
      attributeType = AttributeTypeToken.SENTINEL;
   }

   @Override
   public String toString() {
      return "QueryData [criterias=" + criterias + ", selects=" + selectData + ", options=" + options + "]";
   }

   @Override
   public BranchId getBranch() {
      return branch;
   }

   public void select(AttributeTypeId attributeType) {
      this.attributeType = attributeType;
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

   public AttributeTypeId getAttributeType() {
      return attributeType;
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
   public QueryBuilder andId(ArtifactId id) {
      return addAndCheck(new CriteriaArtifactIds(id));
   }

   @Override
   public QueryBuilder andIds(Collection<? extends ArtifactId> ids) {
      return addAndCheck(new CriteriaArtifactIds(ids));
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
         addAndCheck(new CriteriaArtifactGuids(guids));
      }
      return this;
   }

   @Override
   public QueryBuilder andIsOfType(ArtifactTypeId... artifactType) {
      return andIsOfType(Arrays.asList(artifactType));
   }

   @Override
   public QueryBuilder andIsOfType(Collection<? extends ArtifactTypeId> artifactTypes) {
      return addAndCheck(new CriteriaArtifactType(artifactTypeCache, artifactTypes, true));
   }

   @Override
   public QueryBuilder andTypeEquals(ArtifactTypeId... artifactType) {
      return andTypeEquals(Arrays.asList(artifactType));
   }

   @Override
   public QueryBuilder andTypeEquals(Collection<? extends ArtifactTypeId> artifactTypes) {
      return addAndCheck(new CriteriaArtifactType(artifactTypeCache, artifactTypes, false));
   }

   @Override
   public QueryBuilder andExists(AttributeTypeId... attributeType) {
      return andExists(Arrays.asList(attributeType));
   }

   @Override
   public QueryBuilder andExists(Collection<AttributeTypeId> attributeTypes) {
      return addAndCheck(new CriteriaAttributeTypeExists(attributeTypes));
   }

   @Override
   public QueryBuilder andNotExists(AttributeTypeId attributeType) {
      return addAndCheck(new CriteriaAttributeTypeNotExists(attributeType));
   }

   @Override
   public QueryBuilder andNotExists(AttributeTypeId attributeType, String value) {
      return addAndCheck(new CriteriaAttributeTypeNotExists(attributeType, value));
   }

   @Override
   public QueryBuilder andNotExists(Collection<AttributeTypeId> attributeTypes) {
      return addAndCheck(new CriteriaAttributeTypeNotExists(attributeTypes));
   }

   @Override
   public QueryBuilder andExists(IRelationType relationType) {
      return addAndCheck(new CriteriaRelationTypeExists(relationType));
   }

   @Override
   public QueryBuilder andNotExists(IRelationType relationType) {
      return addAndCheck(new CriteriaRelationTypeNotExists(relationType));
   }

   @Override
   public QueryBuilder andNotExists(RelationTypeSide relationTypeSide) {
      return addAndCheck(new CriteriaRelationTypeSideNotExists(relationTypeSide));
   }

   @Override
   public QueryBuilder andExists(RelationTypeSide relationTypeSide) {
      return addAndCheck(new CriteriaRelationTypeSideExists(relationTypeSide));
   }

   @Override
   public QueryBuilder and(AttributeTypeId attributeType, Collection<String> values, QueryOption... options) {
      return and(Collections.singleton(attributeType), values, options);
   }

   @Override
   public QueryBuilder andAttributeIs(AttributeTypeId attributeType, String value) {
      return addAndCheck(new CriteriaAttributeRaw(Collections.singleton(attributeType), Collections.singleton(value)));
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
   public QueryBuilder and(Collection<AttributeTypeId> attributeTypes, Collection<String> values, QueryOption... options) {
      boolean isIncludeAllTypes = attributeTypes.contains(QueryBuilder.ANY_ATTRIBUTE_TYPE);
      return addAndCheck(
         new CriteriaAttributeKeywords(isIncludeAllTypes, attributeTypes, attributeTypeCache, values, options));
   }

   @Override
   public QueryBuilder andNameEquals(String artifactName) {
      return andAttributeIs(CoreAttributeTypes.Name, artifactName);
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
      return addAndCheck(new CriteriaRelatedTo(relationTypeSide, artifacts));
   }

   @Override
   public QueryBuilder andRelatedTo(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      return addAndCheck(new CriteriaRelatedTo(relationTypeSide, artifactId));
   }

   @Override
   public QueryBuilder andRelatedRecursive(RelationTypeSide relationTypeSide, ArtifactId artifactId) {
      return addAndCheck(new CriteriaRelatedRecursive(relationTypeSide, artifactId));
   }

   @Override
   public QueryBuilder followRelation(RelationTypeSide relationTypeSide) {
      addAndCheck(new CriteriaRelationTypeFollow(relationTypeSide, ArtifactTypeToken.SENTINEL, true));
      newCriteriaSet();
      return this;
   }

   private QueryBuilder addAndCheck(Criteria criteria) {
      criteria.checkValid(getOptions());
      addCriteria(criteria);
      return this;
   }

   @Override
   public QueryBuilder andIsHeirarchicalRootArtifact() {
      andId(CoreArtifactTokens.DefaultHierarchyRoot);
      return this;
   }

   @Override
   public QueryBuilder follow(RelationTypeSide relationTypeSide) {
      return follow(relationTypeSide, ArtifactTypeToken.SENTINEL, true);
   }

   @Override
   public QueryBuilder follow(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType) {
      return follow(relationTypeSide, artifactType, true);
   }

   @Override
   public QueryBuilder followNoSelect(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType) {
      return follow(relationTypeSide, artifactType, false);
   }

   private QueryBuilder follow(RelationTypeSide relationTypeSide, ArtifactTypeToken artifactType, boolean terminalFollow) {
      QueryData followQueryData = followQueryData();
      followQueryData.followCausesChild = terminalFollow;
      followQueryData.addAndCheck(new CriteriaRelationTypeFollow(relationTypeSide, artifactType, terminalFollow));
      return followQueryData;
   }

   private QueryData followQueryData() {
      if (followCausesChild) {
         QueryData child = new QueryData(this);
         childrenQueryData.add(child);
         return child;
      }
      return this;
   }

   @Override
   public List<Map<String, Object>> asArtifactMaps() {
      setQueryType(QueryType.ATTRIBUTES_ONLY);
      return queryEngine.asArtifactMaps(this);
   }

   @Override
   public Map<ArtifactId, ArtifactReadable> asArtifactMap() {
      setQueryType(QueryType.SELECT);
      return queryEngine.asArtifactMap(this, queryFactory);
   }

   @Override
   public List<ArtifactReadable> asArtifacts() {
      setQueryType(QueryType.SELECT);
      return queryEngine.asArtifacts(this, queryFactory);
   }

   @Override
   public ArtifactReadable asArtifact() {
      return asArtifact(this::asArtifacts);
   }

   @Override
   public ArtifactToken asArtifactToken() {
      return asArtifact(this::asArtifactTokens);
   }

   @Override
   public List<ArtifactToken> asArtifactTokens() {
      return asArtifactTokens(Name);
   }

   @Override
   public Map<ArtifactId, ArtifactToken> asArtifactTokenMap() {
      select(Name);
      return queryEngine.asArtifactTokenMap(this);
   }

   @Override
   public List<ArtifactToken> asArtifactTokens(AttributeTypeId attributeType) {
      setQueryType(QueryType.TOKEN);
      select(attributeType);
      return queryEngine.asArtifactTokens(this);
   }

   @Override
   public Map<ArtifactId, ArtifactReadable> loadArtifactMap() {
      Map<ArtifactId, ArtifactReadable> artifacts = new HashMap<>(10000);
      getResults().forEach(artifact -> artifacts.put(artifact, artifact));
      return artifacts;
   }

   @Override
   public ArtifactId asArtifactId() {
      return asArtifact(this::asArtifactIds);
   }

   @Override
   public ArtifactId asArtifactIdOrSentinel() {
      return asArtifactOrSentinel(this::asArtifactIds, ArtifactId.SENTINEL);
   }

   @Override
   public ArtifactToken asArtifactTokenOrSentinel() {
      return asArtifactOrSentinel(this::asArtifactTokens, ArtifactToken.SENTINEL);
   }

   private <T> T asArtifact(Supplier<List<T>> supplier) {
      List<T> artifacts = supplier.get();
      if (artifacts.size() != 1) {
         throw new OseeCoreException("Expected exactly 1 artifact not %s", artifacts.size());
      }
      return artifacts.get(0);
   }

   private <T> T asArtifactOrSentinel(Supplier<List<T>> supplier, T sentinel) {
      List<T> artifacts = supplier.get();
      if (artifacts.size() > 1) {
         throw new OseeCoreException("Expected at most 1 artifact not %s", artifacts.size());
      } else if (artifacts.size() == 1) {
         return artifacts.get(0);
      }
      return sentinel;
   }

   @Override
   public List<ArtifactId> asArtifactIds() {
      setQueryType(QueryType.ID);
      return queryEngine.asArtifactIds(this);
   }

   @Override
   public ResultSet<ArtifactReadable> getResults() {
      setQueryType(QueryType.SELECT);
      try {
         return artQueryFactory.createSearch(null, this).call();
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
         return artQueryFactory.createSearchWithMatches(null, this).call();
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   @Override
   public int getCount() {
      setQueryType(QueryType.COUNT);
      return queryEngine.getArtifactCount(this);
   }

   @Override
   public boolean exists() {
      return getCount() > 0;
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

   public void setQueryType(QueryType queryType) {
      this.queryType = queryType;
      if (parentQueryData != null) {
         parentQueryData.setQueryType(queryType);
      }
   }

   public String getMainTableAlias(TableEnum table, Function<TableEnum, String> addTable) {
      String alias = mainAliases.get(table);
      if (alias == null) {
         alias = addTable.apply(table);
         mainAliases.put(table, alias);
      }
      return alias;
   }

   public boolean mainTableAliasExists(TableEnum table) {
      return mainAliases.containsKey(table);
   }

   public QueryData getRootQueryData() {
      if (parentQueryData == null) {
         return this;
      }
      return parentQueryData.getRootQueryData();
   }

   public QueryData getParentQueryData() {
      return parentQueryData;
   }

   public List<QueryData> getChildrenQueryData() {
      return childrenQueryData;
   }

   public boolean isFollowCausesChild() {
      return followCausesChild;
   }
}