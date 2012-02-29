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
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.search.QueryCollector;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class SearchMatchesCallable extends AbstractSearchCallable<ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>>> {

   private Collection<QueryPostProcessor> processors;

   public SearchMatchesCallable(Log logger, QueryEngine queryEngine, QueryCollector collector, OrcsObjectLoader objectLoader, SessionContext sessionContext, LoadLevel loadLevel, QueryData queryData) {
      super(logger, queryEngine, collector, objectLoader, sessionContext, loadLevel, queryData);
   }

   @Override
   protected ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> innerCall() throws Exception {
      QueryContext queryContext = queryEngine.create(sessionContext.getSessionId(), queryData);
      LoadOptions loadOptions =
         new LoadOptions(queryData.getOptions().isHistorical(), queryData.getOptions().areDeletedIncluded(), loadLevel);
      checkForCancelled();
      List<ReadableArtifact> artifacts = objectLoader.load(this, queryContext, loadOptions, sessionContext);

      List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();

      Collection<QueryPostProcessor> processors = queryContext.getPostProcessors();
      if (processors.isEmpty()) {
         processors = Collections.<QueryPostProcessor> singleton(new DefaultQueryPostProcessor(getLogger()));
      }
      for (QueryPostProcessor processor : processors) {
         processor.setItemsToProcess(artifacts);
         checkForCancelled();
         results.addAll(processor.call());
      }
      return new ResultSetList<Match<ReadableArtifact, ReadableAttribute<?>>>(results);
   }

   @Override
   public void setCancel(boolean isCancelled) {
      super.setCancel(isCancelled);
      if (processors != null && !processors.isEmpty()) {
         for (QueryPostProcessor processor : processors) {
            processor.setCancel(true);
         }
      }
   }

   @Override
   protected int getCount(ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> results) throws Exception {
      return results.getList().size();
   }
}
