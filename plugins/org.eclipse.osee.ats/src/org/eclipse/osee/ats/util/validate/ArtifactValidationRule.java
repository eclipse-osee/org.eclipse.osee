/*******************************************************************************
 * Copyright (c) 2013 Boeing.
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
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.ats.internal.AtsClientService;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

/**
 * @author Shawn F. Cook
 */
public class ArtifactValidationRule extends AbstractValidationRule {

   private String getStatusMessage(Artifact itemChecked, IStatus status) {
      String link =
         XResultDataUI.getHyperlink(String.format("%s:[%s]", itemChecked.getArtifactTypeName(), itemChecked.getName()),
            AtsClientService.get().getAtsId(itemChecked), itemChecked.getBranch());
      return String.format("%s: %s", link, status.getMessage());
   }

   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) {
      Collection<String> errorMessages = new ArrayList<>();
      boolean validationPassed = true;
      IStatus status = OseeValidator.getInstance().validate(IOseeValidator.LONG, artToValidate);

      if (!status.isOK()) {
         errorMessages.add(getStatusMessage(artToValidate, status));
         validationPassed = false;
      }
      return new ValidationResult(errorMessages, validationPassed);
   }

   @Override
   public String getRuleDescription() {
      return "<b>Artifact Validation Checks: </b>All Errors reported must be fixed.";
   }

   @Override
   public String getRuleTitle() {
      return "Artifact Validation Checks:";
   }

}
