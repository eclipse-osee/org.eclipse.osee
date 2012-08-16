/*******************************************************************************
 * Copyright (c) 2012 Boeing.
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
import java.util.List;
import org.eclipse.osee.framework.core.data.ResultSet;
import org.eclipse.osee.framework.core.data.ResultSetList;
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.ArtifactData;
import org.eclipse.osee.orcs.core.ds.ArtifactDataHandler;
import org.eclipse.osee.orcs.core.ds.DataLoader;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.LoadDataHandlerAdapter;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeywords;
import org.eclipse.osee.orcs.core.internal.ArtifactLoader;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.core.internal.search.QueryCollector;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class LocalIdSearchCallable extends AbstractSearchCallable<ResultSet<HasLocalId>> {

   private final ArtifactLoaderFactory objectLoader;
   private final DataLoaderFactory dataLoaderFactory;

   public LocalIdSearchCallable(Log logger, QueryEngine queryEngine, QueryCollector collector, ArtifactLoaderFactory objectLoader, DataLoaderFactory dataLoaderFactory, SessionContext sessionContext, QueryData queryData, AttributeTypes types) {
      super(logger, queryEngine, collector, sessionContext, null, queryData, types);
      this.dataLoaderFactory = dataLoaderFactory;
      this.objectLoader = objectLoader;
   }

   @Override
   protected int getCount(ResultSet<HasLocalId> results) throws Exception {
      return results.getList().size();
   }

   @Override
   protected ResultSet<HasLocalId> innerCall() throws Exception {
      QueryContext queryContext = queryEngine.create(sessionContext.getSessionId(), queryData);

      checkForCancelled();

      boolean requiresAttributeScan =
         queryData.hasCriteriaType(CriteriaAttributeKeywords.class) && !queryContext.getPostProcessors().isEmpty();

      List<HasLocalId> results = new ArrayList<HasLocalId>();
      if (requiresAttributeScan) {
         ArtifactLoader loader = objectLoader.fromQueryContext(sessionContext, queryContext);
         loader.setLoadLevel(loadLevel);
         loader.includeDeleted(queryData.getOptions().areDeletedIncluded());
         loader.fromTransaction(queryData.getOptions().getFromTransaction());
         checkForCancelled();

         List<ArtifactReadable> artifacts = loader.load(this);

         checkForCancelled();

         if (!queryContext.getPostProcessors().isEmpty()) {
            for (QueryPostProcessor processor : queryContext.getPostProcessors()) {
               processor.setItemsToProcess(artifacts);
               processor.setAttributeTypes(types);
               checkForCancelled();
               List<Match<ArtifactReadable, AttributeReadable<?>>> matches = processor.call();
               for (Match<ArtifactReadable, AttributeReadable<?>> match : matches) {
                  results.add(match.getItem());
                  checkForCancelled();
               }
            }
         } else {
            results.addAll(artifacts);
         }
      } else {
         DataLoader loader = dataLoaderFactory.fromQueryContext(queryContext);
         loader.setLoadLevel(LoadLevel.SHALLOW);
         loader.includeDeleted(queryData.getOptions().areDeletedIncluded());
         loader.fromTransaction(queryData.getOptions().getFromTransaction());
         loader.load(this, new AdapterBuidler(results));
      }
      checkForCancelled();
      return new ResultSetList<HasLocalId>(results);
   }
   private static final class AdapterBuidler extends LoadDataHandlerAdapter {

      private final Collection<HasLocalId> results;

      public AdapterBuidler(Collection<HasLocalId> results) {
         super();
         this.results = results;
      }

      @Override
      public ArtifactDataHandler getArtifactDataHandler() {
         return new ArtifactDataHandler() {

            @Override
            public void onData(ArtifactData data) {
               results.add(data);
            }
         };
      }
   }

}
