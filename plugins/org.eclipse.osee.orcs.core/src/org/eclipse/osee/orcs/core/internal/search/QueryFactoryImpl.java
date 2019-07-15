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
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsTypes;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
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

   private final CallableQueryFactory artQueryFactory;
   private final BranchCriteriaFactory branchCriteriaFactory;
   private final TransactionCriteriaFactory txCriteriaFactory;
   private final TupleQuery tupleQuery;
   private final ApplicabilityDsQuery applicabilityDsQuery;
   private final QueryEngine queryEngine;
   private final OrcsTypes orcsTypes;

   public QueryFactoryImpl(CallableQueryFactory artQueryFactory, BranchCriteriaFactory branchCriteriaFactory, TransactionCriteriaFactory txCriteriaFactory, TupleQuery tupleQuery, ApplicabilityDsQuery applicabilityDsQuery, QueryEngine queryEngine, OrcsTypes orcsTypes) {
      this.artQueryFactory = artQueryFactory;
      this.branchCriteriaFactory = branchCriteriaFactory;
      this.txCriteriaFactory = txCriteriaFactory;
      this.tupleQuery = tupleQuery;
      this.applicabilityDsQuery = applicabilityDsQuery;
      this.queryEngine = queryEngine;
      this.orcsTypes = orcsTypes;
   }

   @Override
   public BranchQuery branchQuery() {
      return new BranchQueryImpl(queryEngine, branchCriteriaFactory,
         new QueryData(queryEngine, artQueryFactory, orcsTypes));
   }

   @Override
   public QueryBuilder fromBranch(BranchId branch) {
      return new QueryData(queryEngine, artQueryFactory, orcsTypes, branch);
   }

   @Override
   public QueryBuilder fromArtifacts(Collection<? extends ArtifactReadable> artifacts) {
      Conditions.checkNotNullOrEmpty(artifacts, "artifacts");
      ArtifactReadable artifact = artifacts.iterator().next();
      Set<String> guids = new HashSet<>();
      for (ArtifactReadable art : artifacts) {
         guids.add(art.getGuid());
      }
      return fromBranch(artifact.getBranch()).andGuids(guids);
   }

   @Override
   public QueryBuilder fromArtifactTypeAllBranches(ArtifactTypeToken artifactType) {
      QueryBuilder builder = new QueryData(queryEngine, artQueryFactory, orcsTypes);
      builder.andIsOfType(artifactType);
      return builder;
   }

   @Override
   public TransactionQuery transactionQuery() {
      return new TransactionQueryImpl(queryEngine, txCriteriaFactory,
         new QueryData(queryEngine, artQueryFactory, orcsTypes));
   }

   @Override
   public TupleQuery tupleQuery() {
      return tupleQuery;
   }

   @Override
   public ApplicabilityQuery applicabilityQuery() {
      return new ApplicabilityQueryImpl(applicabilityDsQuery, this);
   }
}