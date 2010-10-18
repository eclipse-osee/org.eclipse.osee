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
import org.eclipse.osee.client.integration.tests.suite.MasterTestSuite_DemoDbInit;
import org.eclipse.osee.client.integration.tests.suite.MasterTestSuite_DemoDbTests;
import org.eclipse.osee.framework.logging.OseeLog;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({//
MasterTestSuite_DemoDbInit.class, //
   MasterTestSuite_DemoDbTests.class, //
})
/**
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoIntegrationSuite {

   @BeforeClass
   public static void setup() throws Exception {
      OseeLog.log(MasterTestSuite_DemoIntegrationSuite.class, Level.INFO,
         "Starting osee client integration test suite...");
   }
}
