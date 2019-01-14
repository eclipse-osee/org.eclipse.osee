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
package org.eclipse.osee.ats.ide.integration.tests.ats.config;

import org.eclipse.osee.ats.api.demo.DemoCountry;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Test;

/**
 * Unit Test for {@link CountryResource}
 *
 * @author Donald G. Dunne
 */
public class CountryResourceTest extends AbstractRestTest {

   private void testCountriesUrl(String url, int size, boolean hasDescription) {
      testUrl(url, size, "USG", "ats.Description", hasDescription);
   }

   @Test
   public void testAtsCountriesRestCall() {
      testCountriesUrl("/ats/country", 2, false);
   }

   @Test
   public void testAtsCountriesDetailsRestCall() {
      testCountriesUrl("/ats/country/details", 2, true);
   }

   @Test
   public void testAtsCountryRestCall() {
      testUrl("/ats/country/" + DemoCountry.usg.getId(), "USG");
   }

   @Test
   public void testAtsCountryDetailsRestCall() {
      testCountriesUrl("/ats/country/" + DemoCountry.usg.getId() + "/details", 1, true);
   }
}