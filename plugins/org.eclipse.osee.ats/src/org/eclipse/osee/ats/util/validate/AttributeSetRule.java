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
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.data.AttributeTypeId;
import org.eclipse.osee.framework.core.data.IArtifactType;
import org.eclipse.osee.framework.core.model.type.ArtifactType;
import org.eclipse.osee.framework.core.operation.AbstractOperation;
import org.eclipse.osee.framework.jdk.core.type.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Donald G. Dunne
 */
public final class AttributeSetRule extends AbstractValidationRule {
   private final IArtifactType baseArtifactType;
   private final AttributeTypeId attributeType;
   private final Integer minimumValues;
   private final String invalidValue;

   public AttributeSetRule(IArtifactType artifactType, AttributeTypeId attributeType, Integer minimumValues, String invalidValue) {
      this.baseArtifactType = artifactType;
      this.attributeType = attributeType;
      this.minimumValues = minimumValues;
      this.invalidValue = invalidValue;
   }

   public boolean hasArtifactType(ArtifactType artifactType) {
      return artifactType.inheritsFrom(baseArtifactType);
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor)  {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;
      if (hasArtifactType(artToValidate.getArtifactType())) {
         // validate attribute is set and not invalidValue
         List<String> attributeValues = artToValidate.getAttributesToStringList(attributeType);
         int validValueFound = 0;
         for (String attributeValue : attributeValues) {
            AbstractOperation.checkForCancelledStatus(monitor);
            if (attributeValue.equals(invalidValue)) {
               errorMessages.add(ValidationReportOperation.getRequirementHyperlink(
                  artToValidate) + " has invalid " + invalidValue + " \"" + attributeType + "\" attribute");
               validationPassed = false;
            } else {
               validValueFound++;
            }
         }
         if (validValueFound < minimumValues) {
            errorMessages.add(ValidationReportOperation.getRequirementHyperlink(
               artToValidate) + " has less than minimum " + minimumValues + " values set for attribute \"" + attributeType + "\"");
            validationPassed = false;
         }
      }
      return new ValidationResult(errorMessages, validationPassed);
   }

   @Override
   public String getRuleDescription() {
      return "<b>Attribute Check: </b>" + "For \"" + baseArtifactType + "\", ensure \"" + attributeType + "\" attribute has at least " + minimumValues + " value(s)" + (invalidValue != null ? " and does NOT have \"" + invalidValue + "\" values" : "");
   }

   @Override
   public String getRuleTitle() {
      return "Attribute Check:";
   }
}