/*********************************************************************
 * Copyright (c) 2017 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import org.eclipse.osee.ats.api.config.AtsConfigEndpointApi;
import org.eclipse.osee.ats.ide.integration.tests.AtsClientService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;

/**
 * Test unit for {@link AtsConfigEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsConfigEndpointImplClientIntegrationTest {

   @org.junit.Test
   public void testAlive() {
      AtsConfigEndpointApi configEp = AtsClientService.get().getServerEndpoints().getConfigEndpoint();
      XResultData resultData = configEp.alive();
      Assert.assertEquals("Alive", resultData.getResults().iterator().next());
   }

}