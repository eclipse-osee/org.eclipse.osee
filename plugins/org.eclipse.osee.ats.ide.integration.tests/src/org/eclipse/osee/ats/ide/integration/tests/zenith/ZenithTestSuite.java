/*********************************************************************
 * Copyright (c) 2025 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.zenith;

import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.framework.jdk.core.util.OseeProperties;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({ScriptPurgeEndpointTest.class})
public class ZenithTestSuite {

   @BeforeClass
   public static void setUp() throws Exception {
      OseeProperties.setIsInTest(true);
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

}
