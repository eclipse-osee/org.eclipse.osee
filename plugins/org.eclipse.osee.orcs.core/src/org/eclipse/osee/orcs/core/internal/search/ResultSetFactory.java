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

import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.CriteriaSet;
import org.eclipse.osee.orcs.core.ds.QueryEngine;
import org.eclipse.osee.orcs.core.ds.QueryOptions;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
@SuppressWarnings("unused")
public class ResultSetFactory {

   // TODO implements ResultSetFactory tie in with ArtifactLoader

   private Log logger;
   private QueryEngine queryEngine;

   // private ArtifactLoader loadingService;  

   public ResultSet<ReadableArtifact> createResultSet(CriteriaSet criteriaSet, QueryOptions options) throws OseeCoreException {
      return null;//new ResultSetImpl(queryEngine, criteriaSet, options);
   }

   public ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> createMatchesResultSet(CriteriaSet criteriaSet, QueryOptions options) throws OseeCoreException {
      //      return new ResultSetImpl(queryEngine, criteriaSet, options)
      return null;
   }

}
