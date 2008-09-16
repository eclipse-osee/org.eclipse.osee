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

import java.sql.Connection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.utility.ITagCollector;
import org.eclipse.osee.framework.search.engine.utility.TagProcessor;

/**
 * @author Roberto E. Escobar
 */
public final class AttributeSearch implements ITagCollector {
   private String searchString;
   private int branchId;
   private Options options;
   private Set<Long> tagStore;

   public AttributeSearch(String searchString, int branchId, Options options) {
      this.tagStore = new HashSet<Long>();
      this.branchId = branchId;
      this.searchString = searchString;
      this.options = options;
   }

   public Set<AttributeData> getMatchingAttributes() throws Exception {
      Set<AttributeData> toReturn = null;
      Connection connection = null;
      long start = System.currentTimeMillis();
      try {
         connection = OseeDbConnection.getConnection();
         TagProcessor.collectFromString(searchString, this);
         toReturn = AttributeDataStore.getAttributesByTags(connection, branchId, options, this.tagStore);
      } finally {
         if (connection != null) {
            connection.close();
         }
      }
      if (toReturn == null) {
         toReturn = Collections.emptySet();
      }
      OseeLog.log(AttributeSearch.class, Level.INFO, String.format("Attribute Search Query found [%d] in [%d] ms",
            toReturn.size(), System.currentTimeMillis() - start));
      return toReturn;
   }

   /*
    * (non-Javadoc)
    * 
    * @see org.eclipse.osee.framework.search.engine.utility.ITagCollector#addTag(java.lang.String,
    *      java.lang.Long)
    */
   @Override
   public void addTag(String word, Long codedTag) {
      this.tagStore.add(codedTag);
   }
}
