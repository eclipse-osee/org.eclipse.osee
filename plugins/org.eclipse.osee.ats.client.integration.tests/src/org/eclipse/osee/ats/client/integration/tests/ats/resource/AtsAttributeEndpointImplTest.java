/*******************************************************************************
 * Copyright (c) 2017 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats.resource;

import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.team.ChangeType;
import org.eclipse.osee.ats.api.workflow.AttributeKey;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
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
      AtsClientService.get().clearCaches();
   }

   @Test
   public void testGet() throws Exception {
      Assert.assertTrue(getJson("ats/attr").contains("attrs"));
   }

   @Test
   public void testGetValidAssignees() throws Exception {
      Object object = getFirstAndCount("ats/attr/Assignee", 6);
      Assert.assertEquals(DemoUsers.Alex_Kay.getName(), object);
   }

   @Test
   public void testGetValidOriginators() throws Exception {
      getFirstAndCount("ats/attr/Originator", 6);
   }

   @Test
   public void testGetValidColorTeams() throws Exception {
      getFirstAndCount(AttributeKey.ColorTeam.getUrl(), 11);

      Object object = getFirstAndCount("ats/attr/ColorTeam", 11);
      Assert.assertEquals("Blood Red Team", object);
   }

   @Test
   public void testGetValidPriority() throws Exception {
      getFirstAndCount(AttributeKey.Priority.getUrl(), 5);
      Object object = getFirstAndCount("ats/attr/Priority", 5);
      Assert.assertEquals("1", object);
   }

   @Test
   public void testGetValidIPT() throws Exception {
      getFirstAndCount(AttributeKey.IPT.getUrl(), 8);
      Object object = getFirstAndCount("ats/attr/IPT", 8);
      Assert.assertEquals("AH-6", object);
   }

   @Test
   public void testEnumeration() throws Exception {
      Object object = getFirstAndCount("ats/attr/" + AtsAttributeTypes.ChangeType.getIdString(), 4);
      Assert.assertEquals(ChangeType.Improvement.name(), object);
   }
}