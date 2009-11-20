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
package org.eclipse.osee.ats.config.demo.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import java.util.logging.Level;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.config.demo.config.PopulateDemoActions;
import org.eclipse.osee.ats.util.AtsUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.exception.OseeAuthenticationException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;

/**
 * @author Donald G. Dunne
 */
public class MasterTestSuite_DemoDbPopulate {

   @Before
   public void setUp() throws Exception {
      try {
         assertTrue("Demo Application Server must be running",
               ClientSessionManager.getAuthenticationProtocols().contains("demo"));
         // Confirm user is Joe Smith
         assertTrue("User \"Joe Smith\" does not exist in DB.  Run Demo DBInit prior to this test.",
               UserManager.getUserByUserId("Joe Smith") != null);
         // Confirm user is Joe Smith
         assertTrue(
               "Authenticated user should be \"Joe Smith\" and is not.  Check that Demo Application Server is being run.",
               UserManager.getUser().getUserId().equals("Joe Smith"));
      } catch (OseeAuthenticationException ex) {
         OseeLog.log(AtsPlugin.class, Level.SEVERE, ex);
         fail("Can't authenticate, either Demo Application Server is not running or Demo DbInit has not been performed");
      }
      // This test should only be run on test db
      assertFalse(AtsUtil.isProductionDb());
      System.out.println("Validating OSEE Application Server...");
      if (!OseeLog.isStatusOk()) {
         System.err.println(OseeLog.getStatusReport() + ". \nExiting.");
         return;
      }
      TestUtil.setIsInTest(true);
   }

   @AfterClass
   public static void tearDown() throws Exception {
      TestUtil.setIsInTest(false);
   }

   /**
    * Test method for {@link org.eclipse.osee.ats.config.demo.config.PopulateDemoActions#run()}.
    */
   @org.junit.Test
   public void testPopulateDemoDb() {
      try {
         PopulateDemoActions populateDemoActions = new PopulateDemoActions(null);
         populateDemoActions.run(false);
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }
   }
}
