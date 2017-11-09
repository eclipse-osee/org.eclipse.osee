/*******************************************************************************
 * Copyright (c) 2015 Boeing.
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
import org.eclipse.osee.ats.demo.api.DemoCountry;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link CountryResource}
 *
 * @author Donald G. Dunne
 */
public class CountryResourceTest extends AbstractConfigurationRestTest {

   @Test
   public void testAtsCountriesRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/country");
      Assert.assertEquals(2, array.size());
      JsonObject obj = getObjectNamed("USG", array);
      Assert.assertNotNull("Did not find value USG", obj);
      Assert.assertFalse(obj.has("ats.Description"));
   }

   @Test
   public void testAtsCountriesDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/country/details");
      Assert.assertEquals(2, array.size());
      JsonObject obj = getObjectNamed("USG", array);
      Assert.assertNotNull("Did not find value USG", obj);
      Assert.assertTrue(obj.has("ats.Description"));
   }

   @Test
   public void testAtsCountryRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/country/" + DemoCountry.usg.getId());
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("USG", array);
      Assert.assertNotNull("Did not find value USG", obj);
      Assert.assertFalse(obj.has("ats.Description"));
   }

   @Test
   public void testAtsCountryDetailsRestCall() throws Exception {
      JsonArray array = getAndCheck("/ats/country/" + DemoCountry.usg.getId() + "/details");
      Assert.assertEquals(1, array.size());
      JsonObject obj = getObjectNamed("USG", array);
      Assert.assertNotNull("Did not find value USG", obj);
      Assert.assertTrue(obj.has("ats.Description"));
   }

}
