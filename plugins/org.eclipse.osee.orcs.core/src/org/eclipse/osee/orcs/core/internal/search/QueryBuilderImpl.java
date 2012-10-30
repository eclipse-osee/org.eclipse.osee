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

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author Roberto E. Escobar
 */
public class QueryBuilderImpl implements QueryBuilder {

   private final CallableQueryFactory queryFactory;
   private final CriteriaFactory criteriaFactory;

   private final SessionContext sessionContext;
   private final QueryData queryData;

   public QueryBuilderImpl(CallableQueryFactory queryFactory, CriteriaFactory criteriaFactory, SessionContext sessionContext, QueryData queryData) {
      this.queryFactory = queryFactory;
      this.criteriaFactory = criteriaFactory;
      this.sessionContext = sessionContext;
      this.queryData = queryData;
   }

   private QueryData getQueryData() {
      return queryData;
   }

   private QueryOptions getOptions() {
      return queryData.getOptions();
   }

   @Override
   public QueryBuilder includeCache() {
      includeCache(true);
      return this;
   }

   @Override
   public QueryBuilder includeCache(boolean enabled) {
      getOptions().setIncludeCache(enabled);
      return this;
   }

   @Override
   public boolean isCacheIncluded() {
      return getOptions().isCacheIncluded();
   }

   @Override
   public QueryBuilder includeDeleted() {
      includeDeleted(true);
      return this;
   }

   @Override
   public QueryBuilder includeDeleted(boolean enabled) {
      getOptions().setIncludeDeleted(enabled);
      return this;
   }

   @Override
   public boolean areDeletedIncluded() {
      return getOptions().areDeletedIncluded();
   }

   @Override
   public QueryBuilder includeTypeInheritance() {
      includeTypeInheritance(true);
      return this;
   }

   @Override
   public QueryBuilder includeTypeInheritance(boolean enabled) {
      getOptions().setIncludeTypeInheritance(enabled);
      return this;
   }

   @Override
   public boolean isTypeInheritanceIncluded() {
      return getOptions().isTypeInheritanceIncluded();
   }

   @Override
   public QueryBuilder fromTransaction(int transactionId) {
      getOptions().setFromTransaction(transactionId);
      return this;
   }

   @Override
   public int getFromTransaction() {
      return getOptions().getFromTransaction();
   }

   @Override
   public QueryBuilder headTransaction() {
      getOptions().setHeadTransaction();
      return this;
   }

   @Override
   public boolean isHeadTransaction() {
      return getOptions().isHeadTransaction();
   }

   @Override
   public QueryBuilder excludeCache() {
      includeCache(false);
      return this;
   }

   @Override
   public QueryBuilder excludeDeleted() {
      includeDeleted(false);
      return this;
   }

   @Override
   public QueryBuilder excludeTypeInheritance() {
      includeTypeInheritance(false);
      return this;
   }

   @Override
   public QueryBuilder resetToDefaults() {
      getOptions().reset();
      return this;
   }

   @Override
   public QueryBuilder andLocalId(int... artifactIds) throws OseeCoreException {
      Set<Integer> ids = new HashSet<Integer>();
      for (Integer id : artifactIds) {
         ids.add(id);
      }
      return andLocalIds(ids);
   }

   @Override
   public QueryBuilder andGuidsOrHrids(String... ids) throws OseeCoreException {
      return andGuidsOrHrids(Arrays.asList(ids));
   }

