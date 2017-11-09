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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
      JsonArray users = getAndCheckArray("/ats/user");
      Assert.assertTrue(users.size() >= 9);
      JsonObject obj = getObjectNamed("Joe Smith", users);
      Assert.assertEquals("3333", obj.get("id").getAsString());
   }

}
