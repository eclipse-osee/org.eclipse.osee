/*******************************************************************************
 * Copyright (c) 2010 Boeing.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Boeing - initial API and implementation
 *******************************************************************************/
package org.eclipse.osee.ats.core.rule.validate;

import java.util.Collection;
import org.eclipse.osee.ats.api.AtsApi;
import org.eclipse.osee.ats.api.rule.validation.AbstractValidationRule;
import org.eclipse.osee.framework.core.data.ArtifactToken;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.ArtifactTypeToken;
import org.eclipse.osee.framework.jdk.core.result.XResultData;

/**
 * @author Stephen J. Molaro
 */
public final class AttributeFormatRule extends AbstractValidationRule {
   private final ArtifactTypeToken baseArtifactType;
   private final AttributeTypeId attributeType;
   private final Integer minimumValues;
   private final String regex;

   public AttributeFormatRule(AtsApi atsApi, ArtifactTypeToken artifactType, AttributeTypeId attributeType, Integer minimumValues, String regex) {
      super(atsApi);
      this.baseArtifactType = artifactType;
      this.attributeType = attributeType;
      this.minimumValues = minimumValues;
      this.regex = regex;
   }

   public boolean hasArtifactType(ArtifactTypeToken artifactType) {
      return atsApi.getStoreService().inheritsFrom(artifactType, baseArtifactType);
   }

   @Override
   public void validate(ArtifactToken artifact, XResultData results) {
      if (hasArtifactType(atsApi.getStoreService().getArtifactType(artifact))) {
         // validate attribute is set and not invalidValue
         Collection<String> attributeValues =
            atsApi.getAttributeResolver().getAttributesToStringList(artifact, attributeType);
         int validValueFound = 0;
         for (String attributeValue : attributeValues) {
            if (regex != null && !attributeValue.matches(regex)) {
               String errStr = "\"" + attributeValue + "\"" + " needs to be of format: (" + regex + ")";
               logError(artifact, errStr, results);
            } else {
               validValueFound++;
            }
         }
         if (validValueFound < minimumValues) {
            String errStr =
               "has less than minimum " + minimumValues + " values set for attribute \"" + attributeType + "\"";
            logError(artifact, errStr, results);
         }
      }
   }

   @Override
   public String getRuleDescription() {
      return "For \"" + baseArtifactType + "\", ensure \"" + attributeType + "\" attribute has at least " + minimumValues + " value(s)" + (regex != null ? " and is of format: " + regex : "");
   }

   @Override
   public String getRuleTitle() {
      return "Attribute Format Check:";
   }
}