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
package org.eclipse.osee.ats.test;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.database.initialize.DatabaseInitializationOperation;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoDbInit {

   @org.junit.Test
   public void testDemoDbInit() throws Exception {
      assertTrue("Demo Application Server must be running", ClientSessionManager.getAuthenticationProtocols().contains(
            "demo"));
      System.out.println("Begin Database Initialization...");
      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();

      DatabaseInitializationOperation.executeWithoutPrompting("OSEE Demo Database");

      TestUtil.severeLoggingEnd(monitorLog);
      System.out.println("Database Initialization Complete.");
   }

}
