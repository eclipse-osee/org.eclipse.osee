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
package org.eclipse.osee.orcs;

import java.util.List;
import org.eclipse.osee.framework.core.applicability.ApplicabilityBranchConfig;
import org.eclipse.osee.framework.core.applicability.FeatureDefinition;
import org.eclipse.osee.framework.core.data.ArtifactId;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.BranchId;
import org.eclipse.osee.framework.core.data.BranchViewToken;
import org.eclipse.osee.framework.core.data.UserId;
import org.eclipse.osee.framework.core.data.VariantDefinition;
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

   List<BranchViewToken> getApplicabilityBranches();

   ArtifactToken getVariantsFolder(BranchId branch);

   String convertConfigToArtifact(BranchId branch);

   VariantDefinition getVariantDefinition(ArtifactToken artifact);

   XResultData createUpdateFeature(FeatureDefinition feature, String action, BranchId branch, UserId account);

   FeatureDefinition getFeature(String feature, BranchId branch);

   XResultData deleteFeature(String feature, BranchId branch, UserId account);

   VariantDefinition getVariant(String variant, BranchId branch);

   XResultData createUpdateVariant(VariantDefinition variant, String action, BranchId branch, UserId account);

   XResultData deleteVariant(String variant, BranchId branch, UserId account);

   XResultData setApplicability(BranchId branch, ArtifactId variant, ArtifactId feature, String applicability, UserId account);

}
