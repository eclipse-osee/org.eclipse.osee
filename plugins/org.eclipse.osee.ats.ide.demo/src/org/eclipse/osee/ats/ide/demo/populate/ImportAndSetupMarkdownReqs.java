/*********************************************************************
 * Copyright (c) 2024 Boeing
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

import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_PL;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;
import org.eclipse.osee.ats.api.demo.DemoArtifactToken;
import org.eclipse.osee.ats.core.demo.DemoUtil;
import org.eclipse.osee.ats.ide.demo.internal.Activator;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.RelationTypeSide;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.operation.IOperation;
import org.eclipse.osee.framework.core.operation.Operations;
import org.eclipse.osee.framework.core.util.OseeInf;
import org.eclipse.osee.framework.jdk.core.util.Collections;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.framework.logging.OseeLog;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.artifact.ArtifactTypeManager;
import org.eclipse.osee.framework.skynet.core.artifact.BranchManager;
import org.eclipse.osee.framework.skynet.core.artifact.search.ArtifactQuery;
import org.eclipse.osee.framework.skynet.core.importing.parsers.IArtifactExtractor;
import org.eclipse.osee.framework.skynet.core.importing.parsers.MarkdownOutlineExtractor;
import org.eclipse.osee.framework.skynet.core.importing.resolvers.IArtifactImportResolver;
import org.eclipse.osee.framework.skynet.core.transaction.SkynetTransaction;
import org.eclipse.osee.framework.skynet.core.transaction.TransactionManager;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationFactory;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactImportOperationParameter;
import org.eclipse.osee.framework.ui.skynet.Import.ArtifactResolverFactory;

/**
 * Utility class to import and setup Markdown requirements on SAW Product Line branch. Used by
 * {@link Pdd10SetupAndImportReqs}.
 *
 * @author Jaden W. Puckett
 */
public class ImportAndSetupMarkdownReqs implements IPopulateDemoDatabase {

   @Override
   public void run() {
      importMarkdownRequirements();
      createAttributesForMarkdownRequirements();
      createRelationsForMarkdownRequirements();
      createWorkingBranch();
   }

   /*
    * Setup
    */

   private void createAttributesForMarkdownRequirements() {
      // Create transaction
      SkynetTransaction createRelationsForMarkdownRequirementsTransaction =
         TransactionManager.createTransaction(SAW_PL, "Create Requirements Trace Relations For Markdown Requirements");

      // Get heading, system, subsystem, and software requirements
      Collection<Artifact> headings = Collections.castAll(
         DemoUtil.getArtifactsFromType(debug, CoreArtifactTypes.HeadingMarkdown, DemoBranches.SAW_PL));
      Collection<Artifact> systemMarkdownArts = Collections.castAll(
         DemoUtil.getArtifactsFromType(debug, CoreArtifactTypes.SystemRequirementMarkdown, DemoBranches.SAW_PL));
      Collection<Artifact> subsystemMarkdownArts = Collections.castAll(
         DemoUtil.getArtifactsFromType(debug, CoreArtifactTypes.SubsystemRequirementMarkdown, DemoBranches.SAW_PL));
      Collection<Artifact> softwareMarkdownArts = Collections.castAll(
         DemoUtil.getArtifactsFromType(debug, CoreArtifactTypes.SoftwareRequirementMarkdown, DemoBranches.SAW_PL));
      Collection<Artifact> imageArts =
         Collections.castAll(DemoUtil.getArtifactsFromType(debug, CoreArtifactTypes.Image, DemoBranches.SAW_PL));

      // Add attributes
      for (Artifact heading : headings) {
         setStringAttribute(heading, CoreAttributeTypes.Description, "Heading for Robot API");
         setStringAttribute(heading, CoreAttributeTypes.DataRightsClassification, "Proprietary");
         heading.persist(createRelationsForMarkdownRequirementsTransaction);
      }
      for (Artifact sysMdArt : systemMarkdownArts) {
         setStringAttribute(sysMdArt, CoreAttributeTypes.Description, "System requirement for Robot API");
         setStringAttribute(sysMdArt, CoreAttributeTypes.DataRightsClassification, "Proprietary");
         sysMdArt.persist(createRelationsForMarkdownRequirementsTransaction);
      }
      for (Artifact subsysMdArt : subsystemMarkdownArts) {
         setStringAttribute(subsysMdArt, CoreAttributeTypes.Description, "Subsystem requirement for Robot API");
         subsysMdArt.persist(createRelationsForMarkdownRequirementsTransaction);
      }
      for (Artifact sofMdArt : softwareMarkdownArts) {
         setStringAttribute(sofMdArt, CoreAttributeTypes.Description, "Software requirement for Robot API");
         setStringAttribute(sofMdArt, CoreAttributeTypes.DataRightsClassification, "Restricted Rights");
         sofMdArt.persist(createRelationsForMarkdownRequirementsTransaction);
      }
      for (Artifact imageArt : imageArts) {
         setStringAttribute(imageArt, CoreAttributeTypes.Description, "Image for Robot API");
         setStringAttribute(imageArt, CoreAttributeTypes.DataRightsClassification, "Proprietary");
         imageArt.persist(createRelationsForMarkdownRequirementsTransaction);
      }

      // Set Data Rights For System Requirements Folder MD
      Artifact systemRequirementsFolderMdArt =
         ArtifactQuery.getArtifactFromId(CoreArtifactTokens.SystemRequirementsFolderMarkdown.getId(), SAW_PL);
      setStringAttribute(systemRequirementsFolderMdArt, CoreAttributeTypes.DataRightsClassification, "Proprietary");
      systemRequirementsFolderMdArt.persist(createRelationsForMarkdownRequirementsTransaction);

      // Set different Data Rights For System Requirements robotCamVisArt
      Artifact robotCamVisArt =
         ArtifactQuery.getArtifactFromId(DemoArtifactToken.RobotCameraVisualization.getId(), SAW_PL);
      setStringAttribute(robotCamVisArt, CoreAttributeTypes.DataRightsClassification, "Restricted Rights");
      robotCamVisArt.persist(createRelationsForMarkdownRequirementsTransaction);

      // Execute transaction
      createRelationsForMarkdownRequirementsTransaction.execute();
   }

