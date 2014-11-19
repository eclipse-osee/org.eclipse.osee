/*******************************************************************************
 * Copyright (c) 2013 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.db.internal.search.indexer.callable.producer;

import org.eclipse.osee.framework.database.IOseeDatabaseService;
import org.eclipse.osee.framework.database.core.OseeConnection;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.core.ds.HasVersion;
import org.eclipse.osee.orcs.data.AttributeTypes;
import org.eclipse.osee.orcs.db.internal.search.indexer.IndexingTaskConsumer;
import org.eclipse.osee.orcs.db.internal.sql.join.SqlJoinFactory;
import org.eclipse.osee.orcs.search.IndexerCollector;

/**
 * @author Roberto E. Escobar
 */
public class IndexerDatabaseCallable extends AbstractIndexerTxDatabaseCallable {

   private final Iterable<? extends HasVersion> datas;

   public IndexerDatabaseCallable(Log logger, OrcsSession session, IOseeDatabaseService dbService, SqlJoinFactory joinFactory, AttributeTypes types, IndexingTaskConsumer consumer, IndexerCollector listener, boolean isCacheAll, int cacheLimit, Iterable<? extends HasVersion> datas) {
      super(logger, session, dbService, joinFactory, types, consumer, listener, isCacheAll, cacheLimit);
      this.datas = datas;
   }

   @Override
   protected void convertInput(final OseeConnection connection) throws Exception {
      for (HasVersion data : datas) {
         addEntry(connection, data.getVersion().getGammaId());
      }

   }
}
