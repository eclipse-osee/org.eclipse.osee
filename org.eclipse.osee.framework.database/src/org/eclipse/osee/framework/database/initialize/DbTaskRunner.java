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
package org.eclipse.osee.framework.database.initialize;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import org.eclipse.osee.framework.database.core.DatabaseNotSupportedException;
import org.eclipse.osee.framework.database.initialize.tasks.IDbInitializationTask;
import org.eclipse.osee.framework.db.connection.info.SupportedDatabase;

/**
 * @author Roberto E. Escobar
 */
public class DbTaskRunner {

   private static DbTaskRunner instance = null;

   private DbTaskRunner() {
   }

   public static DbTaskRunner getInstance() {
      if (instance == null) instance = new DbTaskRunner();
      return instance;
   }

   public void processTasks(Connection connection, Queue<IDbInitializationTask> tasks) throws SQLException, DatabaseNotSupportedException, Exception {
      SupportedDatabase databaseType = SupportedDatabase.getDatabaseType(connection);
      if (databaseType != null) {
         int safetyNet = 0;
         while (!tasks.isEmpty()) {
            IDbInitializationTask task = tasks.remove();
            if (task.canRun()) {
               task.run(connection);
               safetyNet = 0;
            } else {
               tasks.add(task);
               safetyNet++;
               if (safetyNet == tasks.size() + 1) {
                  throw new Exception("Unable to run all of the IDbInitializationTask because canRun() failed");
               }
            }
         }
      } else {
         throw new DatabaseNotSupportedException("Connected to " + connection.getMetaData().getDatabaseProductName());
      }
      System.out.println("finished the initialization");
   }
}