   private void setStringAttribute(Artifact artifact, AttributeTypeToken attribute, String value) {
      artifact.setSoleAttributeFromString(attribute, value);
   }

   private void createWorkingBranch() {
      BranchManager.createWorkingBranchFromBranchToken(DemoBranches.SAW_PL,
         DemoBranches.SAW_PL_Working_Branch_Markdown);
   }

   private void createRelationsForMarkdownRequirements() {
      // Create transaction
      SkynetTransaction createRelationsForMarkdownRequirementsTransaction =
         TransactionManager.createTransaction(SAW_PL, "Create Requirements Trace Relations For Markdown Requirements");

      // Get system, subsystem, and software requirements
      Collection<Artifact> systemMarkdownArts = Collections.castAll(
         DemoUtil.getArtifactsFromType(debug, CoreArtifactTypes.SystemRequirementMarkdown, DemoBranches.SAW_PL));
      Collection<Artifact> subsystemMarkdownArts = Collections.castAll(
         DemoUtil.getArtifactsFromType(debug, CoreArtifactTypes.SubsystemRequirementMarkdown, DemoBranches.SAW_PL));
      Collection<Artifact> softwareMarkdownArts = Collections.castAll(
         DemoUtil.getArtifactsFromType(debug, CoreArtifactTypes.SoftwareRequirementMarkdown, DemoBranches.SAW_PL));

      // Sort subsystem and software requirements alphabetically
      List<Artifact> sortedSubsystemMarkdownArts =
         subsystemMarkdownArts.stream().sorted(Comparator.comparing(Artifact::getName)).collect(Collectors.toList());

      List<Artifact> sortedSoftwareMarkdownArts =
         softwareMarkdownArts.stream().sorted(Comparator.comparing(Artifact::getName)).collect(Collectors.toList());

      // Local indices to track current position in each list
      int subsystemCurrentIndex = 0;
      int softwareCurrentIndex = 0;

      /*
       * Create requirement trace relations with rotating sets of 3 artifacts
       */

      // Relate system requirements to subsystem requirements
      for (Artifact sysMdArt : systemMarkdownArts) {
         List<Artifact> subsystemBatch =
            Collections.getNextBatch(sortedSubsystemMarkdownArts, 3, subsystemCurrentIndex);
         subsystemCurrentIndex = (subsystemCurrentIndex + 3) % sortedSubsystemMarkdownArts.size(); // Update index
         relate(CoreRelationTypes.RequirementTrace_LowerLevelRequirement, sysMdArt, subsystemBatch);
         sysMdArt.persist(createRelationsForMarkdownRequirementsTransaction);
      }

      // Relate subsystem requirements to software requirements
      for (Artifact subSysMdArt : subsystemMarkdownArts) {
         List<Artifact> softwareBatch = Collections.getNextBatch(sortedSoftwareMarkdownArts, 3, softwareCurrentIndex);
         softwareCurrentIndex = (softwareCurrentIndex + 3) % sortedSoftwareMarkdownArts.size(); // Update index
         relate(CoreRelationTypes.RequirementTrace_LowerLevelRequirement, subSysMdArt, softwareBatch);
         subSysMdArt.persist(createRelationsForMarkdownRequirementsTransaction);
      }

      // Execute transaction
      createRelationsForMarkdownRequirementsTransaction.execute();
   }

