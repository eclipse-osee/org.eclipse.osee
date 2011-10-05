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

import java.util.Collection;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.Criteria;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class QueryBuilderImpl implements QueryBuilder {

   private final ResultSetFactory rsetFactory;
   private final CriteriaFactory criteriaFactory;

   private final CriteriaSet criteriaSet;
   private final QueryOptions options;

   public QueryBuilderImpl(ResultSetFactory rsetFactory, CriteriaFactory criteriaFactory, CriteriaSet criteriaSet, QueryOptions options) {
      this.rsetFactory = rsetFactory;
      this.criteriaFactory = criteriaFactory;
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
   public QueryBuilder andExists(IAttributeType attributeType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createExistsCriteria(attributeType);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder andExists(IRelationTypeSide relationType) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createExistsCriteria(relationType);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, Operator operator, String value) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createAttributeCriteria(attributeType, operator, value);
      return addAndCheck(criteria);
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, Operator operator, Collection<String> values) throws OseeCoreException {
      Criteria criteria = criteriaFactory.createAttributeCriteria(attributeType, operator, values);
      return addAndCheck(criteria);
   }

   private QueryBuilder addAndCheck(Criteria criteria) throws OseeCoreException {
      criteria.checkValid(options);
      criteriaSet.add(criteria);
      return this;
   }

   @Override
   public ResultSet<ReadableArtifact> build(LoadLevel loadLevel) throws OseeCoreException {
      return rsetFactory.createResultSet(criteriaSet.clone(), options.clone());
   }

   @Override
   public int getCount() throws OseeCoreException {
      return rsetFactory.getCount(criteriaSet.clone(), options.clone());
   }
}
