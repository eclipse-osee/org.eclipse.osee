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

package org.eclipse.osee.ats.ide.util.validate;

import java.util.HashSet;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.ats.ide.internal.AtsApiService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.publishing.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.ApplicabilityUtility;

/**
 * @author Morgan E. Cook
 */
public class MatchingApplicabilityTagsRule extends AbstractValidationRule {

   private HashCollection<String, String> validFeatureValues;
   private HashSet<String> validConfigurations;
   private HashSet<String> validConfigurationGroups;

   public MatchingApplicabilityTagsRule(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public void validate(ArtifactToken artToken, XResultData rd) {
      Artifact artifact = AtsApiService.get().getQueryServiceIde().getArtifact(artToken);
      String wordml = artifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, "");

      if (validFeatureValues == null) {
         validFeatureValues = ApplicabilityUtility.getValidFeatureValuesForBranch(artifact.getBranch());
      }

      if (validConfigurations == null) {
         validConfigurations = ApplicabilityUtility.getBranchViewNamesUpperCase(artifact.getBranch());
      }

      if (validConfigurationGroups == null) {
         validConfigurationGroups = ApplicabilityUtility.getConfigurationGroupsUpperCase(artifact.getBranch());
      }

      boolean validationPassed = true;
      if (!validFeatureValues.isEmpty()) {
         validationPassed = !WordCoreUtil.areApplicabilityTagsInvalid(wordml, artifact.getBranch(), validFeatureValues,
            validConfigurations, validConfigurationGroups);
         if (!validationPassed) {
            String errStr = "has invalid feature values and/or mismatching start and end applicability tags";
            logError(artifact, errStr, rd);
         }
      }
   }

   @Override
   public String getRuleDescription() {
      return "Ensure applicability tags are valid in the artifact(s)";
   }

   @Override
   public String getRuleTitle() {
      return "Applicability Check:";
   }
}