   @Override
   public QueryBuilder andLocalIds(Collection<Integer> artifactIds) throws OseeCoreException {
      Criteria<QueryOptions> criteria = criteriaFactory.createArtifactIdCriteria(artifactIds);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder andGuidsOrHrids(Collection<String> ids) throws OseeCoreException {
      Set<String> guids = new HashSet<String>();
      Set<String> hrids = new HashSet<String>();
      Set<String> invalids = new HashSet<String>();
      for (String id : ids) {
         if (GUID.isValid(id)) {
            guids.add(id);
         } else if (HumanReadableId.isValid(id)) {
            hrids.add(id);
         } else {
            invalids.add(id);
         }
      }
      Conditions.checkExpressionFailOnTrue(!invalids.isEmpty(), "Invalid guids or hrids detected - %s", invalids);
      if (!guids.isEmpty()) {
         Criteria<QueryOptions> guidCriteria = criteriaFactory.createArtifactGuidCriteria(guids);
         addAndCheck(guidCriteria);
      }

      if (!hrids.isEmpty()) {
         Criteria<QueryOptions> hridCriteria = criteriaFactory.createArtifactHridCriteria(hrids);
         addAndCheck(hridCriteria);
      }
      return this;
   }

   @Override
   public QueryBuilder andIsOfType(IArtifactType... artifactType) throws OseeCoreException {
      Criteria<QueryOptions> criteria = criteriaFactory.createArtifactTypeCriteria(Arrays.asList(artifactType));
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder andIsOfType(Collection<? extends IArtifactType> artifactType) throws OseeCoreException {
      Criteria<QueryOptions> criteria = criteriaFactory.createArtifactTypeCriteria(artifactType);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder andExists(IAttributeType... attributeType) throws OseeCoreException {
      return andExists(Arrays.asList(attributeType));
   }

   @Override
   public QueryBuilder andExists(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException {
      Criteria<QueryOptions> criteria = criteriaFactory.createExistsCriteria(attributeTypes);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder andExists(IRelationType relationType) throws OseeCoreException {
      Criteria<QueryOptions> criteria = criteriaFactory.createExistsCriteria(relationType);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, Operator operator, String value) throws OseeCoreException {
      Criteria<QueryOptions> criteria =
         criteriaFactory.createAttributeCriteria(attributeType, operator, Collections.singleton(value));
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, Operator operator, Collection<String> values) throws OseeCoreException {
      Criteria<QueryOptions> criteria = criteriaFactory.createAttributeCriteria(attributeType, operator, values);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, StringOperator operator, CaseType match, String value) throws OseeCoreException {
      Criteria<QueryOptions> criteria =
         criteriaFactory.createAttributeCriteria(Collections.singleton(attributeType), operator, match, value);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(Collection<? extends IAttributeType> attributeTypes, StringOperator operator, CaseType match, String value) throws OseeCoreException {
      Criteria<QueryOptions> criteria = criteriaFactory.createAttributeCriteria(attributeTypes, operator, match, value);
      return addAndCheck(criteria);
   }

   private QueryBuilder addAndCheck(Criteria<QueryOptions> criteria) throws OseeCoreException {
      criteria.checkValid(getOptions());
      getQueryData().addCriteria(criteria);
      return this;
   }

   @Override
   public QueryBuilder andNameEquals(String artifactName) throws OseeCoreException {
      return and(CoreAttributeTypes.Name, Operator.EQUAL, artifactName);
   }

   @Override
   public QueryBuilder andIds(IArtifactToken... artifactToken) throws OseeCoreException {
      return andIds(Arrays.asList(artifactToken));
   }

   @Override
   public QueryBuilder andIds(Collection<? extends IArtifactToken> artifactTokens) throws OseeCoreException {
      Set<String> guids = new HashSet<String>();
      for (IArtifactToken token : artifactTokens) {
         guids.add(token.getGuid());
      }
      return andGuidsOrHrids(guids);
   }

   @Override
   public QueryBuilder andRelatedTo(IRelationTypeSide relationTypeSide, ArtifactReadable... artifacts) throws OseeCoreException {
      return andRelatedTo(relationTypeSide, Arrays.asList(artifacts));
   }

   @Override
   public QueryBuilder andRelatedTo(IRelationTypeSide relationTypeSide, Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException {
      Set<Integer> ids = new HashSet<Integer>();
      for (HasLocalId token : artifacts) {
         ids.add(token.getLocalId());
      }
      return andRelatedToLocalIds(relationTypeSide, ids);
   }

   @Override
   public QueryBuilder andRelatedToLocalIds(IRelationTypeSide relationTypeSide, int... artifactIds) throws OseeCoreException {
      Set<Integer> ids = new HashSet<Integer>();
      for (Integer id : artifactIds) {
         ids.add(id);
      }
      return andRelatedToLocalIds(relationTypeSide, ids);
   }

   @Override
   public QueryBuilder andRelatedToLocalIds(IRelationTypeSide relationTypeSide, Collection<Integer> artifactIds) throws OseeCoreException {
      Criteria<QueryOptions> criteria = criteriaFactory.createRelatedToCriteria(relationTypeSide, artifactIds);
      return addAndCheck(criteria);
   }

   @Override
   public ResultSet<ArtifactReadable> getResults() throws OseeCoreException {
      ResultSet<ArtifactReadable> result = null;
      try {
         result = createSearch().call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getMatches() throws OseeCoreException {
      ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> result = null;
      try {
         result = createSearchWithMatches().call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public int getCount() throws OseeCoreException {
      Integer result = -1;
      try {
         result = createCount().call();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public CancellableCallable<Integer> createCount() {
      return queryFactory.createCount(sessionContext, getQueryData().clone());
   }

   @Override
   public CancellableCallable<ResultSet<ArtifactReadable>> createSearch() {
      return queryFactory.createSearch(sessionContext, getQueryData().clone());
   }

   @Override
   public CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches() {
      return queryFactory.createSearchWithMatches(sessionContext, getQueryData().clone());
   }

}
