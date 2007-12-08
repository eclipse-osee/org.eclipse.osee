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
package org.eclipse.osee.framework.ui.plugin.util.db;

import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.eclipse.osee.framework.plugin.core.config.ConfigUtil;

/**
 * Checks a statement at a fixed point in time later to see that it has been closed. If the statement has been closed,
 * then this thread will simply die, else it will log a warning to the Error Log providing a stack trace from when this
 * thread was created to help identify statements that are missing close statements.
 * 
 * @author Robert A. Fisher
 */
public class StatementCloseWatcher implements Runnable {
   private static final Logger logger = ConfigUtil.getConfigFactory().getLogger(StatementCloseWatcher.class);
   private static final boolean WATCHER_ON = false;
   private final Statement statement;
   private final Exception birthPlace;
   private final long waitTime;

   /**
    * @param statement The statement object to check for being closed.
    * @param waitTime The time in milliseconds to wait until checking the statement.
    */
   public StatementCloseWatcher(Statement statement, long waitTime) {
      this.statement = statement;
      this.waitTime = waitTime;
      this.birthPlace = new Exception();

      // Force the stack trace to be loaded here.
      birthPlace.getStackTrace();
   }

   public void start() {
      if (WATCHER_ON) {
         Thread th = new Thread(this);
         th.setName("Statement Close Watcher");
         th.setDaemon(true);
         th.start();
      }
   }

   public void run() {
      try {
         Thread.sleep(waitTime);
      } catch (InterruptedException ex) {
         // If we are interruped, then just move on
      }

      try {
         // If the statement has been closed, this should cause an exception
         statement.execute("SELECT COUNT(*) FROM OSEE_DEFINE_branch");

         // And this line should not be reached
         logger.log(Level.WARNING, "Statement appears to not be closed", birthPlace);
      } catch (SQLException ex) {
         // statement exceptioned as expected
      }
   }
}
