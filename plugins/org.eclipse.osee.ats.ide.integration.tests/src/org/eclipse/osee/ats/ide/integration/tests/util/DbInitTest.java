/*********************************************************************
 * Copyright (c) 2011 Boeing
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *     Boeing - initial API and implementation
 **********************************************************************/

package org.eclipse.osee.ats.ide.integration.tests.util;

import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.osee.ats.ide.demo.DemoChoice;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.access.UserGroupService;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class DbInitTest {
   private static boolean wasDbInitSuccessful = false;

   @BeforeClass
   public static void setup() throws Exception {
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("demo"));
      RenderingUtil.setPopupsAllowed(false);
   }

   @org.junit.Test
   public void testDbInit() throws Exception {
      OseeProperties.setIsInTest(true);
      List<String> protocols = ClientSessionManager.getAuthenticationProtocols();
      Assert.assertTrue("Application Server must be running." + protocols, protocols.contains("demo"));

      System.out.println("\nBegin Database Initialization");

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeLog.registerLoggerListener(monitorLog);

      DatabaseInitializationOperation.execute(DemoChoice.ATS_CLIENT_DEMO);

      TestUtil.severeLoggingEnd(monitorLog);
      wasDbInitSuccessful = true;

      if (wasDbInitSuccessful) {
         TestUtil.setDbInitSuccessful(true);

         // Re-authenticate so we can continue and NOT be bootstrap
         ClientSessionManager.releaseSession();
         ClientSessionManager.getSession();
         UserManager.releaseUser();

         AtsApiService.get().reloadServerAndClientCaches();

         if (UserManager.isBootstrap()) {
            throw new OseeStateException("Should not be bootstrap user here");
         }
         if (AtsApiService.get().getUserService().getCurrentUser().getUserId().equals("bootstrap")) {
            throw new OseeStateException("Should not be bootstrap user here");
         }

         UserManager.setSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_EDIT, "false");
         UserManager.getUser().saveSettings();

         UserGroupService.get(CoreUserGroups.DefaultArtifactEditor).addMember(UserManager.getUser());

      }

      OseeProperties.setIsInTest(false);

      //Ensure that all workDefs loaded without error
      AtsApiService.get().getWorkDefinitionService().getAllWorkDefinitions();

      System.out.println("End Database Initialization\n");

   }
}
