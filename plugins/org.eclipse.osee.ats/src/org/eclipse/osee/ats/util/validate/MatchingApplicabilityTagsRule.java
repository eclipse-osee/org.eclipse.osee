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
package org.eclipse.osee.ats.util.validate;

import java.util.HashSet;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.core.util.result.XResultData;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.utility.ApplicabilityUtility;

/**
 * @author Morgan E. Cook
 */
public class MatchingApplicabilityTagsRule extends AbstractValidationRule {

   private HashCollection<String, String> validFeatureValues;
   private HashSet<String> validConfigurations;

   public MatchingApplicabilityTagsRule(AtsApi atsApi) {
      super(atsApi);
   }

   @Override
   public void validate(ArtifactToken artToken, XResultData results) {
      Artifact artifact = AtsClientService.get().getQueryServiceClient().getArtifact(artToken);
      String wordml = artifact.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, "");

      if (validFeatureValues == null) {
         validFeatureValues = ApplicabilityUtility.getValidFeatureValuesForBranch(artifact.getBranch());
      }

      if (validConfigurations == null) {
         validConfigurations = ApplicabilityUtility.getBranchViewNamesUpperCase(artifact.getBranch());
      }

      boolean validationPassed = true;
      if (!validFeatureValues.isEmpty()) {
         validationPassed = !WordCoreUtil.areApplicabilityTagsInvalid(wordml, artifact.getBranch(), validFeatureValues,
            validConfigurations);
         if (!validationPassed) {
            String errStr = "has invalid feature values and/or mismatching start and end applicability tags";
            logError(artifact, errStr, results);
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
