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

package org.eclipse.osee.ats.ide.integration.tests.ats.query;

import com.fasterxml.jackson.databind.JsonNode;
import javax.ws.rs.client.WebTarget;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreBranchCategoryTokens;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link AtsProductLineEndpointImpl}
 *
 * @author Audrey Denk
 */
public class AtsProductLineEndpointTest extends AbstractRestTest {
   private final JaxRsApi jaxRsApi = AtsApiService.get().jaxRsApi();

   @Test
   public void testAtsPleBranchesRestCall() {

      String path = "ats/ple/branches";
      WebTarget target = jaxRsApi.newTargetQuery(path, "type", BranchType.BASELINE.getIdString(), "category",
         CoreBranchCategoryTokens.PLE.getIdString());
      System.out.println(target.getUri());

      testActionRestCall(target, 2);
   }

   private JsonNode testActionRestCall(WebTarget target, int size) {
      String json = getJson(target);
      JsonNode arrayNode = jaxRsApi.readTree(json);
      Assert.assertEquals(size, arrayNode.size());
      return arrayNode.get(0);
   }
}
