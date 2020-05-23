/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;

/**
 * @author Roberto E. Escobar
 */
public class TextStreamTagger extends AbstractTagger {

   public TextStreamTagger(TagProcessor tagProcessor, StreamMatcher matcher) {
      super(tagProcessor, matcher);
   }

   @Override
   public void tagIt(InputStream provider, TagCollector collector) throws Exception {
      InputStream inputStream = null;
      try {
         inputStream = provider;
         getTagProcessor().collectFromInputStream(inputStream, collector);
      } finally {
         Lib.close(inputStream);
      }
   }

   @Override
   public List<MatchLocation> find(InputStream provider, String toSearch, boolean matchAllLocations, QueryOption... options) throws Exception {
      List<MatchLocation> toReturn;
      if (Strings.isValid(toSearch)) {
         InputStream inputStream = null;
         try {
            inputStream = provider;
            toReturn = getMatcher().findInStream(inputStream, toSearch, matchAllLocations, options);
         } finally {
            Lib.close(inputStream);
         }
      } else {
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }
}
