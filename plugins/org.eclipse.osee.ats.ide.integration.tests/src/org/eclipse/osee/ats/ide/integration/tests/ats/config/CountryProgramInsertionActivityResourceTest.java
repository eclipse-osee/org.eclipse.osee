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

import org.eclipse.osee.ats.api.country.JaxCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.ide.integration.tests.AtsApiService;
import org.eclipse.osee.ats.ide.integration.tests.ats.resource.AbstractRestTest;
import org.eclipse.osee.ats.ide.util.AtsApiIde;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.junit.Assert;
import org.junit.Test;

/**
 * Unit Test for {@link CountryResource}
 *
 * @author Donald G. Dunne
 */
public class CountryProgramInsertionActivityResourceTest extends AbstractRestTest {

   @Test
   public void testCreate() {
      AtsApiIde atsApi = AtsApiService.get();

      JaxCountry jaxCountry = new JaxCountry();
      jaxCountry.setName("New Country");
      jaxCountry.setActive(true);
      jaxCountry = atsApi.getServerEndpoints().getCountryEp().create(jaxCountry);
      Assert.assertNotNull(jaxCountry);

      ArtifactToken countryArt = atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.Country, "New Country");
      Assert.assertNotNull(countryArt);

      JaxCountry jaxCountry2 = atsApi.getServerEndpoints().getCountryEp().get(countryArt.getId());
      Assert.assertNotNull(jaxCountry2);
      Assert.assertEquals(jaxCountry.getName(), jaxCountry2.getName());

      JaxProgram jaxProgram = new JaxProgram();
      jaxProgram.setName("New Program");
      jaxProgram.setActive(true);
      jaxProgram.setCountryId(jaxCountry2.getId());
      jaxProgram = atsApi.getServerEndpoints().getProgramEp().create(jaxProgram);
      Assert.assertNotNull(jaxProgram);

