/*******************************************************************************
 * Copyright (c) 2004, 2007 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.client.demo.config;

import static org.eclipse.osee.framework.core.enums.DemoBranches.CIS_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import org.eclipse.osee.ats.api.config.WorkType;
import org.eclipse.osee.ats.api.country.CountryEndpointApi;
import org.eclipse.osee.ats.api.country.JaxCountry;
import org.eclipse.osee.ats.api.data.AtsArtifactTypes;
import org.eclipse.osee.ats.api.data.AtsAttributeTypes;
import org.eclipse.osee.ats.api.data.AtsRelationTypes;
import org.eclipse.osee.ats.api.insertion.InsertionActivityEndpointApi;
import org.eclipse.osee.ats.api.insertion.InsertionEndpointApi;
import org.eclipse.osee.ats.api.insertion.JaxInsertion;
import org.eclipse.osee.ats.api.insertion.JaxInsertionActivity;
import org.eclipse.osee.ats.api.program.JaxProgram;
import org.eclipse.osee.ats.api.program.ProgramEndpointApi;
import org.eclipse.osee.ats.api.util.IAtsChangeSet;
import org.eclipse.osee.ats.api.version.IAtsVersion;
import org.eclipse.osee.ats.client.demo.internal.AtsClientService;
import org.eclipse.osee.ats.config.AtsDatabaseConfig;
import org.eclipse.osee.ats.core.client.util.AtsGroup;
import org.eclipse.osee.ats.demo.api.DemoArtifactToken;
import org.eclipse.osee.ats.demo.api.DemoCountry;
import org.eclipse.osee.ats.demo.api.DemoInsertion;
import org.eclipse.osee.ats.demo.api.DemoInsertionActivity;
import org.eclipse.osee.ats.demo.api.DemoProgram;
import org.eclipse.osee.ats.demo.api.DemoSubsystems;
import org.eclipse.osee.ats.workdef.AtsWorkDefinitionSheetProviders;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.exception.OseeWrappedException;
import org.eclipse.osee.framework.core.util.OsgiUtil;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.OseeSystemArtifacts;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.Requirements;
import org.eclipse.osee.orcs.rest.client.OseeClient;
import org.eclipse.osee.orcs.rest.model.ApplicabilityEndpoint;
import org.eclipse.osee.support.test.util.TestUtil;

/**
 * Initialization class that will load configuration information for a sample DB.
 *
 * @author Donald G. Dunne
 */
public class DemoDatabaseConfig implements IDbInitializationTask {

   @Override
   public void run() throws OseeCoreException {

      AtsWorkDefinitionSheetProviders.initializeDatabase(new XResultData(false), "demo");
      TestUtil.setDemoDb(true);

      // Create SAW_Bld_1 branch
      BranchManager.createTopLevelBranch(SAW_Bld_1);
      populateProgramBranch(SAW_Bld_1);

      ApplicabilityEndpoint applEndpoint =
         OsgiUtil.getService(getClass(), OseeClient.class).getApplicabilityEndpoint(SAW_Bld_1);
      applEndpoint.createDemoApplicability();

      configureForParallelCommit();

      // Create build one branch for CIS
      BranchManager.createTopLevelBranch(CIS_Bld_1);
      populateProgramBranch(CIS_Bld_1);

      AtsGroup.AtsTempAdmin.addMember(UserManager.getUser(DemoUsers.Joe_Smith));
      AtsGroup.AtsTempAdmin.getArtifact().persist("Set Joe as Temp Admin");

      AccessControlManager.setPermission(UserManager.getUser(DemoUsers.Joe_Smith), SAW_Bld_1,
         PermissionEnum.FULLACCESS);
      AccessControlManager.setPermission(UserManager.getUser(DemoUsers.Joe_Smith), CIS_Bld_1,
         PermissionEnum.FULLACCESS);

      AtsDatabaseConfig.organizePrograms(AtsArtifactTypes.Program, DemoArtifactToken.DemoPrograms);

      createUsgCountryConfig();
      createCntryCountryConfig();

      // Create Work Packages
      createAndSetWorkPackages();

      addSawWorkTypes();
   }

