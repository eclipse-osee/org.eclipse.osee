/*********************************************************************
 * Copyright (c) 2024 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.demo.dbpopulate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.client.ClientSessionManager;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeStateException;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.logging.SevereLoggingMonitor;
import org.eclipse.osee.framework.ui.skynet.render.RenderingUtil;
import org.eclipse.osee.support.test.util.TestUtil;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * Calls server to populate demo data including reqts, workflows and etc.
 *
 * @author Donald G. Dunne
 */
public class PopulateDemoDatabaseAndTest {

   @BeforeClass
   public static void setup() throws Exception {
      OseeProperties.setIsInTest(true);
      assertTrue("Demo Application Server must be running",
         ClientSessionManager.getAuthenticationProtocols().contains("orgdemo"));

      assertFalse("Not to be run on a production database.", ClientSessionManager.isProductionDataStore());

      RenderingUtil.setPopupsAllowed(false);
   }

   @org.junit.Test
   public void populateAndTest() throws Exception {
      OseeProperties.setIsInTest(true);
      List<String> protocols = ClientSessionManager.getAuthenticationProtocols();
      Assert.assertTrue("Application Server must be running. " + protocols, protocols.contains("orgdemo"));

      SevereLoggingMonitor monitorLog = TestUtil.severeLoggingStart();
      OseeLog.registerLoggerListener(monitorLog);

      AtsApi atsApi = AtsApiService.get();
      XResultData results = atsApi.getServerEndpoints().getConfigEndpoint().demoDbPopulate();
      if (results.isErrors()) {
         DemoUtil.setPopulateDbSuccessful(false);
         throw new OseeStateException("Error populating demo data %s", results.toString());
      }

      TestUtil.severeLoggingEnd(monitorLog);
      TestUtil.setDbInitSuccessful(true);

      atsApi.reloadServerAndClientCaches();
      OseeProperties.setIsInTest(false);
   }

}
