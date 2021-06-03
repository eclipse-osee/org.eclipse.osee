/*********************************************************************
 * Copyright (c) 2021 Boeing
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
package org.eclipse.osee.framework.core.data;

import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;

/**
 * @author Branden W. Phillips
 */
public class BlockApplicabilityCacheFile {

   private Long viewId;
   private String viewName;
   private Long viewTypeId;
   private List<FeatureDefinition> featureDefinition;
   private Map<String, List<String>> viewApplicabilitiesMap;
   private Map<String, List<String>> configurationMap;
   private String productLinePreferences;

   public BlockApplicabilityCacheFile() {
      // for jax-rs
   }

   public BlockApplicabilityCacheFile(Long viewId, String viewName, Long viewTypeId, List<FeatureDefinition> featureDefinition, Map<String, List<String>> viewApplicabiliesMap, Map<String, List<String>> configurationMap, String productLinePreferences) {
      this.viewId = viewId;
      this.viewName = viewName;
      this.viewTypeId = viewTypeId;
      this.featureDefinition = featureDefinition;
      this.viewApplicabilitiesMap = viewApplicabiliesMap;
      this.configurationMap = configurationMap;
      this.productLinePreferences = productLinePreferences;
   }

   public Long getViewId() {
      return viewId;
   }

   public void setViewId(Long viewId) {
      this.viewId = viewId;
   }

   public String getViewName() {
      return viewName;
   }

   public void setViewName(String viewName) {
      this.viewName = viewName;
   }

   public Long getViewTypeId() {
      return viewTypeId;
   }

   public void setViewTypeId(Long viewTypeId) {
      this.viewTypeId = viewTypeId;
   }

   public List<FeatureDefinition> getFeatureDefinition() {
      return featureDefinition;
   }

   public void setFeatureDefinition(List<FeatureDefinition> featureDefinition) {
      this.featureDefinition = featureDefinition;
   }

   public Map<String, List<String>> getViewApplicabilitiesMap() {
      return viewApplicabilitiesMap;
   }

   public void setViewApplicabilitiesMap(Map<String, List<String>> viewApplicabilitiesMap) {
      this.viewApplicabilitiesMap = viewApplicabilitiesMap;
   }

   public Map<String, List<String>> getConfigurationMap() {
      return configurationMap;
   }

   public void setConfigurationMap(Map<String, List<String>> configurationMap) {
      this.configurationMap = configurationMap;
   }

   public String getProductLinePreferences() {
      return productLinePreferences;
   }

   public void setProductLinePreferences(String productLinePreferences) {
      this.productLinePreferences = productLinePreferences;
   }

}
