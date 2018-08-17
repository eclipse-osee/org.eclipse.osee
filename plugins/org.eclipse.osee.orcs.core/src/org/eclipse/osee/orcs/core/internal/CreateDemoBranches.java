/*******************************************************************************
 * Copyright (c) 2018 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.core.internal;

import static org.eclipse.osee.framework.core.data.ApplicabilityToken.BASE;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.DefaultHierarchyRoot;
import static org.eclipse.osee.framework.core.enums.CoreArtifactTokens.ProductLineFolder;
import static org.eclipse.osee.framework.core.enums.CoreBranches.COMMON;
import static org.eclipse.osee.framework.core.enums.DemoBranches.CIS_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_Bld_1;
import static org.eclipse.osee.framework.core.enums.DemoBranches.SAW_PL;
import static org.eclipse.osee.framework.core.enums.SystemUser.OseeSystem;
import java.util.Arrays;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.IOseeBranch;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.CoreBranches;
import org.eclipse.osee.framework.core.enums.CoreTupleTypes;
import org.eclipse.osee.framework.core.enums.DemoBranches;
import org.eclipse.osee.framework.core.enums.DemoSubsystems;
import org.eclipse.osee.framework.core.enums.DemoUsers;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.OrcsBranch;
import org.eclipse.osee.orcs.core.internal.applicability.DemoFeatures;
import org.eclipse.osee.orcs.core.util.Artifacts;
import org.eclipse.osee.orcs.search.QueryBuilder;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;
import org.eclipse.osee.orcs.transaction.TransactionFactory;

/**
 * @author Ryan D. Brooks
 * @author Donald G. Dunne
 */
public class CreateDemoBranches {
   private final OrcsApi orcsApi;
   private final TransactionFactory txFactory;
   private final QueryBuilder query;
   private final OrcsApplicability ops;
   private final OrcsBranch branchOps;

   public CreateDemoBranches(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
      txFactory = orcsApi.getTransactionFactory();
      query = orcsApi.getQueryFactory().fromBranch(CoreBranches.COMMON);
      ops = orcsApi.getApplicabilityOps();
      branchOps = orcsApi.getBranchOps();
   }

   public void populate() {
      UserId account = DemoUsers.Joe_Smith;
      TransactionBuilder tx = txFactory.createTransaction(COMMON, account, "Create Demo Users");
      orcsApi.getAdminOps().createUsers(tx, DemoUsers.values(), query);
      tx.commit();

      createDemoProgramBranch(SAW_Bld_1, account);

      createDemoProgramBranch(CIS_Bld_1, account);

      branchOps.createBaselineBranch(DemoBranches.SAW_PL, DemoUsers.Joe_Smith, SAW_Bld_1, OseeSystem);
      branchOps.createBaselineBranch(DemoBranches.SAW_PL_Hardening_Branch, DemoUsers.Joe_Smith, SAW_PL, OseeSystem);

      createProductLineConfig(DemoBranches.SAW_PL, account);
   }

   private void configureProducts(TransactionBuilder tx, ArtifactToken[] products) {
      for (int i = 0; i < products.length; i++) {
         tx.addTuple2(CoreTupleTypes.ViewApplicability, products[i], "Config = " + products[i].getName());
         tx.addTuple2(CoreTupleTypes.ViewApplicability, products[i], BASE.getName());
      }
   }

