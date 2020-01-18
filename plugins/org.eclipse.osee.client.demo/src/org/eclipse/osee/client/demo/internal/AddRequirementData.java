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
package org.eclipse.osee.client.demo.internal;

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_2;
import java.io.File;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import org.eclipse.osee.framework.access.AccessControlManager;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.core.enums.PermissionEnum;
import org.eclipse.osee.framework.core.enums.QueryOption;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.database.init.IDbInitializationTask;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.UserManager;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.WordOutlineExtractorDelegate;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.skynet.core.utility.ConnectionHandler;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationParameter;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;

/**
 * @author Donald G. Dunne
 */
public class AddRequirementData implements IDbInitializationTask {

   private static boolean DEBUG = false;
   private static final String UPDATE_BRANCH_TYPE = "update osee_branch set branch_type = ? where branch_id = ?";

   @Override
   public void run() {
      try {
         BranchId branch = SAW_Bld_1;

         //@formatter:off
         importRequirements(branch, CoreArtifactTypes.SoftwareRequirementMsWord, "Software Requirements", OseeInf.getResourceAsFile("requirements/SAW-SoftwareRequirements.xml", getClass()));
         importRequirements(branch, CoreArtifactTypes.SystemRequirementMsWord, "System Requirements", OseeInf.getResourceAsFile("requirements/SAW-SystemRequirements.xml", getClass()));
         importRequirements(branch, CoreArtifactTypes.SubsystemRequirementMsWord, "Subsystem Requirements", OseeInf.getResourceAsFile("requirements/SAW-SubsystemRequirements.xml", getClass()));
         //@formatter:on

         SkynetTransaction demoDbTraceability =
            TransactionManager.createTransaction(branch, "Populate Demo DB - Create Traceability");
         demoDbTraceabilityTx(demoDbTraceability, branch);
         demoDbTraceability.execute();

         // Create SAW_Bld_2 Child Main Working Branch off SAW_Bld_1
         BranchId childBranch = BranchManager.createBaselineBranch(SAW_Bld_1, SAW_Bld_2);
         AccessControlManager.setPermission(UserManager.getUser(DemoUsers.Joe_Smith), SAW_Bld_2,
            PermissionEnum.FULLACCESS);

         // need to update the branch type;
         ConnectionHandler.runPreparedUpdate(UPDATE_BRANCH_TYPE, BranchType.BASELINE, childBranch);
         BranchManager.refreshBranches();
      } catch (Exception ex) {
         OseeCoreException.wrapAndThrow(ex);
      }
   }

   private void importRequirements(BranchId branch, ArtifactTypeToken requirementType, String folderName, File file) throws Exception {
      if (DEBUG) {
         OseeLog.logf(AddRequirementData.class, Level.INFO, "Importing \"%s\" requirements on branch \"%s\"",
            folderName, branch);
      }
      Artifact systemReq = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, folderName, branch);

      IArtifactImportResolver artifactResolver = ArtifactResolverFactory.createAlwaysNewArtifacts(requirementType);
      IArtifactExtractor extractor = new WordOutlineExtractor();
      extractor.setDelegate(new WordOutlineExtractorDelegate());

      ArtifactImportOperationParameter importOptions = new ArtifactImportOperationParameter();
      importOptions.setSourceFile(file);
      importOptions.setDestinationArtifact(systemReq);
      importOptions.setExtractor(extractor);
      importOptions.setResolver(artifactResolver);

      IOperation operation = ArtifactImportOperationFactory.completeOperation(importOptions);
      Operations.executeWorkAndCheckStatus(operation);

