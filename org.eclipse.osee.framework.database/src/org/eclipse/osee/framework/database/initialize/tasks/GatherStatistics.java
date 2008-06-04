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
package org.eclipse.osee.framework.database.initialize.tasks;

import java.sql.Connection;
import org.eclipse.osee.framework.db.connection.ConnectionHandler;
import org.eclipse.osee.framework.db.connection.ConnectionHandlerStatement;
import org.eclipse.osee.framework.db.connection.DbUtil;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

public class GatherStatistics extends DbInitializationTask {
   private static final String gatherStats =
         "begin" + " DBMS_STATS.GATHER_SCHEMA_STATS (ownname => '', estimate_percent => 99," + " granularity => 'ALL', degree => NULL , cascade => TRUE);" + "end;";

   private SupportedDatabase database;

   /**
    * @param database
    */
   public GatherStatistics(SupportedDatabase database) {
      this.database = database;
   }

   public void run(Connection connection) throws Exception {
      System.out.println("GatherStatistics");
      if (this.database == SupportedDatabase.oracle) {
         ConnectionHandlerStatement stmt = null;
         try {
            stmt = ConnectionHandler.runPreparedQuery(gatherStats);
         } finally {
            DbUtil.close(stmt);
         }

      }
   }
}
