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
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.data.AttributeReadable;
import org.eclipse.osee.orcs.search.Match;

public class DefaultQueryPostProcessor extends QueryPostProcessor {

   public DefaultQueryPostProcessor(Log logger) {
      super(logger);
   }

   @Override
   public List<Match<ArtifactReadable, AttributeReadable<?>>> innerCall() throws Exception {
      Conditions.checkNotNull(getItemsToProcess(), "Query first pass results");

      List<Match<ArtifactReadable, AttributeReadable<?>>> results =
         new ArrayList<Match<ArtifactReadable, AttributeReadable<?>>>();

      for (final ArtifactReadable art : getItemsToProcess()) {
         checkForCancelled();
         results.add(new Match<ArtifactReadable, AttributeReadable<?>>() {

            @Override
            public boolean hasLocationData() {
               return false;
            }

            @Override
            public ArtifactReadable getItem() {
               return art;
            }

            @Override
            public Collection<AttributeReadable<?>> getElements() {
               return Collections.emptyList();
            }

            @Override
            public List<MatchLocation> getLocation(AttributeReadable<?> element) {
               return Collections.emptyList();
            }
         });
      }
      return results;
   }
}
