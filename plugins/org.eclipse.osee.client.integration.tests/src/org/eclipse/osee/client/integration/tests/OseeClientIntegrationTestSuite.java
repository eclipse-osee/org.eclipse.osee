/*******************************************************************************
 * Copyright (c) 2012 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.client.integration.tests;

import org.eclipse.osee.client.integration.tests.integration.DirtyArtifactCacheTest;
import org.eclipse.osee.client.integration.tests.integration.LongRunningTestSuite;
import org.eclipse.osee.client.integration.tests.integration.define.XDefineIntegrationTestSuite;
import org.eclipse.osee.client.integration.tests.integration.dsl.ui.integration.XDslUiIntegrationTestSuite;
import org.eclipse.osee.client.integration.tests.integration.skynet.core.XSkynetCoreIntegrationTestSuite;
import org.eclipse.osee.client.integration.tests.integration.ui.skynet.XUiSkynetCoreIntegrationTestSuite;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
   XDslUiIntegrationTestSuite.class,
   XDefineIntegrationTestSuite.class,
   XSkynetCoreIntegrationTestSuite.class,
   XUiSkynetCoreIntegrationTestSuite.class,
   DirtyArtifactCacheTest.class,
   LongRunningTestSuite.class})
public class OseeClientIntegrationTestSuite {
   // Test Suite
}
