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
package org.eclipse.osee.ats.config.demo.config;

import java.io.File;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osee.ats.AtsPlugin;
import org.eclipse.osee.ats.actions.wizard.NewActionJob;
import org.eclipse.osee.ats.artifact.ActionArtifact;
import org.eclipse.osee.ats.artifact.ActionableItemArtifact;
import org.eclipse.osee.ats.artifact.TeamWorkFlowArtifact;
import org.eclipse.osee.ats.artifact.VersionArtifact;
import org.eclipse.osee.ats.config.demo.OseeAtsConfigDemoPlugin;
import org.eclipse.osee.ats.config.demo.config.AtsConfigDemoDatabaseConfig.SawBuilds;
import org.eclipse.osee.ats.config.demo.util.Cscis;
import org.eclipse.osee.ats.config.demo.util.DemoTeams;
import org.eclipse.osee.ats.config.demo.util.ProgramAttributes;
import org.eclipse.osee.ats.config.demo.util.Subsystems;
import org.eclipse.osee.ats.config.demo.util.DemoTeams.Team;
import org.eclipse.osee.ats.util.DefaultTeamState;
import org.eclipse.osee.ats.util.DefaultTeamWorkflowManager;
import org.eclipse.osee.ats.util.AtsPriority.PriorityType;
import org.eclipse.osee.framework.skynet.core.SkynetAuthentication;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.Branch;
import org.eclipse.osee.framework.skynet.core.artifact.BranchPersistenceManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactTypeNameSearch.SearchOperator;
import org.eclipse.osee.framework.skynet.core.attribute.ArtifactSubtypeDescriptor;
import org.eclipse.osee.framework.skynet.core.attribute.ConfigurationPersistenceManager;
import org.eclipse.osee.framework.skynet.core.dbinit.SkynetDbInit;
import org.eclipse.osee.framework.skynet.core.relation.RelationSide;
import org.eclipse.osee.framework.skynet.core.transaction.AbstractSkynetTxTemplate;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionIdManager;
import org.eclipse.osee.framework.skynet.core.user.UserEnum;
import org.eclipse.osee.framework.skynet.core.util.Requirements;
import org.eclipse.osee.framework.ui.plugin.util.Result;
import org.eclipse.osee.framework.ui.skynet.TagBranchesJob;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactExtractor;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportJob;
import org.eclipse.osee.framework.ui.skynet.Import.IArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.NewArtifactImportResolver;
import org.eclipse.osee.framework.ui.skynet.Import.WordOutlineExtractor;
import org.eclipse.osee.framework.ui.skynet.handler.GeneralWordOutlineHandler;
import org.eclipse.osee.framework.ui.skynet.util.ChangeType;
import org.eclipse.osee.framework.ui.skynet.util.OSEELog;
import org.eclipse.osee.framework.ui.skynet.widgets.workflow.UserCommunity;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItem;
import org.eclipse.osee.framework.ui.skynet.widgets.xnavigate.XNavigateItemAction;
import org.eclipse.swt.widgets.Display;

/**
 * Run from the ATS Navigator after the DB is configured for "OSEE Demo Database", this class will populate the database
 * with sample actions written against XYZ configured teams
 * 
 * @author Donald G. Dunne
 */
public class PopulateDemoActions extends XNavigateItemAction {

   private String[] TITLE_PREFIX =
         new String[] {"Problem with the", "Can't see the", "Button A doesn't work on", "Add to the",
               "Make new Button for ", "User can't load "};
   private ChangeType[] CHANGE_TYPE =
         new ChangeType[] {ChangeType.Problem, ChangeType.Problem, ChangeType.Problem, ChangeType.Improvement,
               ChangeType.Improvement, ChangeType.Support, ChangeType.Improvement, ChangeType.Support};

   public PopulateDemoActions(XNavigateItem parent) {
      super(parent, "Populate Demo Actions");
   }

