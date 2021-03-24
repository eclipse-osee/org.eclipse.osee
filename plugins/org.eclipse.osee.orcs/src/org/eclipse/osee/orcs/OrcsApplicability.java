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

package org.eclipse.osee.orcs;

import java.util.List;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.CreateViewDefinition;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public interface OrcsApplicability {

   ApplicabilityBranchConfig getConfig(BranchId branchId, boolean showAll);

   FeatureDefinition getFeatureDefinition(ArtifactToken featureArt);

   ArtifactToken getProductLineFolder(BranchId branch);

   ArtifactToken getFeaturesFolder(BranchId branch);

   ArtifactToken createUpdateFeatureDefinition(FeatureDefinition featureDef, String action, TransactionBuilder tx, XResultData results);

   List<BranchId> getApplicabilityBranches();

   List<BranchId> getApplicabilityBranchesByType(String branchQueryType);

   ArtifactToken getProductsFolder(BranchId branch);

   XResultData convertConfigToArtifact(BranchId branch);

   CreateViewDefinition getViewDefinition(ArtifactToken artifact);

   XResultData createUpdateFeature(FeatureDefinition feature, String action, BranchId branch, UserId account);

   FeatureDefinition getFeature(String feature, BranchId branch);

   XResultData deleteFeature(ArtifactId feature, BranchId branch, UserId account);

   CreateViewDefinition getView(String view, BranchId branch);

   XResultData createUpdateView(CreateViewDefinition view, String action, BranchId branch, UserId account);

   XResultData deleteView(String view, BranchId branch, UserId account);

   XResultData setApplicability(BranchId branch, ArtifactId variant, ArtifactId feature, String applicability, UserId account);

   List<FeatureDefinition> getFeatureDefinitionData(BranchId branch);

   XResultData createApplicabilityForView(ArtifactId viewId, String applicability, UserId account, BranchId branch);

   XResultData createCfgGroup(ConfigurationGroupDefinition group, BranchId branch, UserId account);

   XResultData relateCfgGroupToView(String groupId, String viewId, BranchId branch, UserId account);

   XResultData unrelateCfgGroupToView(String groupId, String viewId, BranchId branch, UserId account);

   ArtifactToken getPlConfigurationGroupsFolder(BranchId branch);

   XResultData deleteCfgGroup(String id, BranchId branch, UserId account);

   XResultData syncConfigGroup(BranchId branch, String cfgGroup, UserId account, XResultData results);

   XResultData syncConfigGroup(BranchId branch, UserId account);

   XResultData removeApplicabilityFromView(BranchId branch, ArtifactId viewId, String applicability, UserId account);

   String evaluateApplicabilityExpression(BranchId branch, ArtifactToken view, ApplicabilityBlock applic);

   String applyApplicabilityToFiles(BranchId branch, ArtifactId view, boolean commentNonApplicableBlocks, String sourcePath);

   ConfigurationGroupDefinition getConfigurationGroup(String cfgGroup, BranchId branch);

   XResultData updateCfgGroup(ConfigurationGroupDefinition group, BranchId branch, UserId account);

}