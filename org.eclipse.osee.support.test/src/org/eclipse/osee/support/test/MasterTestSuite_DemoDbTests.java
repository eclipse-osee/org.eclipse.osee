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

import junit.extensions.TestSetup;
import junit.framework.Test;
import junit.framework.TestSuite;
import org.eclipse.osee.ats.test.AtsTest_Config_Suite;
import org.eclipse.osee.ats.test.AtsTest_Demo_Suite;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.skynet.core.test2.FrameworkCore_Demo_Suite;
import org.eclipse.osee.framework.ui.skynet.test.FrameworkUi_Demo_Suite;

/**
 * This suite should contain all cases and suites that can be run against a Demo Db Init and Demo Populated osee
 * database.
 * 
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoDbTests extends TestSuite {

   public static Test suite() throws ClassNotFoundException {
      TestSuite suite = new TestSuite("MasterTestSuite_DemoDbTests");

      suite.addTest(AtsTest_Config_Suite.suite());
      suite.addTest(AtsTest_Demo_Suite.suite());
      suite.addTest(FrameworkUi_Demo_Suite.suite());
      suite.addTest(FrameworkCore_Demo_Suite.suite());

      TestSetup wrapper = new TestSetup(suite) {
         @Override
         public void setUp() throws Exception {
            assertTrue("Demo Application Server must be running.",
                  ClientSessionManager.getAuthenticationProtocols().contains("demo"));
            assertTrue("Client must authenticate using demo protocol",
                  ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
         }
      };
      return wrapper;
   }
}
