/*
 * Created on May 9, 2011
 *
 * PLACE_YOUR_DISTRIBUTION_STATEMENT_RIGHT_HERE
 */
package org.eclipse.osee.ats.core.validate;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.osee.ats.core.review.role.UserRoleError;
import org.eclipse.osee.ats.core.review.role.UserRoleValidator;
import org.eclipse.osee.ats.core.workflow.AbstractWorkflowArtifact;
import org.eclipse.osee.ats.internal.AtsPlugin;
import org.eclipse.osee.ats.util.widgets.role.XUserRoleViewer;
import org.eclipse.osee.framework.skynet.core.artifact.Artifact;
import org.eclipse.osee.framework.skynet.core.validation.IOseeXWidgetValidator;

/**
 * Validators for XWidgets that are backed by Artifact storage.
 * 
 * @author Donald G. Dunne
 */
public class AtsOseeXWidgetValidator implements IOseeXWidgetValidator {

   @Override
   public boolean isProvider(String xWidgetName) {
      if (xWidgetName.equals(XUserRoleViewer.class.getSimpleName())) {
         return true;
      }
      return false;
   }

   @Override
   public IStatus validate(Artifact artifact, String xWidgetName, String name) {
      if (artifact instanceof AbstractWorkflowArtifact) {
         if (xWidgetName.equals(XUserRoleViewer.class.getSimpleName())) {
            UserRoleError error = UserRoleValidator.isValid(artifact);
            if (!error.isOK()) {
               return new Status(IStatus.ERROR, AtsPlugin.PLUGIN_ID, error.getError());
            }
         }
      }

      // TODO implement validation for ATS provided widgets
      return Status.OK_STATUS;
   }

}
