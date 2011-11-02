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
import org.eclipse.osee.framework.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.core.ds.QueryPostProcessor;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.search.Match;

public class DefaultQueryPostProcessor extends QueryPostProcessor {

   public DefaultQueryPostProcessor(Log logger) {
      super(logger);
   }

   @Override
   public List<Match<ReadableArtifact, ReadableAttribute<?>>> innerCall() throws Exception {
      Conditions.checkNotNull(getItemsToProcess(), "Query first pass results");

      List<Match<ReadableArtifact, ReadableAttribute<?>>> results =
         new ArrayList<Match<ReadableArtifact, ReadableAttribute<?>>>();

      for (final ReadableArtifact art : getItemsToProcess()) {
         checkForCancelled();
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
            public Collection<ReadableAttribute<?>> getElements() {
               return Collections.emptyList();
            }

            @Override
            public List<MatchLocation> getLocation(ReadableAttribute<?> element) {
               return Collections.emptyList();
            }
         });
      }
      return results;
   }
}
