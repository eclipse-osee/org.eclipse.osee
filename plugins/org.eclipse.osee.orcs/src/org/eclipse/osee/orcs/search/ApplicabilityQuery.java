/*********************************************************************
 * Copyright (c) 2016 Boeing
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

package org.eclipse.osee.orcs.search;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.applicability.ApplicabilityUseResultToken;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Ryan D. Brooks
 */
public interface ApplicabilityQuery {

   HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch);

   Collection<ApplicabilityToken> getApplicabilityTokens(BranchId branch, boolean orderByName, String filter,
      Long pageNum, Long pageSize);

   Long getApplicabilityTokenCount(BranchId branch, String filter);

   ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch);

   List<ApplicabilityId> getApplicabilitiesReferenced(ArtifactId artifact, BranchId branch);

   List<ApplicabilityToken> getApplicabilityReferenceTokens(ArtifactId artifact, BranchId branch);

   List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(List<? extends ArtifactId> artIds,
      BranchId branch);

   List<ApplicabilityToken> getViewApplicabilityTokens(ArtifactId artId, BranchId branch);

   HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch1, BranchId branch2);

   Map<String, List<String>> getNamedViewApplicabilityMap(BranchId branch, ArtifactId viewId);

   List<ArtifactToken> getViewsForBranch(BranchId branch);

   ArtifactToken getViewByName(BranchId branch, String viewName);

   String getViewTable(BranchId branch, String filter);

   ArtifactId getVersionConfig(ArtifactId art, BranchId branch);

   List<BranchId> getAffectedBranches(Long injectDateMs, Long removalDateMs, List<ApplicabilityId> applicabilityIds,
      BranchId branch);

   List<BranchId> getAffectedBranches(TransactionId injectionTx, TransactionId removalTx,
      List<ApplicabilityId> applicabilityIds, BranchId branch);

   Set<ArtifactId> getExcludedArtifacts(BranchId branch, ArtifactId view);

   String getExistingFeatureApplicability(BranchId branch, ArtifactId viewId, String featureName);

   List<ArtifactId> getBranchViewsForApplicability(BranchId branch, ApplicabilityId applId);

   boolean featureExistsOnBranch(BranchId branch, String featureName);

   boolean featureValueIsValid(BranchId branch, String featureName, String featureValue);

   boolean viewExistsOnBranch(BranchId branch, ArtifactId viewId);

   boolean applicabilityExistsOnBranchView(BranchId branch, ArtifactId viewId, String applicability);

   List<ArtifactToken> getConfigurationGroupsForBranch(BranchId branch);

   String getConfigMatrix(BranchId branch, String matrixtype, String filter);

   List<ArtifactToken> getConfigurationsForBranch(BranchId branch);

   List<ApplicabilityUseResultToken> getApplicabilityUsage(BranchId branch, String applic, List<ArtifactTypeToken> arts,
      List<AttributeTypeToken> atts);

   List<FeatureDefinition> getFeatureDefinitionData(BranchId branch, String productType);

   List<FeatureDefinition> getFeatureDefinitionData(BranchId branch);

}