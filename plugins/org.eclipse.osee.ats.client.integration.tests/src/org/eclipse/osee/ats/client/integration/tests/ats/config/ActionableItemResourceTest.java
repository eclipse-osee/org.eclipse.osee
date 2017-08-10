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
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Unit Test for {@link ActionableItemResource}
 *
 * @author Donald G. Dunne
 */
public class ActionableItemResourceTest extends AbstractConfigurationRestTest {

   @BeforeClass
   public static void setup() {
      AtsClientService.get().clearCaches();
   }

   @Test
   public void testAtsAisRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/ai");
      Assert.assertEquals(46, array.size());
      JsonObject obj = getObjectNamed("SAW Code", array);
      Assert.assertNotNull("Did not find value SAW Code", obj);
      Assert.assertFalse(obj.has("ats.Active"));
   }

   @Test
   public void testAtsAisDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/ai/details");
      Assert.assertEquals(46, array.size());
      JsonObject obj = getObjectNamed("SAW Code", array);
      Assert.assertNotNull("Did not find value SAW Code", obj);
      Assert.assertTrue(obj.has("ats.Active"));
   }

   @Test
   public void testAtsAiRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/ai/" + getSawCodeAi().getArtId());
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("SAW Code", array);
      Assert.assertNotNull("Did not find value SAW Code", obj);
      Assert.assertFalse(obj.has("ats.Active"));
   }

   @Test
   public void testAtsAiDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/ai/" + getSawCodeAi().getArtId() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("SAW Code", array);
      Assert.assertNotNull("Did not find value SAW Code", obj);
      Assert.assertTrue(obj.has("ats.Active"));
   }

   private Artifact getSawCodeAi() {
      return ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.ActionableItem, "SAW Code",
         AtsClientService.get().getAtsBranch());
   }

}
