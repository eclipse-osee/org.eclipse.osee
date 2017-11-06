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
import org.junit.Test;

/**
 * Unit Test for {@link VersionResource}
 *
 * @author Donald G. Dunne
 */
public class VersionResourceTest extends AbstractConfigurationRestTest {

   @Test
   public void testAtsVersionsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/version");
      Assert.assertEquals(6, array.size());
      JsonObject obj = getObjectNamed("SAW_Bld_1", array);
      Assert.assertNotNull("Did not find value SAW_Bld_1", obj);
      Assert.assertFalse(obj.has("ats.Released"));
   }

   @Test
   public void testAtsVersionsDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/version/details");
      Assert.assertEquals(6, array.size());
      JsonObject obj = getObjectNamed("SAW_Bld_1", array);
      Assert.assertNotNull("Did not find value SAW_Bld_1", obj);
      Assert.assertTrue(obj.has("ats.Released"));
   }

   @Test
   public void testAtsVersionRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/version/" + getSawBld1().getArtId());
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("SAW_Bld_1", array);
      Assert.assertNotNull("Did not find value SAW_Bld_1", obj);
      Assert.assertFalse(obj.has("ats.Released"));
   }

   @Test
   public void testAtsVersionDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/version/" + getSawBld1().getArtId() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("SAW_Bld_1", array);
      Assert.assertNotNull("Did not find value SAW_Bld_1", obj);
      Assert.assertTrue(obj.has("ats.Released"));
   }

   private Artifact getSawBld1() {
      return ArtifactQuery.getArtifactFromTypeAndName(AtsArtifactTypes.Version, "SAW_Bld_1",
         AtsClientService.get().getAtsBranch());
   }

}
