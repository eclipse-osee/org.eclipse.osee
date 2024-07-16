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

package org.eclipse.osee.ats.ide.demo.populate;

import java.util.List;
import org.eclipse.osee.ats.api.country.CountryEndpointApi;
import org.eclipse.osee.ats.api.country.JaxCountry;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.api.demo.DemoCountry;
import org.eclipse.osee.ats.api.demo.DemoInsertion;
import org.eclipse.osee.ats.api.demo.DemoInsertionActivity;
import org.eclipse.osee.ats.api.demo.DemoProgram;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.workflow.IAtsTeamWorkflow;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.internal.AtsApiService;
import org.eclipse.osee.ats.ide.workflow.teamwf.TeamWorkFlowArtifact;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;

/**
 * @author Donald G. Dunne
 */
public class Pdd95CreateDemoEVConfigAndWorkPackages {

   public void run() {
      createCountryConfig();
      createCntryCountryConfig();
      createAndSetWorkPackages();
      setWorkPacakgeForWfs();
   }

   public void setWorkPacakgeForWfs() {
      IAtsChangeSet changes = AtsApiService.get().createChangeSet(getClass().getSimpleName());

      // set work packages
      TeamWorkFlowArtifact commWf = (TeamWorkFlowArtifact) DemoUtil.getSawCodeCommittedWf();
      changes.setSoleAttributeValue((IAtsTeamWorkflow) commWf, AtsAttributeTypes.WorkPackageReference,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_01);

      TeamWorkFlowArtifact unCommWf = (TeamWorkFlowArtifact) DemoUtil.getSawCodeUnCommittedWf();
      changes.setSoleAttributeValue((IAtsTeamWorkflow) unCommWf, AtsAttributeTypes.WorkPackageReference,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_01);

      TeamWorkFlowArtifact noBranchWf = (TeamWorkFlowArtifact) DemoUtil.getSawCodeNoBranchWf();
      changes.setSoleAttributeValue((IAtsTeamWorkflow) noBranchWf, AtsAttributeTypes.WorkPackageReference,
         DemoArtifactToken.SAW_Code_Team_WorkPackage_03);

      changes.execute();
   }

