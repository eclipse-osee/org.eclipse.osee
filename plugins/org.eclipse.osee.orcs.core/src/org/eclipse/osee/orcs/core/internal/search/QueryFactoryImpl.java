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
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.TokenFactory;
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.Options;
import org.eclipse.osee.orcs.core.ds.OptionsUtil;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaBranch;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.search.BranchQuery;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TransactionQuery;

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

   public QueryFactoryImpl(OrcsSession context, CriteriaFactory criteriaFctry, CallableQueryFactory queryFctry, BranchCriteriaFactory branchCriteriaFactory, BranchCallableQueryFactory branchQueryFactory, TransactionCallableQueryFactory txQueryFactory, TransactionCriteriaFactory txCriteriaFactory) {
      super();
      this.context = context;
      this.criteriaFctry = criteriaFctry;
      this.queryFctry = queryFctry;
      this.branchCriteriaFactory = branchCriteriaFactory;
      this.branchQueryFactory = branchQueryFactory;
      this.txQueryFactory = txQueryFactory;
      this.txCriteriaFactory = txCriteriaFactory;
   }

   private QueryBuilder createBuilder(IOseeBranch branch) {
      Options options = OptionsUtil.createOptions();
      CriteriaSet criteriaSet = new CriteriaSet();
      if (branch != null) {
         criteriaSet.add(new CriteriaBranch(branch));
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
   public QueryBuilder fromBranch(IOseeBranch branch) throws OseeCoreException {
      Conditions.checkNotNull(branch, "branch");
      return createBuilder(branch);
   }

   @Override
   public QueryBuilder fromBranch(String branchGuid) throws OseeCoreException {
      return fromBranch(TokenFactory.createBranch(branchGuid,
         String.format("N/A>%s:%s", getClass().getSimpleName(), "fromBranch")));
   }

   @Override
   public QueryBuilder fromArtifacts(Collection<? extends ArtifactReadable> artifacts) throws OseeCoreException {
      Conditions.checkNotNullOrEmpty(artifacts, "artifacts");
      ArtifactReadable artifact = artifacts.iterator().next();
      IOseeBranch branch = artifact.getBranch();
      Set<String> guids = new HashSet<String>();
      for (ArtifactReadable art : artifacts) {
         guids.add(art.getGuid());
      }
      return fromBranch(branch).andGuids(guids);
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
}
