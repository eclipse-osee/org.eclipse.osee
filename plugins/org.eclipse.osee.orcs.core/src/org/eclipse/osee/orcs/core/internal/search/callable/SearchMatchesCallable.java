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
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.internal.ArtifactLoader;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;
import org.eclipse.osee.orcs.core.internal.search.QueryCollector;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class SearchMatchesCallable extends AbstractArtifactSearchCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> {

   private Collection<QueryPostProcessor> processors;

   public SearchMatchesCallable(Log logger, QueryEngine queryEngine, QueryCollector collector, ArtifactLoaderFactory objectLoader, OrcsSession session, LoadLevel loadLevel, QueryData queryData, AttributeTypes types) {
      super(logger, queryEngine, collector, objectLoader, session, loadLevel, queryData, types);
   }

   @Override
   protected ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> innerCall() throws Exception {
      QueryContext queryContext = queryEngine.create(session, queryData);
      checkForCancelled();

      ArtifactLoader loader = objectLoader.fromQueryContext(session, queryContext);
      loader.setOptions(queryData.getOptions());
      loader.setLoadLevel(loadLevel);

      List<ArtifactReadable> artifacts = loader.load(this);

      checkForCancelled();

      List<Match<ArtifactReadable, AttributeReadable<?>>> results =
         new ArrayList<Match<ArtifactReadable, AttributeReadable<?>>>();

      Collection<QueryPostProcessor> processors = queryContext.getPostProcessors();
      if (processors.isEmpty()) {
         processors = Collections.<QueryPostProcessor> singleton(new DefaultQueryPostProcessor(getLogger()));
      }
      for (QueryPostProcessor processor : processors) {
         processor.setItemsToProcess(artifacts);
         processor.setAttributeTypes(types);
         checkForCancelled();
         results.addAll(processor.call());
      }
      return new ResultSetList<Match<ArtifactReadable, AttributeReadable<?>>>(results);
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
   protected int getCount(ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> results) throws Exception {
      return results.getList().size();
   }
}
