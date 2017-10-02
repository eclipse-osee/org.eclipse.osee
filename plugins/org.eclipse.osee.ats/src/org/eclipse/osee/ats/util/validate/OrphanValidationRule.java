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
import org.eclipse.osee.framework.core.enums.CoreArtifactTypes;
import org.eclipse.osee.framework.core.enums.CoreRelationTypes;
import org.eclipse.osee.framework.core.exception.ArtifactDoesNotExist;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;

/**
 * @author Megumi Telles
 */
public class OrphanValidationRule extends AbstractValidationRule {

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;

      if (artToValidate.isOfType(CoreArtifactTypes.AbstractImplementationDetails,
         CoreArtifactTypes.AbstractSoftwareRequirement)) {
         try {
            artToValidate.getRelatedArtifact(CoreRelationTypes.Default_Hierarchical__Parent);
         } catch (ArtifactDoesNotExist ex) {
            errorMessages.add(ValidationReportOperation.getRequirementHyperlink(
               artToValidate) + " (" + artToValidate.getGammaId() + ")" + " is orphaned (no parent on Default Hierarchy).");
            validationPassed = false;
         }
      }

      return new ValidationResult(errorMessages, validationPassed);
   }

   @Override
   public String getRuleDescription() {
      return "<b>Orphan Validation Checks: </b>All Errors reported must be fixed.";
   }

   @Override
   public String getRuleTitle() {
      return "Orphan Validation Checks";
   }

}
