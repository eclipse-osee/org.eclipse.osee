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
package org.eclipse.osee.framework.search.engine;

/**
 * @author Roberto E. Escobar
 */
public interface ISearchStatistics {

   /**
    * Get average search time in milliseconds
    * 
    * @return average search time in milliseconds
    */
   public long getAverageSearchTime();

   /**
    * Get total number of searches processed
    * 
    * @return total number of searches
    */
   public int getTotalSearches();

   /**
    * Get longest search time
    * 
    * @return longest search time
    */
   public long getLongestSearchTime();

   /**
    * Get longest search information.
    * 
    * @return search info
    */
   public String getLongestSearch();
}
