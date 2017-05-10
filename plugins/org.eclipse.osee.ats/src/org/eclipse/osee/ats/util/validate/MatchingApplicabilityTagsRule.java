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

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.ats.core.client.IAtsClient;
import org.eclipse.osee.framework.core.data.FeatureDefinitionData;
import org.eclipse.osee.framework.core.enums.CoreAttributeTypes;
import org.eclipse.osee.framework.core.util.WordCoreUtil;
import org.eclipse.osee.framework.jdk.core.type.HashCollection;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Morgan E. Cook
 */
public class MatchingApplicabilityTagsRule extends AbstractValidationRule {

   private final IAtsClient atsClient;

   public MatchingApplicabilityTagsRule(IAtsClient atsClient) {
      this.atsClient = atsClient;
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) throws OseeCoreException {
      Collection<String> errorMessages = new ArrayList<>();
      String wordml = artToValidate.getSoleAttributeValue(CoreAttributeTypes.WordTemplateContent, "");

      List<FeatureDefinitionData> featureDefinitionData =
         atsClient.getOseeClient().getApplicabilityEndpoint(artToValidate.getBranch()).getFeatureDefinitionData();

      HashCollection<String, String> validFeatureValues = new HashCollection<>();
      for (FeatureDefinitionData feat : featureDefinitionData) {
         validFeatureValues.put(feat.getName(), feat.getValues());
      }

      boolean validationPassed =
         !WordCoreUtil.areApplicabilityTagsInvalid(wordml, artToValidate.getBranch(), validFeatureValues);
      if (!validationPassed) {
         errorMessages.add(String.format(
            "Validation Failed. The following artifact has invalid feature values and/or mismatching start and end applicability tags: " //
               + "Artifact Id: [%s], Artifact Name: [%s]",
            artToValidate.getId(), artToValidate.getSafeName()));
      }

      return new ValidationResult(errorMessages, validationPassed);
   }

   @Override
   public String getRuleDescription() {
      return "<b>Applicability Check: </b>" + "Ensure applicability tags are valid in the artifact(s)";
   }

   @Override
   public String getRuleTitle() {
      return "Applicability Check:";
   }
}
