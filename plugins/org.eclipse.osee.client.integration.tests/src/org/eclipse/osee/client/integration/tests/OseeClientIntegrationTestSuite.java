/*********************************************************************
 * Copyright (c) 2012 Boeing
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

package org.eclipse.osee.client.integration.tests;

import org.eclipse.osee.client.integration.tests.integration.DirtyArtifactCacheTest;
import org.eclipse.osee.client.integration.tests.integration.LongRunningTestSuite;
import org.eclipse.osee.client.integration.tests.integration.define.XDefineIntegrationTestSuite;
import org.eclipse.osee.client.integration.tests.integration.dsl.ui.integration.XDslUiIntegrationTestSuite;
import org.eclipse.osee.client.integration.tests.integration.endpoint.EndpointIntegrationTestSuite;
import org.eclipse.osee.client.integration.tests.integration.orcs.rest.OrcsRestTestSuite;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.XSkynetCoreIntegrationTestSuite;
import org.eclipse.osee.client.integration.tests.integration.ui.skynet.XUiSkynetCoreIntegrationTestSuite;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Roberto E. Escobar
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({
   OrcsRestTestSuite.class,
   EndpointIntegrationTestSuite.class,
   XDslUiIntegrationTestSuite.class,
   XDefineIntegrationTestSuite.class,
   XSkynetCoreIntegrationTestSuite.class,
   XUiSkynetCoreIntegrationTestSuite.class,
   LongRunningTestSuite.class,
   DirtyArtifactCacheTest.class})
public class OseeClientIntegrationTestSuite {
   // Test Suite

   @BeforeClass
   public static void setup() {
      System.setProperty("user.name", "3333");
   }
}
