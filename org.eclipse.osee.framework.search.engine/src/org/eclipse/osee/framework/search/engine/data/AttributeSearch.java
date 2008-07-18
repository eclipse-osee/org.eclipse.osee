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
package org.eclipse.osee.framework.search.engine.data;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;

/**
 * @author Roberto E. Escobar
 */
public final class AttributeSearch implements ITagCollector {
   private List<Long> tags;
   private String searchString;
   private Options options;

   public AttributeSearch(String searchString, Options options) {
      this.tags = new ArrayList<Long>();
      this.searchString = searchString;
      this.options = options;
   }

   public Set<IAttributeLocator> findMatches() throws Exception {
      Set<IAttributeLocator> toReturn = null;
      try {
         TagProcessor.collectFromString(searchString, this);
         toReturn = SearchTagDataStore.fetchTagEntries(options, tags);
      } finally {
         tags.clear();
         tags = null;
      }
      if (toReturn == null) {
         toReturn = Collections.emptySet();
      }
      return toReturn;
   }

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.utility.ITagCollector#addTag(java.lang.String, java.lang.Long)
    */
   @Override
   public void addTag(String word, Long codedTag) {
      tags.add(codedTag);
   }
}
