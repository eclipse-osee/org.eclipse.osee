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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.core.exception.MultipleArtifactsExist;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.orcs.core.ds.LoadOptions;
import org.eclipse.osee.orcs.core.ds.QueryContext;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.core.internal.OrcsObjectLoader;
import org.eclipse.osee.orcs.core.internal.SessionContext;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;
import org.eclipse.osee.orcs.search.ResultSet;

/**
 * @author Roberto E. Escobar
 */
public class ResultSetLocatorImpl implements ResultSet<Match<ReadableArtifact, ReadableAttribute<?>>> {

   private final OrcsObjectLoader objectLoader;
   private final SessionContext sessionContext;
   private final QueryContext queryContext;
   private final LoadOptions loadOptions;

   public ResultSetLocatorImpl(OrcsObjectLoader objectLoader, SessionContext sessionContext, QueryContext queryContext, LoadOptions loadOptions) {
      super();
      this.objectLoader = objectLoader;
      this.sessionContext = sessionContext;
      this.queryContext = queryContext;
      this.loadOptions = loadOptions;
   }

   @Override
   public Match<ReadableArtifact, ReadableAttribute<?>> getOneOrNull() throws OseeCoreException {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> result = getList();
      return result.isEmpty() ? null : result.iterator().next();
   }

   @Override
   public Match<ReadableArtifact, ReadableAttribute<?>> getExactlyOne() throws OseeCoreException {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> result = getList();
      if (result.isEmpty()) {
         throw new ArtifactDoesNotExist("No artifacts found");
      } else if (result.size() > 1) {
         throw new MultipleArtifactsExist("Multiple artifact found - total [%s]", result.size());
      }
      return result.iterator().next();
   }

   @Override
   public List<Match<ReadableArtifact, ReadableAttribute<?>>> getList() throws OseeCoreException {
      List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();

      List<ReadableArtifact> artifacts = objectLoader.load(queryContext, loadOptions, sessionContext);

      Collection<QueryPostProcessor> processors = queryContext.getPostProcessors();
      if (processors.isEmpty()) {
         for (final ReadableArtifact art : artifacts) {
            results.add(new Match<ReadableArtifact, ReadableAttribute<?>>() {

               @Override
               public boolean hasLocationData() {
                  return false;
               }

               @Override
               public ReadableArtifact getItem() {
                  return art;
               }

               @Override
               public Collection<ReadableAttribute<?>> getElements() throws OseeCoreException {
                  return Collections.emptyList();
               }

               @Override
               public List<MatchLocation> getLocation(ReadableAttribute<?> element) throws OseeCoreException {
                  return Collections.emptyList();
               }
            });
         }
      } else {
         for (QueryPostProcessor processor : processors) {
            results.addAll(processor.getLocationMatches(artifacts));
         }
      }
      return results;
   }

   @Override
   public Iterable<Match<ReadableArtifact, ReadableAttribute<?>>> getIterable(int fetchSize) throws OseeCoreException {
      return getList();
   }
}
