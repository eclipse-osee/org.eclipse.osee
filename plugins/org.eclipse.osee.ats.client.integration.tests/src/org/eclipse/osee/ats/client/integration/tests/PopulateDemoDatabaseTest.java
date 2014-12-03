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
package org.eclipse.osee.ats.client.integration.tests;

import static org.junit.Assert.assertTrue;
import org.eclipse.osee.ats.client.demo.DemoUsers;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.PopulateDemoActions;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.client.OseeClientSession;
import org.eclipse.osee.framework.database.core.ConnectionHandler;
import org.eclipse.osee.framework.database.core.OseeInfo;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class PopulateDemoDatabaseTest {

   private static final String INSERT_KEY_VALUE_SQL = "INSERT INTO osee_info (OSEE_KEY, OSEE_VALUE) VALUES (?, ?)";
   private static final String DELETE_KEY_SQL = "DELETE FROM osee_info WHERE OSEE_KEY = ?";

   @BeforeClass
   public static void setup() throws Exception {
      if (!DemoUtil.isDbInitSuccessful()) {
         throw new OseeStateException("DbInit must be successful to continue");
      }
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      RenderingUtil.setPopupsAllowed(false);
      DemoUtil.setPopulateDbSuccessful(false);
   }

   @org.junit.Test
   public void testPopulateDemoDb() {
      System.out.println("\nBegin Populate Demo DB...");
      try {
         ClientSessionManager.releaseSession();
         // Re-authenticate so we can continue
         OseeClientSession session = ClientSessionManager.getSession();
         UserManager.releaseUser();

         Assert.assertEquals("Must run populate as Joe Smith", DemoUsers.Joe_Smith.getUserId(), session.getUserId());
         Assert.assertEquals("Must run populate as Joe Smith", DemoUsers.Joe_Smith.getUserId(),
            UserManager.getUser().getUserId());

         PopulateDemoActions populateDemoActions = new PopulateDemoActions(null);
         populateDemoActions.run(false);

         DemoUtil.setPopulateDbSuccessful(true);
         System.out.println("End Populate Demo DB...\n");
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }
   }

   @org.junit.Test
   public void testMaxStaleness() throws InterruptedException {

      ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, "testKey", "testValue");
      String value = OseeInfo.getValue("testKey");
      Assert.assertEquals("testValue", value);

      ConnectionHandler.runPreparedUpdate(DELETE_KEY_SQL, "testKey");
      ConnectionHandler.runPreparedUpdate(INSERT_KEY_VALUE_SQL, "testKey", "testValue2");
      // Max Staleness is 3 weeks cached value shouldn't change
      value = OseeInfo.getValue("testKey");
      Assert.assertEquals("testValue", value);

      // Max Staleness is 1 second cached value shouldn't change even if we sleep for .8 seconds
      Thread.sleep(800);
      value = OseeInfo.getValue("testKey", (long) 1000);
      Assert.assertEquals("testValue", value);

      // Wait another .2 seconds and max staleness of 1 second will be exceeded and fresh value retrieved from DB
      Thread.sleep(800);
      value = OseeInfo.getValue("testKey", (long) 1000);
      Assert.assertEquals("testValue2", value);
   }

}
