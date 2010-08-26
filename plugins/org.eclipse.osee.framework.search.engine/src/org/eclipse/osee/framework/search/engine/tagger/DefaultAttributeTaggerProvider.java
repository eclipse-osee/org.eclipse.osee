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
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.Strings;
import org.eclipse.osee.framework.resource.management.IResourceLocatorManager;
import org.eclipse.osee.framework.resource.management.IResourceManager;
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

   public DefaultAttributeTaggerProvider(TagProcessor tagProcess, IResourceLocatorManager locatorManager, IResourceManager resourceManager) {
      super(tagProcess, locatorManager, resourceManager);
   }

   @Override
   public void tagIt(AttributeData attributeData, ITagCollector tagCollector) {
      getTagProcessor().collectFromString(getValue(attributeData), tagCollector);
   }

   @Override
   public List<MatchLocation> find(AttributeData attributeData, String toSearch, SearchOptions options) throws OseeCoreException {
      List<MatchLocation> toReturn;
      if (Strings.isValid(toSearch)) {
         InputStream inputStream = null;
         try {
            inputStream = getValueAsStream(attributeData);
            toReturn = WordOrderMatcher.findInStream(inputStream, toSearch, options);
         } finally {
            Lib.close(inputStream);
         }
      } else {
         toReturn = Collections.emptyList();
      }
      return toReturn;
   }
}
