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
package org.eclipse.osee.orcs.db.internal.search.util;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.data.ReadableArtifact;
import org.eclipse.osee.orcs.data.ReadableAttribute;
import org.eclipse.osee.orcs.db.internal.search.tagger.TaggingEngine;
import org.eclipse.osee.orcs.search.CaseType;

/**
 * @author Roberto E. Escobar
 */
public class MatchLocationProcessor {

   private final Log logger;
   private final TaggingEngine engine;

   private MatchLocationProcessor(Log logger, TaggingEngine engine) {
      super();
      this.logger = logger;
      this.engine = engine;

      // TODO connect to postprocess
   }

   private Collection<ReadableArtifact> getArtifacts() {
      return null;
   }

   public void run() throws OseeCoreException {
      Map<ReadableArtifact, List<MatchLocation>> results = new HashMap<ReadableArtifact, List<MatchLocation>>();

      CaseType caseType = CaseType.MATCH_CASE;
      boolean matchAllLocations = true;
      String toSearch = "";

      for (ReadableArtifact artifact : getArtifacts()) {
         for (ReadableAttribute<?> attribute : artifact.getAttributes()) {
            try {
               List<MatchLocation> locations = engine.find(attribute, toSearch, caseType, matchAllLocations);
               if (!locations.isEmpty()) {
                  results.put(artifact, locations);
               }
            } catch (Exception ex) {
               logger.error(ex, "Error processing: [%s]", attribute);
            }
         }
      }
   }

}
