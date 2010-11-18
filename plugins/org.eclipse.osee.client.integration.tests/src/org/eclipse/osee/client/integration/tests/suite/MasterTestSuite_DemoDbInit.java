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
package org.eclipse.osee.client.integration.tests.suite;

import static org.junit.Assert.assertTrue;
import java.util.logging.Level;
import org.eclipse.osee.ats.config.demo.PopulateDemoActions;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.core.client.OseeClientSession;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.DemoUsers;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoDbInit {
   private static boolean wasDbInitSuccessful = false;

   @BeforeClass
   public static void setup() throws Exception {
      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      RenderingUtil.setPopupsAllowed(false);
   }

   @org.junit.Test
   public void testDemoDbInit() throws Exception {
      OseeLog.log(DatabaseInitializationOperation.class, Level.INFO, "Begin database initialization...");

      String lastAuthenticationProtocol = OseeClientProperties.getAuthenticationProtocol();
      try {
         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
         OseeLog.registerLoggerListener(monitorLog);
         OseeClientProperties.setAuthenticationProtocol("trustAll");
         DatabaseInitializationOperation.executeWithoutPrompting("OSEE Demo Database");

         TestUtil.severeLoggingEnd(monitorLog);
         OseeLog.log(DatabaseInitializationOperation.class, Level.INFO, "Completed database initialization");
         wasDbInitSuccessful = true;
      } finally {
         OseeClientProperties.setAuthenticationProtocol(lastAuthenticationProtocol);
      }

      if (wasDbInitSuccessful) {
         ClientSessionManager.releaseSession();
         // Re-authenticate so we can continue
         ClientSessionManager.getSession();
      }
      OseeClientProperties.setInDbInit(false);

   }

   @org.junit.Test
   public void testPopulateDemoDb() {
      Assert.assertTrue("DbInit must be successful to continue", wasDbInitSuccessful);
      try {
         ClientSessionManager.releaseSession();
         // Re-authenticate so we can continue
         OseeClientSession session = ClientSessionManager.getSession();
         UserManager.releaseUser();

         Assert.assertEquals("Must run populate as Joe Smith", DemoUsers.Joe_Smith.getUserID(), session.getUserId());
         Assert.assertEquals("Must run populate as Joe Smith", DemoUsers.Joe_Smith.getUserID(),
            UserManager.getUser().getUserId());

         PopulateDemoActions populateDemoActions = new PopulateDemoActions(null);
         populateDemoActions.run(false);
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }
   }
}
