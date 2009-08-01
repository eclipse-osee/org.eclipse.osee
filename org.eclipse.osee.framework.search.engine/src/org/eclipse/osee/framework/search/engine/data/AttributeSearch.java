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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.SearchOptions;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;

/**
 * @author Roberto E. Escobar
 */
public final class AttributeSearch implements ITagCollector {
   private final String searchString;
   private final int branchId;
   private final SearchOptions options;
   private final Set<Long> tagStore;
   private final Set<String> attributeTypes;

   public AttributeSearch(String searchString, int branchId, SearchOptions options, String... attributeTypes) {
      this.tagStore = new HashSet<Long>();
      this.branchId = branchId;
      this.searchString = searchString;
      this.options = options;
      this.attributeTypes = new HashSet<String>();
      if (attributeTypes != null) {
         for (String value : attributeTypes) {
            this.attributeTypes.add(value);
         }
      }
   }

   public Set<AttributeData> getMatchingAttributes() throws Exception {
      Set<AttributeData> toReturn = null;
      long start = System.currentTimeMillis();
      TagProcessor.collectFromString(searchString, this);
      toReturn = AttributeDataStore.getAttributesByTags(branchId, options, tagStore, attributeTypes);
      if (toReturn == null) {
         toReturn = Collections.emptySet();
      }
      OseeLog.log(AttributeSearch.class, Level.INFO, String.format("Attribute Search Query found [%d] in [%d] ms",
            toReturn.size(), System.currentTimeMillis() - start));
      return toReturn;
   }

   @Override
   public void addTag(String word, Long codedTag) {
      this.tagStore.add(codedTag);
   }
}
