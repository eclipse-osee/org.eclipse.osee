/*********************************************************************
 * Copyright (c) 2026 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.define.operations.publisher.datarights;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.DataRightsClassificationNameParser;
import org.eclipse.osee.orcs.search.QueryBuilder;

/**
 * @author David W. Miller
 */

class DataRightsFootersIndicatorLoaderImpl implements DataRightsFootersIndicatorLoader {

   private static final Logger logger = Logger.getLogger(DataRightsFootersIndicatorLoaderImpl.class.getName());

   @Override
   public Set<String> loadClassificationNames(QueryBuilder commonBranchQuery) {

      Set<String> classificationNames = new LinkedHashSet<>();

      ArtifactReadable footersArtifact;

      /*
       * A missing or unreadable footers artifact must leave the compiled seed governing, so the read
       * never throws (mirrors DataRightClassificationMap.create). The broad catch is deliberate: it
       * also covers the multiple-matches case. It is logged at FINE so a genuinely unreadable but
       * present artifact is diagnosable without failing the caller or adding console chatter.
       */

      try {
         footersArtifact = commonBranchQuery.andId(CoreArtifactTokens.DataRightsFooters).asArtifact();
      } catch (Exception e) {
         DataRightsFootersIndicatorLoaderImpl.logger.log(Level.FINE,
            "DataRightsFooters artifact could not be read from the common branch; falling back to the seed set.", e);
         return classificationNames;
      }

      List<String> footerValues = footersArtifact.getAttributeValues(CoreAttributeTypes.GeneralStringData);

      for (String value : footerValues) {
         DataRightsClassificationNameParser.parseClassificationName(value).ifPresent(classificationNames::add);
      }

      return classificationNames;
   }

}