   private void relate(RelationTypeSide relationSide, Artifact artifact, Collection<Artifact> artifacts) {
      for (Artifact otherArtifact : artifacts) {
         artifact.addRelation(relationSide, otherArtifact);
      }
   }

   /*
    * Import
    */

   private void importMarkdownRequirements() {
      importMarkdownRequirementsFile(SAW_PL, CoreArtifactTypes.SystemRequirementMarkdown,
         CoreArtifactTokens.SystemRequirementsFolderMarkdown,
         OseeInf.getResourceAsFile("requirements/SAW-SystemRequirements.md", getClass()));
      importMarkdownRequirementsFile(SAW_PL, CoreArtifactTypes.SubsystemRequirementMarkdown,
         CoreArtifactTokens.SubSystemRequirementsFolderMarkdown,
         OseeInf.getResourceAsFile("requirements/SAW-SubsystemRequirements.md", getClass()));
      importMarkdownRequirementsFile(SAW_PL, CoreArtifactTypes.SoftwareRequirementMarkdown,
         CoreArtifactTokens.SoftwareRequirementsFolderMarkdown,
         OseeInf.getResourceAsFile("requirements/SAW-SoftwareRequirements.md", getClass()));
      importMarkdownRequirementsImages(SAW_PL, CoreArtifactTokens.SystemRequirementsFolderMarkdown);

      SkynetTransaction transaction = TransactionManager.createTransaction(SAW_PL,
         "Populate Demo DB - Create Markdown Artifacts Targeted By Artifact Links");

      // Robot Camera Visualization

      Artifact uiVisArt = ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.HeadingMarkdown,
         "User Interface PR (Visualization)", SAW_PL);

      Artifact robotCamVisArt = ArtifactTypeManager.addArtifact(DemoArtifactToken.RobotCameraVisualization, SAW_PL);
      String mdContent =
         "The user interface shall display real-time video feed from the robot camera with a minimum resolution of 1080p," //
            + " ensuring that users can clearly view the environment in which the robot operates.";
      robotCamVisArt.setSoleAttributeValue(CoreAttributeTypes.MarkdownContent, mdContent);
      uiVisArt.addChild(robotCamVisArt);
      transaction.addArtifact(robotCamVisArt);

      // Virtual Fixtures

      Artifact virtFixtHeadingArt =
         ArtifactQuery.getArtifactFromTypeAndName(CoreArtifactTypes.HeadingMarkdown, "Virtual Fixtures", SAW_PL);

      Artifact virtualFixturesArt = ArtifactTypeManager.addArtifact(DemoArtifactToken.VirtualFixtures, SAW_PL);
      mdContent =
         "The system shall implement virtual fixtures that utilize constrained optimization techniques to enhance the precision " //
            + "and safety of surgical procedures, as outlined in the works of Kapoor et al. (2006) and Li et al. (2005), ensuring that " //
            + "spatial motion constraints are effectively generated based on anatomical features to assist surgical tasks, as demonstrated " //
            + "in the research by Li and Taylor (2004, 2005).";
      virtualFixturesArt.setSoleAttributeValue(CoreAttributeTypes.MarkdownContent, mdContent);
      virtFixtHeadingArt.addChild(virtualFixturesArt);
      transaction.addArtifact(virtualFixturesArt);

