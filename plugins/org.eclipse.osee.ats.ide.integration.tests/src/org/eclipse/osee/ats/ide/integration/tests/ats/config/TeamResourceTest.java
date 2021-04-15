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

package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import com.fasterxml.jackson.databind.JsonNode;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
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
      JsonNode team = testTeamUrl("/ats/team", 28, false);
      Assert.assertFalse(team.has("version"));
   }

   @Test
   public void testAtsTeamsDetailsRestCall() {
      JsonNode team = testTeamUrl("/ats/team/details", 28, true);
      Assert.assertEquals(3, team.get("version").size());
   }

   @Test
   public void testAtsTeamRestCall() {
      testUrl("/ats/team/" + DemoArtifactToken.SAW_SW.getIdString(), "SAW SW");
   }

   @Test
   public void testAtsTeamDetailsRestCall() {
      JsonNode team = testTeamUrl("/ats/team/" + DemoArtifactToken.SAW_SW.getIdString() + "/details", 1, true);
      Assert.assertTrue(team.has("version"));
   }
}