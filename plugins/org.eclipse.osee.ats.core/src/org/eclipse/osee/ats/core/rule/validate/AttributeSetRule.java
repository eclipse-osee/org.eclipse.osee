/*********************************************************************
 * Copyright (c) 2010 Boeing
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

package org.eclipse.osee.ats.core.rule.validate;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.core.data.AttributeTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Donald G. Dunne
 */
public final class AttributeSetRule extends AbstractValidationRule {
   private final ArtifactTypeToken baseArtifactType;
   private final AttributeTypeToken attributeType;
   private final Integer minimumValues;
   private final String invalidValue;

   public AttributeSetRule(AtsApi atsApi, ArtifactTypeToken artifactType, AttributeTypeToken attributeType, Integer minimumValues, String invalidValue) {
      super(atsApi);
      this.baseArtifactType = artifactType;
      this.attributeType = attributeType;
      this.minimumValues = minimumValues;
      this.invalidValue = invalidValue;
   }

   public boolean hasArtifactType(ArtifactTypeToken artifactType) {
      return artifactType.inheritsFrom(baseArtifactType);
   }

   @Override
   public void validate(ArtifactToken artifact, XResultData rd) {
      if (hasArtifactType(atsApi.getStoreService().getArtifactType(artifact))) {
         // validate attribute is set and not invalidValue
         Collection<String> attributeValues =
            atsApi.getAttributeResolver().getAttributesToStringList(artifact, attributeType);
         int validValueFound = 0;
         for (String attributeValue : attributeValues) {
            if (attributeValue.equals(invalidValue)) {
               String errStr = "has invalid " + invalidValue + " \"" + attributeType + "\" attribute";
               logError(artifact, errStr, rd);
            } else {
               validValueFound++;
            }
         }
         if (validValueFound < minimumValues) {
            String errStr =
               "has less than minimum " + minimumValues + " values set for attribute \"" + attributeType + "\"";
            logError(artifact, errStr, rd);
         }
      }
   }

   @Override
   public String getRuleDescription() {
      return "For \"" + baseArtifactType + "\", ensure \"" + attributeType + "\" attribute has at least " + minimumValues + " value(s)" + (invalidValue != null ? " and does NOT have \"" + invalidValue + "\" values" : "");
   }

   @Override
   public String getRuleTitle() {
      return "Attribute Set Check:";
   }
}