      // Validate that something was imported
      if (systemReq.getChildren().isEmpty()) {
         throw new IllegalStateException("Artifacts were not imported");
      }
   }

   private void relate(RelationTypeSide relationSide, Artifact artifact, Collection<Artifact> artifacts) {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   private void demoDbTraceabilityTx(SkynetTransaction transaction, BranchId branch) throws Exception {
      Collection<Artifact> systemArts =
         getArtTypeRequirements(DEBUG, CoreArtifactTypes.SystemRequirementMsWord, "Robot", branch);

      Collection<Artifact> component = getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "API", branch);
      component.addAll(getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "Hardware", branch));
      component.addAll(getArtTypeRequirements(DEBUG, CoreArtifactTypes.Component, "Sensor", branch));

      Collection<Artifact> subSystemArts =
         getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMsWord, "Robot", branch);
      subSystemArts.addAll(
         getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMsWord, "Video", branch));
      subSystemArts.addAll(
         getArtTypeRequirements(DEBUG, CoreArtifactTypes.SubsystemRequirementMsWord, "Interface", branch));

      Collection<Artifact> softArts =
         getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirementMsWord, "Robot", branch);
      softArts.addAll(getArtTypeRequirements(DEBUG, CoreArtifactTypes.SoftwareRequirementMsWord, "Interface", branch));

      // Relate System to SubSystem to Software Requirements
      for (Artifact systemArt : systemArts) {
         relate(CoreRelationTypes.RequirementTrace_LowerLevelRequirement, systemArt, subSystemArts);
         systemArt.persist(transaction);

         for (Artifact subSystemArt : subSystemArts) {
            relate(CoreRelationTypes.RequirementTrace_LowerLevelRequirement, subSystemArt, softArts);
            subSystemArt.persist(transaction);
         }
      }

      // Relate System, SubSystem and Software Requirements to Componets
      for (Artifact art : systemArts) {
         relate(CoreRelationTypes.Allocation_Component, art, component);
         art.persist(transaction);
      }
      for (Artifact art : subSystemArts) {
         relate(CoreRelationTypes.Allocation_Component, art, component);
         art.persist(transaction);
      }
      for (Artifact art : softArts) {
         relate(CoreRelationTypes.Allocation_Component, art, component);
      }

      // Create Test Script Artifacts
      Set<Artifact> verificationTests = new HashSet<>();
      Artifact verificationHeader =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Verification Tests", branch);
      if (verificationHeader == null) {
         throw new IllegalStateException("Could not find Verification Tests header");
      }
      for (String str : new String[] {"A", "B", "C"}) {
         Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestCase, verificationHeader.getBranch(),
            "Verification Test " + str);
         verificationTests.add(newArt);
         verificationHeader.addRelation(CoreRelationTypes.DefaultHierarchical_Child, newArt);
         newArt.persist(transaction);
      }
      Artifact verificationTestsArray[] = verificationTests.toArray(new Artifact[verificationTests.size()]);

      // Create Validation Test Procedure Artifacts
      Set<Artifact> validationTests = new HashSet<>();
      Artifact validationHeader =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Validation Tests", branch);
      if (validationHeader == null) {
         throw new IllegalStateException("Could not find Validation Tests header");
      }
      for (String str : new String[] {"1", "2", "3"}) {
         Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure,
            validationHeader.getBranch(), "Validation Test " + str);
         validationTests.add(newArt);
         validationHeader.addRelation(CoreRelationTypes.DefaultHierarchical_Child, newArt);
         newArt.persist(transaction);
      }
      Artifact validationTestsArray[] = validationTests.toArray(new Artifact[validationTests.size()]);

      // Create Integration Test Procedure Artifacts
      Set<Artifact> integrationTests = new HashSet<>();
      Artifact integrationHeader =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.Folder, "Integration Tests", branch);
      if (integrationHeader == null) {
         throw new IllegalStateException("Could not find integration Tests header");
      }
      for (String str : new String[] {"X", "Y", "Z"}) {
         Artifact newArt = ArtifactTypeManager.addArtifact(CoreArtifactTypes.TestProcedure,
            integrationHeader.getBranch(), "integration Test " + str);

         integrationTests.add(newArt);
         integrationHeader.addRelation(CoreRelationTypes.DefaultHierarchical_Child, newArt);
         newArt.persist(transaction);
      }
      Artifact integrationTestsArray[] = integrationTests.toArray(new Artifact[integrationTests.size()]);

      // Relate Software Artifacts to Tests
      Artifact softReqsArray[] = softArts.toArray(new Artifact[softArts.size()]);
      softReqsArray[0].addRelation(CoreRelationTypes.Validation_Validator, verificationTestsArray[0]);
      softReqsArray[0].addRelation(CoreRelationTypes.Validation_Validator, verificationTestsArray[1]);
      softReqsArray[1].addRelation(CoreRelationTypes.Validation_Validator, verificationTestsArray[0]);
      softReqsArray[1].addRelation(CoreRelationTypes.Validation_Validator, validationTestsArray[1]);
      softReqsArray[2].addRelation(CoreRelationTypes.Validation_Validator, validationTestsArray[0]);
      softReqsArray[2].addRelation(CoreRelationTypes.Validation_Validator, integrationTestsArray[1]);
      softReqsArray[3].addRelation(CoreRelationTypes.Validation_Validator, integrationTestsArray[0]);
      softReqsArray[4].addRelation(CoreRelationTypes.Validation_Validator, integrationTestsArray[2]);
      softReqsArray[5].addRelation(CoreRelationTypes.Validation_Validator, validationTestsArray[2]);

      for (Artifact artifact : softArts) {
         artifact.persist(transaction);
      }

   }

   private Collection<Artifact> getArtTypeRequirements(boolean DEBUG, ArtifactTypeToken artifactType, String artifactNameStr, BranchId branch) {
      if (DEBUG) {
         OseeLog.logf(AddRequirementData.class, Level.INFO, "Getting [%s] requirement(s) from Branch [%s]",
            artifactNameStr, branch.getIdString());
      }
      Collection<Artifact> arts = ArtifactQuery.getArtifactListFromTypeAndName(artifactType, artifactNameStr, branch,
         QueryOption.CONTAINS_MATCH_OPTIONS);
      if (DEBUG) {
         OseeLog.logf(AddRequirementData.class, Level.INFO, "Found [%s] Artifacts", arts.size());
      }
      return arts;
   }
}
