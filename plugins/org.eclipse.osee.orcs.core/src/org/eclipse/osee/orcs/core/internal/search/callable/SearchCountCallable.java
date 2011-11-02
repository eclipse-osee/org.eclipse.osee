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
import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.ds.criteria.CriteriaAttributeKeyword;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class SearchCountCallable extends AbstractSearchCallable<Integer> {

   public SearchCountCallable(Log logger, QueryEngine queryEngine, OrcsObjectLoader objectLoader, SessionContext sessionContext, LoadLevel loadLevel, CriteriaSet criteriaSet, QueryOptions options) {
      super(logger, queryEngine, objectLoader, sessionContext, loadLevel, criteriaSet, options);
   }

   @Override
   protected Integer innerCall() throws Exception {
      int count = -1;
      if (criteriaSet.hasCriteriaType(CriteriaAttributeKeyword.class)) {
         QueryContext queryContext = queryEngine.create(sessionContext.getSessionId(), criteriaSet, options);
         LoadOptions loadOptions = new LoadOptions(options.isHistorical(), options.areDeletedIncluded(), loadLevel);

         checkForCancelled();
         List<ReadableArtifact> artifacts = objectLoader.load(this, queryContext, loadOptions, sessionContext);

         List<ReadableArtifact> results;
         if (!queryContext.getPostProcessors().isEmpty()) {
            results = new ArrayList<ReadableArtifact>();
            for (QueryPostProcessor processor : queryContext.getPostProcessors()) {
               processor.setItemsToProcess(artifacts);
               checkForCancelled();
               List<Match<ReadableArtifact, ReadableAttribute<?>>> matches = processor.call();
               for (Match<ReadableArtifact, ReadableAttribute<?>> match : matches) {
                  results.add(match.getItem());
                  checkForCancelled();
               }
            }
         } else {
            results = artifacts;
         }
         count = results.size();
      } else {
         QueryContext queryContext = queryEngine.createCount(sessionContext.getSessionId(), criteriaSet, options);
         checkForCancelled();
         count = objectLoader.countObjects(this, queryContext);
      }
      return count;
   }
}
