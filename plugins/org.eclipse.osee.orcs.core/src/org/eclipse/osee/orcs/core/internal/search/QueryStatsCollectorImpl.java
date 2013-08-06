/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
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