      transaction.execute();
   }

   private void importMarkdownRequirementsFile(BranchId branch, ArtifactTypeToken requirementType,
      ArtifactToken folderTok, File file) {
      Artifact systemReqMd = ArtifactQuery.getArtifactFromId(folderTok, branch);

      IArtifactImportResolver artifactResolver =
         ArtifactResolverFactory.createAlwaysNewArtifacts(ArtifactTypeToken.SENTINEL);
      IArtifactExtractor extractor = new MarkdownOutlineExtractor(CoreArtifactTypes.HeadingMarkdown, requirementType);

      ArtifactImportOperationParameter importOptions = new ArtifactImportOperationParameter();
      importOptions.setSourceFile(file);
      importOptions.setDestinationArtifact(systemReqMd);
      importOptions.setExtractor(extractor);
      importOptions.setResolver(artifactResolver);

      IOperation operation = ArtifactImportOperationFactory.completeOperation(importOptions);
      Operations.executeWorkAndCheckStatus(operation);

      // Validate that something was imported
      if (systemReqMd.getChildren().isEmpty()) {
         throw new IllegalStateException("Artifacts were not imported");
      }
   }

   private void importMarkdownRequirementsImages(BranchToken branch, ArtifactToken parentFolderTok) {

      SkynetTransaction transaction = TransactionManager.createTransaction(branch,
         "Populate Demo DB - Create Markdown Requirement Image Artifact(s)");

      Artifact parentFolderArt = ArtifactQuery.getArtifactFromTypeAndName(parentFolderTok.getArtifactType(),
         "System Requirements - Markdown", branch);

      // SAWTSR Image
      File sawtsrFile = OseeInf.getResourceAsFile("requirements/SAWTSR.png", getClass());
      Artifact sawtsrArt = ArtifactTypeManager.addArtifact(DemoArtifactToken.SAWTSR_Image_Markdown, branch);
      sawtsrArt.setSoleAttributeValue(CoreAttributeTypes.Extension, "png");
      // Set the native content attribute of image artifact
      URI source = sawtsrFile.toURI();
      try {
         InputStream inputStream = source.toURL().openStream();
         sawtsrArt.setSoleAttributeValue(CoreAttributeTypes.NativeContent, inputStream);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
      }

      // Robot Data Flow Image
      File robotDataFlowFile = OseeInf.getResourceAsFile("requirements/RobotDataFlow.png", getClass());
      Artifact robotDataFlowArt =
         ArtifactTypeManager.addArtifact(DemoArtifactToken.Robot_Data_Flow_Image_Markdown, branch);
      robotDataFlowArt.setSoleAttributeValue(CoreAttributeTypes.Extension, "png");
      // Set the native content attribute of image artifact
      source = robotDataFlowFile.toURI();
      try {
         InputStream inputStream = source.toURL().openStream();
         robotDataFlowArt.setSoleAttributeValue(CoreAttributeTypes.NativeContent, inputStream);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
      }

      // C Image
      File cFile = OseeInf.getResourceAsFile("requirements/c.jpg", getClass());
      Artifact cArt = ArtifactTypeManager.addArtifact(DemoArtifactToken.C_Image_Markdown, branch);
      cArt.setSoleAttributeValue(CoreAttributeTypes.Extension, "jpg");
      // Set the native content attribute of image artifact
      source = cFile.toURI();
      try {
         InputStream inputStream = source.toURL().openStream();
         cArt.setSoleAttributeValue(CoreAttributeTypes.NativeContent, inputStream);
      } catch (Exception ex) {
         OseeLog.log(Activator.class, Level.SEVERE, Lib.exceptionToString(ex));
      }

      // Add the image artifacts to the parent folder
      parentFolderArt.addChild(sawtsrArt);
      parentFolderArt.addChild(robotDataFlowArt);
      parentFolderArt.addChild(cArt);
      transaction.addArtifact(sawtsrArt);
      transaction.addArtifact(robotDataFlowArt);
      transaction.addArtifact(cArt);

      transaction.execute();
   }

}
