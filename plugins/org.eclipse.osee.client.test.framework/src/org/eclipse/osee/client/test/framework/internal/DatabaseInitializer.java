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

package org.eclipse.osee.client.test.framework.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.eclipse.osee.framework.database.init.IDbInitChoiceEnum;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;

/**
 * @author Roberto E. Escobar
 */
public class DatabaseInitializer {

   private final IDbInitChoiceEnum demoChoiceId;

   public DatabaseInitializer(IDbInitChoiceEnum demoChoiceId) {
      this.demoChoiceId = demoChoiceId;
   }

   public void execute() throws Exception {
      OseeLog.log(DatabaseInitializer.class, Level.INFO, "Initializing datastore...");
      SevereLoggingMonitor monitorLog = new SevereLoggingMonitor();
      try {
         OseeLog.registerLoggerListener(monitorLog);
         initialize();
      } finally {
         OseeLog.unregisterLoggerListener(monitorLog);
         AssertLib.assertLogEmpty(monitorLog);
      }
      OseeLog.log(DatabaseInitializer.class, Level.INFO, "Datastore configured...");
   }

   private void initialize() throws Exception {
      DatabaseInitializationOperation.execute(demoChoiceId);
      OseeLog.log(DatabaseInitializer.class, Level.INFO, "datastore configured...");

      // Re-authenticate so we can continue and NOT be bootstrap
      ClientSessionManager.releaseSession();
      ClientSessionManager.getSession();
      UserManager.releaseUser();

   }

}
