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
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.SearchTagDataStore;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;

/**
 * @author Roberto E. Escobar
 */
public final class AttributeSearch implements ITagCollector {
   private List<Long> tags;
   private String searchString;
   private int branchId;
   private Options options;

   public AttributeSearch(String searchString, int branchId, Options options) {
      this.tags = new ArrayList<Long>();
      this.branchId = branchId;
      this.searchString = searchString;
      this.options = options;
   }

   public List<AttributeData> getMatchingAttributes() throws Exception {
      List<AttributeData> toReturn = null;
      try {
         TagProcessor.collectFromString(searchString, this);
         Set<IAttributeLocator> locators = SearchTagDataStore.fetchTagEntries(options, tags);
         if (toReturn.isEmpty() != true) {
            toReturn = AttributeDataStore.getInstance().getAttributes(branchId, locators);
         }
      } finally {
         tags.clear();
         tags = null;
      }
      if (toReturn == null) {
         toReturn = Collections.emptyList();
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
