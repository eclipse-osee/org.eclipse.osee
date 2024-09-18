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
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;

public class BatGroupFile extends BatFile {

   private String name = "";
   private List<String> configs = new ArrayList<String>();
   private final List<String> features = new ArrayList<String>();
   private final List<BatMatchText> substitutions = new ArrayList<BatMatchText>();
   public BatGroupFile() {
      // for jax-rs doubt it'll be used
   }

   public BatGroupFile(ArtifactReadable configOrGroup, Map<String, List<String>> namedViewApplicabilityMap, List<ArtifactReadable> featureArts) {
      this.setName(configOrGroup.getName().replace(" ", "_"));
      this.addFeatures(featureArts.stream().map(
         f -> f.getName() + "=" + org.eclipse.osee.framework.jdk.core.util.Collections.toString(",",
            namedViewApplicabilityMap.get(f.getName()))).collect(Collectors.toList()));
      List<ArtifactReadable> configs =
         configOrGroup.getRelated(CoreRelationTypes.PlConfigurationGroup_BranchView).getList();
      this.setConfigs(configs.stream().map(x -> x.getName().replace(" ", "_")).collect(Collectors.toList()));
   }

   /**
    * @return the normalizedName
    */
   public String getName() {
      return name;
   }

   /**
    * @param normalizedName the normalizedName to set
    */
   public void setName(String normalizedName) {
      this.name = normalizedName;
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

   /**
    * @return the group
    */
   public List<String> getConfigs() {
      return configs;
   }

   public void setConfigs(List<String> configs) {
      this.configs = configs;
   }

}
