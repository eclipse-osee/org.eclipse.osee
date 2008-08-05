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

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import org.eclipse.osee.framework.db.connection.OseeDbConnection;
import org.eclipse.osee.framework.db.connection.core.JoinUtility;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.search.engine.ISearchEngineTagger;

/**
 * @author Roberto E. Escobar
 */
final class StartUpRunnable extends TimerTask {
   private ISearchEngineTagger tagger;

   StartUpRunnable(ISearchEngineTagger tagger) {
      this.tagger = tagger;
   }

   /* (non-Javadoc)
    * @see java.lang.Runnable#run()
    */
   @Override
   public void run() {
      Connection connection = null;
      try {
         connection = OseeDbConnection.getConnection();
         List<Integer> queries = JoinUtility.getAllTagQueueQueryIds(connection);
         if (queries.isEmpty() != true) {
            OseeLog.log(SearchEngineTagger.class, Level.INFO, String.format(
                  "Tagging [%d] left-over items from tag queue.", queries.size()));
         }
         for (Integer queryId : queries) {
            tagger.tagByQueueQueryId(queryId);
         }
      } catch (Exception ex) {
         OseeLog.log(SearchEngineTagger.class, Level.INFO, "Tagging on Server Startup was not run.");
      } finally {
         if (connection != null) {
            try {
               connection.close();
            } catch (SQLException ex) {
               OseeLog.log(SearchEngineTagger.class, Level.SEVERE, "Error closing connection during start-up.", ex);
            }
         }
      }
   }
}
