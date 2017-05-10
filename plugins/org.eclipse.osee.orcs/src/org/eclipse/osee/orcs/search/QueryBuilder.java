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
package org.eclipse.osee.orcs.search;

import org.eclipse.osee.executor.admin.CancellableCallable;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.ResultSet;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;

/**
 * @author Ryan D. Brooks
 * @author Roberto E. Escobar
 */
public interface QueryBuilder extends ArtifactQueryBuilder<QueryBuilder>, Query {

   /**
    * Executes query
    *
    * @return artifact search results
    */
   ResultSet<ArtifactReadable> getResults() throws OseeCoreException;

   /**
    * Executes query
    *
    * @return artifact search results with match locations
    */
   ResultSet<Match<ArtifactReadable, AttributeReadable<?>>> getMatches() throws OseeCoreException;

   /**
    * Executes query
    *
    * @return localIds search results
    */
   ResultSet<? extends ArtifactId> getResultsAsLocalIds() throws OseeCoreException;

   /**
    * Count search results
    */
   @Override
   int getCount() throws OseeCoreException;

   boolean exists();

   /**
    * Schedule a count search results
    */
   @Override
   CancellableCallable<Integer> createCount() throws OseeCoreException;

   /**
    * Schedule query
    *
    * @return artifact search results
    */
   CancellableCallable<ResultSet<ArtifactReadable>> createSearch() throws OseeCoreException;

   /**
    * Schedule query and find matching locations
    *
    * @return artifact search results with match locations
    */
   CancellableCallable<ResultSet<Match<ArtifactReadable, AttributeReadable<?>>>> createSearchWithMatches() throws OseeCoreException;

   /**
    * Schedule query and find matching locations
    *
    * @return localIds search results
    */
   CancellableCallable<ResultSet<? extends ArtifactId>> createSearchResultsAsLocalIds() throws OseeCoreException;

}
