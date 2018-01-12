/*******************************************************************************
 * Copyright (c) 2014 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import org.codehaus.jackson.JsonNode;
import org.eclipse.osee.framework.core.util.JsonUtil;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test unit for {@link UserResource}
 *
 * @author Donald G. Dunne
 */
public class UserResourceTest extends AbstractRestTest {

   @Test
   public void testGet() throws Exception {
      String json = getJson("/ats/user");
      JsonNode users = JsonUtil.readTree(json);
      Assert.assertTrue(users.size() >= 9);
      Assert.assertEquals(3333, JsonUtil.getArrayElement(users, "name", "Joe Smith").get("id").asInt());
   }
}