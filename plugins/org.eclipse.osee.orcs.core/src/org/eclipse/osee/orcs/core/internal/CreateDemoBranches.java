/*********************************************************************
 * Copyright (c) 2018 Boeing
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

package org.eclipse.osee.orcs.core.internal;

import static org.eclipse.osee.framework.core.enums.DemoBranches.CIS_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_PL;
import java.util.Arrays;
import java.util.List;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchToken;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.GammaId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranchCategoryTokens;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoSubsystems;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.core.internal.applicability.DemoFeatures;
import org.eclipse.osee.orcs.core.util.Artifacts;
import org.eclipse.osee.orcs.search.TupleQuery;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class CreateDemoBranches {
   private final OrcsApi orcsApi;
   private final TransactionFactory txFactory;
   private final OrcsBranch branchOps;

   public CreateDemoBranches(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      txFactory = orcsApi.getTransactionFactory();
      branchOps = orcsApi.getBranchOps();
   }

   public void populate() {
      orcsApi.userService().createUsers(DemoUsers.values(), "Create Demo Users");

      createDemoProgramBranch(SAW_Bld_1);
      createDemoProgramBranch(CIS_Bld_1);

      branchOps.createBaselineBranch(DemoBranches.SAW_PL, SAW_Bld_1, ArtifactId.SENTINEL);

      createProductLineConfig(DemoBranches.SAW_PL, orcsApi);

      Branch hardeningBranch =
         branchOps.createBaselineBranch(DemoBranches.SAW_PL_Hardening_Branch, SAW_PL, ArtifactId.SENTINEL);
      orcsApi.getAccessControlService().removePermissions(hardeningBranch);

      branchOps.createWorkingBranch(DemoBranches.SAW_PL_Working_Branch, SAW_PL, ArtifactId.SENTINEL);
      branchOps.setBranchCategory(DemoBranches.SAW_PL_Working_Branch, CoreBranchCategoryTokens.PLE);
   }

   public static void createProductLineConfig(BranchId branch, OrcsApi orcsApi) {

      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, "Create Product Line folders");

      ArtifactToken plFolder = Artifacts.getOrCreate(CoreArtifactTokens.ProductLineFolder,
         CoreArtifactTokens.DefaultHierarchyRoot, tx, orcsApi);
      Artifacts.getOrCreate(CoreArtifactTokens.ProductsFolder, plFolder, tx, orcsApi);
      ArtifactToken featuresFolder = Artifacts.getOrCreate(CoreArtifactTokens.FeaturesFolder, plFolder, tx, orcsApi);
      Artifacts.getOrCreate(CoreArtifactTokens.PlCfgGroupsFolder, plFolder, tx, orcsApi);

      ArtifactToken productA = tx.createView(branch, "Product A");
      ArtifactToken productB = tx.createView(branch, "Product B");
      ArtifactToken productC = tx.createView(branch, "Product C");
      ArtifactToken productD = tx.createView(branch, "Product D");

      ArtifactToken[] products = new ArtifactToken[] {productA, productB, productC, productD};

      createFeatureConfigs(featuresFolder, tx, orcsApi);

      // Configure productions for each feature
      configureFeature(tx, DemoFeatures.ROBOT_ARM_LIGHT.name(), products, "Excluded", "Included", "Excluded",
         "Excluded");
      configureFeature(tx, DemoFeatures.ENGINE_5.name(), products, "A2543", "A2543", "A2543", "B5543");
      configureFeature(tx, DemoFeatures.JHU_CONTROLLER.name(), products, "Excluded", "Included", "Included",
         "Excluded");
      configureFeature(tx, DemoFeatures.ROBOT_SPEAKER.name(), products, "SPKR_A", "SPKR_A", "SPKR_B", "SPKR_B");

      createLegacyFeatureConfig(featuresFolder, tx);

      tx.createBranchCategory(branch, CoreBranchCategoryTokens.PLE);
      tx.createBranchCategory(branch, CoreBranchCategoryTokens.ATS);

      /*
       * Depending on the order in which the branches are created, Base may have already been created. If so it would be
       * an introduce on another branch
       **/

      TupleQuery query = orcsApi.getQueryFactory().tupleQuery();
      GammaId gamma = GammaId.SENTINEL;
      if (!(gamma = query.getTuple2GammaFromE1E2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0),
         "Base")).isValid()) {
         tx.addTuple2(CoreTupleTypes.ApplicabilityDefinition, ArtifactId.valueOf(0), "Base");
      } else {
         tx.introduceTuple(CoreTupleTypes.ApplicabilityDefinition, gamma);
      }
      tx.commit();
      ConfigurationGroupDefinition group = new ConfigurationGroupDefinition();
      group.setName("abGroup");
      orcsApi.getApplicabilityOps().createCfgGroup(group, branch);
      orcsApi.getApplicabilityOps().relateCfgGroupToView("abGroup", "Product A", branch);
      orcsApi.getApplicabilityOps().relateCfgGroupToView("abGroup", "Product B", branch);
      orcsApi.getApplicabilityOps().syncConfigGroup(branch);

   }

   private static void createFeatureConfigs(ArtifactId folder, TransactionBuilder tx, OrcsApi orcsApi) {
      XResultData results = new XResultData();

      List<String> def1Values = Arrays.asList("Included", "Excluded");
      FeatureDefinition def1 = new FeatureDefinition(DemoFeatures.ROBOT_ARM_LIGHT.name(), "String", def1Values,
         "Included", false, "A significant capability", Arrays.asList("Test"));
      ArtifactToken def1Tok = orcsApi.getApplicabilityOps().createFeatureDefinition(def1, tx, results);
      configureApplicabilityValues(tx, def1Tok, DemoFeatures.ROBOT_ARM_LIGHT.name(), def1Values);

      List<String> def2Values = Arrays.asList("A2543", "B5543");
      FeatureDefinition def2 = new FeatureDefinition(DemoFeatures.ENGINE_5.name(), "String", def2Values, "A2543", false,
         "Used select type of engine", Arrays.asList("Test"));
      ArtifactToken def2Tok = orcsApi.getApplicabilityOps().createFeatureDefinition(def2, tx, results);
      configureApplicabilityValues(tx, def2Tok, DemoFeatures.ENGINE_5.name(), def2Values);

      List<String> def3Values = Arrays.asList("Included", "Excluded");
      FeatureDefinition def3 = new FeatureDefinition(DemoFeatures.JHU_CONTROLLER.name(), "String", def3Values,
         "Included", false, "A small point of variation", null);
      ArtifactToken def3Tok = orcsApi.getApplicabilityOps().createFeatureDefinition(def3, tx, results);
      configureApplicabilityValues(tx, def3Tok, DemoFeatures.JHU_CONTROLLER.name(), def3Values);

      List<String> def4Values = Arrays.asList("SPKR_A", "SPKR_B", "SPKR_C");
      FeatureDefinition def4 = new FeatureDefinition(DemoFeatures.ROBOT_SPEAKER.name(), "String", def4Values, "SPKR_A",
         true, "This feature is multi-select.", null);
      ArtifactToken def4Tok = orcsApi.getApplicabilityOps().createFeatureDefinition(def4, tx, results);
      configureApplicabilityValues(tx, def4Tok, DemoFeatures.ROBOT_SPEAKER.name(), def4Values);

      orcsApi.getApplicabilityOps();
      Conditions.assertFalse(results.isErrors(), results.toString());
   }

   /**
    * TODO: Remove after 26.0 release which converted feature definitions from a single json string
    */
   private static void createLegacyFeatureConfig(ArtifactId folder, TransactionBuilder tx) {
      ArtifactId featureDefinition =
         tx.createArtifact(folder, CoreArtifactTypes.FeatureDefinition, "Feature Definition_SAW_Bld_1");
      String featureDefJson = "[{" + "\"name\": \"" + DemoFeatures.ROBOT_ARM_LIGHT.name() + "\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"Test It\"" + //
         "}, {" + //
         "\"name\": \"" + DemoFeatures.ENGINE_5.name() + "\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"A2543\", \"B5543\"]," + //
         "\"defaultValue\": \"A2543\"," + //
         "\"description\": \"Test It\"" + //
         "},{" + //
         "\"name\": \"" + DemoFeatures.JHU_CONTROLLER.name() + "\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Excluded\"," + //
         "\"description\": \"Test It\"" + //
         "},{" + //
         "\"name\": \"" + DemoFeatures.ROBOT_SPEAKER.name() + "\"," + //
         "\"type\": \"multiple\"," + //
         "\"values\": [\"SPKR_A\", \"SPKR_B\", \"SPKR_C\"]," + //
         "\"defaultValue\": \"SPKR_A\"," + //
         "\"description\": \"Test It\"" + //
         "}" + //
         "]";

      tx.createAttribute(featureDefinition, CoreAttributeTypes.GeneralStringData, featureDefJson);
   }

   private static void configureFeature(TransactionBuilder tx, String featureName, ArtifactId[] products, String... featureValues) {
      for (int i = 0; i < products.length; i++) {
         tx.addTuple2(CoreTupleTypes.ViewApplicability, products[i], featureName + " = " + featureValues[i]);
      }
   }

   private static void configureApplicabilityValues(TransactionBuilder tx, ArtifactId featureId, String featureName, List<String> featureValues) {
      for (String value : featureValues) {
         tx.addTuple2(CoreTupleTypes.ApplicabilityDefinition, featureId, featureName + " = " + value);
      }
   }

   private void createDemoProgramBranch(BranchToken branch) {
      branchOps.createProgramBranch(branch);

      TransactionBuilder tx = txFactory.createTransaction(branch, "Create SAW Product Decomposition");

      ArtifactId sawProduct = tx.createArtifact(CoreArtifactTokens.DefaultHierarchyRoot, CoreArtifactTypes.Component,
         CoreArtifactTokens.SAW_PRODUCT_DECOMP);

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         tx.createArtifact(sawProduct, CoreArtifactTypes.Component, subsystem);
      }
      tx.commit();
   }
}