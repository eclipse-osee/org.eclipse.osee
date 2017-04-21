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
import java.util.HashSet;
import java.util.Set;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.ApplicabilityQuery;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Roberto E. Escobar
 */
public class QueryFactoryImpl implements QueryFactory {

   private final OrcsSession context;
   private final CriteriaFactory criteriaFctry;
   private final CallableQueryFactory queryFctry;
   private final BranchCriteriaFactory branchCriteriaFactory;
   private final BranchCallableQueryFactory branchQueryFactory;
   private final TransactionCallableQueryFactory txQueryFactory;
   private final TransactionCriteriaFactory txCriteriaFactory;
   private final TupleQuery tupleQuery;
   private final ApplicabilityDsQuery applicabilityDsQuery;

   public QueryFactoryImpl(OrcsSession context, CriteriaFactory criteriaFctry, CallableQueryFactory queryFctry, BranchCriteriaFactory branchCriteriaFactory, BranchCallableQueryFactory branchQueryFactory, TransactionCallableQueryFactory txQueryFactory, TransactionCriteriaFactory txCriteriaFactory, TupleQuery tupleQuery, ApplicabilityDsQuery applicabilityDsQuery) {
      this.context = context;
      this.criteriaFctry = criteriaFctry;
      this.queryFctry = queryFctry;
      this.branchCriteriaFactory = branchCriteriaFactory;
      this.branchQueryFactory = branchQueryFactory;
      this.txQueryFactory = txQueryFactory;
      this.txCriteriaFactory = txCriteriaFactory;
      this.tupleQuery = tupleQuery;
      this.applicabilityDsQuery = applicabilityDsQuery;
   }

   private QueryBuilder createBuilder(BranchId branchId) {
      Options options = OptionsUtil.createOptions();
      CriteriaSet criteriaSet = new CriteriaSet();
      if (branchId != null) {
         criteriaSet.add(new CriteriaBranch(branchId));
      }
      QueryData queryData = new QueryData(criteriaSet, options);
      QueryBuilder builder = new QueryBuilderImpl(queryFctry, criteriaFctry, context, queryData);
      return builder;
   }

   @Override
   public BranchQuery branchQuery() {
      Options options = OptionsUtil.createOptions();
      CriteriaSet criteriaSet = new CriteriaSet();
      QueryData queryData = new QueryData(criteriaSet, options);
      BranchQueryImpl query = new BranchQueryImpl(branchQueryFactory, branchCriteriaFactory, context, queryData);
      return query;
   }

   @Override
   public QueryBuilder fromBranch(BranchId branch) throws OseeCoreException {
      return createBuilder(branch);
   }

   @Override
   public QueryBuilder fromArtifacts(Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(artifacts, "artifacts");
      ArtifactReadable artifact = artifacts.iterator().next();
      Set<String> guids = new HashSet<>();
      for (ArtifactReadable art : artifacts) {
         guids.add(art.getGuid());
      }
      return fromBranch(artifact.getBranch()).andGuids(guids);
   }

   @Override
   public QueryBuilder fromArtifactTypeAllBranches(IArtifactType artifactType) throws OseeCoreException {
      QueryBuilder builder = createBuilder(null);
      builder.andIsOfType(artifactType);
      return builder;
   }

   @Override
   public TransactionQuery transactionQuery() {
      Options options = OptionsUtil.createOptions();
      CriteriaSet criteriaSet = new CriteriaSet();
      QueryData queryData = new QueryData(criteriaSet, options);
      TransactionQueryImpl query = new TransactionQueryImpl(txQueryFactory, txCriteriaFactory, context, queryData);
      return query;
   }

   @Override
   public TupleQuery tupleQuery() {
      return tupleQuery;
   }

   @Override
   public ApplicabilityQuery applicabilityQuery() {
      return new ApplicabilityQueryImpl(tupleQuery, applicabilityDsQuery);
   }
}