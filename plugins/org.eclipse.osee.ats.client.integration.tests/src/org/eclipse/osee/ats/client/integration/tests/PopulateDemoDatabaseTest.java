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
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.PopulateDemoActions;
import org.eclipse.osee.ats.client.demo.PopulateDemoAgile;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.IdeClientSession;
import org.eclipse.osee.framework.core.enums.DemoUsers;
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
         IdeClientSession session = ClientSessionManager.getSession();
         UserManager.releaseUser();

         Assert.assertEquals("Must run populate as Joe Smith (3333)", DemoUsers.Joe_Smith.getUserId(),
            session.getUserId());
         Assert.assertEquals("Must run populate as Joe Smith (3333)", DemoUsers.Joe_Smith.getUserId(),
            UserManager.getUser().getUserId());

         PopulateDemoActions populateDemoActions = new PopulateDemoActions(null);
         populateDemoActions.run(false);

         PopulateDemoAgile populateDemoAgile = new PopulateDemoAgile();
         populateDemoAgile.run();

         DemoUtil.setPopulateDbSuccessful(true);
         System.out.println("End Populate Demo DB...\n");
      } catch (Exception ex) {
         Assert.fail(Lib.exceptionToString(ex));
      }
   }

}
