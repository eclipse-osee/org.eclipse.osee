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

import java.util.LinkedList;
import java.util.List;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;

/**
 * @author Donald G. Dunne
 */
public class ApplicabilityBranchConfig {

   BranchViewToken branch;
   BranchViewToken parentBranch;
   Boolean editable;
   List<BranchViewDefinition> views = new LinkedList<>();
   List<ConfigurationGroupDefinition> groups = new LinkedList<>();
   List<ExtendedFeatureDefinition> features = new LinkedList<>();
   ArtifactId associatedArtifactId;

   public void addFeature(ExtendedFeatureDefinition fDef) {
      features.add(fDef);
   }

   public void addView(BranchViewDefinition view) {
      views.add(view);
   }

   public void addGroup(ConfigurationGroupDefinition group) {
      groups.add(group);
   }

   public BranchViewToken getBranch() {
      return branch;
   }

   public void setBranch(BranchViewToken branch) {
      // Create new token so Branch serializer does not take over
      this.branch = new BranchViewToken(branch.getId(), branch.getName(), branch.getViewId());
   }

   public List<BranchViewDefinition> getViews() {
      return views;
   }

   public void setViews(List<BranchViewDefinition> views) {
      this.views = views;
   }

   public List<ExtendedFeatureDefinition> getFeatures() {
      return features;
   }

   public void setFeatures(List<ExtendedFeatureDefinition> features) {
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

   public List<ConfigurationGroupDefinition> getGroups() {
      return groups;
   }

   public void setGroups(List<ConfigurationGroupDefinition> groups) {
      this.groups = groups;
   }

}
