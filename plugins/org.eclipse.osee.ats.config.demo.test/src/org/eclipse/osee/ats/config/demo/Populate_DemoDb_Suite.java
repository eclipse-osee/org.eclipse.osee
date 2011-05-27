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
package org.eclipse.osee.ats.config.demo;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.config.demo.config.DemoDbGroupsTest;
import org.eclipse.osee.ats.config.demo.config.DemoDbUtil;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.plugin.core.util.OseeData;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({DemoDbGroupsTest.class, PopulateActionsTest.class})
/**
 * Tests related to validating the population of demo data.  Note the above 2 classes
 * are also done at end of DemoDbInit, but need to remain here to ensure that db hasn't been
 * corrupted on multiple runs of DemoDbTests
 * 
 * @author Donald G. Dunne
 */
public class Populate_DemoDb_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      DemoDbUtil.checkDbInitAndPopulateSuccess();
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running.",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      assertTrue("Client must authenticate using demo protocol",
         ClientSessionManager.getSession().getAuthenticationProtocol().equals("demo"));
      System.out.println("\n\nBegin " + Populate_DemoDb_Suite.class.getSimpleName());
      if (!OseeData.isProjectOpen()) {
         System.err.println("osee.data project should be open");
         OseeData.ensureProjectOpen();
      }
   }

   @AfterClass
   public static void tearDown() throws Exception {
      if (!OseeData.isProjectOpen()) {
         System.err.println("osee.data project should be open");
         OseeData.ensureProjectOpen();
      }
      System.out.println("End " + Populate_DemoDb_Suite.class.getSimpleName());
   }
}
