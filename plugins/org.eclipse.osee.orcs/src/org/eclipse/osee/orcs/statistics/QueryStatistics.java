/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.orcs.statistics;

/**
 * @author Roberto E. Escobar
 */
public interface QueryStatistics {

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
