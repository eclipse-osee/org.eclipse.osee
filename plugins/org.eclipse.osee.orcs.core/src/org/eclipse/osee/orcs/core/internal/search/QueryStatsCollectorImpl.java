/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.orcs.core.internal.search;

import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.QueryCollector;
import org.eclipse.osee.orcs.core.ds.QueryData;

/**
 * @author Roberto E. Escobar
 */
public class QueryStatsCollectorImpl implements QueryCollector {

   private final QueryStatisticsImpl stats;

   public QueryStatsCollectorImpl(QueryStatisticsImpl stats) {
      this.stats = stats;
   }

   @Override
   public void collect(OrcsSession session, int itemsFound, long processingTime, QueryData data) throws Exception {
      stats.addEntry(data, itemsFound, processingTime);
   }

}