   @Override
   public void run() throws SQLException {
      AtsPlugin.setEmailEnabled(false);
      if (SkynetDbInit.isDbInit() || (!SkynetDbInit.isDbInit() && MessageDialog.openConfirm(
            Display.getCurrent().getActiveShell(), getName(), getName()))) {
         try {

            setDefaultBranch(BranchPersistenceManager.getInstance().getKeyedBranch(SawBuilds.SAW_Bld_1.name()));

            // Import all requirements on SAW_Bld_1 Branch
            ImportRequirementsTx importTx =
                  new ImportRequirementsTx(BranchPersistenceManager.getInstance().getAtsBranch(),
                        !SkynetDbInit.isDbInit());
            importTx.execute();

            sleep(5000);

            // Create traceability between System, Subsystem and Software requirements
            CreateTraceabilityTx traceTx =
                  new CreateTraceabilityTx(BranchPersistenceManager.getInstance().getAtsBranch(),
                        !SkynetDbInit.isDbInit());
            traceTx.execute();

            sleep(5000);

            // Create SAW_Bld_2 Child Main Working Branch off SAW_Bld_1
            CreateMainWorkingBranchTx saw2BranchTx =
                  new CreateMainWorkingBranchTx(BranchPersistenceManager.getInstance().getAtsBranch(),
                        !SkynetDbInit.isDbInit());
            saw2BranchTx.execute();

            // Create SAW_Bld_2 Actions 
            Set<ActionArtifact> actionArts =
                  createActions(getReqSawActionsData(), AtsConfigDemoDatabaseConfig.SawBuilds.SAW_Bld_2.toString(),
                        null);

            // Sleep to wait for the persist of the actions
            sleep(3000);

            for (ActionArtifact actionArt : actionArts) {
               if (actionArt.getDescriptiveName().contains("(committed)")) {
                  // Working Branch off SAW_Bld_2, Make Changes, Commit
                  makeAction1ReqChanges(actionArt);
               } else if (actionArt.getDescriptiveName().contains("(uncommitted)")) {
                  // Working Branch off SAW_Bld_2, Make Changes, DON'T Commit
                  makeAction2ReqChanges(actionArt);
               }
            }

            // Create actions against non-requirement AIs and Teams
            createNonReqChangeDemoActions();

            // Tag all artifacts and all branches
            tagAllArtifacts();

            OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Populate Complete", false);

         } catch (Exception ex) {
            OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
         }
      }
   }

