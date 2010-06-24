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

import static org.junit.Assert.assertTrue;
import java.util.logging.Level;
import org.eclipse.osee.client.integration.tests.suite.MasterTestSuite_DemoDbInit;
import org.eclipse.osee.client.integration.tests.suite.MasterTestSuite_DemoDbPopulate;
import org.eclipse.osee.client.integration.tests.suite.MasterTestSuite_DemoDbTests;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.eclipse.osee.framework.logging.OseeLog;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {//
MasterTestSuite_DemoDbInit.class, //
      MasterTestSuite_DemoDbPopulate.class, //
      MasterTestSuite_DemoDbTests.class, //
})
/**
 * @author Donald G. Dunne
 */
public class MasterSuite_DemoIntegrationSuite {

   @org.junit.Test
   public void setup() throws Exception {
      OseeLog.log(DatabaseInitializationOperation.class, Level.INFO, "Begin database initialization...");

      assertTrue("Demo Application Server must be running", ClientSessionManager.getAuthenticationProtocols().contains(
            "demo"));
   }
}
