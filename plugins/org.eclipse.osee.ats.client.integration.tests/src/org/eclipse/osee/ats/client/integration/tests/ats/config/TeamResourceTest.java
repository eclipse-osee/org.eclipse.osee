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
package org.eclipse.osee.ats.client.integration.tests.ats.config;

import org.codehaus.jackson.JsonNode;
import org.eclipse.osee.ats.client.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link TeamResource}
 *
 * @author Donald G. Dunne
 */
public class TeamResourceTest extends AbstractRestTest {

   private JsonNode testTeamUrl(String url, int size, boolean hasVersion) {
      JsonNode obj = testUrl(url, size, "SAW SW", "ats.Active", hasVersion);
      return obj;
   }

   @Test
   public void testAtsTeamsRestCall() {
      JsonNode team = testTeamUrl("/ats/team", 18, false);
      Assert.assertFalse(team.has("version"));
   }

   @Test
   public void testAtsTeamsDetailsRestCall() {
      JsonNode team = testTeamUrl("/ats/team/details", 18, true);
      Assert.assertEquals(3, team.get("version").size());
   }

   @Test
   public void testAtsTeamRestCall() {
      JsonNode team = testTeamUrl("/ats/team/" + DemoArtifactToken.SAW_SW.getIdString(), 1, false);
      Assert.assertFalse(team.has("version"));
   }

   @Test
   public void testAtsTeamDetailsRestCall() {
      JsonNode team = testTeamUrl("/ats/team/" + DemoArtifactToken.SAW_SW.getIdString() + "/details", 1, true);
      Assert.assertTrue(team.has("version"));
   }
}