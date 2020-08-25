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

package org.eclipse.osee.framework.core.applicability;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchViewToken;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityBranchConfig {

   BranchViewToken branch;
   BranchViewToken parentBranch;
   Boolean editable;
   Map<String, FeatureDefinition> featureIdToFeature = new HashMap<>();
   List<String> featuresOrdered = new LinkedList<>();
   List<ArtifactToken> views = new LinkedList<>();
   private List<ArtifactToken> groups = new LinkedList<>();
   List<String> viewsOrdered = new LinkedList<>();
   List<Map<String, String>> featureToValueMaps = new LinkedList<>();
   List<FeatureDefinition> features = new LinkedList<>();
   ArtifactId associatedArtifactId;

   public void addFeature(FeatureDefinition fDef) {
      featureIdToFeature.put(fDef.getIdString(), fDef);
      featuresOrdered.add(fDef.getName());
      features.add(fDef);
   }

   public void addView(ArtifactToken view) {
      views.add(view);
      viewsOrdered.add(view.getName());
   }

   public void addGroup(ArtifactToken group) {
      groups.add(group);
   }

   public BranchViewToken getBranch() {
      return branch;
   }

   public void setBranch(BranchViewToken branch) {
      // Create new token so Branch serializer does not take over
      this.branch = new BranchViewToken(branch.getId(), branch.getName(), branch.getViewId());
   }

   public List<ArtifactToken> getViews() {
      return views;
   }

   public void setViews(List<ArtifactToken> views) {
      this.views = views;
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

   public List<String> getViewsOrdered() {
      return viewsOrdered;
   }

   public void setViewsOrdered(List<String> viewsOrdered) {
      this.viewsOrdered = viewsOrdered;
   }

   public Map<String, FeatureDefinition> getFeatureIdToFeature() {
      return featureIdToFeature;
   }

   public void setFeatureIdToFeature(Map<String, FeatureDefinition> featureIdToFeature) {
      this.featureIdToFeature = featureIdToFeature;
   }

   public List<FeatureDefinition> getFeatures() {
      return features;
   }

   public void setFeatures(List<FeatureDefinition> features) {
      this.features = features;
   }

   public ArtifactId getAssociatedArtifactId() {
      return associatedArtifactId;
   }

   public void setAssociatedArtifactId(ArtifactId associatedArtifactId) {
      this.associatedArtifactId = associatedArtifactId;
   }

   public BranchViewToken getParentBranch() {
      return parentBranch;
   }

   public void setParentBranch(BranchViewToken parentBranch) {
      this.parentBranch = parentBranch;
   }

   public Boolean getEditable() {
      return editable;
   }

   public void setEditable(Boolean editable) {
      this.editable = editable;
   }

   public List<ArtifactToken> getGroups() {
      return groups;
   }

   public void setGroups(List<ArtifactToken> groups) {
      this.groups = groups;
   }

}
