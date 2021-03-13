/*********************************************************************
 * Copyright (c) 2014 Boeing
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

package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.JaxRsApi;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link UserResource}
 *
 * @author Donald G. Dunne
 */
public class UserResourceTest extends AbstractRestTest {
   private final JaxRsApi jaxRsApi = AtsApiService.get().jaxRsApi();

   @Test
   public void testGet() throws Exception {
      String json = getJson("/ats/user");
      JsonNode users = jaxRsApi.readTree(json);
      Assert.assertTrue(users.size() >= 9);
      Assert.assertEquals(DemoUsers.Joe_Smith.getUserId(),
         JsonUtil.getArrayElement(users, "name", DemoUsers.Joe_Smith.getName()).get("userId").asText());
   }
}