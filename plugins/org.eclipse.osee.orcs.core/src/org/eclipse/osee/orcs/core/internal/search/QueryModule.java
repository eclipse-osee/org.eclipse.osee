/*********************************************************************
 * Copyright (c) 2012 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.core.internal.search;

import org.eclipse.osee.framework.core.OrcsTokenService;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.ApplicabilityDsQuery;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.graph.GraphBuilderFactory;
import org.eclipse.osee.orcs.core.internal.graph.GraphProvider;
import org.eclipse.osee.orcs.core.internal.proxy.ExternalArtifactManager;
import org.eclipse.osee.orcs.search.QueryFactory;
import org.eclipse.osee.orcs.search.TupleQuery;

/**
 * @author Roberto E. Escobar
 */
public class QueryModule {
   private final CallableQueryFactory artQueryFactory;
   private final BranchCriteriaFactory branchCriteriaFactory;
   private final TransactionCriteriaFactory txCriteriaFactory;
   private final TupleQuery tupleQuery;
   private final ApplicabilityDsQuery applicabilityDsQuery;
   private final QueryEngine queryEngine;
   private final OrcsTokenService tokenService;
   private final OrcsApi orcsApi;
   public static interface QueryModuleProvider {
      QueryFactory getQueryFactory(OrcsSession session);
   }

   public QueryModule(OrcsApi orcsApi, Log logger, QueryEngine queryEngine, GraphBuilderFactory builderFactory, GraphProvider provider, OrcsTokenService tokenService, ExternalArtifactManager proxyManager) {
      this.orcsApi = orcsApi;
      this.queryEngine = queryEngine;
      artQueryFactory = new CallableQueryFactory(logger, queryEngine, builderFactory, provider, proxyManager);
      branchCriteriaFactory = new BranchCriteriaFactory();
      txCriteriaFactory = new TransactionCriteriaFactory();
      tupleQuery = queryEngine.createTupleQuery();
      applicabilityDsQuery = queryEngine.createApplicabilityDsQuery();
      this.tokenService = tokenService;
   }

   public QueryFactory createQueryFactory(OrcsSession session) {
      return new QueryFactoryImpl(orcsApi, artQueryFactory, branchCriteriaFactory, txCriteriaFactory, tupleQuery,
         applicabilityDsQuery, queryEngine, tokenService);
   }

   public CallableQueryFactory getArtQueryFactory() {
      return artQueryFactory;
   }
}