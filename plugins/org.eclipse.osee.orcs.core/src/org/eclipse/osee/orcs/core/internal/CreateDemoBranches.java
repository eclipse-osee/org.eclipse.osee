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
import org.eclipse.osee.framework.jdk.core.util.Lib;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.OrcsBranch;
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
      Artifacts.getOrCreate(CoreArtifactTokens.ProductsFolder, plFolder, tx, orcsApi);
      ArtifactToken featuresFolder = Artifacts.getOrCreate(CoreArtifactTokens.FeaturesFolder, plFolder, tx, orcsApi);

      ArtifactToken productA = tx.createView(branch, "Product A");
      ArtifactToken productB = tx.createView(branch, "Product B");
      ArtifactToken productC = tx.createView(branch, "Product C");
      ArtifactToken productD = tx.createView(branch, "Product D");
      ArtifactToken[] products = new ArtifactToken[] {productA, productB, productC, productD};

      configureProducts(tx, products);

      createFeatureConfigs(featuresFolder, tx);

      configureFeature(tx, "Feature 1", products, "Excluded", "Included", "Excluded", "Mod A");
      configureFeature(tx, "Unit Type", products, "Metric", "Metric", "Metric", "Mod B");
      configureFeature(tx, "Feature 3", products, "Excluded", "Included", "Included", "Mod A");
      configureFeature(tx, "Feature 4", products, "Included", "Included", "Included", "Mod A");

      createLegacyFeatureConfig(featuresFolder, tx);

      tx.commit();
   }

   private void configureFeature(TransactionBuilder tx, String featureName, ArtifactId[] products, String... featureValues) {
      for (int i = 0; i < products.length; i++) {
         tx.addTuple2(CoreTupleTypes.ViewApplicability, products[i], featureName + " = " + featureValues[i]);
      }
   }

   private void createFeatureConfigs(ArtifactId folder, TransactionBuilder tx) {
      FeatureDefinition def1 = new FeatureDefinition(Lib.generateArtifactIdAsInt(), "Feature 1", "String",
         Arrays.asList("Included", "Excluded"), "Included", false, "A significant capability");
      ops.storeFeatureDefinition(def1, tx);
      FeatureDefinition def2 = new FeatureDefinition(Lib.generateArtifactIdAsInt(), "Unit Type", "String",
         Arrays.asList("Metrics", "English"), "", false, "Used select type of units");
      ops.storeFeatureDefinition(def2, tx);
      FeatureDefinition def3 = new FeatureDefinition(Lib.generateArtifactIdAsInt(), "Feature 3", "String",
         Arrays.asList("Included", "Excluded"), "Included", false, "A small point of variation");
      ops.storeFeatureDefinition(def3, tx);
      FeatureDefinition def4 = new FeatureDefinition(Lib.generateArtifactIdAsInt(), "Feature 4", "String",
         Arrays.asList("Mod A", "Mod B", "Mod C"), "Mod A", true, "This feature depends of Feature 1");
      ops.storeFeatureDefinition(def4, tx);
      orcsApi.getApplicabilityOps();
   }

   /**
    * TODO: Remove after 26.0 release which converted feature definitions from a single json string
    */
   private void createLegacyFeatureConfig(ArtifactId folder, TransactionBuilder tx) {

      ArtifactId featureDefinition = tx.createArtifact(CoreArtifactTokens.ProductsFolder,
         CoreArtifactTypes.FeatureDefinition, "Feature Definition");

      String featureDefJson = "[{" + "\"name\": \"Feature 1\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"A significant capability\"" + //
         "}, {" + //
         "\"name\": \"Unit Type\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Metric\", \"English\"]," + //
         "\"defaultValue\": \"\"," + //
         "\"description\": \"Used select type of units\"" + //
         "},{" + //
         "\"name\": \"Feature 3\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Included\", \"Excluded\"]," + //
         "\"defaultValue\": \"Included\"," + //
         "\"description\": \"A small point of variation\"" + //
         "},{" + //
         "\"name\": \"Feature 4\"," + //
         "\"type\": \"single\"," + //
         "\"values\": [\"Mod A\", \"Mod B\", \"Mod C\"]," + //
         "\"defaultValue\": \"Mod A\"," + //
         "\"description\": \"This feature depends of Feature 1\"" + //
         "}" + //
         "]";

      tx.createAttribute(featureDefinition, CoreAttributeTypes.GeneralStringData, featureDefJson);
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