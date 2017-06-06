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
package org.eclipse.osee.orcs.db.internal.search.indexer.callable;

import org.eclipse.osee.jdbc.JdbcClient;
import org.eclipse.osee.logger.Log;
import org.eclipse.osee.orcs.OrcsSession;
import org.eclipse.osee.orcs.db.internal.callable.AbstractDatastoreCallable;

/**
 * @author Roberto E. Escobar
 */
public final class PurgeAllTagsDatabaseCallable extends AbstractDatastoreCallable<Integer> {

   public PurgeAllTagsDatabaseCallable(Log logger, OrcsSession session, JdbcClient jdbcClient) {
      super(logger, session, jdbcClient);
   }

   @Override
   public Integer call() throws Exception {
      getLogger().warn("Purging all search tags");
      return getJdbcClient().clearTable("osee_search_tags");
   }
}