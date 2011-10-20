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

import org.eclipse.osee.framework.core.enums.LoadLevel;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetFactory {

   private final QueryEngine queryEngine;
   private final OrcsObjectLoader objectLoader;

   public ResultSetFactory(QueryEngine queryEngine, OrcsObjectLoader objectLoader) {
      super();
      this.queryEngine = queryEngine;
      this.objectLoader = objectLoader;
   }

   public ResultSet<ReadableArtifact> createResultSet(SessionContext sessionContext, LoadLevel loadLevel, CriteriaSet criteriaSet, QueryOptions options) throws OseeCoreException {
      QueryContext queryContext = queryEngine.create(sessionContext.getSessionId(), criteriaSet, options);
      LoadOptions loadOptions = new LoadOptions(options.isHistorical(), options.areDeletedIncluded(), loadLevel);
      return new ResultSetImpl(objectLoader, sessionContext, queryContext, loadOptions);
   }

   public ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> createMatchesResultSet(SessionContext sessionContext, LoadLevel loadLevel, CriteriaSet criteriaSet, QueryOptions options) throws OseeCoreException {
      QueryContext queryContext = queryEngine.create(sessionContext.getSessionId(), criteriaSet, options);
      LoadOptions loadOptions = new LoadOptions(options.isHistorical(), options.areDeletedIncluded(), loadLevel);
      return new ResultSetLocatorImpl(objectLoader, sessionContext, queryContext, loadOptions);
   }

   public int getCount(SessionContext sessionContext, CriteriaSet criteriaSet, QueryOptions options) throws OseeCoreException {
      QueryContext queryContext = queryEngine.createCount(sessionContext.getSessionId(), criteriaSet, options);
      return objectLoader.countObjects(queryContext);
   }

}
