/*******************************************************************************
 * Copyright (c) 2016 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.orcs.search;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ApplicabilityId;
import org.eclipse.osee.framework.core.data.ApplicabilityToken;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.TransactionId;
import org.eclipse.osee.framework.jdk.core.type.Pair;

/**
 * @author Ryan D. Brooks
 */
public interface ApplicabilityQuery {

   HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch);

   ApplicabilityToken getApplicabilityToken(ArtifactId artId, BranchId branch);

   List<ApplicabilityId> getApplicabilitiesReferenced(ArtifactId artifact, BranchId branch);

   List<ApplicabilityToken> getApplicabilityReferenceTokens(ArtifactId artifact, BranchId branch);

   List<Pair<ArtifactId, ApplicabilityToken>> getApplicabilityTokens(List<? extends ArtifactId> artIds, BranchId branch);

   List<ApplicabilityToken> getViewApplicabilityTokens(ArtifactId artId, BranchId branch);

   HashMap<Long, ApplicabilityToken> getApplicabilityTokens(BranchId branch1, BranchId branch2);

   List<FeatureDefinition> getFeatureDefinitionData(BranchId branch);

   Map<String, List<String>> getNamedViewApplicabilityMap(BranchId branch, ArtifactId viewId);

   List<ArtifactToken> getViewForBranch(BranchId branch);

   String getViewTable(BranchId branch, String filter);

   ArtifactId getVersionConfig(ArtifactId art, BranchId branch);

   List<BranchId> getAffectedBranches(Long injectDateMs, Long removalDateMs, List<ApplicabilityId> applicabilityIds, BranchId branch);

   List<BranchId> getAffectedBranches(TransactionId injectionTx, TransactionId removalTx, List<ApplicabilityId> applicabilityIds, BranchId branch);

   Set<ArtifactId> getExcludedArtifacts(BranchId branch, ArtifactId view);

   String getExistingFeatureApplicability(BranchId branch, ArtifactId viewId, String featureName);

   List<ArtifactId> getBranchViewsForApplicability(BranchId branch, ApplicabilityId applId);

   boolean featureExistsOnBranch(BranchId branch, String featureName);

   boolean featureValueIsValid(BranchId branch, String featureName, String featureValue);

   boolean viewExistsOnBranch(BranchId branch, ArtifactId viewId);

   boolean applicabilityExistsOnBranchView(BranchId branch, ArtifactId viewId, String applicability);
}