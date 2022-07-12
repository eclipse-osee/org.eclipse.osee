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

package org.eclipse.osee.ats.ide.integration.tests.ats.resource;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Test unit for {@link AtsActionEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class AtsAttributeEndpointImplTest extends AbstractRestTest {

   @BeforeClass
   public static void setup() {
      AtsApiService.get().clearCaches();
   }

   @Test
   public void testGet() throws Exception {
      Assert.assertTrue(getJson("ats/attr").contains("attrs"));
   }

   @Test
   public void testGetValidAssignees() throws Exception {
      Object object = getFirstAndCountGreater("ats/attr/Assignee", 6);
      Assert.assertEquals(DemoUsers.Alex_Kay.getName(), object);
   }

   @Test
   public void testGetValidOriginators() throws Exception {
      getFirstAndCountGreater("ats/attr/Originator", 6);
   }

   @Test
   public void testGetValidPriority() throws Exception {
      getFirstAndCount(AttributeKey.Priority.getUrl(), 5);
      Object object = getFirstAndCount("ats/attr/" + AtsAttributeTypes.Priority.getName(), 5);
      Assert.assertEquals("1", object);
   }

}