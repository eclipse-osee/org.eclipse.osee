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
package org.eclipse.osee.orcs.rest.mocks;

import java.util.Collection;
import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.IArtifactToken;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IAttributeType;
import org.eclipse.osee.framework.core.data.IRelationTypeSide;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.CaseType;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.Operator;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.StringOperator;

/**
 * @author John R. Misinco
 */
public class MockQueryBuilder implements QueryBuilder {

   @Override
   public QueryBuilder includeCache() {
      return this;
   }

   @Override
   public QueryBuilder includeCache(boolean enabled) {
      return this;
   }

   @Override
   public boolean isCacheIncluded() {
      return false;
   }

   @Override
   public QueryBuilder includeDeleted() {
      return this;
   }

   @Override
   public QueryBuilder includeDeleted(boolean enabled) {
      return this;
   }

   @Override
   public boolean areDeletedIncluded() {
      return false;
   }

   @Override
   public QueryBuilder includeTypeInheritance() {
      return this;
   }

   @Override
   public QueryBuilder includeTypeInheritance(boolean enabled) {
      return this;
   }

   @Override
   public boolean isTypeInheritanceIncluded() {
      return false;
   }

   @Override
   public QueryBuilder fromTransaction(int transactionId) {
      return this;
   }

   @Override
   public int getFromTransaction() {
      return 0;
   }

   @Override
   public QueryBuilder headTransaction() {
      return this;
   }

   @Override
   public boolean isHeadTransaction() {
      return false;
   }

   @Override
   public QueryBuilder excludeCache() {
      return this;
   }

   @Override
   public QueryBuilder excludeDeleted() {
      return this;
   }

   @Override
   public QueryBuilder excludeTypeInheritance() {
      return this;
   }

   @Override
   public QueryBuilder resetToDefaults() {
      return this;
   }

   @Override
   public QueryBuilder andLocalId(int... artifactId) {
      return this;
   }

   @Override
   public QueryBuilder andLocalIds(Collection<Integer> artifactIds) {
      return this;
   }

   @Override
   public QueryBuilder andGuidsOrHrids(String... ids) {
      return this;
   }

   @Override
   public QueryBuilder andGuidsOrHrids(Collection<String> ids) {
      return this;
   }

   @Override
   public QueryBuilder andIds(IArtifactToken... artifactToken) {
      return this;
   }

   @Override
   public QueryBuilder andIds(Collection<? extends IArtifactToken> artifactTokens) {
      return this;
   }

   @Override
   public QueryBuilder andIsOfType(IArtifactType... artifactType) {
      return this;
   }

   @Override
   public QueryBuilder andIsOfType(Collection<? extends IArtifactType> artifactType) {
      return this;
   }

   @Override
   public QueryBuilder andExists(IAttributeType... attributeType) {
      return this;
   }

   @Override
   public QueryBuilder andExists(Collection<? extends IAttributeType> attributeTypes) {
      return this;
   }

   @Override
   public QueryBuilder andExists(IRelationTypeSide relationType) {
      return this;
   }

   @Override
   public QueryBuilder andNameEquals(String artifactName) {
      return this;
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, Operator operator, String value) {
      return this;
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, Operator operator, Collection<String> values) {
      return this;
   }

   @Override
   public QueryBuilder and(IAttributeType attributeType, StringOperator operator, CaseType match, String value) {
      return this;
   }

   @Override
   public QueryBuilder and(Collection<? extends IAttributeType> attributeTypes, StringOperator operator, CaseType match, String value) {
      return this;
   }

   @Override
   public ResultSet<ReadableArtifact> getResults() {
      return null;
   }

   @Override
   public ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> getMatches() {
      return null;
   }

   @Override
   public int getCount() {
      return 0;
   }

   @Override
   public CancellableCallable<Integer> createCount() {
      return null;
   }

   @Override
   public CancellableCallable<ResultSet<ReadableArtifact>> createSearch() {
      return null;
   }

   @Override
   public CancellableCallable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> createSearchWithMatches() {
      return null;
   }

}