      ArtifactToken programArt = atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.Program, "New Program");
      Assert.assertNotNull(programArt);
      JaxProgram jaxProgram2 = atsApi.getServerEndpoints().getProgramEp().get(programArt.getId());
      Assert.assertNotNull(jaxProgram2);
      Assert.assertEquals(jaxProgram.getName(), jaxProgram2.getName());
      Assert.assertEquals(jaxCountry2.getId(), Long.valueOf(jaxProgram2.getCountryId()));

      JaxInsertion jaxInsertion = new JaxInsertion();
      jaxInsertion.setName("New Insertion");
      jaxInsertion.setActive(true);
      jaxInsertion.setProgramId(programArt.getId());
      jaxInsertion = atsApi.getServerEndpoints().getInsertionEp().create(jaxInsertion);
      Assert.assertNotNull(jaxInsertion);

      ArtifactToken insertionArt =
         atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.Insertion, "New Insertion");
      Assert.assertNotNull(insertionArt);
      JaxInsertion jaxInsertion2 = atsApi.getServerEndpoints().getInsertionEp().get(insertionArt.getId());
      Assert.assertNotNull(jaxInsertion2);
      Assert.assertEquals(jaxInsertion.getName(), jaxInsertion2.getName());
      Assert.assertEquals(jaxProgram2.getId(), Long.valueOf(jaxInsertion2.getProgramId()));

      JaxInsertionActivity jaxInsertionActivity = new JaxInsertionActivity();
      jaxInsertionActivity.setName("New Insertion Activity");
      jaxInsertionActivity.setActive(true);
      jaxInsertionActivity.setInsertionId(insertionArt.getId());
      jaxInsertionActivity = atsApi.getServerEndpoints().getInsertionActivityEp().create(jaxInsertionActivity);
      Assert.assertNotNull(jaxInsertionActivity);

      ArtifactToken insertionActivytArt =
         atsApi.getQueryService().getArtifactByName(AtsArtifactTypes.InsertionActivity, "New Insertion Activity");
      Assert.assertNotNull(insertionActivytArt);
      JaxInsertionActivity jaxInsertionActivity2 =
         atsApi.getServerEndpoints().getInsertionActivityEp().get(insertionActivytArt.getId());
      Assert.assertNotNull(jaxInsertionActivity2);
      Assert.assertEquals(jaxInsertionActivity.getName(), jaxInsertionActivity2.getName());
      Assert.assertEquals(jaxInsertion2.getId(), Long.valueOf(jaxInsertionActivity2.getInsertionId()));

   }

   @Test
   public void testUpdateDelete() throws Exception {
      AtsApiIde atsApi = AtsApiService.get();

      JaxCountry jaxCountry = new JaxCountry();
      jaxCountry.setName("Update Country");
      jaxCountry = atsApi.getServerEndpoints().getCountryEp().create(jaxCountry);
      Assert.assertNotNull(jaxCountry);
      Assert.assertEquals("Update Country", jaxCountry.getName());

      jaxCountry.setName("Fixed Country");
      jaxCountry = atsApi.getServerEndpoints().getCountryEp().update(jaxCountry);
      Assert.assertNotNull(jaxCountry);
      Assert.assertEquals("Fixed Country", jaxCountry.getName());

      JaxProgram jaxProgram = new JaxProgram();
      jaxProgram.setName("Update Program");
      jaxProgram.setCountryId(jaxCountry.getId());
      jaxProgram = atsApi.getServerEndpoints().getProgramEp().create(jaxProgram);
      Assert.assertNotNull(jaxProgram);
      Assert.assertEquals("Update Program", jaxProgram.getName());

      jaxProgram.setName("Fixed Program");
      jaxProgram = atsApi.getServerEndpoints().getProgramEp().update(jaxProgram);
      Assert.assertNotNull(jaxProgram);
      Assert.assertEquals("Fixed Program", jaxProgram.getName());

      JaxInsertion jaxInsertion = new JaxInsertion();
      jaxInsertion.setName("Update Insertion");
      jaxInsertion.setProgramId(jaxProgram.getId());
      jaxInsertion = atsApi.getServerEndpoints().getInsertionEp().create(jaxInsertion);
      Assert.assertNotNull(jaxInsertion);
      Assert.assertEquals("Update Insertion", jaxInsertion.getName());

      jaxInsertion.setName("Fixed Insertion");
      jaxInsertion = atsApi.getServerEndpoints().getInsertionEp().update(jaxInsertion);
      Assert.assertNotNull(jaxInsertion);
      Assert.assertEquals("Fixed Insertion", jaxInsertion.getName());

      JaxInsertionActivity jaxInsertionActivity = new JaxInsertionActivity();
      jaxInsertionActivity.setName("Update Insertion Activity");
      jaxInsertionActivity.setInsertionId(jaxInsertion.getId());
      jaxInsertionActivity = atsApi.getServerEndpoints().getInsertionActivityEp().create(jaxInsertionActivity);
      Assert.assertNotNull(jaxInsertionActivity);
      Assert.assertEquals("Update Insertion Activity", jaxInsertionActivity.getName());

      jaxInsertionActivity.setName("Fixed Insertion Activity");
      jaxInsertionActivity = atsApi.getServerEndpoints().getInsertionActivityEp().update(jaxInsertionActivity);
      Assert.assertNotNull(jaxInsertionActivity);
      Assert.assertEquals("Fixed Insertion Activity", jaxInsertionActivity.getName());

      atsApi.getServerEndpoints().getInsertionActivityEp().delete(jaxInsertionActivity.getId());
      ArtifactToken insertActArt = atsApi.getQueryService().getArtifact(jaxInsertionActivity.getId());
      Assert.assertNull(insertActArt);

      atsApi.getServerEndpoints().getInsertionEp().delete(jaxInsertion.getId());
      ArtifactToken insertArt = atsApi.getQueryService().getArtifact(jaxInsertion.getId());
      Assert.assertNull(insertArt);

      atsApi.getServerEndpoints().getProgramEp().delete(jaxProgram.getId());
      ArtifactToken programArt = atsApi.getQueryService().getArtifact(jaxProgram.getId());
      Assert.assertNull(programArt);

      atsApi.getServerEndpoints().getCountryEp().delete(jaxCountry.getId());
      ArtifactToken countryArt = atsApi.getQueryService().getArtifact(jaxCountry.getId());
      Assert.assertNull(countryArt);

   }

}