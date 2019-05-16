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
package org.eclipse.osee.client.test.framework.internal;

import java.util.logging.Level;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.eclipse.osee.framework.database.init.IDbInitChoiceEnum;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.junit.Assert;

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

      String currentUserId = UserManager.getUser().getUserId();
      Assert.assertTrue("Current user should not be bootstrap user",
         !SystemUser.BootStrap.getName().equals(currentUserId));
   }

}
