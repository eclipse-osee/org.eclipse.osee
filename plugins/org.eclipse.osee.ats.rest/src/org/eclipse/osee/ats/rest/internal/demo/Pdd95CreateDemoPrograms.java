/*********************************************************************
 * Copyright (c) 2004, 2007 Boeing
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

package org.eclipse.osee.ats.rest.internal.demo;

import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.country.JaxCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoCountry;
import org.eclipse.osee.ats.api.demo.DemoInsertion;
import org.eclipse.osee.ats.api.demo.DemoInsertionActivity;
import org.eclipse.osee.ats.api.demo.DemoProgram;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;

/**
 * @author Donald G. Dunne
 */
public class Pdd95CreateDemoPrograms extends AbstractPopulateDemoDatabase {

   private IAtsChangeSet changes;

   public Pdd95CreateDemoPrograms(XResultData rd, AtsApi atsApi) {
      super(rd, atsApi);
   }

   @Override
   public void run() {
      rd.logf("Running [%s]...\n", getClass().getSimpleName());
      changes = atsApi.createChangeSet(getClass().getSimpleName());
      createCountryConfig();
      changes.execute();
   }

   // configure country, program, insertion, activity
   private void createCountryConfig() {
      try {

         populateCountryStructure(DemoCountry.DEMO_COUNTRY_US);
         JaxCountry usCountry = createCountry(DemoCountry.DEMO_COUNTRY_US);
         changes.addChild(AtsArtifactToken.TopAgileFolder, ArtifactId.valueOf(usCountry.getId()));

         populateCountryStructure(DemoCountry.DEMO_COUNTRY_AJ);
         JaxCountry ajCountry = createCountry(DemoCountry.DEMO_COUNTRY_AJ);
         changes.addChild(AtsArtifactToken.TopAgileFolder, ArtifactId.valueOf(ajCountry.getId()));

      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void populateCountryStructure(DemoCountry country) {
      for (DemoProgram program : DemoProgram.getAllPrograms()) {
         if (country.getId().equals(program.getCountryId())) {
            country.getPrograms().add(program);

            for (DemoInsertion insertion : DemoInsertion.getAllInsertions()) {
               if (program.getId().equals(insertion.getProgramId())) {
                  program.getInsertions().add(insertion);

                  for (DemoInsertionActivity insertionAct : DemoInsertionActivity.getAllActivities()) {
                     if (insertion.getId().equals(insertionAct.getInsertionId())) {
                        insertion.getActivities().add(insertionAct);
                     }
                  }
               }
            }
         }
      }
   }

   private JaxCountry createCountry(DemoCountry country) {
      JaxCountry jaxCountry = new JaxCountry();
      jaxCountry.setName(country.getName());
      jaxCountry.setId(country.getId());
      jaxCountry.setActive(country.isActive());
      jaxCountry = atsApi.getProgramService().createCountry(jaxCountry, changes);
      for (DemoProgram program : country.getPrograms()) {
         createProgram(jaxCountry, program);
      }
      return jaxCountry;
   }

   private JaxProgram createProgram(JaxCountry country, DemoProgram program) {
      JaxProgram jaxProgram = new JaxProgram();
      jaxProgram.setName(program.getName());
      jaxProgram.setId(program.getId());
      jaxProgram.setActive(program.isActive());
      jaxProgram.setDescription(program.getDescription());
      jaxProgram.setCountryId(program.getCountryId());
      jaxProgram = atsApi.getProgramService().createProgram(jaxProgram, changes);
      for (DemoInsertion demoIns : program.getInsertions()) {
         createInsertion(jaxProgram, demoIns);
      }
      return jaxProgram;
   }

   private JaxInsertion createInsertion(JaxProgram jaxProgram, DemoInsertion demoInsertion) {
      JaxInsertion jaxInsertion = new JaxInsertion();
      jaxInsertion.setName(demoInsertion.getName());
      jaxInsertion.setId(demoInsertion.getId());
      jaxInsertion.setActive(demoInsertion.isActive());
      jaxInsertion.setDescription(demoInsertion.getDescription());
      jaxInsertion.setProgramId(demoInsertion.getProgramId());
      jaxInsertion = atsApi.getAgileService().createInsertion(demoInsertion, changes);
      for (DemoInsertionActivity insertionActivity : demoInsertion.getActivities()) {
         createInsertionActivity(jaxInsertion, insertionActivity, changes);
      }
      return jaxInsertion;
   }

   private JaxInsertionActivity createInsertionActivity(JaxInsertion jaxInsertion,
      DemoInsertionActivity insertionActivity, IAtsChangeSet changes) {
      JaxInsertionActivity jaxInsertionActivity = new JaxInsertionActivity();
      jaxInsertionActivity.setName(insertionActivity.getName());
      jaxInsertionActivity.setId(insertionActivity.getId());
      jaxInsertionActivity.setActive(insertionActivity.isActive());
      jaxInsertionActivity.setDescription(insertionActivity.getDescription());
      jaxInsertionActivity.setInsertionId(insertionActivity.getInsertionId());
      return atsApi.getAgileService().createInsertionActivity(jaxInsertionActivity, changes);
   }

}
