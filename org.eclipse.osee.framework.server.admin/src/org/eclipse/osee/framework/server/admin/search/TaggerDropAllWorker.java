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

import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;
import org.eclipse.osee.framework.server.admin.BaseCmdWorker;

/**
 * @author Roberto E. Escobar
 */
class TaggerDropAllWorker extends BaseCmdWorker {
   private static final String TRUNCATE_SQL = "TRUNCATE osee_search_tags";
   private static final String DELETE_TABLE_SQL = "DELETE FROM osee_search_tags";

   /* (non-Javadoc)
    * @see org.eclipse.osee.framework.server.admin.search.BaseCmdWorker#doWork(java.sql.Connection, long)
    */
   @Override
   protected void doWork(long startTime) throws Exception {
      String deleteSql = null;
      if (SupportedDatabase.isDatabaseType(SupportedDatabase.postgresql)) {
         deleteSql = TRUNCATE_SQL;
      } else {
         deleteSql = DELETE_TABLE_SQL;
      }
      ConnectionHandler.runPreparedUpdate(deleteSql);
      println(String.format("Dropped all tags in %s.", getElapsedTime(startTime)));
   }
}
