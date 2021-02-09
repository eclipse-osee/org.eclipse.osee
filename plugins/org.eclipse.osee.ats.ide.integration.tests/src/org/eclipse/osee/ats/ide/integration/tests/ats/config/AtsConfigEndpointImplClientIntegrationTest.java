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
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link AtsConfigEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsConfigEndpointImplClientIntegrationTest {

   @org.junit.Test
   public void testAlive() {
      AtsConfigEndpointApi configEp = AtsApiService.get().getServerEndpoints().getConfigEndpoint();
      XResultData resultData = configEp.alive();
      Assert.assertEquals("Alive", resultData.getResults().iterator().next());
   }

   @Test
   public void testKeyValue() {
      String value = "This is the one line test";
      AtsApiService.get().setConfigValue("Singleline", value);
      AtsApiService.get().reloadServerAndClientCaches();
      String configValue = AtsApiService.get().getConfigValue("Singleline");
      Assert.assertEquals(value, configValue);

      value = "This is the multi-line test \n Second line \n Third line";
      AtsApiService.get().setConfigValue("Multiline", value);
      AtsApiService.get().reloadServerAndClientCaches();
      configValue = AtsApiService.get().getConfigValue("Multiline");
      Assert.assertEquals(value, configValue);
   }
}