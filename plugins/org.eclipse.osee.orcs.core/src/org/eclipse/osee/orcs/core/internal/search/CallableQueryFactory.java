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
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.DataLoaderFactory;
import org.eclipse.osee.orcs.core.ds.QueryData;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.internal.ArtifactLoaderFactory;
import org.eclipse.osee.orcs.core.internal.search.callable.LocalIdSearchCallable;
import org.eclipse.osee.orcs.core.internal.search.callable.SearchCallable;
import org.eclipse.osee.orcs.core.internal.search.callable.SearchCountCallable;
import org.eclipse.osee.orcs.core.internal.search.callable.SearchMatchesCallable;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.data.HasLocalId;
import org.eclipse.osee.orcs.search.Match;

/**
 * @author Roberto E. Escobar
 */
public class CallableQueryFactory {

   private final Log logger;
   private final QueryEngine queryEngine;
   private final ArtifactLoaderFactory objectLoader;
   private final QueryCollector collector;
   private final DataLoaderFactory dataLoader;
   private final AttributeTypes types;

   public CallableQueryFactory(Log logger, QueryEngine queryEngine, QueryCollector collector, ArtifactLoaderFactory objectLoader, DataLoaderFactory dataLoader, AttributeTypes types) {
      super();
      this.logger = logger;
      this.queryEngine = queryEngine;
      this.objectLoader = objectLoader;
      this.collector = collector;
      this.dataLoader = dataLoader;
      this.types = types;
   }

   public CancellableCallable<Integer> createCount(OrcsSession session, QueryData queryData) {
      return new SearchCountCallable(logger, queryEngine, collector, objectLoader, session, LoadLevel.ATTRIBUTE,
         queryData, types);
   }

   public CancellableCallable<ResultSet<ArtifactReadable>> createSearch(OrcsSession session, QueryData queryData) {
      return new SearchCallable(logger, queryEngine, collector, objectLoader, session, LoadLevel.FULL,
         queryData, types);
   }

   public CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches(OrcsSession session, QueryData queryData) {
      return new SearchMatchesCallable(logger, queryEngine, collector, objectLoader, session, LoadLevel.FULL, queryData, types);
   }

   public CancellableCallable<ResultSet<HasLocalId>> createLocalIdSearch(OrcsSession session, QueryData queryData) {
      return new LocalIdSearchCallable(logger, queryEngine, collector, objectLoader, dataLoader, session, queryData, types);
   }
}
