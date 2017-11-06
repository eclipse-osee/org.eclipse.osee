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

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.eclipse.osee.ats.demo.api.DemoTeam;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link TeamResource}
 *
 * @author Donald G. Dunne
 */
public class TeamResourceTest extends AbstractConfigurationRestTest {

   @Test
   public void testAtsTeamsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/team");
      Assert.assertEquals(array.toString(), 18, array.size());
      JsonObject obj = getObjectNamed("SAW SW", array);
      Assert.assertNotNull(String.format("Did not find value SAW SW in JsonArray [%s]", array), obj);
      Assert.assertFalse(obj.has("version"));
      Assert.assertFalse(obj.has("ats.Active"));
   }

   @Test
   public void testAtsTeamsDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/team/details");
      Assert.assertEquals(array.toString(), 18, array.size());
      JsonObject obj = getObjectNamed("SAW SW", array);
      Assert.assertNotNull(String.format("Did not find value SAW SW in JsonArray [%s]", array), obj);
      Assert.assertEquals(3, obj.getAsJsonArray("version").size());
      Assert.assertTrue(obj.has("ats.Active"));
   }

   @Test
   public void testAtsTeamRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/team/" + getSawSwTeamDef().getArtId());
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("SAW SW", array);
      Assert.assertNotNull(String.format("Did not find value SAW SW in JsonArray [%s]", array), obj);
      Assert.assertFalse(obj.has("version"));
      Assert.assertFalse(obj.has("ats.Active"));
   }

   @Test
   public void testAtsTeamDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/team/" + getSawSwTeamDef().getArtId() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("SAW SW", array);
      Assert.assertNotNull(String.format("Did not find value SAW SW in JsonArray [%s]", array), obj);
      Assert.assertTrue(obj.has("version"));
      Assert.assertTrue(obj.has("ats.Active"));
   }

   private Artifact getSawSwTeamDef() {
      return ArtifactQuery.getArtifactFromToken(DemoTeam.SAW_SW.getTeamDefToken());
   }
}
