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
import org.eclipse.osee.framework.jdk.core.util.Conditions;
import org.eclipse.osee.framework.jdk.core.util.NamedComparator;
import org.eclipse.osee.framework.jdk.core.util.SortOrder;
import org.eclipse.osee.orcs.OrcsApi;
import org.eclipse.osee.orcs.OrcsApplicability;
import org.eclipse.osee.orcs.core.util.Artifacts;
import org.eclipse.osee.orcs.data.ArtifactReadable;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public class OrcsApplicabilityOps implements OrcsApplicability {

   private final OrcsApi orcsApi;

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
      List<ArtifactReadable> branchViews =
         orcsApi.getQueryFactory().fromBranch(branch).andIsOfType(CoreArtifactTypes.BranchView).getResults().getList();
      Collections.sort(branchViews, new NamedComparator(SortOrder.ASCENDING));
      for (ArtifactToken branchView : branchViews) {
         config.addVariant(branchView);
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
      for (FeatureDefinition fDef : config.getFeatures()) {
         int count = 0;
         for (ArtifactToken variant : config.getVariants()) {
            Map<String, String> variantToValue = config.getFeatureToValues(count);
            variantToValue.put(variant.getName().toLowerCase(), getVariantToFeatureValue(variant, fDef));
            count++;
         }
      }

      return config;
   }

   private String getVariantToFeatureValue(ArtifactToken variant, FeatureDefinition fDef) {
      return "Included";
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
   public ArtifactToken getProductLineFolder(BranchId branch) {
      return Artifacts.get(CoreArtifactTokens.ProductLineFolder, branch, orcsApi);
   }

   @Override
   public ArtifactToken getFeatureFolder(BranchId branch) {
      return Artifacts.get(CoreArtifactTokens.FeaturesFolder, branch, orcsApi);
   }

   @Override
   public ArtifactToken getProductsFolder(BranchId branch) {
      return Artifacts.get(CoreArtifactTokens.ProductsFolder, branch, orcsApi);
   }

   @Override
   public ArtifactToken storeFeatureDefinition(FeatureDefinition featureDef, TransactionBuilder tx) {
      ArtifactToken fDefArt = orcsApi.getQueryFactory().fromBranch(tx.getBranch()).andId(
         ArtifactId.valueOf(featureDef.getId())).getResults().getAtMostOneOrNull();
      if (fDefArt == null) {
         fDefArt = tx.getWriteable(ArtifactId.valueOf(featureDef.getId()));
      }
      if (fDefArt == null || fDefArt.isInvalid()) {
         ArtifactToken featureFolder = tx.getWriteable(CoreArtifactTokens.FeaturesFolder);
         if (featureFolder.isInvalid()) {
            featureFolder = getFeatureFolder(tx.getBranch());
         }
         Conditions.assertNotNull(featureFolder, "Feature Folder missing from branch %s", tx.getBranch());
         fDefArt =
            tx.createArtifact(featureFolder, CoreArtifactTypes.Feature, featureDef.getName(), featureDef.getId());
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

}
