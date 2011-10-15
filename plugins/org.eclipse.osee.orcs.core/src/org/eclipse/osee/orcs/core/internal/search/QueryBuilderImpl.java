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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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

   private final ResultSetFactory rsetFactory;
   private final CriteriaFactory criteriaFactory;

   private final SessionContext sessionContext;
   private final CriteriaSet criteriaSet;
   private final QueryOptions options;

   public QueryBuilderImpl(ResultSetFactory rsetFactory, CriteriaFactory criteriaFactory, SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options) {
      this.rsetFactory = rsetFactory;
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
   public QueryBuilder withLocalId(int... artifactIds) throws OseeCoreException {
      Set<Integer> ids = new HashSet<Integer>();
      for (Integer id : artifactIds) {
         ids.add(id);
      }
      return withLocalIds(ids);
   }

   @Override
   public QueryBuilder withGuidsOrHrids(String... ids) throws OseeCoreException {
      return withGuidsOrHrids(Arrays.asList(ids));
   }

   @Override
   public QueryBuilder withLocalIds(Collection<Integer> artifactIds) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createArtifactIdCriteria(artifactIds);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder withGuidsOrHrids(Collection<String> ids) throws OseeCoreException {
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
      if (guids.isEmpty()) {
         Criteria guidCriteria = criteriaFactory.createArtifactGuidCriteria(guids);
         addAndCheck(guidCriteria);
      }

      if (hrids.isEmpty()) {
         Criteria hridCriteria = criteriaFactory.createArtifactHridCriteria(hrids);
         addAndCheck(hridCriteria);
      }
      return this;
   }

   @Override
   public QueryBuilder and(IArtifactType... artifactType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createArtifactTypeCriteria(Arrays.asList(artifactType));
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(Collection<? extends IArtifactType> artifactType) throws OseeCoreException {
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
         criteriaFactory.createAttributeCriteria(attributeType, operator, match, Collections.singleton(value));
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, StringOperator operator, CaseType match, Collection<String> values) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createAttributeCriteria(attributeType, operator, match, values);
      return addAndCheck(criteria);
   }

   private QueryBuilder addAndCheck(Criteria criteria) throws OseeCoreException {
      criteria.checkValid(options);
      criteriaSet.add(criteria);
      return this;
   }

   @Override
   public ResultSet<ReadableArtifact> build(LoadLevel loadLevel) throws OseeCoreException {
      return rsetFactory.createResultSet(sessionContext, loadLevel, criteriaSet.clone(), options.clone());
   }

   @Override
   public ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> buildMatches(LoadLevel loadLevel) throws OseeCoreException {
      return rsetFactory.createMatchesResultSet(sessionContext, loadLevel, criteriaSet.clone(), options.clone());
   }

   @Override
   public int getCount() throws OseeCoreException {
      return rsetFactory.getCount(sessionContext, criteriaSet.clone(), options.clone());
   }
}
