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
package org.eclipse.osee.support.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.test.AtsTest_TestDb_Suite;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.ui.skynet.test.FrameworkUi_TestDb_Suite;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses( {AtsTest_TestDb_Suite.class, FrameworkUi_TestDb_Suite.class})
/**
 * This Test Suite is to run against a postgres database with ATS Developer as the DbInit.<br>
 * <br>
 * Example test cases would be a test that purges or corrupts database data
 * 
 * @author Donald G. Dunne
 */
public class MasterTestSuite_TestDbTests {

   @BeforeClass
   public static void setUp() throws Exception {
      assertTrue("Should be run on production datbase.", TestUtil.isTestDb());
      assertFalse("Application Server must be running.", ClientSessionManager.getAuthenticationProtocols().contains(
            "demo"));
      assertFalse("Can't run using Demo Application Server",
            ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
   }

}
