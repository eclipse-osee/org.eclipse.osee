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
package org.eclipse.osee.orcs.core.internal.applicability;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.Branch;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.enums.BranchState;
import org.eclipse.osee.framework.core.enums.BranchType;
import org.eclipse.osee.framework.core.enums.CoreArtifactTokens;
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.enums.SystemUser;
import org.eclipse.osee.framework.jdk.core.util.AHTML;
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class OrcsApplicabilityOps implements OrcsApplicability {

   private final OrcsApi orcsApi;
   private ArtifactToken plFolder = ArtifactToken.getSentinal();
   private ArtifactToken featureFolder = ArtifactToken.getSentinal();
   private ArtifactToken variantsFolder = ArtifactToken.getSentinal();

   public OrcsApplicabilityOps(OrcsApi orcsApi) {
      this.orcsApi = orcsApi;
   }

   /**
    * @return config as defined in Feature artifacts
    */
   @Override
   public ApplicabilityBranchConfig getConfig(BranchId branchId) {
      ApplicabilityBranchConfig config = new ApplicabilityBranchConfig();
      Branch branch = orcsApi.getQueryFactory().branchQuery().andId(branchId).getResults().getAtMostOneOrNull();
      config.setBranch(branch);

      // Load all variants (stored as branch views)
      List<ArtifactReadable> branchViews =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).getResults().getList();
      Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
      Map<ArtifactId, Map<String, List<String>>> branchViewsMap = new HashMap<>();
      for (ArtifactToken branchView : branchViews) {
         config.addVariant(branchView);
         branchViewsMap.put(branchView,
            orcsApi.getQueryFactory().applicabilityQuery().getNamedViewApplicabilityMap(branch, branchView));
      }

      List<ArtifactReadable> featureArts =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.Feature).getResults().getList();
      Collections.sort(featureArts, new NamedComparator(SortOrder.ASCENDING));
      for (ArtifactToken featureArt : featureArts) {
         FeatureDefinition fDef = getFeatureDefinition(featureArt);
         config.addFeature(fDef);
      }

      // Setup Features column and add maps in order
      for (FeatureDefinition fDef : config.getFeatures()) {
         Map<String, String> variantToValue = new HashMap<>(config.getFeatures().size() + 1);
         /**
          * Note: Confusing, but Features is the header of the first column, so add it here. The rest of the columns
          * headers will be the variant names added below.
          */
         variantToValue.put("feature", fDef.getName());
         config.addFeatureToValueMap(variantToValue);
      }

      // Add variants and values
      int count = 0;
      for (FeatureDefinition fDef : config.getFeatures()) {
         for (ArtifactToken variant : config.getVariants()) {
            Map<String, String> variantToValue = config.getFeatureToValues(count);
            String variantToFeatureValue = getVariantToFeatureValue(variant, fDef, branchViewsMap);
            variantToValue.put(variant.getName().toLowerCase(), variantToFeatureValue);
         }
         count++;
      }

      return config;
   }

   private String getVariantToFeatureValue(ArtifactToken variant, FeatureDefinition fDef, Map<ArtifactId, Map<String, List<String>>> branchViewsMap) {
      Map<String, List<String>> map = branchViewsMap.get(variant);
      List<String> list = map.get(fDef.getName());
      return org.eclipse.osee.framework.jdk.core.util.Collections.toString(",", list);
   }

   @Override
   public FeatureDefinition getFeatureDefinition(ArtifactToken featureArt) {
      ArtifactReadable art = (ArtifactReadable) featureArt;
      FeatureDefinition feature = new FeatureDefinition();
      feature.setId(art.getId());
      feature.setName(art.getName());
      feature.setDefaultValue(art.getSoleAttributeValue(CoreAttributeTypes.DefaultValue, ""));
      feature.setValues(art.getAttributeValues(CoreAttributeTypes.Value));
      feature.setValueType(art.getSoleAttributeAsString(CoreAttributeTypes.FeatureValueType, ""));
      feature.setMultiValued(art.getSoleAttributeValue(CoreAttributeTypes.FeatureMultivalued, false));
      feature.setDescription(art.getSoleAttributeAsString(CoreAttributeTypes.Description, ""));
      feature.setData(featureArt);
      return feature;
   }

   @Override
   public ArtifactToken createUpdateFeatureDefinition(FeatureDefinition featureDef, TransactionBuilder tx) {
      ArtifactToken fDefArt = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(
         ArtifactId.valueOf(featureDef.getId())).getResults().getAtMostOneOrNull();
      if (fDefArt == null) {
         ArtifactToken featuresFolder = tx.getWriteable(CoreArtifactTokens.FeaturesFolder);
         // Check current transaction first
         if (featuresFolder == null) {
            featuresFolder = getFeaturesFolder(tx.getBranch());
         }
         Conditions.assertNotNull(featuresFolder, "Features Folder cannot be null");
         fDefArt =
            tx.createArtifact(featuresFolder, CoreArtifactTypes.Feature, featureDef.getName(), featureDef.getId());
      }
      updateFeatureDefinition(fDefArt, featureDef, tx);
      return fDefArt;
   }

   private void updateFeatureDefinition(ArtifactToken fDefArt, FeatureDefinition featureDef, TransactionBuilder tx) {
      tx.setName(fDefArt, featureDef.getName());
      tx.setSoleAttributeValue(fDefArt, CoreAttributeTypes.DefaultValue, featureDef.getDefaultValue());
      tx.setAttributesFromValues(fDefArt, CoreAttributeTypes.Value, featureDef.getValues());
      tx.setSoleAttributeValue(fDefArt, CoreAttributeTypes.FeatureValueType, featureDef.getValueType());
      tx.setSoleAttributeValue(fDefArt, CoreAttributeTypes.FeatureMultivalued, featureDef.isMultiValued());
      tx.setSoleAttributeValue(fDefArt, CoreAttributeTypes.Description, featureDef.getDescription());
   }

   @Override
   public List<BranchViewToken> getApplicabilityBranches() {
      List<BranchViewToken> tokens = new ArrayList<BranchViewToken>();
      for (Branch branch : orcsApi.getQueryFactory().branchQuery().includeArchived(false).includeDeleted(
         false).andIsOfType(BranchType.BASELINE, BranchType.WORKING).andStateIs(BranchState.CREATED,
            BranchState.MODIFIED).getResults().getList()) {
         if (orcsApi.getQueryFactory().fromBranch(branch).andId(CoreArtifactTokens.ProductLineFolder).exists()) {
            tokens.add(new BranchViewToken(branch, branch.getName(), branch.getViewId()));
         }
      }
      return tokens;
   }

   @Override
   public ArtifactToken getFeaturesFolder(BranchId branch) {
      if (featureFolder.isInvalid()) {
         featureFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(
            CoreArtifactTokens.FeaturesFolder).getArtifactOrSentinal();
      }
      if (featureFolder.isInvalid()) {
         featureFolder = orcsApi.getQueryFactory().fromBranch(branch).andNameEquals(
            CoreArtifactTokens.FeaturesFolder.getName()).getAtMostOneOrSentinal();
      }
      return featureFolder;
   }

   @Override
   public ArtifactToken getVariantsFolder(BranchId branch) {
      if (variantsFolder.isInvalid()) {
         variantsFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(
            CoreArtifactTokens.VariantsFolder).getArtifactOrSentinal();
      }
      if (variantsFolder.isInvalid()) {
         variantsFolder = orcsApi.getQueryFactory().fromBranch(branch).andNameEquals(
            CoreArtifactTokens.VariantsFolder.getName()).getAtMostOneOrSentinal();
      }
      if (variantsFolder.isInvalid()) {
         variantsFolder =
            orcsApi.getQueryFactory().fromBranch(branch).andNameEquals("Products").getAtMostOneOrSentinal();
      }
      return variantsFolder;
   }

   @Override
   public ArtifactToken getProductLineFolder(BranchId branch) {
      if (plFolder.isInvalid()) {
         plFolder = orcsApi.getQueryFactory().fromBranch(branch).andId(
            CoreArtifactTokens.ProductLineFolder).getArtifactOrSentinal();
      }
      if (plFolder.isInvalid()) {
         plFolder = orcsApi.getQueryFactory().fromBranch(branch).andNameEquals("Product Line").getAtMostOneOrSentinal();
      }
      return plFolder;
   }

   @Override
   public String convertConfigToArtifact(BranchId branch) {
      TransactionBuilder tx = orcsApi.getTransactionFactory().createTransaction(branch, SystemUser.OseeSystem,
         "Convert Feature Defs to Artifact");

      for (FeatureDefinition feature : orcsApi.getQueryFactory().applicabilityQuery().getFeatureDefinitionData(
         branch)) {
         ArtifactToken featureArt = getFeaturesFolder(branch);

         // type goes to multivalued
         if (feature.getValueType().equals("single")) {
            // do nothing
         } else if (feature.getValueType().equals("multiple")) {
            feature.setMultiValued(true);
         } else {
            throw new IllegalArgumentException(String.format("Unexpected value type [%s]", feature.getValueType()));
         }

         // set feature value type
         feature.setValueType("String");

         updateFeatureDefinition(featureArt, feature, tx);
      }
      tx.commit();
      return AHTML.simplePage("Completed");
   }

}
