/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests;

import org.eclipse.osee.ats.ide.demo.DemoUtil;
import org.junit.Assert;
import org.junit.BeforeClass;

/**
 * @author Donald G. Dunne
 */
public class AtsApiServiceTest {

   @BeforeClass
   public static void setUp() throws Exception {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

   @org.junit.Test
   public void testHealthEndpoint() {
      Assert.assertTrue(AtsApiService.get().getServerEndpoints().getHealthEndpoint().alive());
   }
}
