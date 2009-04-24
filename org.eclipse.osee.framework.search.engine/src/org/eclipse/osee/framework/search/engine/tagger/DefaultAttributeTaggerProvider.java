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
package org.eclipse.osee.framework.search.engine.tagger;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.search.engine.MatchLocation;
import org.eclipse.osee.framework.search.engine.SearchOptions;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;
import org.eclipse.osee.framework.search.engine.utility.WordOrderMatcher;

/**
 * @author Roberto E. Escobar
 */
public class DefaultAttributeTaggerProvider extends BaseAttributeTaggerProvider {

   public void tagIt(AttributeData attributeData, ITagCollector tagCollector) throws Exception {
      TagProcessor.collectFromString(getValue(attributeData), tagCollector);
   }

   public List<MatchLocation> find(AttributeData attributeData, String toSearch, SearchOptions options) throws Exception {
      if (Strings.isValid(toSearch)) {
         InputStream inputStream = null;
         try {
            inputStream = getValueAsStream(attributeData);
            return WordOrderMatcher.findInStream(inputStream, toSearch, options);
         } finally {
            if (inputStream != null) {
               inputStream.close();
            }
         }
      }
      return Collections.emptyList();
   }
}
