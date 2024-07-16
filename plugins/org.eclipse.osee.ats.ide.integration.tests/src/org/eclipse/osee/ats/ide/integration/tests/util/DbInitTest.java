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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.ide.demo.DemoChoice;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.core.data.UserService;
import org.eclipse.osee.framework.core.enums.CoreUserGroups;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.database.init.DatabaseInitializationOperation;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class DbInitTest {
   @BeforeClass
   public static void setup() throws Exception {
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("orgdemo"));

      assertFalse("Not to be run on a production database.", ClientSessionManager.isProductionDataStore());

      RenderingUtil.setPopupsAllowed(false);
   }

   @org.junit.Test
   public void testDbInit() throws Exception {
      OseeProperties.setIsInTest(true);
      List<String> protocols = ClientSessionManager.getAuthenticationProtocols();
      Assert.assertTrue("Application Server must be running. " + protocols, protocols.contains("orgdemo"));

      System.out.println("\nBegin Database Initialization");

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeLog.registerLoggerListener(monitorLog);

      DatabaseInitializationOperation.execute(DemoChoice.ATS_CLIENT_DEMO);

      TestUtil.severeLoggingEnd(monitorLog);

      TestUtil.setDbInitSuccessful(true);

      // Re-authenticate so we can continue and NOT be OSEE System
      ClientSessionManager.releaseSession();
      ClientSessionManager.getSession();
      UserManager.releaseUser();

      AtsApi atsApi = AtsApiService.get();
      atsApi.reloadServerAndClientCaches();

      UserService userService = atsApi.userService();
      assertNotEquals("User should not be OseeSystem here", userService.getUser(), SystemUser.OseeSystem);

      UserManager.setSetting(UserManager.DOUBLE_CLICK_SETTING_KEY_EDIT, "false");
      UserManager.getUser().saveSettings();

      userService.getUserGroup(CoreUserGroups.DefaultArtifactEditor).addMember(UserManager.getUser(), true);

      OseeProperties.setIsInTest(false);

      //Ensure that all workDefs loaded without error
      atsApi.getWorkDefinitionService().getAllWorkDefinitions();

      System.out.println("End Database Initialization\n");

   }

}
