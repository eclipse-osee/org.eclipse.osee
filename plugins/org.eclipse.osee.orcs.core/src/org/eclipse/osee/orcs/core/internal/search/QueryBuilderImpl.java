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
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.core.exception.OseeExceptions;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.GUID;
import org.eclipse.osee.framework.jdk.core.util.HumanReadableId;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.ResultSet;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author Roberto E. Escobar
 */
public class QueryBuilderImpl implements QueryBuilder {

   private final QueryExecutor queryExecutor;
   private final CriteriaFactory criteriaFactory;

   private final SessionContext sessionContext;
   private final CriteriaSet criteriaSet;
   private final QueryOptions options;

   public QueryBuilderImpl(QueryExecutor queryExecutor, CriteriaFactory criteriaFactory, SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options) {
      this.queryExecutor = queryExecutor;
      this.criteriaFactory = criteriaFactory;
      this.sessionContext = sessionContext;
      this.criteriaSet = criteriaSet;
      this.options = options;
   }

   @Override
   public QueryBuilder includeCache() {
      includeCache(true);
      return this;
   }

   @Override
   public QueryBuilder includeCache(boolean enabled) {
      options.setIncludeCache(enabled);
      return this;
   }

   @Override
   public boolean isCacheIncluded() {
      return options.isCacheIncluded();
   }

   @Override
   public QueryBuilder includeDeleted() {
      includeDeleted(true);
      return this;
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
   public QueryBuilder includeTypeInheritance() {
      includeTypeInheritance(true);
      return this;
   }

   @Override
   public QueryBuilder includeTypeInheritance(boolean enabled) {
      options.setIncludeTypeInheritance(enabled);
      return this;
   }

   @Override
   public boolean isTypeInheritanceIncluded() {
      return options.isTypeInheritanceIncluded();
   }

   @Override
   public QueryBuilder fromTransaction(int transactionId) {
      options.setFromTransaction(transactionId);
      return this;
   }

   @Override
   public int getFromTransaction() {
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
      options.reset();
      criteriaSet.reset();
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
      Criteria criteria = criteriaFactory.createArtifactIdCriteria(artifactIds);
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
         Criteria guidCriteria = criteriaFactory.createArtifactGuidCriteria(guids);
         addAndCheck(guidCriteria);
      }

      if (!hrids.isEmpty()) {
         Criteria hridCriteria = criteriaFactory.createArtifactHridCriteria(hrids);
         addAndCheck(hridCriteria);
      }
      return this;
   }

   @Override
   public QueryBuilder andIsOfType(IArtifactType... artifactType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteria(Arrays.asList(artifactType));
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder andIsOfType(Collection<? extends IArtifactType> artifactType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteria(artifactType);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder andExists(IAttributeType... attributeType) throws OseeCoreException {
      return andExists(Arrays.asList(attributeType));
   }

   @Override
   public QueryBuilder andExists(Collection<? extends IAttributeType> attributeTypes) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createExistsCriteria(attributeTypes);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder andExists(IRelationTypeSide relationType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createExistsCriteria(relationType);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, Operator operator, String value) throws OseeCoreException {
      Criteria criteria =
         criteriaFactory.createAttributeCriteria(attributeType, operator, Collections.singleton(value));
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, Operator operator, Collection<String> values) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createAttributeCriteria(attributeType, operator, values);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, StringOperator operator, CaseType match, String value) throws OseeCoreException {
      Criteria criteria =
         criteriaFactory.createAttributeCriteria(Collections.singleton(attributeType), operator, match, value);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(Collection<? extends IAttributeType> attributeTypes, StringOperator operator, CaseType match, String value) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createAttributeCriteria(attributeTypes, operator, match, value);
      return addAndCheck(criteria);
   }

   private QueryBuilder addAndCheck(Criteria criteria) throws OseeCoreException {
      criteria.checkValid(options);
      criteriaSet.add(criteria);
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
   public ResultSet<ReadableArtifact> getResults() throws OseeCoreException {
      ResultSet<ReadableArtifact> result = null;
      try {
         result = search().get();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> getMatches() throws OseeCoreException {
      ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> result = null;
      try {
         result = searchWithMatches().get();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public int getCount() throws OseeCoreException {
      int result = -1;
      try {
         result = computeCount().get();
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return result;
   }

   @Override
   public Future<Integer> computeCount() throws OseeCoreException {
      return computeCount(null);
   }

   @Override
   public Future<ResultSet<ReadableArtifact>> search() throws OseeCoreException {
      return search(null);
   }

   @Override
   public Future<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> searchWithMatches() throws OseeCoreException {
      return searchWithMatches(null);
   }

   @Override
   public Future<Integer> computeCount(ExecutionCallback<Integer> callback) throws OseeCoreException {
      Future<Integer> toReturn = null;
      try {
         toReturn = queryExecutor.scheduleCount(sessionContext, criteriaSet.clone(), options.clone(), callback);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

   @Override
   public Future<ResultSet<ReadableArtifact>> search(ExecutionCallback<ResultSet<ReadableArtifact>> callback) throws OseeCoreException {
      Future<ResultSet<ReadableArtifact>> toReturn = null;
      try {
         toReturn = queryExecutor.scheduleSearch(sessionContext, criteriaSet.clone(), options.clone(), callback);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }

   @Override
   public Future<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> searchWithMatches(ExecutionCallback<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> callback) throws OseeCoreException {
      Future<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> toReturn = null;
      try {
         toReturn =
            queryExecutor.scheduleSearchWithMatches(sessionContext, criteriaSet.clone(), options.clone(), callback);
      } catch (Exception ex) {
         OseeExceptions.wrapAndThrow(ex);
      }
      return toReturn;
   }
}
