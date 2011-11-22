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

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.search.callable.SearchCallable;
import org.eclipse.osee.orcs.core.internal.search.callable.SearchCountCallable;
import org.eclipse.osee.orcs.core.internal.search.callable.SearchMatchesCallable;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class CallableQueryFactory {

   private final Log logger;
   private final QueryEngine queryEngine;
   private final OrcsObjectLoader objectLoader;

   public CallableQueryFactory(Log logger, QueryEngine queryEngine, OrcsObjectLoader objectLoader) {
      super();
      this.logger = logger;
      this.queryEngine = queryEngine;
      this.objectLoader = objectLoader;
   }

   public CancellableCallable<Integer> createCount(SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options) {
      return new SearchCountCallable(logger, queryEngine, objectLoader, sessionContext, LoadLevel.ATTRIBUTE,
         criteriaSet, options);
   }

   public CancellableCallable<ResultSet<ReadableArtifact>> createSearch(SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options) {
      return new SearchCallable(logger, queryEngine, objectLoader, sessionContext, LoadLevel.FULL, criteriaSet, options);
   }

   public CancellableCallable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> createSearchWithMatches(SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options) {
      return new SearchMatchesCallable(logger, queryEngine, objectLoader, sessionContext, LoadLevel.FULL, criteriaSet,
         options);
   }
}
