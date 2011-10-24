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
package org.eclipse.osee.orcs.core.internal.search.callable;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.search.SearchResultSet;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class SearchCallable implements Callable<ResultSet<ReadableArtifact>> {
   private final QueryEngine queryEngine;
   private final OrcsObjectLoader objectLoader;

   private final SessionContext sessionContext;
   private final LoadLevel loadLevel;
   private final CriteriaSet criteriaSet;
   private final QueryOptions options;

   public SearchCallable(QueryEngine queryEngine, OrcsObjectLoader objectLoader, SessionContext sessionContext, LoadLevel loadLevel, CriteriaSet criteriaSet, QueryOptions options) {
      super();
      this.queryEngine = queryEngine;
      this.objectLoader = objectLoader;
      this.sessionContext = sessionContext;
      this.loadLevel = loadLevel;
      this.criteriaSet = criteriaSet;
      this.options = options;
   }

   @Override
   public ResultSet<ReadableArtifact> call() throws Exception {
      QueryContext queryContext = queryEngine.create(sessionContext.getSessionId(), criteriaSet, options);
      LoadOptions loadOptions = new LoadOptions(options.isHistorical(), options.areDeletedIncluded(), loadLevel);
      List<ReadableArtifact> artifacts = objectLoader.load(queryContext, loadOptions, sessionContext);

      List<ReadableArtifact> results;
      if (!queryContext.getPostProcessors().isEmpty()) {
         results = new ArrayList<ReadableArtifact>();
         for (QueryPostProcessor processor : queryContext.getPostProcessors()) {
            processor.setItemsToProcess(artifacts);
            List<Match<ReadableArtifact, ReadableAttribute<?>>> matches = processor.call();
            for (Match<ReadableArtifact, ReadableAttribute<?>> match : matches) {
               results.add(match.getItem());
            }
         }
      } else {
         results = artifacts;
      }
      return new SearchResultSet<ReadableArtifact>(results);
   }
}