   public void tagAllArtifacts() throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Tagging Branches", false);
      Job job = new TagBranchesJob(BranchPersistenceManager.getInstance().getBranches());
      job.setUser(false);
      job.setPriority(Job.LONG);
      job.schedule();
      job.join();
   }

   public class CreateTraceabilityTx extends AbstractSkynetTxTemplate {
      public CreateTraceabilityTx(Branch branch, boolean popup) {
         super(branch);
      }

      @Override
      protected void handleTxWork() throws Exception {
         try {
            Collection<Artifact> systemArts = getArtTypeRequirements(Requirements.SYSTEM_REQUREMENT, "Robot");

            Collection<Artifact> component = getArtTypeRequirements(Requirements.COMPONENT, "API");
            component.addAll(getArtTypeRequirements(Requirements.COMPONENT, "Hardware"));
            component.addAll(getArtTypeRequirements(Requirements.COMPONENT, "Sensor"));

            Collection<Artifact> subSystemArts = getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Robot");
            subSystemArts.addAll(getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Video"));
            subSystemArts.addAll(getArtTypeRequirements(Requirements.SUBSYSTEM_REQUIREMENT, "Interface"));

            Collection<Artifact> softArts = getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Robot");
            softArts.addAll(getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Video"));
            softArts.addAll(getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, "Interface"));

            // Relate System to SubSystem to Software Requirements
            for (Artifact systemArt : systemArts) {
               systemArt.relate(RelationSide.REQUIREMENT_TRACE__LOWER_LEVEL, subSystemArts, true);

               for (Artifact subSystemArt : subSystemArts) {
                  subSystemArt.relate(RelationSide.REQUIREMENT_TRACE__LOWER_LEVEL, softArts, true);
               }
            }

            // Relate System, SubSystem and Software Requirements to Componets
            for (Artifact art : systemArts)
               art.relate(RelationSide.ALLOCATION__COMPONENT, component, true);
            for (Artifact art : subSystemArts)
               art.relate(RelationSide.ALLOCATION__COMPONENT, component, true);
            for (Artifact art : softArts)
               art.relate(RelationSide.ALLOCATION__COMPONENT, component, true);

            // Create Test Script Artifacts
            Set<Artifact> verificationTests = new HashSet<Artifact>();
            Artifact verificationHeader =
                  (new ArtifactTypeNameSearch("Folder", "Verification Tests",
                        BranchPersistenceManager.getInstance().getDefaultBranch())).getSingletonArtifact(Artifact.class);
            if (verificationHeader == null) throw new IllegalStateException("Could not find Verification Tests header");
            for (String str : new String[] {"A", "B", "C"}) {
               Artifact newArt =
                     ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                           Requirements.TEST_SCRIPT).makeNewArtifact(verificationHeader.getBranch());
               newArt.setDescriptiveName("Verification Test " + str);
               verificationTests.add(newArt);
               verificationHeader.relate(RelationSide.DEFAULT_HIERARCHICAL__CHILD, newArt, true);
               newArt.persist(true);
            }
            Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

            // Create Validation Test Procedure Artifacts
            Set<Artifact> validationTests = new HashSet<Artifact>();
            Artifact validationHeader =
                  (new ArtifactTypeNameSearch("Folder", "Validation Tests",
                        BranchPersistenceManager.getInstance().getDefaultBranch())).getSingletonArtifact(Artifact.class);
            if (validationHeader == null) throw new IllegalStateException("Could not find Validation Tests header");
            for (String str : new String[] {"1", "2", "3"}) {
               Artifact newArt =
                     ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                           Requirements.TEST_PROCEDURE).makeNewArtifact(validationHeader.getBranch());
               newArt.setDescriptiveName("Validation Test " + str);
               validationTests.add(newArt);
               validationHeader.relate(RelationSide.DEFAULT_HIERARCHICAL__CHILD, newArt, true);
               newArt.persist(true);
            }
            Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

            // Create Integration Test Procedure Artifacts
            Set<Artifact> integrationTests = new HashSet<Artifact>();
            Artifact integrationHeader =
                  (new ArtifactTypeNameSearch("Folder", "Integration Tests",
                        BranchPersistenceManager.getInstance().getDefaultBranch())).getSingletonArtifact(Artifact.class);
            if (integrationHeader == null) throw new IllegalStateException("Could not find integration Tests header");
            for (String str : new String[] {"X", "Y", "Z"}) {
               Artifact newArt =
                     ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                           Requirements.TEST_PROCEDURE).makeNewArtifact(integrationHeader.getBranch());
               newArt.setDescriptiveName("integration Test " + str);
               integrationTests.add(newArt);
               integrationHeader.relate(RelationSide.DEFAULT_HIERARCHICAL__CHILD, newArt, true);
               newArt.persist(true);
            }
            Artifact integrationTestsArray[] = integrationTests.toArray(new Artifact[integrationTests.size()]);

            // Relate Software Artifacts to Tests
            Artifact softReqsArray[] = softArts.toArray(new Artifact[softArts.size()]);
            softReqsArray[0].relate(RelationSide.Validation__Validator, verificationTestsArray[0], true);
            softReqsArray[0].relate(RelationSide.Validation__Validator, verificationTestsArray[1], true);
            softReqsArray[1].relate(RelationSide.Validation__Validator, verificationTestsArray[0], true);
            softReqsArray[1].relate(RelationSide.Validation__Validator, validationTestsArray[1], true);
            softReqsArray[2].relate(RelationSide.Validation__Validator, validationTestsArray[0], true);
            softReqsArray[2].relate(RelationSide.Validation__Validator, integrationTestsArray[1], true);
            softReqsArray[3].relate(RelationSide.Validation__Validator, integrationTestsArray[0], true);
            softReqsArray[4].relate(RelationSide.Validation__Validator, integrationTestsArray[2], true);
            softReqsArray[5].relate(RelationSide.Validation__Validator, validationTestsArray[2], true);

         } catch (Exception ex) {
            OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
         }
      }
   }

   public class ImportRequirementsTx extends AbstractSkynetTxTemplate {
      public ImportRequirementsTx(Branch branch, boolean popup) {
         super(branch);
      }

      @Override
      protected void handleTxWork() throws Exception {
         try {
            importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SOFTWARE_REQUIREMENT + "s",
                  Requirements.SOFTWARE_REQUIREMENT, "support/SAW-SoftwareRequirements.xml");
            importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SYSTEM_REQUREMENT + "s",
                  Requirements.SYSTEM_REQUREMENT, "support/SAW-SystemRequirements.xml");
            importRequirements(SawBuilds.SAW_Bld_1.name(), Requirements.SUBSYSTEM_REQUIREMENT + "s",
                  Requirements.SUBSYSTEM_REQUIREMENT, "support/SAW-SubsystemRequirements.xml");
         } catch (Exception ex) {
            OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
         }
      }
   }

   public class CreateMainWorkingBranchTx extends AbstractSkynetTxTemplate {
      public CreateMainWorkingBranchTx(Branch branch, boolean popup) {
         super(branch);
      }

      @Override
      protected void handleTxWork() throws Exception {
         try {
            OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating SAW_Bld_2 branch off SAW_Bld_1", false);
            // Create SAW_Bld_2 branch off SAW_Bld_1
            createChildMainWorkingBranch(SawBuilds.SAW_Bld_1.name(), SawBuilds.SAW_Bld_2.name());
            sleep(5000);
            // Map team definitions versions to their related branches
            AtsConfigDemoDatabaseConfig.mapTeamVersionToBranch(DemoTeams.getInstance().getTeamDef(Team.SAW_SW),
                  SawBuilds.SAW_Bld_2.name(), SawBuilds.SAW_Bld_2.name());
         } catch (Exception ex) {
            OSEELog.logException(OseeAtsConfigDemoPlugin.class, ex, false);
         }
      }
   }

   private Branch createChildMainWorkingBranch(String parentBrachName, String childBranchName) throws Exception {
      Branch parentBranch = BranchPersistenceManager.getInstance().getKeyedBranch(parentBrachName);

      Branch childBranch =
            BranchPersistenceManager.getInstance().createWorkingBranch(
                  TransactionIdManager.getInstance().getEditableTransactionId(parentBranch), childBranchName,
                  childBranchName, SkynetAuthentication.getInstance().getUser(UserEnum.NoOne));
      return childBranch;
   }

   private void makeAction1ReqChanges(ActionArtifact actionArt) throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Making Action 1 Requirement Changes", false);
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getDescriptiveName().contains("Req")) reqTeam = team;
      }

      if (reqTeam == null) throw new IllegalArgumentException("Can't locate Req team.");
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating working branch", false);
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error creating working branch: ")).append(result.getText()).toString());

      sleep(40000);

      setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

      for (Artifact art : getSoftwareRequirements(SoftwareRequirementStrs.Robot)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Modifying artifact => ")).append(art).toString(), false);
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Navigation.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.name(), "A");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Navigation.name());
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch(Requirements.COMPONENT, "Navigation",
                     BranchPersistenceManager.getInstance().getDefaultBranch());
         art.relate(RelationSide.ALLOCATION__COMPONENT, srch.getSingletonArtifactOrException(Artifact.class));
         art.persist(true);
      }

      for (Artifact art : getSoftwareRequirements(SoftwareRequirementStrs.Event)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Modifying artifact => ")).append(art).toString(), false);
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.name(), "D");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch(Requirements.COMPONENT, "Robot API",
                     BranchPersistenceManager.getInstance().getDefaultBranch());
         art.relate(RelationSide.ALLOCATION__COMPONENT, srch.getSingletonArtifactOrException(Artifact.class));
         art.persist(true);
      }

      // Delete two artifacts
      for (Artifact art : getSoftwareRequirements(SoftwareRequirementStrs.daVinci)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Deleting artifact => ")).append(art).toString(), false);
         art.delete();
      }

      // Add three new artifacts
      Artifact parentArt = getInterfaceInitializationSoftwareRequirement();
      for (int x = 1; x < 4; x++) {
         String name = "Robot Interface Init " + x;
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Adding artifact => " + name, false);
         Artifact newArt =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     Requirements.SOFTWARE_REQUIREMENT).makeNewArtifact(parentArt.getBranch());
         newArt.setDescriptiveName(name);
         newArt.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.name(), "D");
         newArt.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         newArt.persist(true);
         parentArt.addChild(newArt);
         parentArt.persist(true);
      }

      sleep(2000L);
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Committing branch", false);
      result = reqTeam.getSmaMgr().getBranchMgr().commitWorkingBranch(false, true);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error committing working branch: ")).append(result.getText()).toString());

      sleep(40000);

      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Completing Action", false);
   }

   private void sleep(long milliseconds) throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Sleeping " + milliseconds, false);
      Thread.sleep(milliseconds);
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Awake", false);
   }

   private void makeAction2ReqChanges(ActionArtifact actionArt) throws Exception {
      TeamWorkFlowArtifact reqTeam = null;
      for (TeamWorkFlowArtifact team : actionArt.getTeamWorkFlowArtifacts()) {
         if (team.getTeamDefinition().getDescriptiveName().contains("Req")) reqTeam = team;
      }

      if (reqTeam == null) throw new IllegalArgumentException("Can't locate Req team.");
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating working branch", false);
      Result result = reqTeam.getSmaMgr().getBranchMgr().createWorkingBranch(null, false);
      if (result.isFalse()) throw new IllegalArgumentException(
            (new StringBuilder("Error creating working branch: ")).append(result.getText()).toString());

      sleep(40000);

      setDefaultBranch(reqTeam.getSmaMgr().getBranchMgr().getWorkingBranch());

      for (Artifact art : getSoftwareRequirements(SoftwareRequirementStrs.Functional)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Modifying artifact => ")).append(art).toString(), false);
         art.setSoleAttributeValue(ProgramAttributes.CSCI.name(), Cscis.Interface.name());
         art.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.name(), "D");
         art.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         ArtifactTypeNameSearch srch =
               new ArtifactTypeNameSearch(Requirements.COMPONENT, "Robot API",
                     BranchPersistenceManager.getInstance().getDefaultBranch());
         art.relate(RelationSide.ALLOCATION__COMPONENT, srch.getSingletonArtifactOrException(Artifact.class));
         art.persist(true);
      }

      // Delete one artifacts
      for (Artifact art : getSoftwareRequirements(SoftwareRequirementStrs.CISST)) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
               (new StringBuilder("Deleting artifact => ")).append(art).toString(), false);
         art.delete();
      }

      // Add two new artifacts
      Artifact parentArt = getInterfaceInitializationSoftwareRequirement();
      for (int x = 15; x < 17; x++) {
         String name = "Claw Interface Init " + x;
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Adding artifact => " + name, false);
         Artifact newArt =
               ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(
                     Requirements.SOFTWARE_REQUIREMENT).makeNewArtifact(parentArt.getBranch());
         newArt.setDescriptiveName(name);
         newArt.setSoleAttributeValue(ProgramAttributes.Safety_Criticality.name(), "D");
         newArt.setSoleAttributeValue(ProgramAttributes.Subsystem.name(), Subsystems.Communications.name());
         newArt.persist(true);
         parentArt.addChild(newArt);
         parentArt.persist(true);
      }

   }

   private enum SoftwareRequirementStrs {
      Robot, CISST, daVinci, Functional, Event
   };

   private Set<Artifact> getSoftwareRequirements(SoftwareRequirementStrs str) {
      return getArtTypeRequirements(Requirements.SOFTWARE_REQUIREMENT, str.name());
   }

   private Set<Artifact> getArtTypeRequirements(String artifactType, String artifactNameStr) {
      OSEELog.logInfo(
            OseeAtsConfigDemoPlugin.class,
            "Getting \"" + artifactNameStr + "\" requirement(s) from Branch " + BranchPersistenceManager.getInstance().getDefaultBranch().getBranchName(),
            false);
      ArtifactTypeNameSearch srch =
            new ArtifactTypeNameSearch(artifactType, artifactNameStr,
                  BranchPersistenceManager.getInstance().getDefaultBranch(), SearchOperator.LIKE);
      Set<Artifact> arts = srch.getArtifacts(Artifact.class);
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Found " + arts.size() + " Artifacts", false);
      return arts;
   }

   private static String INTERFACE_INITIALIZATION = "Interface Initialization";

   private Artifact getInterfaceInitializationSoftwareRequirement() {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Getting \"" + INTERFACE_INITIALIZATION + "\" requirement.", false);
      ArtifactTypeNameSearch srch =
            new ArtifactTypeNameSearch(Requirements.SOFTWARE_REQUIREMENT, INTERFACE_INITIALIZATION,
                  BranchPersistenceManager.getInstance().getDefaultBranch(), SearchOperator.EQUAL);
      return srch.getArtifacts(Artifact.class).iterator().next();
   }

   private void createNonReqChangeDemoActions() throws Exception {
      createActions(getNonReqSawActionData(), AtsConfigDemoDatabaseConfig.SawBuilds.SAW_Bld_3.toString(), null);
      createActions(getNonReqSawActionData(), AtsConfigDemoDatabaseConfig.SawBuilds.SAW_Bld_2.toString(), null);
      createActions(getNonReqSawActionData(), AtsConfigDemoDatabaseConfig.SawBuilds.SAW_Bld_1.toString(),
            DefaultTeamState.Completed);
      createActions(getGenericActionData(), null, null);
   }

   private void setDefaultBranch(Branch branch) throws Exception {
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Setting default branch to \"" + branch + "\".", false);
      BranchPersistenceManager.getInstance().setDefaultBranch(branch);
      sleep(2000L);
      Branch defaultBranch = BranchPersistenceManager.getInstance().getDefaultBranch();
      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Current Default == \"" + defaultBranch + "\".", false);
   }

   private void importRequirements(String buildName, String rootArtifactName, String requirementArtifactName, String filename) throws Exception {

      OSEELog.logInfo(OseeAtsConfigDemoPlugin.class,
            "Importing \"" + rootArtifactName + "\" requirements on branch \"" + buildName + "\"", false);
      Branch branch = BranchPersistenceManager.getInstance().getKeyedBranch(buildName);
      ArtifactTypeNameSearch srch = new ArtifactTypeNameSearch("Folder", rootArtifactName, branch);
      Artifact systemReq = srch.getSingletonArtifactOrException(Artifact.class);
      File file = OseeAtsConfigDemoPlugin.getInstance().getPluginFile(filename);
      IArtifactImportResolver artifactResolver = new NewArtifactImportResolver();
      ArtifactSubtypeDescriptor mainDescriptor =
            ConfigurationPersistenceManager.getInstance().getArtifactSubtypeDescriptor(requirementArtifactName);
      ArtifactExtractor extractor =
            new WordOutlineExtractor(mainDescriptor, branch, 0, new GeneralWordOutlineHandler());
      Job job = new ArtifactImportJob(file, systemReq, extractor, branch, artifactResolver);
      job.setPriority(Job.LONG);
      job.schedule();
      job.join();
      // Validate that something was imported
      if (systemReq.getChildren().size() == 0) throw new IllegalStateException("Artifacts were not imported");

   }

   private Set<ActionArtifact> createActions(Set<ActionData> actionDatas, String versionStr, DefaultTeamState toStateOverride) throws Exception {
      Set<ActionArtifact> actionArts = new HashSet<ActionArtifact>();
      int currNum = 1;
      for (ActionData aData : actionDatas) {
         OSEELog.logInfo(OseeAtsConfigDemoPlugin.class, "Creating " + currNum++ + "/" + actionDatas.size(), false);
         int x = 0;
         for (String prefixTitle : aData.prefixTitles) {
            ActionArtifact actionArt =
                  NewActionJob.createAction(null, prefixTitle + " " + aData.postFixTitle,
                        TITLE_PREFIX[x] + " " + aData.postFixTitle, CHANGE_TYPE[x], PriorityType.Priority_1,
                        aData.getUserCommunities(), false, null, aData.getActionableItems());
            actionArts.add(actionArt);
            for (TeamWorkFlowArtifact teamWf : actionArt.getTeamWorkFlowArtifacts()) {
               DefaultTeamWorkflowManager dtwm = new DefaultTeamWorkflowManager(teamWf);
               dtwm.transitionTo((toStateOverride != null ? toStateOverride : aData.toState), null, false);
               teamWf.persist(true);
               if (versionStr != null && !versionStr.equals("")) {
                  VersionArtifact verArt =
                        ((new ArtifactTypeNameSearch(VersionArtifact.ARTIFACT_NAME, versionStr,
                              BranchPersistenceManager.getInstance().getAtsBranch())).getSingletonArtifactOrException(VersionArtifact.class));
                  teamWf.relate(RelationSide.TeamWorkflowTargetedForVersion_Version, verArt);
                  teamWf.persist(true);
               }
            }
         }
      }
      return actionArts;
   }

   private Set<ActionData> getReqSawActionsData() {
      Set<ActionData> actionDatas = new HashSet<ActionData>();
      actionDatas.add(new ActionData(new String[] {"SAW Requirement (committed) Changes for"}, "Diagram View",
            PriorityType.Priority_1, new String[] {DemoAIs.SAW_Requirements.getAIName(), DemoAIs.SAW_Code.getAIName(),
                  DemoAIs.SAW_Test.getAIName()}, new Integer[] {1}, DefaultTeamState.Implement, false, false, false));
      actionDatas.add(new ActionData(new String[] {"SAW More Requirement (uncommitted) Changes for"}, "Diagram View",
            PriorityType.Priority_3, new String[] {DemoAIs.SAW_Code.getAIName(), DemoAIs.SAW_SW_Design.getAIName(),
                  DemoAIs.SAW_Requirements.getAIName(), DemoAIs.SAW_Test.getAIName()}, new Integer[] {1},
            DefaultTeamState.Implement, false, false, false));
      actionDatas.add(new ActionData(new String[] {"SAW Even More Requirement (no-branch) Changes for"},
            "Diagram View", PriorityType.Priority_3,
            new String[] {DemoAIs.SAW_Code.getAIName(), DemoAIs.SAW_SW_Design.getAIName(),
                  DemoAIs.SAW_Requirements.getAIName(), DemoAIs.SAW_Test.getAIName()}, new Integer[] {1},
            DefaultTeamState.Implement, false, false, false));
      return actionDatas;
   }

   private Set<ActionData> getNonReqSawActionData() {
      Set<ActionData> actionDatas = new HashSet<ActionData>();
      actionDatas.add(new ActionData(new String[] {"Workaround for"}, "Graph View", PriorityType.Priority_1,
            new String[] {DemoAIs.Adapter.getAIName(), DemoAIs.SAW_Code.getAIName()}, new Integer[] {1},
            DefaultTeamState.Implement, false, false, false));
      actionDatas.add(new ActionData(new String[] {"Working with"}, "Diagram Tree", PriorityType.Priority_3,
            new String[] {DemoAIs.SAW_Code.getAIName(), DemoAIs.SAW_SW_Design.getAIName(),
                  DemoAIs.SAW_Requirements.getAIName(), DemoAIs.SAW_Test.getAIName()}, new Integer[] {0, 2},
            DefaultTeamState.Endorse, false, false, false));
      return actionDatas;
   }

   private Set<ActionData> getGenericActionData() {
      Set<ActionData> actionDatas = new HashSet<ActionData>();
      actionDatas.add(new ActionData(new String[] {"Problem with the", "Can't see the"}, "Graph View",
            PriorityType.Priority_1, new String[] {DemoAIs.Adapter.getAIName(), DemoAIs.CIS_Code.getAIName()},
            new Integer[] {1}, DefaultTeamState.Implement, false, false, false));
      actionDatas.add(new ActionData(new String[] {"Problem in", "Can't load"}, "Diagram Tree",
            PriorityType.Priority_3, new String[] {DemoAIs.CIS_Code.getAIName(), DemoAIs.CIS_SW_Design.getAIName(),
                  DemoAIs.CIS_Requirements.getAIName(), DemoAIs.CIS_Test.getAIName()}, new Integer[] {0, 2},
            DefaultTeamState.Endorse, false, false, false));
      actionDatas.add(new ActionData(new String[] {"Button W doesn't work on"}, "Situation Page",
            PriorityType.Priority_3, new String[] {DemoAIs.CIS_Code.getAIName(), DemoAIs.CIS_SW_Design.getAIName(),
                  DemoAIs.CIS_Requirements.getAIName(), DemoAIs.CIS_Test.getAIName()}, new Integer[] {0, 2},
            DefaultTeamState.Analyze, false, false, false));
      actionDatas.add(new ActionData(new String[] {"Problem with the"}, "user window", PriorityType.Priority_4,
            new String[] {DemoAIs.Timesheet.getAIName()}, new Integer[] {1}, DefaultTeamState.Implement, false, false,
            false));
      actionDatas.add(new ActionData(new String[] {"Button S doesn't work on"}, "help", PriorityType.Priority_3,
            new String[] {DemoAIs.Reader.getAIName()}, new Integer[] {1}, DefaultTeamState.Completed, false, false,
            false));
      return actionDatas;
   }

   public class ActionData {
      public final String postFixTitle;
      public final PriorityType priority;
      public final String[] actionableItems;
      public final boolean createTasks;
      public final boolean decisionReview;
      public final boolean peerReview;
      public final DefaultTeamState toState;
      public final Integer[] userCommunityIndecies;
      public String[] configuredUserCommunities;
      private final String[] prefixTitles;

      public ActionData(String[] prefixTitles, String postFixTitle, PriorityType priority, String[] actionableItems, Integer[] userCommunityIndecies, DefaultTeamState toState, boolean createTasks, boolean decisionReview, boolean peerReview) {
         this.prefixTitles = prefixTitles;
         this.postFixTitle = postFixTitle;
         this.priority = priority;
         this.actionableItems = actionableItems;
         this.userCommunityIndecies = userCommunityIndecies;
         this.toState = toState;
         this.createTasks = createTasks;
         this.decisionReview = decisionReview;
         this.peerReview = peerReview;
      }

      public Set<String> getUserCommunities() {
         if (configuredUserCommunities == null) {
            configuredUserCommunities =
                  UserCommunity.getInstance().getUserCommunityNames().toArray(
                        new String[UserCommunity.getInstance().getUserCommunityNames().size()]);
         }
         Set<String> userComms = new HashSet<String>();
         for (Integer index : userCommunityIndecies)
            userComms.add(configuredUserCommunities[index]);
         return userComms;
      }

      public Collection<ActionableItemArtifact> getActionableItems() throws SQLException {
         Set<ActionableItemArtifact> aias = new HashSet<ActionableItemArtifact>();
         for (String str : actionableItems) {
            for (ActionableItemArtifact aia : ActionableItemArtifact.getActionableItems()) {
               if (str.equals(aia.getDescriptiveName())) aias.add(aia);
            }
         }
         return aias;
      }
   }

   private enum DemoAIs {
      Computers,
      Network,
      Config_Mgmt,
      Reviews,
      Timesheet,
      Website,
      Reader,
      CIS_Code,
      CIS_Test,
      CIS_Requirements,
      CIS_SW_Design,
      SAW_Code,
      SAW_Test,
      SAW_Requirements,
      SAW_SW_Design,
      Adapter;

      public String getAIName() {
         return name().replaceAll("_", " ");
      }
   }

}
