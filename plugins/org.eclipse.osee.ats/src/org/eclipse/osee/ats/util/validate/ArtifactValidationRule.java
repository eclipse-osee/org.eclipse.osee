/*
 * Created on Sep 7, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.util.validate;

import java.util.ArrayList;
import java.util.Collection;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.osee.framework.core.exception.OseeCoreException;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeValidator;
import org.eclipse.osee.framework.skynet.core.validation.OseeValidator;
import org.eclipse.osee.framework.ui.skynet.results.XResultDataUI;

public class ArtifactValidationRule extends AbstractValidationRule {

   private String getStatusMessage(Artifact itemChecked, IStatus status) {
      String link =
         XResultDataUI.getHyperlink(String.format("%s:[%s]", itemChecked.getArtifactTypeName(), itemChecked.getName()),
            itemChecked.getHumanReadableId(), itemChecked.getBranch().getId());
      return String.format("%s: %s", link, status.getMessage());
   }

   @SuppressWarnings("unused")
   @Override
   protected ValidationResult validate(Artifact artToValidate, IProgressMonitor monitor) throws OseeCoreException {
      Collection<String> errorMessages = new ArrayList<String>();
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
