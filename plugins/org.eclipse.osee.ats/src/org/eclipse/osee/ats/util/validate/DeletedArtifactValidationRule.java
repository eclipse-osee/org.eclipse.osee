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
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Megumi Telles
 */
public class DeletedArtifactValidationRule extends AbstractValidationRule {

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;
      if (artToValidate.isDeleted()) {
         Artifact relatedArtifact =
            artToValidate.getRelatedArtifactOrNull(CoreRelationTypes.Default_Hierarchical__Parent);
         if (relatedArtifact != null) {
            errorMessages.add(ValidationReportOperation.getRequirementHyperlink(
               artToValidate) + " (" + artToValidate.getGammaId() + ")" + " is deleted but still has a parent relation.  Please delete the relation.");
            validationPassed = false;
         }
      }
      return new ValidationResult(errorMessages, validationPassed);
   }

   @Override
   public String getRuleDescription() {
      return "<b>Deleted Artifact Validation Checks: </b>All Errors reported must be fixed.";
   }

   @Override
   public String getRuleTitle() {
      return "Deleted Artifact Validation Checks";
   }

}
