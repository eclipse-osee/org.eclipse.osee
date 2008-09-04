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
package org.eclipse.osee.framework.server.admin.search;

import org.eclipse.osee.framework.search.engine.ISearchStatistics;
import org.eclipse.osee.framework.server.admin.Activator;
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

/**
 * @author Roberto E. Escobar
 */
class SearchStats extends BaseCmdWorker {

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#doWork(long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      ISearchStatistics stats = Activator.getInstance().getSearchEngine().getStatistics();
      StringBuffer buffer = new StringBuffer();
      buffer.append("\n----------------------------------------------\n");
      buffer.append("                  Search Stats                \n");
      buffer.append("----------------------------------------------\n");
      buffer.append(String.format("Total Searches - [%d]\n", stats.getTotalSearches()));
      buffer.append(String.format("Search Time    - avg: [%s] ms - longest: [%s] ms\n", stats.getAverageSearchTime(),
            stats.getLongestSearchTime()));
      buffer.append(String.format("Longest Search  - %s\n", stats.getLongestSearch()));
      println(buffer.toString());
   }
}
