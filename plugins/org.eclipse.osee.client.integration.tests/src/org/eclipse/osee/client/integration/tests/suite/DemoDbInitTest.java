/*******************************************************************************
 * Copyright (c) 2011 Boeing.
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
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.core.exception.OseeStateException;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.init.DatabaseInitOpFactory;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.User;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class DemoDbInitTest {
   private static boolean wasDbInitSuccessful = false;

   @BeforeClass
   public static void setup() throws Exception {
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      RenderingUtil.setPopupsAllowed(false);
   }

   @org.junit.Test
   public void testDemoDbInit() throws Exception {
      System.out.println("\nBegin database initialization...");

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeLog.registerLoggerListener(monitorLog);
      DatabaseInitOpFactory.executeWithoutPrompting("OSEE Demo Database");

      TestUtil.severeLoggingEnd(monitorLog);
      OseeLog.log(DemoDbInitTest.class, Level.INFO, "Completed database initialization");
      wasDbInitSuccessful = true;

      if (wasDbInitSuccessful) {
         DemoDbUtil.setDbInitSuccessful(true);

         // Re-authenticate so we can continue and NOT be bootstrap
         ClientSessionManager.releaseSession();
         ClientSessionManager.getSession();
         UserManager.releaseUser();

         if (UserManager.getUser().getUserId().equals("bootstrap")) {
            throw new OseeStateException("Should not be bootstrap user here");
         }

         //Clean up transactions author value changing from Bootstrap to the OSEE System user
         final String BOOTSTRAP_ART_ID = "0";
         User oseeUser = UserManager.getUser(SystemUser.OseeSystem);
         ConnectionHandler.runPreparedUpdate(String.format("update osee_tx_details set author='%s' where author='%s'",
            oseeUser.getArtId(), BOOTSTRAP_ART_ID));

      }

      System.out.println("End database initialization...");

   }
}
