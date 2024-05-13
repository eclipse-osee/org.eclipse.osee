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
package org.eclipse.osee.framework.core.applicability;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.eclipse.osee.framework.core.data.ArtifactReadable;

/**
 * @TODO implement groups once supported in BAT tool
 */
public class BatConfigFile {

   private String normalizedName = "";
   private final List<String> features = new ArrayList<String>();
   private final List<BatMatchText> substitutions = new ArrayList<BatMatchText>();
   public BatConfigFile() {
      // for jax-rs doubt it'll be used
   }

   public BatConfigFile(ArtifactReadable configOrGroup, Map<String, List<String>> namedViewApplicabilityMap, List<ArtifactReadable> featureArts) {
      this.setNormalizedName(configOrGroup.getName().replace(" ", "_"));
      this.addFeatures(featureArts.stream().map(
         f -> f.getName() + "=" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",",
            namedViewApplicabilityMap.get(f.getName()))).collect(Collectors.toList()));
   }

   /**
    * @return the normalizedName
    */
   public String getNormalizedName() {
      return normalizedName;
   }

   /**
    * @param normalizedName the normalizedName to set
    */
   public void setNormalizedName(String normalizedName) {
      this.normalizedName = normalizedName;
   }

   /**
    * @return the features
    */
   public List<String> getFeatures() {
      return features;
   }

   /**
    * @param features the features to set
    */
   public void addFeatures(List<String> features) {
      this.features.addAll(features);
   }

   /**
    * @return the substitutions
    */
   public List<BatMatchText> getSubstitutions() {
      return substitutions;
   }

   /**
    * @param substitutions the substitutions to set
    */
   public void addSubstitutions(List<BatMatchText> substitutions) {
      this.substitutions.addAll(substitutions);
   }

}
