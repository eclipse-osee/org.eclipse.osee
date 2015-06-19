/*******************************************************************************
 * Copyright (c) 2011 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.integration.tests.ats;

import java.util.List;
import org.eclipse.osee.ats.api.country.JaxCountry;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.client.demo.DemoUtil;
import org.eclipse.osee.ats.client.demo.config.DemoCountry;
import org.eclipse.osee.ats.client.demo.config.DemoInsertion;
import org.eclipse.osee.ats.client.demo.config.DemoInsertionActivity;
import org.eclipse.osee.ats.client.demo.config.DemoProgram;
import org.eclipse.osee.ats.client.integration.tests.AtsClientService;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author Donald G. Dunne
 */
public class DemoCountryConfigTest {

   @BeforeClass
   public static void validateDbInit() throws OseeCoreException {
      DemoUtil.checkDbInitAndPopulateSuccess();
   }

   @Test
   public void testCreateCountry() throws Exception {
      List<JaxCountry> countries = AtsClientService.getCountryEp().get();
      Assert.assertEquals(2, countries.size());
   }

   @Test
   public void testCreateProgram() throws Exception {
      List<JaxProgram> programs = AtsClientService.getProgramEp().get();
      Assert.assertEquals(5, programs.size());
   }

   @Test
   public void testCreateInsertion() throws Exception {
      List<JaxInsertion> insertions = AtsClientService.getInsertionEp().get();
      Assert.assertEquals(12, insertions.size());
   }

   @Test
   public void testCreateInsertionActivity() throws Exception {
      List<JaxInsertionActivity> insertionActivities = AtsClientService.getInsertionActivityEp().get();
      Assert.assertEquals(10, insertionActivities.size());
   }

   @Test
   public void testCreateSawConfig() throws Exception {
      List<JaxCountry> countries = AtsClientService.getCountryEp().get();
      JaxCountry usgCountry = null;
      for (JaxCountry country : countries) {
         if (country.getUuid() == DemoCountry.usg.getUuid()) {
            usgCountry = country;
            break;
         }
      }
      Assert.assertNotNull(usgCountry);
      Assert.assertEquals(DemoCountry.usg.getName(), usgCountry.getName());
      Assert.assertEquals(DemoCountry.usg.getDescription(), usgCountry.getDescription());

      List<JaxProgram> programs = AtsClientService.getCountryEp().getProgram(usgCountry.getUuid()).get();
      Assert.assertEquals(2, programs.size());

      JaxProgram sawProgram = null;
      for (JaxProgram program : programs) {
         if (program.getUuid() == DemoProgram.sawProgram.getUuid()) {
            sawProgram = program;
            break;
         }
      }

      Assert.assertNotNull(sawProgram);
      Assert.assertEquals(DemoProgram.sawProgram.getName(), sawProgram.getName());

      List<JaxInsertion> insertions = AtsClientService.getProgramEp().getInsertion(sawProgram.getUuid()).get();
      Assert.assertEquals(4, insertions.size());

      JaxInsertion sawCommInsertion = null;
      for (JaxInsertion insertion : insertions) {
         if (insertion.getUuid() == DemoInsertion.sawComm.getUuid()) {
            sawCommInsertion = insertion;
            break;
         }
      }
      Assert.assertNotNull(sawCommInsertion);
      Assert.assertEquals(DemoInsertion.sawComm.getName(), sawCommInsertion.getName());
      Assert.assertEquals(DemoInsertion.sawComm.getDescription(), sawCommInsertion.getDescription());

      List<JaxInsertionActivity> insertionActivities =
         AtsClientService.getInsertionEp().getInsertionActivity(sawCommInsertion.getUuid()).get();
      Assert.assertEquals(2, insertionActivities.size());

      JaxInsertionActivity commPageInsertionActivity = null;
      for (JaxInsertionActivity activity : insertionActivities) {
         if (activity.getUuid() == DemoInsertionActivity.commPage.getUuid()) {
            commPageInsertionActivity = activity;
            break;
         }
      }
      Assert.assertNotNull(commPageInsertionActivity);
      Assert.assertEquals(DemoInsertionActivity.commPage.getName(), commPageInsertionActivity.getName());
      Assert.assertEquals(DemoInsertionActivity.commPage.getDescription(), commPageInsertionActivity.getDescription());
   }
}
