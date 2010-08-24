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
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientProperties;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoDbInit {

   @org.junit.Test
   public void testDemoDbInit() throws Exception {
      OseeLog.log(DatabaseInitializationOperation.class, Level.INFO, "Begin database initialization...");

      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      TestUtil.setIsInTest(true);
      String lastAuthenticationProtocol = OseeClientProperties.getAuthenticationProtocol();
      try {
         SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
         OseeLog.registerLoggerListener(monitorLog);
         OseeClientProperties.setAuthenticationProtocol("trustAll");
         DatabaseInitializationOperation.executeWithoutPrompting("OSEE Demo Database");
         TestUtil.severeLoggingEnd(monitorLog);
         OseeLog.log(DatabaseInitializationOperation.class, Level.INFO, "Completed database initialization");
      } finally {
         OseeClientProperties.setAuthenticationProtocol(lastAuthenticationProtocol);
         TestUtil.setIsInTest(false);
      }
   }
}