   private void createProductLineConfig(BranchId branch, UserId account) {

      TransactionBuilder tx = txFactory.createTransaction(branch, OseeSystem, "Create Product Line folders");

      ArtifactToken plFolder = Artifacts.getOrCreate(ProductLineFolder, DefaultHierarchyRoot, tx, orcsApi);
      Artifacts.getOrCreate(CoreArtifactTokens.VariantsFolder, plFolder, tx, orcsApi);
      ArtifactToken featuresFolder = Artifacts.getOrCreate(CoreArtifactTokens.FeaturesFolder, plFolder, tx, orcsApi);

      ArtifactToken productA = tx.createView(branch, "Product A");
      ArtifactToken productB = tx.createView(branch, "Product B");
      ArtifactToken productC = tx.createView(branch, "Product C");
      ArtifactToken productD = tx.createView(branch, "Product D");
      ArtifactToken[] products = new ArtifactToken[] {productA, productB, productC, productD};

      configureProducts(tx, products);

      createFeatureConfigs(featuresFolder, tx);

      // Configure productions for each feature
      configureFeature(tx, DemoFeatures.ROBOT_ARM_LIGHT.name(), products, "Excluded", "Included", "Excluded",
         "Excluded");
      configureFeature(tx, DemoFeatures.ENGINE_5.name(), products, "25-43A", "25-43A", "25-43A", "55-43A");
      configureFeature(tx, DemoFeatures.JHU_CONTROLLER.name(), products, "Excluded", "Included", "Included",
         "Excluded");
      configureFeature(tx, DemoFeatures.ROBOT_SPEAKER.name(), products, "SPKR A", "SPKR A", "SPKR B", "SPKR B");

      createLegacyFeatureConfig(featuresFolder, tx);

      tx.commit();
   }

   private void createFeatureConfigs(ArtifactId folder, TransactionBuilder tx) {
      XResultData results = new XResultData();
      FeatureDefinition def1 = new FeatureDefinition(Lib.generateArtifactIdAsInt(), DemoFeatures.ROBOT_ARM_LIGHT.name(),
         "String", Arrays.asList("Included", "Excluded"), "Included", false, "A significant capability");
      ops.createUpdateFeatureDefinition(def1, tx, results);
      FeatureDefinition def2 = new FeatureDefinition(Lib.generateArtifactIdAsInt(), DemoFeatures.ENGINE_5.name(),
         "String", Arrays.asList("A2543", "B5543"), "", false, "Used select type of engine");
      ops.createUpdateFeatureDefinition(def2, tx, results);
      FeatureDefinition def3 = new FeatureDefinition(Lib.generateArtifactIdAsInt(), DemoFeatures.JHU_CONTROLLER.name(),
         "String", Arrays.asList("Included", "Excluded"), "Included", false, "A small point of variation");
      ops.createUpdateFeatureDefinition(def3, tx, results);
      FeatureDefinition def4 = new FeatureDefinition(Lib.generateArtifactIdAsInt(), DemoFeatures.ROBOT_SPEAKER.name(),
         "String", Arrays.asList("SPKR_A", "SPKR_B", "SPKR_C"), "SPKR_A", true, "This feature is multi-select.");
      ops.createUpdateFeatureDefinition(def4, tx, results);
      orcsApi.getApplicabilityOps();
      Conditions.assertFalse(results.isErrors(), results.toString());
   }

   /**
    * TODO: Remove after 26.0 release which converted feature definitions from a single json string
    */
   private void createLegacyFeatureConfig(ArtifactId folder, TransactionBuilder tx) {
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
         "\"defaultValue\": \"Excluded\"," + //
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

   private void configureFeature(TransactionBuilder tx, String featureName, ArtifactId[] products, String... featureValues) {
      for (int i = 0; i < products.length; i++) {
         tx.addTuple2(CoreTupleTypes.ViewApplicability, products[i], featureName + " = " + featureValues[i]);
      }
   }

   private void createDemoProgramBranch(IOseeBranch branch, UserId account) {
      branchOps.createProgramBranch(branch, account);

      TransactionBuilder tx = txFactory.createTransaction(branch, account, "Create SAW Product Decomposition");

      ArtifactId sawProduct =
         tx.createArtifact(DefaultHierarchyRoot, CoreArtifactTypes.Component, "SAW Product Decomposition");

      for (String subsystem : DemoSubsystems.getSubsystems()) {
         tx.createArtifact(sawProduct, CoreArtifactTypes.Component, subsystem);
      }
      tx.commit();
   }
}