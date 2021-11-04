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

package org.eclipse.osee.orcs.core.internal;

import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsPerformance;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.internal.indexer.IndexerModule;
import org.eclipse.osee.orcs.statistics.IndexerStatistics;

/**
 * @author Roberto E. Escobar
 */
public class OrcsPerformanceImpl implements OrcsPerformance {
   private final IndexerModule indexerModule;
   private final OrcsSession session;

   public OrcsPerformanceImpl(Log logger, OrcsSession session, IndexerModule indexerModule) {
      this.session = session;
      this.indexerModule = indexerModule;
   }

   @Override
   public IndexerStatistics getIndexerStatistics() {
      return indexerModule.getStatistics(session);
   }

   @Override
   public void clearIndexerStatistics() {
      indexerModule.clearStatistics(session);
   }
}