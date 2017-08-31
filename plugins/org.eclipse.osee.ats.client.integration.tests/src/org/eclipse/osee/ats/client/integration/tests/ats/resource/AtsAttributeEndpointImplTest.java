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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
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
      JsonObject json = queryAndReturnJsonObject("ats/attr");
      Assert.assertTrue(json.has("attrs"));
   }

   @Test
   public void testGetValidAssignees() throws Exception {
      JsonArray json = queryAndConfirmCount("ats/attr/Assignee", 6);
      Assert.assertEquals(DemoUsers.Alex_Kay.getName(), json.iterator().next().getAsString());
   }

   @Test
   public void testGetValidOriginators() throws Exception {
      queryAndConfirmCount("ats/attr/Originator", 6);
   }

   @Test
   public void testGetValidColorTeams() throws Exception {
      queryAndConfirmCount(AttributeKey.ColorTeam.getUrl(), 11);

      JsonArray json = queryAndConfirmCount("ats/attr/ColorTeam", 11);
      Assert.assertEquals("Blood Red Team", json.iterator().next().getAsString());
   }

   @Test
   public void testGetValidPriority() throws Exception {
      queryAndConfirmCount(AttributeKey.Priority.getUrl(), 5);

      JsonArray json = queryAndConfirmCount("ats/attr/Priority", 5);
      Assert.assertEquals("1", json.iterator().next().getAsString());
   }

   @Test
   public void testGetValidIPT() throws Exception {
      queryAndConfirmCount(AttributeKey.IPT.getUrl(), 8);

      JsonArray json = queryAndConfirmCount("ats/attr/IPT", 8);
      Assert.assertEquals("AH-6", json.iterator().next().getAsString());
   }

   @Test
   public void testEnumeration() throws Exception {
      JsonArray json = queryAndConfirmCount("ats/attr/" + AtsAttributeTypes.ChangeType.getIdString(), 4);
      Assert.assertEquals(ChangeType.Improvement.name(), json.iterator().next().getAsString());
   }

}
