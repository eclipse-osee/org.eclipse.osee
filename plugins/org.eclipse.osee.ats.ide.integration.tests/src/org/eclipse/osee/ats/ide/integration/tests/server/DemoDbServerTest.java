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

package org.eclipse.osee.ats.ide.integration.tests.server;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;

/**
 * Calls server to run server tests.
 *
 * @author Donald G. Dunne
 */
public class DemoDbServerTest {

   @org.junit.Test
   public void testServerQueryBuilder() throws Exception {

      AtsApi atsApi = AtsApiService.get();
      XResultData results = atsApi.getServerEndpoints().getConfigEndpoint().demoDbServerTests();
      Assert.assertTrue(results.toString(), results.isSuccess());

   }

}
