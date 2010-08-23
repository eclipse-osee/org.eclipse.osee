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
package org.eclipse.osee.client.integration.tests;

import java.util.logging.Level;
import junit.framework.Assert;
import org.eclipse.osee.client.integration.tests.suite.MasterTestSuite_DemoDbInit;
import org.eclipse.osee.client.integration.tests.suite.MasterTestSuite_DemoDbPopulate;
import org.eclipse.osee.client.integration.tests.suite.MasterTestSuite_DemoDbTests;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.logging.OseeLog;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({//
MasterTestSuite_DemoDbInit.class, //
   MasterTestSuite_DemoDbPopulate.class, //
   MasterTestSuite_DemoDbTests.class, //
})
/**
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoIntegrationSuite {

   //   @Rule
   //   public TemporaryFolder tempFolder = new TemporaryFolder();
   //
   //   @Rule
   //   public ExternalResource appServerResource = new ExternalResource() {
   //      private OseeAppServerUtil appServerTestUtil;
   //
   //      @Override
   //      protected void before() throws Throwable {
   //         File file = tempFolder.newFolder("appData");
   //         TestOseeAppServerConfig config = new TestOseeAppServerConfig(file.getAbsolutePath());
   //         appServerTestUtil = new OseeAppServerUtil(config);
   //         appServerTestUtil.start();
   //      };
   //
   //      @Override
   //      protected void after() {
   //         try {
   //            appServerTestUtil.stop();
   //         } catch (Exception ex) {
   //            OseeLog.log(MasterTestSuite_DemoIntegrationSuite.class, Level.SEVERE, ex);
   //         }
   //      };
   //   };

   @org.junit.Test
   public void setup() throws Exception {
      OseeLog.log(MasterTestSuite_DemoIntegrationSuite.class, Level.INFO,
         "Starting osee client integration test suite...");
      Assert.assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
   }
}
