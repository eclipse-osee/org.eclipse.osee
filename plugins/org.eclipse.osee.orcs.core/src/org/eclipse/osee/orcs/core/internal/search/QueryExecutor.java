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

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.eclipse.osee.executor.admin.ExecutionCallback;
import org.eclipse.osee.executor.admin.ExecutorAdmin;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
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
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class QueryExecutor {

   private final ExecutorAdmin executorAdmin;
   private final QueryEngine queryEngine;
   private final OrcsObjectLoader objectLoader;

   public QueryExecutor(ExecutorAdmin executorAdmin, QueryEngine queryEngine, OrcsObjectLoader objectLoader) {
      super();
      this.executorAdmin = executorAdmin;
      this.queryEngine = queryEngine;
      this.objectLoader = objectLoader;
   }

   public Future<Integer> scheduleCount(SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options, ExecutionCallback<Integer> callback) throws OseeCoreException {
      Callable<Integer> callable =
         new SearchCountCallable(queryEngine, objectLoader, sessionContext, LoadLevel.ATTRIBUTE, criteriaSet, options);
      try {
         return executorAdmin.schedule(callable, callback);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public Future<ResultSet<ReadableArtifact>> scheduleSearch(SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options, ExecutionCallback<ResultSet<ReadableArtifact>> callback) throws OseeCoreException {
      Callable<ResultSet<ReadableArtifact>> callable =
         new SearchCallable(queryEngine, objectLoader, sessionContext, LoadLevel.FULL, criteriaSet, options);
      try {
         return executorAdmin.schedule(callable, callback);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }

   public Future<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> scheduleSearchWithMatches(SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options, ExecutionCallback<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> callback) throws OseeCoreException {
      Callable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> callable =
         new SearchMatchesCallable(queryEngine, objectLoader, sessionContext, LoadLevel.FULL, criteriaSet, options);
      try {
         return executorAdmin.schedule(callable, callback);
      } catch (Exception ex) {
         throw new OseeCoreException(ex);
      }
   }
}