   private void createAndSetWorkPackages() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), "Create Work Packages");

      Artifact codeTeamArt = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.SAW_Code);

      Artifact workPkg1 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_01, "ASDHFA443");
      workPkg1.addRelation(AtsRelationTypes.TeamDefinitionToWorkPackage_AtsTeamDefOrAi, codeTeamArt);
      relateInsertionActivity(workPkg1, DemoInsertionActivity.commPage);
      workPkg1.persist(transaction);

      Artifact workPkg2 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_02, "ASDHFA443");
      workPkg2.addRelation(AtsRelationTypes.TeamDefinitionToWorkPackage_AtsTeamDefOrAi, codeTeamArt);
      relateInsertionActivity(workPkg2, DemoInsertionActivity.commPage);
      workPkg2.persist(transaction);

      Artifact workPkg3 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_03, "ASDHFA443");
      workPkg3.setSoleAttributeValue(AtsAttributeTypes.Active, false);
      workPkg3.addRelation(AtsRelationTypes.TeamDefinitionToWorkPackage_AtsTeamDefOrAi, codeTeamArt);
      relateInsertionActivity(workPkg3, DemoInsertionActivity.commButton);
      workPkg3.persist(transaction);

      Artifact testTeamArt = ArtifactQuery.getArtifactFromToken(DemoArtifactToken.SAW_Test_AI);

      Artifact workPkg11 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0A, "AHESSH3");
      workPkg11.addRelation(AtsRelationTypes.TeamDefinitionToWorkPackage_AtsTeamDefOrAi, testTeamArt);
      relateInsertionActivity(workPkg11, DemoInsertionActivity.commPage);
      workPkg11.persist(transaction);

      Artifact workPkg21 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0B, "HAKSHD3");
      workPkg21.addRelation(AtsRelationTypes.TeamDefinitionToWorkPackage_AtsTeamDefOrAi, testTeamArt);
      relateInsertionActivity(workPkg21, DemoInsertionActivity.commPage);
      workPkg21.persist(transaction);

      Artifact workPkg31 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0C, "EHA4DS");
      workPkg31.setSoleAttributeValue(AtsAttributeTypes.Active, false);
      workPkg31.addRelation(AtsRelationTypes.TeamDefinitionToWorkPackage_AtsTeamDefOrAi, testTeamArt);
      relateInsertionActivity(workPkg31, DemoInsertionActivity.commButton);
      workPkg31.persist(transaction);

      transaction.execute();
   }

   private void relateInsertionActivity(Artifact workPackageArt, DemoInsertionActivity insertionActivity) {
      Artifact insertionActivityArt = AtsApiService.get().getQueryServiceIde().getArtifact(insertionActivity.getId());
      insertionActivityArt.addRelation(AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage, workPackageArt);
   }

   private Artifact createWorkPackage(ArtifactToken workPackageToken, String activityId) {
      Artifact workPkg1 = ArtifactTypeManager.addArtifact(workPackageToken, AtsApiService.get().getAtsBranch());
      char charAt = workPackageToken.getName().charAt(workPackageToken.getName().length() - 1);
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageId, "WP_0" + charAt);
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageProgram, "Program A");
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageType, "LOE");
      workPkg1.addAttributeFromString(AtsAttributeTypes.ActivityId, activityId);
      workPkg1.addAttributeFromString(AtsAttributeTypes.ActivityName, "HUF 2" + charAt + "0");
      return workPkg1;
   }

   // configure country, program, insertion, activity and work package
   private void createCountryConfig() {
      try {
         CountryEndpointApi countryEp = AtsApiService.get().getServerEndpoints().getCountryEp();
         InsertionEndpointApi insertionEp = AtsApiService.get().getServerEndpoints().getInsertionEp();
         InsertionActivityEndpointApi insertionActivityEp =
            AtsApiService.get().getServerEndpoints().getInsertionActivityEp();

         // create country
         createCountry(countryEp, DemoCountry.demo);

         // relate country to programs
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsApiService.get().getAtsBranch(), "Create Demo Country Config");
         Artifact country =
            ArtifactQuery.getArtifactFromId(DemoCountry.demo.getId(), AtsApiService.get().getAtsBranch());
         Artifact program =
            ArtifactQuery.getArtifactFromId(DemoProgram.sawProgram.getId(), AtsApiService.get().getAtsBranch());
         country.addRelation(AtsRelationTypes.CountryToProgram_Program, program);
         program.persist(transaction);

         program = ArtifactQuery.getArtifactFromId(DemoProgram.cisProgram.getId(), AtsApiService.get().getAtsBranch());
         country.addRelation(AtsRelationTypes.CountryToProgram_Program, program);
         program.persist(transaction);
         country.persist(transaction);
         transaction.execute();
         List<DemoInsertion> insertions = DemoInsertion.getInsertions();
         List<DemoInsertionActivity> activities = DemoInsertionActivity.getActivities();

         // create and relate insertion and insertion activities
         for (DemoProgram demoProg : DemoCountry.demo.getPrograms()) {
            createInsertions(insertionEp, insertionActivityEp, demoProg);
         }

      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void createCntryCountryConfig() {
      try {
         DemoCountry country = DemoCountry.cntry;
         CountryEndpointApi countryEp = AtsApiService.get().getServerEndpoints().getCountryEp();
         ProgramEndpointApi programEp = AtsApiService.get().getServerEndpoints().getProgramEp();
         InsertionEndpointApi insertionEp = AtsApiService.get().getServerEndpoints().getInsertionEp();
         InsertionActivityEndpointApi insertionActivityEp =
            AtsApiService.get().getServerEndpoints().getInsertionActivityEp();

         // create country
         createCountry(countryEp, country);

         // create and relate programs; these calls ensure that the static tokens are loaded and thus related to proper parents
         // List<DemoInsertionActivity> list = DemoInsertionActivity.getActivities();
         for (DemoProgram program : country.getPrograms()) {
            createProgram(programEp, program);
            createInsertions(insertionEp, insertionActivityEp, program);
         }
      } catch (Exception ex) {
         throw OseeCoreException.wrap(ex);
      }
   }

   private void createInsertions(InsertionEndpointApi insertionEp, InsertionActivityEndpointApi insertionActivityEp,
      DemoProgram demoProg) throws Exception {
      for (DemoInsertion demoIns : demoProg.getInsertions()) {
         createInsertion(insertionEp, demoIns);

         // create and relate insertion activities
         for (DemoInsertionActivity demoInsertionActivity : demoIns.getActivities()) {
            createInsertionActivity(insertionActivityEp, demoInsertionActivity);
         }
      }
   }

   private JaxInsertionActivity createInsertionActivity(InsertionActivityEndpointApi insertionActivityEp,
      DemoInsertionActivity insertionActivity) throws Exception {
      JaxInsertionActivity jaxInsertionActivity = new JaxInsertionActivity();
      jaxInsertionActivity.setName(insertionActivity.getName());
      jaxInsertionActivity.setId(insertionActivity.getId());
      jaxInsertionActivity.setActive(insertionActivity.isActive());
      jaxInsertionActivity.setDescription(insertionActivity.getDescription());
      jaxInsertionActivity.setInsertionId(insertionActivity.getInsertionId());
      insertionActivityEp.create(jaxInsertionActivity);
      return jaxInsertionActivity;
   }

   private JaxInsertion createInsertion(InsertionEndpointApi insertionEp, DemoInsertion insertion) throws Exception {
      JaxInsertion jaxInsertion = new JaxInsertion();
      jaxInsertion.setName(insertion.getName());
      jaxInsertion.setId(insertion.getId());
      jaxInsertion.setActive(insertion.isActive());
      jaxInsertion.setDescription(insertion.getDescription());
      jaxInsertion.setProgramId(insertion.getProgramId());
      insertionEp.create(jaxInsertion);
      return jaxInsertion;
   }

   private JaxProgram createProgram(ProgramEndpointApi programEp, DemoProgram program) throws Exception {
      JaxProgram jaxProgram = new JaxProgram();
      jaxProgram.setName(program.getName());
      jaxProgram.setId(program.getId());
      jaxProgram.setActive(program.isActive());
      jaxProgram.setDescription(program.getDescription());
      jaxProgram.setCountryId(program.getCountryId());
      programEp.create(jaxProgram);
      return jaxProgram;
   }

   private JaxCountry createCountry(CountryEndpointApi countryEp, DemoCountry country) throws Exception {
      JaxCountry jaxCountry = new JaxCountry();
      jaxCountry.setName(country.getName());
      jaxCountry.setId(country.getId());
      jaxCountry.setActive(country.isActive());
      countryEp.create(jaxCountry);
      return jaxCountry;
   }

}
