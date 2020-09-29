/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.ats.ide.integration.tests.framework.access;

import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.junit.BeforeClass;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

/**
 * @author Donald G. Dunne
 */
@RunWith(Suite.class)
@Suite.SuiteClasses({ //
   FrameworkAccessOnCommonTest.class, //
   FrameworkAccessByArtifactTest.class, //
   FrameworkAccessByAtttributeTypeTest.class, //
   FrameworkAccessByContextIdsTest.class, //
   AccessControlServiceTest.class //
})
public class FrameworkAccess_Suite {
   @BeforeClass
   public static void setUp() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }
}
