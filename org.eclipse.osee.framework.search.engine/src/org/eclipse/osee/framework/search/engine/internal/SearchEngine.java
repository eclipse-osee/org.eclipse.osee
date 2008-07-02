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

import java.util.Set;
import org.eclipse.osee.framework.search.engine.ISearchEngine;
import org.eclipse.osee.framework.search.engine.Options;

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
      Set<AttributeVersion> attributes = attributeSearch.findMatches();
      for (AttributeVersion attrVersion : attributes) {
         // Perform Second Pass;
      }
      return "12345,2";
   }

}
