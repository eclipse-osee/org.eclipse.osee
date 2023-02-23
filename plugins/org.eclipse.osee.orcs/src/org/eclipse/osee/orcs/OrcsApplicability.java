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

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.applicability.ProductTypeDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactReadable;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BlockApplicabilityStageRequest;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.ConfigurationGroupDefinition;
import org.eclipse.osee.framework.core.data.CreateViewDefinition;
import org.eclipse.osee.framework.core.grammar.ApplicabilityBlock;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.orcs.transaction.TransactionBuilder;

/**
 * @author Donald G. Dunne
 */
public interface OrcsApplicability {

   ApplicabilityBranchConfig getConfig(BranchId branchId);

   ApplicabilityBranchConfig getConfigWithCompoundApplics(BranchId branchId);

   FeatureDefinition getFeatureDefinition(ArtifactReadable featureArt);

   ArtifactToken getProductLineFolder(BranchId branch);

   ArtifactToken getFeaturesFolder(BranchId branch);

   ArtifactToken createFeatureDefinition(FeatureDefinition featureDef, TransactionBuilder tx, XResultData results);

   ArtifactToken updateFeatureDefinition(FeatureDefinition featureDef, TransactionBuilder tx, XResultData results);

   List<BranchId> getApplicabilityBranches();

   List<BranchId> getApplicabilityBranchesByType(String branchQueryType);

   ArtifactToken getConfigurationsFolder(BranchId branch);

   CreateViewDefinition getViewDefinition(ArtifactReadable artifact);

   XResultData createFeature(FeatureDefinition feature, BranchId branch);

   XResultData updateFeature(FeatureDefinition feature, BranchId branch);

   FeatureDefinition getFeature(String feature, BranchId branch);

   Collection<FeatureDefinition> getFeatures(BranchId branch);

   Collection<FeatureDefinition> getFeaturesByProductApplicability(BranchId branch, String productApplicability);

   XResultData deleteFeature(ArtifactId feature, BranchId branch);

   CreateViewDefinition getView(String view, BranchId branch);

   Collection<CreateViewDefinition> getViewDefinitions(BranchId branch);

   Collection<CreateViewDefinition> getViewsDefinitionsByProductApplicability(BranchId branch, String productApplicability);

   XResultData createView(CreateViewDefinition view, BranchId branch);

   XResultData updateView(CreateViewDefinition view, BranchId branch);

   XResultData deleteView(String view, BranchId branch);

   XResultData setApplicability(BranchId branch, ArtifactId variant, ArtifactId feature, String applicability);

   List<FeatureDefinition> getFeatureDefinitionData(BranchId branch);

   XResultData createApplicabilityForView(ArtifactId viewId, String applicability, BranchId branch);

   XResultData createCfgGroup(ConfigurationGroupDefinition group, BranchId branch);

   XResultData relateCfgGroupToView(String groupId, String viewId, BranchId branch);

   XResultData unrelateCfgGroupToView(String groupId, String viewId, BranchId branch);

   ArtifactToken getPlConfigurationGroupsFolder(BranchId branch);

   XResultData deleteCfgGroup(String id, BranchId branch);

   XResultData syncConfigGroup(BranchId branch, String cfgGroup, XResultData results);

   XResultData syncConfigGroup(BranchId branch);

   XResultData removeApplicabilityFromView(BranchId branch, ArtifactId viewId, String applicability);

   String evaluateApplicabilityExpression(BranchId branch, ArtifactToken view, ApplicabilityBlock applic);

   XResultData applyApplicabilityToFiles(BlockApplicabilityStageRequest data, BranchId branch);

   XResultData refreshStagedFiles(BlockApplicabilityStageRequest data, BranchId branch);

   XResultData startWatcher(BlockApplicabilityStageRequest data, BranchId branch);

   XResultData stopWatcher();

   ConfigurationGroupDefinition getConfigurationGroup(String cfgGroup, BranchId branch);

   XResultData updateCfgGroup(ConfigurationGroupDefinition group, BranchId branch);

   XResultData validateCompoundApplicabilities(BranchId branch, boolean update, XResultData results);

   ProductTypeDefinition getProductType(String productType, BranchId branch);

   ProductTypeDefinition getProductTypeDefinition(ArtifactReadable artifact);

   Collection<ProductTypeDefinition> getProductTypeDefinitions(BranchId branch, long pageNum, long pageSize, AttributeTypeToken orderByAttributeType);

   XResultData createProductType(ProductTypeDefinition productType, BranchId branch);

   XResultData updateProductType(ProductTypeDefinition productType, BranchId branch);

   XResultData deleteProductType(ArtifactId productType, BranchId branch);

   String uploadBlockApplicability(InputStream zip);

   XResultData applyBlockVisibilityOnServer(String blockApplicId, BlockApplicabilityStageRequest data, BranchId branch);

   XResultData deleteBlockApplicability(String blockApplicId);

   String uploadRunBlockApplicability(Long view, InputStream zip, BranchId branch);

   XResultData createCompoundApplicabilityForBranch(String applicability, BranchId branch);

   XResultData deleteCompoundApplicabilityFromBranch(ApplicabilityId compApplicId, BranchId branch);

   XResultData validate(BranchId branch, boolean update, XResultData results);
}