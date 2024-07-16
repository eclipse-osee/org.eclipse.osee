/*********************************************************************
 * Copyright (c) 2025 Boeing
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

import java.util.List;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.country.CountryEndpointApi;
import org.eclipse.osee.ats.api.country.JaxCountry;
import org.eclipse.osee.ats.api.demo.DemoCountry;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link CountryEndpointImpl}
 *
 * @author Donald G. Dunne
 */
public class CountryEndpointImplTest extends AbstractRestTest {

   @Test
   public void test() {
      AtsApi atsApi = AtsApiService.get();
      CountryEndpointApi countryEp = atsApi.getServerEndpoints().getCountryEp();
      JaxCountry country = countryEp.get(DemoCountry.DEMO_COUNTRY_US.getId());
      Assert.assertNotNull(country);

      String json = getJson(String.format("ats/countryep/%s", DemoCountry.DEMO_COUNTRY_US.getId()));
      Assert.assertTrue(json.contains("\"id\" : \"77771\""));

      getFirstAndCount(String.format("ats/countryep/%s/program", DemoCountry.DEMO_COUNTRY_US.getId()), 2);

      json = getJson(String.format("ats/countryep/%s/program/version", DemoCountry.DEMO_COUNTRY_US.getId()));
      Assert.assertTrue(json.contains("\"id\" : \"44432231\""));

      ProgramEndpointApi programEp = countryEp.getProgram(country.getId());
      List<JaxProgram> programs = programEp.get();
      Assert.assertEquals(2, programs.size());
      JaxProgram sawProgram = null;
      for (JaxProgram prog : programs) {
         if (prog.getName().contains("SAW")) {
            sawProgram = prog;
         }
      }

      Assert.assertNotNull(sawProgram);

      json = getJson(String.format("ats/countryep/%s/program/%s", //
         DemoCountry.DEMO_COUNTRY_US.getId(), sawProgram.getId()));
      Assert.assertTrue(json.contains("\"id\" : \"19196003\""));

      InsertionEndpointApi insertionEp = programEp.getInsertion(sawProgram.getId());
      List<JaxInsertion> insertions = insertionEp.get();
      Assert.assertEquals(4, insertions.size());

      JaxInsertion commInsertion = null;
      for (JaxInsertion insert : insertions) {
         if (insert.getName().equals("COMM")) {
            commInsertion = insert;
         }
      }
      Assert.assertNotNull(commInsertion);

      getFirstAndCount(String.format("ats/countryep/%s/program/%s/insertion", //
         DemoCountry.DEMO_COUNTRY_US.getId(), sawProgram.getId()), 4);

      json = getJson(String.format("ats/countryep/%s/program/%s/insertion/%s", //
         DemoCountry.DEMO_COUNTRY_US.getId(), sawProgram.getId(), commInsertion.getId()));
      Assert.assertTrue(json.contains("\"id\" : \"23477771\""));

      InsertionActivityEndpointApi insertActEp = insertionEp.getInsertionActivity(commInsertion.getId());
      List<JaxInsertionActivity> insertActs = insertActEp.get();
      Assert.assertEquals(2, insertActs.size());

      JaxInsertionActivity commInsertAct = null;
      for (JaxInsertionActivity insertAct : insertActs) {
         if (insertAct.getName().equals("COMM Page")) {
            commInsertAct = insertAct;
         }
      }
      Assert.assertNotNull(commInsertAct);

      getFirstAndCount(String.format("ats/countryep/%s/program/%s/insertion/%s/activity", //
         DemoCountry.DEMO_COUNTRY_US.getId(), sawProgram.getId(), commInsertion.getId()), 2);

      json = getJson(String.format("ats/countryep/%s/program/%s/insertion/%s/activity/%s", //
         DemoCountry.DEMO_COUNTRY_US.getId(), sawProgram.getId(), commInsertion.getId(), commInsertAct.getId(),
         commInsertAct.getId()));
      Assert.assertTrue(json.contains("\"id\" : \"23477781\""));

   }

}