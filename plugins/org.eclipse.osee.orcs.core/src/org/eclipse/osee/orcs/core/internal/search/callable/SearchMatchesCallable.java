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
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.data.ResultSetList;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class SearchMatchesCallable extends AbstractSearchCallable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> {

   public SearchMatchesCallable(Log logger, QueryEngine queryEngine, OrcsObjectLoader objectLoader, SessionContext sessionContext, LoadLevel loadLevel, CriteriaSet criteriaSet, QueryOptions options) {
      super(logger, queryEngine, objectLoader, sessionContext, loadLevel, criteriaSet, options);
   }

   @Override
   protected ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> innerCall() throws Exception {
      QueryContext queryContext = queryEngine.create(sessionContext.getSessionId(), criteriaSet, options);
      LoadOptions loadOptions = new LoadOptions(options.isHistorical(), options.areDeletedIncluded(), loadLevel);
      checkForCancelled();
      List<ReadableArtifact> artifacts = objectLoader.load(this, queryContext, loadOptions, sessionContext);

      List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();

      Collection<QueryPostProcessor> processors = queryContext.getPostProcessors();
      if (processors.isEmpty()) {
         processors = Collections.<QueryPostProcessor> singleton(new DefaultQueryPostProcessor());
      }
      for (QueryPostProcessor processor : processors) {
         processor.setItemsToProcess(artifacts);
         checkForCancelled();
         results.addAll(processor.call());
      }
      return new ResultSetList<Match<ReadableArtifact, ReadableAttribute<?>>>(results);
   }
}
