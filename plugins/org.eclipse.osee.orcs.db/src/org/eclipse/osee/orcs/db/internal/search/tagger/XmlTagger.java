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
package org.eclipse.osee.orcs.db.internal.search.tagger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.jdk.core.type.MatchLocation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.jdk.core.util.io.xml.XmlTextInputStream;

/**
 * @author Roberto E. Escobar
 */
public class XmlTagger extends AbstractTagger {

   public XmlTagger(TagProcessor tagProcessor, StreamMatcher matcher) {
      super(tagProcessor, matcher);
   }

   @Override
   public void tagIt(InputStream provider, TagCollector collector) throws Exception {
      InputStream inputStream = null;
      try {
         inputStream = getStream(provider);
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
            inputStream = getStream(provider);
            toReturn = getMatcher().findInStream(inputStream, toSearch, matchAllLocations, options);
         } finally {
            Lib.close(inputStream);
         }
      } else {
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }

   private InputStream getStream(InputStream provider) throws IOException {
      return new XmlTextInputStream(provider);
   }
}
