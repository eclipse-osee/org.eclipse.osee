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
package org.eclipse.osee.framework.core.applicability;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchViewToken;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityBranchConfig {

   BranchViewToken branch;
   List<FeatureDefinition> features = new LinkedList<>();
   List<String> featuresOrdered = new LinkedList<>();
   List<ArtifactToken> variants = new LinkedList<>();
   List<String> variantsOrdered = new LinkedList<>();
   List<Map<String, String>> featureToValueMaps = new LinkedList<>();

   public void addFeature(FeatureDefinition fDef) {
      features.add(fDef);
      featuresOrdered.add(fDef.getName());
   }

   public void addVariant(ArtifactToken variant) {
      variants.add(variant);
      variantsOrdered.add(variant.getName());
   }

   public BranchViewToken getBranch() {
      return branch;
   }

   public void setBranch(BranchViewToken branch) {
      // Create new token so Branch serializer does not take over
      this.branch = new BranchViewToken(branch.getId(), branch.getName(), branch.getViewId());
   }

   public List<FeatureDefinition> getFeatures() {
      return features;
   }

   public void setFeatures(List<FeatureDefinition> features) {
      this.features = features;
   }

   public List<ArtifactToken> getVariants() {
      return variants;
   }

   public void setVariants(List<ArtifactToken> variants) {
      this.variants = variants;
   }

   public void addFeatureToValueMap(Map<String, String> featureToValue) {
      featureToValueMaps.add(featureToValue);
   }

   public Map<String, String> getFeatureToValues(int index) {
      return featureToValueMaps.get(index);
   }

   public List<Map<String, String>> getFeatureToValueMaps() {
      return featureToValueMaps;
   }

   public List<String> getFeaturesOrdered() {
      return featuresOrdered;
   }

   public List<String> getVariantsOrdered() {
      return variantsOrdered;
   }

   public void setVariantsOrdered(List<String> variantsOrdered) {
      this.variantsOrdered = variantsOrdered;
   }

}