   /**
    * Configure SAW_Bld_1 and SAW_Bld_2 for parallel commit, including recursive setup where SAW_Bld_1 needs to be
    * committed to SAW_Bld_1 and SAW_Bld_2 and SAW_Bld_2 needs to be committed to SAW_Bld_2 and SAW_Bld_1
    */
   private void configureForParallelCommit() {
      IAtsChangeSet changes = AtsClientService.get().createChangeSet("configureForParallelCommit");

      IAtsVersion sawBld1Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_1);
      IAtsVersion sawBld2Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_2);
      IAtsVersion sawBld3Ver = AtsClientService.get().getVersionService().getById(DemoArtifactToken.SAW_Bld_3);

      changes.relate(sawBld1Ver, AtsRelationTypes.ParallelVersion_Child, sawBld2Ver);

      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld1Ver);
      changes.relate(sawBld2Ver, AtsRelationTypes.ParallelVersion_Child, sawBld3Ver);

      changes.execute();
   }

   private void addSawWorkTypes() {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Add SAW Work Types");
      Artifact sawProgram = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Program);
      for (DemoCsci csci : DemoCsci.values()) {
         sawProgram.addAttribute(AtsAttributeTypes.CSCI, csci.name());
      }
      sawProgram.persist(transaction);
      Artifact sawTeamDef = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_SW);
      for (Artifact child : sawTeamDef.getChildren()) {
         child.setSoleAttributeValue(AtsAttributeTypes.ProgramUuid, sawProgram);
         if (child.getName().contains("Code")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Code.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.DP.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
         } else if (child.getName().contains("Test")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Test.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.DP.name());
         } else if (child.getName().contains("Requirements")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Requirements.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
         } else if (child.getName().contains("Design")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.SW_Design.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
         } else if (child.getName().contains("HW")) {
            child.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Hardware.name());
            child.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
         }
         child.persist(transaction);
      }

      Artifact sawTestAi = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Test_AI);
      sawTestAi.setSoleAttributeValue(AtsAttributeTypes.ProgramUuid, sawProgram);
      sawTestAi.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Test.name());
      sawTestAi.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.DP.name());
      sawTestAi.persist(transaction);

      Artifact sawCodeAi = AtsClientService.get().getArtifact(DemoArtifactToken.SAW_Code_AI);
      sawCodeAi.setSoleAttributeValue(AtsAttributeTypes.ProgramUuid, sawProgram);
      sawCodeAi.setSoleAttributeValue(AtsAttributeTypes.WorkType, WorkType.Code.name());
      sawCodeAi.addAttribute(AtsAttributeTypes.CSCI, DemoCsci.SP.name());
      sawCodeAi.persist(transaction);

      transaction.execute();
   }

   // configure USG for country, program, insertion, activity and work package
   private void createUsgCountryConfig() {
      try {
         CountryEndpointApi countryEp = AtsClientService.getCountryEp();
         InsertionEndpointApi insertionEp = AtsClientService.getInsertionEp();
         InsertionActivityEndpointApi insertionActivityEp = AtsClientService.getInsertionActivityEp();

         // create country
         createCountry(countryEp, DemoCountry.usg);

         // relate country to programs
         SkynetTransaction transaction =
            TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Create USG Country Config");
         Artifact country =
            ArtifactQuery.getArtifactFromId(DemoCountry.usg.getUuid(), AtsClientService.get().getAtsBranch());
         Artifact program =
            ArtifactQuery.getArtifactFromId(DemoProgram.sawProgram.getUuid(), AtsClientService.get().getAtsBranch());
         country.addRelation(AtsRelationTypes.CountryToProgram_Program, program);
         program.persist(transaction);

         program =
            ArtifactQuery.getArtifactFromId(DemoProgram.cisProgram.getUuid(), AtsClientService.get().getAtsBranch());
         country.addRelation(AtsRelationTypes.CountryToProgram_Program, program);
         program.persist(transaction);
         country.persist(transaction);
         transaction.execute();

         // create and relate insertion and insertion activities
         DemoInsertion.getInsertions();
         DemoInsertionActivity.getActivities();
         for (DemoProgram demoProg : DemoCountry.usg.getPrograms()) {
            createInsertions(insertionEp, insertionActivityEp, demoProg);
         }

      } catch (Exception ex) {
         throw new OseeWrappedException("Error creating ATS USG Country Config", ex);
      }
   }

   private void createCntryCountryConfig() {
      try {
         DemoCountry country = DemoCountry.cntry;
         CountryEndpointApi countryEp = AtsClientService.getCountryEp();
         ProgramEndpointApi programEp = AtsClientService.getProgramEp();
         InsertionEndpointApi insertionEp = AtsClientService.getInsertionEp();
         InsertionActivityEndpointApi insertionActivityEp = AtsClientService.getInsertionActivityEp();

         // create country
         createCountry(countryEp, country);

         // create and relate programs
         DemoInsertion.getInsertions();
         DemoInsertionActivity.getActivities();
         for (DemoProgram program : country.getPrograms()) {
            createProgram(programEp, program);
            createInsertions(insertionEp, insertionActivityEp, program);
         }
      } catch (Exception ex) {
         throw new OseeWrappedException("Error creating ATS Cntry Country Config", ex);
      }
   }

   private void createInsertions(InsertionEndpointApi insertionEp, InsertionActivityEndpointApi insertionActivityEp, DemoProgram demoProg) throws Exception {
      for (DemoInsertion demoIns : demoProg.getInsertions()) {
         createInsertion(insertionEp, demoIns);

         // create and relate insertion activities
         for (DemoInsertionActivity demoInsertionActivity : demoIns.getActivities()) {
            createInsertionActivity(insertionActivityEp, demoInsertionActivity);
         }
      }
   }

   private JaxInsertionActivity createInsertionActivity(InsertionActivityEndpointApi insertionActivityEp, DemoInsertionActivity insertionActivity) throws Exception {
      JaxInsertionActivity jaxInsertionActivity = new JaxInsertionActivity();
      jaxInsertionActivity.setName(insertionActivity.getName());
      jaxInsertionActivity.setUuid(insertionActivity.getUuid());
      jaxInsertionActivity.setActive(insertionActivity.isActive());
      jaxInsertionActivity.setDescription(insertionActivity.getDescription());
      jaxInsertionActivity.setInsertionUuid(insertionActivity.getInsertionUuid());
      insertionActivityEp.create(jaxInsertionActivity);
      return jaxInsertionActivity;
   }

   private JaxInsertion createInsertion(InsertionEndpointApi insertionEp, DemoInsertion insertion) throws Exception {
      JaxInsertion jaxInsertion = new JaxInsertion();
      jaxInsertion.setName(insertion.getName());
      jaxInsertion.setUuid(insertion.getUuid());
      jaxInsertion.setActive(insertion.isActive());
      jaxInsertion.setDescription(insertion.getDescription());
      jaxInsertion.setProgramUuid(insertion.getProgramUuid());
      insertionEp.create(jaxInsertion);
      return jaxInsertion;
   }

   private JaxProgram createProgram(ProgramEndpointApi programEp, DemoProgram program) throws Exception {
      JaxProgram jaxProgram = new JaxProgram();
      jaxProgram.setName(program.getName());
      jaxProgram.setUuid(program.getUuid());
      jaxProgram.setActive(program.isActive());
      jaxProgram.setDescription(program.getDescription());
      jaxProgram.setCountryUuid(program.getCountryUuid());
      programEp.create(jaxProgram);
      return jaxProgram;
   }

   private JaxCountry createCountry(CountryEndpointApi countryEp, DemoCountry country) throws Exception {
      JaxCountry jaxCountry = new JaxCountry();
      jaxCountry.setName(country.getName());
      jaxCountry.setUuid(country.getUuid());
      jaxCountry.setActive(country.isActive());
      jaxCountry.setDescription(country.getDescription());
      countryEp.create(jaxCountry);
      return jaxCountry;
   }

   private void createAndSetWorkPackages() throws OseeCoreException {
      SkynetTransaction transaction =
         TransactionManager.createTransaction(AtsClientService.get().getAtsBranch(), "Create Work Packages");

      Artifact codeTeamArt =
         ArtifactQuery.getArtifactFromId(DemoArtifactToken.SAW_Code, AtsClientService.get().getAtsBranch());

      Artifact workPkg1 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_01, "ASDHFA443");
      workPkg1.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, codeTeamArt);
      relateInsertionActivity(workPkg1, DemoInsertionActivity.commPage);
      workPkg1.persist(transaction);

      Artifact workPkg2 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_02, "ASDHFA443");
      workPkg2.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, codeTeamArt);
      relateInsertionActivity(workPkg2, DemoInsertionActivity.commPage);
      workPkg2.persist(transaction);

      Artifact workPkg3 = createWorkPackage(DemoArtifactToken.SAW_Code_Team_WorkPackage_03, "ASDHFA443");
      workPkg3.setSoleAttributeValue(AtsAttributeTypes.Active, false);
      workPkg3.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, codeTeamArt);
      relateInsertionActivity(workPkg3, DemoInsertionActivity.commButton);
      workPkg3.persist(transaction);

      Artifact testTeamArt =
         ArtifactQuery.getArtifactFromId(DemoArtifactToken.SAW_Test_AI, AtsClientService.get().getAtsBranch());

      Artifact workPkg11 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0A, "AHESSH3");
      workPkg11.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, testTeamArt);
      relateInsertionActivity(workPkg11, DemoInsertionActivity.commPage);
      workPkg11.persist(transaction);

      Artifact workPkg21 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0B, "HAKSHD3");
      workPkg21.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, testTeamArt);
      relateInsertionActivity(workPkg21, DemoInsertionActivity.commPage);
      workPkg21.persist(transaction);

      Artifact workPkg31 = createWorkPackage(DemoArtifactToken.SAW_Test_AI_WorkPackage_0C, "EHA4DS");
      workPkg31.setSoleAttributeValue(AtsAttributeTypes.Active, false);
      workPkg31.addRelation(AtsRelationTypes.WorkPackage_TeamDefOrAi, testTeamArt);
      relateInsertionActivity(workPkg31, DemoInsertionActivity.commButton);
      workPkg31.persist(transaction);

      transaction.execute();
   }

   private void relateInsertionActivity(Artifact workPackageArt, DemoInsertionActivity insertionActivity) {
      Artifact insertionActivityArt = AtsClientService.get().getArtifact(insertionActivity.getUuid());
      insertionActivityArt.addRelation(AtsRelationTypes.InsertionActivityToWorkPackage_WorkPackage, workPackageArt);
   }

   private Artifact createWorkPackage(ArtifactToken workPackageToken, String activityId) throws OseeCoreException {
      Artifact workPkg1 = ArtifactTypeManager.addArtifact(workPackageToken, AtsClientService.get().getAtsBranch());
      char charAt = workPackageToken.getName().charAt(workPackageToken.getName().length() - 1);
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageId, "WP_0" + charAt);
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageProgram, "Program A");
      workPkg1.addAttributeFromString(AtsAttributeTypes.WorkPackageType, "LOE");
      workPkg1.addAttributeFromString(AtsAttributeTypes.ActivityId, activityId);
      workPkg1.addAttributeFromString(AtsAttributeTypes.ActivityName, "HUF 2" + charAt + "0");
      return workPkg1;
   }

   private void populateProgramBranch(BranchId programBranch) throws OseeCoreException {
      Artifact sawProduct =
         ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, programBranch, "SAW Product Decomposition");

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         sawProduct.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Component, programBranch, subsystem));
      }

      Artifact programRoot = OseeSystemArtifacts.getDefaultHierarchyRootArtifact(programBranch);
      programRoot.addChild(sawProduct);

      for (String name : new String[] {
         Requirements.SYSTEM_REQUIREMENTS,
         Requirements.SUBSYSTEM_REQUIREMENTS,
         Requirements.SOFTWARE_REQUIREMENTS,
         Requirements.HARDWARE_REQUIREMENTS,
         "Verification Tests",
         "Validation Tests",
         "Integration Tests",
         "Applicability Tests"}) {
         programRoot.addChild(ArtifactTypeManager.addArtifact(CoreArtifactTypes.Folder, programBranch, name));
      }

      sawProduct.persist(getClass().getSimpleName());
      programRoot.persist(getClass().getSimpleName());
   }

}
