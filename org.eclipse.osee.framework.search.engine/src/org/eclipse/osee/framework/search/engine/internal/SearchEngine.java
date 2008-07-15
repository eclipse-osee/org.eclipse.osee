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
package org.eclipse.osee.framework.search.engine.internal;

import java.util.List;
import java.util.Set;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.db.connection.core.JoinUtility.ArtifactJoinQuery;
import org.eclipse.osee.framework.search.engine.Activator;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.Options;
import org.eclipse.osee.framework.search.engine.attribute.AttributeData;
import org.eclipse.osee.framework.search.engine.attribute.AttributeDataStore;
import org.eclipse.osee.framework.search.engine.data.AttributeSearch;
import org.eclipse.osee.framework.search.engine.data.IAttributeLocator;

/**
 * @author Roberto E. Escobar
 */
public class SearchEngine implements ISearchEngine {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.search.engine.ISearchEngine#search(java.lang.String, org.eclipse.osee.framework.search.engine.Options)
    */
   @Override
   public String search(String searchString, Options options) throws Exception {
      AttributeSearch attributeSearch = new AttributeSearch(searchString, options);
      Set<IAttributeLocator> attributeLocators = attributeSearch.findMatches();

      List<AttributeData> attributeDatas = AttributeDataStore.getInstance().getAttributes(attributeLocators);
      ArtifactJoinQuery joinQuery = JoinUtility.createArtifactJoinQuery();

      for (AttributeData attributeData : attributeDatas) {
         if (Activator.getInstance().getTaggerManager().find(attributeData, searchString)) {
            joinQuery.add(attributeData.getArtId(), attributeData.getBranchId());
         }
      }
      joinQuery.store();
      return String.format("%d,%d", joinQuery.getQueryId(), joinQuery.size());
   }
}
